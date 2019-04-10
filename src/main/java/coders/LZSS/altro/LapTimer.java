package coders.LZSS.altro;

import java.io.OutputStream;
import java.io.PrintWriter;

public class LapTimer {
	public LapTimer(){
		this(System.out);
		reset();
	}

	public LapTimer(OutputStream os){
		writer = new PrintWriter(os);
		reset();
	}

	public void reset(){
		prev = System.nanoTime();
	}

	public void reset(String format, Object... args){
		println(format, args);
		prev = System.nanoTime();
	}

	public long lapMillis(){
		long c = System.nanoTime();
		long ret = c - prev;
		prev = c;
		return ret / 1000000;
	}

	public long lapNanos(){
		long c = System.nanoTime();
		long ret = c - prev;
		prev = c;
		return ret;
	}

	public long lapMillis(String format, Object... args){
		long ret = lapMillis();
		println("[" + ret + "ms]: " + format, args);
		reset();
		return ret;
	}

	public long lapNanos(String format, Object... args){
		long ret = lapNanos();
		println("[" + ret + "ns]: " + format, args);
		reset();
		return ret;
	}

	private void println(String format, Object... args){
		writer.println(String.format(format, args));
		writer.flush();
	}

	private PrintWriter writer;
	private long prev;
}
