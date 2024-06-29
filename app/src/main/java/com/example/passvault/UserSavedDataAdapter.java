package com.example.passvault;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserSavedDataAdapter extends RecyclerView.Adapter<UserSavedDataAdapter.UsersSavedDataHolder>{

    Context context;
    ArrayList<UserSavedData> arrayList = new ArrayList<>();

    public UserSavedDataAdapter(Context context, ArrayList<UserSavedData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public UsersSavedDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.custom_row_layout,parent,false);

        return new UsersSavedDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersSavedDataHolder holder, int position) {

        holder.username.setText(arrayList.get(position).getUsername());
        //holder.emailid.setText(arrayList.get(position).getEmailId());
        //holder.password.setText(arrayList.get(position).getpassword());
        holder.serviceType.setText(arrayList.get(position).getServiceType());


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class UsersSavedDataHolder extends RecyclerView.ViewHolder {

        TextView username;
        //TextView emailid;
        //TextView password;
        TextView serviceType;

        public UsersSavedDataHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.cardviewUsername_textview);
            serviceType = itemView.findViewById(R.id.cardviewServiceType_textview);
        }
    }


}
