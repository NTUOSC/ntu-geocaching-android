package org.ntuosc.geocaching;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static java.net.HttpURLConnection.*;
import static org.ntuosc.geocaching.AppConfig.*;

public class CheckinTask extends AsyncTask<Tag, Integer, CheckinTask.Result> {

    private MainActivity mActivity;
    private String mEndpointName;
    private String mEndpointKey;

    public CheckinTask(MainActivity activity) {
        mActivity = activity;

        SharedPreferences preferences = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mEndpointName = preferences.getString(PREF_ENDPOINT_NAME, null);
        mEndpointKey = preferences.getString(PREF_ENDPOINT_KEY, null);
    }

    @Override
    protected Result doInBackground(Tag... tags) {
        // Check if arguments valid
        if (mEndpointName == null || mEndpointKey == null) {
            Log.w(PACKAGE_NAME, "Endpoint not configured, ignore checkin");
            return new Result(CODE_ENDPOINT_INCORRECT);
        }

        // Get available tag. We assume there is only one per task.
        Tag tag = tags[0];

        // Build up arguments
        HashMap<String, String> params = new HashMap<String, String>();
        String uid = Util.toHexString(tag.getId());

        params.put("auth", mEndpointKey);
        params.put("cuid", uid);

        // Build up URL
        URL url;

        try {
            url = new URL(String.format(Locale.getDefault(), URL_ENDPOINT, mEndpointName));
        }
        catch (MalformedURLException ex) {
            Log.e(PACKAGE_NAME, "URL formatting failed", ex);
            return new Result(CODE_ENDPOINT_INCORRECT);
        }

        // Send request
        HttpsURLConnection request = null;
        String response = null;

        try {
            request = (HttpsURLConnection) url.openConnection();

            request.setUseCaches(false);
            request.setDoInput(true);
            request.setDoOutput(true);
            request.setRequestMethod("POST");
            request.setRequestProperty("X-Geocaching-Client", "NTUOSC");
            request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream output = request.getOutputStream();
            Writer writer = new BufferedWriter(new OutputStreamWriter(output, DEFAULT_ENCODING));
            writer.write(Util.toQueryString(params));
            writer.flush();
            writer.close();
            output.close();

            response = Util.readToEnd(request.getInputStream());
        }
        catch (IOException ex) {
            Log.e(PACKAGE_NAME, "Error occurred while building up connection", ex);
            ex.printStackTrace();

            if (request != null) {
                try {
                    // Read HTTP Error Code
                    Log.v(PACKAGE_NAME, String.format(Locale.getDefault(),
                            "HTTP %d: %s",
                            request.getResponseCode(), request.getResponseMessage()));

                    // Try parse and read server response
                    response = Util.readToEnd(request.getErrorStream());
                    JSONObject entity = new JSONObject(response);

                    Log.v(PACKAGE_NAME, String.format(Locale.getDefault(),
                            "Error message: %s", entity.getString("message")));

                    // Return appropriate result code
                    if (request.getResponseCode() == HTTP_BAD_REQUEST)
                        return new Result(CODE_ENDPOINT_INCORRECT);
                    else if (request.getResponseCode() == HTTP_INTERNAL_ERROR)
                        return new Result(CODE_GENERIC_ERROR);
                }
                catch (Exception inner) {
                    Log.e(PACKAGE_NAME, "Failed to parse error response", inner);
                    inner.printStackTrace();
                }
            }

            // Huston, we've got a problem
            return new Result(CODE_NETWORK_ERROR);
        }

        // Parse JSON and read data
        try {
            JSONObject entity = new JSONObject(response);

            Result result = new Result(CODE_SUCCESS);
            result.checkin_count = entity.getInt("count");
            result.registered = (entity.getInt("is_registered") > 0);
            result.redeemable = (entity.getInt("can_redeem") > 0);

            Log.v(PACKAGE_NAME, "Checkin success!");
            return result;
        }
        catch (JSONException ex) {
            Log.e(PACKAGE_NAME, "Failed to parse response", ex);
            ex.printStackTrace();
            return new Result(CODE_GENERIC_ERROR);
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        // NOTE: This approach might leak memory as stated in
        // http://simonvt.net/2014/04/17/asynctask-is-bad-and-you-should-feel-bad/
        // But since it's getting late, whatever (flee)
        if (mActivity != null) {
            mActivity.onPostCheckin(result);

            // Release reference
            mActivity = null;
        }
    }

    public class Result {
        public Integer code;
        public Integer checkin_count;
        public boolean registered;
        public boolean redeemable;

        protected Result(Integer code) {
            this.code = code;
            checkin_count = 0;
            registered = false;
            redeemable = false;
        }
    }
}
