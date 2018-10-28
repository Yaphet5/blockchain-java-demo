package util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;

public class pp_para implements Serializable {
	private static final long serialVersionUID = -2049597486247025611L;
	public String name = "me";
	public transient BigInteger n;
	public transient BigInteger k;
	public transient BigInteger g;
	public transient BigInteger n2;
	public byte[] nb;
	public byte[] kb;
	public byte[] gb;
	public byte[] n2b;

	private void writeObject(ObjectOutputStream oos) throws IOException {
		
		nb = n.toByteArray();
		kb = k.toByteArray();
		gb = g.toByteArray();
		n2b = n2.toByteArray();
		oos.defaultWriteObject();
		System.out.println("session serialized");
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		n = new BigInteger(nb);
		k = new BigInteger(kb);
		g = new BigInteger(gb);
		n2 = new BigInteger(n2b);
		System.out.println("session deserialized");
	}
}