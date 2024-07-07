package com.example.passvault;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserSavedDataAdapter extends RecyclerView.Adapter<UserSavedDataAdapter.UsersSavedDataHolder>{

    Context context;
    ArrayList<UserSavedData> arrayList = new ArrayList<>();
    private int lastPosition = -1;
    private OnItemClickListener listener;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public UserSavedDataAdapter(Context context, ArrayList<UserSavedData> arrayList, OnItemClickListener listener, OnEditClickListener editClickListener, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
        this.editClickListener = editClickListener;
        this.onDeleteClickListener = deleteClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(UserSavedData userSavedData);
    }

    public interface OnEditClickListener {
        void onEditClick(UserSavedData userSavedData);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(UserSavedData userSavedData);
    }

    @NonNull
    @Override
    public UsersSavedDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.custom_row_layout,parent,false);

        return new UsersSavedDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersSavedDataHolder holder, int position) {

        UserSavedData data = arrayList.get(position);

        holder.username.setText(data.getUsername());
        holder.serviceType.setText(data.getServiceType());

        setAnimation(holder.itemView, position);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(data));
        holder.editButton.setOnClickListener(v -> editClickListener.onEditClick(data));
        holder.delete_button.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void clear() {
        int size = arrayList.size();
        arrayList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class UsersSavedDataHolder extends RecyclerView.ViewHolder {

        TextView username;
        TextView serviceType;
        ImageButton editButton;
        ImageButton delete_button;

        public UsersSavedDataHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.cardviewUsername_textview);
            serviceType = itemView.findViewById(R.id.cardviewServiceType_textview);
            editButton = itemView.findViewById(R.id.editButton);
            delete_button = itemView.findViewById(R.id.deleteButton);
        }
    }
}
