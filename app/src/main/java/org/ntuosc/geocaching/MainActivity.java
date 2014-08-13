package org.ntuosc.geocaching;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity
        extends Activity
        implements EndpointConfigFragment.OnEndpointChangedListener {

    public static final String PREFERENCES_NAME = "NTUOSC_Geo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        fragment.show(getFragmentManager(), "endpointConfig");

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
    public void onEndpointChanged(String endpointName, String endpointKey) {
        // Endpoint changed!
    }
}
