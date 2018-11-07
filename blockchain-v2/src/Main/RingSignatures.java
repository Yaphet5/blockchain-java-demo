package Main;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author leijurv
 */
public class RingSignatures {
    
    
    public static byte[] hash(byte[] message, int size) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(message);
        byte[] d = digest.digest();
        byte[] res = new byte[size / 8];//Extend this hash out to however long it needs to be
        int dlen = d.length;
        for (int i = 0; i < res.length / dlen; i++) {
            System.arraycopy(d, 0, res, i * dlen, dlen);
        }
        return res;
    }
    
    public static boolean verify(byte[][] sig, RSAKeyPair[] keys, byte[] message, int bitlength) throws Exception {
        byte[] k = hash(message, 256);
        byte[] v = sig[sig.length - 1];
        BigInteger[] x = new BigInteger[sig.length - 1];
        BigInteger[] y = new BigInteger[x.length];
        for (int i = 0; i < x.length; i++) {
            x[i] = new BigInteger(Util.leadingZero(sig[i]));
            y[i] = keys[i].encode(x[i], bitlength);
        }
        byte[] res = Util.runCKV(k, v, y);
        return new BigInteger(res).equals(new BigInteger(v));
    }
    
    
    /**
     * 
     * @param keys Ⱥ�������˵Ĺ�˽�h������һ��˽Կ��ʣ��Ĺ�Կ��
     * @param message ��Ϣ����
     * @param b 
     * @param r
     * @return
     * @throws Exception
     */
    public static byte[][] genRing(RSAKeyPair[] keys, byte[] message, int b, Random r) throws Exception {
        byte[] k = hash(message, 256);
        int s = -1;
        for (int i = 0; i < keys.length; i++) {
        	//�ҵ�Ⱥ��Ψһһ����˽Կ��
        	//Find the one that we have the private key to
            if (keys[i].hasPrivate()) {
                if (s != -1) {
                    throw new IllegalStateException("Too many private keyssss");
                }
                s = i;
            }
        }
        
        if (s == -1) {
            throw new IllegalStateException("Need at least 1 private key to create a ring signature");
        }
        
        byte[] v = new byte[b / 8];//Number of bytes = number of bits / 8
        r.nextBytes(v);//Maybe this should be more random?
        BigInteger[] x = new BigInteger[keys.length];
        BigInteger[] y = new BigInteger[keys.length];
        
        for (int i = 0; i < keys.length; i++) {
            if (i != s) {//Do this for everyone but me, mine is generated later
                x[i] = new BigInteger(b, r);//bΪ�����ɴ��������bit��
                y[i] = keys[i].encode(x[i], b);
            }
        }
        
        //k��message�Ĺ�ϣֵ
        byte[] CKV = Util.solveCKV(k, v, y, s);
        //System.out.println(CKV.length);
//System.out.println("--------------------------"+CKV[0]);
        y[s] = new BigInteger(Util.leadingZero(CKV));
        x[s] = keys[s].decode(y[s], b);
        //System.out.println("YS: " + y[s]);
        byte[] check = Util.runCKV(k, v, y);
        int d = new BigInteger(check).compareTo(new BigInteger(v));
        if (d != 0) {
            throw new IllegalStateException("Shrek");
        }
        byte[][] result = new byte[keys.length + 1][];
        for (int i = 0; i < keys.length; i++) {
            byte[] X = x[i].toByteArray();
            if (X.length == 129) {
                X = Util.trimLeading(X);
            }
            result[i] = X;
        }
        result[keys.length] = v;
        return result;
    }
    

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        /*
         System.out.println(System.getProperty("java.home"));
         RSAKeyPair dank = RSAKeyPair.generate(new BigInteger("61"), new BigInteger("53"), new BigInteger("17"));
         System.out.println(dank.encode(new BigInteger("65000000"), 100));
         System.out.println(dank.decode(new BigInteger("65001491"), 100));
         byte[] key = new BigInteger("5021").toByteArray(); // TODO
         byte[] input = new BigInteger("5021").toByteArray(); // TODO
         byte[] output = encrypt(hash(key), input);
         System.out.println(new BigInteger(decrypt(hash(key), encrypt(hash(key), input))));*/
        int bitlength = 1024;
        Random random = new Random();//new Random(5021);
        while (true) {
            int numKeys = random.nextInt(10) + 1;//Ⱥ�ڳ�Ա����
            System.out.println("---------Ring numbers will be:"+numKeys);
            RSAKeyPair[] keys = new RSAKeyPair[numKeys];
            int s = random.nextInt(numKeys);
            byte[] message = new byte[8];//һ��8�ֽڵ��������
            random.nextBytes(message);
            
            for (int i = 0; i < keys.length; i++) {//��ÿ��Ⱥ�ڳ�Ա�����ɹ�˽�h
                keys[i] = RSAKeyPair.generate(new BigInteger(bitlength / 2 - 4, 8, random), new BigInteger(bitlength / 2, 8, random));
                if (i != s) {
                	//ֻ��Ҫһ��˽Կ   ���keys�湫Կ����
                    keys[i] = keys[i].withoutPriv();//We only need one of the private keys
                }
                System.out.println("keypair_"+i+"__"+keys[i].toString()+"\n");
            }
            
            System.out.println("---------Creating and verifying");
            long time = System.currentTimeMillis();
            byte[][] sig = genRing(keys, message, bitlength, random);
//            String res = new String(message,"hex");
            
//            System.out.println(Arrays.toString(message));
//            
//            System.out.println(Arrays.toString(sig));
            
            boolean b = verify(sig, keys, message, bitlength);
            System.out.println(b + " numKeys:" + numKeys + " No." + s + " time:" + (System.currentTimeMillis() - time) + "ms");
//            if (!b) {
//                return;
//            }
            System.out.println("result: "+b);
            return;
        }
    }//main ends
}