package com.example.osteoporosis_detection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Section2Fragment extends Fragment {

    public Section2Fragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_section2_faqs, container, false);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<FAQ> faqList = new ArrayList<>();
        faqList.add(new FAQ(getString(R.string.faq_q1), getString(R.string.faq_a1)));
        faqList.add(new FAQ(getString(R.string.faq_q2), getString(R.string.faq_a2)));
        faqList.add(new FAQ(getString(R.string.faq_q3), getString(R.string.faq_a3)));
        faqList.add(new FAQ(getString(R.string.faq_q4), getString(R.string.faq_a4)));

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FAQAdapter adapter = new FAQAdapter(faqList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}