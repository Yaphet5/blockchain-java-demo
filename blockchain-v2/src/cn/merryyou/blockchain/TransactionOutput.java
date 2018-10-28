package cn.merryyou.blockchain;

import cn.merryyou.blockchain.utils.StringUtil;

import java.math.BigInteger;
import java.security.PublicKey;

/**
 * Created on 2018/10/20.
 *
 * @author DragonW 
 * @since 1.0
 */
public class TransactionOutput {

    public String id;
    public PublicKey reciepient; //also known as the new owner of these coins.
    public BigInteger value; //the amount of coins they own
    public String parentTransactionId; //the id of the transaction this output was created in

    //Constructor
    public TransactionOutput(PublicKey reciepient, BigInteger value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+value.toString()+parentTransactionId);
    }

    //Check if coin belongs to you
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == reciepient);
    }
}
