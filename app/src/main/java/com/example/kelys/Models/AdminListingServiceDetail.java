package com.example.kelys.Models;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kelys.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdminListingServiceDetail extends AppCompatActivity  {

    TextView btn_close;
    Button updatehotel;
    EditText textNom, textPrice, textDesc ;
    private String Description, Price, PName,saveSpinner;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private String productRandomKey, downloadImageURI, oldHotelName, oldDescription, oldPrice, oldCategorie, oldRateHotel ;
    private int oldRateHotelId;
    ImageView imageView;
    Spinner spinner;
    private Query HotelQuery, RoomQuery;
    private DatabaseReference  ProductsRef,categ_hotel_ref;
    private StorageReference ProductImageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_listing_service_detail);

        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Images des Hotels");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Hotel");

        btn_close = (TextView) findViewById(R.id.menu_icone);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        DisplayMetrics ds = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(ds);

        int width = ds.widthPixels;
        int height = ds.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        textNom = (EditText) findViewById(R.id.txt2);
        textDesc = (EditText) findViewById(R.id.txt3);
        textPrice = (EditText) findViewById(R.id.txt4);
        spinner = (Spinner) findViewById(R.id.spinner_detail_hotel);
        imageView = (ImageView) findViewById(R.id.service_img);
        updatehotel = (Button) findViewById(R.id.update_service);
        loadingBar = new ProgressDialog(this);
        oldHotelName = null;
        oldCategorie = null;
        oldDescription = null;
        oldPrice = null;
        oldRateHotel = null;

        String uid = getIntent().getStringExtra("uid");
        HotelQuery = FirebaseDatabase.getInstance().getReference().child("Hotel").orderByChild("pid").equalTo(uid);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        updatehotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

        HotelQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    final ModelHotel hotel = d.getValue(ModelHotel.class);
                    textNom.setText(hotel.getPname());
                    textPrice.setText(hotel.getPrice());
                    textDesc.setText(hotel.getDescription());
                    Picasso.get().load(hotel.getImage()).into(imageView);
                    downloadImageURI = hotel.getImage();

                    oldHotelName = hotel.getPname();
                    oldCategorie = hotel.getCategory();
                    oldDescription = hotel.getDescription();
                    oldPrice = hotel.getPrice();
                    oldRateHotel = hotel.getRate_hotel();

                    productRandomKey = hotel.getPid();

                    final int etoile = Integer.parseInt(String.valueOf(hotel.getRate_hotel().charAt(0)));

                    // get list of  hotels categories
                    categ_hotel_ref = FirebaseDatabase.getInstance().getReference().child("Categorie_hotel");

                    categ_hotel_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final List<String> CategHotelList = new ArrayList<String>();
                            int compteur = 0;
                            for (DataSnapshot sn : snapshot.getChildren())
                            {
                                CategHotelList.add(sn.child("lib_categ_hotel").getValue(String.class));

                                if(sn.child("lib_categ_hotel").getValue(String.class).equals(oldRateHotel))
                                {
                                    oldRateHotelId = compteur;
                                }

                                compteur++;
                            }

                            ArrayAdapter<String> categhotelAdapter = new ArrayAdapter<String>(getBaseContext(),
                                    android.R.layout.simple_list_item_1,
                                    CategHotelList){
                                public View getDropDownView(int position, View convertView, ViewGroup parent)
                                {
                                    View view = super.getDropDownView(position,convertView,parent);
                                    TextView tv = (TextView) view;
                                    tv.setTextColor(Color.BLACK);
                                    return view;
                                }
                            };
                            //hotelAdapter.notifyDataSetChanged();
                            spinner.setAdapter(categhotelAdapter);
                            spinner.setSelection(oldRateHotelId);
                            /*
                            switch (etoile)
                            {
                                case 1 :
                                    spinner.setSelection(0);

                                    break;
                                case 2 :
                                    spinner.setSelection(1);
                                    break;
                                case 3 :
                                    spinner.setSelection(2);
                                    break;
                                case 4 :
                                    spinner.setSelection(3);
                                    break;
                                case 5 :
                                    spinner.setSelection(4);
                                    break;
                            }
*/
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void ValidateProductData() {
        Description = textDesc.getText().toString();
        Price = textPrice.getText().toString();
        PName = textNom.getText().toString();
        saveSpinner = spinner.getSelectedItem().toString();

        updateRoomInformation();
        UpdateProductInformation();

/*
        if (ImageUri == null){
            Toast.makeText(this,"Veuillez télécharger une image svp...", Toast.LENGTH_SHORT).show();
        }
        */
        /*if (TextUtils.isEmpty(Description)){
            //Toast.makeText(this,"Veuillez ecrire la description du produit...",Toast.LENGTH_SHORT).show();
            UpdateProductInformation();

        }
        else if (TextUtils.isEmpty(Price)){
            //Toast.makeText(this,"Veuillez entrer le prix du produit...",Toast.LENGTH_SHORT).show();
            UpdateProductInformation();

        }
        else if (TextUtils.isEmpty(PName)){
            //Toast.makeText(this,"Veuillez entrer le nom du produit...",Toast.LENGTH_SHORT).show();
            UpdateProductInformation();

        }
        else if (saveSpinner == null)
        {
            //Toast.makeText(this,"Veuillez choisir une catégorie d'hotel...",Toast.LENGTH_SHORT).show();
            UpdateProductInformation();

        }
        else {
            UpdateProductInformation();
        }*/

    }

    private void updateRoomInformation() {
        RoomQuery = FirebaseDatabase.getInstance().getReference().child("Chambre").orderByChild("hotelName").equalTo(oldHotelName);
        RoomQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sn : snapshot.getChildren())
                {
                    if (!PName.equals(oldHotelName))
                    {
                        // modifier le nom de l'hotel dans la table Chambre
                        sn.child("hotelName").getRef().setValue(PName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void UpdateProductInformation() {
        loadingBar.setTitle("Modification des Hotels");
        loadingBar.setMessage("Cher Admin, Patientez SVP, nous sommes en train de modifier l'hotel");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        if (ImageUri != null)
        {

            final StorageReference filePath = ProductImageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

            final UploadTask uploadTask = filePath.putFile(ImageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(AdminListingServiceDetail.this,"Erreur: " + message,Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AdminListingServiceDetail.this,"Enregistrement En Cours...",Toast.LENGTH_SHORT).show();

                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();

                            }

                            downloadImageURI = filePath.getDownloadUrl().toString();
                            return filePath.getDownloadUrl();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                downloadImageURI = task.getResult().toString();

                                Toast.makeText(AdminListingServiceDetail.this,"Image enregistrée dans la base de données",Toast.LENGTH_SHORT).show();

                                UpdateProductInfoToDatabase();
                            }
                        }
                    });
                }
            });
        }

        UpdateProductInfoToDatabase();


    }

    private void UpdateProductInfoToDatabase() {

        ProductsRef.child(productRandomKey).child("image").setValue(downloadImageURI);

        if (!Description.equals(oldDescription))
        {
            ProductsRef.child(productRandomKey).child("description").setValue(Description);
        }



        if (!Price.equals(oldPrice))
        {
            ProductsRef.child(productRandomKey).child("price").setValue(Price);
        }

        if (!PName.equals(oldHotelName))
        {
            ProductsRef.child(productRandomKey).child("pname").setValue(PName);
        }

        if (!saveSpinner.equals(oldRateHotel))
        {
            ProductsRef.child(productRandomKey).child("rate_hotel").setValue(saveSpinner);
        }







        Intent intent = new Intent(AdminListingServiceDetail.this, ListingHotel.class);
        startActivity(intent);

        loadingBar.dismiss();
        Toast.makeText(AdminListingServiceDetail.this,"Les infos sur l'hotel ont été modifiés avec Succès...",Toast.LENGTH_SHORT).show();


    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){

            ImageUri = data.getData();
            imageView.setImageURI(ImageUri);

        }
    }
}