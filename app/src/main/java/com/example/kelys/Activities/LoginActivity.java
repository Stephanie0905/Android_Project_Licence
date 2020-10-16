package com.example.kelys.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kelys.Helpers.AdminHelperClass;
import com.example.kelys.Helpers.ForgotPassword;
import com.example.kelys.Helpers.UserHelperClass;
import com.example.kelys.JavaMail.JavaMailAPI;
import com.example.kelys.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    Button callSignUp,login_btn,forgottenPassword_btn;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout username,password;



    //private FirebaseAuth nFirebaseAuth;
    private Intent HomeActivity;

    String passwordFromDB, nameFromDB, usernameFromDB,phoneNoFromDB,emailFromDB;
    String passwordFromDB_admin, nameFromDB_admin, usernameFromDB_admin,phoneNoFromDB_admin,emailFromDB_admin;

    // Les preferences partagees
    SharedPreferences sharedPreferences;
    public static  final String fileName = "login";
    public static  final String UsernamePreference = "Username";
    public static  final String EmailPreference = "Email";
    public static  final String Passwordpreference = "Password";
    public static  final String IsAdminpreference = "IsAdmin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);

        if (sharedPreferences.contains(UsernamePreference))
        {
            Intent i ;
            switch (sharedPreferences.getString(IsAdminpreference,""))
            {
                case "false":
                    i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                    break;

                case "true":
                    i = new Intent(LoginActivity.this, AdminHome.class);
                    startActivity(i);
                    break;
            }



        }

        else
            {

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setContentView(R.layout.activity_login);

                //init
                // = FirebaseAuth.getInstance();
                HomeActivity = new Intent(this, com.example.kelys.Activities.HomeActivity.class);

                //hooks
                callSignUp = findViewById(R.id.signup_screen);
                image = findViewById(R.id.logoImage);
                logoText = findViewById(R.id.logo_name);
                sloganText = findViewById(R.id.slogan_name);
                username = findViewById(R.id.username); // change par joe ; jai pas trouve d id username dans la vue; j ai trouve l id usermail
                password = findViewById(R.id.password);
                login_btn = findViewById(R.id.login_btn);
                forgottenPassword_btn = findViewById(R.id.forgottenPassword_btn);

                callSignUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);

                        Pair<View,String>[] pairs = new Pair[7];
                        pairs[0] = new Pair<View,String>(image, "logo_image");
                        pairs[1] = new Pair<View,String>(logoText, "logo_text");
                        pairs[2] = new Pair<View,String>(sloganText, "logo_desc");
                        pairs[3] = new Pair<View,String>(username, "username_tran");
                        pairs[4] = new Pair<View,String>(password, "password_tran");
                        pairs[5] = new Pair<View,String>(login_btn, "button_tran");
                        pairs[6] = new Pair<View,String>(callSignUp, "login_signup_tran");



                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                            startActivity(intent, options.toBundle());
                            finish();
                        }
                    }
                });


                // MOT DE PASSE OUBLIE
                forgottenPassword_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // REDIRIGER VERS UNE ACTIVITY OU ENTRER L'ADRESSE MAIL
                        startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
                        // GENERER UN MOT DE PASSE ALEATOIRE
                        // METTRE A JOUR LE MOT DE PASSE DANS FIREBASE
                        // ENVOYER UN MAIL CONTENANT LE NOUVEAU MOT DE PASSE
                    }
                });


            }

    }

    private Boolean validateUsername() {
        String val = username.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,40}\\z";


        if (val.isEmpty()) {
            username.setError("Field cannot be empty");
            return false;
        }
        /*
        else if (val.length() >= 15) {
            username.setError("Username too long");
            return false;
        }
        else if (!val.matches(noWhiteSpace)) {
            username.setError("White Spaces are not allowed");
            return false;
        } */ else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = password.getEditText().getText().toString();
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
            password.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            password.setError("Password is too weak");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public void loginUser(View view) {
        //validate login info
        if(!validateUsername() | !validatePassword()) {

            return;
        }
        else{

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
            final String valusername = username.getEditText().getText().toString().trim();
            Query checkUser = reference.orderByChild("username").equalTo(valusername);

            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Iterable<DataSnapshot> users = snapshot.getChildren();
                    boolean userExists = false;

                    for (DataSnapshot user : users) {
                        UserHelperClass singleUser = user.getValue(UserHelperClass.class);
                        //final String nameUser = singleUser.getUsername();
                        if (singleUser.getUsername().equals(valusername)){
                            userExists= true;
                            break;
                        }

                        else{
                            continue;
                        }
                    }

                    if(userExists){
                        isUser();
                    }

                    else{
                        isAdmin();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }

    }


    private void isUser(){

        final String userEnteredUsername = username.getEditText().getText().toString().trim();
        final String userEnteredPassword = password.getEditText().getText().toString().trim();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (userEnteredUsername.matches(emailPattern))
        {
            // c est une adresse mail qui a ete entree
            final String userEnteredEmail = userEnteredUsername;

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Iterable<DataSnapshot> users = snapshot.getChildren();
                     passwordFromDB = "";
                     nameFromDB = "";
                     usernameFromDB = "";
                     phoneNoFromDB = "";
                     emailFromDB = "";
                    boolean emailAlreadyExists = false;

                    for (DataSnapshot user : users) {
                        UserHelperClass singleUser = user.getValue(UserHelperClass.class);
                        String emailUser = singleUser.getEmail();
                        if (!emailUser.equals(userEnteredEmail))
                        {
                            continue;
                        }

                        else{
                            passwordFromDB = singleUser.getPassword();
                            nameFromDB = singleUser.getName();
                            usernameFromDB = singleUser.getUsername();
                            phoneNoFromDB = singleUser.getPhoneNo();
                            emailFromDB = singleUser.getEmail();
                            emailAlreadyExists = true;
                            break;
                        }
                    }

                    if (emailAlreadyExists){
                        if (passwordFromDB.equals(userEnteredPassword)){

                            username.setError(null);
                            username.setErrorEnabled(false);

                            // mise a jour de la preference partagee
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(UsernamePreference,usernameFromDB);
                            editor.putString(IsAdminpreference,"false");
                            editor.putString(EmailPreference,emailFromDB);
                            editor.putString(Passwordpreference,passwordFromDB);
                            editor.commit();


                            Toast.makeText(LoginActivity.this, "Connexion user réussie", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            intent.putExtra("name", nameFromDB);
                            intent.putExtra("email", emailFromDB);
                            intent.putExtra("isAdmin", "false");
                            startActivity(intent);

                            //UserUpdateprofil();


                            //Intent intent = new Intent(getApplicationContext(), UserProfil.class);
                            //intent.putExtra("name", nameFromDB);
                            //intent.putExtra("username", usernameFromDB);
                            //intent.putExtra("email", emailFromDB);
                            //intent.putExtra("phoneNo", phoneNoFromDB);
                            //intent.putExtra("password", passwordFromDB);
                            //startActivity(intent);



                            //initialisation home activity
                            //Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            //startActivity(intent);


                        }
                        else{
                            password.setError("Wrong Password");
                            password.requestFocus();
                        }
                    }
                    else{
                        username.setError("No such Email Address exist");
                        username.requestFocus();





                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        else {
            // c est un username que l utilisateur a tape



            Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);

            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){

                        username.setError(null);
                        username.setErrorEnabled(false);

                        String passwordFromDB = dataSnapshot.child(userEnteredUsername).child("password").getValue(String.class);

                        if (passwordFromDB.equals(userEnteredPassword)){

                            username.setError(null);
                            username.setErrorEnabled(false);
                            String nameFromDB = dataSnapshot.child(userEnteredUsername).child("name").getValue(String.class);
                            String usernameFromDB = dataSnapshot.child(userEnteredUsername).child("username").getValue(String.class);
                            String phoneNoFromDB = dataSnapshot.child(userEnteredUsername).child("phoneNo").getValue(String.class);
                            String emailFromDB = dataSnapshot.child(userEnteredUsername).child("email").getValue(String.class);


                            // mise a jour de la preference partagee
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(UsernamePreference,usernameFromDB);
                            editor.putString(IsAdminpreference,"false");
                            editor.putString(EmailPreference,emailFromDB);
                            editor.putString(Passwordpreference,passwordFromDB);
                            editor.commit();



                            //initialisation home activity
                            Toast.makeText(LoginActivity.this, "Connexion User réussie", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            intent.putExtra("name", nameFromDB);
                            intent.putExtra("email", emailFromDB);
                            intent.putExtra("isAdmin", "false");
                            startActivity(intent);

                            //UserUpdat
                            // eprofil();
                        }
                        else{
                            password.setError("Wrong Password");
                            password.requestFocus();
                        }
                    }
                    else{
                        username.setError("No such User exist");
                        username.requestFocus();
                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void isAdmin(){
        final String adminEnteredUsername = username.getEditText().getText().toString().trim();
        final String adminEnteredPassword = password.getEditText().getText().toString().trim();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin");

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (adminEnteredUsername.matches(emailPattern))
        {
            // c est une adresse mail qui a ete entree
            final String adminEnteredEmail = adminEnteredUsername;

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Iterable<DataSnapshot> admin = snapshot.getChildren();
                    passwordFromDB_admin = "";
                    nameFromDB_admin = "";
                    usernameFromDB_admin = "";
                    phoneNoFromDB_admin = "";
                    emailFromDB_admin = "";
                    boolean emailAlreadyExists = false;

                    for (DataSnapshot Admin : admin) {
                        AdminHelperClass singleAdmin = Admin.getValue(AdminHelperClass.class);
                        String emailAdmin = singleAdmin.getEmail();
                        if (!emailAdmin.equals(adminEnteredEmail))
                        {
                            continue;
                        }

                        else{
                            passwordFromDB_admin = singleAdmin.getPassword();
                            nameFromDB_admin = singleAdmin.getName();
                            usernameFromDB_admin = singleAdmin.getUsername();
                            emailFromDB_admin = singleAdmin.getEmail();
                            emailAlreadyExists = true;
                            break;
                        }
                    }

                    if (emailAlreadyExists){
                        if (passwordFromDB_admin.equals(adminEnteredPassword)){

                            username.setError(null);
                            username.setErrorEnabled(false);

                            // mise a jour de la preference partagee
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(UsernamePreference,usernameFromDB_admin);
                            editor.putString(IsAdminpreference,"true");
                            editor.putString(EmailPreference,emailFromDB_admin);
                            editor.putString(Passwordpreference,passwordFromDB_admin);
                            editor.commit();


                            Toast.makeText(LoginActivity.this, "Connexion Admin réussie", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),AdminHome.class);
                            //intent.putExtra("name", nameFromDB_admin);
                            //intent.putExtra("email", emailFromDB_admin);
                            startActivity(intent);

                            //UserUpdateprofil();


                            //Intent intent = new Intent(getApplicationContext(), UserProfil.class);
                            //intent.putExtra("name", nameFromDB);
                            //intent.putExtra("username", usernameFromDB);
                            //intent.putExtra("email", emailFromDB);
                            //intent.putExtra("phoneNo", phoneNoFromDB);
                            //intent.putExtra("password", passwordFromDB);
                            //startActivity(intent);



                            //initialisation home activity
                            //Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            //startActivity(intent);


                        }
                        else{
                            password.setError("Wrong Password Administrator");
                            password.requestFocus();
                        }
                    }
                    else{
                        username.setError("No such Email Address exists");
                        username.requestFocus();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        else {
            // c est un username que l utilisateur a tape


            reference = FirebaseDatabase.getInstance().getReference("admin");
            Query checkUser = reference.orderByChild("username").equalTo(adminEnteredUsername);

            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){

                        username.setError(null);
                        username.setErrorEnabled(false);

                        String passwordFromDB_admin = dataSnapshot.child(adminEnteredUsername).child("password").getValue(String.class);

                        if (passwordFromDB_admin.equals(adminEnteredPassword)){

                            username.setError(null);
                            username.setErrorEnabled(false);
                            String nameFromDB_admin = dataSnapshot.child(adminEnteredUsername).child("name").getValue(String.class);
                            String usernameFromDB_admin  = dataSnapshot.child(adminEnteredUsername).child("username").getValue(String.class);
                            String emailFromDB_admin  = dataSnapshot.child(adminEnteredUsername).child("email").getValue(String.class);


                            // mise a jour de la preference partagee
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(UsernamePreference,usernameFromDB_admin);
                            editor.putString(IsAdminpreference,"true");
                            editor.putString(EmailPreference,emailFromDB_admin);
                            editor.putString(Passwordpreference,passwordFromDB_admin);
                            editor.commit();

                            Toast.makeText(LoginActivity.this, "Connexion Admin réussie", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),AdminHome.class);
                            startActivity(intent);


                            //UserUpdat
                            // eprofil();
                        }
                        else{
                            password.setError("Wrong Password Administrator");
                            password.requestFocus();
                        }
                    }
                    else{
                        username.setError("No such User exist Administrator");
                        username.requestFocus();
                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void updateUser() {
        startActivity(HomeActivity);
        finish();
    }



    private void UserUpdateprofil() {
        Intent intent = new Intent(getApplicationContext(), UserProfil.class);
        intent.putExtra("name", nameFromDB);
        intent.putExtra("username", usernameFromDB);
        intent.putExtra("email", emailFromDB);
        intent.putExtra("phoneNo", phoneNoFromDB);
        intent.putExtra("password", passwordFromDB);
        //startActivity(intent);
    }



}