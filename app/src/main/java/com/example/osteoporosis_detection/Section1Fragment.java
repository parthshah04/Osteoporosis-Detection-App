package com.example.osteoporosis_detection;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Section1Fragment extends Fragment {

    public Section1Fragment() {
        // Required empty public constructor
    }

    public static Section1Fragment newInstance() {
        return new Section1Fragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_section1_about, container, false);
    }
}