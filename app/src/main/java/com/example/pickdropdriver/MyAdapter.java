package com.example.pickdropdriver;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<TripD> list;



    public MyAdapter(Context context, ArrayList<TripD> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v  = LayoutInflater.from(context).inflate(R.layout.activity_item,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TripD tripD = list.get(position);
        holder.name.setText(tripD.getName());
        holder.date.setText(tripD.getDate());
        holder.destination.setText(tripD.getDestination());
        holder.passengers.setText(tripD.getPassengers());
        holder.telephone.setText(tripD.getTelno());
        holder.actype.setText(tripD.getActype());


        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseReference databaseRef = getInstance().getReference("Location");
                        databaseRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange( DataSnapshot dataSnapshot) {


                                    Double value = dataSnapshot.child("Lat").getValue(double.class);
                                    Double value2  = dataSnapshot.child("Lang").getValue(double.class);
                                    String uri = "http://maps.google.com/maps?q=:" + value + "," + value2;
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    intent.setPackage("com.google.android.apps.maps");
                                    context.startActivity(intent);
                                }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }


                        });
                    }
                }, 1000);
            }
        });
        }




    @Override
    public int getItemCount() {
        return list.size();
    }

    public  static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,date,destination,passengers,telephone,actype;
        Button accept;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            destination = itemView.findViewById(R.id.destination);
            passengers = itemView.findViewById(R.id.passengers);
            telephone = itemView.findViewById(R.id.telephone);
            actype= itemView.findViewById(R.id.actype);
            accept = itemView.findViewById(R.id.accept);
        }
    }

}
