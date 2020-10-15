package com.example.kelys.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kelys.Helpers.UserHelperClass;
import com.example.kelys.JavaMail.JavaMailAPI;
import com.example.kelys.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {

    //variables
    ImageView image;
    TextView accueilText, descText;
    TextInputLayout regId, regName, regUsername, regEmail, regPassword, regPhoneNo;
    Button regBtn, regToLoginBtn;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //hooks to all xml elements in activity_register
        image = findViewById(R.id.reg_imglogo);
        accueilText = findViewById(R.id.reg_text_accueil);
        descText = findViewById(R.id.reg_text_desc);
        regName = findViewById(R.id.reg_name);
        regUsername = findViewById(R.id.reg_username);
        regEmail = findViewById(R.id.reg_email);
        regPhoneNo = findViewById(R.id.reg_phone);
        regPassword = findViewById(R.id.reg_password);
        regBtn = findViewById(R.id.reg_btn);
        regToLoginBtn = findViewById(R.id.reg_login_btn);
        regId = findViewById(R.id.reg_id);


        //save data in Firebase on button click
        //regBtn.setOnClickListener(new View.OnClickListener() {
          //  @Override
           // public void onClick(View v) {
              //  rootNode = FirebaseDatabase.getInstance();
                //reference = rootNode.getReference("users");

                //get all the values
               // String name = regName.getEditText().getText().toString();
                //String username = regUsername.getEditText().getText().toString();
                //String email = regEmail.getEditText().getText().toString();
                //String phoneNo = regPhoneNo.getEditText().getText().toString();
                //String password = regPassword.getEditText().getText().toString();

                //UserHelperClass helperClass = new UserHelperClass(name, username, email, phoneNo, password);


                //reference.child(phoneNo).setValue(helperClass);

            //}
        //});//register button method end

        regToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);

                Pair<View,String>[] pairs = new Pair[9];
                pairs[0] = new Pair<View,String>(image, "logo_image");
                pairs[1] = new Pair<View,String>(accueilText, "accueil_text");
                pairs[2] = new Pair<View,String>(descText, "text_desc");
                pairs[3] = new Pair<View,String>(regName, "name_user");
                pairs[4] = new Pair<View,String>(regUsername, "username");
                pairs[5] = new Pair<View,String>(regEmail, "mail_user");
                pairs[6] = new Pair<View,String>(regPassword, "password_user");
                pairs[7] = new Pair<View,String>(regBtn, "btn_register");
                pairs[8] = new Pair<View,String>(regToLoginBtn, "btn_login");



                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                    finish();
                }
            }
        });

    }//onCreate method end

    private Boolean validateName() {
        String val = regName.getEditText().getText().toString();

        if (val.isEmpty()) {
            regName.setError("Field cannot be empty");
            return false;
        }
        else {
            regName.setError(null);
            regName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateUsername() {
        String val = regUsername.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            regUsername.setError("Field cannot be empty");
            return false;
        } else if (val.length() >= 15) {
            regUsername.setError("Username too long");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            regUsername.setError("White Spaces are not allowed");
            return false;
        } else {
            regUsername.setError(null);
            regUsername.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = regEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            regEmail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            regEmail.setError("Invalid email address");
            return false;
        } else {
            regEmail.setError(null);
            regEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePhoneNo() {
        String val = regPhoneNo.getEditText().getText().toString();

        if (val.isEmpty()) {
            regPhoneNo.setError("Field cannot be empty");
            return false;
        } else {
            regPhoneNo.setError(null);
            regPhoneNo.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = regPassword.getEditText().getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z0-9])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            regPassword.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            regPassword.setError("Password is too weak");
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }

    private String getMailContent(String f) throws IOException {


        InputStream is = getAssets().open(f);
        int size = is.available();

        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        String str = new String(buffer);
        //str = str.replace("old string", "new string");

        return str;

    }




    private void sendEmailInscription(String username,String email) {

        // cette fonction utilise la fonction getMailContent

        String MaskedEmail = email.substring(0,2) +"****"+email.substring(email.length() - 3);
        String MaskedPassword = regPassword.getEditText().getText().toString().substring(0,1)+"****"+regPassword.getEditText().getText().toString().substring(regPassword.getEditText().getText().toString().length() - 1);

        // masquer l'email et le mot de passe

        String subject = "Inscription sur l'application Kely's Tours par "+username;
        String message = null;
        try {
            // chargement du template mail
            message = getMailContent("Inscription.html");
            message = message.replace("{username}", username);
            message = message.replace("{name}", regName.getEditText().getText().toString());
            message = message.replace("{email}", MaskedEmail);
            message = message.replace("{phone}", regPhoneNo.getEditText().getText().toString());
            message = message.replace("{password}", MaskedPassword);


            //envoi du mail
            JavaMailAPI javaMailAPI = new JavaMailAPI(RegisterActivity.this, email,subject, message);
            javaMailAPI.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }




    //This function will execute when user click on Register Button
    public void registerUser(View view) {

        if(!validateName() | !validatePassword() | !validatePhoneNo() | !validateEmail() | !validateUsername()){
            return;
        }

        //get all the values
        //String name = regName.getEditText().getText().toString();
        //final String username = regUsername.getEditText().getText().toString();
        //String email = regEmail.getEditText().getText().toString();
        //String phoneNo = regPhoneNo.getEditText().getText().toString();
        //String password = regPassword.getEditText().getText().toString();

        //UserHelperClass helperClass = new UserHelperClass(name, username, email, phoneNo, password);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        //String key = reference.push().getKey();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = regName.getEditText().getText().toString();
                final String username = regUsername.getEditText().getText().toString();
                String email = regEmail.getEditText().getText().toString();
                String phoneNo = regPhoneNo.getEditText().getText().toString();
                String password = regPassword.getEditText().getText().toString();

                if (snapshot.hasChild(username)){
                    Toast.makeText(RegisterActivity.this,"Username '"+ username +"' Already exists in Database",Toast.LENGTH_SHORT).show();
                }

                else{
                    Iterable<DataSnapshot> users = snapshot.getChildren();
                    boolean emailAlreadyExists = false;

                    for (DataSnapshot user : users) {
                        UserHelperClass singleUser = user.getValue(UserHelperClass.class);
                        String emailUser = singleUser.getEmail();
                        if (!emailUser.equals(email))
                        {
                            continue;
                        }

                        else{
                            emailAlreadyExists = true;
                            break;
                        }
                    }

                    if (emailAlreadyExists){
                        Toast.makeText(RegisterActivity.this,"Email Address '"+email+"' already exists in database",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        UserHelperClass helperClass = new UserHelperClass(name, username, email, phoneNo, password);
                        reference.child(username).setValue(helperClass);
                        Toast.makeText(RegisterActivity.this,"ENREGISTREMENT EFFECTUE",Toast.LENGTH_SHORT).show();

                        //ENVOI DE MAIL A L UTILISATEUR
                        sendEmailInscription(username,email);

                        //envoi des données sur la plateforme user
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);




                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //reference.child(username).setValue(helperClass);

        //Toast.makeText(this,"ENREGISTREMENT EFFECTUE",Toast.LENGTH_SHORT).show();

    }

}