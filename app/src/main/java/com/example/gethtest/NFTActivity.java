package com.example.gethtest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class NFTActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nft);

        Button etherBtn = (Button) findViewById(R.id.ether_btn);
        etherBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EthereumActivity.class);
                startActivity(intent);
            }
        });

        Button erc20Btn = (Button) findViewById(R.id.erc20_btn);
        erc20Btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Erc20Activity.class);
                startActivity(intent);
            }
        });
    }
}
