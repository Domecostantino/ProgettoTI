package codificatori_conv_rip;

import java.util.Arrays;

public class Biforcazione {
	private byte [] statoNonPreso;
	private int passoBiforcazione;
	private int erroreBiforcazione;

	public int getErroreBiforcazione() {
		return erroreBiforcazione;
	}

	public void setErroreBiforcazione(int erroreBiforcazione) {
		this.erroreBiforcazione = erroreBiforcazione;
	}

	public byte [] getStatoNonPreso() {
		return statoNonPreso;
	}

	public void setStatoNonPreso(byte [] statoNonPreso) {
		this.statoNonPreso = statoNonPreso;
	}

	public int getPassoBiforcazione() {
		return passoBiforcazione;
	}

	public void setPassoBiforcazione(int passoBiforcazione) {
		this.passoBiforcazione = passoBiforcazione;
	}

	public String toString() {
		return Arrays.toString(statoNonPreso) + " err=" + erroreBiforcazione + " passo=" + passoBiforcazione;
	}
}
