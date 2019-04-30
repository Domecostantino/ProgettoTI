package coders.deflate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.LinkedList;

public class LZ77 {
	private int search_size, lookahead_size;
	private final int OFFSET_SIZE, LENGHT_SIZE;
	private char[] searchBuffer, lookaheadBuffer;

	public LZ77(int dim_searchBuf, int dim_lookaheadBuf) {
		this.lookahead_size = dim_lookaheadBuf;
		this.search_size = dim_searchBuf;
		OFFSET_SIZE = String.valueOf(search_size).length();
		LENGHT_SIZE = String.valueOf(lookahead_size).length();
		searchBuffer = new char[search_size];
		lookaheadBuffer = new char[lookahead_size];
	}

	private class Pointer {
		private int index, lenght;
		private char character;

		public Pointer(int index, int lenght, char character) {
			this.index = index;
			this.lenght = lenght;
			this.character = character;
		}
	}

	private void fillSBuffer() {
		for (int i = 0; i < searchBuffer.length; i++) {
			searchBuffer[i] = '}';
		}
	}

	private int charIndexInSBuffer(char c, int start) {
		for (int i = start; i < search_size; i++) {
			if (searchBuffer[i] == c)
				return i + 1;
		}
		return 0;
	}

	private void update(int dim, BufferedReader br) throws IOException {
		for (int i = 0; i < search_size - dim; i++) {
			searchBuffer[i] = searchBuffer[i + dim];
		}
		int k = 0;
		for (int i = search_size - dim; i < search_size && k < lookahead_size; i++) {
			searchBuffer[i] = lookaheadBuffer[k];
			k++;
		}
		for (int i = 0; i < lookahead_size - dim; i++) {
			lookaheadBuffer[i] = lookaheadBuffer[i + dim];
		}
		for (int i = lookahead_size - dim; i < lookahead_size; i++) {
			int read = br.read();
			if (read != -1)
				lookaheadBuffer[i] = (char) read;
			else
				lookahead_size = i;
		}
	}

	public String encode(String inputPath) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)));
		StringBuilder outSb = new StringBuilder();
		fillSBuffer();

		for (int i = 0; i < lookahead_size; i++) {
			int read = br.read();
			if (read != -1) {
				lookaheadBuffer[i] = (char) read;
			} else {
				lookahead_size = i;
			}
		}

		while (lookahead_size != 0) {

			char finalNextChar = lookaheadBuffer[0];
			int start = 0;
			int ind = charIndexInSBuffer(finalNextChar, start);
			int finalLenght = 0;
			int finalOffset = ind;
			StringBuilder finalSeq = new StringBuilder("" + finalNextChar);
			while (ind != 0) {
				int len = 0;
				int k = ind - 1;
				int i = 0;
				char nextChar=lookaheadBuffer[0];
				StringBuilder seq = new StringBuilder();
				while (i < lookahead_size) {
					if (ind != 0 && i != lookahead_size - 1 && lookaheadBuffer[i] == searchBuffer[k]) {
						len++;
						seq.append(lookaheadBuffer[i]);
					} else {
						nextChar = lookaheadBuffer[i];
						seq.append(lookaheadBuffer[i]);
						break;
					}
					k = (k + 1) % search_size;
					k = k == 0 ? ind - 1 : k;
					i++;
//					if (k == search_size && i < lookahead_size && searchBuffer[ind - 1] == lookaheadBuffer[i])
//						k = ind - 1;
				}
				if (len >= finalLenght) {
					finalLenght = len;
					finalOffset = ind;
					finalSeq = seq;
					finalNextChar=nextChar;
				}
				start = ind + 1;
				ind = charIndexInSBuffer(nextChar, start);
//				System.out.println(seq);
			}

//			System.out.println(String.valueOf(ind) + "," + String.valueOf(len) + nextChar);
			String offset = String.valueOf(finalOffset);
			String lenght = String.valueOf(finalLenght);
			while (offset.length() < OFFSET_SIZE) {
				offset = '0' + offset;
			}
			while (lenght.length() < LENGHT_SIZE) {
				lenght = '0' + lenght;
			}
			String codedSeq = "§" + offset + lenght + finalNextChar;
			if (finalSeq.length() > codedSeq.length())
				outSb.append(codedSeq);
			else
				outSb.append(finalSeq);

			update(finalLenght + 1, br);
		}
		br.close();
		return outSb.toString();
	}

	public Pointer nextPointer(StringBuilder in) {
		int count = 0;
		String index = "", length = "";
		char character = ' ';
		in.deleteCharAt(0);
		while (in.length() != 0) {
			if (count < OFFSET_SIZE) {
				int next = Integer.parseInt("" + in.charAt(0));
				in.deleteCharAt(0);
				index += next;
			} else if (count < LENGHT_SIZE+OFFSET_SIZE) {
				int next = Integer.parseInt("" + in.charAt(0));
				in.deleteCharAt(0);
				length += next;
			} else {
				character = in.charAt(0);
				in.deleteCharAt(0);
				break;
			}
			count++;
		}
		if (in.length() == 0) {
			return null;
		}

		return new Pointer(Integer.parseInt(index), Integer.parseInt(length), character);
	}

	public String decode(String encodedData) {
		StringBuilder in = new StringBuilder(encodedData);
		StringBuilder out = new StringBuilder();
		fillSBuffer();
		int pos = 0;
		while (in.length() != 0) {
			Pointer p = null;

			if (in.charAt(0) != '§') {
				out.append(in.charAt(0));
				in.deleteCharAt(0);
				pos++;
			} else {
				p = nextPointer(in);
				if (p.index != 0) {
					int k = p.index - 1;
					for (int i = 0; i < p.lenght; i++) {
						int offset = pos - search_size + k;
//						System.out.println(p.index+","+p.lenght+","+" "+offset);
						out.append(out.charAt(offset));
						k = (k + 1) % search_size;
						k = k == 0 ? p.index - 1 : k;
					}
//					System.out.println(p.character);
					out.append(p.character);
					pos += p.lenght + 1;
				}

			}
		}
		return out.toString();
	}

	public static void main(String[] args) throws IOException {
//		int in = (int) 'z';
//		System.out.println(in);
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("ciao.txt")));
//		int i = br.read();
//		while (i != -1) {
//			char c = (char) i;
//			System.out.print(c);
//			i = br.read();
//		}
//
//		br.close();
		LZ77 coder = new LZ77(99, 99);
		String coded = coder.encode("mail_de_rango.txt");
//		System.out.println(coded);
		System.out.println(coder.decode(coded));
//		StringBuilder sb = new StringBuilder("");
//		Pointer p = coder.nextPointer(sb);
//		System.out.println(p.index + "," + p.lenght + p.character);
//		Pointer p1 = coder.nextPointer(sb);
//		System.out.println(p1.index + "," + p1.lenght + p1.character);
	}

}
