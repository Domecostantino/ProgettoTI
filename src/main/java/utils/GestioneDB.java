package utils;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import channel.CanaleSimmetricoBinario;
import channel.GilbertElliot;
import coders.convolutional.ConvolutionalChannelCoder;
import coders.hamming.HammingChannelCoder;
import coders.huffman.HuffmanSourceCoder;
import coders.repetition.ConcatenatedChannelCoder;
import coders.repetition.RepChannelCoder;
import simulator.Simulation;

public class GestioneDB {

	private static Connection conn;
	private static String DBurl = "jdbc:postgresql://localhost/dbTeoriaInf";
	private static final String USER = "postgres";
	private static final String PASS = "password";

	public enum SOURCE_COD {
		DEFLATE, HUFFMAN, LZW
	}

	public enum CHAN_COD {
		CONV_2_3, CONV_2_4, CONV_2_5, CONV_2_6, CONV_2_7, CONV_3_3, CONV_3_4, CONV_3_5, CONV_3_6, CONV_3_7, RIP_3,
		RIP_5, RIP_7, RIP_9, HAMM_7_4, HAMM_15_11, HAMM_31_26, CONC_3_3, CONC_3_5, CONC_5_5
	}

	public enum CHANNEL {
		BSC_01, BSC_001, BSC_0001, GE_HARD, GE_SOFT
	}

	public GestioneDB() {
		//System.out.println("Connecting to database...");
		//inizializzaDB();
	}

	public boolean inizializzaDB() {
		boolean connection=false;
		Statement stmt = null;
		try {
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DBurl, USER, PASS);
			System.out.println("Opened database successfully");

			stmt = conn.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS SIMULATIONS " + "(id SERIAL PRIMARY KEY NOT NULL,"
					+ "FILENAME TEXT, SOURCE_COD TEXT, CHAN_COD TEXT, CHANNEL TEXT, SOURCE_COD_TIME BIGINT,"
					+ "SENDING_TIME BIGINT, CHAN_COD_TIME BIGINT, SOURCE_DECODE_TIME BIGINT, CHAN_DECODE_TIME BIGINT, INITIAL_SIZE BIGINT,"
					+ " SOURCE_COD_SIZE BIGINT, COMPRESSION_RATE FLOAT, CHAN_DECODE_ERROR_RATE FLOAT,"
					+ " ONLYSOURCE_CODE_ERROR_RATE FLOAT, RECOVERY_RATE FLOAT);";

			stmt.executeUpdate(sql);
			stmt.close();
			connection=true;
			System.out.println("Table created successfully");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			//System.exit(0);
		}
		return connection;
	}

	public void insertSimulation(Simulation simulation) {
		try {
			String filename = simulation.getFileInputPath();
                        String[] path=filename.split("/|\\\\");
                        filename=path[path.length-1];
			filename = filename.substring(0, filename.length() - 4);
			SOURCE_COD source_cod = null;
			CHAN_COD chan_cod = null;
			CHANNEL channel = null;
			Statistics stat = simulation.getStatistics();
			long sourceCodeTime = stat.getSourceCodingTime();
			long channelCodeTime = stat.getChannelCodingTime();
			long sendingTime = stat.getSendingTime();
			long sourceDecodeTime = stat.getSourceDecodingTime();
			System.out.println(sendingTime);
			long channelDecodeTime = stat.getChannelDecodingTime();
			long initialSize = stat.getInitialSize();
			long sourceCodeSize = stat.getSourceCodeSize();
			float compressionRate = (float) stat.getCompressionRate();
			float chanDecodeErrorRate = (float) stat.getChannelDecodingErrorRate();
			float onlySourceDecodeErrorRate = (float) stat.getOnlySourceCodeChannelErrorRate();
			float recoveryRate = (float) stat.getErrorRecoveryRate();

			// settiamo codificatore sorgente
			source_cod = getSourceCodeSim(simulation.getSourceCoder().getClass().getName());

			// settiamo codificatore canale
			chan_cod = getChanCodeSim(simulation.getChannelCoder().getClass().getName(), simulation);

			// settiamo canale
			channel = getChannelSim(simulation.getChannel().getClass().getName(), simulation);

			Statement stmt = conn.createStatement();
			String sql = "INSERT INTO SIMULATIONS (FILENAME,SOURCE_COD,CHAN_COD,CHANNEL,SOURCE_COD_TIME,CHAN_COD_TIME,SENDING_TIME,"
					+ "SOURCE_DECODE_TIME,CHAN_DECODE_TIME,INITIAL_SIZE,SOURCE_COD_SIZE,COMPRESSION_RATE,CHAN_DECODE_ERROR_RATE,ONLYSOURCE_CODE_ERROR_RATE,RECOVERY_RATE) VALUES ('"
					+ filename + "','" + source_cod + "','" + chan_cod + "','" + channel + "','" + sourceCodeTime
					+ "','" + channelCodeTime + "','" + sendingTime + "','" + sourceDecodeTime + "','"
					+ channelDecodeTime + "','" + initialSize + "','" + sourceCodeSize + "','" + compressionRate + "','"
					+ chanDecodeErrorRate + "','" + onlySourceDecodeErrorRate + "','" + recoveryRate + "');";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			System.err.println(e);
		}
	}

