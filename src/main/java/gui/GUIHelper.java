/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import channel.CanaleSimmetricoBinario;
import channel.ChannelModel;
import channel.GilbertElliot;
import coder.channel.ChannelCoder;
import coder.source.SourceCoder;
import coders.LZW.funzionante.LZWCoder;
import coders.convolutional.ConvolutionalChannelCoder;
import coders.deflate.DeflateCoder;
import coders.hamming.HammingChannelCoder;
import coders.huffman.HuffmanCoder;
import coders.repetition.ConcatenatedChannelCoder;
import coders.repetition.RepChannelCoder;
import java.io.File;
import simulator.Simulation;
import utils.Statistics;

/**
 *
 * @author jonny
 */
public class GUIHelper {

    private Source source = null;
    private Channel channel = null;
    private Error error = null;
    private int convR = 2, convK = 2;
    private int repR = 3;
    private int[] concR = {3, 3};
    private File file;
    private static GUIHelper instance=null;
    
    private GUIHelper(){}
    
    public static synchronized GUIHelper getInstance(){
        if(instance==null){
            instance=new GUIHelper();
        }
        return instance;
    }
    
    enum Source {
        HUFFMAN, LZW, DEFLATE;
    }

    enum Channel {
        REPETITION, CONVOLUTIONAL, CONCATENATED, HAMMING;
    }

    enum Error {
        SBC, G_E;
    }

    public Source getSource() {
        return source;
    }

    public Channel getChannel() {
        return channel;
    }

    public Error getError() {
        return error;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public void setConvR(int convR) {
        this.convR = convR;
    }

    public void setConvK(int convK) {
        this.convK = convK;
    }

    public void setRepR(int repR) {
        this.repR = repR;
    }

    public void setConcR(int[] concR) {
        this.concR = concR;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void run() {
        SourceCoder scoder = null;
        switch (source) {
            case HUFFMAN:
                scoder = new HuffmanCoder();
            case LZW:
                scoder = new LZWCoder();
            case DEFLATE:
                scoder = new DeflateCoder();
        }
        ChannelCoder ccoder = null;
        switch (channel) {
            case CONCATENATED:
                ccoder = new ConcatenatedChannelCoder(concR);
            case CONVOLUTIONAL:
                ccoder = new ConvolutionalChannelCoder(convK, convR);
            case HAMMING:
                ccoder = new HammingChannelCoder();
            case REPETITION:
                ccoder = new RepChannelCoder(repR);
        }
        ChannelModel errorModel = null;
        switch (error) {
            case G_E:
                errorModel = new GilbertElliot();
            case SBC:
                errorModel = new CanaleSimmetricoBinario();
        }
        Statistics stat=new Statistics();
        Simulation simulation=new Simulation(scoder, ccoder, errorModel, stat, file.getAbsolutePath());
        simulation.execute();
    }

}
