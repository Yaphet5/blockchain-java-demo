import java.math.BigInteger;
import java.util.Random;

import util.pp_para;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created on 2018/10/20.
 *
 * @author DragonW 
 * @since 1.0
 */

public class bcp {
	boolean make=false;
	static int bitlength=1024;
	//public static final BigInteger:BigInteger.ONE;
	int certainty=1;

	/*public class pp_para implements Serializable
	{
		private static final long serialVersionUID = -2049597486247025611L;
		String name="me";
		transient BigInteger n;
		transient BigInteger k;
		transient BigInteger g;
		transient BigInteger n2;
		byte[] nb;
		byte[] kb;
		byte[] gb;
		byte[] n2b;
	    private void writeObject(ObjectOutputStream oos) throws IOException {  
	        oos.defaultWriteObject();
	        nb=n.toByteArray();
	        kb=k.toByteArray();
	        gb=g.toByteArray();
	        n2b=n2.toByteArray(); 
	        System.out.println("session serialized");  
	    }  
	  
	    private void readObject(ObjectInputStream ois) throws IOException,  
	            ClassNotFoundException {
	        ois.defaultReadObject();
	        n=new BigInteger(nb);
	        k=new BigInteger(kb);
	        g=new BigInteger(gb);
	        n2=new BigInteger(n2b);
	        System.out.println("session deserialized");  
	    }  
	}*/
	pp_para PP=new pp_para();

	public class xianmen
	{
		BigInteger a;
	 	BigInteger b;
 	}
 	xianmen s=new xianmen();//Ciphertexts' trapdoor: a and b 
	public  BigInteger pk,sk;
	BigInteger pp;
	BigInteger qq;
	int l;
	BigInteger supermk;
	BigInteger constant0=new BigInteger("0");
	BigInteger constant4=new BigInteger("4");
	BigInteger constant2=new BigInteger("2");
	public bcp(int certainty){
		 BigInteger pp,qq;
		 BigInteger p =BigInteger.ONE;
		 BigInteger q =BigInteger.ONE;
		do{
			do{
				pp=BigInteger.probablePrime(bitlength/2-1, new Random());
				p=(pp.multiply(constant2)).add(BigInteger.ONE);
			}while(p.isProbablePrime(certainty)==false);
			System.out.print("pp and p have been init\n");
			do{
				qq =BigInteger.probablePrime(bitlength/2-1, new Random());
				q=(qq.multiply(constant2)).add(BigInteger.ONE);//p=p+pp*2=2*pp+1
			}while((q.isProbablePrime(certainty)==false));
			PP.n=p.multiply(q);//N=p*q
			l=PP.n.bitLength();
			System.out.print("qq and q have been init\n");;
		}while(l>1024);
	System.out.println("OK");
	PP.n2=PP.n.pow(2);
	System.out.println("N2 is "+PP.n2);
	
	while(!make)
	{
		BigInteger tmp=new BigInteger("2");
		BigInteger g=new BigInteger(bitlength,certainty,new Random());
	
		g=g.subtract(BigInteger.ONE);
		g=g.pow(2);
		g=g.mod(PP.n2);//problem?
		if(g.compareTo(BigInteger.ONE)==0){ 
			make=false;
			continue;
		}
		tmp=g.modPow(PP.n2, p);
		if(tmp.compareTo(BigInteger.ONE)==0){
			make=false;
			continue;
		}
		tmp=g.modPow(p,PP.n2);
		if(tmp.compareTo(BigInteger.ONE)==0){
			make=false;
			continue;
		}
		tmp=g.modPow(pp,PP.n2);
		if(tmp.compareTo(BigInteger.ONE)==0){
			make=false;
			continue;
		}
		tmp=g.modPow(q,PP.n2);
		if(tmp.compareTo(BigInteger.ONE)==0){
			make=false;
			continue;
		}
		tmp=g.modPow(qq,PP.n2);
		if(tmp.compareTo(BigInteger.ONE)==0){
			make=false;
			continue;
		}
		tmp=p.multiply(q);
		tmp=g.modPow(tmp,PP.n2);
	
		if(tmp.compareTo(BigInteger.ONE)==0){
			make=false;
			continue;
		}
		tmp=p.multiply(qq);
		tmp=g.modPow(tmp,PP.n2);
		if(tmp.compareTo(BigInteger.ONE)==0){
			make=false;
			continue;
		}
		tmp=pp.multiply(q);
		tmp=g.modPow(tmp,PP.n2);
		if(tmp.compareTo(BigInteger.ONE)==0){
			make=false;
			continue;
		}
		tmp=pp.multiply(qq);
		tmp=g.modPow(tmp,PP.n2);
		if(tmp.compareTo(BigInteger.ONE)==0){
			make=false;
			continue;
		}
		tmp=q.multiply(qq);
		tmp=g.modPow(tmp,PP.n2);
		if(tmp.compareTo(BigInteger.ONE)==0){
			make=false;
			continue;
		}
		tmp=p.multiply(pp);
		tmp=g.modPow(tmp,PP.n2);
		if(tmp.compareTo(BigInteger.ONE)==0){
			make=false;
			continue;
		}
		tmp=p.multiply(pp);
		tmp=tmp.multiply(q);
		tmp=g.modPow(tmp,PP.n2);
		if(tmp.compareTo(BigInteger.ONE)==0){
			make=false;
			continue;
		}
		tmp=p.multiply(pp);
		tmp=tmp.multiply(qq);
		tmp=g.modPow(tmp,PP.n2);
		if(tmp.compareTo(BigInteger.ONE)==0){
			make=false;
			continue;
			}
		tmp=p.multiply(q);
		tmp=tmp.multiply(qq);
		tmp=g.modPow(tmp,PP.n2);
		if(tmp.compareTo(BigInteger.ONE)==0){
				make=false;
				continue;
		}
		//System.out.println("yes we can");
		tmp=pp.multiply(q);
		tmp=tmp.multiply(qq);
		tmp=g.modPow(tmp,PP.n2);
		if(tmp.compareTo(BigInteger.ONE)==0){
			make=false;
		}
	   	make=true;
		PP.g=g;
		supermk=pp.multiply(qq);
		BigInteger tmp3=PP.g.modPow(supermk, PP.n2);
		tmp3=tmp3.subtract(BigInteger.ONE);
		PP.k=tmp3.divide(PP.n);
		System.out.println("PP.n is "+PP.n);
		System.out.println("PP.k is "+PP.k);
		System.out.println("PP.g is "+PP.g);
		System.out.println("MK is "+supermk);
		}
	}
	
