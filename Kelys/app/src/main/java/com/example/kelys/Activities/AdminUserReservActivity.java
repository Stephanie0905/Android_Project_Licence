package com.example.kelys.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kelys.JavaMail.JavaMailAPI;
import com.example.kelys.Models.ModelOrders;
import com.example.kelys.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AdminUserReservActivity extends AppCompatActivity {

    TextInputLayout fullName,email,phoneNo,date1,date2,id_product,date_reserv,price;

    String user_name, user_email, user_phoneNo, user_date_reserv,prod_price,prod_id,date1_reserv,date2_reserv;

    Button confirm_reserv,delete_reserv;



    DatabaseReference reference;

    private String userId = "";
    private String categorie = "";
    private String statut = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_reserv);

        reference = FirebaseDatabase.getInstance().getReference()
                .child("Reservations").child(userId);

        userId = getIntent().getStringExtra("uid");


        Log.d("myself", "me");
        //Hooks
        fullName = findViewById(R.id.name_profil);
        email = findViewById(R.id.mail_profil);
        phoneNo = findViewById(R.id.phoneNo_profil);
        date1 = findViewById(R.id.date1);
        date2 = findViewById(R.id.date2);
        id_product = findViewById(R.id.id_product);
        date_reserv = findViewById(R.id.date_reserv);
        price = findViewById(R.id.price_reserv);
        confirm_reserv = findViewById(R.id.validate_reserv);
        delete_reserv = findViewById(R.id.delete_reserv);



        Intent intent = getIntent();
        user_date_reserv = intent.getStringExtra("uid");
        user_name = intent.getStringExtra("name user");
        user_email = intent.getStringExtra("mail user");
        user_phoneNo = intent.getStringExtra("phone user");
        prod_price = intent.getStringExtra("price");
        prod_id = intent.getStringExtra("Nom_produit");
        date1_reserv = intent.getStringExtra("date1");
        date2_reserv = intent.getStringExtra("date2");
        categorie = intent.getStringExtra("categorie");
        statut = intent.getStringExtra("statut");
        fullName.getEditText().setText(user_name);
        email.getEditText().setText(user_email);
        phoneNo.getEditText().setText(user_phoneNo);
        date_reserv.getEditText().setText(user_date_reserv);
        date1.getEditText().setText(date1_reserv);
        date2.getEditText().setText(date2_reserv);
        id_product.getEditText().setText(prod_id);
        price.getEditText().setText(prod_price);




        //showalldata
        //showAllUserData();


        delete_reserv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //boite de dialogue pour confirmer la reservation
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AdminUserReservActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Suppression de la réservation");
                builder.setMessage("Voulez-vous supprimer cette réservation ?");
                builder.setPositiveButton("Confirmer",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                delete_reservation(user_date_reserv);

                                switch (categorie)
                                {
                                    case "Chambre":
                                        delete_reservation("Chambre",user_date_reserv);
                                        annulation_reservation();
                                        break;

                                    case "Residence":
                                        delete_reservation("Residence",user_date_reserv);
                                        annulation_reservation();
                                        break;

                                    case "Restaurant":
                                        delete_reservation("Restaurant",user_date_reserv);
                                        annulation_reservation();
                                        break;
                                    case "Vehicule":
                                        delete_reservation("Vehicule",user_date_reserv);
                                        annulation_reservation();
                                        break;

                                }



                                Intent intent = new Intent(getApplicationContext(), AdminNewOrderActivity.class);
                                intent.putExtra("statut",statut);
                                startActivity(intent);

                            }

                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }





    private void delete_reservation(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Reservations").child(id);

        //ref.removeValue();
        ref.setValue(null);

    }

    private void delete_reservation(String table, String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Reservation "+table).child(id);

        //ref.removeValue();
        ref.setValue(null);


        Toast.makeText(this,"Réservation Supprimée!!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ModelOrders> options =
                new FirebaseRecyclerOptions.Builder<ModelOrders>()
                .setQuery(reference, ModelOrders.class)
                .build();

    }




    public void onBackPressed()
    {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), AdminNewOrderActivity.class);
        intent.putExtra("statut",statut);
        startActivity(intent);
    }

    public String generateinvoice() {
        // generer le recu

        // creer le document PDF
        PdfDocument document = new PdfDocument();

        // Description de la page

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200,2010,1).create();


        // demarrer la page du document
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        paint.setColor(Color.RED);
        canvas.drawText("Bonjour, Votre réservation vient d'être validée.",80,50, paint);

        paint.setColor(Color.BLACK);
        canvas.drawText("voici les détails de la réservation : ",80,100, paint);



        canvas.drawText("Date de réservation : "+user_date_reserv,80,150, paint);
        canvas.drawText("Nom du demandeur : "+user_name,80,200, paint);
        canvas.drawText("Email du demandeur : "+user_email,80,250, paint);
        canvas.drawText("Numéro de téléphone du demandeur : "+user_phoneNo,80,300, paint);
        canvas.drawText("Prix de la réservation : "+prod_price,80,350, paint);
        canvas.drawText("Catégorie du produit réservé : "+categorie,80,400, paint);

        if (date2_reserv.equals(""))
        {
            canvas.drawText("La réservation a été faite le  : "+date1_reserv,80,450, paint);
        }

        else
        {
            canvas.drawText("La réservation a été faite entre le  : "+date1_reserv+" et le "+ date2_reserv,80,450, paint);

        }



        // finish the page
        document.finishPage(page);



        // write the document content
        // NE PAS MODIFIER CETTE SECTION
        String directory_path = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()+"/pdf/";
        File file = new File(directory_path);

        if (!file.exists())
        {
            file.mkdirs();
        }

        String targetPdf = directory_path+"reçu.pdf";
        File filePath = new File(targetPdf);

        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(getApplicationContext(), "Reçu généré", Toast.LENGTH_SHORT).show();
        }

        catch (IOException e)
        {
            Log.e("Main","error "+ e.toString());
            Toast.makeText(getApplicationContext(), "Something wrong: "+ e.toString(), Toast.LENGTH_SHORT).show();
        }


        // close the document
        document.close();

        return targetPdf;


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


    public void validation_reservation(View view) {

        String targetPdf = generateinvoice();

        String objet_mail = "Confirmation de la réservation de "+user_name+" du "+user_date_reserv;
        //String message = "Bonjour Monsieur/Madame " + user_name+ ".\nNous venons de prendre acte de votre réservation. \nVeuillez par ce mail prendre acte de l'enregistrement de votre réservation. Nous vous recontacterons pour plus de détails. \nCordialement, \n\nKelys Team Development.";

        String message = null;
        try {
            // chargement du template mail
            message = getMailContent("MailConfirmationReservation.html");
            message = message.replace("{categorie}", categorie);
            message = message.replace("{nomProduit}", prod_id);
            message = message.replace("{cout}", prod_price);
            message = message.replace("{email}", user_email);
            message = message.replace("{phone}", user_phoneNo);


            if (categorie.equals("Restaurant"))
            {
                message = message.replace("{date}", "Le "+date1_reserv);
            }

            else
                {
                    message = message.replace("{date}", "Du "+date1_reserv+" au "+date2_reserv);
                }



            //envoi du mail
            sendMailWithAttachment(user_name,user_email,objet_mail,message, targetPdf);

        } catch (IOException e) {
            e.printStackTrace();
        }



        setStatut("Validé");


        Intent intent = new Intent(getApplicationContext(), AdminNewOrderActivity.class);
        intent.putExtra("statut",statut);
        startActivity(intent);



    }

    public void annulation_reservation() {
        String objet_mail = "Annulation de la réservation de "+user_name+" du "+user_date_reserv;
        //String message = "Bonjour Monsieur/Madame" + user_name+ ".\nVotre réservation a été annulée par notre équipe. \nCordialement, \n\nKelys Team Development.";




        //String message = "Bonjour Monsieur/Madame " + user_name+ ".\nNous venons de prendre acte de votre réservation. \nVeuillez par ce mail prendre acte de l'enregistrement de votre réservation. Nous vous recontacterons pour plus de détails. \nCordialement, \n\nKelys Team Development.";

        String message = null;
        try {
            // chargement du template mail
            message = getMailContent("MailAnnulationReservation.html");
            message = message.replace("{categorie}", categorie);
            message = message.replace("{nomProduit}", prod_id);
            message = message.replace("{cout}", prod_price);
            message = message.replace("{email}", user_email);
            message = message.replace("{phone}", user_phoneNo);


            if (categorie.equals("Restaurant"))
            {
                message = message.replace("{date}", "Le "+date1_reserv);
            }

            else
            {
                message = message.replace("{date}", "Du "+date1_reserv+" au "+date2_reserv);
            }



            //envoi du mail
            sendMail(user_name,user_email,objet_mail,message);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void sendMail(String username,String email, String subject, String message) {

        //envoi du mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email,subject, message);
        javaMailAPI.execute();



    }

    private void sendMailWithAttachment(String username,String email, String subject, String message, String FileName) {

        //envoi du mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email,subject, message, FileName);
        javaMailAPI.execute();



    }

    private void setStatut(String newStatut)
    {
        this.statut = newStatut;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Reservations");;
        reference.child(user_date_reserv).child("statut").setValue(newStatut);
        reference.child(user_date_reserv).child("mail_user_statut").setValue(user_email+"_"+newStatut);
    }
}