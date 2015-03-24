package fpgrowth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import model.FPHeaderTableColumn;
import model.FPNode;
import model.FPTree;


public class FPGrowth {
	
	private String []DB = {"facdgimp", "abcflmo", "bfhjo", "bcksp", "afcelpmn"};
	private Map<Character, Integer> flist = new HashMap<Character, Integer>();
	private ValueComparator bvc = new ValueComparator(flist);
	private TreeMap<Character, Integer> sortedFlist = new TreeMap<Character, Integer>(bvc);
	private int minsup = 3;
	private FPTree tree;
	
	public void fpgrowth() {
		// scan data and find support for each item
		for(String T : DB) {
			for(int i = 0; i < T.length(); i++) {
				char a = T.charAt(i);
				if(flist.containsKey(a)) {
					flist.put(a, flist.get(a)+1);
				} else {
					flist.put(a, 1);
				}
			}
		}
		
		// discard infrequent items;
		Iterator<Character> it = flist.keySet().iterator();
		
		List<Character> infreq = new ArrayList<Character>();
		while(it.hasNext()) {
			char c = it.next();
			
			if(flist.get(c) < minsup) {
				infreq.add(c);
			}
		}
		
		for(int i = 0; i < infreq.size(); i++) {
			flist.remove(infreq.get(i));
		}

		// sort freq. items in decreasing order
		sortedFlist.putAll(flist);
		
		System.out.println(sortedFlist);
		// sort transactions accord. sortedFlist
		
		for(int i = 0; i < DB.length; i++) {
			it = sortedFlist.descendingKeySet().iterator();
			String t = DB[i];
			while(it.hasNext()) {
				char c = it.next();
				if(t.indexOf(c) != -1) {
					int idx = t.indexOf(c);
					if(idx != t.length() - 1) {
						DB[i] = c + t.substring(0, idx) + t.substring(idx+1);
					} else {
						t = c + t.substring(0, idx);
					}
				}
			}
		}
		
		for(String t:DB) { System.out.println(t); }
		
		// construct tree
		tree = new FPTree(DB, sortedFlist, minsup);		
		
		// mining
		tree.growth();
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