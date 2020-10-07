package com.emanmustafa.chat_app.group;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emanmustafa.chat_app.Adapter.GroupChatAdapter;
import com.emanmustafa.chat_app.Fragments.APIService;
import com.emanmustafa.chat_app.MainActivity;
import com.emanmustafa.chat_app.MessageActivity;
import com.emanmustafa.chat_app.Model.GroupChat;
import com.emanmustafa.chat_app.Model.User;
import com.emanmustafa.chat_app.Model.UserJoining;
import com.emanmustafa.chat_app.Notifications.Client;
import com.emanmustafa.chat_app.Notifications.Data;
import com.emanmustafa.chat_app.Notifications.MyResponse;
import com.emanmustafa.chat_app.Notifications.Sender;
import com.emanmustafa.chat_app.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class groupchatactivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    private String checher = "", myUrl = "";
    private Uri fileUri;
    private StorageTask uploadTask;

    DatabaseReference reference;

    TextView txt_gone;

    FirebaseUser fuser;

    private Toolbar m;
    private ImageButton sendmessagebutton,attach_image_file;
    private EditText usermessageinput;
    private ScrollView scroll;
    private TextView displaytextmessage;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef,GroupNameRef,GroupMessageKeyRef;
    private String currentGroupName,currentUserID, currentUserName, currentDate,currentTime;

    RecyclerView recyclerView;
    GroupChatAdapter groupChatAdapter;


    Intent intent;
    String userid;
    APIService apiService;
    boolean notify = false;
    Typeface typeface;

    List<GroupChat> mchat=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_groupchatactivity);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

       // linearLayout=findViewById(R.id.mylinearlayout);
        txt_gone=findViewById(R.id.txt_gone);


        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        intent = getIntent();
        userid = intent.getStringExtra("userid");
        mAuth= FirebaseAuth.getInstance();
        RelativeLayout relativeLayout=findViewById(R.id.bottom);


        if(!mAuth.getCurrentUser().getEmail().equals("97tmimi@gmail.com")) {

            relativeLayout.setVisibility(View.GONE);
            txt_gone.setVisibility(View.VISIBLE);
        }
        else

        {

           relativeLayout.setVisibility(View.VISIBLE);
           txt_gone.setVisibility(View.GONE);
        }



        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Group").child(currentGroupName);


        Initializefields();
        GetUserInfo();



        fuser = FirebaseAuth.getInstance().getCurrentUser();

        attach_image_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CharSequence options[] = new CharSequence[]
                        {
                                "صورة"
                                , "ملف pdf"
                                , "Word ملف"
                        };

                AlertDialog.Builder builder = new AlertDialog.Builder(groupchatactivity.this);
                builder.setTitle("حدد");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {

                            checher = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Select Image"), 40);

                        }
                        if (i == 1) {

                            checher = "pdf";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent, "Select Pdf"), 40);


                        }

                        if (i == 2) {

                            checher = "docx";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent, "Select word"), 40);


                        }

                    }
                });

                builder.show();
              /*  notify=true;
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent , "التقط صورة") , 418);

               */
            }
        });
        sendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                notify=true;

                SaveMessageInfoToDatabase(fuser.getUid(), userid);

                usermessageinput.setText("");
              //  scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });


    }




  @Override
    protected void onStart() {

        readMessage();
        super.onStart();
    }



    private void SaveMessageInfoToDatabase(String sender, final String receiver)
    {
        notify=true;
        final String message = usermessageinput.getText().toString();
        String messageKey = GroupNameRef.push().getKey();

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this,"اكتب رسالة أولا من فضلك..", Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar calForData = Calendar.getInstance();
            SimpleDateFormat currentDataFormat = new SimpleDateFormat("MMM dd ,YYYY");
            currentDate = currentDataFormat.format(calForData.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

        //    HashMap<String, Object> groupMessageKey = new HashMap<>();
         //   GroupNameRef.updateChildren(groupMessageKey);
           // GroupMessageKeyRef = GroupNameRef.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("username", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            messageInfoMap.put("id", messageKey);


            GroupNameRef.push().setValue(messageInfoMap);

        }


            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());


          reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (notify) {
                        sendNotifiaction(user.getUsername(), message);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }


    private void GetUserInfo() {
        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child("username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Initializefields() {
        m=(Toolbar)findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(m);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        Typeface type1=Typeface.createFromAsset( getAssets(), "font_arabic.ttf");
        Typeface type2=Typeface.createFromAsset(getAssets(),"font.ttf" );
        TextView toolbar_title=findViewById(R.id.toolbar_title);
        toolbar_title.setText(currentGroupName);
        m.setNavigationOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                // and this

                finish();
            }
        });
       // toolbar_title.setTypeface(typeface_moharrambold);
        toolbar_title.setTypeface(type1);
      //  getSupportActionBar().setTitle(currentGroupName);
        sendmessagebutton=(ImageButton)findViewById(R.id.sendmessagebutton);
        attach_image_file=(ImageButton)findViewById(R.id.attach_image_file);

        usermessageinput=(EditText)findViewById(R.id.inputgroupmessage);


    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext())
        {



            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();


          //  scroll.fullScroll(ScrollView.FOCUS_DOWN);


        }
    }

    private void sendNotifiaction( final String username, final String message){

       DatabaseReference tokenGroups=FirebaseDatabase.getInstance().getReference().child("Groups")
               .child(currentGroupName).child("User Joining");
       tokenGroups.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               for(DataSnapshot snapshot: dataSnapshot.getChildren())
               {
                   UserJoining userJoining=snapshot.getValue(UserJoining.class);
                   String id_recive=userJoining.getUid_user();
                   Log.e("recivierrrrrr" , id_recive +"\n");
                   String tokenAdmin="cqaeHGU8v9A:APA91bHfonxQDk8IZjcVNc8SNtHepsX-qcnibOmAUNmaUUjNUPhtgXVoAUhaf_cjdSyFN5zQvsP37zXgxSkfFMVDQOm8LnqXPWaY-T7RC31HooDOHMYpP6-feGqx-9RAkVnfqnt3KH9V";
                   Data data = new Data(fuser.getUid(), R.drawable.ic_baseline_notifications_24, username+": "+message, "رسالة جديدة",
                          id_recive);

                   Sender sender = new Sender(data, tokenAdmin);
                   Log.e("sender", sender.toString());
                   apiService.sendNotification(sender)
                           .enqueue(new Callback<MyResponse>() {
                               @Override
                               public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                   Log.e(getClass().getName() + "response" , response.toString());
                                   if (response.code() == 200){
                                       if (response.body().success != 1){

                                           Toast.makeText(groupchatactivity.this, "فشل التحميل!", Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               }

                               @Override
                               public void onFailure(Call<MyResponse> call, Throwable t) {

                               }
                           });

               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 40 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            final FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("يتم التحميل الآن.. أرجو الانتظار..");
            progressDialog.show();

            fileUri = data.getData();
            if (!checher.equals("image")) {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Documents Files");
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                final String messageid = ref.push().getKey();
                final StorageReference filePath = storageReference.child(messageid + "." + checher);
                filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final Uri downloadUri = uri;
                                Calendar calForData = Calendar.getInstance();
                                SimpleDateFormat currentDataFormat = new SimpleDateFormat("MMM dd ,YYYY");
                                currentDate = currentDataFormat.format(calForData.getTime());

                                Calendar calForTime = Calendar.getInstance();
                                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                                currentTime = currentTimeFormat.format(calForTime.getTime());

                                HashMap messageHashMap = new HashMap();

                                messageHashMap.put("message", "مستند");
                                messageHashMap.put("user_admin",currentUserName);
                                messageHashMap.put("url", downloadUri.toString());
                                messageHashMap.put("date", currentDate);
                                messageHashMap.put("time", currentTime);


                                ref.child("Group").child(currentGroupName).push().setValue(messageHashMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();

                                                    Toast.makeText(groupchatactivity.this, "تم التحميل بنجاح!", Toast.LENGTH_SHORT).show();

                                                } else {

                                                    progressDialog.dismiss();
                                                    Toast.makeText(groupchatactivity.this, "فشل التحميل!", Toast.LENGTH_SHORT).show();


                                                }
                                            }
                                        });
                            }

                        });
                    }
                });
            } else if (checher.equals("image")) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                final String messageid = ref.push().getKey();
                final StorageReference filePath = storageReference.child(messageid + "." + checher);
                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }


                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            HashMap messageHashMap = new HashMap();

                            Calendar calForData = Calendar.getInstance();
                            SimpleDateFormat currentDataFormat = new SimpleDateFormat("MMM dd ,YYYY");
                            currentDate = currentDataFormat.format(calForData.getTime());

                            Calendar calForTime = Calendar.getInstance();
                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                            currentTime = currentTimeFormat.format(calForTime.getTime());

                            messageHashMap.put("message", "أرسل لك صورة");
                            messageHashMap.put("user_admin",currentUserName);
                            messageHashMap.put("url", myUrl);
                            messageHashMap.put("date", currentDate);
                            messageHashMap.put("time", currentTime);


                            ref.child("Group").child(currentGroupName).push().setValue(messageHashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();

                                                Toast.makeText(groupchatactivity.this, "تم التحميل بنجاح!", Toast.LENGTH_SHORT).show();

                                            } else {

                                                progressDialog.dismiss();
                                                Toast.makeText(groupchatactivity.this, "فشل التحميل!", Toast.LENGTH_SHORT).show();


                                            }
                                        }
                                    });


                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "لم يتم تحديد صورة", Toast.LENGTH_SHORT).show();

            }

        }
    }

    void readMessage()
    {

        GroupNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              mchat.clear();


                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {

                    GroupChat groupChat=snapshot.getValue(GroupChat.class);
                    mchat.add(groupChat);
                    groupChatAdapter=new GroupChatAdapter(getApplicationContext() , mchat);
                    recyclerView.setAdapter(groupChatAdapter);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
