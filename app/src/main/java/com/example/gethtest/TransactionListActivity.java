package com.example.gethtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.ethereum.geth.Address;
import org.ethereum.geth.Context;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import org.ethereum.geth.Node;

public class TransactionListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    KeyStore ks;
    String datadir;
    EthereumClient ethereumClient;
    Node node;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        // Geth node data 저장 경로
        datadir = this.getFilesDir().toString();

        ks = new KeyStore(datadir + "/keystore", Geth.LightScryptN, Geth.LightScryptP);
        System.out.println("=== CREATE KeyStore Instance ===");

        // ethereumClient 설정
        if (NodeData.getNode() != null){
            try {
                node = NodeData.getNode();
                ethereumClient = NodeData.getNode().getEthereumClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Spinner 설정
        String[] accounts = new String[(int) ks.getAccounts().size()];

        for(int i = 0; i < ks.getAccounts().size(); i++){
            try {
                accounts[i] = ks.getAccounts().get(i).getAddress().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Spinner accountsSpinner = (Spinner) findViewById(R.id.address_list);
        accountsSpinner.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,accounts);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        accountsSpinner.setAdapter(aa);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            TextView textView = (TextView) findViewById(R.id.txs_list_content);
            Address address = ks.getAccounts().get(i).getAddress();
            System.out.println(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}