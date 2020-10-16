package com.example.kelys.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kelys.Models.ModelOrders;
import com.example.kelys.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class UserDetailReservation extends AppCompatActivity {
    private RecyclerView orderlist;
    private DatabaseReference orderRef;
    private Query query;
    private ImageView btn_back;

    String statut = "";

    // preferences partagees

    SharedPreferences sharedPreferences;
    public static  final String fileName = "login";
    public static  final String EmailPreference = "Email";
    String currentEmailUser = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_reservation);
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        currentEmailUser = sharedPreferences.getString(EmailPreference,"");
        // status
        statut = getIntent().getStringExtra("statut");

        //orderRef = FirebaseDatabase.getInstance().getReference().child("Reservations").child(statut);
        orderRef = FirebaseDatabase.getInstance().getReference().child("Reservations");
        Log.d("statut", statut);
        query = orderRef.orderByChild("mail_user_statut").equalTo(currentEmailUser+"_"+statut);

        btn_back = findViewById(R.id.menu_icone);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetailReservation.super.onBackPressed();
            }
        });

        orderlist = findViewById(R.id.cart_list);
        orderlist.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void onStart() {
        super.onStart();



        FirebaseRecyclerOptions<ModelOrders> options =
                new FirebaseRecyclerOptions.Builder<ModelOrders>()
                        .setQuery(query,ModelOrders.class)
                        .build();

        FirebaseRecyclerAdapter<ModelOrders, UserOrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<ModelOrders, UserOrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserOrderViewHolder holder, int i, @NonNull final ModelOrders modelOrders) {


                        holder.userName.setText("Nom: " + modelOrders.getName_user());
                        holder.userPhoneNumber.setText("Télephone: " + modelOrders.getPhone_user());
                        holder.userTotalPrice.setText("Prix: " + modelOrders.getPrice());
                        holder.userDateTime.setText("Réservation Le: " + modelOrders.getId());
                        holder.userEmail.setText("Email: " + modelOrders.getMail_user());

                        //Log.d("price",modelOrders.getPrice().toString());
                        //Log.d("Id",modelOrders.getId().toString());


                        holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(UserDetailReservation.this, UserReservConsultActivity.class);
                                Log.d("modelOrdersGetId", modelOrders.getId().toString());
                                intent.putExtra("uid",modelOrders.getId());
                                intent.putExtra("Nom_produit",modelOrders.getNom_produit());
                                intent.putExtra("price",modelOrders.getPrice());
                                intent.putExtra("name user",modelOrders.getName_user());
                                intent.putExtra("mail user",modelOrders.getMail_user());
                                intent.putExtra("phone user",modelOrders.getPhone_user());
                                intent.putExtra("date1",modelOrders.getDate_debut());
                                intent.putExtra("date2",modelOrders.getDate_fin());
                                intent.putExtra("categorie", modelOrders.getCategorie());
                                intent.putExtra("statut", modelOrders.getStatut());
                                startActivity(intent);
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public UserOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);

                        return new UserOrderViewHolder(view);
                    }
                };
        orderlist.setAdapter(adapter);
        adapter.startListening();

    }

    public static class UserOrderViewHolder extends RecyclerView.ViewHolder{

        public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userEmail;
        public Button showOrderBtn;


        public UserOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.cart_product_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userEmail = itemView.findViewById(R.id.order_mail);
            showOrderBtn = itemView.findViewById(R.id.show_all_product_btn);
        }
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), UserReservationActivity.class);
        startActivity(intent);
    }
}