package it.thewalkingthread.talky;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import it.thewalkingthread.talky.Model.User;

public class SettingsActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Intent intent;
    String username;
    DatabaseReference reference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        intent = getIntent();
        username = intent.getStringExtra("Username");

        auth = FirebaseAuth.getInstance();
        new Holder();
    }

    private void logOut(){
        auth.signOut();
        Toast.makeText(SettingsActivity.this,R.string.toast_logOut,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SettingsActivity.this,StartActivity.class);
        this.startActivity(intent);
    }

    private void updateUsername(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("username");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.setValue(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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


        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){

                case R.id.fbtn_back:
                    finish();

                case R.id.fbtn_change_img:

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
            }
            return false;
        }

    }

}
