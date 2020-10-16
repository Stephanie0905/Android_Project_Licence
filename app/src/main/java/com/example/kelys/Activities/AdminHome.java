package com.example.kelys.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kelys.Adapters.CategoriesHelperClass;
import com.example.kelys.Models.HotelActivity;
import com.example.kelys.Models.ListingHotel;
import com.example.kelys.Models.ListingResidence;
import com.example.kelys.Models.ListingRestaurant;
import com.example.kelys.Models.ListingRoom;
import com.example.kelys.Models.ListingVehicule;
import com.example.kelys.Models.Residences;
import com.example.kelys.Models.Restaurants;
import com.example.kelys.Models.Vehicules;
import com.example.kelys.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class AdminHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //variables
    static final float END_SCALE = 0.7f;



    private GradientDrawable gradient1, gradient2, gradient3, gradient4;
    ImageView menuIcon, addCategorie;
    LinearLayout contentView;
    Button logout;
    TextView name,intro;

    ImageView link1,link2,link3,link4;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_home);
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);

        //bouton connexion et deconnexion
        logout = findViewById(R.id.admin_logout);


        //header menu
        name = findViewById(R.id.nav_app_name);
        intro = findViewById(R.id.nav_slogan);

        //hooks
        addCategorie = findViewById(R.id.add_categ);
        menuIcon = findViewById(R.id.menu_icone);
        contentView = findViewById(R.id.content);

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);

        link1 = findViewById(R.id.link_hotel);
        link2 = findViewById(R.id.link_resto);
        link3 = findViewById(R.id.link_residence);
        link4 = findViewById(R.id.link_car);


        navigationDrawer();

        addCategorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this,AdminCategoryActivity.class);
                startActivity(intent);
            }
        });

        link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, ListingHotel.class);
                startActivity(intent);
            }
        });

        link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this,ListingRestaurant.class);
                startActivity(intent);
            }
        });

        link3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this,ListingResidence.class);
                startActivity(intent);
            }
        });

        link4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this,ListingVehicule.class);
                startActivity(intent);
            }
        });
    }

    //navigation drawer function
    private void navigationDrawer() {
        //Navigation drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.admin_home);

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


    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else
        {
            super.onBackPressed();
            if (sharedPreferences.contains(UsernamePreference))
            {

                //moveTaskToBack(true);
                //android.os.Process.killProcess(android.os.Process.myPid());
                //System.exit(1);

                this.finishAffinity();

            }

        }

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



    private void categoriesRecycler() {

        //All Gradients
        gradient2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffd4cbe5, 0xffd4cbe5});
        gradient1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xff7adccf, 0xff7adccf});
        gradient3 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xfff7c59f, 0xFFf7c59f});
        gradient4 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffb8d7f5, 0xffb8d7f5});


        ArrayList<CategoriesHelperClass> categoriesHelperClasses = new ArrayList<>();
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient1, R.drawable.hotel, "Hotel"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient2, R.drawable.cafe, "Restaurant"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient3, R.drawable.resident, "Residence"));
        categoriesHelperClasses.add(new CategoriesHelperClass(gradient4, R.drawable.car, "Vehicule"));



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
                Intent intent = new Intent(AdminHome.this,AdminNewOrderActivity.class);
                intent.putExtra("statut","En attente");
                startActivity(intent);
                finish();
                break;

            case R.id.list_hotel_admin:

                startActivity(new Intent(getApplicationContext(),ListingHotel.class));
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
                Intent i = new Intent(AdminHome.this,AdminNewOrderActivity.class);
                i.putExtra("statut","Validé");
                startActivity(i);
                finish();
                break;
        }

        return true;
    }
}