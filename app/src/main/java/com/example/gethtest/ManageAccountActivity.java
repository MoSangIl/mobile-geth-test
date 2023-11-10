package com.example.gethtest;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.ethereum.geth.Account;
import org.ethereum.geth.Context;
import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import org.json.JSONObject;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import java.math.BigInteger;

public class ManageAccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    Context context = new Context();
    KeyStore ks;
    String datadir;
    Spinner accountsSpinner;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_account);


        // Geth node data 저장 경로
        datadir = this.getFilesDir().toString();

        ks = new KeyStore(datadir + "/keystore", Geth.LightScryptN, Geth.LightScryptP);
        System.out.println("=== CREATE KeyStore Instance ===");

        initSpinnerValues();




        Button newAccountButton = (Button) findViewById(R.id.new_account) ;
        newAccountButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText passwordText = (EditText) findViewById(R.id.password_text_create);
                    String password = passwordText.getText().toString();
                    if (password.equals("")) {
                        showToast("암호를 입력하세요");
                        return;
                    }
                    Account newAcc = ks.newAccount(password);
                    System.out.println("=== CREATE New Account ===");
                    System.out.println("ACCOUNT : " + newAcc.getAddress());
                    showToast("Successfully Created " + newAcc.getAddress());

                    passwordText.setText("");
                    // spinner 값 초기화
                    initSpinnerValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) ;

        Button importAccountButton = (Button) findViewById(R.id.import_account) ;
        importAccountButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText privateKeyTest = (EditText) findViewById(R.id.private_key_text);
                    String hexPrivateKey = privateKeyTest.getText().toString();

                    if (hexPrivateKey.equals("")) {
                        showToast("private key를 입력하세요");
                        return;
                    }
                    EditText passwordText = (EditText) findViewById(R.id.password_text_import);
                    String password = passwordText.getText().toString();

                    if (password.equals("")) {
                        showToast("암호를 입력하세요");
                        return;
                    }

                    byte[] privateKey_bytes = decodeUsingBigInteger(hexPrivateKey);
                    System.out.println("input private key: " + hexPrivateKey);

                    Account account = ks.importECDSAKey(privateKey_bytes, password);
                    System.out.println("=== Import Account ===");
                    System.out.println("ACCOUNT : " + account.getAddress());
                    showToast("Successfully Imported " + account.getAddress());

                    privateKeyTest.setText("");
                    passwordText.setText("");
                    // spinner 값 초기화
                    initSpinnerValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button exportAccountKeyfileButton = (Button) findViewById(R.id.export_account_keyfile) ;
        exportAccountKeyfileButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText currentPasswordText = (EditText) findViewById(R.id.password_text_export);
                    String currentPassword = currentPasswordText.getText().toString();

                    if (currentPassword.equals("")) {
                        showToast("현재 암호를 입력하세요");
                        return;
                    }
                    EditText newPasswordText = (EditText) findViewById(R.id.new_password_text_export);
                    String newPassword = newPasswordText.getText().toString();

                    if (newPassword.equals("")) {
                        showToast("새로운 암호를 입력하세요");
                        return;
                    }

                    long id = accountsSpinner.getSelectedItemId();
                    Account account = ks.getAccounts().get(id);

                    byte[] privateKeyJson_bytes = ks.exportKey(account, currentPassword, newPassword);

                    String privateKeyJson = new String(privateKeyJson_bytes);

                    System.out.println("ACCOUNT : " + account.getAddress());
                    System.out.println("=== Export Account keyfile ===");
                    showToast("Successfully Exported " + account.getAddress());

                    currentPasswordText.setText("");
                    newPasswordText.setText("");

                    // alert
                    AlertDialog.Builder builder = new AlertDialog.Builder(ManageAccountActivity.this);

                    builder.setMessage(privateKeyJson)
                            .setTitle("Private Keyfile info\n" + account.getAddress());

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button exportAccountPrivateKeyButton = (Button) findViewById(R.id.export_account_privatekey) ;
        exportAccountPrivateKeyButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText currentPasswordText = (EditText) findViewById(R.id.password_text_export);
                    String currentPassword = currentPasswordText.getText().toString();

                    if (currentPassword.equals("")) {
                        showToast("현재 암호를 입력하세요");
                        return;
                    }
                    EditText newPasswordText = (EditText) findViewById(R.id.new_password_text_export);
                    String newPassword = newPasswordText.getText().toString();

                    if (newPassword.equals("")) {
                        showToast("새로운 암호를 입력하세요");
                        return;
                    }

                    long id = accountsSpinner.getSelectedItemId();
                    Account account = ks.getAccounts().get(id);

                    byte[] privateKeyJson_bytes = ks.exportKey(account, currentPassword, newPassword);

                    String privateKeyJson = new String(privateKeyJson_bytes);

                    JSONObject walletFileJsonObject = new JSONObject(privateKeyJson);
                    JSONObject cryptoJsonObject = walletFileJsonObject.getJSONObject("crypto");
                    JSONObject cipherParamsJsonObject = cryptoJsonObject.getJSONObject("cipherparams");
                    JSONObject kdfParamsJsonObject = cryptoJsonObject.getJSONObject("kdfparams");
                    System.out.println(walletFileJsonObject);

                    // Decrypt privateKeyJson into private key
                    WalletFile walletFile = new WalletFile();
                    WalletFile.Crypto crypto = new WalletFile.Crypto();
                    WalletFile.CipherParams cipherParams = new WalletFile.CipherParams();
                    WalletFile.ScryptKdfParams scryptKdfParams = new WalletFile.ScryptKdfParams();

                    walletFile.setAddress(walletFileJsonObject.getString("address"));
                    crypto.setCipher(cryptoJsonObject.getString("cipher"));
                    crypto.setCiphertext(cryptoJsonObject.getString("ciphertext"));
                    cipherParams.setIv(cipherParamsJsonObject.getString("iv"));
                    crypto.setCipherparams(cipherParams);
                    crypto.setKdf(cryptoJsonObject.getString("kdf")); // Aes128CtrKdf or scrypt
                    scryptKdfParams.setDklen(kdfParamsJsonObject.getInt("dklen"));
                    scryptKdfParams.setN(kdfParamsJsonObject.getInt("n"));
                    scryptKdfParams.setP(kdfParamsJsonObject.getInt("p"));
                    scryptKdfParams.setR(kdfParamsJsonObject.getInt("r"));
                    scryptKdfParams.setSalt(kdfParamsJsonObject.getString("salt"));
                    crypto.setKdfparams(scryptKdfParams);
                    crypto.setMac(cryptoJsonObject.getString("mac"));
                    walletFile.setCrypto(crypto);
                    walletFile.setId(walletFileJsonObject.getString("id"));
                    walletFile.setVersion(walletFileJsonObject.getInt("version"));

                    ECKeyPair result = Wallet.decrypt(newPassword, walletFile);
                    String privateKeyHex = result.getPrivateKey().toString(16);

                    System.out.println("ACCOUNT : " + account.getAddress());
                    System.out.println("=== Export Account private key ===");
                    showToast("Successfully Exported " + account.getAddress());

                    currentPasswordText.setText("");
                    newPasswordText.setText("");

                    // alert
                    AlertDialog.Builder builder = new AlertDialog.Builder(ManageAccountActivity.this);

                    String padding = privateKeyHex.length() < 64 ? String.valueOf((int) Math.pow(10, (64 - privateKeyHex.length()))).substring(1) : "";

                    builder.setMessage("\n privatekey: " + padding + privateKeyHex)
                            .setTitle("Private Key\n" + account.getAddress());

                    System.out.println(privateKeyHex);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button deleteAccountButton = (Button) findViewById(R.id.delete_account) ;
        deleteAccountButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText passwordText = (EditText) findViewById(R.id.password_text_delete);
                    String password = passwordText.getText().toString();

                    if (password.equals("")) {
                        showToast("암호를 입력하세요");
                        return;
                    }

                    long id = accountsSpinner.getSelectedItemId();
                    Account account = ks.getAccounts().get(id);

                    ks.deleteAccount(account, password);

                    System.out.println("=== Delete Account ===");
                    System.out.println("ACCOUNT : " + account.getAddress());
                    showToast("Successfully removed " + account.getAddress());

                    passwordText.setText("");
                    // spinner 값 초기화
                    initSpinnerValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) ;

        Button importAccountWithKeyfileButton = (Button) findViewById(R.id.import_account_with_keyfile) ;
        importAccountWithKeyfileButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    // Pseudo Code for import with Keyfile

                    // Get Keyfile from the android device! (Downloads, Documents.. and so on if possible)

                    // Read keyfile and then Convert into byte array

