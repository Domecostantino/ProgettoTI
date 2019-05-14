package coders.hamming;

import java.util.BitSet;

import coder.channel.ChannelCoder;
import coder.channel.ChannelMessage;
import utils.GenericUtils;
import utils.MyBitSet;

public class HammingChannelCoder implements ChannelCoder {

	private HammingCode coder;
    private int r;
    private int length;

    public HammingChannelCoder() {
        this.r = 3;
        this.length = (int) Math.pow(2, r) - 1;
        coder = new HammingCode(3);// Hamming 7/4
    }

    public HammingChannelCoder(int r) {
        this.r = r;
        this.length = (int) Math.pow(2, r) - 1;
        coder = new HammingCode(r);
    }

    @Override
    public MyBitSet encode(ChannelMessage inChannelMessage) {
        BitSet out = null;
        // trasformo input in array di boolean
        byte[] fileContent = inChannelMessage.getPayload();
        int outlength = fileContent.length * 8 * length / r;
        out = new BitSet(outlength);
        int setIndex = 0;
        BitSet bs = BitSet.valueOf(fileContent);
        int bits = length-r;
        int blocks=(int)Math.ceil(fileContent.length * 8/(double)bits);
        for (int i = 0; i < blocks; i++) {
            
//			System.out.println(bs.length());
            boolean[] boolData0 = new boolean[bits];
            
            for (int j = 0; j < bits; j++) {
                    boolData0[j] = bs.get(i*bits+j);
                
            }
            // codifico
            boolean[] encData0 = coder.encode(boolData0);
            for (int j = 0; j < encData0.length; j++) {
                if (encData0[j]) {
                    out.set(setIndex);
                }
                setIndex++;
            }
        }

        return new MyBitSet(out, outlength);
    }

    @Override
    public void decode(MyBitSet encoded_data, ChannelMessage outChannelMessage) {
        int numBlocks = encoded_data.getLength() / length;
        int bits=length-r;
        BitSet bs = new BitSet(numBlocks * bits);
        for (int j = 0; j < numBlocks; j++) {
            boolean[] boolData = new boolean[length];
            for (int i = 0; i < length; i++) {
                boolData[i] = encoded_data.getBitset().get(j * length + i);
            }
            // decodifico
            boolean[] decData = coder.decode(boolData);

            for (int i = 0; i < decData.length; i++) {
                if (decData[i]) {
                    bs.set(j * bits + i);
                }
            }
        }

        // scrivo file
        byte[] out = bs.toByteArray();
        outChannelMessage.setPayload(out);
    }
    
    public int getR() {
		return r;
	}

    public static void main(String[] args){
        HammingChannelCoder h=new HammingChannelCoder(3);
        ChannelMessage mess=GenericUtils.getChannelMessage("ciao.txt");
        MyBitSet enc=h.encode(mess);
        enc.getBitset().flip(0,1);
        h.decode(enc, mess);
        System.out.println(new String(mess.getPayload()));
        
    }
    
}
