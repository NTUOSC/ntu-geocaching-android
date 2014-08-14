package org.ntuosc.geocaching;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

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
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            String uid = byteToHexString(tag.getId());

            params.add(new BasicNameValuePair("auth", mEndpointKey));
            params.add(new BasicNameValuePair("cuid", uid));

            // Create client and build up URL
            HttpClient client = new DefaultHttpClient();
            HttpPost request;

            try {
                request = new HttpPost(String.format(Locale.getDefault(), URL_ENDPOINT, mEndpointName));
                request.setEntity(new UrlEncodedFormEntity(params));
            }
            catch (IllegalArgumentException ex) {
                Log.e(PACKAGE_NAME, "URL formatting failed", ex);
                return CODE_ENDPOINT_INCORRECT;
            }
            catch (UnsupportedEncodingException ex) {
                Log.e(PACKAGE_NAME, "Default encoding not supported? How come?", ex);
                return CODE_GENERIC_ERROR;
            }

            try {
                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();
                Log.v(PACKAGE_NAME, "Checkin success");
            }
            catch (IOException ex) {
                Log.e(PACKAGE_NAME, "Error occurred while requesting checkin", ex);
            }

        }
    }

    private static final char[] hexNumbers = "0123456789abcdef".toCharArray();

    public static String byteToHexString(byte[] bytes) {
        char[] result = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xff;
            result[i * 2] = hexNumbers[value >> 4];
            result[i * 2 + 1] = hexNumbers[value & 0x0f];
        }
        return new String(result);
    }
}