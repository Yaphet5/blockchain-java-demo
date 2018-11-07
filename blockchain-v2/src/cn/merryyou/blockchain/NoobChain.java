package cn.merryyou.blockchain;

import java.math.BigInteger;
import java.util.Random;

import cn.merryyou.blockchain.utils.JsonUtil;
//import bcp.xianmen;
import util.pp_para;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;




/**
 * Created on 2018/10/20.
 *
 * @author DragonW 
 * @since 1.0
 */
public class NoobChain {

/*	public class pp_para
	{
		BigInteger n;
		BigInteger k;
		BigInteger g;
		BigInteger n2;
	}
	*/
	
    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();

    public static int difficulty = 5;
    public static BigInteger minimumTransaction = new BigInteger("0");
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    static int bitlength=1024;

	static BigInteger a;
	static BigInteger b;
// 	xianmen s=new xianmen();//Ciphertexts' trapdoor: a and b 
	public static BigInteger pk;
	public static BigInteger sk;
    public static void KetGeneration(pp_para PP)
	{
		BigInteger a= new BigInteger(2*bitlength,new Random());
		a=a.mod(PP.n2);
		sk=a;
		System.out.println("\n----------Generate new <sk,pk> pair.---------\n");
		System.out.println("sk is "+a);
		BigInteger h=PP.g.modPow(sk, PP.n2);
		pk=h;
		System.out.println("pk is "+h+"\n");
			
	}
	public static void Encryption(BigInteger pk,BigInteger m,pp_para PP)
	{	
		BigInteger tmp=new BigInteger("1");
		BigInteger r=new BigInteger(2*bitlength,new Random());//
		a=PP.g.modPow(r, PP.n2);

		tmp=m.multiply(PP.n);
		tmp=tmp.add(BigInteger.ONE);
		b=pk.modPow(r,PP.n2);
		b=b.multiply(tmp);
		b=b.mod(PP.n2);

		System.out.println("("+a+", "+b+")");
	}
	public static void Decryption(BigInteger sk,pp_para PP,BigInteger a,BigInteger b)
	{
//		System.out.println("begin decryption!");
		BigInteger tmp=new BigInteger("1");
		BigInteger N2=PP.n2;
		tmp=a.modInverse(N2);
		tmp=tmp.modPow(sk, N2);
		tmp=tmp.multiply(b);
		tmp=tmp.subtract(BigInteger.ONE);
		tmp=tmp.mod(N2);
//		System.out.println("tmp1 is "+tmp);
//		System.out.println("n2 is "+N2);
		/*if(tmp.remainder(PP.n)!=constant0)
		{
			System.out.println("Error in decryption!\n");
			return;
		}*/
		BigInteger c = new BigInteger("0");
		c=tmp.divide(PP.n);
		System.out.println("The corresponding plaintext is "+c+".");
	}
    
    
    public static void main(String[] args) throws ClassNotFoundException {
  
    	pp_para PP = new pp_para();
    	try {
            //创建Socket对象
            Socket socket=new Socket("localhost",8889);
            System.out.println("\n----------Establish a connection with the server.---------\n");
//            System.out.println(socket.isConnected()?"chengong":"shibai");
            
            //根据输入输出流和服务端连接
//            OutputStream outputStream=socket.getOutputStream();//获取一个输出流，向服务端发送信息
//            PrintWriter printWriter=new PrintWriter(outputStream);//将输出流包装成打印流
//            printWriter.print("服务端你好，我是客户1");
//            printWriter.flush();
//            socket.shutdownOutput();//关闭输出流        
//            
//            InputStream inputStream=socket.getInputStream();//获取一个输入流，接收服务端的信息
//            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);//包装成字符流，提高效率
//            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);//缓冲区
//            String info="";
//            String temp=null;//临时变量
//            while((temp=bufferedReader.readLine())!=null){
//                info+=temp;
//                System.out.println("客户端接收服务端发送信息："+info);
//            }
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject("服务端你好，我是客户1");
            oos.flush();
            
            ObjectInputStream in=new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            
            PP = (pp_para) in.readObject();
            System.out.println("\n----------Security parameters PP have been received！----------");
      
/*			try {
				pp_para PP=(pp_para)in.readObject();
				System.out.println("\n-------------PP.n is: " + PP.n);
				System.out.println("\n-------------PP.g is: " + PP.g);
				System.out.println("\n-------------PP.k is: " + PP.k);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
//			System.out.println(pp.name);
//			System.out.println(pp.n);
			socket.close();
            
            //关闭相对应的资源
//            is.close();
//            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    	KetGeneration(PP);
//    	BigInteger m=new BigInteger("11111111111111111");
//    	Encryption(pk,m,PP);
//    	Decryption(sk,PP,a,b);
    	
    	
    	//add our blocks to the blockchain ArrayList:
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider

        //Create wallets:
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        //create genesis transaction, which sends 100 NoobCoin to walletA:
        BigInteger genesisBalance = new BigInteger("100");
//        Encryption(pk,genesisBalance,PP);
//        BigInteger genesisBalance2 = new BigInteger("40");
//        Encryption(pk,genesisBalance2,PP);
//        BigInteger genesisBalance3 = new BigInteger("60");
//        Encryption(pk,genesisBalance3,PP);
//        
        
        
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, genesisBalance, null);
        genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction
        genesisTransaction.transactionId = "0"; //manually set the transaction id
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.

        System.out.println("\nCreating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //testing
        Block block1 = new Block(genesis.hash);
//      System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA's balance is: ");
        Encryption(pk,walletA.getBalance(),PP);
        Decryption(sk,PP,a,b);
        
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        BigInteger block1Send = new BigInteger("40");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, block1Send));
        addBlock(block1);
        
        
        
//        System.out.println(JsonUtil.toJson(block1));
        
        
//        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//        System.out.println("WalletB's balance is: " + walletB.getBalance());
        System.out.println("\nWalletA's balance is: ");
        Encryption(pk,walletA.getBalance(),PP);
        Decryption(sk,PP,a,b);
        System.out.println("\nWalletB's balance is: ");
        Encryption(pk,walletB.getBalance(),PP);
        Decryption(sk,PP,a,b);

        
        Block block2 = new Block(block1.hash);
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        BigInteger block2Send = new BigInteger("1000");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, block2Send));
        addBlock(block2);
//        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//        System.out.println("WalletB's balance is: " + walletB.getBalance());
        System.out.println("\nWalletA's balance is: ");
        Encryption(pk,walletA.getBalance(),PP);
        Decryption(sk,PP,a,b);
        System.out.println("\nWalletB's balance is: ");
        Encryption(pk,walletB.getBalance(),PP);
        Decryption(sk,PP,a,b);

        
//        System.out.println(JsonUtil.toJson(block2));
        
        
        Block block3 = new Block(block1.hash);
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        BigInteger block3Send = new BigInteger("20");
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, block3Send));
//        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//        System.out.println("WalletB's balance is: " + walletB.getBalance());
        System.out.println("\nWalletA's balance is: ");
        Encryption(pk,walletA.getBalance(),PP);
        Decryption(sk,PP,a,b);
        System.out.println("\nWalletB's balance is: ");
        Encryption(pk,walletB.getBalance(),PP);
        Decryption(sk,PP,a,b);

        System.out.println(JsonUtil.toJson(block3));
        
        isChainValid();

    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("#Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            //check if hash is solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            //loop thru blockchains transactions:
            TransactionOutput tempOutput;
            for(int t=0; t <currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if(!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for(TransactionInput input: currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }
            }

        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}
