package com.thesocialnetwork.linkr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TabUserList extends Fragment{

    RecyclerView rv;
    View mMainView;
    LinearLayoutManager layoutManager;
    AdapterUserList adapterUserList;
    ArrayList<ModelUserProfile> arrayList=new ArrayList<>();
    DatabaseReference db_userProfile;
    String UserId;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mMainView= inflater.inflate(R.layout.user_list, container, false);
        setHasOptionsMenu(true);
        rv=mMainView.findViewById(R.id.user_list);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mMainView.getContext());
        rv.setLayoutManager(layoutManager);
        UserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        db_userProfile=FirebaseDatabase.getInstance().getReference().child("usersProfile");
        db_userProfile.keepSynced(true);
        return mMainView;
    }

    @Override
    public void onResume() {
        super.onResume();

        adapterUserList=new AdapterUserList(mMainView.getContext(),fetch());
        rv.setAdapter(adapterUserList);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        final MenuItem searchItem = menu.findItem(R.id.search_user);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search User..");

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

                adapterUserList.getFilter().filter(newText);
                return false;
            }
        });
    }

    private ArrayList<ModelUserProfile> fetch() {

        db_userProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(!ds.getKey().equals(UserId))
                    {
                        ModelUserProfile model=ds.getValue(ModelUserProfile.class);
                        arrayList.add(model);
                    }
                }
                adapterUserList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return arrayList;
    }
}
