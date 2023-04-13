package com.example.staffinemployees.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.staffinemployees.R;

public class HomeEventsAdapter extends RecyclerView.Adapter<HomeEventsAdapter.HomeEventsViewHolder> {
    Context context;

    public HomeEventsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public HomeEventsAdapter.HomeEventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rv_event_layout, parent, false);
        return new HomeEventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeEventsAdapter.HomeEventsViewHolder holder, int position) {

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "event clicked  ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class HomeEventsViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout card;

        public HomeEventsViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
        }
    }
}