//                    byte[] keyfileContent = "....";
//                    String currentKey = "current Key.. for imported keyfile";
//                    String newKey = "new key.. for keyfile";

//                    Account account = ks.importKey(keyfileContent, currentKey, newKey); // this will create keyfile on your keystore directory with new password, and return that account!

                    // Init UI ex) spinner in this view
//                    initSpinnerValues();

                    // alert
                    AlertDialog.Builder builder = new AlertDialog.Builder(ManageAccountActivity.this);

                    builder.setMessage("It is not implemented but written in pseudocode.. \n you can see the code on this button ClickListener" )
                            .setTitle("Import Account with Keyfile");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) ;
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private byte[] decodeUsingBigInteger(String hexString) {
        byte[] byteArray = new BigInteger(hexString, 16)
                .toByteArray();
        if (byteArray[0] == 0) {
            byte[] output = new byte[byteArray.length - 1];
            System.arraycopy(
                    byteArray, 1, output,
                    0, output.length);
            return output;
        }
        return byteArray;
    }

    private void initSpinnerValues(){
        // Spinner 설정
        String[] accountsOnSpinner = new String[(int) ks.getAccounts().size()];

        for(int i = 0; i < ks.getAccounts().size(); i++){
            try {
                accountsOnSpinner[i] = ks.getAccounts().get(i).getAddress().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        accountsSpinner = (Spinner) findViewById(R.id.address_list);
        accountsSpinner.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,accountsOnSpinner);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        accountsSpinner.setAdapter(aa);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
