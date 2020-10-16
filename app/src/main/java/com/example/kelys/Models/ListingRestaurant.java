package com.example.kelys.Models;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.example.kelys.Activities.AdminCategoryActivity;
import com.example.kelys.Activities.AdminHome;
import com.example.kelys.Activities.AdminNewOrderActivity;
import com.example.kelys.Activities.AdminUserReservActivity;
import com.example.kelys.Activities.LoginActivity;
import com.example.kelys.Adapters.RestaurantAdapter;
import com.example.kelys.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ListingRestaurant extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView orderlist;
    private DatabaseReference orderRef;
    private ImageView btn_menu;
    LinearLayout contentView;
    static final float END_SCALE = 0.7f;
    RecyclerView.LayoutManager layoutManager;
    private ProgressDialog loadingBar;



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
        setContentView(R.layout.activity_listing_restaurant);
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Restaurant");
        loadingBar = new ProgressDialog(this);

//resto
        orderlist = (RecyclerView) findViewById(R.id.cart_list_resto);
        orderlist.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL, false);
        orderlist.setLayoutManager(layoutManager);


        btn_menu = (ImageView) findViewById(R.id.menu_listresto_admin);

        contentView = findViewById(R.id.content);

//Menu Hooks
        drawerLayout = findViewById(R.id.drawerlayout_listresto);
        navigationView = findViewById(R.id.nv_listresto_admin);

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



        FirebaseRecyclerOptions<ModelRestaurant> options =
                new FirebaseRecyclerOptions.Builder<ModelRestaurant>()
                        .setQuery(orderRef,ModelRestaurant.class)
                        .build();

        FirebaseRecyclerAdapter<ModelRestaurant, ListingRestaurant.ListingRestoViewHolder> adapter =
                new FirebaseRecyclerAdapter<ModelRestaurant, ListingRestaurant.ListingRestoViewHolder>(options) {


                    @Override
                    protected void onBindViewHolder(@NonNull ListingRestaurant.ListingRestoViewHolder listingRestoViewHolder, int i, @NonNull final ModelRestaurant modelRestaurant) {
                        listingRestoViewHolder.restoName.setText("Nom du Restaurant: " + modelRestaurant.getPname());
                        listingRestoViewHolder.restoDesc.setText("Description: " + modelRestaurant.getDescription());
                        listingRestoViewHolder.restoPrice.setText("Prix: " + modelRestaurant.getPrice());
                        listingRestoViewHolder.restoDate.setText("Date de Création: " + modelRestaurant.getDate());

                        /*listingRestoViewHolder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ListingRestaurant.this, AdminListingRestoDetail.class);
                                Log.d("modelOrdersGetId", modelRestaurant.getPid());
                                intent.putExtra("uid",modelRestaurant.getPid());
                                intent.putExtra("Nom",modelRestaurant.getPname());
                                intent.putExtra("price",modelRestaurant.getPrice());
                                intent.putExtra("date",modelRestaurant.getDate());
                                intent.putExtra("time",modelRestaurant.getTime());
                                intent.putExtra("categorie", modelRestaurant.getCategory());
                                startActivity(intent);

                            }

                        });*/

                        listingRestoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Modifier",
                                                "Supprimer"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(ListingRestaurant.this);
                                builder.setTitle("Options:");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0){
                                            Intent intent = new Intent(ListingRestaurant.this, AdminListingRestoDetail.class);
                                            Log.d("modelOrdersGetId", modelRestaurant.getPid());
                                            intent.putExtra("uid",modelRestaurant.getPid());
                                            intent.putExtra("Nom",modelRestaurant.getPname());
                                            intent.putExtra("price",modelRestaurant.getPrice());
                                            intent.putExtra("date",modelRestaurant.getDate());
                                            intent.putExtra("time",modelRestaurant.getTime());
                                            intent.putExtra("categorie", modelRestaurant.getCategory());
                                            startActivity(intent);
                                        }

                                        if (i == 1){
                                            loadingBar.setTitle("Suppression En Cours");
                                            loadingBar.setMessage("Cher Admin, Patientez SVP, nous sommes en train de supprimer cet Restaurant!");
                                            loadingBar.setCanceledOnTouchOutside(false);
                                            loadingBar.show();
                                            Query orderQuery = orderRef.orderByChild("pid").equalTo(modelRestaurant.getPid());

                                            orderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String restoName = null;
                                                    for(DataSnapshot sn : snapshot.getChildren())
                                                    {
                                                        // obtenir le nom de l'hotel
                                                        restoName = sn.child("pname").getValue().toString();

                                                        // supprimer les reservaiions des chambres

                                                        sn.getRef().removeValue();
                                                        sn.getRef().removeValue();


                                                    }
                                                    Toast.makeText(ListingRestaurant.this,"Restaurant Supprimé " ,Toast.LENGTH_SHORT).show();
                                                    //loadingBar.dismiss();


                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            Intent intent = new Intent(getApplicationContext(), AdminHome.class);
                                            startActivity(intent);


                                        }
                                    }
                                });
                                builder.show();
                            }

                        });
                    }
                    @NonNull
                    @Override
                    public ListingRestaurant.ListingRestoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_service,parent,false);

                        return new ListingRestaurant.ListingRestoViewHolder(view);
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


            case R.id.list_hotel_admin:

                startActivity(new Intent(getApplicationContext(),ListingHotel.class));
                break;

            case R.id.list_room_admin:

                startActivity(new Intent(getApplicationContext(), ListingRoom.class));
                break;

            case R.id.list_car_admin:

                startActivity(new Intent(getApplicationContext(), ListingVehicule.class));
                break;

            case R.id.list_resto_admin:

                startActivity(new Intent(getApplicationContext(),ListingRestaurant.class));
                break;

            case R.id.list_resid_admin:

                startActivity(new Intent(getApplicationContext(), ListingResidence.class));
                break;


            case R.id.list_reserv:
                Intent intent = new Intent(ListingRestaurant.this, AdminNewOrderActivity.class);
                intent.putExtra("statut","En attente");
                startActivity(intent);
                finish();
                break;

            case R.id.nav_all_categories:

                startActivity(new Intent(getApplicationContext(), AdminCategoryActivity.class));
                break;


            case R.id.admin_home:
                startActivity(new Intent(getApplicationContext(),AdminHome.class));
                break;


            case R.id.list_reserv2:
                Intent i = new Intent(ListingRestaurant.this,AdminNewOrderActivity.class);
                i.putExtra("statut","Validé");
                startActivity(i);
                finish();
                break;
        }


        return true;
    }

    public static class ListingRestoViewHolder extends RecyclerView.ViewHolder{

        public TextView restoName, restoDesc, restoPrice, restoDate,close;
       // public Button showOrderBtn;


        public ListingRestoViewHolder(@NonNull View itemView) {
            super(itemView);

            close = itemView.findViewById(R.id.txtclose);
            restoName = itemView.findViewById(R.id.list_hotel_name);
            restoDesc = itemView.findViewById(R.id.list_hotel_desc);
            restoPrice = itemView.findViewById(R.id.list_hotel_price);
            restoDate = itemView.findViewById(R.id.list_hotel_date);
            //showOrderBtn = itemView.findViewById(R.id.show_all_btn);
        }
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), AdminHome.class);
        startActivity(intent);
    }
}