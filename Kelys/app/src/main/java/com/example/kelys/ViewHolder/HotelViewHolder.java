package com.example.kelys.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kelys.R;

public class HotelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtname, txtdescription, txtprice;
    public ImageView imageView;
    public ItemClickListener listener;
    public RatingBar ratingBar;

    public HotelViewHolder(View itemView){
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.h_image);
        txtname = (TextView) itemView.findViewById(R.id.hote_name);
        txtdescription = (TextView) itemView.findViewById(R.id.desc_hotel);
        txtprice = (TextView) itemView.findViewById(R.id.price);
        ratingBar = (RatingBar) itemView.findViewById(R.id.star);

    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }


    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(),false);
    }
}
