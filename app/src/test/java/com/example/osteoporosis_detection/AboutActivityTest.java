package com.example.osteoporosis_detection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class AboutActivityTest {

    private AboutActivity activity;
    private SharedPreferences mockSharedPreferences;

    @Before
    public void setUp() {
        mockSharedPreferences = mock(SharedPreferences.class);
        activity = Robolectric.buildActivity(AboutActivity.class).create().get();
        activity.sharedPreferences = mockSharedPreferences;
    }

//    @Test
//    public void testInitialFragmentLoading() {
//        FragmentManager fragmentManager = activity.getSupportFragmentManager();
//        assertNotNull(fragmentManager.findFragmentById(R.id.fragment_container_section1));
//        assertTrue(fragmentManager.findFragmentById(R.id.fragment_container_section1) instanceof Section1Fragment);
//    }

    @Test
    public void testBackIconClick() {
        ImageView backIcon = activity.findViewById(R.id.backIcon);
        backIcon.performClick();

        Intent expectedIntent = new Intent(activity, StartingActivity.class);
        ShadowActivity shadowActivity = org.robolectric.shadow.api.Shadow.extract(activity);
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
        assertTrue(activity.isFinishing());
    }

//    @Test
//    public void testMenuIconClick() {
//        ImageView menuIcon = activity.findViewById(R.id.menuIcon);
//        menuIcon.performClick();
//
//        // This test is limited as PopupMenu is not fully supported in Robolectric
//        // We can only verify that the click listener is set
//        assertNotNull(menuIcon.getOnClickListener());
//    }

    @Test
    public void testMoreInfoClick() {
        TextView textMoreInfo = activity.findViewById(R.id.text_more_info);
        textMoreInfo.performClick();

        Intent expectedIntent = new Intent(activity, InstructionActivity.class);
        ShadowActivity shadowActivity = org.robolectric.shadow.api.Shadow.extract(activity);
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }

    @Test
    public void testExpandableSection() {
        TextView heading = activity.findViewById(R.id.section2_heading);
        View expandableSection = activity.findViewById(R.id.expandable_section2);

        assertEquals(View.GONE, expandableSection.getVisibility());

        heading.performClick();
        assertEquals(View.VISIBLE, expandableSection.getVisibility());

        heading.performClick();
        assertEquals(View.GONE, expandableSection.getVisibility());
    }

    @Test
    public void testLogout() {
        SharedPreferences.Editor mockEditor = mock(SharedPreferences.Editor.class);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.clear()).thenReturn(mockEditor);

        activity.logout();

        verify(mockEditor).clear();
        verify(mockEditor).apply();

        Intent expectedIntent = new Intent(activity, LoginActivity.class);
        ShadowActivity shadowActivity = org.robolectric.shadow.api.Shadow.extract(activity);
        Intent actualIntent = shadowActivity.getNextStartedActivity();

        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK, actualIntent.getFlags());
        assertTrue(activity.isFinishing());
    }
}
