package com.example.chitchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<GroupList> moviesList;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public MyViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.my_text);
        }
    }


    public MyAdapter( List<GroupList> moviesList) {
        this.moviesList = moviesList;
    }


    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        MyViewHolder vh = new MyViewHolder(itemView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        GroupList group = moviesList.get(position);
        holder.textView.setText(group.getTitle());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}