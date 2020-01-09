public class WordsInfo {//Parameter for the HashMap
	
	public int WordsCount;
	public int InMailsFound;
	boolean updated; //an int that's made to help us calculate the mailsFound value
	

	public WordsInfo() {
		WordsCount = 1;
		InMailsFound = 1;
		updated = true;
	}
	
	public WordsInfo(int words, int inMail, boolean upd) {
		WordsCount = words;
		InMailsFound = inMail;
		updated = upd;
	}
	
}