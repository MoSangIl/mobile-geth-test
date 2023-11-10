package com.example.gethtest;

import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Node;

class NodeData {
    private static Node node;
    private static EthereumClient ethereumClient;
    public static Node getNode() {return node;}
    public static EthereumClient getEthereumClient() {return ethereumClient;}
    public static void setNode(Node node) throws Exception {
        NodeData.node = node;
        NodeData.ethereumClient = NodeData.node.getEthereumClient();
    }
}
