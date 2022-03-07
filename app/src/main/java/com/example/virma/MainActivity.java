package com.example.virma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnConnect = (Button) findViewById(R.id.buttonLogin);
        btnConnect.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                openConnectActivity();
            }
        });
    }

    public void openConnectActivity(){
        startActivity(new Intent(this, ConnectActivity.class));
    }
}