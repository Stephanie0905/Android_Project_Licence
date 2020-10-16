package com.example.kelys.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kelys.Models.ModelOrders;
import com.example.kelys.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserProfil extends AppCompatActivity {

    TextInputLayout fullName,email,phoneNo,password;
    TextView fullNameLabel,usernameLabel;
    TextView paymentLabel, bookingLabel, bookingDesc;

    String user_username, user_name, user_email, user_phoneNo, user_password;

    DatabaseReference reference, ReservationRef;
    Query ReservationQuery;

    long numberReserv = 0;
    int TotalPrice = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_user_profil);

        reference = FirebaseDatabase.getInstance().getReference("users");



        //Hooks
        fullName = findViewById(R.id.name_profil);
        email = findViewById(R.id.mail_profil);
        phoneNo = findViewById(R.id.phoneNo_profil);
        password = findViewById(R.id.password_profil);
        fullNameLabel = findViewById(R.id.ent_name_profil);
        usernameLabel = findViewById(R.id.desc_name_profil);
        paymentLabel = findViewById(R.id.payment_label);
        bookingLabel = findViewById(R.id.booking_label);
        bookingDesc = findViewById(R.id.booking_desc);

        //showalldata
        showAllUserData();
    }

    private void showAllUserData() {
        Intent intent = getIntent();
        user_username = intent.getStringExtra("username");
        user_name = intent.getStringExtra("name");
        user_email = intent.getStringExtra("email");
        user_phoneNo = intent.getStringExtra("phoneNo");
        user_password = intent.getStringExtra("password");

        fullNameLabel.setText(user_name);
        usernameLabel.setText(user_username);
        fullName.getEditText().setText(user_name);
        email.getEditText().setText(user_email);
        phoneNo.getEditText().setText(user_phoneNo);
        password.getEditText().setText(user_password);



        // Les reservations faites par le user courant
        ReservationRef = FirebaseDatabase.getInstance().getReference("Reservations");
        ReservationQuery = ReservationRef.orderByChild("name_user").equalTo(user_name);
        ReservationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // obtenir le nombre de reservations faites par le user courant
                numberReserv = snapshot.getChildrenCount();
                bookingLabel.setText(String.valueOf(numberReserv));

                if (numberReserv > 1)
                {
                    bookingDesc.setText(bookingDesc.getText() + "s");
                }


                // calcult du totalPrice
                Iterable<DataSnapshot> users = snapshot.getChildren();

                for (DataSnapshot user : users) {
                    ModelOrders singleUser = user.getValue(ModelOrders.class);
                    int currentPrice = Integer.parseInt(singleUser.getPrice());
                    TotalPrice += currentPrice;

                }

                paymentLabel.setText(String.valueOf(TotalPrice));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







    }

    public void update_profil(View view){

        if(isNameChanged() || isPasswordChanged() || isEmailChanged() || isPhoneChanged()){

            Toast.makeText(this,"Modification Effectuée", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(),Compte.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this,"Données identiques! Aucune modification effectuée!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(),Compte.class);
            startActivity(intent);

        }
        finish();
    }

    private boolean isPasswordChanged() {
        if (!user_password.equals(password.getEditText().getText().toString())){
            reference.child(user_username).child("password").setValue(password.getEditText().getText().toString());
            user_password = password.getEditText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isNameChanged() {

        if (!user_name.equals(fullName.getEditText().getText().toString())){
            reference.child(user_username).child("name").setValue(fullName.getEditText().getText().toString());
            user_name = fullName.getEditText().getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isEmailChanged(){
        if (!user_email.equals(email.getEditText().getText().toString())){
            reference.child(user_username).child("email").setValue(email.getEditText().getText().toString());
            user_email = email.getEditText().getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isPhoneChanged(){
        if (!user_phoneNo.equals(phoneNo.getEditText().getText().toString())){
            reference.child(user_username).child("phoneNo").setValue(phoneNo.getEditText().getText().toString());
            user_phoneNo = phoneNo.getEditText().getText().toString();
            return true;
        }
        else {
            return false;
        }
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Compte.class);
        startActivity(intent);
    }


}