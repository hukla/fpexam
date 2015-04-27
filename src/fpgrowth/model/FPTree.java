package fpgrowth.model;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

public class FPTree {
	private Vector<FPNode> headerTable = new Vector<FPNode>();
	private FPNode root;
	private int minsup;
	private Map<String, Integer> freqPatterns = new HashMap<String, Integer>();
	
	public FPTree() {
		root = null;
	}

	public FPTree(Vector<String> DBVector, Map<String, Integer> flist, int minsup) {
		System.out.print("sorting transactions ...");
		long startTime = System.currentTimeMillis();
		root = new FPNode();
		this.minsup = minsup;
		
		for(String itemForTable : flist.keySet()) {
			headerTable.add(new FPNode(itemForTable));
		}
		
		FPNode tempNode;
		FPNode newNode;
		String item;

		for (String transaction : DBVector) {
			tempNode = root;
			StringTokenizer tokenizer = new StringTokenizer(transaction);
			while(tokenizer.hasMoreTokens()) {
				item = tokenizer.nextToken();
				int childIdx = tempNode.getChildIdx(item);
				int cursup = (childIdx != -1) ? tempNode.getChildren().get(childIdx).getSupport() + 1 : 1; 
				if(cursup == 1) {
					newNode = new FPNode(cursup, item, tempNode);
					tempNode.putChild(newNode);
					
					int tableIdx = -1; 
					for(int i = 0; i < headerTable.size(); i++) {
						if(headerTable.get(i).getItem().equals(item)) {
							tableIdx = i;
							break;
						}
					}
					FPNode tempNodeLink = headerTable.get(tableIdx);
					
					while(tempNodeLink.getNodeLink() != null) tempNodeLink = tempNodeLink.getNodeLink();
					
					tempNodeLink.setNodeLink(newNode);
				} else {
					newNode = tempNode.getChild(item);
					newNode.setSupport(cursup);
				}
				
				tempNode = newNode;
			} // done
		}
		long endTime = System.currentTimeMillis();
		System.out.print(" done [" + (endTime - startTime) / 1000.000 + "s]\n");
	}

	public FPTree(Vector<FPNode> headerTable, FPNode root, int minsup) {
		this.headerTable = headerTable;
		this.root = root;
		this.minsup = minsup;
	}

	int tableHasItem(String item) {
		for (int i = 0; i < headerTable.size(); i++) {
			if (headerTable.get(i).getItem().equals(item)) {
				return i;
			}
		}
		return -1;
	}
	
	public void growth() {
		System.out.print("reducing transactions ...");
		long startTime = System.currentTimeMillis();
		growth(root, null, headerTable);
		long endTime = System.currentTimeMillis();
		System.out.print(" done [" + (endTime - startTime) / 1000.000 + "s]\n");
	}
	
	public void growth(FPNode root, String base, Vector<FPNode> headerTable) {
		for(FPNode iteminTree : headerTable) {
			String currentPattern = (base != null ? base : "") + (base != null ? " " : "") + iteminTree.getItem();
			int supportofCurrentPattern = 0; // support of current pattern
			Map<String, Integer> conditionalPatternBase = new HashMap<String, Integer>();
			
			while (iteminTree.getNodeLink() != null) {
				iteminTree = iteminTree.getNodeLink();
				supportofCurrentPattern += iteminTree.getSupport();
				String conditionalPattern = null;
				FPNode conditionalItem = iteminTree.getParent();
				
				while(conditionalItem.getItem() != "") {
					conditionalPattern = conditionalItem.getItem()+ (conditionalPattern != null ? " " : "") + (conditionalPattern != null ? conditionalPattern : "");
					conditionalItem = conditionalItem.getParent();
				}
				if(conditionalPattern != null) {
					conditionalPatternBase.put(conditionalPattern, iteminTree.getSupport());
				}
				
			}
			
			if(supportofCurrentPattern >= minsup) {
				freqPatterns.put(currentPattern, supportofCurrentPattern);
			}
			// count the support of each conditional item
			Map<String, Integer> conditionalItemsMaptoFreq = new HashMap<String, Integer>(); 
			for(String conditionalPattern : conditionalPatternBase.keySet()) {
				StringTokenizer tokenizer = new StringTokenizer(conditionalPattern);
				while(tokenizer.hasMoreTokens()) {
					String item = tokenizer.nextToken();
					if(conditionalItemsMaptoFreq.containsKey(item)) {
						int count = conditionalItemsMaptoFreq.get(item);
						count += conditionalPatternBase.get(conditionalPattern);
						conditionalItemsMaptoFreq.put(item, count);
					} else {
						conditionalItemsMaptoFreq.put(item, conditionalPatternBase.get(conditionalPattern));
					}
				}
				
			}
			
			// conditional fp tree
			// 1. headertable creation
			Vector<FPNode> condHeaderTable = new Vector<FPNode>();
			for(String itemsforTable : conditionalItemsMaptoFreq.keySet()) {
				int count = conditionalItemsMaptoFreq.get(itemsforTable);
				condHeaderTable.add(new FPNode(itemsforTable, count)); 
			}
			FPNode conditionalFPtree = condFPtreeConstructor(conditionalPatternBase, conditionalItemsMaptoFreq, condHeaderTable);
			
			if(!conditionalFPtree.getChildren().isEmpty()) {
				growth(conditionalFPtree, currentPattern, condHeaderTable);
			}
			
		}
		
	}
	

