package com.geekym.face_recognition_engage.HomeFragments.Tools.PDFs;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.geekym.face_recognition_engage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class Add_PDF_Activity extends AppCompatActivity {

    ImageView file_icon, cancel;
    Button browse, upload;
    Uri filepath;
    EditText file_title;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pdfs);

        Initialization();  //Function to initialize the variables

        Intent intent = getIntent();
        String CollegeName = intent.getStringExtra("CollegeName");

        //Cancel button (x)
        cancel.setOnClickListener(view -> {
            file_icon.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.INVISIBLE);
            browse.setVisibility(View.VISIBLE);
        });

        //Browse PDF from the file manager
        browse.setOnClickListener(view -> Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent();
                        intent.setType("application/pdf");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Pdf Files"), 101);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check());

        upload.setOnClickListener(view -> process_upload(filepath, CollegeName));

    }

    //Function to initialize the variables
    private void Initialization() {
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        file_title = findViewById(R.id.filetitle);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        userID = user.getUid();
        browse = findViewById(R.id.imagebrowse);
        upload = findViewById(R.id.imageupload);
        file_icon = findViewById(R.id.filelogo);
        cancel = findViewById(R.id.cancelfile);
        file_icon.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            filepath = data.getData();
            file_icon.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
            browse.setVisibility(View.INVISIBLE);
        }
    }


    public void process_upload(Uri filepath, String CollegeName) {
        //When the user Clicks on Upload Button

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading PDF");
        pd.show();

        final StorageReference reference = storageReference.child("uploads/" + System.currentTimeMillis() + ".pdf");
        reference.putFile(filepath)
                .addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {

                    PDFsModel obj = new PDFsModel(file_title.getText().toString(), uri.toString());
                    databaseReference.child("PDFs").child(CollegeName).child(databaseReference.push().getKey()).setValue(obj);

                    pd.dismiss();
                    DynamicToast.makeSuccess(getApplicationContext(),"File Uploaded").show();

                    file_icon.setVisibility(View.INVISIBLE);
                    cancel.setVisibility(View.INVISIBLE);
                    browse.setVisibility(View.VISIBLE);
                    file_title.setText("");
                }))

                .addOnProgressListener(taskSnapshot -> {
                    float percent = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    pd.setMessage("Uploaded :" + (int) percent + "%");
                });

    }
}