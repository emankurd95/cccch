package com.emanmustafa.chat_app.group;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emanmustafa.chat_app.Adapter.GroupChatAFragmentdapter;
import com.emanmustafa.chat_app.MainActivity;
import com.emanmustafa.chat_app.Model.UserJoining;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.emanmustafa.chat_app.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class groups extends Fragment {


    Button btn_add;
    FirebaseAuth mAuth;
    private View groupfragmentview;
    private List<String> Listofgroups = new ArrayList<>();
    private DatabaseReference groupref;
   // List<String> list_user_joining=new ArrayList<>();
    RecyclerView recyclerView;
    GroupChatAFragmentdapter groupChatAFragmentdapter;

    public groups() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupfragmentview = inflater.inflate(R.layout.fragment_groups, container, false);
        groupref = FirebaseDatabase.getInstance().getReference().child("Groups");
        Initializefields();
        retrieveanddisplay();

        return groupfragmentview;

}

    private void Initializefields() {

        groupChatAFragmentdapter=new GroupChatAFragmentdapter(getContext() , Listofgroups);
        recyclerView=groupfragmentview.findViewById(R.id.recycler_view_chat_fragmemt);
        GridLayoutManager gridLayout=new GridLayoutManager(getContext() , 2);
       // LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext() , LinearLayoutManager.VERTICAL , false);
        recyclerView.setLayoutManager(gridLayout);
        recyclerView.setAdapter(groupChatAFragmentdapter);





    }

    private void retrieveanddisplay() {
        groupref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set=new HashSet<>();
                Iterator iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                Listofgroups.clear();
                Listofgroups.addAll(set);
                groupChatAFragmentdapter.notifyDataSetChanged();
              //  groupChatAFragmentdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth=FirebaseAuth.getInstance();
        btn_add=getView().findViewById(R.id.btn_add_group);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Requestnewgroup();

            }
        });


        if(!mAuth.getCurrentUser().getEmail().equals("97tmimi@gmail.com")) {

            btn_add.setVisibility(View.GONE);
        }
        else

        {
            btn_add.setVisibility(View.VISIBLE);

        }
    }

    private void Requestnewgroup() {
        androidx.appcompat.app.AlertDialog.Builder builder=new AlertDialog.Builder(getContext(),R.style.AlertDialog);
        builder.setTitle("إنشاء مجموعة جديدة:");
        final EditText groupnamefield=new EditText(getContext());
        builder.setView(groupnamefield);

        builder.setPositiveButton("إنشاء", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupname=groupnamefield.getText().toString();
                if(TextUtils.isEmpty(groupname))
                {
                    Toast.makeText(getContext(),"من فضلك أدخل اسم المجموعة..",Toast.LENGTH_LONG).show();
                }
                else
                {
                    createnewgroup(groupname);
                }
            }


        });
        builder.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.show();


    }

    private void createnewgroup(final String groupname) {


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
        ref.child("Groups").child(groupname).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getContext(),groupname+"تم إنشاء المجموعة بنجاح",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
