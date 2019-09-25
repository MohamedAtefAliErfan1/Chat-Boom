package com.example.chatboom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class mainboard extends AppCompatActivity implements home.OnFragmentInteractionListener,friends.OnFragmentInteractionListener,requests.OnFragmentInteractionListener{
Button homee,friendse,requestse;
    private static final int RC_SIGN_IN =1 ;
    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference muser=null;
    private FirebaseAuth.AuthStateListener mauthStateListener;
    private String user;
    public static String chateowner="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainboard);


        mfirebaseAuth=FirebaseAuth.getInstance();
        muser= FirebaseDatabase.getInstance().getReference().child("users");
        homee=findViewById(R.id.home);
        friendse=findViewById(R.id.friends);
        requestse=findViewById(R.id.requests);
        Toolbar t=findViewById(R.id.toolbar);
        t.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(t);
        getSupportActionBar().setTitle("Chat Boom");




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.to_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mainboard.this,SearchActivity.class);
                startActivity(intent);
            }
        });
        mauthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                if (firebaseUser==null){
                    startActivity(new Intent(mainboard.this,MainActivity.class));

                }

            }
        };



         FragmentManager fm1 = getSupportFragmentManager();
         FragmentTransaction fragmentTransaction1 = fm1.beginTransaction();
        fragmentTransaction1.add(R.id.container,new home());
        fragmentTransaction1.commit();



        homee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homee.setTextColor(Color.WHITE);
                homee.setBackground(getResources().getDrawable(R.drawable.tabshade));
                friendse.setTextColor(getResources().getColor(R.color.colorPrimary));
                friendse.setBackground(getResources().getDrawable(R.drawable.tabshadewhite));
                requestse.setTextColor(getResources().getColor(R.color.colorPrimary));
                requestse.setBackground(getResources().getDrawable(R.drawable.tabshadewhite));
                 FragmentManager fm = getSupportFragmentManager();
                 FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.container,new home());
                fragmentTransaction.commit();


            }
        });
        friendse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendse.setTextColor(Color.WHITE);
                friendse.setBackground(getResources().getDrawable(R.drawable.tabshade));
                homee.setTextColor(getResources().getColor(R.color.colorPrimary));
                homee.setBackground(getResources().getDrawable(R.drawable.tabshadewhite));
                requestse.setTextColor(getResources().getColor(R.color.colorPrimary));
                requestse.setBackground(getResources().getDrawable(R.drawable.tabshadewhite));
                final FragmentManager fm = getSupportFragmentManager();
                final FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.container,new friends());
                fragmentTransaction.commit();
            }
        });
requestse.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        requestse.setTextColor(Color.WHITE);
        requestse.setBackground(getResources().getDrawable(R.drawable.tabshade));
        homee.setTextColor(getResources().getColor(R.color.colorPrimary));
        homee.setBackground(getResources().getDrawable(R.drawable.tabshadewhite));
        friendse.setTextColor(getResources().getColor(R.color.colorPrimary));
        friendse.setBackground(getResources().getDrawable(R.drawable.tabshadewhite));
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.container,new requests());
        fragmentTransaction.commit();
    }
});

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            AuthUI.getInstance().signOut(getApplicationContext());
            getSharedPreferences("remember",MODE_PRIVATE)
                    .edit()
                    .clear()
                    .commit();

            return true;
        }
        if (id==R.id.acc_setting){
            Intent intent=new Intent(mainboard.this,account.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN){
            if (resultCode==RESULT_OK){
                Toast.makeText(getApplicationContext(),"signed in",Toast.LENGTH_LONG).show();
            }
            else if (resultCode==RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),"fail to sign in",Toast.LENGTH_LONG).show();
                finish();
            }


        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder exit=new AlertDialog.Builder(mainboard.this);
            exit.setTitle("EXIT !");
            exit.setMessage("Are want to close the app");
            exit.setNegativeButton("Close app", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                }
            }).setPositiveButton("Cancel",null);

            exit.show();
            exit.create();
        }
        return super.onKeyDown(keyCode, event);

    }
    @Override
    protected void onStart() {
        super.onStart();
        user=mfirebaseAuth.getCurrentUser().getUid();
        chateowner=mfirebaseAuth.getCurrentUser().getUid();
        String id=muser.child(user).getKey();

        if (id==null){
            HashMap<String,String> userdetails=new HashMap<>();
            userdetails.put("name",mfirebaseAuth.getCurrentUser().getDisplayName());
            userdetails.put("image","no image");
            userdetails.put("status","Hey ther, I'm using KIT application");
            userdetails.put("online", String.valueOf(ServerValue.TIMESTAMP));
            muser.child(user).setValue(userdetails);
            AlertDialog.Builder b=new AlertDialog.Builder(this);
            b.setTitle("initiate your profile");
            b.setMessage("need to set your profile statue and photo")
                    .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(mainboard.this, "will initiate your profile to default", Toast.LENGTH_SHORT).show();
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(mainboard.this,account.class));
                }
            }).show();
        }
        else {

            muser.child(user).child("online").setValue("online");
        }
    } @Override
    protected void onStop() {
        super.onStop();
        muser.child(user).child("online").setValue(ServerValue.TIMESTAMP);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        muser.child(user).child("online").setValue(ServerValue.TIMESTAMP);

    }
}
