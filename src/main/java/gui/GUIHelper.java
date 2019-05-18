/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import channel.CanaleSimmetricoBinario;
import channel.ChannelModel;
import channel.GilbertElliot;
import coders.ChannelCoder;
import coders.SourceCoder;
import coders.LZW.LZWSourceCoder;
import coders.convolutional.ConvolutionalChannelCoder;
import coders.deflate.DeflateCoder;
import coders.hamming.HammingChannelCoder;
import coders.huffman.HuffmanSourceCoder;
import coders.repetition.ConcatenatedChannelCoder;
import coders.repetition.RepChannelCoder;
import java.awt.Color;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import utils.GestioneDB;
import utils.GestioneDB.CHAN_COD;
import utils.GestioneDB.SOURCE_COD;
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
    private GestioneDB db = new GestioneDB();
    private Simulation simulation;
    
    private static final Map<String, String> NAMES_MAP = createMap();

    private static Map<String, String> createMap() {
        Map<String, String> result = new HashMap<String, String>();
        result.put(SOURCE_COD.HUFFMAN.toString(), "HUFFMAN");
        result.put(SOURCE_COD.LZW.toString(), "LEMPEL ZIV WELCH");
        result.put(SOURCE_COD.DEFLATE.toString(), "DEFLATE");
        result.put(CHAN_COD.RIP_3.toString(), "RIPETIZIONE R=3");
        result.put(CHAN_COD.RIP_5.toString(), "RIPETIZIONE R=5");
        result.put(CHAN_COD.RIP_7.toString(), "RIPETIZIONE R=7");
        result.put(CHAN_COD.RIP_9.toString(), "RIPETIZIONE R=9");
        result.put(CHAN_COD.HAMM_7_4.toString(), "HAMMING 7-4");
        result.put(CHAN_COD.HAMM_15_11.toString(), "HAMMING 15-11");
        result.put(CHAN_COD.HAMM_31_26.toString(), "HAMMING 26-31");
        result.put(CHAN_COD.CONC_3_3.toString(), "CONCATENATO 3-3");
        result.put(CHAN_COD.CONC_3_5.toString(), "CONCATENATO 3-5");
        result.put(CHAN_COD.CONC_5_5.toString(), "CONCATENATO 5-5");
        result.put(CHAN_COD.CONV_2_3.toString(), "CONVOLUZIONALE R=2 K=3");
        result.put(CHAN_COD.CONV_2_4.toString(), "CONVOLUZIONALE R=2 K=4");
        result.put(CHAN_COD.CONV_2_5.toString(), "CONVOLUZIONALE R=2 K=5");
        result.put(CHAN_COD.CONV_2_6.toString(), "CONVOLUZIONALE R=2 K=6");
        result.put(CHAN_COD.CONV_2_7.toString(), "CONVOLUZIONALE R=2 K=7");
        result.put(CHAN_COD.CONV_3_3.toString(), "CONVOLUZIONALE R=3 K=3");
        result.put(CHAN_COD.CONV_3_4.toString(), "CONVOLUZIONALE R=3 K=4");
        result.put(CHAN_COD.CONV_3_5.toString(), "CONVOLUZIONALE R=3 K=5");
        result.put(CHAN_COD.CONV_3_6.toString(), "CONVOLUZIONALE R=3 K=6");
        result.put(CHAN_COD.CONV_3_7.toString(), "CONVOLUZIONALE R=3 K=7");
        return Collections.unmodifiableMap(result);
    }
    

    private GUIHelper() {
    }

    public static synchronized GUIHelper getInstance() {
        if (instance == null) {
            instance = new GUIHelper();
        }
        return instance;
    }

    void showMeanRecoveryRatePerChannelModel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        HashMap<String, Double> diz = db.getRecoveryRatePerChannelModel(simulation);

        for (Map.Entry<String, Double> e : diz.entrySet()) {
            dataset.setValue(e.getValue(), e.getKey(), "");
        }
        GestioneDB.CHAN_COD chan_cod = db.getChanCodeSim(simulation.getChannelCoder().getClass().getName(), simulation);
        JFreeChart chart = ChartFactory.createBarChart3D("Recovery rate medio per modello di canale\nCod di canale: " +NAMES_MAP.get(chan_cod.toString()), "", "Rate", dataset);

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

    void showCompressionRatePerFile() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        HashMap<String, Double> diz = db.getCompressionRatePerFile(simulation);

        for (Map.Entry<String, Double> e : diz.entrySet()) {
            dataset.setValue(e.getValue(), e.getKey(), "");
        }
        GestioneDB.SOURCE_COD source_cod = db.getSourceCodeSim(simulation.getSourceCoder().getClass().getName());

        JFreeChart chart = ChartFactory.createBarChart3D("Compression rate per file\n"+"Cod di sorgente: "+NAMES_MAP.get(source_cod.toString()), "", "Rate", dataset);

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

    void showDelayMeans() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        LinkedList<Double> list = db.calcolaRitardiMedi(simulation);
        dataset.setValue("Codifica di sorgente", list.get(0));
        dataset.setValue("Codifica di canale", list.get(1));
        dataset.setValue("Tempo di invio", list.get(2));
        dataset.setValue("Decodifica di canale", list.get(3));
        dataset.setValue("Decodifica di sorgente", list.get(4));
        
        GestioneDB.CHAN_COD chan_cod = db.getChanCodeSim(simulation.getChannelCoder().getClass().getName(), simulation);
        GestioneDB.SOURCE_COD source_cod = db.getSourceCodeSim(simulation.getSourceCoder().getClass().getName());
        
        JFreeChart chart = ChartFactory.createPieChart3D(
                "Tempi degli step di simulazione\nCod di sorgente: "+NAMES_MAP.get(source_cod.toString())+", Cod di canale: "+NAMES_MAP.get(chan_cod.toString()), // chart title
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

    void showCompressionFactor() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(stat.getInitialSize(), "Dimensione input", "");
        dataset.setValue(stat.getSourceCodeSize(), "Dimensione codifica", "");
        
        GestioneDB.SOURCE_COD source_cod = db.getSourceCodeSim(simulation.getSourceCoder().getClass().getName());
        JFreeChart chart = ChartFactory.createBarChart3D("Compressione\n"+NAMES_MAP.get(source_cod.toString()), "", "Dimensione", dataset);

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
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(stat.getOnlySourceCodeChannelErrorRate(), "Error rate senza\ncodifica di canale", "");
        dataset.setValue(stat.getChannelDecodingErrorRate(), "Error rate con\ncodifica di canale", "");
        GestioneDB.CHAN_COD chan_cod = db.getChanCodeSim(simulation.getChannelCoder().getClass().getName(), simulation);
        JFreeChart chart = ChartFactory.createBarChart3D("Codifica di canale: "+NAMES_MAP.get(chan_cod.toString()), "", "%", dataset);

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

    void showTimeSlices() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Codifica di sorgente", stat.getSourceCodingTime());
        dataset.setValue("Codifica di canale", stat.getChannelCodingTime());
        dataset.setValue("Tempo di invio", stat.getSendingTime());
        dataset.setValue("Decodifica di canale", stat.getChannelDecodingTime());
        dataset.setValue("Decodifica di sorgente", stat.getSourceDecodingTime());
        
        GestioneDB.CHAN_COD chan_cod = db.getChanCodeSim(simulation.getChannelCoder().getClass().getName(), simulation);
        GestioneDB.SOURCE_COD source_cod = db.getSourceCodeSim(simulation.getSourceCoder().getClass().getName());

        JFreeChart chart = ChartFactory.createPieChart3D(
                "Tempi degli step di simulazione\nCod di sorgente: "+NAMES_MAP.get(source_cod.toString())+", Cod di canale: "+NAMES_MAP.get(chan_cod.toString()), // chart title
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
        JTextArea source = GUI.getInstance().getSourceText();
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
                scoder = new HuffmanSourceCoder();
                break;
            case LZW:
                scoder = new LZWSourceCoder();
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
        simulation = new Simulation(scoder, ccoder, errorModel, stat, file.getName());
        simulation.execute();
        GUI.getInstance().getInputText().append("Ritardo: " + stat.getTotalTime() + " ms");
        GUI.getInstance().getInputText().append("\nritardo cod sorg: " + stat.getSourceCodingTime() + " ms");
        GUI.getInstance().getInputText().append("\nritardo cod canale: " + stat.getChannelCodingTime() + " ms");
        GUI.getInstance().getInputText().append("\nritardo di invio: " + stat.getSendingTime() + " ms");
        GUI.getInstance().getInputText().append("\nritardo decod canale: " + stat.getChannelDecodingTime() + " ms");
        GUI.getInstance().getInputText().append("\nritardo decod sorg: " + stat.getSourceDecodingTime() + " ms");

        GUI.getInstance().getInputText().append("\n\ndimensione file iniziale " + stat.getInitialSize() + " byte");
        GUI.getInstance().getInputText().append("\ndimensione file compressioneSorgente " + stat.getSourceCodeSize() + " byte");
        GUI.getInstance().getInputText().append("\ncompression rate: " + stat.getCompressionRate() + "\n");

        GUI.getInstance().getInputText().append("\nerror rate cod canale " + stat.getChannelDecodingErrorRate() + " %");
        GUI.getInstance().getInputText().append("\nerror rate canale solo cod sorgente " + stat.getOnlySourceCodeChannelErrorRate() + " %");
        GUI.getInstance().getInputText().append("\nrecovery rate del codificatore di canale " + stat.getErrorRecoveryRate() );
        GUI.getInstance().getInputText().append("\n---------------------------------------------------------------\n\n");

        //Inserimento valori nel DB
        db.insertSimulation(simulation);

        JTextArea output = GUI.getInstance().getOutputText();
        String outString = GenericUtils.readFile(Simulation.outputPath(file.getAbsolutePath()), StandardCharsets.UTF_8);
        output.setText(outString);
        output.setEditable(false);
        String inString = GenericUtils.readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
        highlight(output, findDifferences(inString, outString), new Color(0.9f, 0.1f, 0f, 0.1f));
        highlight(GUI.getInstance().getSourceText(), findDifferences(outString, inString), new Color(0f, 0.7f, 0.3f, 0.2f));
    }

}
