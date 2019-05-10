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
    private int repH = 3;
    private int[] concR = {3, 3};
    private int g_eType=0;
    private double ber=0.005;
    private File file;
    private static GUIHelper instance = null;

    private GUIHelper() {
    }

    public static synchronized GUIHelper getInstance() {
        if (instance == null) {
            instance = new GUIHelper();
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

    public void setRepH(int repH) {
        this.repH = repH;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setG_eType(int g_eType) {
        this.g_eType = g_eType;
    }

    public void setBer(double ber) {
        this.ber = ber;
    }

    
    
    public void run() {
        SourceCoder scoder = null;
        switch (source) {
            case HUFFMAN:
                scoder = new HuffmanCoder();
                break;
            case LZW:
                scoder = new LZWCoder();
                break;
            case DEFLATE:
                scoder = new DeflateCoder();
                break;
        }
        ChannelCoder ccoder = null;
        switch (channel) {
            case CONCATENATED:
                ccoder = new ConcatenatedChannelCoder(concR);
                break;
            case CONVOLUTIONAL:
                ccoder = new ConvolutionalChannelCoder(convK, convR);
                break;
            case HAMMING:
                ccoder = new HammingChannelCoder(repH);
                break;
            case REPETITION:
                ccoder = new RepChannelCoder(repR);
                break;
        }
        ChannelModel errorModel = null;
        switch (error) {
            case G_E:
                errorModel = new GilbertElliot(g_eType);
                break;
            case SBC:
                errorModel = new CanaleSimmetricoBinario(ber);
                break;
        }
        Statistics stat = new Statistics();
        Simulation simulation = new Simulation(scoder, ccoder, errorModel, stat, file.getAbsolutePath());
        long t1 = System.currentTimeMillis();
        simulation.execute();
        long t2 = System.currentTimeMillis();
        ProvaGUI.getInstance().getInputText().append("\nRitardo: " + (t2 - t1)+ " ms");
        ProvaGUI.getInstance().getInputText().append("\nritardo cod sorg: " + stat.getSourceCodingTime() + " ms");
        ProvaGUI.getInstance().getInputText().append("\nritardo cod canale: " + stat.getChannelCodingTime() + " ms");
        ProvaGUI.getInstance().getInputText().append("\nritardo decod canale: " + stat.getChannelDecodingTime() + " ms");
        ProvaGUI.getInstance().getInputText().append("\nritardo decod sorg: " + stat.getSourceDecodingTime() + " ms");

        ProvaGUI.getInstance().getInputText().append("\n\ndimensione file iniziale " + stat.getInitialSize() + " byte");
        ProvaGUI.getInstance().getInputText().append("\ndimensione file compressioneSorgente " + stat.getSourceCodeSize() + " byte");
        ProvaGUI.getInstance().getInputText().append("\ncompression rate: " + stat.getCompressionRate()+"\n");

        ProvaGUI.getInstance().getInputText().append("\nerror rate cod canale " + stat.getChannelDecodingErrorRate()*100+" %");
        ProvaGUI.getInstance().getInputText().append("\nerror rate canale solo cod sorgente " + stat.getOnlySourceCodeChannelErrorRate()*100+" %");
        ProvaGUI.getInstance().getInputText().append("\nrecovery rate del codificatore di canale "+ stat.getErrorRecoveryRate()*100+" %");

    }

}
