package com.example.chatboom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Signup extends AppCompatActivity {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText pass,email,name;
    static String namestr;
     ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    pd  =new ProgressDialog(this);
        pass=findViewById(R.id.signpass);
        email=findViewById(R.id.signemail);
        name=findViewById(R.id.yourname);
        Button sign=findViewById(R.id.signupbtn);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().isEmpty()||pass.getText().toString().isEmpty()||name.getText().toString().isEmpty())
                {
                    if(email.getText().toString().isEmpty())
                        email.setError("field required");
                    if(pass.getText().toString().isEmpty())
                        pass.setError("field required");
                    if(name.getText().toString().isEmpty())
                        name.setError("field required");
                }
                else
                {
                    pd.setTitle("Wait...");
                    pd.setMessage("Access you in ");
                    String em=email.getText().toString().trim();
                    String pa=pass.getText().toString().trim();
                    namestr=name.getText().toString().trim();
                   mAuth.createUserWithEmailAndPassword(em,pa)
                           .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {
                                   if (task.isSuccessful()) {
                                       // Sign in success, update UI with the signed-in user's information
                                       Log.d(TAG, "createUserWithEmail:success");
                                       FirebaseUser user = mAuth.getCurrentUser();

                                       UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                               .setDisplayName(name.getText().toString()).build();

                                       user.updateProfile(profileUpdates);

                                       Toast.makeText(getApplicationContext(), "success.",
                                               Toast.LENGTH_SHORT).show();
                                       pd.dismiss();
                                       startActivity(new Intent(Signup.this,
                                               mainboard.class));

                                   } else {
                                       // If sign in fails, display a message to the user.
                                       Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                       Toast.makeText(getApplicationContext(), "Authentication failed.",
                                               Toast.LENGTH_SHORT).show();

                                   }
                               }
                           });

                }
            }
        });
        //conig google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LinearLayout signgoogle=findViewById(R.id.google_button);
        final LinearLayout signface=findViewById(R.id.facebook_button);
        signface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(signface,"not yet :)",Snackbar.LENGTH_LONG).show();
            }
        });
        signgoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setTitle("Wait...");
                pd.setMessage("Access you in ");
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);





            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "sign in failed", Toast.LENGTH_SHORT).show();
                
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        Toast.makeText(this, "Authenticating", Toast.LENGTH_SHORT).show();
        // [START_EXCLUDE silent]
       // showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(Signup.this, "success", Toast.LENGTH_SHORT).show();
                            final FirebaseUser user = mAuth.getCurrentUser();
                            final String user_id=user.getUid();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("users");
                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.

                                    if (dataSnapshot.child(user_id).exists()) {
                                        FirebaseDatabase userdatabase = FirebaseDatabase.getInstance();
                                        DatabaseReference userref = userdatabase.getReference("users");
//                                userref.child(user_id).child("name").setValue(user.getDisplayName());
//                                userref.child(user_id).child("image").setValue("no image");
//                                userref.child(user_id).child("status").setValue("Hey ther, I'm using KIT application");
                                        HashMap<String,String> userdetails=new HashMap<>();
                                        userdetails.put("name",user.getDisplayName());
                                        userdetails.put("image","no image");
                                        userdetails.put("status","Hey ther, I'm using KIT application");
                                        userref.child(user_id).setValue(userdetails);
                                            getSharedPreferences("remember",MODE_PRIVATE)
                                                    .edit()
                                                    .putString("Username",user.getDisplayName())
                                                    .putString("id",user.getUid())
                                                    .apply();

                                    }
                                    else {
                                        if(MainActivity.chk_rem)
                                        {
                                            getSharedPreferences("remember",MODE_PRIVATE)
                                                    .edit()
                                                    .putString("Username",user.getDisplayName())
                                                    .putString("id",user.getUid())
                                                    .apply();

                                        }

                                    }
                                }



                                @Override
                                public void onCancelled(DatabaseError error) {
                                    Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                                }
                            });
                            pd.dismiss();
                            Intent i=new Intent(Signup.this,mainboard.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                          //  Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(Signup.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();

                        }

                        // [START_EXCLUDE]
                     //   hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
}
