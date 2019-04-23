package codificatori_tmp;

import java.io.*;
import java.util.*;

import coders.Coder;
import coders.Message;
import codificatori_tmp.Nodo.IndiceSuccessore;
import utils.ConvolutionalUtils;

public class ConvolutionalCoder implements Coder {

    private int bitUtili = 0;

    private int N, n, k;  //t � la sequenza di bit che si vuole codificare (quanti gruppi di k bit si vogliono codificare)
    boolean[][] g;
    private HashMap<Integer, LinkedList<Nodo>> traliccio;
    private int numeroLivelli;

    public ConvolutionalCoder(int N, int n, int numLivelli, int k){
        this.N = N;
        this.n = n;
        numeroLivelli = numLivelli;
        this.k = k;

        GeneratorePolinomi generatore = new GeneratorePolinomi();
        this.g = generatore.getPolinomio(n, N);

        createTrellis();
    }

    
    private void createTrellis() {
    	traliccio = new HashMap<>();
        boolean[] memoria = new boolean[(N - 1) * k];
        Nodo radice = new Nodo(N, n, memoria, g, k);

        traliccio.put(0, new LinkedList<>());
        traliccio.get(0).add(radice);
        for (int i = 0; i < numeroLivelli; i++) {
            traliccio.put(i + 1, new LinkedList<>());
            for (Nodo y : traliccio.get(i)) { //consideriamo tutti i nodi al livello i-esimo
                HashMap<Nodo.IndiceSuccessore, boolean[]> successori = y.getSuccessori();
                for (HashMap.Entry<Nodo.IndiceSuccessore, boolean[]> entry : successori.entrySet()) {
                    if (!nodoPresenteSulLivello(i + 1, entry.getValue()))
                        traliccio.get(i + 1).add(new Nodo(N, n, entry.getValue(), g, k));
                }
            }
        }//for
	}


