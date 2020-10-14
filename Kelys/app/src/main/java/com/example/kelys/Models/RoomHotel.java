package com.example.kelys.Models;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.kelys.Adapters.RoomAdapter;
import com.example.kelys.Helpers.ConfirmFinalOrderActivity;
import com.example.kelys.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RoomHotel extends AppCompatActivity  {

    private Button addToCartButton;
    private ImageView productImage;
    private TextView productprice, productDescription, productName, categorieChambre;
    private String productPID = "", hotelPID, hotelName, defUser;
    private String productID = "";
    private TextView saveUser, saveUserPhone, saveUserMail;
    private String saveCurrentDate1,saveCurrentDate2,saveCurrentDate,saveCurrentTime;
    private String ActivityCaller;

    Dialog myDialog;

    private TextView nDialogDate1,nDialogDate2;
    private DatePickerDialog.OnDateSetListener  onDateSetListener1, onDateSetListener2;


    String user_name, user_email, user_phoneNo;

    // Les preferences partagees
    SharedPreferences sharedPreferences;
    public static  final String fileName = "login";
    public static  final String UsernamePreference = "Username";
    public static  final String EmailPreference = "Email";
    public static  final String Passwordpreference = "Password";
    public static  final String IsAdminpreference = "IsAdmin";
    private static final String Tag = "RoomHotel";



    Date currentmaxDate = Calendar.getInstance().getTime();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_hotel);



        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);

        myDialog = new Dialog(this);

        productPID = getIntent().getStringExtra("pid");
        hotelPID = getIntent().getStringExtra("HotelPid");
        hotelName = getIntent().getStringExtra("HotelName");
        ActivityCaller = getIntent().getStringExtra("ActivityCaller");
        //defUser = getIntent().getStringExtra("username");

        defUser = getCurrentUsername(sharedPreferences);
        Log.d("defUser", defUser);

        productImage = (ImageView) findViewById(R.id.room_image_details);
        productName = (TextView) findViewById(R.id.room_name_details);
        productDescription = (TextView) findViewById(R.id.room_description_detail);
        productprice = (TextView) findViewById(R.id.room_price_details);
        addToCartButton = (Button) findViewById(R.id.pd_add_to_cart_button);
        categorieChambre = (TextView) findViewById(R.id.room_type_detail);




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

                final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(RoomHotel.this);
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

                                productID = saveCurrentDate + "-" + saveCurrentTime;
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


    private String getCurrentUsername(SharedPreferences shared)
    {
        String username = shared.getString(UsernamePreference,"");
        return username;
    }

    private void getDataPicker(){

        //popup datepicker dialog
        nDialogDate1 = (TextView) myDialog.findViewById(R.id.choose_date1);
        nDialogDate2 = (TextView) myDialog.findViewById(R.id.choose_date2);

        // désactiver nDialogDate2
        nDialogDate2.setEnabled(false);
        nDialogDate2.setClickable(false);

        nDialogDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                calendar.add(Calendar.YEAR, -2); // subtract 2 years from now


                final DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RoomHotel.this,
                        R.style.MyDatePickerDialogTheme,
                        onDateSetListener1,
                        year,month,day);





                //datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
               //datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(Color.rgb(16,1,100));
               // datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(Color.argb(140,16,1,100));
               // datePickerDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setBackgroundColor(Color.argb(140,16,1,100));

                /*
                * trouver la date maximum dans la BDD qui servira à définir le datepickerdialog
                * voir ci-dessus
                * */
                final Query productRef = FirebaseDatabase.getInstance().getReference().child("Reservation Chambre").orderByChild("pid").equalTo(productPID);

                productRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        /*
                        for (DataSnapshot sn : snapshot.getChildren())
                        {

                            Date tempDate = null;
                            Log.d("date1", sn.child("date1").getValue(String.class));
                            try {
                                tempDate = new SimpleDateFormat("dd/MM/yyyy").parse(sn.child("date1").getValue(String.class));
                                if( tempDate.getTime() >= currentmaxDate.getTime())
                                {
                                    currentmaxDate = tempDate;


                                }

                            } catch (ParseException e) {
                                Log.e("error",e.getMessage());
                            }


                        }

                        */
                        datePickerDialog.getDatePicker().setMinDate(currentmaxDate.getTime());





                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                /*
                 * trouver la date maximum dans la BDD qui servira à définir le datepickerdialog
                 * voir ci-dessous
                 * */



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
                calendar.add(Calendar.YEAR, -2); // subtract 2 years from now

                Log.d("ProductID",productID);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RoomHotel.this,
                        R.style.MyDatePickerDialogTheme,
                        onDateSetListener2,
                        year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                datePickerDialog.show();
                //datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                Date tempDate = null;
                try {
                    tempDate = new SimpleDateFormat("dd/MM/yyyy").parse(saveCurrentDate1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                datePickerDialog.getDatePicker().setMinDate(tempDate.getTime());


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

                // activer ndialog2
                nDialogDate2.setEnabled(true);
                nDialogDate2.setClickable(true);
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

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Chambre");

        productRef.child(productPID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    RoomAdapter room = snapshot.getValue(RoomAdapter.class);

                    productName.setText(room.getPname());
                    productDescription.setText(room.getDescription());
                    productprice.setText(room.getPrice());
                    categorieChambre.setText(room.getDetail_room());
                    Picasso.get().load(room.getImage()).into(productImage);
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



        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Reservation Chambre");

        final HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("pid", productPID);
        cartMap.put("id", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price", productprice.getText().toString());
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

                            Toast.makeText(RoomHotel.this,"Réservation en cours...",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RoomHotel.this,ConfirmFinalOrderActivity.class);
                            if(ActivityCaller == null)
                            {

                            }

                            else if(ActivityCaller.equals("HomeActivity"))
                            {
                                intent.putExtra("ActivityCaller","HomeActivity");
                            }
                            startActivity(intent);


                        }
                        else {

                            String message = task.getException().toString();
                            Toast.makeText(RoomHotel.this,"Error: " , Toast.LENGTH_SHORT).show();

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
        cartMap.put("price", productprice.getText().toString());
        cartMap.put("date_debut", saveCurrentDate1);
        cartMap.put("date_fin", saveCurrentDate2);
        cartMap.put("name_user", saveUser.getText().toString());
        cartMap.put("phone_user", saveUserPhone.getText().toString());
        cartMap.put("mail_user", saveUserMail.getText().toString());
        cartMap.put("categorie", "Chambre");
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

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        if(ActivityCaller == null)
        {
            Intent intent = new Intent(RoomHotel.this,DetailHotel.class);
            intent.putExtra("pid", hotelPID);
            intent.putExtra("pname", hotelName);
            startActivity(intent);
        }

        else if(ActivityCaller.equals("HomeActivity"))
        {
            Intent intent = new Intent(RoomHotel.this,DetailHotel.class);
            intent.putExtra("pid", hotelPID);
            intent.putExtra("pname", hotelName);
            intent.putExtra("ActivityCaller", "HomeActivity");
            startActivity(intent);
        }

    }

    private long getMaxDateForRoomReservation() {

        Query productRef = FirebaseDatabase.getInstance().getReference().child("Reservation Chambre").orderByChild("pid").equalTo(productPID);


        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot sn : snapshot.getChildren())
                {

                        Date tempDate = null;
                    Log.d("date1", sn.child("date1").getValue(String.class));
                        try {
                            tempDate = new SimpleDateFormat("dd/MM/yyyy").parse(sn.child("date1").getValue(String.class));
                            if( tempDate.getTime() >= currentmaxDate.getTime())
                            {
                                currentmaxDate = tempDate;
                                Log.d("currentmaxdate", String.valueOf(currentmaxDate));

                            }

                        } catch (ParseException e) {
                            Log.e("error",e.getMessage());
                        }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return currentmaxDate.getTime();

    }


}