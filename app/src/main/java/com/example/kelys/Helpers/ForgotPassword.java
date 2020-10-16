package com.example.kelys.Helpers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kelys.Activities.Compte;
import com.example.kelys.Activities.LoginActivity;
import com.example.kelys.Activities.RegisterActivity;
import com.example.kelys.JavaMail.JavaMailAPI;
import com.example.kelys.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class ForgotPassword extends AppCompatActivity {

    Button btn_forgot;
    TextInputLayout mail_forgot;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    String emailEntered, username;
    String newPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btn_forgot = findViewById(R.id.btn_forgot);
        mail_forgot = findViewById(R.id.mail_forgot);



        btn_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("users");

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterable<DataSnapshot> users = snapshot.getChildren();
                        emailEntered = mail_forgot.getEditText().getText().toString();
                        Log.d("Email",emailEntered);
                        for (DataSnapshot user : users) {
                            UserHelperClass singleUser = user.getValue(UserHelperClass.class);
                            String emailUser = singleUser.getEmail();
                            if (!emailUser.equals(emailEntered))
                            {
                                continue;
                            }

                            else{
                                // get username thanks to email entered

                                username = singleUser.getUsername().toString();
                                break;
                            }
                        }

                        // GENERER PASSWORD
                        newPassword = getGeneratedPassword();

                        // METTRE A JOUR DANS FIREBASE
                        reference.child(username).child("password").setValue(newPassword);

                        // ENVOYER LE MAIL A L'UTILISATEUR
                        sendForgotPassword(username,emailEntered,newPassword);
                        //AFFICHER MESSAGE
                        Toast.makeText(ForgotPassword.this,"A new password has been sent to you via email. Connect with this new password",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });
    }

    private void sendForgotPassword(String username,String email, String password) {

        String message = "Dear,\n\nThese are your new credentials for Kelys : "+
                         "Username     : "+username+
                         "Email        : "+email+
                         "New Password : "+password+"\n\n\n"+
                         "Regards,\n\nKelys Development Team.";
        String subject = "Your credentials to get access Kelys for "+username;

        //envoi du mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email,subject, message);
        javaMailAPI.execute();


    }

    private String getGeneratedPassword()
    {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;

        for (int i = 0; i < randomLength ; i++)
        {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }

        return randomStringBuilder.toString();
    }

    private void sendForgottenEmail()
    {

    }
}