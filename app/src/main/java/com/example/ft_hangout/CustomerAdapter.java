package com.example.ft_hangout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ft_hangout.controller.MainActivity;
import com.example.ft_hangout.controller.Updateactivity;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder> {
    private Context context;
    Activity activity;
    private ArrayList contact_id, contact_name, contact_lastName, contact_email, contact_number, contact_birthday;

    public CustomerAdapter(Activity activity, Context context, ArrayList contact_id, ArrayList contact_name, ArrayList contact_lastName, ArrayList contact_email, ArrayList contact_number, ArrayList contact_birthday){
        this.activity = activity;
        this.context = context;
        this.contact_id = contact_id;
        this.contact_name = contact_name;
        this.contact_lastName = contact_lastName;
        this.contact_email = contact_email;
        this.contact_number = contact_number;
        this.contact_birthday = contact_birthday;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row,parent, false);
        return  new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,  int position) {
            holder.contact_id.setText(String.valueOf(contact_id.get(position)));

        holder.contact_name.setText(String.valueOf(contact_name.get(position)));
        holder.contact_lastName.setText(String.valueOf(contact_lastName.get(position)));
        holder.contact_email.setText(String.valueOf(contact_email.get(position)));
        holder.contact_number.setText(String.valueOf(contact_number.get(position)));
        holder.contact_birthday.setText(String.valueOf(contact_birthday.get(position)));

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Updateactivity.class);
                intent.putExtra("id", String.valueOf(contact_id.get(position)));
                intent.putExtra("name", String.valueOf(contact_name.get(position)));
                intent.putExtra("lastName", String.valueOf(contact_lastName.get(position)));
                intent.putExtra("email", String.valueOf(contact_email.get(position)));
                intent.putExtra("Number", String.valueOf(contact_number.get(position)));
                intent.putExtra("birthday", String.valueOf(contact_birthday.get(position)));

                activity.startActivityForResult(intent, 1);
            }
        });


    }

    @Override
    public int getItemCount() {

        return contact_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mainLayout;
        TextView contact_id, contact_name, contact_lastName, contact_email, contact_number, contact_birthday;
        ImageButton call, sms;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            contact_id = itemView.findViewById(R.id.contact_id);
            contact_name = itemView.findViewById(R.id.contact_name);
            contact_lastName = itemView.findViewById(R.id.contact_lastName);
            contact_email = itemView.findViewById(R.id.contact_email);
            contact_number = itemView.findViewById(R.id.contact_number);
            contact_birthday = itemView.findViewById(R.id.contact_birthday);

            mainLayout = itemView.findViewById(R.id.mainLayout);


        }
    }
}
