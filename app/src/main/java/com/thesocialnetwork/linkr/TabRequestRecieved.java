package com.thesocialnetwork.linkr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
 * Created by corei3 on 06-05-2018.
 */

public class TabRequestRecieved extends Fragment {

    RecyclerView rv;
    View mMainView;
    LinearLayoutManager layoutManager;
    DatabaseReference db_friendList;
    String currentUserKey;
    AdapterPendingRequests adapterPendingRequests;
    ArrayList<ModelFriendRequest> arrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.friend_list, container, false);
        rv = mMainView.findViewById(R.id.friend_list);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mMainView.getContext());
        rv.setLayoutManager(layoutManager);
        currentUserKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db_friendList = FirebaseDatabase.getInstance().getReference().child("friendList").child(currentUserKey);
        db_friendList.keepSynced(true);
        return mMainView;
    }

    @Override
    public void onResume() {
        super.onResume();

        adapterPendingRequests = new AdapterPendingRequests(mMainView.getContext(), fetch(), "recieved");
        rv.setAdapter(adapterPendingRequests);
    }

    private ArrayList<ModelFriendRequest> fetch() {

        db_friendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (dataSnapshot.child(ds.getKey()).child("user_status").getValue().toString()
                            .equals("requestRecieved")) {
                        ModelFriendRequest model = ds.getValue(ModelFriendRequest.class);
                        arrayList.add(model);
                    }
                }
                adapterPendingRequests.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return arrayList;
    }
}
