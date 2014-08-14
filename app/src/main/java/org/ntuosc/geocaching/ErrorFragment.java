package org.ntuosc.geocaching;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ErrorFragment extends DialogFragment {
    private static final String ARG_DIALOG_TITLE = "title";
    private static final String ARG_DIALOG_MESSAGE = "message";
    private static final String ARG_DIALOG_BUTTON_TEXT = "buttonText";
    private static final String ARG_DIALOG_ERROR_CODE = "errorCode";

    private String mTitle;
    private String mMessage;
    private String mButtonText;
    private int mErrorCode;

    public static ErrorFragment newInstance(String title, String message, String buttonText, int errorCode) {
        ErrorFragment fragment = new ErrorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DIALOG_TITLE, title);
        args.putString(ARG_DIALOG_MESSAGE, message);
        args.putString(ARG_DIALOG_BUTTON_TEXT, buttonText);
        args.putInt(ARG_DIALOG_ERROR_CODE, errorCode);
        fragment.setArguments(args);
        return fragment;
    }

    public ErrorFragment() {
        // Required empty public constructor
    }

    protected void loadArguments(Bundle params) {
        if (params != null) {
            mTitle = params.getString(ARG_DIALOG_TITLE);
            mMessage = params.getString(ARG_DIALOG_MESSAGE);
            mButtonText = params.getString(ARG_DIALOG_BUTTON_TEXT);
            mErrorCode = params.getInt(ARG_DIALOG_ERROR_CODE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        loadArguments(getArguments());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        loadArguments(getArguments());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(mTitle)
               .setMessage(mMessage)
               .setIcon(android.R.drawable.ic_dialog_alert)
               .setPositiveButton(mButtonText, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int i) {
                       dialog.dismiss();
                   }
               });

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (mErrorCode != 0) {
            Listener listener = (Listener) getActivity();
            listener.onErrorDialogDismiss(mErrorCode);
        }
    }

    public interface Listener {

        public void onErrorDialogDismiss(int errorCode);

    }
}
