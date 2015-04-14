package fpgrowth;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import model.FPTree;


public class FPGrowth {
	
	private String []DB = {"ab", "bcd", "acde", "ade", "abc", "abcd", "a", "abc", "abd", "bce"};
	private int minsup = 50000;
	private FPTree tree;
	
	public Vector<String> dbCreator() {
		Vector<String> result = new Vector<String>();
		
		File file = new File("dictionary.txt");
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			while(true) {
				String line = br.readLine();
				if(line == null) {
					break;
				}
				System.out.println(line);
				StringTokenizer tokenizer = new StringTokenizer(line);
				while(tokenizer.hasMoreTokens()) {
					result.add(tokenizer.nextToken());
				}
			}
			
			br.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void dbvectorConstructor(String []DB, Vector<String> DBVector) {
		for(String t : DB) {
			String result = null;
			for(int i = 0; i < t.length(); i++) {
				result = (result != null) ? result + " " + t.charAt(i) : ""+t.charAt(i);
			}
			DBVector.addElement(result);
		}
//		System.out.println(DBVector);
	}
	
	public void dbvectorConstructor(Vector<String> DB, Vector<String> dBVector) {
		for(String t : DB) {
			String result = null;
			for(int i = 0; i < t.length(); i++) {
				result = (result != null) ? result + " " + t.charAt(i) : ""+t.charAt(i);
			}
			dBVector.addElement(result);
		}
	}
	
	private void preProcessing(Vector<String> DBVector, Map<Character, Integer> itemsMapToFrequencies, TreeMap<Character, Integer> sortedItemsByFrequencies, Vector<Character> itemsToRemove) {
		// count the number of items
		for(String transaction : DBVector) {
			StringTokenizer tokenizer = new StringTokenizer(transaction);
			
			while(tokenizer.hasMoreTokens()) {
				char item = tokenizer.nextToken().charAt(0);
				if(itemsMapToFrequencies.containsKey(item)) {
					itemsMapToFrequencies.put(item, itemsMapToFrequencies.get(item) + 1);
				} else {
					itemsMapToFrequencies.put(item, 1);
				}
			}
		}
		
		// discard infrequent items;
		Iterator<Character> it = itemsMapToFrequencies.keySet().iterator();
		
		while(it.hasNext()) {
			char item = it.next();
			
			if(itemsMapToFrequencies.get(item) < minsup) {
				itemsToRemove.add(item);
			}
		}
		// sort freq. items in decreasing order
		sortedItemsByFrequencies.putAll(itemsMapToFrequencies);

		for(char itemTR : itemsToRemove) {
			sortedItemsByFrequencies.remove(itemTR);
		}
		
		System.out.println("1. Sorted Items By Frequenices\n\t"+sortedItemsByFrequencies);
		
		// sort DB according to the frequency
		for(int i = 0; i < DBVector.size(); i++) {
			String transaction = DBVector.get(i);
			it = sortedItemsByFrequencies.descendingKeySet().iterator();
			while(it.hasNext()) {
				char item = it.next();
				if(transaction.indexOf(item) != -1) {
					int idx = transaction.indexOf(item);
					if(idx != transaction.length() - 1) {
						transaction = transaction.substring(idx, idx + 2) + transaction.substring(0, idx) + transaction.substring(idx + 2);
					} else {
						transaction = transaction.substring(idx) + " " + transaction.substring(0, idx); 
					}
				}
			}
			DBVector.set(i, transaction);
		}
		
		
		System.out.println("2. sorted DB\n\t" + DBVector);
		
	}
	
	public void fpgrowth() {
		Vector<String> DBVector = new Vector<String>();
		Map<Character, Integer> itemsMapToFrequencies = new HashMap<Character, Integer>();
		ValueComparator bvc = new ValueComparator(itemsMapToFrequencies);
		TreeMap<Character, Integer> sortedItemsByFrequencies = new TreeMap<Character, Integer>(bvc);
		Vector<Character> itemsToRemove = new Vector<Character>(); 

		// refactor strings in db
//		System.out.println("db refactoring...");
		dbvectorConstructor(dbCreator(), DBVector);
		preProcessing(DBVector, itemsMapToFrequencies, sortedItemsByFrequencies, itemsToRemove);
	
		// construct FPtree
//		System.out.println("constructing tree...");
		tree = new FPTree(DBVector, itemsMapToFrequencies, minsup);	// DONE 
		
		// mining
//		System.out.println("mining...");
		tree.growth();
		
		Comparator<String> cmp = new Comparator<String>() {
			
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		};
		
		TreeSet<String> result = new TreeSet<String>(cmp);
		result.addAll(tree.getFreqPatterns().keySet());
		
		
		System.out.println("3. frequent patterns");
		
		System.out.println("\t"+result);
	}
	
	public void printTree() {
		System.out.println(tree);
	}
	public static void main(String[] args) {
		FPGrowth test = new FPGrowth();
		test.fpgrowth();
//		test.printTree();
	}

}

class ValueComparator implements Comparator<Character> {
	
	Map<Character, Integer> base;
	
	public ValueComparator(Map<Character, Integer> base) {
		this.base = base;
	}
	
	public int compare(Character a, Character b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		}
	}
}