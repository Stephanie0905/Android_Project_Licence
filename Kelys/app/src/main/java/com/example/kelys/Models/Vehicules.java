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
import com.example.kelys.Activities.VehiculeActivity;
import com.example.kelys.Adapters.ResidenceAdapter;
import com.example.kelys.Adapters.VehiculeAdapter;
import com.example.kelys.R;
import com.example.kelys.ViewHolder.ResidenceViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Vehicules<adapter> extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //variables
    static final float END_SCALE = 0.7f;
    ImageView menuIcon;
    LinearLayout contentView;

    private DatabaseReference ProductRef;
    RecyclerView carRecycler;
    RecyclerView.LayoutManager layoutManager;
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
            setContentView(R.layout.activity_vehicules);
            menuIcon = findViewById(R.id.menu_icone);
            contentView = findViewById(R.id.content);

        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);

        //Menu Hooks
            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.navigation_view);
            navigationDrawer();

            ProductRef = FirebaseDatabase.getInstance().getReference().child("Vehicule");

            //popular hotel
            carRecycler = (RecyclerView) findViewById(R.id.recyclerview_id);
            carRecycler.setHasFixedSize(true);
            //layoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false);
            carRecycler.setLayoutManager(new GridLayoutManager(this, 3));
            setCarRecycler();
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

    private void setCarRecycler() {
        FirebaseRecyclerOptions<Modelvehicule> options =
                new FirebaseRecyclerOptions.Builder<Modelvehicule>()
                        .setQuery(ProductRef, Modelvehicule.class)
                        .build();
        //Log.d("OPTIONS", );

        FirebaseRecyclerAdapter<Modelvehicule, VehiculeAdapter> adapter =
                new FirebaseRecyclerAdapter<Modelvehicule, VehiculeAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull VehiculeAdapter vehiculeAdapter, int i, @NonNull final Modelvehicule modelvehicule) {
                        vehiculeAdapter.txtname.setText(modelvehicule.getPname());

                        Picasso.get().load(modelvehicule.getImage()).into(vehiculeAdapter.imageView);

                        vehiculeAdapter.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Vehicules.this, VehiculeActivity.class);
                                intent.putExtra("pid", modelvehicule.getPid());
                                startActivity(intent);
                            }
                        });
                    }



                    @NonNull
                    @Override
                    public VehiculeAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicule_cardview,parent,false);
                        VehiculeAdapter holder = new VehiculeAdapter(view);
                        return holder;
                    }
                };
        carRecycler.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Vehicules.this, HomeActivity.class);
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
                Intent intent = new Intent(Vehicules.this, HotelActivity.class);
                intent.putExtra("ActivityCaller", "HomeActivity");
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

        return true;       }
}