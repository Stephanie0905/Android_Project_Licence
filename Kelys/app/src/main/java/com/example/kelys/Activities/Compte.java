package com.example.kelys.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kelys.Models.HotelActivity;
import com.example.kelys.Models.Residences;
import com.example.kelys.Models.Restaurants;
import com.example.kelys.Models.Vehicules;
import com.example.kelys.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Compte extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //variables
    static final float END_SCALE = 0.7f;
    ImageView menuIcon;
    LinearLayout contentView;
    ImageView icon_user,btn_back,link1,link2;
    TextView text_acc_user,txt_nom_user,voir_tout_user,info_name_user,info_mail_user,info_mobil_user,voir_tout_reserv;
    //Drawer menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;



    DatabaseReference reference;

    String nameFromDB;
    String usernameFromDB;
    String phoneNoFromDB;
    String emailFromDB;
    String passwordFromDB;

    // preferences partagees
    SharedPreferences sharedPreferences;
    public static  final String fileName = "login";
    public static  final String UsernamePreference = "Username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_compte);

        menuIcon = findViewById(R.id.menu_icone);
        contentView = findViewById(R.id.content);
        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationDrawer();

        icon_user = findViewById(R.id.img_user_compte);
        voir_tout_user = findViewById(R.id.voir_tout);
        text_acc_user = findViewById(R.id.accueil_name_user);
        txt_nom_user = findViewById(R.id.name_user_compte);
        info_name_user= findViewById(R.id.info_name_user);
        info_mail_user= findViewById(R.id.info_mail_user);
        info_mobil_user= findViewById(R.id.info_mobil_user);
        voir_tout_reserv = findViewById(R.id.voir_tout1);
        link1=findViewById(R.id.link_reserv_encours);
        link2=findViewById(R.id.link_reserv_accept);
        btn_back = findViewById(R.id.menu_icone);



        link1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ListReservUser.class);
                intent.putExtra("statut","En attente");
                startActivity(intent);
                finish();
            }
        });

        link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ListReservUser.class);
                intent.putExtra("statut","Validé");
                startActivity(intent);
                finish();
            }
        });


        reference = FirebaseDatabase.getInstance().getReference("users");
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);

        Query checkUser = reference.orderByChild("username").equalTo(sharedPreferences.getString(UsernamePreference,""));

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get username from sharedpreferences
                String username = sharedPreferences.getString(UsernamePreference,"alice");

                //text_acc_user.setText(snapshot.child(username).child("username").getValue(String.class));
                txt_nom_user.setText(snapshot.child(username).child("name").getValue(String.class));
                info_name_user.setText(snapshot.child(username).child("name").getValue(String.class));
                info_mobil_user.setText(snapshot.child(username).child("phoneNo").getValue(String.class));
                info_mail_user.setText(snapshot.child(username).child("email").getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        text_acc_user.setText(text_acc_user.getText() + sharedPreferences.getString(UsernamePreference,""));



        voir_tout_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query checkUser = reference.orderByChild("username").equalTo(sharedPreferences.getString(UsernamePreference,""));

                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // get username from sharedpreferences
                        String username = sharedPreferences.getString(UsernamePreference,"alice");

                        nameFromDB = snapshot.child(username).child("name").getValue(String.class);
                        usernameFromDB = snapshot.child(username).child("username").getValue(String.class);
                        phoneNoFromDB = snapshot.child(username).child("phoneNo").getValue(String.class);
                        emailFromDB = snapshot.child(username).child("email").getValue(String.class);
                        passwordFromDB = snapshot.child(username).child("password").getValue(String.class);


                        Intent intent = new Intent(getApplicationContext(), UserProfil.class);

                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("phoneNo", phoneNoFromDB);
                        intent.putExtra("password", passwordFromDB);
                        startActivity(intent);

                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


        voir_tout_reserv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UserReservationActivity.class);
                startActivity(intent);
                finish();
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

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(Compte.this,HomeActivity.class);
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
                Intent intent = new Intent(Compte.this, HotelActivity.class);
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