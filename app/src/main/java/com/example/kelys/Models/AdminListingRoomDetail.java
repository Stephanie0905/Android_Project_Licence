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

public class AdminListingRoomDetail extends AppCompatActivity {
    TextView btn_close;

    EditText textNom, textPrice, textDesc;
    Button updateroom;

    ImageView imageView;
    private Query RoomQuery;
    private String Description, Price, PName,saveSpinner,saveSpinner1;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private String productRandomKey, downloadImageURI, detailRoom, hotelName;
    private int detailRoomId, hotelNameId;
    Spinner spinner,spinner1;
    private DatabaseReference ProductsRef,hotel_ref,room_ref;
    private StorageReference ProductImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_listing_room_detail);

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

        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Images des Chambres");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Chambre");
        loadingBar = new ProgressDialog(this);

        textNom = (EditText) findViewById(R.id.txt2_room);
        textPrice = (EditText) findViewById(R.id.txt4_room);
        textDesc = (EditText) findViewById(R.id.txt3_room);
        spinner = (Spinner) findViewById(R.id.spinner_detail_room1);
        spinner1 = (Spinner) findViewById(R.id.spinner_detail_room2);
        imageView = (ImageView) findViewById(R.id.service_img_room);
        updateroom = (Button) findViewById(R.id.update_service_room);

        updateroom.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        ValidateProductData();
    }
});

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        String uid = getIntent().getStringExtra("uid");
        RoomQuery = FirebaseDatabase.getInstance().getReference().child("Chambre").orderByChild("pid").equalTo(uid);
        RoomQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    RoomAdapter roomAdapter = d.getValue(RoomAdapter.class);
                    textNom.setText(roomAdapter.getPname());
                    textPrice.setText(roomAdapter.getPrice());
                    textDesc.setText(roomAdapter.getDescription());

                    Picasso.get().load(roomAdapter.getImage()).into(imageView);
                    downloadImageURI = roomAdapter.getImage();

                    productRandomKey = roomAdapter.getPid();
                    detailRoom = roomAdapter.getDetail_room();
                    hotelName = roomAdapter.getHotelName();
                    //final int room = Integer.parseInt(String.valueOf(roomAdapter.getDetail_room().charAt(0)));

                    // get list of room
                    room_ref = FirebaseDatabase.getInstance().getReference().child("Categorie_chambre");

                    room_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final List<String> CategRoomList = new ArrayList<String>();
                            int compteur = 0;
                            for (DataSnapshot sn : snapshot.getChildren()) {
                                CategRoomList.add(sn.child("lib_categ_ch").getValue(String.class));

                                if (sn.child("lib_categ_ch").getValue(String.class).equals(detailRoom))
                                {
                                    detailRoomId = compteur;
                                }

                                compteur++;
                            }
                            ArrayAdapter<String> categroomAdapter = new ArrayAdapter<String>(getBaseContext(),
                                    android.R.layout.simple_list_item_1,
                                    CategRoomList) {
                                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getDropDownView(position, convertView, parent);
                                    TextView tv = (TextView) view;
                                    tv.setTextColor(Color.BLACK);
                                    return view;
                                }

                            };
                            spinner.setAdapter(categroomAdapter);
                            spinner.setSelection(detailRoomId);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //final int hotel = Integer.parseInt(String.valueOf(roomAdapter.getDetail_room().charAt(0)));

                    // get list of hotels
                    hotel_ref = FirebaseDatabase.getInstance().getReference().child("Hotel");

                    hotel_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final List<String> HotelList = new ArrayList<String>();
                            int compteur = 0;
                            for (DataSnapshot sn : snapshot.getChildren()) {
                                HotelList.add(sn.child("pname").getValue(String.class));
                                if (sn.child("pname").getValue(String.class).equals(hotelName))
                                {
                                    hotelNameId = compteur;
                                }

                                compteur++;
                            }

                            ArrayAdapter<String> hotelAdapter = new ArrayAdapter<String>(getBaseContext(),
                                    android.R.layout.simple_list_item_1,
                                    HotelList) {
                                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getDropDownView(position, convertView, parent);
                                    TextView tv = (TextView) view;
                                    tv.setTextColor(Color.BLACK);
                                    return view;
                                }
                            };
                            //hotelAdapter.notifyDataSetChanged();
                            spinner1.setAdapter(hotelAdapter);
                            spinner1.setSelection(hotelNameId);

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

    private void openGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    private void ValidateProductData() {
        Description = textDesc.getText().toString();
        Price = textPrice.getText().toString();
        PName = textNom.getText().toString();
        saveSpinner = spinner.getSelectedItem().toString();
        saveSpinner1 = spinner1.getSelectedItem().toString();

        UpdateProductInformation();

    }

    private void UpdateProductInformation() {
        loadingBar.setTitle("Modification des Chambres");
        loadingBar.setMessage("Cher Admin, Patientez SVP, nous sommes en train de modifier la Chambre");
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
                    Toast.makeText(AdminListingRoomDetail.this,"Erreur: " + message,Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AdminListingRoomDetail.this,"Enregistrement En Cours...",Toast.LENGTH_SHORT).show();

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

                                Toast.makeText(AdminListingRoomDetail.this,"Image enregistrée dans la base de données",Toast.LENGTH_SHORT).show();

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

        Intent intent = new Intent(AdminListingRoomDetail.this, ListingRoom.class);
        startActivity(intent);

        loadingBar.dismiss();
        Toast.makeText(AdminListingRoomDetail.this,"Le Produit a été modifié avec Succès...",Toast.LENGTH_SHORT).show();

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