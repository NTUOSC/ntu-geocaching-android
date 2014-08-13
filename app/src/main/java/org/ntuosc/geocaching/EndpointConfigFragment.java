package org.ntuosc.geocaching;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import static android.content.Context.*;
import static org.ntuosc.geocaching.AppConfig.*;

public class EndpointConfigFragment extends DialogFragment {

    public static final String CLOSE_ON_CANCEL = "closeOnCancel";

    public EndpointConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle(R.string.title_enter_endpoint)

                .setView(inflater.inflate(R.layout.dialog_endpoint, null))

                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onDialogAccepted(dialogInterface);
                    }

                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                onDialogShow(dialogInterface);
            }
        });

        return dialog;
    }

    protected void onDialogShow(DialogInterface dialogInterface) {

        final AlertDialog dialog = (AlertDialog) dialogInterface;

        // Read preferences
        SharedPreferences preferences = getActivity()
                .getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Acquire view references
        EditText nameField = (EditText) dialog.findViewById(R.id.field_endpoint_name);
        EditText keyField = (EditText) dialog.findViewById(R.id.field_endpoint_key);

        // Load preferences to view
        nameField.setText(preferences.getString(PREF_ENDPOINT_NAME, ""));
        keyField.setText(preferences.getString(PREF_ENDPOINT_KEY, ""));

        // Set listener
        TextWatcher watcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void afterTextChanged(Editable editable) {
                updateDialogButton(dialog);
            }

        };
        nameField.addTextChangedListener(watcher);
        keyField.addTextChangedListener(watcher);

        updateDialogButton(dialogInterface);
    }

    protected void updateDialogButton(DialogInterface dialogInterface) {

        AlertDialog dialog = (AlertDialog) dialogInterface;

        EditText nameField = ((EditText) dialog.findViewById(R.id.field_endpoint_name));

        EditText keyField = ((EditText) dialog.findViewById(R.id.field_endpoint_key));

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(nameField.length() > 0 && keyField.length() > 0);

    }

    protected void onDialogAccepted(DialogInterface dialogInterface) {

        AlertDialog dialog = (AlertDialog) dialogInterface;

        String name = ((EditText) dialog.findViewById(R.id.field_endpoint_name))
                .getText().toString();

        String key = ((EditText) dialog.findViewById(R.id.field_endpoint_key))
                .getText().toString();

        SharedPreferences preferences = getActivity()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        preferences.edit()
                .putString(PREF_ENDPOINT_NAME, name)
                .putString(PREF_ENDPOINT_KEY, key)
                .commit();
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        Bundle bundle = getArguments();

        if (bundle != null && bundle.getBoolean(CLOSE_ON_CANCEL, false)) {
            getActivity().finish();
        }

        super.onCancel(dialogInterface);
    }

}
