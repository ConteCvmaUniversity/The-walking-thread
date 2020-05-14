package it.thewalkingthread.talky;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        new Holder();
    }

    private void logOut(){
        auth.signOut();
        Toast.makeText(SettingsActivity.this,R.string.toast_logOut,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SettingsActivity.this,StartActivity.class);
        this.startActivity(intent);
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
            return false;
        }
    }
}
