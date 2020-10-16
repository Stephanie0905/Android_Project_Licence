package com.example.kelys.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelys.R;

public class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtname;
    public ImageView imageView;
    public ItemClickListener listener;

    public RoomViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.room_img);
        txtname = (TextView) itemView.findViewById(R.id.room_name);

    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(),false);
    }
}
