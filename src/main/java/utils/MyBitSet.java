/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.BitSet;

/**
 *
 * @author jonny
 */
public class MyBitSet {
    private BitSet bitset;
    private int length;
    public MyBitSet(BitSet bitset,int length){
        this.length=length;
        this.bitset=bitset;
    }

    public BitSet getBitset() {
        return bitset;
    }

    public int getLength() {
        return length;
    }
    
}
