package com.example.pickdropdriver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class profile extends AppCompatActivity {
    TextView nic,veri, profemail, profname, profaddres, profphone,passreset;

    FirebaseUser user;
    String uid;
    FirebaseAuth mAuth;
    private static final int IMAGE_REQEST = 2;
    private Uri imageuri;
    String TAG;
    ImageView profilemg, upload, largeprof;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        upload = (ImageView) findViewById(R.id.upload);
        largeprof = (ImageView) findViewById(R.id.photo);
        profilemg = (ImageView) findViewById(R.id.photo2);
        profemail = (TextView) findViewById(R.id.email);
        nic = (TextView) findViewById(R.id.nic);
        profaddres = (TextView) findViewById(R.id.address);
        profphone = (TextView) findViewById(R.id.phone);
        profname = (TextView) findViewById(R.id.username);
        veri = (TextView) findViewById(R.id.ver);
        passreset = (TextView) findViewById(R.id.pr);



        //-----------------password reset section
        passreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetmail =  new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog  =  new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ..");
                passwordResetDialog.setMessage("Enter Your Registered Email to Send the Reset Link .. ");
                passwordResetDialog.setView(resetmail);


                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetmail.getText().toString();
                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(profile.this,"Reset Mail Sent ",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(profile.this,"Reset Mail Not Sent " + e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.create().show();
            }
        });
        //------------ end of password reset section


        FirebaseUser user = mAuth.getCurrentUser();
        if (user.isEmailVerified()){
            veri.setText("");
        }else{
            veri.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(profile.this,"Email Verification Sent ",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }



        DatabaseReference databaseRef = getInstance().getReference();
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String data = dataSnapshot.child("Service Provider").child("Registered Drivers").child(uid).child("Email").getValue(String.class);
                String data2 = dataSnapshot.child("Service Provider").child("Registered Drivers").child(uid).child("NIC").getValue(String.class);
                String data3 = dataSnapshot.child("Service Provider").child("Registered Drivers").child(uid).child("Name").getValue(String.class);
                String data4 = dataSnapshot.child("Service Provider").child("Registered Drivers").child(uid).child("Phone").getValue(String.class);
                String data5 = dataSnapshot.child("Service Provider").child("Registered Drivers").child(uid).child("Address").getValue(String.class);
                profemail.setText(data);
                nic.setText(data2);
                profname.setText(data3);
                profphone.setText(data4);
                profaddres.setText(data5);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        DatabaseReference f = databaseRef.child("Service_Provider").child("Registered Drivers").child("UserImages");

        f.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String link = dataSnapshot.getValue(String.class);
                Picasso.get().load(link).into(profilemg);
                Picasso.get().load(link).into(largeprof);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQEST && resultCode == RESULT_OK) {
            imageuri = data.getData();

            uploadImage();
        }


    }

    private String getFileExtention(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }


    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageuri != null) {
            StorageReference fileref = FirebaseStorage.getInstance().getReference().child("Service_Provider").child("Registered Drivers").child("UserImages").child(System.currentTimeMillis() + "." + getFileExtention(imageuri));
            fileref.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.d("Download url", url);
                            FirebaseDatabase.getInstance().getReference().child("Service_Provider").child("Registered Drivers").child("UserImages").setValue(uri.toString());
                            pd.dismiss();
                        }
                    });
                }
            });

        }
    }
}