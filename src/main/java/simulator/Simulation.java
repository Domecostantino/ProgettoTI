package simulator;

import java.io.File;
import java.util.BitSet;

import channel.CanaleSimmetricoBinario;
import channel.ChannelModel;
import coder.channel.ChannelCoder;
import coder.channel.ChannelMessage;
import coder.source.SourceCoder;
import coders.LZW.funzionante.LZWCoder;
import coders.convolutional.ConvolutionalChannelCoder;
import coders.hamming.HammingChannelCoder;
import utils.GenericUtils;
import utils.Statistics;

public class Simulation {
	private String fileInputPath;
	private ChannelModel channel;
	private ChannelCoder channelCoder;
	private SourceCoder sourceCoder;
	private Statistics statistics;

	public Simulation(SourceCoder sourceCoder, ChannelCoder channelCoder, ChannelModel channelModel,
			Statistics statistics, String fileInputPath) {

		this.sourceCoder = sourceCoder;
		this.channelCoder = channelCoder;
		this.channel = channelModel;
		this.statistics = statistics;
		
		this.fileInputPath = fileInputPath;

	}

	public void execute() {
		statistics.setInitialTime(System.currentTimeMillis());
		
		//codifica
		String fileOutput = fileInputPath.substring(0,fileInputPath.length()-4)+"_Decoded.txt";
		String sourceCode = fileInputPath.substring(0,fileInputPath.length()-4)+"_TMP";
		
		sourceCoder.encode(fileInputPath, sourceCode);
		
		statistics.setSourceCodingTime(System.currentTimeMillis());
		
		ChannelMessage mess = GenericUtils.getChannelMessage(sourceCode);
		
		BitSet b = channelCoder.encode(mess);
		statistics.setChannelCodingTime(System.currentTimeMillis());
		
		//invio su canale
		BitSet corruptedBits = channel.send(b);
		
		
		//decodifica
		channelCoder.decode(corruptedBits, mess);
		statistics.setChannelDecodingTime(System.currentTimeMillis());
		GenericUtils.writeChannelMessage(mess, sourceCode+"2");
		sourceCoder.decode(sourceCode+"2", fileOutput);
		statistics.setSourceDecodingTime(System.currentTimeMillis());
		System.out.println("Dimensione file input:" + new File(fileInputPath).length());
		System.out.println("Dimensione file codifica sorgente:" + GenericUtils.getChannelMessage(sourceCode).getPayload().length);
	
	
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException {

		long t1 = System.currentTimeMillis();
		LZWCoder sourceCoder = new LZWCoder();
		HammingChannelCoder channelCoder = new HammingChannelCoder();
		
		ConvolutionalChannelCoder channelCoder2 = new ConvolutionalChannelCoder(7, 3);
		CanaleSimmetricoBinario channel = new CanaleSimmetricoBinario();

		Simulation sim = new Simulation(sourceCoder, channelCoder2,channel,new Statistics(),"Lorem ipsum.txt");

		sim.execute();
		long t2 = System.currentTimeMillis();
		System.out.println("Ritardo: "+(t2-t1));
		System.out.println("ritardo cod sorg: "+sim.statistics.getSourceCodingTime()+" ms");
		System.out.println("ritardo cod canale: "+sim.statistics.getChannelCodingTime()+" ms");
		System.out.println("ritardo decod canale: "+sim.statistics.getChannelDecodingTime()+" ms");
		System.out.println("ritardo decod sorg: "+sim.statistics.getSourceDecodingTime()+" ms");
	}

}
