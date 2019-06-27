package com.thesocialnetwork.linkr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TabFriendList extends Fragment {

    RecyclerView rv;
    LinearLayoutManager layoutManager;
    AdapterFriendList adapterFriendList;
    ArrayList<ModelFriendRequest> arrayList=new ArrayList<>();
    DatabaseReference db_friendList;
    String currentUserKey;
    View mMainView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mMainView=inflater.inflate(R.layout.friend_list, container, false);
        setHasOptionsMenu(true);
        rv=mMainView.findViewById(R.id.friend_list);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mMainView.getContext());
        rv.setLayoutManager(layoutManager);
        currentUserKey= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db_friendList= FirebaseDatabase.getInstance().getReference().child("friendList").child(currentUserKey);
        db_friendList.keepSynced(true);

        return mMainView;
    }

    @Override
    public void onResume() {
        super.onResume();

        adapterFriendList=new AdapterFriendList(mMainView.getContext(),fetch(),"friend");
        rv.setAdapter(adapterFriendList);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        final MenuItem searchItem = menu.findItem(R.id.search_user);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Friend..");

        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {

            @Override
            public void onViewDetachedFromWindow(View arg0) {
                // search was detached/closed
                menu.findItem(R.id.action_pending_request).setVisible(true);
                getActivity().findViewById(R.id.tab_layout).setVisibility(View.VISIBLE);
            }

            @Override
            public void onViewAttachedToWindow(View arg0) {
                // search was opened
                menu.findItem(R.id.action_pending_request).setVisible(false);
                getActivity().findViewById(R.id.tab_layout).setVisibility(View.GONE);

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapterFriendList.getFilter().filter(newText);
                return false;
            }
        });
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
