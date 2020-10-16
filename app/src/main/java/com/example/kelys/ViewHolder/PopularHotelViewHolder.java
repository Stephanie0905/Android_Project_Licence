package com.example.kelys.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kelys.R;

public class PopularHotelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtname, txtdescription, txtprice;
    public ImageView imageView;
    public ItemClickListener listener;


    public PopularHotelViewHolder(View itemView){
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.hotel_image);
         txtname = (TextView) itemView.findViewById(R.id.name);
        txtdescription = (TextView) itemView.findViewById(R.id.desc);
        txtprice = (TextView) itemView.findViewById(R.id.price);

    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(),false);
    }
}
