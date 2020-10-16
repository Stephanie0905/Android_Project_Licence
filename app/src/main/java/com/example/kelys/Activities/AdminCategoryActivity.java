package com.example.kelys.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.kelys.Models.ListingHotel;
import com.example.kelys.Models.ListingResidence;
import com.example.kelys.Models.ListingRestaurant;
import com.example.kelys.Models.ListingRoom;
import com.example.kelys.Models.ListingVehicule;
import com.example.kelys.R;
import com.google.android.material.navigation.NavigationView;

public class AdminCategoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView hotel, residence;
    private ImageView restaurant, vehicule;
    private ImageView room,btn_menu;
    static final float END_SCALE = 0.7f;
    LinearLayout contentView;


    // preferences partagees

    SharedPreferences sharedPreferences;
    public static  final String fileName = "login";
    public static  final String UsernamePreference = "Username";

    //Drawer menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_category);
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);


        btn_menu = (ImageView) findViewById(R.id.menu_categ_admin);

        contentView = findViewById(R.id.content);


        //Menu Hooks
        drawerLayout = findViewById(R.id.drawerlayout_categ);
        navigationView = findViewById(R.id.nv_categ_admin);

        hotel = (ImageView) findViewById(R.id.categ_hotel);
        residence = (ImageView) findViewById(R.id.categ_residence);
        restaurant = (ImageView) findViewById(R.id.categ_restaurant);
        vehicule = (ImageView) findViewById(R.id.categ_vehicule);
        room = (ImageView) findViewById(R.id.categ_room);

        hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewHotel.class);
                intent.putExtra("Categorie","Hotel");
                startActivity(intent);

            }
        });

        residence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewResidence.class);
                intent.putExtra("Categorie","Residence");
                startActivity(intent);
            }
        });

        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewRestaurant.class);
                intent.putExtra("Categorie","Restaurant");
                startActivity(intent);
            }
        });

        vehicule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewVehicule.class);
                intent.putExtra("Categorie","Vehicule");
                startActivity(intent);
            }
        });

        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewRoom.class);
                intent.putExtra("Categorie","Room");
                startActivity(intent);
            }
        });

        navigationDrawer();

    }

    private void navigationDrawer() {
        //Navigation drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.admin_home);

        btn_menu.setOnClickListener(new View.OnClickListener() {
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
    public void onBackPressed() {
        Intent intent = new Intent(AdminCategoryActivity.this,AdminHome.class);
        startActivity(intent);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){


            case R.id.admin_logout:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                //startActivity(new Intent(getApplicationContext(), LogOut.class));
                break;




            case R.id.list_reserv:
                Intent intent = new Intent(AdminCategoryActivity.this,AdminNewOrderActivity.class);
                intent.putExtra("statut","En attente");
                startActivity(intent);
                finish();
                break;

            case R.id.list_hotel_admin:

                startActivity(new Intent(getApplicationContext(), ListingHotel.class));
                break;

            case R.id.nav_all_categories:

                startActivity(new Intent(getApplicationContext(),AdminCategoryActivity.class));
                break;

            case R.id.list_room_admin:

                startActivity(new Intent(getApplicationContext(), ListingRoom.class));
                break;

            case R.id.list_car_admin:

                startActivity(new Intent(getApplicationContext(), ListingVehicule.class));
                break;

            case R.id.list_resto_admin:

                startActivity(new Intent(getApplicationContext(), ListingRestaurant.class));
                break;

            case R.id.list_resid_admin:

                startActivity(new Intent(getApplicationContext(), ListingResidence.class));
                break;

            case R.id.admin_home:
                startActivity(new Intent(getApplicationContext(),AdminHome.class));
                break;


            case R.id.list_reserv2:
                Intent i = new Intent(AdminCategoryActivity.this,AdminNewOrderActivity.class);
                i.putExtra("statut","Validé");
                startActivity(i);
                finish();
                break;
        }

        return true;
    }
}