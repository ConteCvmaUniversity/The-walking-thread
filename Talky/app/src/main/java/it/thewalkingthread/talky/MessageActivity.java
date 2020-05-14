package it.thewalkingthread.talky;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import it.thewalkingthread.talky.Adapter.MessageAdapter;
import it.thewalkingthread.talky.Model.Chat;
import it.thewalkingthread.talky.Model.User;

public class MessageActivity extends AppCompatActivity {
    FirebaseUser fuser;
    Intent mIntent;
    DatabaseReference reference;
    String userId;

    MessageAdapter messageAdapter;
    List<Chat> chats;

    RecyclerView rv_message;

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

    private void readMessages(final String myid, final String userId){
        chats = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myid)){
                        chats.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this,chats);
                    rv_message.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    class Holder implements View.OnClickListener,TextView.OnEditorActionListener{
        FloatingActionButton fbtn_back;
        CircleImageView civ_profileImage;
        TextView tv_username;

        FloatingActionButton fbtn_send;
        EditText et_message;




        Holder(){
            fbtn_back = findViewById(R.id.fbtn_back);
            civ_profileImage = findViewById(R.id.civ_profileImage);
            tv_username = findViewById(R.id.tv_username);

            fbtn_send = findViewById(R.id.fbtn_send);
            et_message = findViewById(R.id.et_message);
            rv_message = findViewById(R.id.rv_message);

            rv_message.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            linearLayoutManager.setStackFromEnd(true);
            /*
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_message.getContext(),
                    ((LinearLayoutManager) linearLayoutManager).getOrientation());


            rv_message.addItemDecoration(dividerItemDecoration);

             */

            rv_message.setLayoutManager(linearLayoutManager);


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
                    readMessages(fuser.getUid(),userId);
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
                    hideKeyboard(MessageActivity.this);

                }
                else {
                    fbtn_send.setEnabled(false);
                }
                et_message.setText("");

            }

        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            fbtn_send.setEnabled(true);
            return false;
        }

        void hideKeyboard(Activity activity) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
