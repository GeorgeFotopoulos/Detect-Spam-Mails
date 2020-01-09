public class ID3node {
	
	public String word;
	public double frequency;
	public boolean decision;
	ID3node left, right;
	
	ID3node(String word, double frequency, boolean decision) {
		this.word = word;
		this.frequency = frequency;
		this.decision = decision;
		left = null;
		right = null;
	}
	
	/*  true represents Spam
	false represents Ham */
	ID3node(boolean decision) {
		this.decision = decision;
	}
	
}