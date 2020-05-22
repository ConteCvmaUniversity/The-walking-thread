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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import it.thewalkingthread.talky.Adapter.UserAdapter;
import it.thewalkingthread.talky.Model.Chatlist;
import it.thewalkingthread.talky.Model.User;
import it.thewalkingthread.talky.Notification.Token;

public class MainActivity extends AppCompatActivity {

    CircleImageView civ_profileImage;
    TextView tv_username;
    ImageButton imgbtn_settings;
    FloatingActionButton fbtn_newchat;
    RecyclerView rc_chats;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    List<Chatlist> userList;
    List<User> mUsers;

    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Holder();

    }



    class Holder implements View.OnClickListener {

        Holder(){
            tv_username = findViewById(R.id.tv_username);
            civ_profileImage = findViewById(R.id.civ_profileImage);
            imgbtn_settings = findViewById(R.id.imgbtn_settings);
            fbtn_newchat = findViewById(R.id.fbtn_newchat);
            rc_chats = findViewById(R.id.rc_chats);

            fbtn_newchat.setOnClickListener(this);
            imgbtn_settings.setOnClickListener(this);

            userLink();
            readPreviousChats();

        }

        @Override
        public void onClick(View v) {

            if(v.getId() == R.id.fbtn_newchat){
                Intent intent = new Intent(MainActivity.this,UserListActivity.class);
                startActivity(intent);
            }

            if(v.getId() == R.id.imgbtn_settings){
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                intent.putExtra("Username", tv_username.getText().toString());
                startActivity(intent);
            }
        }

        private void userLink(){
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
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

        private void readPreviousChats(){
            //Managing Recycler view
            rc_chats.setHasFixedSize(true);


            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            rc_chats.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rc_chats.getContext(),
                    ((LinearLayoutManager) layoutManager).getOrientation());


            rc_chats.addItemDecoration(dividerItemDecoration);


            userList = new ArrayList<>();
            reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Chatlist chatlist = snapshot.getValue(Chatlist.class);
                        userList.add(chatlist);
                    }

                    chatList();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            updateToken(FirebaseInstanceId.getInstance().getToken());
        }

        private void chatList(){
            mUsers = new ArrayList<>();
            reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUsers.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        for(Chatlist chatlist : userList){
                            if(user.getId().equals(chatlist.getId())){
                                mUsers.add(user);
                            }
                        }
                    }
                    userAdapter = new UserAdapter(mUsers);
                    rc_chats.setAdapter(userAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        private void updateToken(String token){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
            Token tokenl = new Token(token);
            reference.child(firebaseUser.getUid()).setValue(tokenl);

        }
    }

}
