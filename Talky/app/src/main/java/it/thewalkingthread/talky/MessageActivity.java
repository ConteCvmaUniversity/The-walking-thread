package it.thewalkingthread.talky;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
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

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import it.thewalkingthread.talky.Model.User;

public class MessageActivity extends AppCompatActivity {
    FirebaseUser fuser;
    Intent mIntent;
    DatabaseReference reference;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage);

        mIntent = getIntent();
        userId = mIntent.getStringExtra("userID");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        new Holder();
    }

    private void sendMessage(String sender,String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        reference.child("Chats").push().setValue(hashMap);
    }

    class Holder implements View.OnClickListener,TextView.OnEditorActionListener{
        FloatingActionButton fbtn_back;
        CircleImageView civ_profileImage;
        TextView tv_username;

        FloatingActionButton fbtn_send;
        EditText et_message;
        RecyclerView rv_message;



        Holder(){
            fbtn_back = findViewById(R.id.fbtn_back);
            civ_profileImage = findViewById(R.id.civ_profileImage);
            tv_username = findViewById(R.id.tv_username);

            fbtn_send = findViewById(R.id.fbtn_send);
            et_message = findViewById(R.id.et_message);
            rv_message = findViewById(R.id.rv_message);


            fbtn_back.setOnClickListener(this);
            fbtn_send.setOnClickListener(this);
            et_message.setOnEditorActionListener(this);



            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    tv_username.setText(user.getUsername());
                    if(user.getImageURL().equals("default")){
                        civ_profileImage.setImageResource(R.drawable.ic_account);
                    }
                    else {
                        Glide.with(MessageActivity.this).load(user.getImageURL()).into(civ_profileImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.fbtn_back){
                startActivity(new Intent(MessageActivity.this,MainActivity.class));
                finish();
            }
            if (v.getId() == R.id.fbtn_send){
                String msg = et_message.getText().toString();
                if (!msg.equals("")){
                    sendMessage(fuser.getUid(),userId,msg);
                }
                else {
                    fbtn_send.setEnabled(false);
                }
                et_message.setText("");

            }

        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            return false;
        }


    }
}
