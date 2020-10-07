package com.emanmustafa.chat_app.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.emanmustafa.chat_app.Model.GroupChat;
import com.emanmustafa.chat_app.Model.UserJoining;
import com.emanmustafa.chat_app.R;
import com.emanmustafa.chat_app.group.groupchatactivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.Buffer;
import java.util.List;

public class GroupChatAFragmentdapter extends RecyclerView.Adapter<GroupChatAFragmentdapter.ViewHolder> {


   AlertDialog.Builder builder;

    private Context mContext;
        private List<String> mChat;
    FirebaseAuth mAuth;

    FirebaseUser fuser;

    public GroupChatAFragmentdapter(Context mContext, List<String> mChat){
        this.mChat = mChat;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public GroupChatAFragmentdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.name_group_card, parent, false);
        return new GroupChatAFragmentdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupChatAFragmentdapter.ViewHolder holder, final int position) {


        final int[] k = {0};
        mAuth= FirebaseAuth.getInstance();
         String name_group=mChat.get(position);
         holder.txt_name.setText(name_group);

         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                         if(!mAuth.getCurrentUser().getEmail().equals("97tmimi@gmail.com")) {

                             final UserJoining userJoining = new UserJoining();
                             final String uid_user = mAuth.getCurrentUser().getUid();
                             userJoining.setUid_user(uid_user);
                             final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Groups");


                             final String name_group = mChat.get(position).toString();

                             mRef.child(name_group).addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                     Log.d("ddddddd" , "Value:\n"+dataSnapshot.getValue().toString() + "\n");
                                     if (dataSnapshot.getValue().toString().contains("User Joining")) {


                                         mRef.child(name_group).child("User Joining").addChildEventListener(new ChildEventListener() {
                                             @Override
                                             public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                                 Log.d("daaaaaaaataaaaa", dataSnapshot.getKey() + "\n User Id : " + uid_user + "\n");

                                                 if (dataSnapshot.getKey().equals(uid_user)) {
                                                     mRef.child("User Joining").child(uid_user).child("k").addValueEventListener(new ValueEventListener() {
                                                         @Override
                                                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                             Log.d("daaaaaaaataaaaa", snapshot.getKey());
                                                             if (!snapshot.getKey().equals("0")) {
                                                                 Intent groupChatIntent = new Intent(mContext, groupchatactivity.class);
                                                                 groupChatIntent.putExtra("groupName", name_group);
                                                                 mContext.startActivity(groupChatIntent);
                                                             }


                                                         }

                                                         @Override
                                                         public void onCancelled(@NonNull DatabaseError databaseError) {

                                                         }
                                                     });


                                                 }

                                                 else if(!dataSnapshot.getKey().equals(uid_user))
                                                 {
                                                    builder  = new AlertDialog.Builder(mContext);

                                                     builder.setTitle("هل تريد الاشتراك بالقناة؟");
                                                     builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                                         @Override
                                                         public void onClick(DialogInterface dialogInterface, int i) {


                                                             k[0]++;
                                                             userJoining.setK(k[0]);


                                                             mRef.child(name_group).child("User Joining").child(uid_user).setValue(userJoining).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                 @Override
                                                                 public void onComplete(@NonNull Task<Void> task) {

                                                                     if (task.isSuccessful()) {

                                                                         Toast.makeText(mContext, "تم الاشتراك بنجاح", Toast.LENGTH_SHORT).show();

                                                                         Intent groupChatIntent = new Intent(mContext, groupchatactivity.class);
                                                                         groupChatIntent.putExtra("groupName", name_group);
                                                                         mContext.startActivity(groupChatIntent);


                                                                     }
                                                                 }
                                                             });


                                                         }
                                                     });

                                                     builder.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                                                         @Override
                                                         public void onClick(DialogInterface dialogInterface, int i) {

                                                             dialogInterface.dismiss();
                                                         }
                                                     });

                                                     builder.show();
                                                 }

                                             }



                                             @Override
                                             public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                             }

                                             @Override
                                             public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                             }

                                             @Override
                                             public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                             }

                                             @Override
                                             public void onCancelled(@NonNull DatabaseError databaseError) {

                                             }
                                         });



                                     } else if(!dataSnapshot.getValue().toString().contains("User Joining")){
                                          builder = new AlertDialog.Builder(mContext);

                                         builder.setTitle("هل تريد الاشتراك بالقناة؟");
                                         builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                             @Override
                                             public void onClick(DialogInterface dialogInterface, int i) {


                                                 k[0]++;
                                                 userJoining.setK(k[0]);


                                                 mRef.child(name_group).child("User Joining").child(uid_user).setValue(userJoining).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                     @Override
                                                     public void onComplete(@NonNull Task<Void> task) {

                                                         if (task.isSuccessful()) {

                                                             Toast.makeText(mContext, "تم الاشتراك بنجاح", Toast.LENGTH_SHORT).show();

                                                             Intent groupChatIntent = new Intent(mContext, groupchatactivity.class);
                                                             groupChatIntent.putExtra("groupName", name_group);
                                                             mContext.startActivity(groupChatIntent);


                                                         }
                                                     }
                                                 });


                                             }
                                         });

                                         builder.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                                             @Override
                                             public void onClick(DialogInterface dialogInterface, int i) {

                                                 dialogInterface.dismiss();
                                             }
                                         });

                                         builder.show();
                                     }
                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError databaseError) {

                                 }
                             });

                         }
                         else if(mAuth.getCurrentUser().getEmail().equals("97tmimi@gmail.com"))
                         {

                             String currentGroupName = mChat.get(position).toString();
                             Intent groupChatIntent = new Intent(mContext, groupchatactivity.class);
                             groupChatIntent.putExtra("groupName", currentGroupName);

                             mContext.startActivity(groupChatIntent);

                         }

             }
         });







    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_name;

        public ViewHolder(View itemView) {
            super(itemView);


            Typeface typeface_khobarnormal=Typeface.createFromAsset(mContext.getAssets() , "khobarnormal.ttf");
            Typeface typeface_moharrambold=Typeface.createFromAsset(mContext.getAssets() , "moharrambold.ttf");

            txt_name = itemView.findViewById(R.id.txt_name_group);
            txt_name.setTypeface(typeface_moharrambold);



        }
    }

}