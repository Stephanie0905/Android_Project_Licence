package com.example.kelys.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.kelys.Adapters.FeaturedAdapter;
import com.example.kelys.Adapters.FeaturedHelperClass;
import com.example.kelys.Models.DetailHotel;
import com.example.kelys.Models.HotelActivity;
import com.example.kelys.Models.ModelHotel;
import com.example.kelys.Models.Residences;
import com.example.kelys.Models.Restaurants;
import com.example.kelys.Models.Vehicules;
import com.example.kelys.R;
import com.example.kelys.ViewHolder.HotelViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    //variables
    static final float END_SCALE = 0.7f;

    RecyclerView featuredRecycler;
    RecyclerView.Adapter adapter;
    ImageView menuIcon;
    LinearLayout contentView;
    ImageView link1,link2,link3,link4;
    Button logout;
    TextView name,intro;
    TextView welcome_username;
    private DatabaseReference ProductRef;

    //Drawer menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    //connexion firebase
    //FirebaseUser currentUser;

    // preferences partagees
    private String ActivityCaller;

    SharedPreferences sharedPreferences;
    public static  final String fileName = "login";
    public static  final String UsernamePreference = "Username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);



        if (sharedPreferences.contains(UsernamePreference))
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_home);

            //bouton connexion et deconnexion
            logout = findViewById(R.id.nav_logout);
            ProductRef = FirebaseDatabase.getInstance().getReference().child("Hotel");
            ActivityCaller = getIntent().getStringExtra("ActivityCaller");


            link1 = findViewById(R.id.link_hotel);
            link2 = findViewById(R.id.link_resto);
            link3 = findViewById(R.id.link_residence);
            link4 = findViewById(R.id.link_car);

            //header menu
            name = findViewById(R.id.nav_app_name);
            intro = findViewById(R.id.nav_slogan);
            welcome_username = findViewById(R.id.accueil_name_user);
            welcome_username.setText(welcome_username.getText() + sharedPreferences.getString(UsernamePreference,""));




            //currentUser = nAuth.getCurrentUser();

            //hooks
            featuredRecycler = findViewById(R.id.featured_recycler);
            menuIcon = findViewById(R.id.menu_icone);
            contentView = findViewById(R.id.content);

            ImageSlider imageSlider = findViewById(R.id.slider);
            List<SlideModel> slideModels = new ArrayList<>();
            slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/kelys-f9609.appspot.com/o/Images%20des%20Hotels%2F5324octobre%2008%2C%20202021%3A19%3A57%20PM.jpg?alt=media&token=d358fb36-797c-41f4-b81d-18f595b9b9f5",""));
            slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/kelys-f9609.appspot.com/o/Images%20des%20Hotels%2F5326octobre%2009%2C%20202015%3A13%3A02%20PM.jpg?alt=media&token=1fb821df-92ff-414e-96ea-d705cc294e04",""));
            imageSlider.setImageList(slideModels,true);

            //Functions will be executed automatically when this activity will be created
            featuredRecycler();


            //Menu Hooks
            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.navigation_view);

            link1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this,HotelActivity.class);
                    intent.putExtra("ActivityCaller", "HomeActivity");
                    startActivity(intent);
                }
            });

            link2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this,Restaurants.class);
                    startActivity(intent);
                }
            });

            link3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this,Residences.class);
                    startActivity(intent);
                }
            });

            link4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this,Vehicules.class);
                    startActivity(intent);
                }
            });

            //updateNavheader();
            navigationDrawer();
        }



    }

    //@Override
    //protected void onStart() {
        //super.onStart();

        //FirebaseUser nFirebaseUser = nFirebaseAuth.getCurrentUser();
        //if (nFirebaseUser !=null){
            //there is some user login
            //startActivity(new Intent(this, HomeActivity.class));
            //finish();
        //}
        //else{
            //no one login in
            //startActivity(new Intent(this,LoginActivity.class));
            //finish();
        //}
    //}

    //navigation drawer function
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
                Intent intent = new Intent(HomeActivity.this,HotelActivity.class);
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

        return true;
    }



    private void featuredRecycler() {
        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));

        FirebaseRecyclerOptions<ModelHotel> options =
                new FirebaseRecyclerOptions.Builder<ModelHotel>()
                        .setQuery(ProductRef, ModelHotel.class)
                        .build();
        //Log.d("OPTIONS", );

        FirebaseRecyclerAdapter<ModelHotel, FeaturedAdapter> adapter =
                new FirebaseRecyclerAdapter<ModelHotel, FeaturedAdapter>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final FeaturedAdapter holder, int i, @NonNull final ModelHotel model) {
                        holder.txtname.setText(model.getPname());
                        holder.txtdescription.setText(model.getDescription());
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
                                Intent intent = new Intent(HomeActivity.this, DetailHotel.class);
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
                    public FeaturedAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_card_design,parent,false);
                        FeaturedAdapter holder = new FeaturedAdapter(view);
                        return holder;
                    }
                };
        featuredRecycler.setAdapter(adapter);
        adapter.startListening();


        /*ArrayList<FeaturedHelperClass> featuredLocations = new ArrayList<>();

        featuredLocations.add(new FeaturedHelperClass(R.drawable.resto_img,"Lounge Resto","hbfvhslk bvhldfkv,kghjldfksjv fhdjskhfcjk"));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.ecologe,"Residence Ecologe","hbfvhslk bvhldfkv,kghjldfksjv fhdjskhfcjk"));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.ecologe,"Hotel","hbfvhslk bvhldfkv,kghjldfksjv fhdjskhfcjk"));



        adapter = new FeaturedAdapter(featuredLocations);
        featuredRecycler.setAdapter(adapter);*/


    }

    //public void updateNavheader(){

        //navigationView = findViewById(R.id.navigation_view);
        //View headerView = navigationView.getHeaderView(0);
        //TextView navUserName = headerView.findViewById(R.id.nav_username);
        //TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);
       // ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        //navUserMail.setText(currentUser.getEmail());
        //navUserName.setText(currentUser.getDisplayName());

        //Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);


    //}

}
