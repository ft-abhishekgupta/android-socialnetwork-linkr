package com.thesocialnetwork.linkr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by corei3 on 28-05-2018.
 */

public class AdapterMessageList extends RecyclerView.Adapter<AdapterMessageList.MyViewHolder> {

    Context context;
    ArrayList<ModelMessage> listModel = new ArrayList<>();
    int VIEW_TYPE_MESSAGE_SENT = 1;
    int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    @Override
    public int getItemViewType(int position) {

        String mAuthUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String mCurrentUserId = listModel.get(position).getSent_by();

        if (mAuthUserId.equals(mCurrentUserId)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
        }
    }

    public AdapterMessageList(Context context, ArrayList<ModelMessage> listModel) {

        this.context = context;
        this.listModel = listModel;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_me, parent, false);
            AdapterMessageList.MyViewHolder myViewHolder = new AdapterMessageList.MyViewHolder(view);
            return myViewHolder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_other, parent, false);
            AdapterMessageList.MyViewHolder myViewHolder = new AdapterMessageList.MyViewHolder(view);
            return myViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        int VIEW_TYPE_MESSAGE_SENT = 1;

        if (getItemViewType(position) == VIEW_TYPE_MESSAGE_SENT) {
            TextView text_me = (TextView) holder.itemView.findViewById(R.id.text_byMe);
            TextView time_me = (TextView) holder.itemView.findViewById(R.id.msg_time_me);

            text_me.setText(listModel.get(position).getText());
            time_me.setText(getCurrentTime(listModel.get(position).getTime()));
        } else {
            TextView text_other = (TextView) holder.itemView.findViewById(R.id.text_message_body);
            TextView time_other = (TextView) holder.itemView.findViewById(R.id.text_message_time);

            text_other.setText(listModel.get(position).getText());
            time_other.setText(getCurrentTime(listModel.get(position).getTime()));
        }
    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    public String getCurrentTime(String timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis((Long.parseLong(timestamp)) * 1000L);
        String time = DateFormat.format("HH:mm", cal).toString();
        return time;
    }
}
