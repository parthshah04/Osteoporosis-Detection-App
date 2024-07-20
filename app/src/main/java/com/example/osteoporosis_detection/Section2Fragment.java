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
        faqList.add(new FAQ("What is osteoporosis?", "Osteoporosis is a severe disease which can weaken the bones significantly."));
        faqList.add(new FAQ("How is osteoporosis diagnosed?", "Osteoporosis is diagnosed through bone density tests."));
        faqList.add(new FAQ("What are the risk factors?", "Risk factors include age, gender, and lifestyle choices."));
        faqList.add(new FAQ("How can osteoporosis be prevented?", "Prevention includes a healthy diet and regular exercise."));

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FAQAdapter adapter = new FAQAdapter(faqList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}