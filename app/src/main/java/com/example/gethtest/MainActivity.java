package com.example.gethtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ethereum.geth.Block;
import org.ethereum.geth.Context;
import org.ethereum.geth.Enode;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Geth;
import org.ethereum.geth.Header;
import org.ethereum.geth.KeyStore;
import org.ethereum.geth.NewHeadHandler;
import org.ethereum.geth.Node;
import org.ethereum.geth.NodeConfig;
import org.ethereum.geth.Subscription;
import org.ethereum.geth.Transactions;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Node node;
    KeyStore ks;
    NodeConfig nodeConfig;
    EthereumClient ethereumClient;
    String datadir;
    String[] bootnodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String data = "0xa9059cbb0000000000000000000000004d5a528692ebe30e64f81cd96b5b7cfbd3835d05000000000000000000000000000000000000000000000000000000000000006f";
        String data1 = "{\n" +
                "\t\t\t\"inputs\": [\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"internalType\": \"address\",\n" +
                "\t\t\t\t\t\"name\": \"to\",\n" +
                "\t\t\t\t\t\"type\": \"address\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"internalType\": \"uint256\",\n" +
                "\t\t\t\t\t\"name\": \"amount\",\n" +
                "\t\t\t\t\t\"type\": \"uint256\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t],\n" +
                "\t\t\t\"name\": \"transfer\",\n" +
                "\t\t\t\"outputs\": [\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"internalType\": \"bool\",\n" +
                "\t\t\t\t\t\"name\": \"\",\n" +
                "\t\t\t\t\t\"type\": \"bool\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t],\n" +
                "\t\t\t\"stateMutability\": \"nonpayable\",\n" +
                "\t\t\t\"type\": \"function\"\n" +
                "\t\t}";
        String text = "I am a test";
        System.out.println(text.getBytes());
        int[] bytes = {73,32, 97, 109, 32, 97, 32, 84, 101, 115, 116, 32, 84, 101, 120, 116, 32, 102, 105, 108, 101};
        System.out.println(bytes.toString());
//        byteStream.add(73,32, 97, 109, 32, 97, 32, 84, 101, 115, 116, 32, 84, 101, 120, 116, 32, 102, 105, 108, 101
        System.out.println(data.getBytes());
        System.out.println(data1.getBytes());


        Button etherBtn = findViewById(R.id.ether_btn);
        etherBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EthereumActivity.class);
                startActivity(intent);
            }
        });

        Button nftBtn = findViewById(R.id.nft_btn);
        nftBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NFTActivity.class);
                startActivity(intent);
            }
        });

        Button erc20Btn = findViewById(R.id.erc20_btn);
        erc20Btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Erc20Activity.class);
                startActivity(intent);
            }
        });

        Button manageAccountBtn = findViewById(R.id.manage_account);
        manageAccountBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ManageAccountActivity.class);
                startActivity(intent);
            }
        });

        Button txsListBtn = findViewById(R.id.txs_list_btn);
        txsListBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TransactionListActivity.class);
                startActivity(intent);
            }
        });
        // Geth node data 저장 경로
        datadir = this.getFilesDir().toString();

        ks = new KeyStore(datadir + "/keystore", Geth.LightScryptN, Geth.LightScryptP);

        System.out.println("=== CREATE KeyStore Instance ===");

        readStaticFullnodeIp();

        // Set Bootnodes enodes
        bootnodes = new String[]{
                "\"enode://10ffa537bf258b4f559179cf71fe7a35d729bbca11597351e9811a3db59f39f555eba88996c39756346f96ce4f84ebd874a23d78a050005afa90d01c7240150e@1.229.180.187:30303\""
//                "\"enode://62047fda7f582c0b802a8787247b80dc7da787b0e165c7455d48de8fc9652c272c2226162621800bc3a90d8a021a09b575b8c650295859f13735ddb5cc949870@15.165.48.104:30305\"",
//                "\"enode://f5480ae3210144e53f48933feaef86e85b450cff77774830f56001e798852720027488eaf09b31c5addb3b86bfa71368aaf3c2e2d652ea11ea80247d302d38a6@3.38.147.218:30305\"",
//                "\"enode://9308545609b749d6be7a6a8b58f3ed77241c0dfafb1c5b28ea315f18c4aaf4b7fada4282caaca81f9c69b95df592af3e231e5af51caf98210a3210ec0b492acf@3.35.158.56:30305\"",
//                "\"enode://0eef7d7364c0cf6e043df1ebb3a8b9f0545da5199e5c5b40ca83680273eadc5ddc660db8c530a240594b6f5bc99a7079a4cde5eb32a84172f5464dbf2075d55d@15.165.42.14:30305\""
        };


        // Button Handler 등록 ==============================================================
        Button stopButton = findViewById(R.id.button);
        stopButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    node.close();
                    System.out.println("===== Stop Node =====");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) ;

        Button startButton = findViewById(R.id.button2);
        startButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    readyForStartNode();
                    System.out.println("===== Start Node =====");
                    node.start();

