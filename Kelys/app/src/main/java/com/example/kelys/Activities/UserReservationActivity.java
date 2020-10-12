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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kelys.Models.ModelOrders;
import com.example.kelys.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class UserReservationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //variables
    static final float END_SCALE = 0.7f;

    private RecyclerView orderlist;
    private DatabaseReference orderRef;
    Query orderQuery;
    ImageView menuIcon;
    LinearLayout contentView;
    //Drawer menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    // preferences partagees

    SharedPreferences sharedPreferences;
    public static  final String fileName = "login";
    public static  final String EmailPreference = "Email";
    String currentEmailUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reservation);
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
        currentEmailUser = sharedPreferences.getString(EmailPreference,"");

        orderRef = FirebaseDatabase.getInstance().getReference().child("Reservations");
        orderQuery = orderRef.orderByChild("mail_user").equalTo(currentEmailUser);

        orderlist = findViewById(R.id.cart_list);
        orderlist.setLayoutManager(new LinearLayoutManager(this));


        //hooks
        menuIcon = findViewById(R.id.menu_icone);
        contentView = findViewById(R.id.content);

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);


        navigationDrawer();

    }

    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Compte.class);
        startActivity(intent);
    }

    //navigation drawer function
    private void navigationDrawer() {
        //Navigation drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.compte_user);

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


    protected void onStart() {
        super.onStart();



        FirebaseRecyclerOptions<ModelOrders> options =
                new FirebaseRecyclerOptions.Builder<ModelOrders>()
                        .setQuery(orderQuery,ModelOrders.class)
                        .build();

        FirebaseRecyclerAdapter<ModelOrders, AdminNewOrderActivity.AdminOrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<ModelOrders, AdminNewOrderActivity.AdminOrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminNewOrderActivity.AdminOrderViewHolder holder, int i, @NonNull final ModelOrders modelOrders) {


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

                                Intent intent = new Intent(UserReservationActivity.this, UserReservConsultActivity.class);
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
                    public AdminNewOrderActivity.AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);

                        return new AdminNewOrderActivity.AdminOrderViewHolder(view);
                    }
                };
        orderlist.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){


            case R.id.compte_user:
                Intent intent = new Intent(getApplicationContext(),Compte.class);
                startActivity(intent);
                //startActivity(new Intent(getApplicationContext(), LogOut.class));
                break;



            case R.id.list_reserv:
                Intent intent1 = new Intent(UserReservationActivity.this,UserDetailReservation.class);
                intent1.putExtra("statut","En attente");
                startActivity(intent1);
                finish();
                break;


            case R.id.list_reserv2:
                Intent i = new Intent(UserReservationActivity.this,UserDetailReservation.class);
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

}