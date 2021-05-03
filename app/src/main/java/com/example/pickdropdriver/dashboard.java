package com.example.pickdropdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class dashboard extends AppCompatActivity {
    Button sigout;
    ImageView prof;
    TextView NAME, nic;
    FirebaseAuth mauth;
    FirebaseUser user;
    String uid;
    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("appoved trips");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       current_user_db.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               notification();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

        setContentView(R.layout.activity_dashboard);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        mauth = FirebaseAuth.getInstance();
        sigout = findViewById(R.id.logout);
        NAME = findViewById(R.id.username);
        nic = findViewById(R.id.nic);
        prof = findViewById(R.id.prof);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent log = new Intent(dashboard.this, profile.class);
                startActivity(log);
            }

        });
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String data = dataSnapshot.child("Service Provider").child("Registered Drivers").child(uid).child("Name").getValue(String.class);
                String data1 = dataSnapshot.child("Service Provider").child("Registered Drivers").child(uid).child("NIC NO").getValue(String.class);
                NAME.setText(data);
                nic.setText(data1);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });


        sigout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mauth.signOut();
                soutser();
            }

        });

    }

    private void notification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(dashboard.this,"n")
                .setContentText("Pick & Drop")
                .setSmallIcon(R.drawable.logonew)
                .setAutoCancel(true)
                .setContentText("New Driver Registered!");
        NotificationManagerCompat managerCompat =   NotificationManagerCompat.from(dashboard.this);
        managerCompat.notify(999,builder.build());

    }

    private void soutser() {
        Intent log = new Intent(dashboard.this, MainActivity.class);
        startActivity(log);
        finish();
    }


    public void toreq(View view) {
        Intent log = new Intent(dashboard.this, newtrip.class);
        startActivity(log);
    }
}
