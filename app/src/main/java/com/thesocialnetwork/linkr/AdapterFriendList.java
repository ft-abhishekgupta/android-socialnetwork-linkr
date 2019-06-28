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
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class AdapterFriendList extends RecyclerView.Adapter<AdapterFriendList.MyViewHolder> implements Filterable {

    ArrayList<ModelFriendRequest> listModel = new ArrayList<>();
    ArrayList<ModelFriendRequest> filterList = new ArrayList<>();
    Context context;
    FilterFriendList filterFriendList;
    String sent_from;

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

    public AdapterFriendList(Context context, ArrayList<ModelFriendRequest> listModel, String sent_from) {

        this.context = context;
        this.listModel = listModel;
        this.sent_from = sent_from;
        this.filterList = listModel;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_friend_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

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

        if (sent_from.equals("friend")) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CharSequence options[] = new CharSequence[] { "Open Profile", "Send Message" };
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Select Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Click Event for each item.
                            if (i == 0) {

                                Intent intent = new Intent(context, UserProfile.class);
                                String key = listModel.get(position).getUser_key();
                                String name = listModel.get(position).getUser_name();
                                intent.putExtra("user_key", key);
                                intent.putExtra("user_name", name);
                                intent.putExtra("send_by", "friend");
                                context.startActivity(intent);
                            }

                            if (i == 1) {

                                Intent intent = new Intent(context, ChatAct.class);
                                String key = listModel.get(position).getUser_key();
                                intent.putExtra("user_key", key);
                                context.startActivity(intent);
                            }
                        }
                    });

                    builder.show();
                }
            });
        } else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, ChatAct.class);
                    String key = listModel.get(position).getUser_key();
                    intent.putExtra("user_key", key);
                    context.startActivity(intent);
                }
            });
        }

        user_name.setText(listModel.get(position).getUser_name());
        user_email.setText(listModel.get(position).getUser_email());
    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    @Override
    public Filter getFilter() {

        if (filterFriendList == null) {
            filterFriendList = new FilterFriendList(filterList, this);
        }
        return filterFriendList;
    }
}