package com.example.kelys.Models;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.kelys.Activities.RestaurantActivity;
import com.example.kelys.Adapters.RestaurantAdapter;
import com.example.kelys.Adapters.VehiculeAdapter;
import com.example.kelys.R;
import com.example.kelys.ViewHolder.PopularHotelViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Restaurants<adapter> extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DatabaseReference ProductRef;
    RecyclerView Recycler;
    RecyclerView.LayoutManager layoutManager;

    ImageView backBtn;
    //variables
    static final float END_SCALE = 0.7f;
    ImageView menuIcon;
    LinearLayout contentView;
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
            setContentView(R.layout.activity_restaurants);
            sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
            menuIcon = findViewById(R.id.menu_icone);
            contentView = findViewById(R.id.content);
            //Menu Hooks
            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.navigation_view);
            navigationDrawer();
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);


        ProductRef = FirebaseDatabase.getInstance().getReference().child("Restaurant");

            //recycler
            Recycler = (RecyclerView) findViewById(R.id.recyclerview_id);
            Recycler.setHasFixedSize(true);
            //layoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false);
            Recycler.setLayoutManager(new GridLayoutManager(this, 3));

            restaurantRecycler();
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

    private void restaurantRecycler() {


        FirebaseRecyclerOptions<ModelRestaurant> options =
                new FirebaseRecyclerOptions.Builder<ModelRestaurant>()
                        .setQuery(ProductRef, ModelRestaurant.class)
                        .build();
        //Log.d("OPTIONS", );

        FirebaseRecyclerAdapter<ModelRestaurant, RestaurantAdapter> adapter =
                new FirebaseRecyclerAdapter<ModelRestaurant, RestaurantAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RestaurantAdapter holder, int i, @NonNull final ModelRestaurant model) {
                        holder.txtname.setText(model.getPname());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Restaurants.this, RestaurantActivity.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RestaurantAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicule_cardview,parent,false);
                        RestaurantAdapter holder = new RestaurantAdapter(view);
                        return holder;
                    }
                };
        Recycler.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Restaurants.this, HomeActivity.class);
        startActivity(intent);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                break;

            case R.id.nav_all_categories:

                //case R.id.view_all_categories:
                startActivity(new Intent(getApplicationContext(), AllCategories.class));
                break;

            case R.id.nav_hotel:

                //case R.id.link_hotel:
                Intent intent = new Intent(Restaurants.this,HotelActivity.class);
                intent.putExtra("ActivityCaller", "restaurants");
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

        return true;    }
}