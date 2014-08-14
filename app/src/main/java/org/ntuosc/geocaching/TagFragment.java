package org.ntuosc.geocaching;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.nfc.Tag;

public class TagFragment extends Fragment {

    private static final String ARG_TAG = "tag";

    private Tag mTag;

    public static TagFragment newInstance(Tag tag) {
        TagFragment fragment = new TagFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    public TagFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTag = getArguments().getParcelable(ARG_TAG);
        }
    }

}
