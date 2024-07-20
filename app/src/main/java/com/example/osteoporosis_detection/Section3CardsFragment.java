package com.example.osteoporosis_detection;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Section3CardsFragment extends Fragment {

    public Section3CardsFragment() {
        // Required empty public constructor
    }

    public static Section3CardsFragment newInstance() {
        return new Section3CardsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_section3_cards, container, false);
    }
}