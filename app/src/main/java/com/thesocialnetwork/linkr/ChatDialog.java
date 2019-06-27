package com.thesocialnetwork.linkr;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by corei3 on 07-06-2018.
 */

public class ChatDialog extends DialogFragment {

    RecyclerView rv;
    AdapterFriendList adapterFriendList;
    ArrayList<ModelFriendRequest> arrayList=new ArrayList<>();
    DatabaseReference db_friendList;
    String currentUserKey;
    View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView=inflater.inflate(R.layout.chat_dialog_box,container);

        //RECYCER
        rv= (RecyclerView) mMainView.findViewById(R.id.rv_chat);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        currentUserKey= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db_friendList= FirebaseDatabase.getInstance().getReference().child("friendList").child(currentUserKey);
        db_friendList.keepSynced(true);

        //ADAPTER
        adapterFriendList=new AdapterFriendList(this.getActivity(),fetch(),"chat");
        rv.setAdapter(adapterFriendList);

        this.getDialog().setTitle("Message To");

        return mMainView;
    }

    private ArrayList<ModelFriendRequest> fetch() {

        db_friendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(dataSnapshot.child(ds.getKey()).child("user_status").getValue().toString().equals("requestAccepted"))
                    {
                        ModelFriendRequest model=ds.getValue(ModelFriendRequest.class);
                        arrayList.add(model);
                    }
                }
                adapterFriendList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return arrayList;
    }
}
