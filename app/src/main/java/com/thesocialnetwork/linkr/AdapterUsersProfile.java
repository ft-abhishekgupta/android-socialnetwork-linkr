package com.thesocialnetwork.linkr;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * Created by corei3 on 30-05-2018.
 */

public class AdapterUsersProfile extends RecyclerView.Adapter<AdapterUsersProfile.MyViewHolder> {

    ArrayList<ModelDetailedInfo> listModels = new ArrayList<>();
    String infoAbout;
    Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userData, userDataType;
        View itemView;
        LinearLayout linearLayout;

        public MyViewHolder(View itemView) {

            super(itemView);
            this.userData = (TextView) itemView.findViewById(R.id.user_data);
            this.userDataType = (TextView) itemView.findViewById(R.id.user_data_type);
            this.linearLayout = (LinearLayout) itemView.findViewById(R.id.user_items_linear_layout);
            this.itemView = itemView;
        }
    }

    public AdapterUsersProfile(Context context, ArrayList<ModelDetailedInfo> listModels, String infoAbout) {

        this.context = context;
        this.infoAbout = infoAbout;
        this.listModels = listModels;
    }

    @Override
    public AdapterUsersProfile.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_user_items, parent, false);
        AdapterUsersProfile.MyViewHolder myViewHolder = new AdapterUsersProfile.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterUsersProfile.MyViewHolder holder, final int position) {

        TextView userData = holder.userData;
        TextView userDataType = holder.userDataType;
        LinearLayout linearLayout = holder.linearLayout;

        userData.setText(listModels.get(position).getData());
        userDataType.setText(listModels.get(position).getType());

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogBox(infoAbout, listModels.get(position).getData());
            }
        });
    }

    private void showDialogBox(String infoAbout, final String data) {

        if (infoAbout.equals("Phone")) {
            CharSequence options[] = new CharSequence[] { "Copy", "Call" };
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Options");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Click Event for each item.
                    if (i == 0) {
                        ClipboardManager clipboard = (ClipboardManager) context
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", data);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    }

                    if (i == 1) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + data));
                        context.startActivity(intent);
                    }
                }
            });

            builder.show();
        } else if (infoAbout.equals("Email")) {
            CharSequence options[] = new CharSequence[] { "Copy", "Send Email" };
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Options");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Click Event for each item.
                    if (i == 0) {
                        ClipboardManager clipboard = (ClipboardManager) context
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", data);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    }

                    if (i == 1) {

                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", data, null));
                        context.startActivity(Intent.createChooser(emailIntent, "Send email.."));
                    }
                }
            });

            builder.show();
        } else if (infoAbout.equals("Address")) {
            CharSequence options[] = new CharSequence[] { "Copy" };
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Options");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Click Event for each item.
                    if (i == 0) {
                        ClipboardManager clipboard = (ClipboardManager) context
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", data);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            builder.show();
        } else {
            CharSequence options[] = new CharSequence[] { "Copy", "Open in browser" };
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Options");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Click Event for each item.
                    if (i == 0) {
                        ClipboardManager clipboard = (ClipboardManager) context
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", data);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    }

                    if (i == 1) {

                        String url = data;
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(browserIntent);
                    }
                }
            });

            builder.show();
        }
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }
}
