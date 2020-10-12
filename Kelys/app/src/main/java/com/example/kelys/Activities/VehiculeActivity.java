package com.example.kelys.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kelys.Helpers.ConfirmFinalOrderActivity;
import com.example.kelys.Models.DetailResidence;
import com.example.kelys.Models.Modelvehicule;
import com.example.kelys.Models.PopularHotel;
import com.example.kelys.Models.RoomHotel;
import com.example.kelys.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class VehiculeActivity extends AppCompatActivity {

    private ImageView  productImage;
    private TextView productPrice, productDescription, productName;

    private String productPID = "";
    private String CarName = "";
    private Button addToCartButton;
    private String productID = "",defUser;
    private TextView saveUser, saveUserPhone, saveUserMail;
    private String saveCurrentDate1,saveCurrentDate2,saveCurrentDate,saveCurrentTime;

    Dialog myDialog;

    private TextView nDialogDate1,nDialogDate2;
    private DatePickerDialog.OnDateSetListener onDateSetListener1, onDateSetListener2;

    String user_name, user_email, user_phoneNo;

    // Les preferences partagees
    SharedPreferences sharedPreferences;
    public static  final String fileName = "login";
    public static  final String UsernamePreference = "Username";
    private static final String Tag = "Vehicule";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicule);

        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);

        myDialog = new Dialog(this);

        productPID = getIntent().getStringExtra("pid");
        CarName = getIntent().getStringExtra("pname");

        //defUser = getIntent().getStringExtra("username");

        defUser = getCurrentUsername(sharedPreferences);
        Log.d("defUser", defUser);

        productImage = (ImageView) findViewById(R.id.product_image_details);
        productName = (TextView) findViewById(R.id.product_name_details);
        productDescription = (TextView) findViewById(R.id.product_description_detail);
        productPrice = (TextView) findViewById(R.id.product_price_details);
        addToCartButton = (Button) findViewById(R.id.pd_add_to_cart_button);




        getProductDetails(productPID);
        getUserDetails(defUser);

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //données popup
                final TextView txtclose;
                final Button btnFollow;
                myDialog.setContentView(R.layout.popup_room_reservation);
                txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
                //txtclose.setText("M");
                btnFollow = (Button) myDialog.findViewById(R.id.btn_reserv);

                final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(VehiculeActivity.this);
                builder.setTitle("Effectuer une Réservation");
                builder.setMessage("Voulez-vous effectuer une réservation?");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        txtclose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();
                            }
                        });
                        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        myDialog.show();
                        getDataPicker();

                        btnFollow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Calendar calendar = Calendar.getInstance();

                                SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd, yyyy");
                                saveCurrentDate = currentDate.format(calendar.getTime());

                                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                                saveCurrentTime = currentTime.format(calendar.getTime());

                                productID = saveCurrentDate + saveCurrentTime;
                                saveDatainReservationTable();
                                saveDatainFirebase();
                            }
                        });

                    }
                });


                builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
            }
        });


    }

    /*private void addingToReservRoom() {

        //données popup
        final TextView txtclose;
        final Button btnFollow;
        myDialog.setContentView(R.layout.popup_room_reservation);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        //txtclose.setText("M");
        btnFollow = (Button) myDialog.findViewById(R.id.btn_reserv);





        //boite de dialogue pour confirmer la reservation
        AlertDialog.Builder builder = new AlertDialog.Builder(VehiculeActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Confirmation de la réservation");
        builder.setMessage("Voulez-vous effectuer une réservation sur cette offre?");
        builder.setPositiveButton("Confirmer",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        txtclose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();
                            }
                        });
                        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        myDialog.show();
                        getDataPicker();


                        btnFollow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Calendar calendar = Calendar.getInstance();

                                SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd, yyyy");
                                saveCurrentDate = currentDate.format(calendar.getTime());

                                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                                saveCurrentTime = currentTime.format(calendar.getTime());
                                productID = saveCurrentDate + saveCurrentTime;
                                saveDatainReservationTable();
                                saveDatainFirebase();
                            }
                        });

                    }

                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }*/

    private String getCurrentUsername(SharedPreferences shared)
    {
        String username = shared.getString(UsernamePreference,"");
        return username;
    }

    private void getDataPicker(){

        //popup datepicker dialog
        nDialogDate1 = (TextView) myDialog.findViewById(R.id.choose_date1);
        nDialogDate2 = (TextView) myDialog.findViewById(R.id.choose_date2);

        nDialogDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        VehiculeActivity.this,
                        R.style.Theme_MaterialComponents_Light_Dialog_MinWidth,
                        onDateSetListener1,
                        year,month,day);


                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                datePickerDialog.show();
            }
        });

        nDialogDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        VehiculeActivity.this,
                        R.style.Theme_MaterialComponents_Light_Dialog_MinWidth,
                        onDateSetListener2,
                        year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                datePickerDialog.show();
            }
        });

        onDateSetListener1 =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(Tag, "onDataSet : mm/dd/yyy: " + dayOfMonth + "/" + month + "/" + year);

                String date = dayOfMonth + "/" + month + "/" + year;
                nDialogDate1.setText(date);
                saveCurrentDate1 = date;

            }
        };

        onDateSetListener2 =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(Tag,"onDataSet : mm/dd/yyy: " + dayOfMonth + "/" + month + "/" + year);

                String date = dayOfMonth + "/" + month + "/" + year;
                nDialogDate2.setText(date);
                saveCurrentDate2 = date;


            }
        };
    }

    private void getProductDetails(String productPID) {

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Vehicule");

        productRef.child(productPID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Modelvehicule modelvehicule = snapshot.getValue(Modelvehicule.class);

                    productName.setText(modelvehicule.getPname());
                    productDescription.setText(modelvehicule.getDescription());
                    productPrice.setText(modelvehicule.getPrice());
                    Picasso.get().load(modelvehicule.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUserDetails(String defUser){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(defUser);
        Log.d("reference",String.valueOf(reference));
        saveUser = (TextView) findViewById(R.id.save_user);
        saveUserPhone = (TextView) findViewById(R.id.save_userphone);
        saveUserMail = (TextView) findViewById(R.id.save_usermail);



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    user_name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    user_email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                    user_phoneNo = Objects.requireNonNull(snapshot.child("phoneNo").getValue()).toString();

                    saveUser.setText(user_name);
                    saveUserMail.setText(user_email);
                    saveUserPhone.setText(user_phoneNo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void saveDatainFirebase(){



        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Reservation Vehicule");

        final HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("pid", productPID);
        cartMap.put("id", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date1", saveCurrentDate1);
        cartMap.put("date2", saveCurrentDate2);
        cartMap.put("name user", saveUser.getText().toString());
        cartMap.put("phone user", saveUserPhone.getText().toString());
        cartMap.put("mail user", saveUserMail.getText().toString());

        cartListRef.child(productID).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Toast.makeText(VehiculeActivity.this,"Réservation en cours...",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(VehiculeActivity.this, ConfirmFinalOrderActivity.class);
                            startActivity(intent);

                        }
                        else {

                            String message = task.getException().toString();
                            Toast.makeText(VehiculeActivity.this,"Error: " , Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    private void saveDatainReservationTable(){

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Reservations");

        final HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("pid", productPID);
        cartMap.put("id", productID);
        cartMap.put("Nom_produit", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date_debut", saveCurrentDate1);
        cartMap.put("date_fin", saveCurrentDate2);
        cartMap.put("name_user", saveUser.getText().toString());
        cartMap.put("phone_user", saveUserPhone.getText().toString());
        cartMap.put("mail_user", saveUserMail.getText().toString());
        cartMap.put("categorie", "Vehicule");
        cartMap.put("statut", "En attente");
        cartMap.put("mail_user_statut", saveUserMail.getText().toString()+"_En attente");

        cartListRef.child(productID).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){


                        }
                        else {

                        }
                    }
                });


    }
}