package com.emanmustafa.chat_app.Adapter;

import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.emanmustafa.chat_app.Model.Chat;
import com.emanmustafa.chat_app.R;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private java.util.List<Chat> mChat;
    private String imageurl;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, java.util.List<Chat> mChat, String imageurl){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            android.view.View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            android.view.View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {

        final Chat chat = mChat.get(position);



        if (imageurl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }


        if(chat.getMessage().equals("أرسل لك صورة") && !chat.getUrl().equals("")) {
            if (chat.getSender().equals(fuser.getUid())) {

                holder.show_message.setVisibility(View.GONE);
                holder.rigth_image.setVisibility(View.VISIBLE);
                Picasso.get().load(chat.getUrl()).into(holder.rigth_image);

             //   holder.imageView_right!!.visibility=View.VISIBLE
               // Picasso.get().load(chat.getUrl()).into(holder.imageView_right)
                //holder.imageView_right!!.setOnClickListener(View.OnClickListener {



                }

            else if(!chat.getSender().equals(fuser.getUid()))
            {
                holder.show_message.setVisibility(View.GONE);
                holder.left_image.setVisibility(View.VISIBLE);
                Picasso.get().load(chat.getUrl()).into(holder.left_image);
            }
        }

        else if(chat.getMessage().contains("https://firebasestorage.googleapis.com"))
        {
            if (chat.getSender().equals(fuser.getUid())) {

                holder.show_message.setVisibility(View.GONE);

                holder.rigth_image.setVisibility(View.VISIBLE);
                holder.rigth_image.setImageResource(R.drawable.ic_baseline_file_copy_24);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent intent=new Intent(Intent.ACTION_VIEW , Uri.parse(mChat.get(position).getMessage()));

                        Log.d("pdfffffffff", Uri.parse(mChat.get(position).getMessage()).toString());
                        mContext.startActivity(intent);
                    }
                });

            }

            else if(!chat.getSender().equals(fuser.getUid()))
            {

                holder.show_message.setVisibility(View.GONE);
                holder.left_image.setVisibility(View.VISIBLE);
                holder.left_image.setImageResource(R.drawable.ic_baseline_file_copy_24);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent=new Intent(Intent.ACTION_VIEW , Uri.parse(mChat.get(position).getMessage()));

                        Log.d("pdfffffffff", Uri.parse(mChat.get(position).getMessage()).toString());

                        //   Intent intent=new Intent(Intent.ACTION_VIEW , Uri.parse(mChat.get(position).getMessage()));
                        mContext.startActivity(intent);
                    }
                });

            }
        }

        else
        {

            holder.show_message.setText(chat.getMessage());



        }



        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(android.view.View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public android.widget.TextView show_message;
        public ImageView profile_image,rigth_image,left_image;
        public android.widget.TextView txt_seen;


        public ViewHolder(android.view.View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            rigth_image=itemView.findViewById(R.id.right_image_view);
            left_image=itemView.findViewById(R.id.left_image_view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}