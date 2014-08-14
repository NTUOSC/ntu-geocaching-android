package org.ntuosc.geocaching;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static java.net.HttpURLConnection.*;
import static org.ntuosc.geocaching.AppConfig.*;

public class CheckinTask extends AsyncTask<Tag, Integer, Integer> {

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
    protected Integer doInBackground(Tag... tags) {
        // Check if arguments valid
        if (mEndpointName == null || mEndpointKey == null) {
            Log.w(PACKAGE_NAME, "Endpoint not configured, ignore checkin");
            return CODE_ENDPOINT_INCORRECT;
        }

        // Iterate through available tags. Presumably one.
        for (Tag tag : tags) {

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
                return CODE_ENDPOINT_INCORRECT;
            }

            // Send request

            try {
                HttpsURLConnection request = (HttpsURLConnection) url.openConnection();

                request.setUseCaches(false);
                request.setRequestMethod("POST");
                request.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                request.setDoInput(true);
                request.setDoOutput(true);
                request.setChunkedStreamingMode(0);

                OutputStream output = request.getOutputStream();
                Writer writer = new BufferedWriter(new OutputStreamWriter(output, DEFAULT_ENCODING));
                writer.write(Util.toQueryString(params));
                writer.flush();
                writer.close();
                output.close();

                try {
                    String response = Util.readToEnd(request.getInputStream());

                    // Theoretically need to parse JSON but gonna ignore the result here
                    // since we're not distinguishing "ok" and "notice: came before"
                    // JSONObject entity = new JSONObject(entity);
                }
                catch (IOException ex) {
                    Log.e(PACKAGE_NAME, String.format(Locale.getDefault(),
                            "Server responded with HTTP Error %d", request.getResponseCode()), ex);
                    try {
                        String errorResponse = Util.readToEnd(request.getErrorStream());
                        JSONObject entity = new JSONObject(errorResponse);

                        Log.v(PACKAGE_NAME, String.format(Locale.getDefault(),
                                "Error message: %s", entity.getString("message")));

                        if (request.getResponseCode() == HTTP_BAD_REQUEST)
                            return CODE_ENDPOINT_INCORRECT;
                        else if (request.getResponseCode() == HTTP_INTERNAL_ERROR)
                            return CODE_GENERIC_ERROR;
                        else
                            return CODE_NETWORK_ERROR;

                    }
                    catch (JSONException inner) {
                        if (request.getResponseCode() == HTTP_INTERNAL_ERROR)
                            return CODE_GENERIC_ERROR;
                        else
                            return CODE_NETWORK_ERROR;
                    }
                    catch (IOException inner) {
                        Log.e(PACKAGE_NAME, "Failed to parse error response", inner);
                        return CODE_NETWORK_ERROR;
                    }
                }
                finally {
                    request.disconnect();
                }
            }
            catch (IOException ex) {
                Log.e(PACKAGE_NAME, "Error occurred while requesting checkin", ex);
                return CODE_NETWORK_ERROR;
            }
        }

        return CODE_SUCCESS;
    }
}