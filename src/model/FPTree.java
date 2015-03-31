package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

public class FPTree {
	private Vector<FPNode> headerTable = new Vector<FPNode>();
	private FPNode root;
	private int minsup;
	private Map<String, Integer> freqPatterns;
	
	public FPTree() {
		root = null;
	}

	public FPTree(String[] DB, Map<Character, Integer> flist, int minsup) {
		root = new FPNode();
		this.minsup = minsup;

		FPNode tempNode;
		FPNode newNode;
		char item;

		for (String t : DB) {
			tempNode = root;

			for (int i = 0; i < t.length(); i++) {
				item = t.charAt(i);
				int cursup = 1;
				int j = 0;
				
				if(!tempNode.getChildren().isEmpty()) {
					for (; j < tempNode.getChildren().size(); j++) {
						if (tempNode.getChildren().get(j).getItem() == item) {
							cursup = tempNode.getChildren().get(j).getSupport() + 1;
							break;
						}
					}
				}

				newNode = new FPNode(cursup, item, tempNode);

				if (cursup == 1) {
					tempNode.putChild(newNode);

					int tableIdx = tableHasItem(item);
					if (tableIdx == -1) {
						headerTable.add(new FPNode(item, newNode));
					} else {
						FPNode tempNodeLink = headerTable.get(tableIdx).getNodeLink();
						while (tempNodeLink.getNodeLink() != null) {
							tempNodeLink = tempNodeLink.getNodeLink();
						}
						tempNodeLink.setNodeLink(newNode);
					}
					
					tempNode = newNode;
				} else {
					tempNode.getChildren().get(j).setSupport(cursup);
					
					tempNode = tempNode.getChildren().get(j);
				}
				
			}
		}
		
//		System.out.println(headerTable);
	}

	public FPTree(Vector<FPNode> headerTable, FPNode root, int minsup) {
		this.headerTable = headerTable;
		this.root = root;
		this.minsup = minsup;
	}

	int tableHasItem(char item) {
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
			String currentPattern = (base != null ? base : "") + (base != null ? " " : "") + iteminTree.getItem();
			int supcur = 0; // support of current pattern
			Map<String, Integer> conditionalPatternBase = new HashMap<String, Integer>();
			
			while (iteminTree.getNodeLink() != null) {
				iteminTree = iteminTree.getNodeLink();
				supcur += iteminTree.getSupport();
				String conditionalPattern = null;
				FPNode conditionalItem = iteminTree.getParent();
				
				while(conditionalItem != null) {
					conditionalPattern = conditionalItem.getItem()+ " " + (conditionalPattern != null ? conditionalPattern : "");
					conditionalItem = conditionalItem.getParent();
				}
				if(conditionalPattern != null) {
					int count = (conditionalPatternBase.containsKey(conditionalPattern)) ? conditionalPatternBase.get(conditionalPattern) : 0;
					conditionalPatternBase.put(conditionalPattern, count + iteminTree.getSupport());
				}
				
			}
			
			freqPatterns.put(currentPattern, supcur);
			
			// count the support of each conditional item
			Map<Character, Integer> conditionalItemsMaptoFreq = new HashMap<Character, Integer>(); 
			for(String conditionalPattern : conditionalPatternBase.keySet()) {
				StringTokenizer tokenizer = new StringTokenizer(conditionalPattern);
				while(tokenizer.hasMoreTokens()) {
					char item = tokenizer.nextToken().charAt(0);
					if(conditionalItemsMaptoFreq.containsKey(item)) {
						int count = conditionalItemsMaptoFreq.get(item);
						count += conditionalPatternBase.get(item);
						conditionalItemsMaptoFreq.put(item, count);
					} else {
						conditionalItemsMaptoFreq.put(item, conditionalPatternBase.get(item));
					}
				}
				
			}
			
			// conditional fp tree
			// 1. headertable creation
			Vector<FPNode> condHeaderTable = new Vector<FPNode>();
			for(char itemsforTable : conditionalItemsMaptoFreq.keySet()) {
				int count = conditionalItemsMaptoFreq.get(itemsforTable);
				if(count < minsup) {
					continue;
				}
				FPNode f = new FPNode(itemsforTable);
				f.setSupport(count);
				condHeaderTable.add(f);
			}
			FPNode conditionalFPtree = condFPtreeConstructor(conditionalPatternBase, conditionalItemsMaptoFreq, condHeaderTable);
			
			if(!conditionalFPtree.getChildren().isEmpty()) {
				growth(conditionalFPtree, currentPattern, condHeaderTable);
			}
			
		}
		
	}
	

	private FPNode condFPtreeConstructor(Map<String, Integer> conditionalPatternBase, Map<Character, Integer> conditionalItemsMaptoFreq, Vector<FPNode> condHeaderTable) {
		
		//TODO
		FPNode condFPtree = new FPNode();
		
		for(String pattern : conditionalPatternBase.keySet()) {
			Vector<Character> patternVector = new Vector<Character>();
			StringTokenizer tokenizer = new StringTokenizer(pattern);
			while(tokenizer.hasMoreTokens()) {
				char item = tokenizer.nextToken().charAt(0);
				if(conditionalItemsMaptoFreq.get(item) > minsup) {
					patternVector.addElement(item);
				}
			}
		}
		
		return null;
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

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(
				"FFFFFFFFFFFFFFFFFFFP TREEEEEEEEEEEEEEEEEEEEEEE\n");
		result.append(root);

		return result.toString();
	}
}
