import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

/* NTYMENOS: C:/Users/GeorgeF/Downloads/Eclipse Workspace/
 * PATRIKIS: C:/Users/geopa/Downloads/
 * FOTOPOULOS: C:/Users/GeorgeF/Downloads/Eclipse Workspace/
*/

public class ID3 {
	
	static int foundSpam = 0, foundHam = 0;
	static double boundDiv;
	static HashMap<String, NodeInfo> finalhmap = new HashMap<String, NodeInfo>();// a HashMap with the name of the word as a key,and a node as a parameter with the decision if the word is spam or ham(tree).  
	static HashMap<String, WordsInfo > spamhmap = new HashMap<String, WordsInfo>();
	static HashMap<String, WordsInfo> hamhmap = new HashMap<String, WordsInfo>();
	static boolean trainOrTest; // trainOrTest=false (train), trainOrTest=true(FN,FP,TN,TP for Enron3) (test)
	
	public static void main(String[] args) throws IOException {
		
		boundDiv = 1;//used to take portions of the total mails(0.5 ,half of the total mails in the folder.)
		trainOrTest = true;
		DecisionTree id3 = ID3train();
		final File folderSpam = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron3/spam/");
		int bound = (int) (MailReader.findFolderSize(folderSpam) * boundDiv);//total number of mails (used for testing with boundDiv)
		listFilesForFolderID3(id3, folderSpam, bound);
		System.out.println("--- SPAM MESSAGES ---\nSPAM: " + foundSpam + ", HAM: " + foundHam + "\n");
		foundSpam = 0;
		foundHam = 0;
		final File folderHam = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron3/ham/");
		bound= (int) (MailReader.findFolderSize(folderHam) * boundDiv);
		listFilesForFolderID3(id3, folderHam, bound);
		System.out.println("--- HAM MESSAGES ---\nSPAM: " + foundSpam + ", HAM: " + foundHam + "\n");
		foundSpam = 0;
		foundHam = 0;
		
	}
	
	/* After calculating all the information of the words,
	 * such as the entropy,the frequency and the decision,
	 * it transfers all that,in a tree and it returns it.
	 */
	public static DecisionTree ID3train() throws IOException {
		
		boolean decision;
		double frequency;//the frequency of a word's appearance in the total number of mails
		int SpamWordsCount = 0, HamWordsCount = 0, spamMails, hamMails;
		
		File trainFolder = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron6/spam");//folder for training spams.
		spamMails = (int) (MailReader.findFolderSize(trainFolder) * boundDiv);//the number of spam mails
		SpamWordsCount = MailReader.listFilesForFolderID3(trainFolder, spamhmap, spamMails);//the number of words in spam mails
		trainFolder = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron2/ham");//folder for training hams.
		hamMails = (int) (MailReader.findFolderSize(trainFolder) * boundDiv);//the number of spam mails
		HamWordsCount = MailReader.listFilesForFolderID3(trainFolder, hamhmap, hamMails);//the number of words in ham mails
		System.out.println("Spam Words: " + SpamWordsCount + ", Ham Words: " + HamWordsCount + "\n");
		
		for (String name: hamhmap.keySet()) {
			decision = false;
			if(hamhmap.get(name).WordsCount > 20) {//a threshold,so that we take into account only the "severe" words with more than 20 appearances.
            	if(spamhmap.get(name) != null){
            		if(hamhmap.get(name).WordsCount<=spamhmap.get(name).WordsCount) decision = true;
            	}
            	if(decision == false) {
            		frequency = (double)hamhmap.get(name).WordsCount / hamhmap.get(name).InMailsFound;
            	} else {
            		frequency = (double)spamhmap.get(name).WordsCount / spamhmap.get(name).InMailsFound;
            	}
            	if(spamhmap.get(name)!=null) {
            		finalhmap.put(name, new NodeInfo(entropy(hamhmap.get(name).WordsCount,spamhmap.get(name).WordsCount), decision, frequency));
            	} else {
					finalhmap.put(name, new NodeInfo(0, false, frequency));
				}
            }
		}
		
		for (String name: spamhmap.keySet()) {
			decision = true;
            if(spamhmap.get(name).WordsCount > 20) {//threshold
            	if(hamhmap.get(name)!=null) {
            		if(hamhmap.get(name).WordsCount > spamhmap.get(name).WordsCount) decision = false;
            	}
            	if(decision==false) {
            		frequency = (double) hamhmap.get(name).WordsCount / hamhmap.get(name).InMailsFound;
            	} else {
            		frequency = (double) spamhmap.get(name).WordsCount / spamhmap.get(name).InMailsFound;
            	}
            	if(finalhmap.get(name)==null) {
            		if(hamhmap.get(name)!=null) {
            			finalhmap.put(name, new NodeInfo(entropy(hamhmap.get(name).WordsCount, spamhmap.get(name).WordsCount), decision, frequency));
               		} else {
               			finalhmap.put(name, new NodeInfo(0, true, frequency));
               		}
            	}
            }
		}
		
		String tempName = "";
		DecisionTree ID3 = new DecisionTree();
		while(!finalhmap.isEmpty()) { 
			double tempEntropy = 1.0;
			for (String name: finalhmap.keySet()) {
				if(tempEntropy >= finalhmap.get(name).entropy) {
					tempEntropy = finalhmap.get(name).entropy;
	  	       		tempName = name;
				}
			}
			ID3.add(new ID3node(tempName, Math.round(finalhmap.get(tempName).frequency), finalhmap.get(tempName).decision));
			finalhmap.remove(tempName);
		}
		
		return ID3;
		
	}
	
