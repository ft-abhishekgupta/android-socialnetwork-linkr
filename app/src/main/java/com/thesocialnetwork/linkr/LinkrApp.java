package com.thesocialnetwork.linkr;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by corei3 on 08-06-2018.
 */

public class LinkrApp extends Application {

    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {

            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("usersProfile").child(mAuth.getCurrentUser().getUid());
            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {

                        Long tsLong = System.currentTimeMillis()/1000;
                        String timestamp = tsLong.toString();
                        mUserDatabase.child("profile_online_status").onDisconnect().setValue(timestamp);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }
}
