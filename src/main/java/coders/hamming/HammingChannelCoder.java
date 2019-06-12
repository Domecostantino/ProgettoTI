package coders.hamming;

import java.util.BitSet;

import coders.ChannelCoder;
import coders.ChannelMessage;
import utils.GenericUtils;
import utils.MyBitSet;

public class HammingChannelCoder implements ChannelCoder {

    private Hamming coder;
    private int r;
    private int length;

    public HammingChannelCoder() {
        this.r = 3;
        this.length = (int) Math.pow(2, r) - 1;
        coder = new Hamming(3);// Hamming 7/4
    }

    public HammingChannelCoder(int r) {
        this.r = r;
        this.length = (int) Math.pow(2, r) - 1;
        coder = new Hamming(r);
    }

    @Override
    public MyBitSet encode(ChannelMessage inChannelMessage) {
        BitSet out = null;
        // trasformo input in array di boolean
        byte[] fileContent = inChannelMessage.getPayload();
        int bits = length - r;
        int outlength = (int) Math.ceil(inChannelMessage.getPayloadLength() * length / (double) bits);
        System.out.println(outlength);
        out = new BitSet(outlength);
        int setIndex = 0;
        BitSet bs = BitSet.valueOf(fileContent);
        int blocks = (int) Math.ceil(inChannelMessage.getPayloadLength() / (double) bits);
        for (int i = 0; i < blocks; i++) {

//			System.out.println(bs.length());
            boolean[] boolData0 = new boolean[bits];

            for (int j = 0; j < bits; j++) {
                boolData0[j] = bs.get(i * bits + j);

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
        int numBlocks = (int) Math.ceil(encoded_data.getLength() / (double) length);
        int bits = length - r;
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

    public static void main(String[] args) {
        HammingChannelCoder h = new HammingChannelCoder(5);
        ChannelMessage mess = GenericUtils.getChannelMessage("ciao.txt");
        MyBitSet enc = h.encode(mess);
//        enc.getBitset().flip(0,1);
        h.decode(enc, mess);
        System.out.println(new String(mess.getPayload()));

    }

}