	public SOURCE_COD getSourceCodeSim(String nomeClasse) {
		switch (nomeClasse) { // TODO rivedere i packages
		case "coders.LZW.LZWSourceCoder":
			return SOURCE_COD.LZW;
		case "coders.deflate.DeflateCoder":
			return SOURCE_COD.DEFLATE;
		case "coders.huffman.HuffmanSourceCoder":
			return SOURCE_COD.HUFFMAN;
		}
		return null;
	}

	public CHANNEL getChannelSim(String nomeClasse, Simulation simulation) {
		switch (nomeClasse) {
		case "channel.CanaleSimmetricoBinario":
			CanaleSimmetricoBinario ch = (CanaleSimmetricoBinario) simulation.getChannel();
			switch (ch.getBer()) {
			case "0.1":
				return CHANNEL.BSC_01;
			case "0.01":
				return CHANNEL.BSC_001;
			case "0.001":
				return CHANNEL.BSC_0001;
			}
			break;
		case "channel.GilbertElliot":
			GilbertElliot ch2 = (GilbertElliot) simulation.getChannel();
			if (ch2.getType() == GilbertElliot.HARD)
				return CHANNEL.GE_HARD;
			else
				return CHANNEL.GE_SOFT;
		}
		return null;
	}

	public CHAN_COD getChanCodeSim(String nomeClasse, Simulation simulation) {
		switch (nomeClasse) {
		case "coders.hamming.HammingChannelCoder":
			HammingChannelCoder ch = (HammingChannelCoder) simulation.getChannelCoder();
			switch (ch.getR()) {
			case 3:
				return CHAN_COD.HAMM_7_4;
			case 4:
				return CHAN_COD.HAMM_15_11;
			case 5:
				return CHAN_COD.HAMM_31_26;
			}
			break;
		case "coders.convolutional.ConvolutionalChannelCoder":
			ConvolutionalChannelCoder ch1 = (ConvolutionalChannelCoder) simulation.getChannelCoder();
			switch (ch1.getR()) {
			case 2:
				switch (ch1.getK()) {
				case 3:
					return CHAN_COD.CONV_2_3;
				case 4:
					return CHAN_COD.CONV_2_4;
				case 5:
					return CHAN_COD.CONV_2_5;
				case 6:
					return CHAN_COD.CONV_2_6;
				case 7:
					return CHAN_COD.CONV_2_7;
				}
				break;

			case 3:
				switch (ch1.getK()) {
				case 3:
					return CHAN_COD.CONV_3_3;
				case 4:
					return CHAN_COD.CONV_3_4;
				case 5:
					return CHAN_COD.CONV_3_5;
				case 6:
					return CHAN_COD.CONV_3_6;
				case 7:
					return CHAN_COD.CONV_3_7;
				}
				break;
			}
			break;
		case "coders.repetition.ConcatenatedChannelCoder":
			ConcatenatedChannelCoder ch2 = (ConcatenatedChannelCoder) simulation.getChannelCoder();
			int[] repLev = ch2.getRep_per_level();
			if (repLev[0] == 3 && repLev[1] == 3)
				return CHAN_COD.CONC_3_3;
			else if (repLev[0] == 3 && repLev[1] == 5)
				return CHAN_COD.CONC_3_5;
			else
				return CHAN_COD.CONC_5_5;
		case "coders.repetition.RepChannelCoder":
			RepChannelCoder ch3 = (RepChannelCoder) simulation.getChannelCoder();
			switch (ch3.getR()) {
			case 3:
				return CHAN_COD.RIP_3;
			case 5:
				return CHAN_COD.RIP_5;
			case 7:
				return CHAN_COD.RIP_7;
			case 9:
				return CHAN_COD.RIP_9;
			}
			break;
		}
		return null;
	}

