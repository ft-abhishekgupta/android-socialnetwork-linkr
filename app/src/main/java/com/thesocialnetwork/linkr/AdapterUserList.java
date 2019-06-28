package com.thesocialnetwork.linkr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class AdapterUserList extends RecyclerView.Adapter<AdapterUserList.MyViewHolder> implements Filterable {

    ArrayList<ModelUserProfile> listModel = new ArrayList<>();
    ArrayList<ModelUserProfile> filterList = new ArrayList<>();
    Context context;
    FilterUserList filterUserList;
    boolean toOpen = true;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView user_name, user_email, user_status;
        CircularImageView user_image;
        View itemView;
        RelativeLayout data_lt, request_lt;

        public MyViewHolder(View itemView) {
            super(itemView);

            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_email = (TextView) itemView.findViewById(R.id.user_email);
            user_status = (TextView) itemView.findViewById(R.id.user_status);
            user_image = (CircularImageView) itemView.findViewById(R.id.user_img);
            request_lt = (RelativeLayout) itemView.findViewById(R.id.request_layout);
            data_lt = (RelativeLayout) itemView.findViewById(R.id.data_layout);
            this.itemView = itemView;
        }
    }

    public AdapterUserList(Context context, ArrayList<ModelUserProfile> listModel) {

        this.context = context;
        this.listModel = listModel;
        this.filterList = listModel;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_user_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final String userKey = listModel.get(position).getProfile_uid();
        final String currentUserKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference db_friendList = FirebaseDatabase.getInstance().getReference().child("friendList");

        final TextView user_name, user_email, user_status;
        CircularImageView user_image;
        RelativeLayout request_lt, data_lt;
        user_name = holder.user_name;
        user_email = holder.user_email;
        user_status = holder.user_status;
        user_image = holder.user_image;
        request_lt = holder.request_lt;
        data_lt = holder.data_lt;

        final String item_name = listModel.get(position).getProfile_name();
        final String item_email = listModel.get(position).getProfile_email();
        final String currentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        final String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db_friendList.child(currentUserKey).child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("user_status")) {
                    String s = dataSnapshot.child("user_status").getValue().toString().trim();
                    if (s.equals("requestSent"))
                        user_status.setText("Request Sent");
                    else if (s.equals("requestRecieved"))
                        user_status.setText("Request Recieved");
                    else
                        user_status.setText("Friends");
                } else {
                    user_status.setText("Send Request");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        user_name.setText(item_name);
        user_email.setText(item_email);
        Glide.with(context).load(listModel.get(position).getProfile_img()).thumbnail(0.5f).crossFade().centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(user_image);

        data_lt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("user_key", userKey);
                intent.putExtra("user_name", item_name);
                intent.putExtra("send_by", "user");
                context.startActivity(intent);
            }
        });

        request_lt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toOpen = true;
                db_friendList.child(currentUserKey).child(userKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (toOpen) {
                            if (dataSnapshot.hasChild("user_status")) {
                                String s = dataSnapshot.child("user_status").getValue().toString().trim();
                                if (s.equals("requestSent"))
                                    openDialog("RequestSent");
                                else if (s.equals("requestRecieved"))
                                    openDialog("RequestRecieved");
                                else
                                    openDialog("Friends");
                            } else {
                                openDialog("SendRequest");
                            }
                        }
                        toOpen = false;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            private void openDialog(String friends) {

                if (friends.equals("SendRequest")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Send Friend Request");
                    alert.setMessage("Are you sure?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ModelFriendRequest currentUserModel = new ModelFriendRequest(userKey, item_name, item_email,
                                    "requestSent");
                            ModelFriendRequest listUserModel = new ModelFriendRequest(currentUserKey, currentUserName,
                                    currentUserEmail, "requestRecieved");
                            db_friendList.child(currentUserKey).child(userKey).setValue(currentUserModel);
                            db_friendList.child(userKey).child(currentUserKey).setValue(listUserModel);
                            dialog.dismiss();
                        }
                    });
                    alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alert.show();
                }

                else if (friends.equals("RequestRecieved")) {
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

                else if (friends.equals("RequestSent")) {
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
                }

                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("You both are friends");
                    alert.setMessage("Remove Friend?");
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
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    @Override
    public Filter getFilter() {

        if (filterUserList == null) {
            filterUserList = new FilterUserList(filterList, this);
        }
        return filterUserList;
    }
}
