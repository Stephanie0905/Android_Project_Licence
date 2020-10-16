package com.example.kelys.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
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

import com.example.kelys.Models.ListingHotel;
import com.example.kelys.Models.ListingResidence;
import com.example.kelys.Models.ListingRestaurant;
import com.example.kelys.Models.ListingRoom;
import com.example.kelys.Models.ListingVehicule;
import com.example.kelys.Models.ModelOrders;
import com.example.kelys.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class AdminNewOrderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView orderlist;
    private DatabaseReference orderRef;
    private Query query;
    private ImageView btn_menu;
    private TextView lib_reserv;
    LinearLayout contentView;
    static final float END_SCALE = 0.7f;



    String statut = "";
    //Drawer menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    //preferences partagees

    SharedPreferences sharedPreferences;
    public static  final String fileName = "login";
    public static  final String UsernamePreference = "Username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_new_order);
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        // status
        statut = getIntent().getStringExtra("statut");

        //orderRef = FirebaseDatabase.getInstance().getReference().child("Reservations").child(statut);
        orderRef = FirebaseDatabase.getInstance().getReference().child("Reservations");
        Log.d("statut", statut);
        query = orderRef.orderByChild("statut").equalTo(statut);

        btn_menu = (ImageView) findViewById(R.id.menu_neworder_admin);

        /* code pour changer le titre d'affichage des réservations en fonction du statut */
        lib_reserv = (TextView) findViewById(R.id.lib_reserv);

        if (statut.equals("En attente"))
        {
            lib_reserv.setText("Réservations en attente");
        }

        else if (statut.equals("Validé"))
        {
            lib_reserv.setText("Réservations Validées");
        }
        /* fin du code*/


        contentView = findViewById(R.id.content);

//Menu Hooks
        drawerLayout = findViewById(R.id.drawerlayout_neworder);
        navigationView = findViewById(R.id.nv_neworder_admin);

        orderlist = findViewById(R.id.cart_list);
        orderlist.setLayoutManager(new LinearLayoutManager(this));

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
    protected void onStart() {
        super.onStart();



        FirebaseRecyclerOptions<ModelOrders> options =
                new FirebaseRecyclerOptions.Builder<ModelOrders>()
                .setQuery(query,ModelOrders.class)
                .build();

        FirebaseRecyclerAdapter<ModelOrders, AdminOrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<ModelOrders, AdminOrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrderViewHolder holder, int i, @NonNull final ModelOrders modelOrders) {


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

                                Intent intent = new Intent(AdminNewOrderActivity.this, AdminUserReservActivity.class);
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
                    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);

                        return new AdminOrderViewHolder(view);
                    }
                };
        orderlist.setAdapter(adapter);
        adapter.startListening();

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
                Intent intent = new Intent(AdminNewOrderActivity.this,AdminNewOrderActivity.class);
                intent.putExtra("statut","En attente");
                startActivity(intent);
                finish();
                break;


            case R.id.admin_home:
                startActivity(new Intent(getApplicationContext(),AdminHome.class));
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

            case R.id.list_reserv2:
                Intent i = new Intent(AdminNewOrderActivity.this,AdminNewOrderActivity.class);
                i.putExtra("statut","Validé");
                startActivity(i);
                finish();
                break;
        }


        return true;
    }

    public static class AdminOrderViewHolder extends RecyclerView.ViewHolder{

        public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userEmail;
        public Button showOrderBtn;


        public AdminOrderViewHolder(@NonNull View itemView) {
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
        Intent intent = new Intent(getApplicationContext(), AdminHome.class);
        startActivity(intent);
    }
}