	public LinkedList<Double> calcolaRitardiMedi(Simulation sim) {
		LinkedList<Double> result = new LinkedList<>();
		SOURCE_COD source_cod = getSourceCodeSim(sim.getSourceCoder().getClass().getName());
		CHAN_COD chan_cod = getChanCodeSim(sim.getChannelCoder().getClass().getName(), sim);

		long sourceCodeTime = 0;
		long channelCodeTime = 0;
		long sendingTime = 0;
		long sourceDecodeTime = 0;
		long channelDecodeTime = 0;
		int resultSetSize = 0;
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM SIMULATIONS WHERE SOURCE_COD = '" + source_cod + "' AND CHAN_COD = '" + chan_cod
					+ "';";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				sourceCodeTime += rs.getLong("SOURCE_COD_TIME");
				channelCodeTime += rs.getLong("CHAN_COD_TIME");
				sendingTime += rs.getLong("SENDING_TIME");
				channelDecodeTime += rs.getLong("CHAN_DECODE_TIME");
				sourceDecodeTime += rs.getLong("SOURCE_DECODE_TIME");
				resultSetSize++;
			}
			System.out.println();
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.err.println(e);
		}

		result.add(sourceCodeTime / (double) resultSetSize);
		result.add(channelCodeTime / (double) resultSetSize);
		result.add(sendingTime / (double) resultSetSize);
		result.add(channelDecodeTime / (double) resultSetSize);
		result.add(sourceDecodeTime / (double) resultSetSize);

		System.out.println(result);
		return result;
	}

	public HashMap<String, Double> getCompressionRatePerFile(Simulation sim) {
		HashMap<String, Double> result = new HashMap<>();
		SOURCE_COD source_cod = getSourceCodeSim(sim.getSourceCoder().getClass().getName());

		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM SIMULATIONS WHERE SOURCE_COD = '" + source_cod + "';";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String fileNameRS = rs.getString("FILENAME");
				if (!result.containsKey(fileNameRS)) {
					double compressionRateFile = rs.getDouble("COMPRESSION_RATE");
					result.put(fileNameRS, compressionRateFile);
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.err.println(e);
		}
		System.out.println(result);
		return result;
	}
	
	
	public HashMap<String, Double> getRecoveryRatePerChannelModel(Simulation sim) {
		HashMap<String, Double> result = new HashMap<>();
		
		HashMap<String , LinkedList<Double>> elements = new HashMap<>();
		CHAN_COD chan_cod = getChanCodeSim(sim.getChannelCoder().getClass().getName(), sim);

		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM SIMULATIONS WHERE CHAN_COD = '" + chan_cod + "';";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String chanModel = rs.getString("CHANNEL");
				double recoveryRateChanModel = rs.getDouble("RECOVERY_RATE");
				if (!elements.containsKey(chanModel)) {
					LinkedList<Double> values = new LinkedList<>();
					values.add(recoveryRateChanModel);
					elements.put(chanModel, values);
				} else {
					elements.get(chanModel).add(recoveryRateChanModel);
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.err.println(e);
		}
		
		//calcoliamo la media
		for (Entry<String, LinkedList<Double>> iterable_element : elements.entrySet()) {
			double tmp = 0D;
			for (Double elem : iterable_element.getValue()) {
				tmp += elem;
			}
			tmp = tmp/iterable_element.getValue().size();
			result.put(iterable_element.getKey(), tmp);
		}
		
		System.out.println(result);
		return result;
	}
	
	

	public void chiudiConn() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		GestioneDB db = new GestioneDB();

		Simulation s = new Simulation(new HuffmanSourceCoder(), new ConvolutionalChannelCoder(3, 2),
				new CanaleSimmetricoBinario(0.01), new Statistics(), "Lorem ipsum.txt");
		s.execute();
		db.insertSimulation(s);

		db.calcolaRitardiMedi(s);
		
		db.getCompressionRatePerFile(s);
		
		db.getRecoveryRatePerChannelModel(s);

