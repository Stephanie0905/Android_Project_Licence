package com.example.kelys.Models;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.google.android.gms.internal.firebase_auth.zzla;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminListingCArDetail extends AppCompatActivity {

    TextView btn_close,optionselected;
    Button updatecar,Option;
    EditText textNom, textPrice, textDesc ;
    private String Description, Price, PName,saveSpinner,saveSpinner1;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private String productRandomKey, downloadImageURI, oldCarName, oldDescription, oldPrice, oldCategorie, oldTypeCar,optionName ;
    private int oldCarId;
    ImageView imageView;
    Spinner spinner;
    private Query CarQuery;
    private DatabaseReference ProductsRef,categ_car_ref;
    private StorageReference ProductImageRef;
    List<String> optionList;
    List<String> optionChekedList;

    boolean[] checkedoptionArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_listing_c_ar_detail);
        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Images des Vehicules");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Vehicule");

        optionList =new ArrayList<>();

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
        textPrice = (EditText) findViewById(R.id.txt4);
        textDesc = (EditText) findViewById(R.id.txt3);
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

                // la fonction ci-dessous charge les options de véhicule définies dans FIREBASE
                DefineOptionList();
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

        //List<String> selectedOptions = new ArrayList<>();
        HashMap<String,Object> selectedOptions = new HashMap<>();
        int compteurOption = 0;

        //enregistrer les options
        for (int i = 0; i < checkedoptionArray.length; i++)
        {
            if (checkedoptionArray[i] == true)
            {
               // optionList;
                //selectedOptions.add(optionList.get(i));
                compteurOption++;
                selectedOptions.put("option "+compteurOption,optionList.get(i));


            }
        }
        ProductsRef.child(productRandomKey).child("options").removeValue();
        ProductsRef.child(productRandomKey).child("options").setValue(selectedOptions);
        ProductsRef.child(productRandomKey).child("type_car").setValue(saveSpinner);

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



    private void DefineOptionList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminListingCArDetail.this);


        // get list of options in firebase
        DatabaseReference optionRef = FirebaseDatabase.getInstance().getReference().child("Options_vehicule");
        List<String> optionL = new ArrayList<>();
        optionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sn : snapshot.getChildren())
                {
                    optionList.add(sn.child("lib").getValue(String.class));
                    optionL.add(sn.child("lib").getValue(String.class));
                }

                //optionList = Arrays.asList(optionArray);
                optionChekedList = new ArrayList<String>();
                builder.setTitle("Selectionne les options");


                if (checkedoptionArray == null)
                {
                    checkedoptionArray = new boolean[optionL.size()];




                    // A REVOIR CI-DESOUS
                    String[] optionArray = null;
                    optionArray = (String[]) optionL.toArray(new String[optionL.size()]);
                    String[] finalOptionArray = optionArray;
                    String uid = getIntent().getStringExtra("uid");
                    Query CarQ = FirebaseDatabase.getInstance().getReference().child("Vehicule").orderByChild("pid").equalTo(uid);
                    String[] finalOptionArray1 = optionArray;
                    CarQ.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            for (DataSnapshot sn : snapshot.getChildren())
                            {


                                Modelvehicule opt =  sn.getValue(Modelvehicule.class);
                                Map<String, Object> currentOptions = opt.getOptions();

                                for (Map.Entry<String, Object> cu : currentOptions.entrySet())
                                {
                                    for (int i = 0; i < finalOptionArray1.length; i++)
                                    {
                                        if (cu.getValue().toString().equals(finalOptionArray1[i]))
                                        {
                                            checkedoptionArray[i] = true;
                                        }
                                    }
                                }



                                Log.d("OPT VEHICULE", opt.toString());

                                /*
                                for (int i = 0; i< opt.getOptions().size(); i++)
                                {
                                    for (int j = 0; j < finalOptionArray.length; j++)
                                    {
                                        if (opt.getOptions().get(i).equals(finalOptionArray[j]))
                                        {
                                            checkedoptionArray[j] = true;
                                            break;
                                        }
                                    }
                                }
                                */
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    // A REVOIR CI-DESSUS

                    //set multichoice


                    // store checked options
                    //List<String> checkedOptionsList = new ArrayList<>();
                    builder.setMultiChoiceItems(optionArray, checkedoptionArray, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            checkedoptionArray[which] = isChecked;

                            String currentItem = optionL.get(which);
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
                                    optionChekedList.add(optionL.get(i));
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

                else {
                    //set multichoice
                    String[] optionArray = null;
                    optionArray = (String[]) optionL.toArray(new String[optionL.size()]);
                    // store checked options
                    //List<String> checkedOptionsList = new ArrayList<>();
                    builder.setMultiChoiceItems(optionArray, checkedoptionArray, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            checkedoptionArray[which] = isChecked;

                            String currentItem = optionL.get(which);
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
                                    optionChekedList.add(optionL.get(i));
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


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }





}

