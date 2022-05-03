package com.vidshort.lyrical.video.status.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstActivity extends AppCompatActivity {

    Button btnDifferentAccount, btnSameAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_activity);

        btnDifferentAccount = findViewById(R.id.btnDifferentAccount);
        btnSameAccount = findViewById(R.id.btnSameAccount);

        btnDifferentAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstActivity.this, MainActivity.class));
            }
        });

        btnSameAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstActivity.this, SameAccountActivity.class));
            }
        });
    }
}