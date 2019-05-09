package utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.BitSet;

import coder.channel.ChannelMessage;
import coders.Message;

public class GenericUtils {

	public static BitSet getBitSetFromString(String binary) {
		BitSet bitset = new BitSet(binary.length());
		for (int i = 0; i < binary.length(); i++) {
			if (binary.charAt(i) == '1') {
				bitset.set(i);
			}
		}
		if (binary.charAt(binary.length() - 1) == '0') {
			bitset.set(binary.length() - 1);
			bitset.flip(binary.length() - 1);
		}
		return bitset;
	}

	public static String toBinaryString(BitSet bitset, int lenght) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < lenght; i++) {
			sb.append((bitset.get(i)) ? '1' : '0');
		}

		return sb.toString();
	}

	public static Message getMessageFromFile(String filePath) {
		Message m = new Message();
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath));
			Object o = ois.readObject();

			m.setHeader(o);
			int length = ois.readInt();
			byte[] payload = (byte[]) ois.readObject();
//			System.out.println("bytes ricevuti " + payload.length);
			BitSet bs = BitSet.valueOf(payload);
			// lenght viene passato per evitare di leggere i bit di padding
			String stringPayload = toBinaryString(bs, length);
//			System.out.println(stringPayload);
			m.setPayload(stringPayload);
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return m;
	}

	public static void writeMessageToFile(Message mess, String outputFilePath) {
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(outputFilePath));
			oos.writeObject(mess.getHeader());
			oos.writeInt(mess.getPayload().length());
			byte[] payload = getBitSetFromString(mess.getPayload()).toByteArray();
//			System.out.println("bytes codifica huffman: " + payload.length);
			oos.writeObject(payload);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String readFile(String path, Charset encoding) {
		byte[] encoded = null;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			System.out.println("Read failure");
			e.printStackTrace();
		}
		return new String(encoded, encoding);
	}

	public static void writeString(String input, String outputPath) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(outputPath);
			pw.append(input);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static ChannelMessage getChannelMessage(String fileName) {
		ChannelMessage mess = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
			Object o = ois.readObject();
			int l = ois.readInt();
			byte[] payload = (byte[]) ois.readObject();
			mess = new ChannelMessage(o, l, payload);
			ois.close();
		} catch (IOException e) {
//			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (mess == null) {
			try {
				byte[] payload = Files.readAllBytes(new File(fileName).toPath());
				mess = new ChannelMessage(null, payload.length * 8, payload);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mess;
	}

	public static void writeChannelMessage(ChannelMessage message, String outputPath) {
		if (message.getHuffmanHeader() != null) {
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(new FileOutputStream(outputPath));
				oos.writeObject(message.getHuffmanHeader());
				oos.writeInt(message.getPayloadLength());
				byte[] payload = message.getPayload();
				oos.writeObject(payload);
				oos.flush();
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {// se non c'è header
			try {
				DataOutputStream writer = new DataOutputStream(new FileOutputStream(outputPath));
				writer.write(message.getPayload());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}