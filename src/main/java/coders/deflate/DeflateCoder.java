package coders.deflate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.BitSet;

import coder.source.Coder;
import coders.Message;
import coders.huffman.CanonicalHuffmanCoder;
import coders.huffman.CanonicalHuffmanDecoder;
import utils.HuffmanUtils;

public class DeflateCoder implements Coder {
	LZ77 lz77 = new LZ77(1155, 1155);

	@Override
	public void encode(String inputFileName, String outputFileName) {
		try {
			String enc = lz77.encode(inputFileName);
			System.out.println("bytes codifica lz77: "+enc.length());
			CanonicalHuffmanCoder hcoder = new CanonicalHuffmanCoder();
			Message mess = hcoder.encode(enc);
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFileName));
			oos.writeObject(mess.getHeader());
			byte[] payload=HuffmanUtils.fromString(mess.getPayload()).toByteArray();
			System.out.println("bytes codifica huffman: "+payload.length);
			oos.writeObject(payload);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void decode(String inputFileName, String outputFileName) {
		Object o = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFileName));
			o = ois.readObject();
			Message m = new Message();
			m.setHeader(o);
			byte[] payload=(byte[]) ois.readObject();
			System.out.println("bytes ricevuti "+payload.length);
			String stringPayload=HuffmanUtils.toBinaryString(BitSet.valueOf(payload));
			System.out.println(stringPayload);
			m.setPayload(stringPayload);
			ois.close();
			CanonicalHuffmanDecoder hdec = new CanonicalHuffmanDecoder();
			PrintWriter pw=new PrintWriter(outputFileName);
			String decodedString=lz77.decode(hdec.decode(m));
//			System.out.println(decodedString);
			pw.append(decodedString);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		DeflateCoder dc=new DeflateCoder();
		String in="dberr.txt";
		String out="dberrdec.txt";
		String cod="deflatedberr";
		dc.encode(in, cod);
		System.out.println("Dimensione file input:"+new File(in).length());
		System.out.println("Dimensione file codificato:"+new File(cod).length());
		dc.decode(cod, out);
	}
	
}
