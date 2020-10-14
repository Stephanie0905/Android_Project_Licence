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

import com.example.kelys.Adapters.ResidenceAdapter;
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

public class AdminListingResidenceDetail extends AppCompatActivity {
    TextView btn_close;

    EditText textNom, textPrice, textDesc;
    Button updateresid;
    ImageView imageView;
    private Query ResidQuery;
    private String Description, Price, PName,saveSpinner;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private String productRandomKey, downloadImageURI, oldresidName, oldDescription, oldPrice, oldCategorie ;
    private int oldCategorieId ;
    Spinner spinner;
    private DatabaseReference ProductsRef,categ_resid_ref;
    private StorageReference ProductImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_listing_residence_detail);

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

        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Images des Residences");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Residence");

        loadingBar = new ProgressDialog(this);


        textNom = (EditText) findViewById(R.id.txt2_resid);
        textPrice = (EditText) findViewById(R.id.txt4_resid);
        textDesc = (EditText) findViewById(R.id.txt3_resid);
        spinner = (Spinner) findViewById(R.id.spinner_detail_resid);
        imageView = (ImageView) findViewById(R.id.service_img_resid);
        updateresid = (Button) findViewById(R.id.update_service_resid);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        updateresid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

        String uid = getIntent().getStringExtra("uid");
        ResidQuery = FirebaseDatabase.getInstance().getReference().child("Residence").orderByChild("pid").equalTo(uid);
        ResidQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    ResidenceAdapter residenceAdapter = d.getValue(ResidenceAdapter.class);
                    textNom.setText(residenceAdapter.getPname());
                    textPrice.setText(residenceAdapter.getPrice());
                    textDesc.setText(residenceAdapter.getDescription());
                    Picasso.get().load(residenceAdapter.getImage()).into(imageView);

                    downloadImageURI = residenceAdapter.getImage();


                    oldresidName = residenceAdapter.getPname();
                    oldCategorie = residenceAdapter.getType_resid();
                    oldDescription = residenceAdapter.getDescription();
                    oldPrice = residenceAdapter.getPrice();

                    productRandomKey = residenceAdapter.getPid();

                    // get list of  resto categories
                    categ_resid_ref = FirebaseDatabase.getInstance().getReference().child("Categorie_residence");

                    categ_resid_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final List<String> CategResidList = new ArrayList<String>();
                            int compteur = 0;
                            for (DataSnapshot sn : snapshot.getChildren())
                            {
                                CategResidList.add(sn.child("lib_categ_resid").getValue(String.class));

                                if (sn.child("lib_categ_resid").getValue(String.class).equals(oldCategorie))
                                {
                                    oldCategorieId = compteur;
                                }

                                compteur++;

                            }

                            ArrayAdapter<String> categresidAdapter = new ArrayAdapter<String>(getBaseContext(),
                                    android.R.layout.simple_list_item_1,
                                    CategResidList){
                                public View getDropDownView(int position, View convertView, ViewGroup parent)
                                {
                                    View view = super.getDropDownView(position,convertView,parent);
                                    TextView tv = (TextView) view;
                                    tv.setTextColor(Color.BLACK);
                                    return view;
                                }
                            };
                            //hotelAdapter.notifyDataSetChanged();
                            spinner.setAdapter(categresidAdapter);
                            spinner.setSelection(oldCategorieId);

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
        loadingBar.setTitle("Modification des Residences");
        loadingBar.setMessage("Cher Admin, Patientez SVP, nous sommes en train de modifier le Residences");
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
                    Toast.makeText(AdminListingResidenceDetail.this,"Erreur: " + message,Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AdminListingResidenceDetail.this,"Enregistrement En Cours...",Toast.LENGTH_SHORT).show();

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

                                Toast.makeText(AdminListingResidenceDetail.this,"Image enregistrée dans la base de données",Toast.LENGTH_SHORT).show();

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

        if (!PName.equals(oldresidName))
        {
            ProductsRef.child(productRandomKey).child("pname").setValue(PName);
        }

        if (!saveSpinner.equals(oldCategorie))
        {
            ProductsRef.child(productRandomKey).child("type_resid").setValue(saveSpinner);
        }


        Intent intent = new Intent(AdminListingResidenceDetail.this, ListingResidence.class);
        startActivity(intent);

        loadingBar.dismiss();
        Toast.makeText(AdminListingResidenceDetail.this,"Les infos sur la résidence ont été modifiés avec Succès...",Toast.LENGTH_SHORT).show();

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