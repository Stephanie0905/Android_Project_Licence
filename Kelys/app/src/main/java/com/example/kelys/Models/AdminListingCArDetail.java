package com.example.kelys.Models;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.example.kelys.Activities.AdminAddNewVehicule;
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
import java.util.Arrays;
import java.util.List;

public class AdminListingCArDetail extends AppCompatActivity {

    TextView btn_close,optionselected;
    Button updatecar,Option;
    EditText textNom, textPrice, textDesc ;
    private String Description, Price, PName,saveSpinner,saveSpinner1;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private String productRandomKey, downloadImageURI, oldCarName, oldDescription, oldPrice, oldCategorie, oldTypeCar,optionName ;
    private int oldCarId,optionNameId;
    ImageView imageView;
    Spinner spinner;
    private Query CarQuery;
    private DatabaseReference ProductsRef,categ_car_ref,option_ref;
    private StorageReference ProductImageRef;
    List<String> optionList;
    List<String> optionChekedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_listing_c_ar_detail);
        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Images des Vehicules");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Vehicule");

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
        textPrice = (EditText) findViewById(R.id.txt3);
        textDesc = (EditText) findViewById(R.id.txt4);
        spinner = (Spinner) findViewById(R.id.spinner_detail_car);
        imageView = (ImageView) findViewById(R.id.service_img);
        updatecar = (Button) findViewById(R.id.update_service);
        loadingBar = new ProgressDialog(this);
        oldCarName = null;
        oldCategorie = null;
        oldDescription = null;
        oldPrice = null;
        oldTypeCar = null;

        String uid = getIntent().getStringExtra("uid");
        CarQuery = FirebaseDatabase.getInstance().getReference().child("Vehicule").orderByChild("pid").equalTo(uid);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        updatecar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

        CarQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final List<String> OptionList = new ArrayList<String>();
                for (DataSnapshot d : snapshot.getChildren()) {
                    Modelvehicule modelvehicule = d.getValue(Modelvehicule.class);
                    textNom.setText(modelvehicule.getPname());
                    textPrice.setText(modelvehicule.getPrice());
                    textDesc.setText(modelvehicule.getDescription());
                    Picasso.get().load(modelvehicule.getImage()).into(imageView);
/*
                    ModelOption modelOption =  d.child("options").getValue(ModelOption.class);
                    List<String> listOptions = modelOption.getOptions();
                    int compteurOption = 0;
                    for (int i = 0; i < listOptions.size(); i++)
                    {
                        OptionList.add(listOptions.get(i));

                        if (listOptions.get(i).equals(optionName)) {
                            optionNameId = compteurOption;
                        }

                        compteurOption++;


                    } */


                    //OptionList.add(sn.child("lib").getValue(String.class));




                    downloadImageURI = modelvehicule.getImage();

                    oldCarName = modelvehicule.getPname();
                    oldCategorie = modelvehicule.getCategory();
                    oldDescription = modelvehicule.getDescription();
                    oldPrice = modelvehicule.getPrice();
                    oldTypeCar = modelvehicule.getType_car();

                    productRandomKey = modelvehicule.getPid();

                    // get list of  vehicles categories
                    categ_car_ref = FirebaseDatabase.getInstance().getReference().child("Type_vehicule");

                    categ_car_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final List<String> CategCarList = new ArrayList<String>();
                            int compteur = 0;
                            for (DataSnapshot sn : snapshot.getChildren()) {
                                CategCarList.add(sn.child("lib_type").getValue(String.class));

                                if (sn.child("lib_type").getValue(String.class).equals(oldTypeCar)) {
                                    oldCarId = compteur;
                                }

                                compteur++;
                            }
                            ArrayAdapter<String> categcarAdapter = new ArrayAdapter<String>(getBaseContext(),
                                    android.R.layout.simple_list_item_1,
                                    CategCarList) {
                                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getDropDownView(position, convertView, parent);
                                    TextView tv = (TextView) view;
                                    tv.setTextColor(Color.BLACK);
                                    return view;
                                }
                            };

                            spinner.setAdapter(categcarAdapter);
                            spinner.setSelection(oldCarId);
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


        Option = (Button) findViewById(R.id.btnOption);
        optionselected = (TextView) findViewById(R.id.opt_select);


        Option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminListingCArDetail.this);

                //string array for alert dialog multicheckbox item
                String[] optionArray = new String[]{"AutoRadio","Climatisation","Boite Manuelle","Boite Automatique","GPS","Ordinateur de Bord","Caméra de recul","Vitres Electriques"};

                final boolean[] checkedoptionArray = new boolean[]{
                        true,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false
                };

                optionList = Arrays.asList(optionArray);
                optionChekedList = new ArrayList<String>();
                builder.setTitle("Selectionne les options");

                //set multichoice
                builder.setMultiChoiceItems(optionArray, checkedoptionArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedoptionArray[which] = isChecked;

                        String currentItem = optionList.get(which);
                        // Toast.makeText(AdminAddNewVehicule.this, currentItem +" " + isChecked, Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        optionselected.setText("Vos choix sont: \n");
                        for (int i = 0; i<checkedoptionArray.length; i++){
                            boolean checked = checkedoptionArray[i];

                            if (checked){
                                // optionselected.setText(optionselected.getText() + optionList.get(i) + "\n");
                                optionChekedList.add(optionList.get(i));
                            }
                        }
                    }
                });

                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }

    private void ValidateProductData() {
        Description = textDesc.getText().toString();
        Price = textPrice.getText().toString();
        PName = textNom.getText().toString();
        saveSpinner = spinner.getSelectedItem().toString();
        //saveSpinner1 = spinner1.getSelectedItem().toString();

        UpdateProductInformation();

    }

    private void UpdateProductInformation() {
        loadingBar.setTitle("Modification des Véhicules");
        loadingBar.setMessage("Cher Admin, Patientez SVP, nous sommes en train de modifier le Véhicule");
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
                    Toast.makeText(AdminListingCArDetail.this,"Erreur: " + message,Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AdminListingCArDetail.this,"Enregistrement En Cours...",Toast.LENGTH_SHORT).show();

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

                                Toast.makeText(AdminListingCArDetail.this,"Image enregistrée dans la base de données",Toast.LENGTH_SHORT).show();

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
        ProductsRef.child(productRandomKey).child("description").setValue(Description);
        ProductsRef.child(productRandomKey).child("image").setValue(downloadImageURI);
        ProductsRef.child(productRandomKey).child("price").setValue(Price);
        ProductsRef.child(productRandomKey).child("pname").setValue(PName);
        ProductsRef.child(productRandomKey).child("hotelName").setValue(saveSpinner1);
        ProductsRef.child(productRandomKey).child("detail_room").setValue(saveSpinner);

        Intent intent = new Intent(AdminListingCArDetail.this, ListingVehicule.class);
        startActivity(intent);

        loadingBar.dismiss();
        Toast.makeText(AdminListingCArDetail.this,"Le Produit a été modifié avec Succès...",Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

}
