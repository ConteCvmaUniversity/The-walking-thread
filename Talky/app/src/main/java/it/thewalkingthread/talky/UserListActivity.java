package it.thewalkingthread.talky;

import android.os.Bundle;

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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.thewalkingthread.talky.Adapter.UserAdapter;
import it.thewalkingthread.talky.Model.User;

public class UserListActivity extends AppCompatActivity {
    private UserAdapter userAdapter;
    private List<User> users;
    RecyclerView rv_users_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        new Holder();
    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getId().equals(firebaseUser.getUid())){
                         users.add(user);
                    }
                }
                userAdapter = new UserAdapter(users);
                rv_users_list.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class Holder{


        Holder(){
            rv_users_list = findViewById(R.id.rv_users_list);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(UserListActivity.this);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_users_list.getContext(),
                    ((LinearLayoutManager) layoutManager).getOrientation());


            rv_users_list.addItemDecoration(dividerItemDecoration);




            users = new ArrayList<>();
            readUsers();
            rv_users_list.setLayoutManager(layoutManager);
        }


    }
}
