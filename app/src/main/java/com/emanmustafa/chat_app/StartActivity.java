package com.emanmustafa.chat_app;

import android.content.Intent;

import android.graphics.Typeface;
import android.widget.Button;
import android.widget.TextView;

import com.emanmustafa.chat_app.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    TextView txt1,txt2;

    Button login, register;

    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //check if user is null
        if(firebaseUser != null)
        {
            if(firebaseUser.getEmail().equals("97tmimi@gmail.com")){
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            else
            {
                Intent intent = new Intent(StartActivity.this, UserActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        Typeface typeface_moharrambold=Typeface.createFromAsset(getAssets() , "moharrambold.ttf");

        txt1=findViewById(R.id.textView);
        txt2=findViewById(R.id.textView2);


        login = findViewById(R.id.login);

        register = findViewById(R.id.register);


        login.setTypeface(typeface_moharrambold);
        register.setTypeface(typeface_moharrambold);
        login.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });
    }
}