	public void Decryption(BigInteger sk,BigInteger c,pp_para PP,xianmen s)
	{
		System.out.println("begin decryption!");
		BigInteger tmp=new BigInteger("1");
		BigInteger N2=PP.n2;
		tmp=s.a.modInverse(N2);
		tmp=tmp.modPow(sk, N2);
		tmp=tmp.multiply(s.b);
		tmp=tmp.subtract(BigInteger.ONE);
		tmp=tmp.mod(N2);
		System.out.println("tmp1 is "+tmp);
		System.out.println("n2 is "+N2);
		/*if(tmp.remainder(PP.n)!=constant0)
		{
			System.out.println("Error in decryption!\n");
			return;
		}*/
		c=tmp.divide(PP.n);
		System.out.println("c is "+c);
	}
	public void KetGeneration(pp_para PP)
	{
		BigInteger a= new BigInteger(2*bitlength,new Random());
		a=a.mod(PP.n2);
		sk=a;
		System.out.println("sk is "+a);
		BigInteger h=PP.g.modPow(sk, PP.n2);
		pk=h;
		System.out.println("pk is "+h);
			
	}
	public void Encryption(BigInteger pk,BigInteger m,pp_para PP)
	{	
		BigInteger tmp=new BigInteger("1");
		BigInteger r=new BigInteger(2*bitlength,new Random());//
		BigInteger a=PP.g.modPow(r, PP.n2);
		s.a=a;
		tmp=m.multiply(PP.n);
		tmp=tmp.add(BigInteger.ONE);
		BigInteger b=pk.modPow(r,PP.n2);
		b=b.multiply(tmp);
		b=b.mod(PP.n2);
		s.b=b;
		System.out.println("a is "+s.a);
		System.out.println("b is "+s.b);
		
	}
	public void Decryption_mk(pp_para PP,xianmen t,BigInteger pk)
	{
		System.out.println("---------Using MK to decrypt--------");
		BigInteger seita=supermk;
		seita=seita.modInverse(PP.n);//6
		BigInteger gama;
		BigInteger c;
		BigInteger a;
		BigInteger kk=PP.k.modInverse(PP.n);
		BigInteger tmp1=(pk.modPow(supermk,PP.n2).subtract(BigInteger.ONE)).mod(PP.n2);
		a=tmp1.divide(PP.n);
		a=a.multiply(kk);
		a=a.mod(PP.n);
		System.out.println("The parameter a is "+a);
		BigInteger tmp2=(t.a.modPow(supermk,PP.n2).subtract(BigInteger.ONE));
		tmp2=tmp2.mod(PP.n2);
		tmp2=tmp2.divide(PP.n);
		tmp2=tmp2.multiply(kk);
		BigInteger r=tmp2;
		r=r.mod(PP.n);
		System.out.println("The parameter r is "+r);
		gama=r.multiply(a);
		gama=gama.mod(PP.n);
		
		BigInteger tmp3=PP.g.modInverse(PP.n2); 
		tmp3=tmp3.modPow(gama, PP.n2);
		tmp3=tmp3.multiply(s.b);
		tmp3=tmp3.modPow(supermk, PP.n2);
		tmp3=tmp3.subtract(BigInteger.ONE);
		tmp3=tmp3.divide(PP.n);
		//seita=seita.mod(PP.n);
		tmp3=tmp3.multiply(seita);
		c=tmp3.mod(PP.n);
		System.out.println("c is "+c);
	}
	public void multiply(xianmen r,xianmen s1,xianmen s2)
	{
		r.a=s1.a.multiply(s2.a);
		r.a=r.a.mod(PP.n2);
		//
		r.b=s1.b.multiply(s2.a);
		r.b=r.b.mod(PP.n2);
		
		}
	public void expconstant1ntiate(xianmen r,xianmen s1,BigInteger m)
	{
		r.a=s1.a.modPow(m, PP.n2);
		r.b=s1.b.modPow(m, PP.n2);
	}
	public void randommize(BigInteger r)
	{
		
	}
	
