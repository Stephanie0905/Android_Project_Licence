package com.example.kelys.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AdminAddNewVehicule extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button addnewproduct,Option;
    private EditText inputproductname, inputproductdescription, inputproducttprice;
    private ImageView inputproductimage;
    private String CategoryName, Description, Price, PName, saveCurrentDate, saveCurrentTime;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private String productRandomKey, downloadImageURI,saveSpinner;
    private StorageReference ProductImageRef;
    private DatabaseReference ProductsRef,TypeCar;
    private Spinner spinner;
    TextView optionselected;
    List<String> optionList;
    List<String> optionChekedList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // empecher le clavier de s'afficher
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_admin_add_new_vehicule);
        CategoryName = getIntent().getExtras().get("Categorie").toString();

        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Images des Vehicules");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Vehicule");

        inputproductdescription = (EditText) findViewById(R.id.product_car_description);
        inputproductname = (EditText) findViewById(R.id.product_car_name);
        inputproducttprice = (EditText) findViewById(R.id.product_car_price);
        addnewproduct = (Button) findViewById(R.id.add_new_car);
        inputproductimage = (ImageView) findViewById(R.id.select_car_img);
        loadingBar = new ProgressDialog(this);

        spinner = (Spinner) findViewById(R.id.spinner_car);
        spinner.setOnItemSelectedListener(this);

        // get list of vehicules
        TypeCar = FirebaseDatabase.getInstance().getReference().child("Type_vehicule");

        TypeCar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final List<String> CarList = new ArrayList<String>();
                for (DataSnapshot sn : snapshot.getChildren())
                {
                    //Log.d("type de vehicule",sn.child("lib_type").getValue(String.class).toString());
                    CarList.add(sn.child("lib_type").getValue(String.class));

                }

                ArrayAdapter<String> carAdapter = new ArrayAdapter<String>(getBaseContext(),
                        android.R.layout.simple_list_item_1,
                        CarList){
                    public View getDropDownView(int position, View convertView, ViewGroup parent)
                    {
                        View view = super.getDropDownView(position,convertView,parent);
                        TextView tv = (TextView) view;
                        tv.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                //hotelAdapter.notifyDataSetChanged();
                spinner.setAdapter(carAdapter);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Option = (Button) findViewById(R.id.btnOption);
        optionselected = (TextView) findViewById(R.id.opt_select);

        inputproductimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addnewproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

        Option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefineOptionList();
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminAddNewVehicule.this);

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
                */
            }
        });


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
            inputproductimage.setImageURI(ImageUri);

        }
    }

    private void ValidateProductData() {
        Description = inputproductdescription.getText().toString();
        Price = inputproducttprice.getText().toString();
        PName = inputproductname.getText().toString();
        saveSpinner = spinner.getSelectedItem().toString();


        if (ImageUri == null){
            Toast.makeText(this,"Veuillez télécharger une image svp...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description)){
            Toast.makeText(this,"Veuillez ecrire la description du véhicule...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price)){
            Toast.makeText(this,"Veuillez entrer le prix du véhicule...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(PName)){
            Toast.makeText(this,"Veuillez entrer le nom du véhicule...",Toast.LENGTH_SHORT).show();
        }
        else if (saveSpinner == null)
        {
            Toast.makeText(this,"Veuillez choisir un type de véhicule...",Toast.LENGTH_SHORT).show();
        }
        else {
            StoreProductInformation();
        }

    }

    private void StoreProductInformation() {

        loadingBar.setTitle("Ajout de nouveaux produits");
        loadingBar.setMessage("Cher Admin, Patientez SVP, nous sommes en train d'ajouter le nouveau produit");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = ProductImageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewVehicule.this,"Erreur: " + message,Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewVehicule.this,"L'image du véhicule a été téléchargée avec succes!",Toast.LENGTH_SHORT).show();

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

                           // Toast.makeText(AdminAddNewVehicule.this,"got the Product image Url to database is successfullly",Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });

    }

    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageURI);
        productMap.put("category", CategoryName);
        productMap.put("price", Price);
        productMap.put("pname", PName);
        productMap.put("type_car", saveSpinner);



        HashMap<String, Object> OptionsMap = new HashMap<>();
        //int compteur = 1;
        for (int i = 0; i < optionChekedList.size(); i++)
        {
            OptionsMap.put("option "+(i+1)+"",optionChekedList.get(i));
            Log.d("option "+(i+1)+"",optionChekedList.get(i));

        }

        productMap.put("options", OptionsMap);
/*      List<String> listOptions = new ArrayList<String>();
        listOptions.add("option 1");
        listOptions.add("option 2");
        listOptions.add("option 3");

        productMap.put("options", new ModelOption(listOptions));
 */

        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Intent intent = new Intent(AdminAddNewVehicule.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewVehicule.this,"Le véhicule a été ajouté avec Succès...",Toast.LENGTH_SHORT).show();

                        }
                        else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewVehicule.this,"Erreur: " + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       // Log.d("Type Sélectionnée",spinner.getSelectedItem().toString());

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void DefineOptionList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminAddNewVehicule.this);


        //string array for alert dialog multicheckbox item
       //String[] optionArray = new String[]{"AutoRadio","Climatisation","Boite Manuelle","Boite Automatique","GPS","Ordinateur de Bord","Caméra de recul","Vitres Electriques"};


        // get list of options in firebase
        DatabaseReference optionRef = FirebaseDatabase.getInstance().getReference().child("Options_vehicule");
        List<String> optionL = new ArrayList<>();
        optionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sn : snapshot.getChildren())
                {
                    optionL.add(sn.child("lib").getValue(String.class));
                }

                //optionList = Arrays.asList(optionArray);
                optionChekedList = new ArrayList<String>();
                builder.setTitle("Selectionne les options");


                final boolean[] checkedoptionArray = new boolean[optionL.size()];


                for (int i = 0; i < checkedoptionArray.length; i++)
                {
                    if (i == 0)
                    {
                        checkedoptionArray[i] = true;
                    }

                    else
                    {
                        checkedoptionArray[i] = false;
                    }
                }

                //set multichoice
                String[] optionArray = null;
                optionArray = (String[]) optionL.toArray(new String[optionL.size()]);
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



       /* final boolean[] checkedoptionArray = new boolean[]{
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                false
        }; */


    }

    }
