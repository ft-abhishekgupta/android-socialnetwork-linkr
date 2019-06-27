package com.thesocialnetwork.linkr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by corei3 on 30-04-2018.
 */

public class AdapterProfileData extends RecyclerView.Adapter<AdapterProfileData.MyViewHolder> {


    ArrayList<ModelDetailedInfo> listModels=new ArrayList<>();
    String infoAbout;
    Context context;
    String ph,email;

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        TextView data_type,user_data;
        ImageView edit_data;

        public MyViewHolder(View itemView) {

            super(itemView);
            this.data_type=(TextView) itemView.findViewById(R.id.data_type);
            this.user_data=(TextView) itemView.findViewById(R.id.user_data);
            this.edit_data=(ImageView) itemView.findViewById(R.id.edit_btn);
        }
    }

    public AdapterProfileData(Context context, ArrayList<ModelDetailedInfo> listModels , String infoAbout) {

        this.context=context;
        this.listModels = listModels;
        this.infoAbout = infoAbout;
        final String UserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("usersProfile").child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ph=dataSnapshot.child("profile_phone").getValue().toString().trim();
                email=dataSnapshot.child("profile_email").getValue().toString().trim();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public AdapterProfileData.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_listitems, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterProfileData.MyViewHolder holder, final int position) {

        TextView data_type = holder.data_type;
        TextView user_data = holder.user_data;
        final ImageView edit_data=holder.edit_data;
        final String UserId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        data_type.setText(listModels.get(position).getType());
        user_data.setText(listModels.get(position).getData());
        if(listModels.get(position).getData().equals(ph) || listModels.get(position).getData().equals(email))
        {
            edit_data.setVisibility(View.INVISIBLE);
        }
        else
        {
            edit_data.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu popup = new PopupMenu(context, edit_data);
                    popup.inflate(R.menu.edit_data);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            switch (menuItem.getItemId()) {

                                case R.id.edit:

                                    String data=listModels.get(position).getData();
                                    String type=listModels.get(position).getType();
                                    String privacy=listModels.get(position).getPrivacy();
                                    String for_what;

                                    DatabaseReference db1;
                                    if(infoAbout.equals("Phone"))
                                    {
                                        for_what="Phone";
                                        db1=FirebaseDatabase.getInstance().getReference().child("detailedInfo").child(UserId).child("other_phone").child(listModels.get(position).getKey());
                                        db1.keepSynced(true);
                                    }
                                    else if(infoAbout.equals("Email"))
                                    {
                                        for_what="Email";
                                        db1=FirebaseDatabase.getInstance().getReference().child("detailedInfo").child(UserId).child("other_email").child(listModels.get(position).getKey());
                                        db1.keepSynced(true);
                                    }
                                    else if(infoAbout.equals("Address"))
                                    {
                                        for_what="Address";
                                        db1=FirebaseDatabase.getInstance().getReference().child("detailedInfo").child(UserId).child("other_address").child(listModels.get(position).getKey());
                                        db1.keepSynced(true);
                                    }
                                    else
                                    {
                                        for_what="Social Link";
                                        db1=FirebaseDatabase.getInstance().getReference().child("detailedInfo").child(UserId).child("other_link").child(listModels.get(position).getKey());
                                        db1.keepSynced(true);
                                    }

                                    showDialogBox(db1,data,type,privacy,for_what);
                                    return true;



                                case R.id.delete:

                                    DatabaseReference db;
                                    if(infoAbout.equals("Phone"))
                                    {
                                        db=FirebaseDatabase.getInstance().getReference().child("detailedInfo").child(UserId).child("other_phone").child(listModels.get(position).getKey());
                                        db.keepSynced(true);
                                    }
                                    else if(infoAbout.equals("Email"))
                                    {
                                        db=FirebaseDatabase.getInstance().getReference().child("detailedInfo").child(UserId).child("other_email").child(listModels.get(position).getKey());
                                        db.keepSynced(true);
                                    }
                                    else if(infoAbout.equals("Address"))
                                    {
                                        db=FirebaseDatabase.getInstance().getReference().child("detailedInfo").child(UserId).child("other_address").child(listModels.get(position).getKey());
                                        db.keepSynced(true);
                                    }
                                    else
                                    {
                                        db=FirebaseDatabase.getInstance().getReference().child("detailedInfo").child(UserId).child("other_link").child(listModels.get(position).getKey());
                                        db.keepSynced(true);
                                    }

                                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot) {


                                            AlertDialog.Builder alert = new AlertDialog.Builder(
                                                    context);
                                            alert.setTitle("Alert!!");
                                            alert.setMessage("Are you sure to delete this item?");
                                            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    dataSnapshot.getRef().removeValue();
                                                    dialog.dismiss();
                                                    listModels.remove(position);
                                                    notifyDataSetChanged();
                                                    //notifyItemRemoved(position);
                                                    //notifyItemRangeChanged(position, listModels.size());
                                                    Toast.makeText(context,"Removed Successfully", Toast.LENGTH_SHORT).show();
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

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                        }
                                    });
                                    return true;



                                default:
                                    return false;
                            }
                        }
                    });
                    popup.show();
                }
            });
        }
    }

    private void showDialogBox(final DatabaseReference db, String data, String type, String privacy, String infoAbout) {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.add_item_dialog, null);
        final EditText editText=(EditText) promptsView.findViewById(R.id.enterDetail);
        final Spinner spinner1=(Spinner) promptsView.findViewById(R.id.spinner_type);
        final Spinner spinner2=(Spinner) promptsView.findViewById(R.id.spinner_secure);
        String data_type[]={"Type","Home","Work","Main","Other"};
        String secure[]={"Privacy","Only Me","Friends","Everyone"};
        String source_link[]={"Source","LinkedIn","Facebook","Twitter","Google+","Instagram","SnapChat"};

        if(infoAbout.equals("Social Link"))
        {
            final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, source_link);
            adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner1.setAdapter(adapter1);
            spinner1.setSelection(adapter1.getPosition(type));
        }
        else
        {
            final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, data_type);
            adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner1.setAdapter(adapter1);
            spinner1.setSelection(adapter1.getPosition(type));
        }

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, secure);
        adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setSelection(adapter2.getPosition(privacy));
        editText.setText(data);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder.setTitle(infoAbout)
                .setCancelable(false)
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //Toast.makeText(context, "Oh Yes", Toast.LENGTH_SHORT).show();
                        db.child("data").setValue(editText.getText().toString().trim());
                        db.child("type").setValue(spinner1.getSelectedItem().toString().trim());
                        db.child("privacy").setValue(spinner2.getSelectedItem().toString().trim());
                        notifyDataSetChanged();
                        Toast.makeText(context, "Data Updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }
}
