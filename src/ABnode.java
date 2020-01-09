public class ABnode {
	
	String word;
	ABnode left, right;
	int dec;
	
	ABnode(String name) {
		this.word = name;
	}
	
	ABnode(int dec) {
		this.dec = dec;
	}
	
}