package com.emanmustafa.chat_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emanmustafa.chat_app.Model.Chat;
import com.emanmustafa.chat_app.Model.GroupChat;
import com.emanmustafa.chat_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder> {

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private java.util.List<GroupChat> mChat;


    FirebaseUser fuser;

    public GroupChatAdapter(Context mContext, java.util.List<GroupChat> mChat){
        this.mChat = mChat;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public GroupChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.right_group_chat, parent, false);
        return new GroupChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupChatAdapter.ViewHolder holder, final int position) {


        FirebaseAuth mAuth=FirebaseAuth.getInstance();



        GroupChat chat=mChat.get(position);



        if(chat.getMessage().equals("أرسل لك صورة") && !chat.getUrl().equals("")) {


            holder.txt_user.setText(chat.getUser_admin());
                holder.groupchattextdisplay.setVisibility(View.GONE);
                holder.left_image.setVisibility(View.VISIBLE);
            holder.txt_date.setText(chat.getTime());
                Picasso.get().load(chat.getUrl()).into(holder.left_image);
        }


        else if(chat.getMessage().equals( "مستند") && !chat.getUrl().equals(""))
        {


            holder.txt_user.setText(chat.getUser_admin());
            holder.groupchattextdisplay.setVisibility(View.GONE);
            holder.left_image.setVisibility(View.GONE);
            holder.left_image_attach.setVisibility(View.VISIBLE);
            holder.txt_date.setText(chat.getTime());
             holder.left_image_attach.setImageResource(R.drawable.ic_baseline_file_copy_24);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent intent=new Intent(Intent.ACTION_VIEW , Uri.parse(mChat.get(position).getUrl()));

                        Log.d("pdfffffffff", Uri.parse(mChat.get(position).getMessage()).toString());
                        mContext.startActivity(intent);
                    }
                });

            }



        else
        {


            holder.groupchattextdisplay.setText(chat.getMessage());
            holder.txt_date.setText(chat.getTime());
            holder.txt_user.setText("أحمد التميمي (صحفي)");

//"أحمد التميمي (صحفي)"

        }






    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        TextView groupchattextdisplay, txt_date,txt_user,txt_gone;
        ImageView left_image,left_image_attach;



        public ViewHolder(View itemView) {
            super(itemView);

            txt_gone=itemView.findViewById(R.id.txt_gone);
            Typeface typeface_moharrambold=Typeface.createFromAsset(mContext.getAssets() , "moharrambold.ttf");
            Typeface type1=Typeface.createFromAsset(mContext.getAssets(), "font_arabic.ttf");
            Typeface type3=Typeface.createFromAsset(mContext.getAssets(), "fontarabic2.ttf");

            Typeface type2=Typeface.createFromAsset(mContext.getAssets(),"font.ttf" );


            groupchattextdisplay = itemView.findViewById(R.id.groupchattextdisplay);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_date.setTypeface(type1);
            txt_user = itemView.findViewById(R.id.txt_admin);
           txt_user.setTypeface(type1);

           left_image=itemView.findViewById(R.id.left_image_view);
            left_image_attach=itemView.findViewById(R.id.left_image_attach);

        }
    }

}