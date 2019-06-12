package coders.LZW;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class LZW {

    /**
     * Dizionario degli indici
     */
    public HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
    private String[] Array_char;
    private int count;

    /**
     * Costruttore
     */
    public LZW() {
    }

    /**
     * Metodo per comprimere.
     * Memorizza i 2 indici (12bits) in un array di 3 byte scrivendolo su file
     *
     * @param input
     * @param output
     * @throws IOException
     */
    public void LZWEncode(String input, String output) throws IOException {

        
        Array_char = new String[4096];
        for (int i = 0; i < 256; i++) {
            dictionary.put(Character.toString((char) i), i);
            Array_char[i] = Character.toString((char) i);
        }
        count = 256;

        DataInputStream read = new DataInputStream(new BufferedInputStream(new FileInputStream(input)));

        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(output)));

        byte input_byte;
        String temp = "";
        byte[] buffer = new byte[3];
        boolean onleft = true;

        try {

            /*
             * Lettura del primo carattere
             */
            input_byte = read.readByte();
            int i = new Byte(input_byte).intValue();
            if (i < 0) {
                i += 256;
            }
            char c = (char) i;
            temp = "" + c;

            /*
             * Lettura dei caratteri successivi
             */
            while (true) {
                input_byte = read.readByte();
                i = new Byte(input_byte).intValue();

                if (i < 0) {
                    i += 256;
                }
                c = (char) i;

                if (dictionary.containsKey(temp + c)) {
                    temp = temp + c;
                } else {
                    String s12 = to12bit(dictionary.get(temp));
                    /*
                     * 12 bits -> array 
                     */

                    if (onleft) {
                        buffer[0] = (byte) Integer.parseInt(s12.substring(0, 8), 2);
                        buffer[1] = (byte) Integer.parseInt(s12.substring(8, 12) + "0000", 2);
                    } else {
                        buffer[1] += (byte) Integer.parseInt(s12.substring(0, 4), 2);
                        buffer[2] = (byte) Integer.parseInt(s12.substring(4, 12), 2);
                        for (int b = 0; b < buffer.length; b++) {
                            out.writeByte(buffer[b]);//scrittura su file
                            buffer[b] = 0;
                        }
                    }
                    onleft = !onleft;
                    if (count < 4096) {
                        dictionary.put(temp + c, count++);
                    }
                    temp = "" + c;
                }
            }

        } catch (EOFException e) {
            String temp_12 = to12bit(dictionary.get(temp));
            if (onleft) {
                buffer[0] = (byte) Integer.parseInt(temp_12.substring(0, 8), 2);
                buffer[1] = (byte) Integer.parseInt(temp_12.substring(8, 12) + "0000", 2);
                out.writeByte(buffer[0]);
                out.writeByte(buffer[1]);
            } else {
                buffer[1] += (byte) Integer.parseInt(temp_12.substring(0, 4), 2);
                buffer[2] = (byte) Integer.parseInt(temp_12.substring(4, 12), 2);
                for (int b = 0; b < buffer.length; b++) {
                    out.writeByte(buffer[b]);
                    buffer[b] = 0;
                }
            }
            read.close();
            out.close();
        }

    }

    /**
     * Converte 8 bit in 12 bit
     */
    public String to12bit(int i) {
        String temp = Integer.toBinaryString(i);
        while (temp.length() < 12) {
            temp = "0" + temp;
        }
        return temp;
    }

    /**
     * Estrae l'indice di 12 bit da 2 byte e ne ritorna il valore intero
     *
     * @param b1
     * @param b2
     * @param onleft
     * @return an Integer which holds the value of the key
     */
    public int getvalue(byte b1, byte b2, boolean onleft) {
        String temp1 = Integer.toBinaryString(b1);
        String temp2 = Integer.toBinaryString(b2);
        while (temp1.length() < 8) {
            temp1 = "0" + temp1;
        }
        if (temp1.length() == 32) {
            temp1 = temp1.substring(24, 32);
        }
        while (temp2.length() < 8) {
            temp2 = "0" + temp2;
        }
        if (temp2.length() == 32) {
            temp2 = temp2.substring(24, 32);
        }

        if (onleft) {
            return Integer.parseInt(temp1 + temp2.substring(0, 4), 2);
        } else {
            return Integer.parseInt(temp1.substring(4, 8) + temp2, 2);
        }

    }

    /**
     * Metodo di decompressione. 
     * Legge 3 byte di informazione e scrive le 2 sequenze associate agli indici e itera.
     *
     * @param input - file path
     * @param output - file path
     * @throws IOException
     */
    public void LZWDecode(String input, String output) throws IOException {
        
        Array_char = new String[4096];
        for (int i = 0; i < 256; i++) {
            dictionary.put(Character.toString((char) i), i);
            Array_char[i] = Character.toString((char) i);
        }
        count = 256;

        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(input)));

        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(output)));

        int currword, priorword;
        byte[] buffer = new byte[3];
        boolean onleft = true;
        try {

            
            buffer[0] = in.readByte();
            buffer[1] = in.readByte();

            priorword = getvalue(buffer[0], buffer[1], onleft);
            onleft = !onleft;
            out.writeBytes(Array_char[priorword]);

            while (true) {
                try {

                    if (onleft) {
                        buffer[0] = in.readByte();
                        buffer[1] = in.readByte();
                        currword = getvalue(buffer[0], buffer[1], onleft);
                    } else {
                        buffer[2] = in.readByte();
                        currword = getvalue(buffer[1], buffer[2], onleft);
                    }
                    onleft = !onleft;
                    if (currword >= count) {

                        if (count < 4096) {
                            Array_char[count] = Array_char[priorword] + Array_char[priorword].charAt(0);
                        }
                        count++;
                        out.writeBytes(Array_char[priorword] + Array_char[priorword].charAt(0));
                    } else {

                        if (count < 4096) {
                            Array_char[count] = Array_char[priorword] + Array_char[currword].charAt(0);
                        }
                        count++;
                        out.writeBytes(Array_char[currword]);
                    }
                    priorword = currword;
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    System.out.println("Errore decodifica");
                }
            }

        } catch (EOFException e) {
            in.close();
            out.close();
        }

    }

    

}
