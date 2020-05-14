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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import it.thewalkingthread.talky.Adapter.UserAdapter;
import it.thewalkingthread.talky.Model.Chat;
import it.thewalkingthread.talky.Model.User;

public class MainActivity extends AppCompatActivity {

    CircleImageView civ_profileImage;
    TextView tv_username;
    ImageButton imgbtn_settings;
    FloatingActionButton fbtn_newchat;
    RecyclerView rc_chats;

    FirebaseUser firebaseUser;
    DatabaseReference reference;


    List<String> userList;
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
            //TODO Activity for settings -> TAAAAC

            if(v.getId() == R.id.fbtn_newchat){
                Intent intent = new Intent(MainActivity.this,UserListActivity.class);
                startActivity(intent);
            }

            if(v.getId() == R.id.imgbtn_settings){
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
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


            userList = new ArrayList<>();

            reference = FirebaseDatabase.getInstance().getReference("Chats");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Chat chat = snapshot.getValue(Chat.class);
                        assert chat != null;
                        if(chat.getSender().equals(firebaseUser.getUid())){
                            userList.add(chat.getReceiver());
                        }
                        if(chat.getReceiver().equals(firebaseUser.getUid())){
                            userList.add(chat.getSender());
                        }
                    }

                    readChats();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        private void readChats(){
            mUsers = new ArrayList<>();
            reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUsers.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        for(String id : userList){
                            if(user.getId().equals(id)){
                                if(mUsers.size() != 0){
                                    for(User userl : mUsers){
                                        if(!user.getId().equals(userl.getId())){
                                            mUsers.add(user);
                                        }
                                    }
                                } else{
                                    mUsers.add(user);
                                }
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
    }

}
