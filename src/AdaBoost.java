import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

/* NTYMENOS: C:/Users/GeorgeF/Downloads/Eclipse Workspace/
 * PATRIKIS: C:/Users/geopa/Downloads/
 * FOTOPOULOS: C:/Users/GeorgeF/Downloads/Eclipse Workspace/
*/

public class AdaBoost {
	
	static int classifiedSpam = 0, classifiedHam=0, N, M, spamFolderSize = 0, hamFolderSize = 0;
	static double error;
	static String temp;
	static int[] predict;
	static double[] weight, z;
	static DecStump[] h;//Decision tree
	static StringTokenizer st1;
	static HashMap<String, WordsInfo> spamhmap = new HashMap<String, WordsInfo>();
	static HashMap<String, WordsInfo> hamhmap = new HashMap<String, WordsInfo>();
	static HashMap<String, WordsForAdaboost> wordshmap=new HashMap<String, WordsForAdaboost>();
	static boolean testOrTrain; //testOrTrain=false train -- testOrTrain=true test
	
	public static void main(String[] args) throws IOException {
		
		double boundDiv = 1;//used to take portions of the total mails(0.5 ,half of the total mails in the folder.)
		testOrTrain = true;
		
		File trainSpam = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron1/spam/");//training folder
		spamFolderSize = MailReader.findFolderSize(trainSpam);
		File trainHam = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron1/ham/");//training folder
		hamFolderSize = MailReader.findFolderSize(trainHam);
		
		spamFolderSize=(int) (spamFolderSize * boundDiv);
		hamFolderSize = (int) (hamFolderSize * boundDiv);
		
		System.out.println("Spam Mails: " + spamFolderSize + ", Ham Mails: " + hamFolderSize);
		createWeakLearners();
		System.out.println("Total Size: " + wordshmap.size());
		AdaBoostAlg();
		File testFolder = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron2/spam/");//testing folder
		testAB(testFolder, spamFolderSize);
		System.out.println("SPAM mails: " );
		System.out.println("Ham: " + classifiedHam+ ", Spam: " + classifiedSpam);
		testFolder = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron2/ham/");//testing folder
		testAB(testFolder, hamFolderSize);
		System.out.println("HAM mails: " );
		System.out.println("Ham: " + classifiedHam+ ", Spam: " + classifiedSpam);
		
	}
	
	public static void testAB(final File folder, int folderSize) throws IOException {
		
		classifiedSpam = 0;
		classifiedHam = 0;
		int count = 0;
		double finalPrediction;
		boolean[] foundword = new boolean[M];
		
		for (final File fileEntry : folder.listFiles()) {
			count++;
			finalPrediction = 0;
			for(int i=0; i<M; i++) foundword[i] = false;
			st1 = new StringTokenizer(MailReader.readFile(folder + "/" + fileEntry.getName()));
		    for (; st1.hasMoreTokens(); ) {
		    	temp = st1.nextToken();
		    	if(temp.length() > 2) {
		    		for(int i=0; i<M; i++) {
		    			if(h[i].root.word.equals(temp) && z[i] > 0){
		    				foundword[i] = true;
		    				finalPrediction += h[i].root.right.dec * z[i];
		    			}
		    		}
		        }
		    }
		    
		    for(int i=0; i<M; i++) {
		    	if(!foundword[i] && z[i] > 0) {
		    		finalPrediction += h[i].root.left.dec * z[i];
		    	}
		    }
		    
		    if(finalPrediction > 0) {
		    	classifiedSpam++;
		    } else {
		    	classifiedHam++;
		    }
		    
		    if(!testOrTrain) {
		    	if(count==folderSize) break;
		    }
		    
		}
	    
	}
	
