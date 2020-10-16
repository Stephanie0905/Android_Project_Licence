package com.example.kelys.ViewHolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.kelys.R;
import com.google.android.material.textfield.TextInputLayout;

public class ProductDetailActivity extends RecyclerView.ViewHolder implements View.OnClickListener{


    public TextInputLayout fullName,email,phoneNo,date1,date2,id_product,date_reserv,price;
    public ItemClickListener listener;


    public ProductDetailActivity(@NonNull View itemView) {
        super(itemView);

        //Hooks
        fullName = itemView.findViewById(R.id.name_profil);
        email = itemView.findViewById(R.id.mail_profil);
        phoneNo = itemView.findViewById(R.id.phoneNo_profil);
        date1 =  itemView.findViewById(R.id.date1);
        date2 =  itemView.findViewById(R.id.date1);
        id_product =  itemView.findViewById(R.id.id_product);
        date_reserv = itemView.findViewById(R.id.date_reserv);
        price =  itemView.findViewById(R.id.price_reserv);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }


    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(),false);
    }
}