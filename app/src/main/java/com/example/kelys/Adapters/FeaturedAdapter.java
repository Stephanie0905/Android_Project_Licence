package com.example.kelys.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelys.R;
import com.example.kelys.ViewHolder.ItemClickListener;

import java.util.ArrayList;

public class FeaturedAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtname, txtdescription;
    public ImageView imageView;
    public ItemClickListener listener;
    public RatingBar ratingBar;


    public FeaturedAdapter(View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.featured_img);
        txtname = (TextView) itemView.findViewById(R.id.featured_title);
        txtdescription = (TextView) itemView.findViewById(R.id.featured_title);
        ratingBar = (RatingBar) itemView.findViewById(R.id.star_feature);    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }


    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(),false);
    }

}