	//a method with the functionality of the AdaBoost algorithm
	public static void AdaBoostAlg() throws IOException {
		
		int i = 0;
		double sumWeightAfter;
		boolean found;
		M = wordshmap.size();
		h = new DecStump[M];
		z = new double[M];
		weight = new double[N];
		predict = new int[N];
		for (int j=0; j<N; j++) weight[j] = 1.0/N;
		int j;
		
		for(String name : wordshmap.keySet()) { //(int i=0;i<M;i++)
			h[i] = new DecStump(name, wordshmap.get(name).decision);
			error = 0;
			File folder = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron1/spam");
			j = 0;
			
			for (final File fileEntry : folder.listFiles()) {
				predict[j] = h[i].root.left.dec;
				found = false;
				st1 = new StringTokenizer(MailReader.readFile(folder + "/" + fileEntry.getName()));
			    for( ; st1.hasMoreTokens(); ) {
			    	temp = st1.nextToken();
			    	if(temp.equals(h[i].root.word)) {
			    		found = true;
			    		predict[j] = h[i].root.right.dec;
			    		if(h[i].root.right.dec==-1) {
			    			error = error + weight[j];
			    			break;
			    		}
			        }
			    }
			    if(!found) {
			    	if(h[i].root.left.dec==-1) {
		    			error = error + weight[j];
		    		}
			    }
			    j++;
			    if(j==spamFolderSize) break;//when testing ,we need to break the loop so that it runs for the specified number of mails.
			}
			
			folder = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron1/ham");
			for (final File fileEntry : folder.listFiles()) {
				predict[j] = h[i].root.left.dec;
				found = false;
				st1 = new StringTokenizer(MailReader.readFile(folder + "/" + fileEntry.getName()));
			    for ( ; st1.hasMoreTokens(); ) {
			    	temp = st1.nextToken();
			    	if(temp.equals(h[i].root.word)) {
			    		found = true;
			    		predict[j] = h[i].root.right.dec;
			    		if(h[i].root.right.dec==1) {
			    			error = error + weight[j];
			    			break;
			    		}
			        }
			    }
			    if(!found) {
			    	if(h[i].root.left.dec==1) {
		    			error = error + weight[j];
		    		}
			    }
			    j++;
			    if(j==(hamFolderSize+spamFolderSize)) break;//when testing ,we need to break the loop so that it runs for the specified number of mails.
			}
			
			sumWeightAfter = 0;
			for(int x=0; x<spamFolderSize; x++) {
				if(predict[x]==1 && error < 0.5) {
					weight[x] = weight[x] * error / (1-error);
				}
				sumWeightAfter += weight[x];
			}
			for(int x=0; x<hamFolderSize; x++) {
				if(predict[x+spamFolderSize]==-1 && error < 0.5) {
					weight[x+spamFolderSize] = weight[x+spamFolderSize] * error / (1-error);
					
				}
				sumWeightAfter += weight[x+spamFolderSize];
			}
			for(int y=0; y<N; y++) {
				weight[y] = weight[y] / sumWeightAfter;
			}
			z[i] = Math.log((1-error) / error);
			i++;
		}
		
		wordshmap.clear();
		
	}

	//The weak learner for AdaBoost.
	public static void createWeakLearners() throws IOException {
		
		File folder = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron1/spam");
		MailReader.listFilesForFolderAB(folder, spamhmap, spamFolderSize);
		folder = new File("C:/Users/GeorgeF/Downloads/Eclipse Workspace/enron1/ham");
		MailReader.listFilesForFolderAB(folder, hamhmap, hamFolderSize);
		N = hamFolderSize + spamFolderSize;
		for(String name: spamhmap.keySet()) {
			if(hamhmap.get(name)==null) {
				if((spamhmap.get(name).InMailsFound)>spamFolderSize/30) {
					
					wordshmap.put(name, new WordsForAdaboost(spamhmap.get(name).InMailsFound,1));
				}
			}
			else {
				if(spamhmap.get(name).InMailsFound / hamhmap.get(name).InMailsFound > ((double)spamFolderSize/hamFolderSize) * 22.5 && spamhmap.get(name).InMailsFound>spamFolderSize / 75) {
				
					wordshmap.put(name, new WordsForAdaboost(spamhmap.get(name).InMailsFound,1));
				}
			}
		}
		for(String name: hamhmap.keySet()) {
			if(wordshmap.get(name)==null) {
				if(spamhmap.get(name)==null) {
					if((hamhmap.get(name).InMailsFound) > (hamFolderSize / 5)) {
						wordshmap.put(name, new WordsForAdaboost(hamhmap.get(name).InMailsFound,-1));
					}
				} else {
					if(hamhmap.get(name).InMailsFound / spamhmap.get(name).InMailsFound > 13 * (hamFolderSize / spamFolderSize) && hamhmap.get(name).InMailsFound > hamFolderSize / 122.4) {
						wordshmap.put(name, new WordsForAdaboost(hamhmap.get(name).InMailsFound, -1));
					}
				}
			}
		}
		
	}

}