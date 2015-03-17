package fpgrowth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class FPGrowth {
	
	private String []DB = {"facdgimp", "abcflmo", "bfhjo", "bcksp", "afcelpmn"};
	private Map<Character, Integer> flist = new HashMap<Character, Integer>();
	private ValueComparator bvc = new ValueComparator(flist);
	private TreeMap<Character, Integer> sortedFlist = new TreeMap<Character, Integer>(bvc);
	private int minsup = 3;
	
	public void FPGrowth() {
		for(String T : DB) {
			for(int i = 0; i < T.length(); i++) {
				char a = T.charAt(i);
//				System.out.println(a);
				if(flist.containsKey(a)) {
					flist.put(a, flist.get(a)+1);
				} else {
					flist.put(a, 1);
				}
			}
		}
//		System.out.println(flist);
		sortedFlist.putAll(flist);
//		System.out.println(sortedFlist);
		
	}
	
	public void growth() {
		
	}
	
	public static void main(String[] args) {
		FPGrowth test = new FPGrowth();
		test.FPGrowth();
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