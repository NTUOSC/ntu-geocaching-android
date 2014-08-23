package org.ntuosc.geocaching;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.nfc.Tag;
import android.widget.TextView;

public class CheckinDoneFragment extends DialogFragment {
    private static final String ARG_CHECKIN_COUNT = "checkinCount";
    private static final String ARG_NOTIFY_REGISTER = "notifyRegister";

    private int mCheckinCount;
    private boolean mNotifyRegister;

    public static CheckinDoneFragment newInstance(int checkinCount, boolean notifyRegister) {
        CheckinDoneFragment fragment = new CheckinDoneFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHECKIN_COUNT, checkinCount);
        args.putBoolean(ARG_NOTIFY_REGISTER, notifyRegister);
        fragment.setArguments(args);
        return fragment;
    }

    public CheckinDoneFragment() {
        // Required empty public constructor
    }

    protected void loadArguments(Bundle params) {
        if (params != null) {
            mCheckinCount = params.getInt(ARG_CHECKIN_COUNT);
            mNotifyRegister = params.getBoolean(ARG_NOTIFY_REGISTER);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadArguments(getArguments());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_checkin, null))
               .setPositiveButton(R.string.action_ok, null);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

            }
        });

        return dialog;
    }

    protected void onShowDialog(DialogInterface dialogInterface) {
        AlertDialog dialog = (AlertDialog) dialogInterface;

        // Set dialog text
        TextView promptText = (TextView) dialog.findViewById(R.id.checkin_prompt_text);
        promptText.setText(String.format(
            getString(mNotifyRegister ?
                            R.string.prompt_checkin_done_redeemable :
                            R.string.prompt_checkin_done_normal),
            mCheckinCount));
    }
}
