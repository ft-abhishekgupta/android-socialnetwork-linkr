package com.thesocialnetwork.linkr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GoogleApiClient GoogleApiClient;
    private DatabaseReference databaseReference;
    private FirebaseAuth FirebaseAuth;
    String userId="";
    TextView mName ,mEmail;
    CircularImageView mImg;
    ValueEventListener valueEventListener;
    ArrayList<ModelFriendRequest> arrayList=new ArrayList<>();
    String currentUserKey;
    FirebaseUser currentUser;
    DatabaseReference db_friendList,db_usersProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseAuth = FirebaseAuth.getInstance();
        userId = FirebaseAuth.getCurrentUser().getUid();
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        currentUserKey=currentUser.getUid();
        db_friendList= FirebaseDatabase.getInstance().getReference().child("friendList").child(currentUserKey);
        db_usersProfile= FirebaseDatabase.getInstance().getReference().child("usersProfile").child(currentUserKey);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new home()).commit();

        View header = navigationView.getHeaderView(0);
        mName = header.findViewById(R.id.textview1);
        mEmail = header.findViewById(R.id.textView2);
        mImg = header.findViewById(R.id.circleImageView);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("usersProfile").child(userId);
        databaseReference.keepSynced(true);


        countRequests();
    }

    private void countRequests() {

        db_friendList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(dataSnapshot.child(ds.getKey()).child("user_status").getValue().toString().equals("requestRecieved") || dataSnapshot.child(ds.getKey()).child("user_status").getValue().toString().equals("requestSent"))
                    {
                        ModelFriendRequest model=ds.getValue(ModelFriendRequest.class);
                        arrayList.add(model);
                    }
                }
                if(arrayList.size()>0)
                    Toast.makeText(MainActivity.this, "You have " +arrayList.size()+" pending requests", Toast.LENGTH_SHORT).show();
                //showDialogBox(arrayList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showDialogBox(int size) {

        AlertDialog.Builder alert = new AlertDialog.Builder(
                MainActivity.this);
        alert.setTitle("Pending Requests");
        alert.setMessage("You have " +size+" pending requests. Do you want to process?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(MainActivity.this, PendingRequests.class);
                startActivity(intent);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("profile_name").getValue().toString();
                String email = dataSnapshot.child("profile_email").getValue().toString();
                String img = dataSnapshot.child("profile_img").getValue().toString();
                mName.setText(name);
                mEmail.setText(email);
                Glide.with(getApplicationContext()).load(img)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(mImg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);

        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            Long tsLong = System.currentTimeMillis()/1000;
            String timestamp = tsLong.toString();
            db_usersProfile.child("profile_online_status").setValue(timestamp);

            signOut();
            return true;
        }else if(id==R.id.action_exit){
            System.exit(0);
        }
            return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(GoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        FirebaseAuth.getInstance().signOut();
                        Intent i=new Intent(MainActivity.this,Login.class);
                        finish();
                        startActivity(i);
                    }
                });
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            Intent i=new Intent(this,About.class);
            startActivity(i);
        }else if (id == R.id.nav_dev) {
            Intent i=new Intent(this,Developer.class);
            startActivity(i);
        }else if(id==R.id.nav_share){
            final String appPackageName = getApplicationContext().getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Linkr - Profile Linking Android App : https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            }
        else if(id==R.id.nav_edit){
            Intent intent = new Intent(this, EditProfile.class);
            startActivity(intent);
            return true;
        }
        else if(id==R.id.nav_contacts){
            Intent intent = new Intent(this, Contacts.class);
            startActivity(intent);
            return true;
        }
        else if(id==R.id.nav_manage){
            Intent intent = new Intent(this, PendingRequests.class);
            startActivity(intent);
            return true;
        }
        else if(id==R.id.nav_chat){
            
            openChatDialog();
            return true;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openChatDialog() {

        final FragmentManager fm=getSupportFragmentManager();
        final  ChatDialog tv=new ChatDialog();
        tv.show(fm,"chat_tag");
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        GoogleApiClient.connect();

        if(currentUser != null){

            db_usersProfile.child("profile_online_status").setValue("online");
        }

        super.onStart();
    }

    @Override
    protected void onStop() {

        if(currentUser != null){

            Long tsLong = System.currentTimeMillis()/1000;
            String timestamp = tsLong.toString();
            db_usersProfile.child("profile_online_status").setValue(timestamp);
        }

        super.onStop();
    }
}
