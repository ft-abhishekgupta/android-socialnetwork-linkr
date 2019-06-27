package com.thesocialnetwork.linkr;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class ChatAct extends AppCompatActivity {

    String user_name,user_img,user_status;
    TextView mTitle,mLastSeen;
    CircularImageView mActionBarImg;
    DatabaseReference db_users_profile,db_mesgList;
    String mAuthUserId,mCurrentUserId;
    ImageButton mChatSend;
    EditText mMessageBox;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView messageListRV;
    LinearLayoutManager linearLayoutManager;

    //For Loading Messages
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    ArrayList<ModelMessage> messagesList=new ArrayList<>();
    AdapterMessageList mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle bundle=getIntent().getExtras();
        mCurrentUserId=bundle.getString("user_key");

        final ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_chat_bar, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        mTitle=(TextView)findViewById(R.id.custom_bar_title);
        mLastSeen=(TextView)findViewById(R.id.custom_bar_seen_time);
        mActionBarImg=(CircularImageView) findViewById(R.id.custom_bar_image);
        mChatSend=(ImageButton)findViewById(R.id.chat_send_btn);
        mMessageBox=(EditText)findViewById(R.id.chat_message_view);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        messageListRV=(RecyclerView) findViewById(R.id.messages_list);
        linearLayoutManager = new LinearLayoutManager(this);
        messageListRV.setHasFixedSize(true);
        messageListRV.setLayoutManager(linearLayoutManager);

        db_mesgList=FirebaseDatabase.getInstance().getReference().child("msgList");
        mAuthUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db_users_profile= FirebaseDatabase.getInstance().getReference().child("usersProfile").child(mCurrentUserId);

        db_users_profile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user_name=dataSnapshot.child("profile_name").getValue().toString();
                user_img=dataSnapshot.child("profile_img").getValue().toString();
                user_status=dataSnapshot.child("profile_online_status").getValue().toString();
                mTitle.setText(user_name);
                Glide.with(ChatAct.this).load(user_img).thumbnail(0.5f).crossFade().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(mActionBarImg);
                if(user_status.equals("online")) {
                    mLastSeen.setText("Online");
                } else {
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = Long.parseLong(user_status);
                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());
                    mLastSeen.setText(lastSeenTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(mMessageBox.getText().toString()))
                {
                    storeMsg();
                    mMessageBox.setText("");
                }
            }
        });

        mAdapter = new AdapterMessageList(this,messagesList);
        messageListRV.setAdapter(mAdapter);

        loadMessages();

        //For Loading Message
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPos = 0;
                loadMoreMessages();
            }
        });

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

    private void loadMoreMessages() {
        DatabaseReference messageRef = db_mesgList.child(mAuthUserId).child(mCurrentUserId);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ModelMessage message = dataSnapshot.getValue(ModelMessage.class);
                String messageKey = dataSnapshot.getKey();
                if(!mPrevKey.equals(messageKey)){
                    messagesList.add(itemPos++, message);
                } else {
                    mPrevKey = mLastKey;
                }

                if(itemPos == 1) {
                    mLastKey = messageKey;
                }
                Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                linearLayoutManager.scrollToPositionWithOffset(10, 0);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void loadMessages() {

        DatabaseReference messageRef = db_mesgList.child(mAuthUserId).child(mCurrentUserId);
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ModelMessage message = dataSnapshot.getValue(ModelMessage.class);
                itemPos++;
                if(itemPos == 1){
                    String messageKey = dataSnapshot.getKey();
                    mLastKey = messageKey;
                    mPrevKey = messageKey;
                }
                messagesList.add(message);
                mAdapter.notifyDataSetChanged();
                messageListRV.scrollToPosition(messagesList.size() - 1);
                swipeRefreshLayout.setRefreshing(false);

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void storeMsg() {

        String push_id=db_mesgList.child(mAuthUserId).child(mCurrentUserId).push().getKey();
        String text=mMessageBox.getText().toString();

        Long tsLong = System.currentTimeMillis()/1000;
        String timestamp = tsLong.toString();
        ModelMessage modelMessageForAuthId=new ModelMessage(text,timestamp,mAuthUserId);

        db_mesgList.child(mAuthUserId).child(mCurrentUserId).child(push_id).setValue(modelMessageForAuthId);
        db_mesgList.child(mCurrentUserId).child(mAuthUserId).child(push_id).setValue(modelMessageForAuthId);

    }
}
