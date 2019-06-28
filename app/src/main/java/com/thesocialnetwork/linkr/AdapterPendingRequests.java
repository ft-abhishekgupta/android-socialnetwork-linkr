package com.thesocialnetwork.linkr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

/**
 * Created by corei3 on 06-05-2018.
 */

public class AdapterPendingRequests extends RecyclerView.Adapter<AdapterPendingRequests.MyViewHolder> {

    ArrayList<ModelFriendRequest> listModel = new ArrayList<>();
    Context context;
    String type;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView user_name, user_email;
        CircularImageView user_image;
        View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);

            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_email = (TextView) itemView.findViewById(R.id.user_email);
            user_image = (CircularImageView) itemView.findViewById(R.id.user_img);
            this.itemView = itemView;
        }
    }

    public AdapterPendingRequests(Context context, ArrayList<ModelFriendRequest> listModel, String type) {

        this.context = context;
        this.listModel = listModel;
        this.type = type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_friend_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final String userKey = listModel.get(position).getUser_key();
        final String currentUserKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference db_friendList = FirebaseDatabase.getInstance().getReference().child("friendList");

        final TextView user_name, user_email;
        final CircularImageView user_image;
        user_name = holder.user_name;
        user_email = holder.user_email;
        user_image = holder.user_image;

        FirebaseDatabase.getInstance().getReference().child("usersProfile").child(listModel.get(position).getUser_key())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String imgUrl = dataSnapshot.child("profile_img").getValue().toString().trim();
                        Glide.with(context).load(imgUrl).thumbnail(0.5f).crossFade().centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL).into(user_image);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        user_name.setText(listModel.get(position).getUser_name());
        user_email.setText(listModel.get(position).getUser_email());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (type.equals("sent")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Request Already Sent");
                    alert.setMessage("Cancel Request?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            db_friendList.child(currentUserKey).child(userKey).removeValue();
                            db_friendList.child(userKey).child(currentUserKey).removeValue();
                            dialog.dismiss();
                        }
                    });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    alert.show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Request is Pending");
                    alert.setMessage("What do you want to do?");
                    alert.setPositiveButton("Accept", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            db_friendList.child(currentUserKey).child(userKey).child("user_status")
                                    .setValue("requestAccepted");
                            db_friendList.child(userKey).child(currentUserKey).child("user_status")
                                    .setValue("requestAccepted");
                            dialog.dismiss();
                        }
                    });
                    alert.setNegativeButton("Reject", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            db_friendList.child(currentUserKey).child(userKey).removeValue();
                            db_friendList.child(userKey).child(currentUserKey).removeValue();
                            dialog.dismiss();
                        }
                    });

                    alert.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }
}
