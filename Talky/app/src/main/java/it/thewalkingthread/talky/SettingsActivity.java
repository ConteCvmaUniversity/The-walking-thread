package it.thewalkingthread.talky;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import it.thewalkingthread.talky.Model.User;

public class SettingsActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 100;
    FirebaseAuth auth;
    Intent intent;
    String username;
    DatabaseReference reference;
    FirebaseUser firebaseUser;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        intent = getIntent();
        username = intent.getStringExtra("Username");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        //imageURL = "default";
        new Holder();
    }

    private void logOut(){
        auth.signOut();
        Toast.makeText(SettingsActivity.this,R.string.toast_logOut,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SettingsActivity.this,StartActivity.class);
        this.startActivity(intent);
    }

    private void updateUsername(){
        final DatabaseReference refUsername = reference.child("username");
        refUsername.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                refUsername.setValue(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void askPermissions(){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        List<String> listPermissionsNeeded = new ArrayList<>();
        for(String s: permissions){
            if(ContextCompat.checkSelfPermission(SettingsActivity.this,s) != PackageManager.PERMISSION_GRANTED)
                listPermissionsNeeded.add(s);
        }
        if(!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(SettingsActivity.this,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MY_PERMISSION_REQUEST);
            return;
        }
        updateImage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case MY_PERMISSION_REQUEST:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    updateImage();
                } else {
                    Toast.makeText(SettingsActivity.this, R.string.toast_permission_denied,Toast.LENGTH_SHORT).show();
                }
                break;

            }
        }
    }
    private void updateImage(){
            storageReference = FirebaseStorage.getInstance().getReference("uploads");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, IMAGE_REQUEST);

    }


    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = SettingsActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(SettingsActivity.this);
        pd.setMessage(getResources().getString(R.string.progress_dialog));

        pd.show();

        if (imageUri != null){
            if(!imageURL.equals("default"))
                deletePreviousImage();

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() +"."+getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference.child("imageURL").setValue(mUri);

                        pd.dismiss();
                    }
                    else{
                        Toast.makeText(SettingsActivity.this,R.string.toast_fail,Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SettingsActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
        else{
            Toast.makeText(SettingsActivity.this,R.string.toast_no_image_selected, Toast.LENGTH_SHORT).show();
        }
    }
    private void deletePreviousImage(){
        String prova = imageURL;
        String jpeg = imageURL.substring(imageURL.indexOf("%2F")+ "%2F".length(), imageURL.indexOf("?"));
        storageReference.child(jpeg).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(SettingsActivity.this,R.string.toast_upload_in_progress,Toast.LENGTH_SHORT).show();
            }
            else{
                uploadImage();
            }
        }
    }

    class Holder implements View.OnClickListener, TextView.OnEditorActionListener {

        CircleImageView civ_profile;
        FloatingActionButton fbtn_change_img;
        FloatingActionButton fbtn_back;
        Button btn_logOut;
        CardView cv_username;
        EditText et_username;

        Holder(){

            civ_profile = findViewById(R.id.civ_profile);
            fbtn_change_img = findViewById(R.id.fbtn_change_img);
            btn_logOut = findViewById(R.id.btn_logOut);
            cv_username = findViewById(R.id.cv_username);
            et_username = findViewById(R.id.et_username);
            fbtn_back = findViewById(R.id.fbtn_back);

            cv_username.setOnClickListener(this);
            btn_logOut.setOnClickListener(this);
            fbtn_change_img.setOnClickListener(this);
            fbtn_back.setOnClickListener(this);

            et_username.setOnEditorActionListener(this);
            et_username.setEnabled(false);
            et_username.setText(username);

            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    imageURL = user.getImageURL();
                    if(imageURL.equals("default")){
                        civ_profile.setImageResource(R.drawable.ic_account);
                    }
                    else{
                        Glide.with(SettingsActivity.this).load(imageURL).into(civ_profile);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.fbtn_back:
                    finish();
                    break;

                case R.id.fbtn_change_img:
                    askPermissions();
                    break;

                case R.id.btn_logOut:
                    logOut();
                    break;

                case R.id.cv_username:
                    et_username.setEnabled(true);
                    break;
            }
        }


        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                username = et_username.getText().toString();
                updateUsername();
                et_username.setEnabled(false);
            }
            return false;
        }

    }

}
