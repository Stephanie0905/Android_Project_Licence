package com.example.kelys.Models;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kelys.Activities.AllCategories;
import com.example.kelys.Activities.HomeActivity;
import com.example.kelys.Adapters.RoomAdapter;
import com.example.kelys.R;
import com.example.kelys.ViewHolder.HotelViewHolder;
import com.example.kelys.ViewHolder.PopularHotelViewHolder;
import com.example.kelys.ViewHolder.RoomViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailHotel extends AppCompatActivity {

    private DatabaseReference RoomRef;
    private Query RoomQuery;
    private ImageView  productImage;
    private TextView productPrice, productDescription, productName, productRatingDetail;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private String productPID = "";
    private String HotelName = "";
    private String ActivityCaller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_hotel);

        RoomRef = FirebaseDatabase.getInstance().getReference().child("Chambre");


        productPID = getIntent().getStringExtra("pid");
        HotelName = getIntent().getStringExtra("pname");
        ActivityCaller = getIntent().getStringExtra("ActivityCaller");
        Log.d("hotelPID", productPID);
        Log.d("hotelName", HotelName);

        RoomQuery = RoomRef.orderByChild("hotelName").equalTo(HotelName);




        productImage = (ImageView) findViewById(R.id.product_image_details);
        productPrice = (TextView) findViewById(R.id.product_price_details);
        productDescription = (TextView) findViewById(R.id.product_description_detail);
        productName = (TextView) findViewById(R.id.product_name_details);
        productRatingDetail = (TextView) findViewById(R.id.product_rating_details);

        //recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_detail_hotel);
        //recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);


        getProductDetails(productPID);
    }

    private void getProductDetails(String productPID) {

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Hotel");

        productRef.child(productPID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    PopularHotel popularHotel = snapshot.getValue(PopularHotel.class);

                    productName.setText(popularHotel.getPname());
                    productDescription.setText(popularHotel.getDescription());
                    productPrice.setText(popularHotel.getPrice());
                    productRatingDetail.setText(popularHotel.getRate_hotel());
                  Picasso.get().load(popularHotel.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        roomHotel();
    }

    private void roomHotel() {


        //all hotel
        FirebaseRecyclerOptions<RoomAdapter> options =
                new FirebaseRecyclerOptions.Builder<RoomAdapter>()
                        .setQuery(RoomQuery, RoomAdapter.class)
                        .build();
        //Log.d("OPTIONS", );

        FirebaseRecyclerAdapter<RoomAdapter, RoomViewHolder> adapter =
                new FirebaseRecyclerAdapter<RoomAdapter, RoomViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RoomViewHolder holder, int i, @NonNull final RoomAdapter model) {
                        holder.txtname.setText(model.getPname());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        Log.d("RoomName", model.getPname());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(DetailHotel.this,RoomHotel.class);

                                intent.putExtra("pid", model.getPid());
                                intent.putExtra("HotelPid", productPID);
                                intent.putExtra("HotelName", HotelName);
                                if(ActivityCaller == null)
                                {

                                }
                                else if(ActivityCaller.equals("HomeActivity"))
                                {
                                    intent.putExtra("ActivityCaller", "HomeActivity");

                                }
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item,parent,false);
                        RoomViewHolder holder = new RoomViewHolder(view);
                        return holder;
                    }


                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
        //adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        if(ActivityCaller == null)
        {
            Intent intent = new Intent(DetailHotel.this,HotelActivity.class);
            startActivity(intent);
        }

        else if(ActivityCaller.equals("HomeActivity"))
        {
            Intent intent = new Intent(DetailHotel.this,HotelActivity.class);
            intent.putExtra("ActivityCaller", "HomeActivity");
            startActivity(intent);
        }
    }




}