	public void encodeFile(File inputFile, File outputFile) {
        try {
            inputStream = new FileInputStream(inputFile);
            bis = new BufferedInputStream(inputStream);

            outputStream = new FileOutputStream(outputFile);
            bos = new BufferedOutputStream(outputStream);

            LinkedList<Boolean> bufferAppoggio = new LinkedList<>();
            int byteLetto = bis.read();

            while (byteLetto != -1) {
                BitUtility.copiaByte(BitUtility.fromIntToByte(byteLetto, 8), bufferAppoggio);
                while (bufferAppoggio.size() >= 8 - ((N - 1))) {
                    boolean[] tmp = new boolean[8];
                    BitUtility.estraiByte(bufferAppoggio, tmp, (8) - (N - 1));
                    for (int i = tmp.length; i < 8; i++)
                        tmp[i] = false;     //per reset registri
                    boolean[] byteCodificato = codifica(tmp);
                    //stampaByte(byteCodificato);
                    bos.write(BitUtility.funzioneSupportoCodificaConvoluzionale1(byteCodificato));
                }

                byteLetto = bis.read();

            }//while
            //nel buffer di appoggio avro (N-1) o 2(N-1) o 3(N-1) ecc
            bitUtili = bufferAppoggio.size();

            bos.write(BitUtility.funzioneSupportoCodificaConvoluzionale1(codifica(BitUtility.aggiungiPadding(bufferAppoggio))));
            bis.close();
            inputStream.close();
            bos.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void decodeFile(File inputFile, File outputFile) {
        try {
            inputStream = new FileInputStream(inputFile);
            bis = new BufferedInputStream(inputStream);

            outputStream = new FileOutputStream(outputFile);
            bos = new BufferedOutputStream(outputStream);

            LinkedList<Boolean> bufferOutput = new LinkedList<>();
            byte[] byteLetti = new byte[n];
            int flag = bis.read(byteLetti);
            boolean padding = bis.available() <= 0;

            while (flag != -1) {
                boolean[] decodifica = decodifica(BitUtility.funzioneSupportoCodificaConvoluzionale2(byteLetti, n));
                if (!padding) {
                    BitUtility.accumulaBit(decodifica, bufferOutput, 8 - (N - 1));
                    while (bufferOutput.size() >= 8)
                        bos.write(BitUtility.fromByteToInt(BitUtility.estraiByte(bufferOutput)));
                } else {
                    if (bitUtili > 0) {
                        BitUtility.accumulaBit(decodifica, bufferOutput, bitUtili);
                        bos.write(BitUtility.fromByteToInt(BitUtility.estraiByte(bufferOutput)));
                    }
                }
                flag = bis.read(byteLetti);
                padding = bis.available() <= 0;
            }

            bis.close();
            inputStream.close();
            bos.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean nodoPresenteSulLivello(int i, boolean[] stato) {
        for (Nodo n : traliccio.get(i))
            if (stessoStato(n.getStato(), stato))
                return true;
        return false;
    }

    private boolean stessoStato(boolean[] s1, boolean[] s2) {
        for (int i = 0; i < s1.length; i++)
            if (s1[i] != s2[i])
                return false;
        return true;
    }

    private Nodo getNodoFromStato(boolean[] stato, int livello) {
        for (Nodo n : traliccio.get(livello)) {
            if (stessoStato(n.getStato(), stato)) {
                return n;
            }
        }
        return null;
    }

    private void clear() {
        for (int i = 0; i <= numeroLivelli; i++)
            for (Nodo n : traliccio.get(i))
                n.getBranchMetric().clear();
    }

    //L'input deve essere un multiplo di k
    public boolean[] codifica(boolean[] input) {
        boolean[] risultato = new boolean[(input.length/k)*n];

        Nodo nodoCorrente = traliccio.get(0).getFirst();
        int j=0;
        for(int i=0; i<input.length; i+=k) {

            //prelevo i k bit da codificare
            boolean [] tmp = new boolean[k];
            int pos = 0;
            for (int x = i; x < i+k; x++) {
                tmp[pos++] = input[x];
            }

            //calcolo la codifica dei bit considerati
            boolean[] output = nodoCorrente.getOutput(tmp);

            //copio la codifica nel risultato
            for(int x=0; x<n; x++){
                risultato[j++] = output[x];
            }

            if(i < numeroLivelli-1) {
                boolean[] statoSuccessivo = nodoCorrente.getSuccessore(tmp);
                for(Nodo n : traliccio.get(i+1)) {
                    if(stessoStato(statoSuccessivo, n.getStato())) {
                        nodoCorrente = n;
                        break;
                    }
                }
            }
        }//for
        return risultato;
    }//calcolaOutput

    private boolean[] estraiSottoStringa(int indice, boolean[] input) {
        boolean[] tmp = new boolean[k];
        for (int i = indice; i < indice + k; i++) {
            tmp[i] = input[i];
        }
        return tmp;
    }

    public boolean[] decodifica(boolean[] input) {
        LinkedList<boolean[]> ris = new LinkedList<>();
        boolean[] risultato = new boolean[numeroLivelli * k];
        for (int i = 0; i < numeroLivelli; i++) {
            boolean[] inputParziale = new boolean[n];

            System.arraycopy(input, i * n, inputParziale, 0, n);
            LinkedList<Nodo> stati = traliccio.get(i);

            for (int j = 0; j < stati.size(); j++) {

                //Itero su tutti i possibili successori del nodo corrente
                for (HashMap.Entry<Nodo.IndiceSuccessore, boolean[]> entry : stati.get(0).getSuccessori().entrySet()) {

                    Nodo n = getNodoFromStato(stati.get(j).getSuccessore(entry.getKey().getIndice()), i + 1);
                    int lunghezzaCammino = stati.get(j).getLunghezzaCammino();
                    n.calcolaMetriche(stati.get(j), inputParziale, stati.get(j).getOutput(entry.getKey().getIndice()), entry.getKey().getIndice(), lunghezzaCammino);

                }//for

            }//for

            //Alla fine dell'analisi del livello i-esimo, procedo con il calcolo delle metriche di ciascun nodo trovato

            for (Nodo n : traliccio.get(i + 1)) {
                n.getBranchMetric().impostaMetricaMinima();
                n.setLunghezzaCammino(n.getBranchMetric().getDistanza());
            }
        }//for
        boolean[] stato = new boolean[(N - 1) * k];

        Nodo corrente = this.getNodoFromStato(stato, numeroLivelli);

        for (int i = numeroLivelli - 1; i >= 0; i--) {
            ris.addFirst(corrente.getBranchMetric().getDecodifica());
            if (i > 0)
                corrente = corrente.getBranchMetric().getPrecedente();
        }
        clear();
        int pos = 0;
        for (Iterator iterator = ris.iterator(); iterator.hasNext(); ) {
            boolean[] bs = (boolean[]) iterator.next();
            for (int i = 0; i < bs.length; i++) {
                risultato[pos++] = bs[i];
            }
        }
        return risultato;
    }//decodifica


    public static void main(String[] args) throws Exception{
        int N = 3;
        int n = 6;
        int k = 1;
        boolean[] input = new boolean[]{true,false,true,true,false,true,false,false};
        System.out.println("STRINGA = " + ConvolutionalUtils.stampaByte(input));

        ConvolutionalCoder traliccio = new ConvolutionalCoder(N, n, 8, k);

        boolean[] output = traliccio.codifica(input);
        System.out.println("CODIFICA = " + ConvolutionalUtils.stampaByte(output));
        boolean[] decodifica = traliccio.decodifica(output);
        System.out.println("DECODIFICA = " + ConvolutionalUtils.stampaByte(decodifica));
    }

	@Override
	public Message encode(String input) {
		// TODO Auto-generated method stub
		return null;
	}

}
