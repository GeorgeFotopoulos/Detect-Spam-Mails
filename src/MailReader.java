import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class MailReader {
	
	static StringTokenizer st1;
	static String temp;
	static WordsInfo frequency2;
	
	/* It's running the folder's content and then calculates the total mails found.
	 * It's checking all the words with more than 2 letters and puts them in a HashMap.
	 * We are using an Iterator so that the HashMap is not big.
	 * It returns an int ,that indicates how many words are found.
	 * (Used for Naive Bayes)
	 */
	public static int listFilesForFolder(final File folder, HashMap<String,Integer> hMap, int mails) throws IOException {
		int WordsCount = 0, i = 0, frequency;
		
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry, hMap, mails);
			} else {
				i++;
				st1 = new StringTokenizer(readFile(folder + "/" + fileEntry.getName()));
				for (; st1.hasMoreTokens(); ) {
					temp=st1.nextToken();
					if(temp.length()>2) {
						if(hMap.get(temp)==null) {
							hMap.put(temp, 1);
						} else {
							frequency= hMap.get(temp);
							hMap.remove(temp);
							frequency++;
							hMap.put(temp, frequency);
						}
					}
				}
				if(mails==i) break; //the 'mails' int ,is helpful for train purposes,so that it doesn't run all the mails every time
			}
		}
		
		for(Iterator<Map.Entry<String, Integer>> it = hMap.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, Integer> entry = it.next();
			if(entry.getValue()<=4) {
				it.remove();
			}
		}
		
		for (String name: hMap.keySet()){
			WordsCount+=hMap.get(name);
		}
		
		return WordsCount;
	}
	/* Almost same functionality with the listFilesForFolder.
	 * The difference is that it returns the number of the Mails found ,
	 * calculates how many times a word is found in a mail and
	 * the HashMap, that is made , has that information (our Object: WordsInfo) 
	 * (used for AdaBoost)
	 */
	public static int listFilesForFolderAB(final File folder, HashMap<String,WordsInfo> hMap, int FolderSize) throws IOException {
		int mails=0;
		
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolderAB(fileEntry, hMap, FolderSize);
			} else {
				mails++;
				st1 = new StringTokenizer(readFile(folder + "/" + fileEntry.getName()));
				for (; st1.hasMoreTokens(); ) {
					temp = st1.nextToken();
					if(temp.length()>2) {
						if(hMap.get(temp)==null) {
							frequency2=new WordsInfo();
							hMap.put(temp, frequency2);
						} else {   
							if(!hMap.get(temp).updated) {
								frequency2 = hMap.get(temp);
								hMap.remove(temp);
								frequency2.InMailsFound++;
								frequency2.updated = true;
								hMap.put(temp, frequency2);
							}
							frequency2 = hMap.get(temp);
							hMap.remove(temp);
							frequency2.WordsCount++;
							hMap.put(temp, frequency2);
						}
					}
				}
				for(Iterator<Map.Entry<String, WordsInfo>> it = hMap.entrySet().iterator(); it.hasNext(); ) {
					Map.Entry<String, WordsInfo> entry = it.next();
					entry.setValue(new WordsInfo(entry.getValue().WordsCount, entry.getValue().InMailsFound, false));
				}
				if(mails==FolderSize) break; //the 'mails' int ,is helpful for train purposes,so that it doesn't run all the mails every time
			}
		}
		
		return mails;
	}
	
	

	@SuppressWarnings("unused")
	public static int findFolderSize(final File folder)throws IOException {
		int Files = 0;
			for (final File fileEntry : folder.listFiles()) Files++;
		return Files;
	}
	/* Almost same functionality with the listFilesForFolder.
	 * The difference is that it returns the number of words that the HashMap contains ,
	 * calculates how many times a word is found in a mail and
	 * the HashMap, that is made , has that information (our Object: WordsInfo) 
	 * (used for ID3)
	 */
	public static int listFilesForFolderID3(final File folder,HashMap<String,WordsInfo> hMap, int mails) throws IOException {
		int WordsCount = 0, countedMails = 0;
		
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolderID3(fileEntry,hMap,mails);
			} else {
				countedMails++;
				st1 = new StringTokenizer(readFile(folder + "/" + fileEntry.getName()));
				for (; st1.hasMoreTokens(); ) {
					temp = st1.nextToken();
					if(temp.length()>2) {
						if(hMap.get(temp)==null) {
							frequency2 = new WordsInfo();
							hMap.put(temp, frequency2);
						} else {   
							if(!hMap.get(temp).updated) {
								frequency2 = hMap.get(temp);
								hMap.remove(temp);
								frequency2.InMailsFound++;
								frequency2.updated=true;
								hMap.put(temp, frequency2);
							}
							frequency2 = hMap.get(temp);
							hMap.remove(temp);
							frequency2.WordsCount++;
							hMap.put(temp, frequency2);
						}
					}
				}
				for(Iterator<Map.Entry<String, WordsInfo>> it = hMap.entrySet().iterator(); it.hasNext(); ) {
					Map.Entry<String, WordsInfo> entry = it.next();
					entry.setValue(new WordsInfo(entry.getValue().WordsCount, entry.getValue().InMailsFound, false));
				}
				if(countedMails==mails) break;//the 'countedMails' int ,is helpful for train purposes,so that it doesn't run all the mails every time
			}
		}
		
		for (String name: hMap.keySet()) {
			WordsCount += hMap.get(name).WordsCount;
		}
		
		return WordsCount;
	}
	/* It takes a mail,and it's erasing all the lines ,making it
	 * just one straight line,with one space in between every word.
	 */
	public static String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader (file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		try {
			while((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			return stringBuilder.toString();
		} finally {
			reader.close();
		}
	}
	
}