//		Connection conn = null;
//		Statement stmt = null;
//		try {
//			// STEP 3: Open a connection
//			System.out.println("Connecting to database...");
//			conn = DriverManager.getConnection(DBurl, USER, PASS);
//
//			// STEP 4: Execute a query
//			System.out.println("Creating statement...");
//			stmt = conn.createStatement();
//			String sql;
//			sql = "SELECT id, first, last, age FROM Employees";
//			ResultSet rs = stmt.executeQuery(sql);
//
//			// STEP 5: Extract data from result set
//			while (rs.next()) {
//				// Retrieve by column name
//				int id = rs.getInt("id");
//				int age = rs.getInt("age");
//				String first = rs.getString("first");
//				String last = rs.getString("last");
//
//				// Display values
//				System.out.print("ID: " + id);
//				System.out.print(", Age: " + age);
//				System.out.print(", First: " + first);
//				System.out.println(", Last: " + last);
//			}
//			// STEP 6: Clean-up environment
//			rs.close();
//			stmt.close();
//			conn.close();
//		} catch (SQLException se) {
//			// Handle errors for JDBC
//			se.printStackTrace();
//		} catch (Exception e) {
//			// Handle errors for Class.forName
//			e.printStackTrace();
//		} finally {
//			// finally block used to close resources
//			try {
//				if (stmt != null)
//					stmt.close();
//			} catch (SQLException se2) {
//			} // nothing we can do
//			try {
//				if (conn != null)
//					conn.close();
//			} catch (SQLException se) {
//				se.printStackTrace();
//			} // end finally try
//		} // end try
//		System.out.println("Goodbye!");
	}// end main

