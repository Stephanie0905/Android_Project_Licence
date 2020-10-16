package com.example.kelys.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kelys.Models.HotelActivity;
import com.example.kelys.Models.ModelOrders;
import com.example.kelys.Models.Residences;
import com.example.kelys.Models.Restaurants;
import com.example.kelys.Models.Vehicules;
import com.example.kelys.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ListReservUser extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView orderlist;
    private DatabaseReference orderRef;
    private Query query;
    private ImageView btn_menu;
    LinearLayout contentView;
    static final float END_SCALE = 0.7f;
    RecyclerView.LayoutManager layoutManager;
    private ProgressDialog loadingBar;

    private TextView lbl_reservation;

    String statut = "";


    // preferences partagees

    SharedPreferences sharedPreferences;
    public static  final String fileName = "login";
    public static  final String EmailPreference = "Email";
    String currentEmailUser = "";

    //Drawer menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_list_reserv_user);

        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        currentEmailUser = sharedPreferences.getString(EmailPreference,"");
        orderRef = FirebaseDatabase.getInstance().getReference().child("Reservations");
        loadingBar = new ProgressDialog(this);

        // status
        statut = getIntent().getStringExtra("statut");

        // description
        lbl_reservation = (TextView) findViewById(R.id.lbl_reservation);
        if (statut.equals("En attente"))
        {
            lbl_reservation.setText("Réservations en attente");
        }

        else if (statut.equals("Validé"))
        {
            lbl_reservation.setText("Réservations validées");
        }

        //Log.d("statut", statut);
        query = orderRef.orderByChild("statut").equalTo(statut);


        orderlist = findViewById(R.id.cart_list);
        orderlist.setLayoutManager(new LinearLayoutManager(this));

        btn_menu = (ImageView) findViewById(R.id.menu_admin);

        contentView = findViewById(R.id.content);

//Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nv_listresid_admin);

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

    protected void onStart() {
        super.onStart();



        FirebaseRecyclerOptions<ModelOrders> options =
                new FirebaseRecyclerOptions.Builder<ModelOrders>()
                        .setQuery(query,ModelOrders.class)
                        .build();

        FirebaseRecyclerAdapter<ModelOrders, ListReservUser.UserOrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<ModelOrders, ListReservUser.UserOrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ListReservUser.UserOrderViewHolder holder, int i, @NonNull final ModelOrders modelOrders) {


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

                                Intent intent = new Intent(ListReservUser.this, UserReservConsultActivity.class);
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
                    public ListReservUser.UserOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);

                        return new ListReservUser.UserOrderViewHolder(view);
                    }
                };
        orderlist.setAdapter(adapter);
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
                Intent intent = new Intent(ListReservUser.this, HotelActivity.class);
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
        Intent intent = new Intent(getApplicationContext(), Compte.class);
        intent.putExtra("statut", statut);
        startActivity(intent);
    }
}