	private FPNode condFPtreeConstructor(Map<String, Integer> conditionalPatternBase, Map<String, Integer> conditionalItemsMaptoFreq, Vector<FPNode> condHeaderTable) {
		FPNode condFPtree = new FPNode(); // root
		
		FPNode tempNode;
		FPNode newNode;
		String item;
		
		for(String pattern : conditionalPatternBase.keySet()) {
			tempNode = condFPtree;
			
			Vector<String> patternVector = new Vector<String>();
			StringTokenizer tokenizer = new StringTokenizer(pattern);
			while(tokenizer.hasMoreTokens()) {
				item = tokenizer.nextToken();
				if(conditionalItemsMaptoFreq.get(item) >= minsup) {
					patternVector.addElement(item);
				}
			} // creating pattern vector
			
			for(String p : patternVector) { // TODO
				int childIdx = tempNode.getChildIdx(p);
				int cursup = (childIdx != -1) ? tempNode.getChildren().get(childIdx).getSupport() + conditionalPatternBase.get(pattern): conditionalPatternBase.get(pattern);
				// put newNode 
				if(childIdx == -1) {
					// if tempNode doesn't have p as a child
					newNode = new FPNode(cursup, p, tempNode);
					tempNode.putChild(newNode);

					// put newNode in the header table
					int tableIdx = -1;
					for(int i = 0; i < condHeaderTable.size(); i++) {
						if(condHeaderTable.get(i).getItem().equals(p)) {
							tableIdx = i;
							break;
						}
					}
					
					FPNode tempNodeLink = condHeaderTable.get(tableIdx);
					while(tempNodeLink.getNodeLink() != null) {
						tempNodeLink = tempNodeLink.getNodeLink();
					}
					
					tempNodeLink.setNodeLink(newNode);	
				} else {
					// if tempNode does have p as a child
					newNode = tempNode.getChild(p);
					newNode.setSupport(cursup);
				}
				
				tempNode = newNode;
			}
			
		}
		
		return condFPtree; 
	}
	
	public Vector<FPNode> getHeaderTable() {
		return headerTable;
	}

	public void setHeaderTable(Vector<FPNode> headerTable) {
		this.headerTable = headerTable;
	}

	public int getMinsup() {
		return minsup;
	}

	public void setMinsup(int minsup) {
		this.minsup = minsup;
	}

	public FPNode getRoot() {
		return root;
	}

	public void setRoot(FPNode root) {
		this.root = root;
	}

	
	
	public Map<String, Integer> getFreqPatterns() {
		return freqPatterns;
	}

	public void setFreqPatterns(Map<String, Integer> freqPatterns) {
		this.freqPatterns = freqPatterns;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(
				"FFFFFFFFFFFFFFFFFFFP TREEEEEEEEEEEEEEEEEEEEEEE\n");
		result.append(root);

		return result.toString();
	}
}