//                    NewHeadHandler newHeadHandler = new NewHeadHandler() {
//                        @Override
//                        public void onError(String s) {
//                            System.out.println("Head Hadnler On Error Callback " + s);
//                        }
//
//                        @Override
//                        public void onNewHead(Header header) {
//                            // Block이 싱크됨
//                            // 이전 블록 수 이후부터 싱크된 블록까지 리스팅
//                            // 해당 블록마다 반복
////                            Block block = new Block(String.valueOf(header.getNumber()));
////                            // 블록의 트랜젝션 리스트 가져옴
////                            Transactions transacionsOfBlock = block.getTransactions();
////
////                            // 트랜젝션 마다 반복
////                            for (int i = 0; i < transacionsOfBlock.size(); i++){
////                                // pedning으로 관리하는 트랜젝션이 있는지 확인
////
////                                try {
//////                                    if (transacionsOfBlock.)
////                                } catch (Exception e) {
////                                    e.printStackTrace();
////                                }
////                                // 있으면, 완료 처리함, peding에서 빼기
////                            }
//
//                                    // 없으면, 패스
//                            System.out.println(header.getNumber());
//                            System.out.println(header.getTxHash());
//                            System.out.println(header.string());
//                            Context context = new Context();
//                            try {
//                                System.out.println(ethereumClient.getPendingTransactionCount(context));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    };
//
//                    Context context = new Context();
//                    ethereumClient.subscribeNewHead(context, newHeadHandler, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) ;

        Button writeFileButton = findViewById(R.id.write_file);
        writeFileButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText numberOfProvidedNodesToUserEditText = findViewById(R.id.number_of_fullnode_to_provide);

                int numberOfFullnodes = bootnodes.length;   // set the number of all fullnodes
                int numberOfProvidedNodesToAUser = Integer.parseInt(numberOfProvidedNodesToUserEditText.getText().toString()); // set the number of fullnodes to provide for user

                // stop if exceed the provided nodes count
                if (numberOfProvidedNodesToAUser > numberOfFullnodes) {
                    Toast.makeText(MainActivity.this, "최대" + numberOfFullnodes + "개 까지 가능", Toast.LENGTH_LONG).show();
                    numberOfProvidedNodesToUserEditText.setText("5");
                    return;
                }

                // init the needed variables
                ArrayList<Integer> randomNodesIdx = new ArrayList<>();
                ArrayList<String> randomNodes = new ArrayList<>();

                // create random number: count = K = numberOfProvidedNodesToAUser <= numberOfFullnodes
                Random random = new Random(System.currentTimeMillis());
                while (randomNodes.size() < numberOfProvidedNodesToAUser){
                    int rand = random.nextInt(numberOfFullnodes);
                    System.out.println(rand);
                    if (!randomNodesIdx.contains(rand)){
                        randomNodesIdx.add(rand);
                        randomNodes.add(bootnodes[rand]);
                    }
                }
                System.out.println(randomNodesIdx.toString());
                System.out.println(randomNodes.toString());

                // Write the static-node.json file
                try {
                    writeFileOnInternalStorage(
                            "static-nodes.json",
                            randomNodes.toString()
                    );

                    readStaticFullnodeIp();
                    numberOfProvidedNodesToUserEditText.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) ;
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    // Node 정보 가져오기
    private void getNodeInfo() {
        long discoveryPort = node.getNodeInfo().getDiscoveryPort();
        long listenerPort = node.getNodeInfo().getListenerPort();
        String enode = node.getNodeInfo().getEnode();
        String name = node.getNodeInfo().getName();

        System.out.println("discovery : " + discoveryPort + "\nlistener : " + listenerPort);
        System.out.println("Enode: " + enode + "\nName: " + name);
    }

    // Node Config 정보 가져오기
    private void getNodeConfigInfo() {
        long networkId = nodeConfig.getEthereumNetworkID();
        boolean enabled = nodeConfig.getEthereumEnabled();
        Enode enode = null;
        try {
            enode = nodeConfig.getBootstrapNodes().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long maxPeers = nodeConfig.getMaxPeers();
        long dbCache = nodeConfig.getEthereumDatabaseCache();
        String genesis = nodeConfig.getEthereumGenesis();
        String netStats = nodeConfig.getEthereumNetStats();
        String pprofAddress = nodeConfig.getPprofAddress();

        System.out.println(
                "=== NetwokrId: " + networkId + "==============\n" +
                        "=== Enabled: " + enabled + "==============\n" +
                        "=== BootNode: " + enode.toString() + "==============\n" +
                        "=== Max Peers: " + maxPeers + "==============\n" +
                        "=== DB Cache: " + dbCache + "==============\n" +
                        "=== Net Stats: " + netStats + "==============\n" +
                        "=== Pprof Address: " + pprofAddress + "==============\n" +
                        "=== Genesis: " + genesis + "==============\n"
        );
    }

    // Node 시작 전 설정
    private void readyForStartNode() throws Exception {
//        Enode bootenode = new Enode("enode://8bd133118b7de04e2b7fdd9e5917b802b888294fca9f5cb387fa4e9261955ba02b5240f29cc5ed016767f7f47611ce57acecf7cd3eb4b2127c63cc403ce5273e@3.37.175.60:30305");
//        Enodes bootenodes = new Enodes();
//        bootenodes.append(bootenode);
//        System.out.println(bootenode.toString());
        nodeConfig = new NodeConfig();
        getNodeConfigInfo();

        // Get Genesis Config & Setting Genesis
        InputStream is = getAssets().open("genesis.json");
        int fileSize = is.available();

        byte[] buffer = new byte[fileSize];
        is.read(buffer);
        is.close();

        String genesis = new String(buffer, StandardCharsets.UTF_8);
        nodeConfig.setEthereumGenesis(genesis);
//        nodeConfig.addBootstrapNode(bootenode);
        nodeConfig.setEthereumNetworkID(8192021);
//        nodeConfig.setMaxPeers(0);
//        nodeConfig.setEthereumEnabled(true);
//        nodeConfig.setEthereumDatabaseCache(16);
//        nodeConfig.setEthereumNetStats("waffle:1234@localhost:8545");
//        nodeConfig.setBootstrapNodes(bootenodes);

        getNodeConfigInfo();

//          node = new Node(datadir + "mainnet", nodeConfig);
//        node = Geth.newNode(datadir, nodeConfig);
        node = new Node(datadir, nodeConfig);
        getNodeInfo();
        NodeData.setNode(node);
        ethereumClient = NodeData.getEthereumClient();
    }

    private void writeFileOnInternalStorage(String sFileName, String sBody) throws IOException {
        File dir = new File(this.getFilesDir(), "GethDroid");
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            File gpxfile = new File(dir, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void readStaticFullnodeIp(){
        TextView fullnodeIPText = findViewById(R.id.fullnode_ip);
        File file = new File(datadir + "/GethDroid", "static-nodes.json");
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            String fileTexts = "";
            while ((line = reader.readLine()) != null){
                fileTexts += line;
            }
            JSONArray nodesList = new JSONArray(fileTexts);

            String staticFullnodeInfo = "현재 static FullNode 개수:" + nodesList.length() + "\n";
            for(int i = 0; i < nodesList.length(); i++){
                String fullNode = nodesList.getString(i);
//                String ip = fullNode.split("@")[1].split(":")[0];
                String enode = fullNode.substring(8, 18);
                staticFullnodeInfo += enode + "\n";
            }
            fullnodeIPText.setText(staticFullnodeInfo);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            node.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

