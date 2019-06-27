package com.thesocialnetwork.linkr;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class home extends Fragment {
    View v;
    ImageView i;
    Animation rotate;
    private DatabaseReference du;
    String s;
    private FirebaseAuth mAuth;
    String uid;
    TextView contacts,chat,requests,profile,about;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.home,container,false);
        getActivity().setTitle("Home");

        return v;
    }

    @Override
    public void onResume() {

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Sketch.ttf");

        contacts = v.findViewById(R.id.hContacts);
        chat = v.findViewById(R.id.hChat);
        requests = v.findViewById(R.id.hRequests);
        profile = v.findViewById(R.id.hProfile);
        about = v.findViewById(R.id.hAbout);

        contacts.setTypeface(typeface);
        chat.setTypeface(typeface);
        requests.setTypeface(typeface);
        profile.setTypeface(typeface);
        about.setTypeface(typeface);

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), Contacts.class);
                startActivity(intent);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openChatDialog();
            }
        });

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), PendingRequests.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), EditProfile.class);
                startActivity(intent);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getContext(),About.class);
                startActivity(i);
            }
        });
        super.onResume();

    }

    private void openChatDialog() {

        final FragmentManager fm=getFragmentManager();
        final  ChatDialog tv=new ChatDialog();
        tv.show(fm,"chat_tag");
    }
}
