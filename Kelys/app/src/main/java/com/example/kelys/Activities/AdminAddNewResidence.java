package com.example.kelys.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AdminAddNewResidence extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button addnewproduct;
    private EditText inputproductname, inputproductdescription, inputproducttprice;
    private ImageView inputproductimage;
    private String CategoryName, Description, Price, PName, saveCurrentDate, saveCurrentTime,saveSpinner;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private String productRandomKey, downloadImageURI;
    private StorageReference ProductImageRef;
    private DatabaseReference ProductsRef,categ_resid_ref;
    private Spinner spinner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_residence);

        CategoryName = getIntent().getExtras().get("Categorie").toString();

        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Images des Residences");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Residence");




        spinner = (Spinner) findViewById(R.id.spinner_resid);
        spinner.setOnItemSelectedListener(this);
        // get list of hotels
        categ_resid_ref = FirebaseDatabase.getInstance().getReference().child("Categorie_residence");

        categ_resid_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final List<String> CategResidList = new ArrayList<String>();
                for (DataSnapshot sn : snapshot.getChildren())
                {
                    CategResidList.add(sn.child("lib_categ_resid").getValue(String.class));
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





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        inputproductdescription = (EditText) findViewById(R.id.product_resid_description);
        inputproductname = (EditText) findViewById(R.id.product_resid_name);
        inputproducttprice = (EditText) findViewById(R.id.product_resid_price);
        addnewproduct = (Button) findViewById(R.id.add_new_resid);
        inputproductimage = (ImageView) findViewById(R.id.select_resid_img);
        loadingBar = new ProgressDialog(this);

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
            Toast.makeText(this,"Veuillez ecrire la description de la résidence...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price)){
            Toast.makeText(this,"Veuillez entrer le prix de la résidence...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(PName)){
            Toast.makeText(this,"Veuillez entrer le nom de la résidence...",Toast.LENGTH_SHORT).show();
        }
        else if (saveSpinner == null)
        {
            Toast.makeText(this,"Veuillez choisir une catégorie...",Toast.LENGTH_SHORT).show();
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

        productRandomKey = saveCurrentDate +"-"+ saveCurrentTime;

        final StorageReference filePath = ProductImageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewResidence.this,"Erreur: " + message,Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewResidence.this,"L'image de la résidence a été téléchargée avec succes!",Toast.LENGTH_SHORT).show();

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

                           // Toast.makeText(AdminAddNewResidence.this,"got the Product image Url to database is successfullly",Toast.LENGTH_SHORT).show();

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
        productMap.put("type_resid", saveSpinner);


        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Intent intent = new Intent(AdminAddNewResidence.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewResidence.this,"Le Produit a été ajouté avec Succès...",Toast.LENGTH_SHORT).show();

                        }
                        else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewResidence.this,"Erreur: " + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Log.d("Type Sélectionné",spinner.getSelectedItem().toString());

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}