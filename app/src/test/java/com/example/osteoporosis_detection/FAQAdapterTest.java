package com.example.osteoporosis_detection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class FAQAdapterTest {

    private FAQAdapter adapter;
    private List<FAQ> faqList;

    @Mock
    private ViewGroup mockParent;
    @Mock
    private View mockItemView;
    @Mock
    private TextView mockQuestionTextView;
    @Mock
    private TextView mockAnswerTextView;
    @Mock
    private ImageView mockExpandIcon;
    @Mock
    private Context mockContext;

    @Mock
    private LayoutInflater mockLayoutInflater;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        faqList = Arrays.asList(
                new FAQ("Question 1", "Answer 1"),
                new FAQ("Question 2", "Answer 2")
        );
        adapter = new FAQAdapter(faqList);

        when(mockParent.getContext()).thenReturn(mockContext);
        when(mockItemView.findViewById(R.id.questionTextView)).thenReturn(mockQuestionTextView);
        when(mockItemView.findViewById(R.id.answerTextView)).thenReturn(mockAnswerTextView);
        when(mockItemView.findViewById(R.id.expandIcon)).thenReturn(mockExpandIcon);


        when(mockContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(mockLayoutInflater);
        when(mockLayoutInflater.inflate(eq(R.layout.faq_item), eq(mockParent), eq(false))).thenReturn(mockItemView);
    }


    @Test
    public void testGetItemCount() {
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testOnCreateViewHolder() {
        FAQAdapter.FAQViewHolder holder = adapter.onCreateViewHolder(mockParent, 0);
        assertNotNull(holder);
    }

    @Test
    public void testOnBindViewHolder() {
        FAQAdapter.FAQViewHolder holder = new FAQAdapter.FAQViewHolder(mockItemView);
        adapter.onBindViewHolder(holder, 0);

        verify(mockQuestionTextView).setText("Question 1");
        verify(mockAnswerTextView).setText("Answer 1");
    }

//    @Test
//    public void testItemClick_ExpandAnswer() {
//        FAQAdapter.FAQViewHolder holder = new FAQAdapter.FAQViewHolder(mockItemView);
//        when(mockAnswerTextView.getVisibility()).thenReturn(View.GONE);
//
//        adapter.onBindViewHolder(holder, 0);
//        holder.itemView.performClick();
//
//        verify(mockAnswerTextView).setVisibility(View.VISIBLE);
//        verify(mockExpandIcon).setImageResource(android.R.drawable.arrow_up_float);
//    }

//    @Test
//    public void testItemClick_CollapseAnswer() {
//        FAQAdapter.FAQViewHolder holder = new FAQAdapter.FAQViewHolder(mockItemView);
//        when(mockAnswerTextView.getVisibility()).thenReturn(View.VISIBLE);
//
//        adapter.onBindViewHolder(holder, 0);
//
//        // Simulate click
//        holder.itemView.performClick();
//
//        verify(mockAnswerTextView).setVisibility(View.GONE);
//        verify(mockExpandIcon).setImageResource(android.R.drawable.arrow_down_float);
//    }
}
