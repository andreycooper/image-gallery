package com.weezlabs.imagegallery.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.weezlabs.imagegallery.R;


public class PreviewFragment extends Fragment {
    private static final String IMAGE_PATH = "com.weezlabs.imagegallery.IMAGE_PATH";

    private String mImagePath;

    public static PreviewFragment newInstance(String imagePath) {
        PreviewFragment fragment = new PreviewFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_PATH, imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    public PreviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImagePath = getArguments().getString(IMAGE_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_preview, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), getActivity().getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
            }
        });
        Glide.with(getActivity())
                .load(mImagePath)
                .into(imageView);
        return rootView;
    }


}
