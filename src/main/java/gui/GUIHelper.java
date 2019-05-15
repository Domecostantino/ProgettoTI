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
import java.awt.Color;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;
import simulator.Simulation;
import utils.GenericUtils;
import utils.Statistics;

/**
 *
 * @author jonny
 */
public class GUIHelper {

    private Source source = null;
    private Channel channel = null;
    private Error error = null;
    private int convR = 2, convK = 3;
    private int repR = 3;
    private int repH = 3;
    private int[] concR = {3, 3};
    private int g_eType = 0;
    private double ber = 0.001;
    private File file;
    private static GUIHelper instance = null;
    private Statistics stat;

    private GUIHelper() {
    }

    public static synchronized GUIHelper getInstance() {
        if (instance == null) {
            instance = new GUIHelper();
        }
        return instance;
    }

    void showCompressionFactor() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(stat.getInitialSize(),"Dimensione input","");
        dataset.setValue(stat.getSourceCodeSize(),"Dimensione codifica","" );
        JFreeChart chart = ChartFactory.createBarChart3D("Compressione", "", "Dimensione", dataset);
                
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        JFrame barchart = new JFrame();
        barchart.setContentPane(chartPanel);
        barchart.pack();
        barchart.setVisible(true);
    }

    void showErrorComparison() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Errore canale", stat.getOnlySourceCodeChannelErrorRate());
        dataset.setValue("Errore con codifica", stat.getChannelDecodingErrorRate());
        JFreeChart chart = ChartFactory.createPieChart3D(
                "Comparazione di errore", // chart title
                dataset, // data
                true, // include legend
                true,
                false
        );

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        JFrame piechart = new JFrame();
        piechart.setContentPane(chartPanel);
        piechart.pack();
        piechart.setVisible(true);
    }
    
     void showTimeSlices() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Codifica di sorgente", stat.getSourceCodingTime());
        dataset.setValue("Codifica di canale", stat.getChannelCodingTime());
        dataset.setValue("Decodifica di canale", stat.getChannelDecodingTime());
        dataset.setValue("Decodifica di sorgente", stat.getSourceDecodingTime());
        JFreeChart chart = ChartFactory.createPieChart3D(
                "Tempi degli step di simulazione", // chart title
                dataset, // data
                true, // include legend
                true,
                false
        );

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        JFrame piechart = new JFrame();
        piechart.setContentPane(chartPanel);
        piechart.pack();
        piechart.setVisible(true);
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

    public Statistics getStatistics() {
        return stat;
    }

    
    
    public void setFile(File file) {
        this.file = file;
        JTextArea source = ProvaGUI.getInstance().getSourceText();
        source.setText(GenericUtils.readFile(file.getAbsolutePath(), StandardCharsets.UTF_8));
        //List<Pair<Integer, Integer>> indexes = new LinkedList<>();
        //indexes.add(new Pair(0, 5));
        //highlight(source, indexes);
    }

    public void setG_eType(int g_eType) {
        this.g_eType = g_eType;
    }

    public void setBer(double ber) {
        this.ber = ber;
    }

    private class Triple {

        int start, end;
        String line;

        public Triple(int start, int end, String line) {
            this.start = start;
            this.end = end;
            this.line = line;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Triple other = (Triple) obj;
            if (!Objects.equals(this.line, other.line)) {
                return false;
            }
            return true;
        }

    }

    private List<Pair<Integer, Integer>> findDifferences(String s, String o) {
        List<Pair<Integer, Integer>> indexes = new LinkedList<>();
        List<Triple> slines = getLines(s);
        List<Triple> olines = getLines(o);
        //linee presenti in o ma non presenti in s
        olines.removeAll(slines);
        for (Triple oline : olines) {
            indexes.add(new Pair(oline.start, oline.end));
        }
        return indexes;
    }

    private List<Triple> getLines(String s) {
        List<Triple> lines = new LinkedList<>();
        int lineStart = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\n') {
                lines.add(new Triple(lineStart, i, s.substring(lineStart, i)));
                lineStart = i + 1;
            }
        }
        return lines;
    }

    private void highlight(JTextArea text, List<Pair<Integer, Integer>> indexes, Color c) {
        Highlighter highlighter = text.getHighlighter();
        HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(c);
        highlighter.removeAllHighlights();
        for (Pair<Integer, Integer> p : indexes) {
            try {
                highlighter.addHighlight(p.getKey(), p.getValue(), painter);
            } catch (BadLocationException ex) {
                Logger.getLogger(GUIHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
        stat = new Statistics();
        Simulation simulation = new Simulation(scoder, ccoder, errorModel, stat, file.getAbsolutePath());
        long t1 = System.currentTimeMillis();
        simulation.execute();
        long t2 = System.currentTimeMillis();
        ProvaGUI.getInstance().getInputText().append("\n---------------------------------------------------------------");
        ProvaGUI.getInstance().getInputText().append("\nRitardo: " + (t2 - t1) + " ms");
        ProvaGUI.getInstance().getInputText().append("\nritardo cod sorg: " + stat.getSourceCodingTime() + " ms");
        ProvaGUI.getInstance().getInputText().append("\nritardo cod canale: " + stat.getChannelCodingTime() + " ms");
        ProvaGUI.getInstance().getInputText().append("\nritardo decod canale: " + stat.getChannelDecodingTime() + " ms");
        ProvaGUI.getInstance().getInputText().append("\nritardo decod sorg: " + stat.getSourceDecodingTime() + " ms");

        ProvaGUI.getInstance().getInputText().append("\n\ndimensione file iniziale " + stat.getInitialSize() + " byte");
        ProvaGUI.getInstance().getInputText().append("\ndimensione file compressioneSorgente " + stat.getSourceCodeSize() + " byte");
        ProvaGUI.getInstance().getInputText().append("\ncompression rate: " + stat.getCompressionRate() + "\n");

        ProvaGUI.getInstance().getInputText().append("\nerror rate cod canale " + stat.getChannelDecodingErrorRate() * 100 + " %");
        ProvaGUI.getInstance().getInputText().append("\nerror rate canale solo cod sorgente " + stat.getOnlySourceCodeChannelErrorRate() * 100 + " %");
        ProvaGUI.getInstance().getInputText().append("\nrecovery rate del codificatore di canale " + stat.getErrorRecoveryRate() * 100 + " %\n");

        JTextArea output = ProvaGUI.getInstance().getOutputText();
        String outString = GenericUtils.readFile(Simulation.outputPath(file.getAbsolutePath()), StandardCharsets.UTF_8);
        output.setText(outString);
        output.setEditable(false);
        String inString = GenericUtils.readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
        highlight(output, findDifferences(inString, outString), new Color(0.9f, 0.1f, 0f, 0.1f));
        highlight(ProvaGUI.getInstance().getSourceText(), findDifferences(outString, inString), new Color(0f, 0.7f, 0.3f, 0.2f));
    }

}
