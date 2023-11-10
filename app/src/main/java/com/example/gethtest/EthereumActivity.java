package com.example.gethtest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.bouncycastle.util.encoders.Hex;
import org.ethereum.geth.Account;
import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.Context;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Geth;
import org.ethereum.geth.Header;
import org.ethereum.geth.KeyStore;
import org.ethereum.geth.NewHeadHandler;
import org.ethereum.geth.Node;
import org.ethereum.geth.Subscription;
import org.ethereum.geth.Transaction;
import org.ethereum.geth.Transactions;
import org.web3j.abi.datatypes.primitive.Long;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import jnr.ffi.annotations.LongLong;

public class EthereumActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Context context = new Context();
    KeyStore ks;
    String datadir;
    EthereumClient ethereumClient;
    Node node;
    Transactions pendingTransactions;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ethereum);

        // Geth node data 저장 경로
        datadir = this.getFilesDir().toString();

        ks = new KeyStore(datadir + "/keystore", Geth.LightScryptN, Geth.LightScryptP);
        System.out.println("=== CREATE KeyStore Instance ===");

        // ethereumClient 설정
        if (NodeData.getNode() != null){
            try {
                node = NodeData.getNode();
                ethereumClient = NodeData.getEthereumClient();
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

        Button sendTxsButton = (Button) findViewById(R.id.ether_transfer_btn) ;
        sendTxsButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    long id = accountsSpinner.getSelectedItemId();
                    TextView toAddress = (TextView) findViewById(R.id.ether_transfer_address);
                    TextView value = (TextView) findViewById(R.id.ether_transfer_value);
                    BigInteger bigInt = new BigInteger(value.getText().toString());
                    signOnTransaction(id, (String) toAddress.getText().toString(), new BigInt(bigInt.longValue()) );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) ;


        Button erc20Btn = (Button) findViewById(R.id.erc20_btn);
        erc20Btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Erc20Activity.class);
                startActivity(intent);
            }
        });

        Button nftBtn = (Button) findViewById(R.id.nft_btn);
        nftBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NFTActivity.class);
                startActivity(intent);
            }
        });
    }
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    private static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    private void signOnTransaction(long _from, String _to, BigInt _value) throws Exception {
        // Create a new account to sign transactions with
//        Account signer = null;
//        for (int i = 0; i < ks.getAccounts().size(); i++){
//            if(ks.getAccounts().get(i).getAddress().toString().equals(_from)){
//                signer = ks.getAccounts().get(i);
//                break;
//            }
//        }
        Account signer = ks.getAccounts().get(_from);
        if (signer == null) throw new NullPointerException("Signer is null");

        long nonce = ethereumClient.getPendingNonceAt(context, signer.getAddress());
        System.out.println(nonce);
        Transaction tx = new Transaction(
                nonce, new Address(_to),
                _value, 21000, new BigInt(1000000000), null); // Random empty transaction
        BigInt chain = new BigInt(8192021); // Chain identifier of the main net

        // Sign a transaction with a single authorization
//        Transaction signed = ks.signTxPassphrase(signer, "1234", tx, chain);
//        System.out.println(signed.encodeJSON());
//        System.out.println(signed.string());
//        System.out.println(signed.getHash().toString());
        Transaction txJson = new Transaction("{\n" +
                "        \"nonce\": \"0x2031\",\n" +
                "        \"gasPrice\": \"0x3b9aca00\",\n" +
                "        \"gas\": \"0xc350\",\n" +
                "        \"to\": \"0x458dede45b77c8523a6fe70ee2fbc9b917bed9c4\",\n" +
                "        \"value\": \"0x0\",\n" +
                "        \"input\": \"0xa9059cbb000000000000000000000000ae13b18623f3bdc2d7262729fb37e2c32760e9fa0000000000000000000000000000000000000000000000000000000000000001\",\n" +
                "        \"v\": \"0x0\",\n" +
                "        \"r\": \"0x0\",\n" +
                "        \"s\": \"0x0\"\n" +
                "    }");
        Transaction signed = ks.signTxPassphrase(signer, "1234", txJson, chain);
        // transaction 형식
//        {"type":"0x0","nonce":"0x2030","gasPrice":"0x3b9aca00","maxPriorityFeePerGas":null,"maxFeePerGas":null,"gas":"0x5208","value":"0x158e460913d00000","input":"0x","v":"0xfa004e","r":"0xbca15021022956e8f7cf0300296064d24f5f73d09678b944cd59f32aa27aa37e","s":"0x68ae083055ba961ba0ce117c87fe18bae5baf45aa32b0ebfd9e0a06ee6d262b9","to":"0xae13b18623f3bdc2d7262729fb37e2c32760e9fa","hash":"0x0f357b3dc4e8465f4007717e66339d5caf6b8ca8a650339bff4c6fd8ef20d1ad"}
        System.out.println(bytesToHex(signed.encodeRLP()));
//        ethereumClient.sendTransaction(context, signed);
        long pendingTxsCount = ethereumClient.getPendingTransactionCount(context);

        Toast toast = Toast.makeText(this.getApplicationContext(), "pending txs: " + pendingTxsCount, Toast.LENGTH_LONG);
        toast.show();

        // Sign a transaction with multiple manually cancelled authorizations
//        ks.unlock(signer, "Signer password");
//        signed = ks.signTx(signer, tx, chain);
//        ks.lock(signer.getAddress());

        // Sign a transaction with multiple automatically cancelled authorizations
//        ks.timedUnlock(signer, "Signer password", 1000000000);
//        signed = ks.signTx(signer, tx, chain);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            TextView textView = (TextView) findViewById(R.id.ether_balance);
            Address address = ks.getAccounts().get(i).getAddress();
            System.out.println(address);
            BigInt balance = ethereumClient.getBalanceAt(context, address, -1);
            String accountInfos = balance +"WEI";
            textView.setText(accountInfos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
