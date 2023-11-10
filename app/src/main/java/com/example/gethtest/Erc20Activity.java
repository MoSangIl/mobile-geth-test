package com.example.gethtest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.ethereum.geth.Account;
import org.ethereum.geth.Accounts;
import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.BoundContract;
import org.ethereum.geth.CallMsg;
import org.ethereum.geth.CallOpts;
import org.ethereum.geth.Context;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Geth;
import org.ethereum.geth.Interface;
import org.ethereum.geth.Interfaces;
import org.ethereum.geth.KeyStore;
import org.ethereum.geth.Node;
import org.ethereum.geth.Signer;
import org.ethereum.geth.TransactOpts;
import org.ethereum.geth.Transaction;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Erc20Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    KeyStore ks;
    Context context = new Context();
    Node node;
    EthereumClient ethereumClient;
    String datadir;
    CallOpts callOpts;
    TransactOpts transactOpt;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.erc20);

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

        Button etherBtn = (Button) findViewById(R.id.ether_btn);
        etherBtn.setOnClickListener(new View.OnClickListener() {

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

        Button sendTokenbutton = (Button) findViewById(R.id.erc20_transfer_btn);
        sendTokenbutton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view){
                try {
                    // Contract 가져오기
                    BoundContract boundContract = getContractInterface("0x458dede45B77C8523a6fE70ee2FBC9B917BEd9c4", "OSDCToken.json");
                    System.out.println("Created Contract Interface");

                    // Address 가져오기
                    long id = accountsSpinner.getSelectedItemId();
                    TextView toAddress = (TextView) findViewById(R.id.erc20_transfer_address);
                    TextView value = (TextView) findViewById(R.id.erc20_transfer_value);

                    Address from = ks.getAccounts().get(id).getAddress();
                    Address to = new Address(toAddress.getText().toString());
                    String password = "1234";
                    BigInt amountOfToken = new BigInt(Long.parseLong(value.getText().toString()));

                    // TransactOpts 설정하기
                    BigInt chainId = new BigInt(8192021);

                    setTransactOpts(context, from, password, chainId);
                    CallMsg callMsg = new CallMsg();
                    callMsg.setFrom(from);
                    callMsg.setTo(to);



                    String data = "0xa9059cbb0000000000000000000000004d5a528692ebe30e64f81cd96b5b7cfbd3835d05000000000000000000000000000000000000000000000000000000000000006f";
//                    String data = "{\n" +
//                            "\t\t\t\"inputs\": [\n" +
//                            "\t\t\t\t{\n" +
//                            "\t\t\t\t\t\"internalType\": \"address\",\n" +
//                            "\t\t\t\t\t\"name\": \"to\",\n" +
//                            "\t\t\t\t\t\"type\": \"address\"\n" +
//                            "\t\t\t\t},\n" +
//                            "\t\t\t\t{\n" +
//                            "\t\t\t\t\t\"internalType\": \"uint256\",\n" +
//                            "\t\t\t\t\t\"name\": \"amount\",\n" +
//                            "\t\t\t\t\t\"type\": \"uint256\"\n" +
//                            "\t\t\t\t}\n" +
//                            "\t\t\t],\n" +
//                            "\t\t\t\"name\": \"transfer\",\n" +
//                            "\t\t\t\"outputs\": [\n" +
//                            "\t\t\t\t{\n" +
//                            "\t\t\t\t\t\"internalType\": \"bool\",\n" +
//                            "\t\t\t\t\t\"name\": \"\",\n" +
//                            "\t\t\t\t\t\"type\": \"bool\"\n" +
//                            "\t\t\t\t}\n" +
//                            "\t\t\t],\n" +
//                            "\t\t\t\"stateMutability\": \"nonpayable\",\n" +
//                            "\t\t\t\"type\": \"function\"\n" +
//                            "\t\t}";
//                    String abiJSON = "OSDCToken.json"
//                    InputStream is = getAssets().open(abiJSON);
//                    int fileSize = is.available();
//
//                    byte[] buffer = new byte[fileSize];
//                    is.read(buffer);
//                    is.close();
//
//                    json = new String(buffer, "UTF-8");
//                    abi = new JSONObject(json).getString("abi");
                    callMsg.setData(data.getBytes());
                    callMsg.setValue(amountOfToken);
                    callMsg.setGas(1);
                    callMsg.setGasPrice(new BigInt(1000000000));
                    long est_gas = ethereumClient.estimateGas(context, callMsg);
                    BigInt gas = ethereumClient.suggestGasPrice(context);
                    System.out.println(est_gas);
                    System.out.println(gas);
//                    transferERC20(boundContract, to, amountOfToken);

                    System.out.println("check3");

                }catch (IOException ex) {
                    ex.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private BoundContract getContractInterface(String _tokenAddress, String abiJSON) throws Exception {
        // 토큰 주소: 0x7Ac375f1A454898F33C7BE7691C8BC292E73Cca6
        Address tokenAddress = new Address(_tokenAddress);
        // abi 필요함
        String json = "";
        String abi = "";

        InputStream is = getAssets().open(abiJSON);
        int fileSize = is.available();

        byte[] buffer = new byte[fileSize];
        is.read(buffer);
        is.close();

        json = new String(buffer, "UTF-8");
        abi = new JSONObject(json).getString("abi");

        System.out.println("-------ABI String---------");
        System.out.println(abi);


        return Geth.bindContract(tokenAddress, abi, ethereumClient);
    }

    private CallOpts setCallOpts(Context _context, Address _from, Long _gasLimit) {
        callOpts = Geth.newCallOpts();
        callOpts.setContext(_context);
        callOpts.setFrom(_from);
        callOpts.setGasLimit(_gasLimit);
        return callOpts;
    }

    private BigInt getERC20BalanceOf(BoundContract boundContract, Address _tokenOwner) throws Exception {
        // 반환값 interface 설정
        Interface balance = new Interface();
        balance.setDefaultBigInt();

        Interfaces outs = new Interfaces(1);
        outs.set(0, balance);

        // 실행할 함수 이름
        String method = "balanceOf";

        // 함수 인자 설정
        Interface arg_address = new Interface();
        arg_address.setAddress(_tokenOwner);

        Interfaces args = new Interfaces(1);
        args.set(0, arg_address);

        // 실행
        boundContract.call(
                callOpts,
                outs,
                method,
                args
        );
        System.out.println("------after call balancOf -----");

        // 결과 반환
        return balance.getBigInt();
    }

    private String getERC20Symbol(BoundContract boundContract) throws Exception {
        // 반환값 interface 설정
        Interface symbol = new Interface();
        symbol.setDefaultString();

        Interfaces outs = new Interfaces(1);
        outs.set(0, symbol);

        // 실행할 함수 이름
        String method = "symbol";

        // 함수 인자 설정
        Interfaces args = new Interfaces(0);

        // 실행
        boundContract.call(callOpts, outs, method, args);

        return symbol.getString();
    }

    private void setTransactOpts(Context _context, Address _from, String _password, BigInt _chainId) throws Exception {
        long nonce = ethereumClient.getPendingNonceAt(_context, _from);

        Signer signer = new Signer() {
            @Override
            public Transaction sign(Address address, Transaction transaction) throws Exception {
                Accounts accounts = ks.getAccounts();
                Account target_account = null;
                for (int i = 0; i < accounts.size(); i++){
                    Account account = accounts.get(i);
                    if(account.getAddress().equals(address)) {
                        target_account = account;
                        break;
                    }
                }
                if (target_account == null){
                    throw new NullPointerException("This address, '" + address + "' not existed in keystore");
                }
                Transaction signed = ks.signTxPassphrase(target_account, _password, transaction, _chainId);
                return signed;
            }
        };

        transactOpt = Geth.newTransactOpts();
        transactOpt.setContext(_context);
        transactOpt.setFrom(_from);
        transactOpt.setGasLimit(30384);
        transactOpt.setNonce(nonce);
//        transactOpt.setGasPrice();
        transactOpt.setSigner(signer);
        //                    transactOpt.setValue();
    }

    private void transferERC20(BoundContract boundContract, Address _toAddress, BigInt _amountOfToken) throws Exception {
        // Argument Interface
        Interface to_address = Geth.newInterface();
        to_address.setAddress(_toAddress);
        Interface amount = Geth.newInterface();
        amount.setBigInt(_amountOfToken);

        Interfaces args = new Interfaces(2);
        args.set(0, to_address);
        args.set(1, amount);

        // transaction transact
        boundContract.transact(transactOpt, "transfer", args);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            TextView textView = (TextView) findViewById(R.id.erc20_balance);

            // Contract 가져오기
            BoundContract boundContract = getContractInterface("0x458dede45B77C8523a6fE70ee2FBC9B917BEd9c4", "OSDCToken.json");
            System.out.println("Created Contract Interface");

            // Address 가져오기
            Address address = ks.getAccounts().get(i).getAddress();
            // CallOpts 설정
            Long gasLimit = Long.valueOf(21000);
            setCallOpts(context, address, gasLimit);

            // call 할 메소드 및 인자, 반환값 정의하기
            // 정의하기 어렵긴 하므로, 각 컨트랙트 메소드마다 새로운 함수로 packing
//                    Address tokenOwner = address_deployer;
            BigInt balance = getERC20BalanceOf(boundContract, address);

            // Symbol도 가져오자
            String symbol = getERC20Symbol(boundContract);

            System.out.println(address + " / 잔액: " + balance + " " + symbol);

            String tokenBalance = balance + " " + symbol;
            textView.setText(tokenBalance);
        }catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
