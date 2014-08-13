package org.ntuosc.geocaching;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

public class EndpointConfigFragment extends DialogFragment {

    private OnEndpointChangedListener mListener;

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

                        AlertDialog dialog = (AlertDialog) dialogInterface;

                        String name = ((EditText) dialog.findViewById(R.id.field_endpoint_name))
                                      .getText().toString();

                        String key = ((EditText) dialog.findViewById(R.id.field_endpoint_key))
                                     .getText().toString();

                        if (mListener != null)
                            mListener.onEndpointChanged(name, key);

                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Cancelled
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEndpointChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEndpointChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnEndpointChangedListener {

        public void onEndpointChanged(String endpointName, String endpointKey);

    }

}
