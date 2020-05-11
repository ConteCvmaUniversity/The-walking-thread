package it.thewalkingthread.talky;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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
import de.hdodenhof.circleimageview.CircleImageView;
import it.thewalkingthread.talky.Model.User;

public class MainActivity extends AppCompatActivity {

    CircleImageView civ_profileImage;
    TextView tv_username;
    ImageButton imgbtn_settings;
    FloatingActionButton fbtn_newchat;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Holder();
        userLink();
    }

    private void userLink(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tv_username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    civ_profileImage.setImageResource(R.drawable.ic_account);
                } else{
                    Glide.with(MainActivity.this).load(user.getImageURL()).into(civ_profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class Holder implements View.OnClickListener {

        Holder(){
            tv_username = findViewById(R.id.tv_username);
            civ_profileImage = findViewById(R.id.civ_profileImage);
            imgbtn_settings = findViewById(R.id.imgbtn_settings);
            fbtn_newchat = findViewById(R.id.fbtn_newchat);

            fbtn_newchat.setOnClickListener(this);
            imgbtn_settings.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //TODO Activity for settings -> TAAAAC

            if(v.getId() == R.id.fbtn_newchat){
                Intent intent = new Intent(MainActivity.this,UserListActivity.class);
                startActivity(intent);
            }
        }
    }

}
