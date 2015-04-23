package model;

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
		root = new FPNode();
		this.minsup = minsup;

		for(String itemForTable : flist.keySet()) {
			headerTable.add(new FPNode(itemForTable));
		}
		
		System.out.println("\theader table : "+headerTable);

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
				System.out.println(childIdx);
				if(cursup == 1) {
					System.out.println(item + ": "+cursup);
					newNode = new FPNode(cursup, item, tempNode);
					tempNode.putChild(newNode);
					
					int tableIdx = -1; 
					for(int i = 0; i < headerTable.size(); i++) {
						if(headerTable.get(i).getItem() == item) {
							tableIdx = i;
							break;
						}
					}
					System.out.println(tableIdx);
					FPNode tempNodeLink = headerTable.get(tableIdx);
					
					while(tempNodeLink.getNodeLink() != null) tempNodeLink = tempNodeLink.getNodeLink();
					
					tempNodeLink.setNodeLink(newNode);
				} else {
					newNode = tempNode.getChild(item);
					newNode.setSupport(cursup);
				}
				
				tempNode = newNode;
			} // done
//
//			for (int i = 0; i < transaction.length(); i++) {
//				item = transaction.StringAt(i);
//				int cursup = 1;
//				int j = 0;
//				
//				if(!tempNode.getChildren().isEmpty()) {
//					for (; j < tempNode.getChildren().size(); j++) {
//						if (tempNode.getChildren().get(j).getItem() == item) {
//							cursup = tempNode.getChildren().get(j).getSupport() + 1;
//							break;
//						}
//					}
//				}
//
//				newNode = new FPNode(cursup, item, tempNode);
//
//				if (cursup == 1) {
//					tempNode.putChild(newNode);
//
//					int tableIdx = tableHasItem(item);
//					if (tableIdx == -1) {
//						headerTable.add(new FPNode(item, newNode));
//					} else {
//						FPNode tempNodeLink = headerTable.get(tableIdx).getNodeLink();
//						while (tempNodeLink.getNodeLink() != null) {
//							tempNodeLink = tempNodeLink.getNodeLink();
//						}
//						tempNodeLink.setNodeLink(newNode);
//					}
//					
//					tempNode = newNode;
//				} else {
//					tempNode.getChildren().get(j).setSupport(cursup);
//					
//					tempNode = tempNode.getChildren().get(j);
//				}
//				
//			}
//		}
//		
////		System.out.println(headerTable);
		}
//		System.out.println(root);
	}

	public FPTree(Vector<FPNode> headerTable, FPNode root, int minsup) {
		this.headerTable = headerTable;
		this.root = root;
		this.minsup = minsup;
	}

	int tableHasItem(String item) {
		for (int i = 0; i < headerTable.size(); i++) {
			if (headerTable.get(i).getItem() == item) {
				return i;
			}
		}
		return -1;
	}
	
	public void growth() {
		growth(root, null, headerTable);
	}
	
	public void growth(FPNode root, String base, Vector<FPNode> headerTable) {
		for(FPNode iteminTree : headerTable) {
//			System.out.println(iteminTree);
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
//					int count = (conditionalPatternBase.containsKey(conditionalPattern)) ? conditionalPatternBase.get(conditionalPattern) : 0;
//					conditionalPatternBase.put(conditionalPattern, count + iteminTree.getSupport());
					conditionalPatternBase.put(conditionalPattern, iteminTree.getSupport());
//					System.out.println(conditionalPatternBase.get(conditionalPattern));
				}
				
			}
			
			if(supportofCurrentPattern >= minsup) {
				freqPatterns.put(currentPattern, supportofCurrentPattern);
			}
//			System.out.println(freqPatterns);
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
//				if(count < minsup) { continue; }
				condHeaderTable.add(new FPNode(itemsforTable, count)); 
			}
			FPNode conditionalFPtree = condFPtreeConstructor(conditionalPatternBase, conditionalItemsMaptoFreq, condHeaderTable);
//			System.out.println(conditionalFPtree);
			
			if(!conditionalFPtree.getChildren().isEmpty()) {
//				System.out.println("recursive");
				growth(conditionalFPtree, currentPattern, condHeaderTable);
			}
			
		}
		
		
	}
	

	private FPNode condFPtreeConstructor(Map<String, Integer> conditionalPatternBase, Map<String, Integer> conditionalItemsMaptoFreq, Vector<FPNode> condHeaderTable) {
		
		//TODO
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
				// TODO
				// put newNode 
				if(childIdx == -1) {
					// if tempNode doesn't have p as a child
					newNode = new FPNode(cursup, p, tempNode);
					tempNode.putChild(newNode);

					// put newNode in the header table
					int tableIdx = -1;
					for(int i = 0; i < condHeaderTable.size(); i++) {
						if(condHeaderTable.get(i).getItem() == p) {
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
