package com.example.kelys.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.kelys.Graph1_UserFragment;
import com.example.kelys.Graph2_UserFragment;
import com.example.kelys.Graph3_UserFragment;
import com.example.kelys.Models.HotelActivity;
import com.example.kelys.Models.Residences;
import com.example.kelys.Models.Restaurants;
import com.example.kelys.Models.Vehicules;
import com.example.kelys.R;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.navigation.NavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class GraphiqueDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

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


    ChipNavigationBar navigationBar;
    FragmentManager fragmentManager;
    private static final String TAG = GraphiqueDashboard.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_graphique_dashboard);
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);

        menuIcon = findViewById(R.id.menu_icone);
        contentView = findViewById(R.id.content);
        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationDrawer();

        //navigation bar for graph
        navigationBar = findViewById(R.id.navbar_graph);

        if (savedInstanceState == null){
            navigationBar.setItemSelected(R.id.graph1,true);
            fragmentManager = getSupportFragmentManager();
            Graph1_UserFragment graph1_userFragment = new Graph1_UserFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container,graph1_userFragment)
                    .commit();
        }

        navigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                switch (id){
                    case R.id.graph1:
                        fragment = new Graph1_UserFragment();
                        break;

                    case R.id.graph2:
                        fragment = new Graph2_UserFragment();
                        break;

                    case R.id.graph3:
                        fragment = new Graph3_UserFragment();
                        break;
                }

                if (fragment!=null){
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container,fragment)
                            .commit();
                }else {
                    Log.e(TAG,"Erreur");
                }

            }
        });


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

    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
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
                Intent intent = new Intent(GraphiqueDashboard.this, HotelActivity.class);
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

        return true;    }
}