package com.example.kelys.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelys.Activities.VehiculeActivity;
import com.example.kelys.Models.Modelvehicule;
import com.example.kelys.R;
import com.example.kelys.ViewHolder.ItemClickListener;

import java.util.List;

public class VehiculeAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtname;
    public ImageView imageView;
    public ItemClickListener listener;

    public VehiculeAdapter(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.vehicule_img_id);
        txtname = (TextView) itemView.findViewById(R.id.vehicule_title_id);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }


    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(),false);

    }
}
