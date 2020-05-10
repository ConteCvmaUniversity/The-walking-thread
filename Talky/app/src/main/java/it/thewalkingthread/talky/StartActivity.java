package it.thewalkingthread.talky;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Holder();
    }

    class Holder implements View.OnClickListener {

        CardView cv_google;
        CardView cv_email;
        CardView cv_facebook;

        Holder(){
            cv_google = findViewById(R.id.cv_google);
            cv_email = findViewById(R.id.cv_email);
            cv_facebook = findViewById(R.id.cv_facebook);
            cv_google.setOnClickListener(this);
            cv_facebook.setOnClickListener(this);
            cv_email.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId()==R.id.cv_google){
                startActivity(new Intent(StartActivity.this,GoogleSignInActivity.class));
            }

            else if (v.getId()==R.id.cv_facebook){
                startActivity(new Intent(StartActivity.this,GoogleSignInActivity.class));
            }
            else{
                startActivity(new Intent(StartActivity.this,EmailSignInActivity.class));
            }
        }
    }
}
