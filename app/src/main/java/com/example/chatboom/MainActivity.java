package com.example.chatboom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    LinearLayout main;
    String pa,em;
    EditText email,pass;
    private FirebaseAuth mAuth;
   static boolean chk_rem=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        main=findViewById(R.id.mainlinear);
        mAuth= FirebaseAuth.getInstance();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences m=getSharedPreferences("remember",MODE_PRIVATE);
                String user=m.getString("Username",null);
                String pass=m.getString("id",null);
                if((user!=null)&&(pass!=null))
                {

                    startActivity(new Intent(MainActivity.this,mainboard.class));
                }
                else {
                    email = new EditText(getApplicationContext());
                    decoredittext(email, "Email");
                    email.setSingleLine();
                    email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    main.addView(email);
                    email.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim1));
                    em=email.getText().toString();



                }
            }
        },2000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pass=new EditText(getApplicationContext());
                decoredittext(pass,"password");
                pass.setSingleLine();
                pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                main.addView(pass);
                pass.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim1));
                pa=pass.getText().toString();
            }
        },2500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                View v=getLayoutInflater().inflate(R.layout.loginbtns,main,false);
                LinearLayout login=v.findViewById(R.id.login);
                LinearLayout sign=v.findViewById(R.id.signup);
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        em=email.getText().toString();
                        pa=pass.getText().toString();

                        if(email.getText().toString().isEmpty()||pass.getText().toString().isEmpty()) {
                            if (email.getText().toString().isEmpty()){
                                email.setError("E-mail is empty");

                            }
                            if (pass.getText().toString().isEmpty()){
                                pass.setError("Password is empty");

                            }
                        }
                        else if(!email.getText().toString().isEmpty()||!pass.getText().toString().isEmpty())
                        {
                            ProgressDialog pd=new ProgressDialog(getApplicationContext());
                            pd.setTitle("Access you in ");
                            pd.setMessage("wait a several second to authenticating you");
                            login_method(em,pa);
                            pd.dismiss();
                        }

                    }
                });
                sign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this,Signup.class));
                    }
                });
                main.addView(v);
                v.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim1));
            }
        },3000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView tx=new TextView(getApplicationContext());
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0,20,0,0);
                tx.setLayoutParams(lp);
                tx.setText(R.string.remmy);
                tx.setTextColor(Color.WHITE);
                tx.setGravity(Gravity.CENTER);
                tx.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                main.addView(tx);
                tx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chk_rem=true;
                        Toast.makeText(MainActivity.this, "We Will Remember You :)", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        },3500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView tx=new TextView(getApplicationContext());
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0,20,0,0);
                tx.setLayoutParams(lp);
                tx.setText("©All rights reserved To Mohamed Atef");
                tx.setTextColor(Color.WHITE);
                tx.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
                tx.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
                main.addView(tx);
            }
        },3500);



    }

    public EditText decoredittext(EditText et,String hint)
    {
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int ex= (int) (15*this.getResources().getDisplayMetrics().density);
        int s2= (int) (10*this.getResources().getDisplayMetrics().density);
        lp.setMargins(ex,ex-5,ex,0);
        et.setLayoutParams(lp);
        et.setBackground(getResources().getDrawable(R.drawable.chade));
        et.setTextColor(Color.WHITE);
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        et.setPadding(s2,s2,s2,s2);
        et.setHint(hint);
        et.setHintTextColor(Color.WHITE);
        return et;
    }
    void login_method(final String m, final String p){
        mAuth.signInWithEmailAndPassword(m, p)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(chk_rem)
                            {
                                getSharedPreferences("remember",MODE_PRIVATE)
                                        .edit()
                                        .putString("Username",m)
                                        .putString("id",user.getUid())

                                        .apply();
                            }

                            startActivity(new Intent(MainActivity.this,mainboard.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.e("taggggggggggggggggggg",e.getLocalizedMessage());
            }
        });
    }
}
