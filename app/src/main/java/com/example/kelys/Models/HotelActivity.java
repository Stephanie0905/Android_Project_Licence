package com.example.kelys.Models;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.kelys.Activities.AdminCategoryActivity;
import com.example.kelys.Activities.AdminHome;
import com.example.kelys.Activities.AllCategories;
import com.example.kelys.Activities.Compte;
import com.example.kelys.Activities.GraphiqueDashboard;
import com.example.kelys.Activities.HomeActivity;
import com.example.kelys.Activities.LoginActivity;
import com.example.kelys.R;
import com.example.kelys.ViewHolder.HotelViewHolder;
import com.example.kelys.ViewHolder.PopularHotelViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class HotelActivity<adapter> extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //variables
    static final float END_SCALE = 0.7f;
    ImageView menuIcon;
    LinearLayout contentView;

    private DatabaseReference ProductRef;
    RecyclerView hotelRecycler;
    RecyclerView.LayoutManager layoutManager;
    private String ActivityCaller;

    //Drawer menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    // preferences partagees

    SharedPreferences sharedPreferences;
    public static  final String fileName = "login";
    public static  final String UsernamePreference = "Username";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_hotel);
            menuIcon = findViewById(R.id.menu_icone);
            contentView = findViewById(R.id.content);
            //Menu Hooks
            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.navigation_view);
            navigationDrawer();
            ProductRef = FirebaseDatabase.getInstance().getReference().child("Hotel");

            ActivityCaller = getIntent().getStringExtra("ActivityCaller");
            sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);

            //hotel
            hotelRecycler = (RecyclerView) findViewById(R.id.hotel_recycler);
            hotelRecycler.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
            hotelRecycler.setLayoutManager(layoutManager);

            allHotel();
        }




    private void navigationDrawer() {
        //Navigation drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        //drawerLayout.setScrimColor(getResources().getColor(R.color.colorPrimary));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                //scale the view based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                //translate the view, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }


        });
    }


    @Override
    protected void onStart() {
        super.onStart();


    }





    /*private void popularHotel() {
        //popular hotel
        FirebaseRecyclerOptions<PopularHotel> options =
                new FirebaseRecyclerOptions.Builder<PopularHotel>()
                        .setQuery(ProductRef, PopularHotel.class)
                        .build();
        //Log.d("OPTIONS", );

        FirebaseRecyclerAdapter<PopularHotel, PopularHotelViewHolder> adapter =
                new FirebaseRecyclerAdapter<PopularHotel, PopularHotelViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PopularHotelViewHolder holder, int i, @NonNull final PopularHotel model) {
                        holder.txtname.setText(model.getPname());
                        holder.txtdescription.setText(model.getDescription());
                        holder.txtprice.setText("Prix = "+model.getPrice() + "FCFA");
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HotelActivity.this,DetailHotel.class);
                                intent.putExtra("pid", model.getPid());
                                intent.putExtra("pname", model.getPname());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public PopularHotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_hotel_row_item,parent,false);
                        PopularHotelViewHolder holder = new PopularHotelViewHolder(view);
                        return holder;
                    }
                };
        popularRecycler.setAdapter(adapter);
        adapter.startListening();
    }*/

    private void allHotel() {
        //all hotel
        FirebaseRecyclerOptions<ModelHotel> options =
                new FirebaseRecyclerOptions.Builder<ModelHotel>()
                        .setQuery(ProductRef, ModelHotel.class)
                        .build();
        //Log.d("OPTIONS", );

        FirebaseRecyclerAdapter<ModelHotel, HotelViewHolder> adapter =
                new FirebaseRecyclerAdapter<ModelHotel, HotelViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final HotelViewHolder holder, int i, @NonNull final ModelHotel model) {
                        holder.txtname.setText(model.getPname());
                        holder.txtdescription.setText(model.getDescription());
                        holder.txtprice.setText("A Partir de "+model.getPrice() + "FCFA");
                        holder.ratingBar.setRating(Float.parseFloat(String.valueOf(model.getRate_hotel().charAt(0))));

                        holder.ratingBar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.ratingBar.setIsIndicator(true);
                                holder.ratingBar.setEnabled(false);

                            }
                        });

                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HotelActivity.this,DetailHotel.class);
                                intent.putExtra("pid", model.getPid());
                                intent.putExtra("pname", model.getPname());
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
                    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_row_item,parent,false);
                        HotelViewHolder holder = new HotelViewHolder(view);
                        return holder;
                    }
                };
        hotelRecycler.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                break;

            case R.id.nav_all_categories:

                //case R.id.view_all_categories:
                startActivity(new Intent(getApplicationContext(),AllCategories.class));
                break;

            case R.id.nav_hotel:

                //case R.id.link_hotel:
                Intent intent = new Intent(HotelActivity.this,HotelActivity.class);
                intent.putExtra("ActivityCaller", "HotelActivity");
                startActivity(intent);
                break;

            //case R.id.link_resto:

            case R.id.nav_restaurant:
                startActivity(new Intent(getApplicationContext(), Restaurants.class));
                break;

            case R.id.nav_residence:

                //case R.id.link_residence:
                startActivity(new Intent(getApplicationContext(), Residences.class));
                break;

            case R.id.nav_vehicule:

                //case R.id.link_car:
                startActivity(new Intent(getApplicationContext(), Vehicules.class));
                break;

            case R.id.nav_logout:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                //startActivity(new Intent(getApplicationContext(), LogOut.class));
                break;

            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), Compte.class));
                finish();
                break;

            case R.id.nav_stat:
                startActivity(new Intent(getApplicationContext(), GraphiqueDashboard.class));
                finish();
                break;

        }

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HotelActivity.this, HomeActivity.class);
        startActivity(intent);

    }

}