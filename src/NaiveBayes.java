import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

/* NTYMENOS: C:/Users/GeorgeF/Downloads/Eclipse Workspace/
 * PATRIKIS: C:/Users/geopa/Downloads/
 * FOTOPOULOS: C:/Users/GeorgeF/Downloads/Eclipse Workspace/
*/

public class NaiveBayes {
	
	static int foundSpam = 0, foundHam = 0, spamWordsCount = 0, hamWordsCount = 0;
	static StringTokenizer st1;
	static HashMap<String, Integer> spamhmap = new HashMap<String, Integer>();
	static HashMap<String, Integer> hamhmap = new HashMap<String, Integer>();
	static boolean testOrTrain; // trainOrTest=false-same folders, trainOrTest=true(FN,FP,TN,TP for Enron3)-different folders
	
	
	public static void main(String[] args) throws IOException {
		
		int spamMails, hamMails;
		double boundDiv = 1;//used to take portions of the total mails(0.5 ,half of the total mails in the folder.)
		testOrTrain = false;
		
		File trainSpam = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron5/spam");//folder used for training spams.
		spamMails = (int) (MailReader.findFolderSize(trainSpam) * boundDiv);
		spamWordsCount = MailReader.listFilesForFolder(trainSpam, spamhmap, spamMails);
		File trainHam = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron2/ham");//folder used for training hams.
		hamMails = (int) (MailReader.findFolderSize(trainHam) * boundDiv);
		hamWordsCount = MailReader.listFilesForFolder(trainHam, hamhmap, hamMails);
		
		System.out.println("Spam Words: " + spamWordsCount);
		System.out.println("Ham Words: " + hamWordsCount + "\n");
		
		File folderHam = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron3/ham/");//folder used for testing hams.
		if(!testOrTrain) folderHam = trainHam;
		listFilesForFolder(folderHam, hamMails);
		System.out.println("--- HAM MESSAGES ---\nSPAM: " + foundSpam + ", HAM: " + foundHam + "\n");
		
		foundSpam = 0;
		foundHam = 0;
		
		File folderSpam = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron3/spam/");//folder used for testing spams.
		if(!testOrTrain) folderSpam = trainSpam;
		listFilesForFolder(folderSpam, spamMails);
		System.out.println("--- SPAM MESSAGES ---\nSPAM: " + foundSpam + ", HAM: " + foundHam);
		
	}
	
	// A method that runs the naiveBayesAlg algorithm for the mails int the specified folder given in main.
	public static void listFilesForFolder(final File folder, int mails) throws IOException {
		int counter = 0;
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry, mails);
	        } else {
	            naiveBayesAlg(folder + "/" + fileEntry.getName());
	            counter++;
	            if(!testOrTrain)//testing or training
	            	if(counter==mails) break;//a counter used for testing
	        }
	    }
	}
	
	/* It takes a single mail as a parameter every time
	 * and runs the Naive Bayes algorithm for every word.
	 */
	public static void naiveBayesAlg(String mailToCheck) throws IOException {
		
		String temp, FullMail = MailReader.readFile(mailToCheck);
		st1 = new StringTokenizer(FullMail);
		double SpamPos = 0, HamPos = 0;
		
		for ( ; st1.hasMoreTokens(); ) {
    		temp = st1.nextToken();
    		if(hamhmap.get(temp)==null) {
    			HamPos += Math.log1p((double) 1 / ((double) hamWordsCount));
    		} else {
    			HamPos += Math.log1p((double) (hamhmap.get(temp) + 1) / ((double) hamWordsCount));
    		}
    		
    		if(spamhmap.get(temp)==null) {
    			SpamPos += Math.log1p((double) 1 / ((double) spamWordsCount));
    		} else {
    			SpamPos += Math.log1p((double) (spamhmap.get(temp) + 1) / ((double) spamWordsCount));
    		}
    	}
		
		if(SpamPos>HamPos) {
			foundSpam++;
		} else {
			foundHam++;
		}
		
	}
	
}