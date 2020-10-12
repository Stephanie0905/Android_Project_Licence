package com.example.kelys.Models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.kelys.Adapters.RoomAdapter;
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

public class AdminListingRestoDetail extends AppCompatActivity {


    TextView btn_close;

    EditText textNom, textPrice, textDesc;
    Button updateresto;
    ImageView imageView;
    private Query RestoQuery;
    private String Description, Price, PName,saveSpinner;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private String productRandomKey, downloadImageURI, oldRestoName, oldDescription, oldPrice, oldCategorie ;
    Spinner spinner;
    private DatabaseReference ProductsRef,categ_Resto_ref;
    private StorageReference ProductImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_listing_resto_detail);

        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Images des Restaurants");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Restaurant");


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


        textNom = (EditText) findViewById(R.id.txt2_resto);
        textPrice = (EditText) findViewById(R.id.txt4_resto);
        textDesc = (EditText) findViewById(R.id.txt3_resto);
        spinner = (Spinner) findViewById(R.id.spinner_detail_resto);
        imageView = (ImageView) findViewById(R.id.service_img_resto);
        updateresto = (Button) findViewById(R.id.update_service_resto);
        loadingBar = new ProgressDialog(this);
        oldRestoName = null;
        oldCategorie = null;
        oldDescription = null;
        oldPrice = null;


        String uid = getIntent().getStringExtra("uid");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        updateresto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

        RestoQuery = FirebaseDatabase.getInstance().getReference().child("Restaurant").orderByChild("pid").equalTo(uid);
        RestoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelRestaurant modelRestaurant = d.getValue(ModelRestaurant.class);
                    textNom.setText(modelRestaurant.getPname());
                    textPrice.setText(modelRestaurant.getPrice());
                    textDesc.setText(modelRestaurant.getDescription());
                    Picasso.get().load(modelRestaurant.getImage()).into(imageView);
                    downloadImageURI = modelRestaurant.getImage();


                    oldRestoName = modelRestaurant.getPname();
                    oldCategorie = modelRestaurant.getType_resto();
                    oldDescription = modelRestaurant.getDescription();
                    oldPrice = modelRestaurant.getPrice();

                    productRandomKey = modelRestaurant.getPid();

                    // get list of  resto categories
                    categ_Resto_ref = FirebaseDatabase.getInstance().getReference().child("Categorie_restaurant");

                    categ_Resto_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final List<String> CategRestoList = new ArrayList<String>();
                            for (DataSnapshot sn : snapshot.getChildren())
                            {
                                CategRestoList.add(sn.child("lib_categ_resto").getValue(String.class));


                            }

                            ArrayAdapter<String> categrestoAdapter = new ArrayAdapter<String>(getBaseContext(),
                                    android.R.layout.simple_list_item_1,
                                    CategRestoList){
                                public View getDropDownView(int position, View convertView, ViewGroup parent)
                                {
                                    View view = super.getDropDownView(position,convertView,parent);
                                    TextView tv = (TextView) view;
                                    tv.setTextColor(Color.BLACK);
                                    return view;
                                }
                            };
                            //hotelAdapter.notifyDataSetChanged();
                            spinner.setAdapter(categrestoAdapter);

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

        UpdateProductInformation();
    }

    private void UpdateProductInformation() {
        loadingBar.setTitle("Modification des Restaurants");
        loadingBar.setMessage("Cher Admin, Patientez SVP, nous sommes en train de modifier le Restaurant");
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
                    Toast.makeText(AdminListingRestoDetail.this,"Erreur: " + message,Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AdminListingRestoDetail.this,"Enregistrement En Cours...",Toast.LENGTH_SHORT).show();

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

                                Toast.makeText(AdminListingRestoDetail.this,"Image enregistrée dans la base de données",Toast.LENGTH_SHORT).show();

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

        if (!PName.equals(oldRestoName))
        {
            ProductsRef.child(productRandomKey).child("pname").setValue(PName);
        }

        if (!saveSpinner.equals(oldCategorie))
        {
            ProductsRef.child(productRandomKey).child("type_resto").setValue(saveSpinner);
        }


        Intent intent = new Intent(AdminListingRestoDetail.this, ListingRestaurant.class);
        startActivity(intent);

        loadingBar.dismiss();
        Toast.makeText(AdminListingRestoDetail.this,"Les infos sur le restaurant ont été modifiés avec Succès...",Toast.LENGTH_SHORT).show();


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