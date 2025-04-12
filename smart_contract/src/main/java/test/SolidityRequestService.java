/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

/**
 *
 * @author vyyh
 */
 

import contract.SolidityERC721;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
 
import java.math.BigInteger;
 
public class SolidityRequestService {
    private static SolidityRequestService contractUtil = new SolidityRequestService();
    private SolidityRequestService(){
    }
    public static SolidityRequestService getInstance() {
        return contractUtil;
    }
    //copy RPC link from ganache
    private static String netWorkUrl = "HTTP://127.0.0.1:7545";
    //copy an account's private key
    private static String walletKey = "0x1887ae97036460b2793b55b35ba0830699b68ff94994d506d04e0b7fa4403368";
    //deploy contract address
    private static String contractAddress = "0x44021E059871f784E5004B6bd20B2ceBa4Ae93b0";
 
    public static void useContract(){
        try {
            
            Web3j web3 = Web3j.build(new HttpService(netWorkUrl));
            Credentials credentials = Credentials.create(walletKey);
            TransactionManager transactionManager = new RawTransactionManager(web3, credentials,3);
            BigInteger gasPrice = web3.ethGasPrice().send().getGasPrice();
            SolidityERC721 contract = SolidityERC721.load(contractAddress,web3,
                    transactionManager,new StaticGasProvider(gasPrice, Contract.GAS_LIMIT));
//         
 //           RemoteFunctionCall<String> setWord = contract.name();
 //           String send = setWord.send();
 //           System.out.println("name: "+send);
 
 
            RemoteFunctionCall<BigInteger> bigIntegerRemoteFunctionCall = contract.balanceOf("0x1887ae97036460b2793b55b35ba0830699b68ff94994d506d04e0b7fa4403368");
            BigInteger send1 = bigIntegerRemoteFunctionCall.send();
            System.out.println("balance: "+send1);
 
//            RemoteCall<TransactionReceipt> setWord = contract.newGreeting("hello world");
//            TransactionReceipt transactionReceipt = setWord.send();
 //           String transactionHash = transactionReceipt.getTransactionHash();
 //           System.out.println(transactionHash);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
 
    public static void main(String[] args) {
        SolidityRequestService instance = SolidityRequestService.getInstance();
        instance.useContract();
    }


}