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
import com.example.kelys.Adapters.RoomAdapter;
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

public class ListingRoom extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
        setContentView(R.layout.activity_listing_room);
        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);

         orderRef = FirebaseDatabase.getInstance().getReference().child("Chambre");
        loadingBar = new ProgressDialog(this);

//room
        orderlist = (RecyclerView) findViewById(R.id.cart_list_room);
        orderlist.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL, false);
        orderlist.setLayoutManager(layoutManager);


        btn_menu = (ImageView) findViewById(R.id.menu_listroom_admin);

        contentView = findViewById(R.id.content);

//Menu Hooks
        drawerLayout = findViewById(R.id.drawerlayout_listroom);
        navigationView = findViewById(R.id.nv_listroom_admin);

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



        FirebaseRecyclerOptions<RoomAdapter> options =
                new FirebaseRecyclerOptions.Builder<RoomAdapter>()
                        .setQuery(orderRef,RoomAdapter.class)
                        .build();

        FirebaseRecyclerAdapter<RoomAdapter, ListingRoom.ListingRoomViewHolder> adapter =
                new FirebaseRecyclerAdapter<RoomAdapter, ListingRoom.ListingRoomViewHolder>(options) {


                    @Override
                    protected void onBindViewHolder(@NonNull ListingRoom.ListingRoomViewHolder listingRoomViewHolder, int i, @NonNull final RoomAdapter roomAdapter) {
                        listingRoomViewHolder.roomName.setText("Nom de la Chambre: " + roomAdapter.getPname());
                        listingRoomViewHolder.roomDesc.setText("Description: " + roomAdapter.getDescription());
                        listingRoomViewHolder.roomPrice.setText("Prix: " + roomAdapter.getPrice());
                        listingRoomViewHolder.roomDate.setText("Date de Création: " + roomAdapter.getDate());

                        /*listingRoomViewHolder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ListingRoom.this, AdminListingRoomDetail.class);
                                Log.d("modelOrdersGetId", roomAdapter.getPid());
                                intent.putExtra("uid",roomAdapter.getPid());
                                intent.putExtra("Nom",roomAdapter.getPname());
                                intent.putExtra("price",roomAdapter.getPrice());
                                intent.putExtra("date",roomAdapter.getDate());
                                intent.putExtra("time",roomAdapter.getTime());
                                intent.putExtra("categorie", roomAdapter.getCategory());
                                startActivity(intent);

                            }

                        });*/

                        listingRoomViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Modifier",
                                                "Supprimer"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(ListingRoom.this);
                                builder.setTitle("Options:");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0){
                                            Intent intent = new Intent(ListingRoom.this, AdminListingRoomDetail.class);
                                            Log.d("modelOrdersGetId", roomAdapter.getPid());
                                            intent.putExtra("uid",roomAdapter.getPid());
                                            intent.putExtra("Nom",roomAdapter.getPname());
                                            intent.putExtra("price",roomAdapter.getPrice());
                                            intent.putExtra("date",roomAdapter.getDate());
                                            intent.putExtra("time",roomAdapter.getTime());
                                            intent.putExtra("categorie", roomAdapter.getCategory());
                                            startActivity(intent);
                                        }
                                        if (i == 1){
                                            Query orderQuery = orderRef.orderByChild("pid").equalTo(roomAdapter.getPid());

                                            orderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String roomName = null;
                                                    for(DataSnapshot sn : snapshot.getChildren())
                                                    {
                                                        // obtenir le nom de la chambre
                                                        roomName = sn.child("pname").getValue().toString();

                                                        // supprimer les reservaiions des chambres
                                                        loadingBar.setTitle("Suppression En Cours");
                                                        loadingBar.setMessage("Cher Admin, Patientez SVP, nous sommes en train de supprimer cette Chambre!");
                                                        loadingBar.setCanceledOnTouchOutside(false);
                                                        loadingBar.show();
                                                        sn.getRef().removeValue();


                                                    }
                                                    Toast.makeText(ListingRoom.this,"Chambre Supprimée " ,Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();

                                                    Query ReservQuery = FirebaseDatabase.getInstance().getReference().child("Reservation Chambre").orderByChild("pname").equalTo(roomName);

                                                    ReservQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot sn : snapshot.getChildren())
                                                            {
                                                                // supprimer les reservaiions des chambres dans la table Rervation Chambre
                                                                sn.getRef().removeValue();
                                                            }


                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    Query GlobalReservQuery = FirebaseDatabase.getInstance().getReference().child("Reservations").orderByChild("Nom_produit").equalTo(roomName);

                                                    GlobalReservQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot sn : snapshot.getChildren())
                                                            {
                                                                // supprimer les reservaiions des chambres dans la table Reservations
                                                                sn.getRef().removeValue();
                                                            }


                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

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
                    public ListingRoom.ListingRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_service,parent,false);

                        return new ListingRoom.ListingRoomViewHolder(view);
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

            case R.id.nav_all_categories:

                startActivity(new Intent(getApplicationContext(), AdminCategoryActivity.class));
                break;

            case R.id.list_reserv:
                Intent intent = new Intent(ListingRoom.this, AdminNewOrderActivity.class);
                intent.putExtra("statut","En attente");
                startActivity(intent);
                finish();
                break;


            case R.id.admin_home:
                startActivity(new Intent(getApplicationContext(),AdminHome.class));
                break;


            case R.id.list_reserv2:
                Intent i = new Intent(ListingRoom.this,AdminNewOrderActivity.class);
                i.putExtra("statut","Validé");
                startActivity(i);
                finish();
                break;
        }


        return true;
    }

    public static class ListingRoomViewHolder extends RecyclerView.ViewHolder{

        public TextView roomName, roomDesc, roomPrice, roomDate,close;
        //public Button showOrderBtn;


        public ListingRoomViewHolder(@NonNull View itemView) {
            super(itemView);

            close = itemView.findViewById(R.id.txtclose);
            roomName = itemView.findViewById(R.id.list_hotel_name);
            roomDesc = itemView.findViewById(R.id.list_hotel_desc);
            roomPrice = itemView.findViewById(R.id.list_hotel_price);
            roomDate = itemView.findViewById(R.id.list_hotel_date);
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