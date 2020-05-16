package it.thewalkingthread.talky.Notification;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

import androidx.annotation.NonNull;

public class MyFirebaseIdService extends FirebaseMessagingService {

    FirebaseUser firebaseUser;
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            updateToken(s);
        }

    }

    private void updateToken(String newToken) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(newToken);
        reference.child(firebaseUser.getUid()).setValue(token);
    }
}
