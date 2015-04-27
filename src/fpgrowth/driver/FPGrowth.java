package fpgrowth.driver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import fpgrowth.model.FPTree;

public class FPGrowth {
	private int mincount;
	private double minsup;
	private FPTree tree;
	private String fileName;
	
	public Vector<String> dbCreator() {
		Vector<String> result = new Vector<String>();
		
		File file = new File(fileName);
		System.out.print("reading "+fileName+" ... ");
		long startTime = System.currentTimeMillis();
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			while(true) {
				String line = br.readLine();
				if(line == null) {
					break;
				}
				result.add(line.toLowerCase());
			}
			br.close();
			fr.close();
			long endTime = System.currentTimeMillis();
			System.out.print("done ["+ (endTime - startTime) / 1000.000 +"s]\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		mincount = (int) (minsup * result.size());
		return result;
	}
	
	
	private void preProcessing(Vector<String> DBVector, Map<String, Integer> itemsMapToFrequencies, TreeMap<String, Integer> sortedItemsByFrequencies, Vector<String> itemsToRemove) {
		System.out.print("filtering, sorting and recording items ...");
		long startTime = System.currentTimeMillis();
		// count the number of items
		for(String transaction : DBVector) {
			StringTokenizer tokenizer = new StringTokenizer(transaction);
			
			while(tokenizer.hasMoreTokens()) {
				String item = tokenizer.nextToken();
				if(itemsMapToFrequencies.containsKey(item)) {
					itemsMapToFrequencies.put(item, itemsMapToFrequencies.get(item) + 1);
				} else {
					itemsMapToFrequencies.put(item, 1);
				}
			}
		}
		
		// discard infrequent items;
		Iterator<String> it = itemsMapToFrequencies.keySet().iterator();
		while(it.hasNext()) {
			String item = it.next();
			if(itemsMapToFrequencies.get(item) < mincount) {
				itemsToRemove.add(item);
			}
		}

		// sort freq. items in decreasing order
		sortedItemsByFrequencies.putAll(itemsMapToFrequencies);

		for(String itemTR : itemsToRemove) {
			sortedItemsByFrequencies.remove(itemTR);
		}
		
		// sort DB according to the frequency
		for(int i = 0; i < DBVector.size(); i++) {
			String transaction = DBVector.get(i);
			it = sortedItemsByFrequencies.descendingKeySet().iterator();
			while(it.hasNext()) {
				String item = it.next();
				String []tarr = transaction.split(" ");
				int j = 0;
				for(; j < tarr.length; j++) {
					if(tarr[j].equals(item)) {
						String temp = tarr[0];
						tarr[0] = tarr[j];
						tarr[j] = temp;
						break;
					}
				}
				
				transaction = "";
				for(int k = 0; k < tarr.length; k++) {
					transaction += tarr[k] + " ";
				}
				
				if(transaction.length() > 1) {
					transaction = transaction.substring(0, transaction.length() - 1);
				}
			}
			DBVector.set(i, transaction);
		}
		long endTime = System.currentTimeMillis();
		System.out.print("done [" + (endTime - startTime) / 1000.000 + "s]\n");
	}
	
	public void fpgrowth() {
		Vector<String> DBVector = dbCreator();
		Map<String, Integer> itemsMapToFrequencies = new HashMap<String, Integer>();
		ValueComparator bvc = new ValueComparator(itemsMapToFrequencies);
		TreeMap<String, Integer> sortedItemsByFrequencies = new TreeMap<String, Integer>(bvc);
		Vector<String> itemsToRemove = new Vector<String>(); 

		// refactor strings in db
		preProcessing(DBVector, itemsMapToFrequencies, sortedItemsByFrequencies, itemsToRemove);
	
		// construct FPtree
		tree = new FPTree(DBVector, itemsMapToFrequencies, mincount);	// DONE 
		
		// mining
		tree.growth();
		
		Comparator<String> cmp = new Comparator<String>() {
			
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		};
		
		TreeSet<String> result = new TreeSet<String>(cmp);
		result.addAll(tree.getFreqPatterns().keySet());
		
		System.out.print("writing output ...");
		File file = new File("result");
		try {
			long startTime = System.currentTimeMillis();
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);

			Iterator<String> it = result.iterator();
			
			while(it.hasNext()) {
				String line = it.next();
				bw.write(line+" ("+tree.getFreqPatterns().get(line)+") \n");
			}
			
			bw.close();
			fw.close();
			long endTime = System.currentTimeMillis();
			System.out.print(" done [" + (endTime - startTime) / 1000.000 + "s]\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
//		System.out.println("frequent patterns from "+fileName+" with minsup "+mincount);
//		System.out.println("\t"+result);
	}
	
	public void printTree() {
		System.out.println(tree);
	}
	public int getMincount() {
		return mincount;
	}

	public void setMincount(int minsup) {
		this.mincount = minsup;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public double getMinsup() {
		return minsup;
	}

	public void setMinsup(double minsup) {
		this.minsup = minsup;
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		FPGrowth test = new FPGrowth();
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter filename: ");
		test.setFileName(scan.nextLine());
		System.out.println("Enter minsup: ");
		test.setMinsup(scan.nextDouble());
		test.fpgrowth();
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);
		System.out.println("duration: "+duration / 1000.000 +"s");
	}

}

class ValueComparator implements Comparator<String> {
	
	Map<String, Integer> base;
	
	public ValueComparator(Map<String, Integer> base) {
		this.base = base;
	}
	
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		}
	}
}
