package fpgrowth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import model.FPHeaderTableColumn;
import model.FPNode;
import model.FPTree;


public class FPGrowth {
	
	private String []DB = {"facdgimp", "abcflmo", "bfhjo", "bcksp", "afcelpmn"};
	private int minsup = 3;
	private FPTree tree;
	
	public void dbvectorConstructor(String []DB, Vector<String> DBVector) {
		for(String t : DB) {
			String result = null;
			for(int i = 0; i < t.length(); i++) {
				result = (result != null) ? result + " " + t.charAt(i) : ""+t.charAt(i);
			}
			DBVector.addElement(result);
		}
		System.out.println(DBVector);
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
		
		for(int i = 0; i < itemsToRemove.size(); i++) {
			sortedItemsByFrequencies.remove(itemsToRemove.get(i));
		}
		
		System.out.println("1. Sorted Items By Frequenices\n\t"+sortedItemsByFrequencies);
		
		// sort DB according to the frequency
		for(String transaction : DBVector) {
			it = sortedItemsByFrequencies.descendingKeySet().iterator();
			while(it.hasNext()) {
				char item = it.next();
				if(transaction.indexOf(item) != -1) {
					int idx = transaction.indexOf(item);
					if(idx != transaction.length() - 1) {
						transaction = transaction.substring(idx, idx + 2) + transaction.substring(0, idx) + transaction.substring(idx + 2);
					} else {
						transaction = transaction.substring(idx) + transaction.substring(0, idx); 
					}
				}
			}
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
		dbvectorConstructor(DB, DBVector);
		preProcessing(DBVector, itemsMapToFrequencies, sortedItemsByFrequencies, itemsToRemove);
		
		// construct FPtree
		tree = new FPTree(DBVector, itemsMapToFrequencies, minsup);		
		
		// mining
		tree.growth();
		
		System.out.println("3. frequent patterns");
		System.out.println("\t" + tree.getFreqPatterns());
	}
	
	public void printTree() {
		System.out.println(tree);
	}
	public static void main(String[] args) {
		FPGrowth test = new FPGrowth();
		test.fpgrowth();
		test.printTree();
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