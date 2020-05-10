package it.thewalkingthread.talky;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class EmailSignInActivity extends AppCompatActivity {


    FirebaseAuth auth;
    DatabaseReference reference;
    private static final String TAG = "Auth error";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_in);

        auth = FirebaseAuth.getInstance();

        new Holder();



    }

    class Holder implements View.OnClickListener{
        EditText fieldEmail,fieldPassword;
        Button emailSignInButton,emailCreateAccountButton;

        Holder(){
            fieldEmail = findViewById(R.id.fieldEmail);
            fieldPassword = findViewById(R.id.fieldPassword);
            emailSignInButton = findViewById(R.id.emailSignInButton);
            emailCreateAccountButton = findViewById(R.id.emailCreateAccountButton);

            emailCreateAccountButton.setOnClickListener(this);
            emailSignInButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            String txt_email = fieldEmail.getText().toString();
            String txt_password = fieldPassword.getText().toString();

            if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                Toast.makeText(EmailSignInActivity.this,R.string.toast_empty_fields,Toast.LENGTH_SHORT).show();
            }
            else if (txt_password.length()<6){
                Toast.makeText(EmailSignInActivity.this,R.string.toast_pass_limit,Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(EmailSignInActivity.this,R.string.toast_wait,Toast.LENGTH_SHORT).show();
                if(v.getId() == R.id.emailCreateAccountButton) {
                    register(txt_email, txt_password);
                }
                else if (v.getId() == R.id.emailSignInButton){
                    signIn(txt_email,txt_password);
                }
            }
        }

        private void register(final String email, String password){

            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                String userID = firebaseUser.getUid();
                                reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                                HashMap<String,String> hashMap = new HashMap<>();
                                hashMap.put("id",userID);
                                hashMap.put("username",email);
                                hashMap.put("imageURL","default");

                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()){
                                           //Start Main
                                           /*
                                           * Intent intent = new Intent(EmailSignInActivity.this,MainActivity.class);
                                           * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                                           * startActivity(intent);
                                           * finish();
                                           * */
                                           Toast.makeText(EmailSignInActivity.this,R.string.toast_welcome,Toast.LENGTH_SHORT).show();


                                       }

                                    }
                                });
                            }
                            else{
                                Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                                Toast.makeText(EmailSignInActivity.this,R.string.toast_registration_failed,Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }

        private void signIn(String email, String password){
            auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                //Start Main
                                /*
                                 * Intent intent = new Intent(EmailSignInActivity.this,MainActivity.class);
                                 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                                 * startActivity(intent);
                                 * finish();
                                 * */
                                Toast.makeText(EmailSignInActivity.this,"Daje",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                                Toast.makeText(EmailSignInActivity.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }
}
