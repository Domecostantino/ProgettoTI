package coders.convolutional;

public class ConvolutionalHeader {
	private int K, r;

	public ConvolutionalHeader(int K, int r) {
		this.K = K;
		this.r = r;
	}

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}
}