	public void sample_uniform(BigInteger rrand,int max)
	{
		BigInteger rand=new BigInteger(max,new Random());
	
	}

	public static void main(String[] args) throws ClassNotFoundException 
	{
		bcp bc =new bcp(100000000);
		try {
            ServerSocket serverSocket = new ServerSocket(8889);
            Socket socket=null;
            System.out.println("服务端已启动，等待客户端连接..");
            ObjectInputStream is_object = null;
            while (true) {
                socket = serverSocket.accept();// 侦听并接受到此套接字的连接,返回一个Socket对象
                
//                InputStream inputStream = socket.getInputStream();
//                // 得到一个输入流，接收客户端传递的信息
//                InputStreamReader inputStreamReader = new InputStreamReader(
//                        inputStream);// 提高效率，将自己字节流转为字符流
//                BufferedReader bufferedReader = new BufferedReader(
//                        inputStreamReader);// 加入缓冲区
//                String temp = null;
//                String info = "";
//                while ((temp = bufferedReader.readLine()) != null) {
//                    info += temp;
//                    System.out.println("已接收到客户端连接");
//                    System.out.println("服务端接收到客户端信息：" + info + ",当前客户端ip为："
//                            + socket.getInetAddress().getHostAddress());
//                }
//                OutputStream outputStream = socket.getOutputStream();// 获取一个输出流，向服务端发送信息
//                PrintWriter printWriter = new PrintWriter(outputStream);// 将输出流包装成打印流
//                printWriter.print("你好，服务端已接收到您的信息");
//                printWriter.flush();
                
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                is_object = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                System.out.println((String)is_object.readObject());
//                oos.writeObject(bc.PP);
                oos.writeObject(bc.PP);
                oos.flush();
//    			socket.close();
                
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
		
		/*
		long startTime=System.currentTimeMillis();

		bc.KetGeneration(bc.PP);
		BigInteger m=new BigInteger("11111111111111111");
		long kaishijiami=System.currentTimeMillis();
		bc.Encryption(bc.pk,m,bc.PP);
		long endTime1=System.currentTimeMillis();
		System.out.println("Encryption time: "+0.001*(endTime1-kaishijiami)+"s");
		bc.Decryption(bc.sk,m,bc.PP,bc.s);
		long endTime2=System.currentTimeMillis();
		System.out.println("Decryption time: "+0.001*(endTime2-endTime1)+"s");
		bc.Decryption_mk(bc.PP, bc.s, bc.pk);
		long endTime3=System.currentTimeMillis();
		System.out.println("MK Decryption time: "+0.001*(endTime3-endTime2)+"s");
		System.out.println("Total time: "+0.001*(endTime3-startTime)+"s");
		*/
	}
}
