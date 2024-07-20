package com.example.osteoporosis_detection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SplashPagerAdapter extends RecyclerView.Adapter<SplashPagerAdapter.SplashViewHolder> {

    private int[] layouts;

    public SplashPagerAdapter(int[] layouts) {
        this.layouts = layouts;
    }

    @NonNull
    @Override
    public SplashViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layouts[viewType], parent, false);
        return new SplashViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SplashViewHolder holder, int position) {
        // No additional binding necessary for static layouts
    }

    @Override
    public int getItemCount() {
        return layouts.length;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class SplashViewHolder extends RecyclerView.ViewHolder {
        SplashViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