	/* After training ,a tree is made,an then we run the ID3 algorithm,
	 * for every mail.
	 */
	public static void ID3alg(DecisionTree ID3, String Folder) throws IOException {
		
		String temp, FullMail = MailReader.readFile(Folder);
		StringTokenizer st1 = new StringTokenizer(FullMail);
		int mailFrequency;
		HashMap<String, Integer> mailhash = new HashMap<String, Integer>();
		ID3node current = ID3.root;
		
		for ( ;st1.hasMoreTokens(); ) {
			temp = st1.nextToken();
			if(mailhash.get(temp)==null) {
				mailhash.put(temp, 1);
			} else {
				mailFrequency = 1 + mailhash.get(temp).intValue();
				mailhash.remove(temp);
				mailhash.put(temp, mailFrequency);
			}
		}
		
		while(current!=null) {
			if(mailhash.get(current.word)==null) {
				current = current.left;
			} else {
				if(current.frequency <= mailhash.get(current.word).intValue()) {
					if(current.right.decision) {
						foundSpam++;
						break;
					} else {
						foundHam++;
						break;
					}
				} else {
					current = current.left;
				}
			}
		}
		
		if(current==null) foundSpam++;
		
	}
	
	// A method that runs the ID3alg method for all the folder's content(Spam/Ham)
	public static void listFilesForFolderID3(DecisionTree ID3, final File folder, int bound) throws IOException {
		int i = 0;
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
	        	listFilesForFolderID3(ID3, fileEntry, bound);
	        } else {
	        	ID3alg(ID3, folder + "/" + fileEntry.getName());
	            i++;
	            if(!trainOrTest) {
	            	if(i==bound) break;//the 'i' int ,is helpful for train purposes,so that it doesn't run all the mails every time
	            }
	        }
		}
	}
	
	//Calculates the entropy of the word.
	public static double entropy(int hamCount, int spamCount) {
		return -1 * ((double) hamCount / (hamCount + spamCount) * log2((double)hamCount / (hamCount + spamCount)) + (double)spamCount / (hamCount + spamCount) * log2((double)spamCount / (hamCount + spamCount)));
	}
	
	public static double log2(double num) {
		return (Math.log(num) / Math.log(2));
	}
	
}