package com.example.kelys.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kelys.JavaMail.JavaMailAPI;
import com.example.kelys.Models.ModelOrders;
import com.example.kelys.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UserReservConsultActivity extends AppCompatActivity {

    TextInputLayout fullName,email,phoneNo,date1,date2,id_product,date_reserv,price;

    String user_name, user_email, user_phoneNo, user_date_reserv,prod_price,prod_id,date1_reserv,date2_reserv;


    DatabaseReference reference;

    private String userId = "";
    private String categorie = "";
    private String statut = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reserv_consult);



        userId = getIntent().getStringExtra("uid");
        reference = FirebaseDatabase.getInstance().getReference()
                .child("Reservations").child(userId);


        //Hooks
        fullName = findViewById(R.id.name_profil);
        email = findViewById(R.id.mail_profil);
        phoneNo = findViewById(R.id.phoneNo_profil);
        date1 = findViewById(R.id.date1);
        date2 = findViewById(R.id.date2);
        id_product = findViewById(R.id.id_product);
        date_reserv = findViewById(R.id.date_reserv);
        price = findViewById(R.id.price_reserv);




        Intent intent = getIntent();
        user_date_reserv = intent.getStringExtra("uid");
        user_name = intent.getStringExtra("name user");
        user_email = intent.getStringExtra("mail user");
        user_phoneNo = intent.getStringExtra("phone user");
        prod_price = intent.getStringExtra("price");
        prod_id = intent.getStringExtra("Nom_produit");
        date1_reserv = intent.getStringExtra("date1");
        date2_reserv = intent.getStringExtra("date2");
        categorie = intent.getStringExtra("categorie");
        statut = intent.getStringExtra("statut");
        fullName.getEditText().setText(user_name);
        email.getEditText().setText(user_email);
        phoneNo.getEditText().setText(user_phoneNo);
        date_reserv.getEditText().setText(user_date_reserv);
        date1.getEditText().setText(date1_reserv);
        date2.getEditText().setText(date2_reserv);
        id_product.getEditText().setText(prod_id);
        price.getEditText().setText(prod_price);



        //showalldata
        //showAllUserData();




    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ModelOrders> options =
                new FirebaseRecyclerOptions.Builder<ModelOrders>()
                        .setQuery(reference, ModelOrders.class)
                        .build();

    }



    public void onBackPressed()
    {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), UserReservationActivity.class);
        intent.putExtra("statut",statut);
        startActivity(intent);
    }





}