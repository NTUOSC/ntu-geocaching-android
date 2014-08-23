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
import android.widget.TextView;

public class RedeemFragment extends DialogFragment {
    private static final String ARG_TAG_ID = "tagId";

    private String mTagId;

    public static RedeemFragment newInstance(String tagId) {
        RedeemFragment fragment = new RedeemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TAG_ID, tagId);
        fragment.setArguments(args);
        return fragment;
    }
    public RedeemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTagId = getArguments().getString(ARG_TAG_ID);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.title_query_redeem)
                .setMessage(R.string.prompt_query_redeem)
                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        onDialogAccepted(dialog);
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    public void onDialogAccepted(DialogInterface dialog) {
        ((MainActivity) getActivity()).onPreRedeem(mTagId);
    }
}
