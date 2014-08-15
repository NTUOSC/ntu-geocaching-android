package org.ntuosc.geocaching;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class MainActivity
        extends Activity
        implements ErrorFragment.Listener {

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
            // Show tag information
            Toast.makeText(this, String.format(Locale.getDefault(),
                    getString(R.string.prompt_tag_id), Util.toHexString(tag.getId())),
                    Toast.LENGTH_SHORT).show();

            // Check in!
            CheckinTask task = new CheckinTask(this);
            task.execute(tag);
        }

        super.onNewIntent(intent);
    }

    public void enableNfcDispatch() {
        updatePrompt(null);
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);

        if (nfc != null && nfc.isEnabled()) {
            PendingIntent nfcIntent = PendingIntent.getActivity(this, AppConfig.CODE_NFC_REQUEST,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            nfc.enableForegroundDispatch(this, nfcIntent, null, null);
            updatePrompt(true);

        } else {
            updatePrompt(false);
        }
    }

    public void disableNfcDispatch() {
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc != null && nfc.isEnabled()) {
            nfc.disableForegroundDispatch(this);
        }
    }

    public void updatePrompt(Boolean status) {
        TextView promptText = (TextView) findViewById(R.id.prompt_text);
        promptText.setText(
                status == null ? R.string.prompt_waiting_device :
                        status ? R.string.prompt_scan_card :
                                 R.string.prompt_enable_nfc
        );
    }

    public void onPostCheckin(Integer code) {
        DialogFragment fragment;

        switch (code) {
            case AppConfig.CODE_SUCCESS:
                fragment = new CheckinDoneFragment();
                fragment.show(getFragmentManager(), "checkin");
                break;

            case AppConfig.CODE_ENDPOINT_INCORRECT:
                fragment = ErrorFragment.newInstance(
                        getString(R.string.title_endpoint_incorrect),
                        getString(R.string.prompt_endpoint_incorrect),
                        getString(R.string.action_continue),
                        AppConfig.CODE_ENDPOINT_INCORRECT
                );
                fragment.show(getFragmentManager(), "error");
                break;

            case AppConfig.CODE_NETWORK_ERROR:
                fragment = ErrorFragment.newInstance(
                        getString(R.string.title_network_error),
                        getString(R.string.prompt_network_error),
                        getString(R.string.action_ok),
                        0);
                fragment.show(getFragmentManager(), "error");
                break;

            case AppConfig.CODE_GENERIC_ERROR:
                fragment = ErrorFragment.newInstance(
                        getString(R.string.title_generic_error),
                        getString(R.string.prompt_generic_error),
                        getString(R.string.action_ok),
                        0);
                fragment.show(getFragmentManager(), "error");
                break;
        }
    }

    @Override
    public void onErrorDialogDismiss(int errorCode) {
        if (errorCode == AppConfig.CODE_ENDPOINT_INCORRECT) {
            // Open endpoint config dialog for user
            DialogFragment fragment = new EndpointConfigFragment();
            fragment.show(getFragmentManager(), ENDPOINT_CONFIG);
        }
    }
}
