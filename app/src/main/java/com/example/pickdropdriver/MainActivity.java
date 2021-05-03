package com.example.pickdropdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    EditText email,pass;
    ImageView log;
    FirebaseAuth fauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        log = findViewById(R.id.login);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);

        fauth = FirebaseAuth.getInstance();


        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String m = email.getText().toString().trim();
                String p = pass.getText().toString().trim();

                if (TextUtils.isEmpty(m)){
                    email.setError("Email Required");
                    return;
                }

                if (TextUtils.isEmpty(p)){
                    pass.setError("Password Required");
                    return;
                }

                if (p.length()<6){
                    pass.setError("Password must be 6 char above");
                    return;
                }
                fauth.signInWithEmailAndPassword(m,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            Toast.makeText(MainActivity.this,"Logged in Successfully" ,Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),dashboard.class));
                        }else{

                            Toast.makeText(MainActivity.this,"Error !!" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();





                        }


                    }
                });


            }
        });



    }
}