package com.thesocialnetwork.linkr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class UserProfile extends AppCompatActivity {

    String mCurrentUserId,mAuthUserId,user_name,user_img,send_by;
    CircularImageView user_img_view;
    RecyclerView rv_phone,rv_address,rv_email,rv_link;
    TextView user_name_view;
    LinearLayoutManager layoutManagerPhone,layoutManagerEmail,layoutManagerAddress,layoutManagerLink;

    ArrayList<ModelDetailedInfo> dataListModelPhone=new ArrayList<>();
    ArrayList<ModelDetailedInfo> dataListModelEmail=new ArrayList<>();
    ArrayList<ModelDetailedInfo> dataListModelAddress=new ArrayList<>();
    ArrayList<ModelDetailedInfo> dataListModelLink=new ArrayList<>();
    AdapterUsersProfile adapterInfoPhone,adapterInfoLink,adapterInfoEmail,adapterInfoAddress;

    DatabaseReference db_usersProfile,db_detailedInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().hide();

        Bundle bundle=getIntent().getExtras();
        mCurrentUserId=bundle.getString("user_key");
        user_name=bundle.getString("user_name");
        send_by=bundle.getString("send_by");

        user_img_view=(CircularImageView) findViewById(R.id.user_img);
        rv_phone = (RecyclerView) findViewById(R.id.recycler_view_phone);
        rv_email = (RecyclerView) findViewById(R.id.recycler_view_email);
        rv_address = (RecyclerView) findViewById(R.id.recycler_view_address);
        rv_link = (RecyclerView) findViewById(R.id.recycler_view_link);
        user_name_view=(TextView)findViewById(R.id.profile_displayName);
        user_name_view.setText(user_name);

        rv_address.setHasFixedSize(true);
        rv_address.setNestedScrollingEnabled(false);
        rv_email.setHasFixedSize(true);
        rv_email.setNestedScrollingEnabled(false);
        rv_phone.setHasFixedSize(true);
        rv_phone.setNestedScrollingEnabled(false);
        rv_link.setHasFixedSize(true);
        rv_link.setNestedScrollingEnabled(false);
        layoutManagerPhone = new LinearLayoutManager(this);
        layoutManagerEmail = new LinearLayoutManager(this);
        layoutManagerAddress = new LinearLayoutManager(this);
        layoutManagerLink = new LinearLayoutManager(this);
        rv_link.setLayoutManager(layoutManagerLink);
        rv_phone.setLayoutManager(layoutManagerPhone);
        rv_email.setLayoutManager(layoutManagerEmail);
        rv_address.setLayoutManager(layoutManagerAddress);

        mAuthUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db_usersProfile= FirebaseDatabase.getInstance().getReference().child("usersProfile").child(mCurrentUserId);
        db_usersProfile.keepSynced(true);
        db_detailedInfo=FirebaseDatabase.getInstance().getReference().child("detailedInfo").child(mCurrentUserId);
        db_detailedInfo.keepSynced(true);

        db_usersProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_img=dataSnapshot.child("profile_img").getValue().toString();
                Glide.with(UserProfile.this).load(user_img).thumbnail(0.5f).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(user_img_view);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {

        adapterInfoPhone=new AdapterUsersProfile(UserProfile.this,fetch("Phone"),"Phone");
        adapterInfoEmail=new AdapterUsersProfile(UserProfile.this,fetch("Email"),"Email");
        adapterInfoAddress=new AdapterUsersProfile(UserProfile.this,fetch("Address"),"Address");
        adapterInfoLink=new AdapterUsersProfile(UserProfile.this,fetch("Link"),"Link");

        rv_phone.setAdapter(adapterInfoPhone);
        rv_email.setAdapter(adapterInfoEmail);
        rv_address.setAdapter(adapterInfoAddress);
        rv_link.setAdapter(adapterInfoLink);

        super.onResume();
    }

    private ArrayList<ModelDetailedInfo> fetch(String str) {

        if(send_by.equals("user"))
        {
            if(str.equals("Phone"))
            {
                db_detailedInfo.child("other_phone").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataListModelPhone.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if(dataSnapshot.child(ds.getKey()).child("privacy").getValue().toString().equals("Everyone"))
                            {
                                ModelDetailedInfo model=ds.getValue(ModelDetailedInfo.class);
                                dataListModelPhone.add(model);
                            }
                        }
                        adapterInfoPhone.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return dataListModelPhone;
            }
            else if(str.equals("Email"))
            {
                db_detailedInfo.child("other_email").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataListModelEmail.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if(dataSnapshot.child(ds.getKey()).child("privacy").getValue().toString().equals("Everyone"))
                            {
                                ModelDetailedInfo model=ds.getValue(ModelDetailedInfo.class);
                                dataListModelEmail.add(model);
                            }
                        }
                        adapterInfoEmail.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return dataListModelEmail;
            }
            else if(str.equals("Address"))
            {
                db_detailedInfo.child("other_address").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataListModelAddress.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if(!dataSnapshot.child(ds.getKey()).child("privacy").getValue().toString().equals("Everyone"))
                            {
                                ModelDetailedInfo model=ds.getValue(ModelDetailedInfo.class);
                                dataListModelAddress.add(model);
                            }
                        }
                        adapterInfoAddress.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return dataListModelAddress;
            }
            else
            {
                db_detailedInfo.child("other_link").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataListModelLink.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if(!dataSnapshot.child(ds.getKey()).child("privacy").getValue().toString().equals("Everyone"))
                            {
                                ModelDetailedInfo model=ds.getValue(ModelDetailedInfo.class);
                                dataListModelLink.add(model);
                            }
                        }
                        adapterInfoLink.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return dataListModelLink;
            }
        }
        else
        {
            if(str.equals("Phone"))
            {
                db_detailedInfo.child("other_phone").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataListModelPhone.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if(!dataSnapshot.child(ds.getKey()).child("privacy").getValue().toString().equals("Only Me"))
                            {
                                ModelDetailedInfo model=ds.getValue(ModelDetailedInfo.class);
                                dataListModelPhone.add(model);
                            }
                        }
                        adapterInfoPhone.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return dataListModelPhone;
            }
            else if(str.equals("Email"))
            {
                db_detailedInfo.child("other_email").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataListModelEmail.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if(!dataSnapshot.child(ds.getKey()).child("privacy").getValue().toString().equals("Only Me"))
                            {
                                ModelDetailedInfo model=ds.getValue(ModelDetailedInfo.class);
                                dataListModelEmail.add(model);
                            }
                        }
                        adapterInfoEmail.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return dataListModelEmail;
            }
            else if(str.equals("Address"))
            {
                db_detailedInfo.child("other_address").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataListModelAddress.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if(!dataSnapshot.child(ds.getKey()).child("privacy").getValue().toString().equals("Only Me"))
                            {
                                ModelDetailedInfo model=ds.getValue(ModelDetailedInfo.class);
                                dataListModelAddress.add(model);
                            }
                        }
                        adapterInfoAddress.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return dataListModelAddress;
            }
            else
            {
                db_detailedInfo.child("other_link").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataListModelLink.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if(!dataSnapshot.child(ds.getKey()).child("privacy").getValue().toString().equals("Only Me"))
                            {
                                ModelDetailedInfo model=ds.getValue(ModelDetailedInfo.class);
                                dataListModelLink.add(model);
                            }
                        }
                        adapterInfoLink.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return dataListModelLink;
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
