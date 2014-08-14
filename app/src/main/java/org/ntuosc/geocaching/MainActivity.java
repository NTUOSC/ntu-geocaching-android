package org.ntuosc.geocaching;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity
        extends Activity {

    public static final String ENDPOINT_CONFIG = "endpointConfig";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load preferences
        SharedPreferences preferences = getSharedPreferences(AppConfig.PREF_NAME, MODE_PRIVATE);
        if (!preferences.contains(AppConfig.PREF_ENDPOINT_NAME)) {
            DialogFragment fragment = new EndpointConfigFragment();

            Bundle bundle = new Bundle();
            bundle.putBoolean(EndpointConfigFragment.CLOSE_ON_CANCEL, true);

            fragment.setArguments(bundle);
            fragment.show(getFragmentManager(), ENDPOINT_CONFIG);
        }

        // Start detecting tag
        enableNfcDispatch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        disableNfcDispatch();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableNfcDispatch();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_edit)
            return onEditEndpointMenuItemClicked();

        else if (id == R.id.action_about)
            return onAboutMenuItemClicked();

        return super.onOptionsItemSelected(item);
    }

    public boolean onEditEndpointMenuItemClicked() {
        DialogFragment fragment = new EndpointConfigFragment();
        fragment.show(getFragmentManager(), ENDPOINT_CONFIG);

        return true;
    }

    public boolean onAboutMenuItemClicked() {
        // Launch NTUOSC site
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ntuosc.org"));
        try {
            startActivity(intent);
            return true;
        }
        catch (ActivityNotFoundException ex) {
            // No browser found. Just ignore it and all is good.
            return false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            // Tag detected!
        }

        super.onNewIntent(intent);
    }

    public void enableNfcDispatch() {
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);

        PendingIntent nfcIntent = PendingIntent.getActivity(this, AppConfig.CODE_NFC_REQUEST,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        nfc.enableForegroundDispatch(this, nfcIntent, null, null);
    }

    public void disableNfcDispatch() {
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        nfc.disableForegroundDispatch(this);
    }
}