//	static void DBConnection() throws ClassNotFoundException {
//
//		Statement stmt = null;
//		try {
//			conn = getConnection();
//			System.out.println("Opened database successfully");
//
//			stmt = conn.createStatement();
//			String sql = "CREATE TABLE IF NOT EXISTS LISTAPRODOTTI " + "(NOME VARCHAR(32) PRIMARY KEY     NOT NULL,"
//					+ "POMODORI BOOL DEFAULT false," + "INSALATA BOOL DEFAULT false," + "PATATE BOOL DEFAULT false,"
//					+ "OLIO BOOL DEFAULT false," + "PANE BOOL DEFAULT false," + "MERENDINE BOOL DEFAULT false,"
//					+ "BISCOTTI BOOL DEFAULT false," + "BIRRE BOOL DEFAULT false," + "CAFFè BOOL DEFAULT false,"
//					+ "PASTA BOOL DEFAULT false," + "RISO BOOL DEFAULT false," + "LATTE BOOL DEFAULT false,"
//					+ "YOGURT BOOL DEFAULT false," + "TEA BOOL DEFAULT false," + "VINO BOOL DEFAULT false,"
//					+ "FORMAGGI BOOL DEFAULT false," + "SALUMI BOOL DEFAULT false," + "SCATOLAME BOOL DEFAULT false,"
//					+ "SURGELATI BOOL DEFAULT false," + "VERDURE BOOL DEFAULT false," + "SNACKS BOOL DEFAULT false,"
//					+ "ALCOLICI BOOL DEFAULT false," + "AGRUMI BOOL DEFAULT false," + "ACQUA BOOL DEFAULT false,"
//					+ "CARNE BOOL DEFAULT false," + "PESCE BOOL DEFAULT false," + "DETERGENTI BOOL DEFAULT false);";
//
//			stmt.executeUpdate(sql);
//			stmt.close();
//		} catch (Exception e) {
//			System.err.println(e.getClass().getName() + ": " + e.getMessage());
//			System.exit(0);
//		}
//		System.out.println("Table created successfully");
//	}
//
//	static Connection getConnection() throws URISyntaxException, SQLException {
//
//		URI dbUri = new URI(System.getenv("DATABASE_URL"));
//
//		String username = dbUri.getUserInfo().split(":")[0];
//		String password = dbUri.getUserInfo().split(":")[1];
//		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath()
//				+ "?sslmode=require";
//
//		return DriverManager.getConnection(dbUrl, username, password);
//	}
//
//	static void aggiugiUtente(long chat_id) {
//		try {
//			Statement stmt = conn.createStatement();
//			String sql = "INSERT INTO LISTAPRODOTTI (NOME) VALUES ('" + chat_id + "');";
//			stmt.executeUpdate(sql);
//			stmt.close();
//		} catch (SQLException e) {
//			System.err.println(e);
//		}
//
//	}
//
//	static List<Prodotto> ripristinaUtente(long chat_id) {
//		List<Prodotto> listaProdotti = new ArrayList<>();
//		try {
//			Statement stmt = conn.createStatement();
//			String sql = "SELECT * FROM LISTAPRODOTTI WHERE NOME = '" + chat_id + "';";
//			ResultSet rs = stmt.executeQuery(sql);
//			rs.next();
//			rs.close();
//			stmt.close();
//		} catch (SQLException e) {
//			System.err.println(e);
//		}
//		return listaProdotti;
//
//	}
//
//	static Map<Long, List<Prodotto>> ripristinaListeUtenti() {
//		Map<Long, List<Prodotto>> mappa = new HashMap<>();
//		try {
//			Statement stmt = conn.createStatement();
//			ResultSet rs = stmt.executeQuery("SELECT * FROM LISTAPRODOTTI;");
//			while (rs.next()) {
//				String chat_id = rs.getString("nome");
//
//				mappa.put(Long.parseLong(chat_id), listaProdotti);
//			}
//			rs.close();
//			stmt.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return mappa;
//	}
//
//	static HashSet<Integer> ripristinaNegoziantiAbilitati() {
//		HashSet<Integer> negozianti = new HashSet<>();
//		try {
//			Statement stmt = conn.createStatement();
//			ResultSet rs = stmt.executeQuery("SELECT * FROM NEGOZIANTI;");
//			while (rs.next()) {
//				Integer chat_id = rs.getInt("codiceNegoziante");
//				negozianti.add(chat_id);
//			}
//			rs.close();
//			stmt.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return negozianti;
//	}
//
//	static void svuotaListaUtente(long chat_id) {
//		try {
//			Statement stmt = conn.createStatement();
//			String sql = "DELETE FROM LISTAPRODOTTI WHERE NOME='" + chat_id + "';";
//			stmt.executeUpdate(sql);
//			sql = "INSERT INTO LISTAPRODOTTI (NOME) VALUES ('" + chat_id + "');";
//			stmt.executeUpdate(sql);
//			stmt.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	static void aggiungiProdotto(Long chat_id, String nomeP) {
//		try {
//			Statement stmt = conn.createStatement();
//			String sql = "UPDATE LISTAPRODOTTI SET " + nomeP + " = TRUE WHERE NOME = '" + chat_id + "';";
//			stmt.executeUpdate(sql);
//			stmt.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	static void rimuoviProdotto(long chat_id, String nomeP) {
//		try {
//			Statement stmt = conn.createStatement();
//			String sql = "UPDATE LISTAPRODOTTI SET " + nomeP + " = FALSE WHERE NOME = '" + chat_id + "';";
//			stmt.executeUpdate(sql);
//			stmt.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	static boolean esisteSupermarket(Long chatId) {
//		boolean result = false;
//		try {
//			Statement stmt = conn.createStatement();
//			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM SUPERMARKETS WHERE id = '" + chatId + "';");
//			rs.next();
//			if (rs.getInt(1) > 0)
//				result = true;
//			rs.close();
//			stmt.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
//
//	static String infoSupermarket(Long chatId) {
//		String result = "";
//		try {
//			Statement stmt = conn.createStatement();
//			ResultSet rs = stmt.executeQuery("SELECT * FROM SUPERMARKETS WHERE id = '" + chatId + "';");
//			StringBuilder sb = new StringBuilder();
//			rs.next();
//			sb.append("_Nome_ = ");
//			sb.append(rs.getString("nome"));
//			sb.append("\n");
//			sb.append("_Città_ = ");
//			sb.append(rs.getString("citta"));
//			sb.append("\n");
//			sb.append("_Indirizzo_ = ");
//			sb.append(rs.getString("indirizzo"));
//			sb.append("\n");
//			sb.append("_Posizione_ = ");
//			sb.append(rs.getDouble("latitudine"));
//			sb.append(", ");
//			sb.append(rs.getDouble("longitudine"));
//			result = sb.toString();
//			rs.close();
//			stmt.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
//
//	static Supermarket posizioneSupermarket(String nome) {
//		Supermarket sm = new Supermarket(0);
//		try {
//			Statement stmt = conn.createStatement();
//			ResultSet rs = stmt.executeQuery("SELECT * FROM SUPERMARKETS WHERE nome = '" + nome + "';");
//			rs.next();
//			sm.setLatitudine((float) rs.getDouble("latitudine"));
//			sm.setLongitudine((float) rs.getDouble("longitudine"));
//			rs.close();
//			stmt.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return sm;
//	}
//
//	static List<Supermarket> listaSupermercati() {
//		List<Supermarket> lista = new LinkedList<>();
//		try {
//			Statement stmt = conn.createStatement();
//			ResultSet rs = stmt.executeQuery("SELECT * FROM SUPERMARKETS;");
//			while (rs.next()) {
//				int chat_id = rs.getInt("id");
//				String nome = rs.getString("nome");
//				String citta = rs.getString("citta");
//				String ind = rs.getString("indirizzo");
//				float lat = rs.getFloat("latitudine");
//				float longi = rs.getFloat("longitudine");
//				Supermarket sm = new Supermarket(chat_id, nome, citta, ind, lat, longi);
//				lista.add(sm);
//			}
//			rs.close();
//			stmt.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return lista;
//	}
//
//	static boolean aggiornaSupermarket(Supermarket supermarket) {
//		try {
//			Statement stmt = conn.createStatement();
//			// prima si eliminano dal db il supermarket precedentemente salvato
//			// (conviene rispetto ad update)
//			String sql = "DELETE FROM SUPERMARKETS WHERE ID = " + supermarket.getProprietario() + ";";
//			stmt.executeUpdate(sql);
//
//			// poi inseriamo la nuova tupla
//			sql = "INSERT INTO SUPERMARKETS (ID,NOME,CITTA,INDIRIZZO,LATITUDINE,LONGITUDINE) " + "VALUES ("
//					+ supermarket.getProprietario() + ", '" + supermarket.getNome() + "', '" + supermarket.getCitta()
//					+ "', '" + supermarket.getIndirizzo() + "', " + supermarket.getLatitudine() + ", "
//					+ supermarket.getLongitudine() + ");";
//			stmt.executeUpdate(sql);
//			stmt.close();
//			return true;
//		} catch (Exception e) {
//			System.err.println(e);
//			return false;
//		}
//	}
//
//	static boolean aggiornaOfferte(int idSuperm, List<Offerta> listaOfferte) {
//		try {
//			Statement stmt = conn.createStatement();
//			// prima si eliminano dal db le offerte precedenti del supermercato
//			String sql = "DELETE FROM OFFERTE WHERE SUPERMARKET = " + idSuperm + ";";
//			stmt.executeUpdate(sql);
//
//			// poi inseriamo le nuove offerte
//			for (Offerta offerta : listaOfferte) {
//				sql = "INSERT INTO OFFERTE (SUPERMARKET,PREZZO,CATEGORIA,DESCRIZIONE) " + "VALUES ("
//						+ offerta.getIdSupermercato() + ", " + offerta.getPrezzo() + ", '"
//						+ offerta.getProdotto().getNome() + "', '" + offerta.getDescrizione() + "');";
//				stmt.executeUpdate(sql);
//			}
//			stmt.close();
//			return true;
//		} catch (Exception e) {
//			System.err.println(e);
//			return false;
//		}
//	}
//
//	static List<Offerta> estraiOfferteSupermarket(String nome, String indirizzo, List<Prodotto> listaPreferenze) {
//		List<Offerta> offerteVol = new LinkedList<>();
//		long idSuper;
//		try {
//			Statement stmt = conn.createStatement();
//
//			// recuperiamo l'id del supermercato dati il nome l'indirizzo
//			String sql = "SELECT ID FROM SUPERMARKETS WHERE NOME = '" + nome + "' AND INDIRIZZO = '" + indirizzo + "';";
//			ResultSet rs = stmt.executeQuery(sql);
//			rs.next();
//			idSuper = Long.parseLong(rs.getString(1));
//
//			// ora recuperiamo le offerte in accordo alla lista dell'utente
//			for (Prodotto categoria : listaPreferenze) {
//				sql = "SELECT * FROM OFFERTE WHERE SUPERMARKET = " + idSuper + " AND CATEGORIA = '"
//						+ categoria.getNome() + "';";
//				rs = stmt.executeQuery(sql);
//				while (rs.next()) {
//					Prodotto prod = new Prodotto(categoria.getNome());
//					double prezzo = rs.getDouble("prezzo");
//					String descr = rs.getString("descrizione");
//					Offerta off = new Offerta(prod, prezzo, descr);
//					offerteVol.add(off);
//				}
//			}
//			stmt.close();
//			return offerteVol;
//		} catch (Exception e) {
//			System.err.println(e);
//			return null;
//		}
//	}
//
//	static boolean offertePresentiNelProssimoSM(String nomeProsSM, List<Prodotto> listaUtenteCorrente) {
//		try {
//			Statement stmt = conn.createStatement();
//			long idSuper;
//			// recuperiamo l'id del supermercato dato il nome
//			String sql = "SELECT ID FROM SUPERMARKETS WHERE NOME = '" + nomeProsSM + "';";
//			ResultSet rs = stmt.executeQuery(sql);
//			rs.next();
//			idSuper = Long.parseLong(rs.getString(1));
//
//			// ora recuperiamo le offerte del prossimo supermarket
//			sql = "SELECT * FROM OFFERTE WHERE SUPERMARKET = " + idSuper + ";";
//			rs = stmt.executeQuery(sql);
//			// verifichiamo se c'è almeno un'offerta relativa alla lista dell'utente
//			while (rs.next()) {
//				if (listaUtenteCorrente.contains(new Prodotto(rs.getString("categoria"))))
//					return true;
//			}
//			stmt.close();
//			return false;
//		} catch (Exception e) {
//			System.err.println(e);
//			return false;
//		}
//	}
//

}
