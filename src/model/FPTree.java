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

	public FPTree(Vector<String> DBVector, Map<Character, Integer> flist, int minsup) {
		root = new FPNode();
		this.minsup = minsup;
		
		for(char itemForTable : flist.keySet()) {
			headerTable.add(new FPNode(itemForTable));
		}
		
		System.out.println("header table : "+headerTable);

		FPNode tempNode;
		FPNode newNode;
		char item;

		for (String transaction : DBVector) {
			tempNode = root;
			
			StringTokenizer tokenizer = new StringTokenizer(transaction);
			while(tokenizer.hasMoreTokens()) {
				item = tokenizer.nextToken().charAt(0);
				int childIdx = tempNode.getChildren().indexOf(item);
				int cursup = (childIdx != -1) ? tempNode.getChildren().get(childIdx).getSupport() + 1 : 1;
				
				newNode = new FPNode(cursup, item, tempNode);
				
				if(cursup == 1) {
					tempNode.putChild(newNode);
				} else {
					tempNode.getChildren().set(childIdx, newNode);
				}
				
				int tableIdx = headerTable.indexOf(item); // TODO item이 있는 FPNode를 찾아야댐 개빡침
				FPNode tempNodeLink = headerTable.get(tableIdx);
				
				while(tempNodeLink.getNodeLink() != null) tempNodeLink = tempNodeLink.getNodeLink();
					
				tempNodeLink.setNodeLink(newNode);
				tempNode = newNode;
			} // done
//
//			for (int i = 0; i < transaction.length(); i++) {
//				item = transaction.charAt(i);
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
			System.out.println(iteminTree);
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
		FPNode condFPtree = new FPNode(); // root
		
		FPNode tempNode;
		FPNode newNode;
		char item;
		
		for(String pattern : conditionalPatternBase.keySet()) {
			tempNode = condFPtree;
			
			
			Vector<Character> patternVector = new Vector<Character>();
			StringTokenizer tokenizer = new StringTokenizer(pattern);
			while(tokenizer.hasMoreTokens()) {
				item = tokenizer.nextToken().charAt(0);
				if(conditionalItemsMaptoFreq.get(item) > minsup) {
					patternVector.addElement(item);
				}
			} // creating pattern vector
			
			for(char p : patternVector) {
				int childIdx = tempNode.getChildren().indexOf(p);
				int cursup = (childIdx != -1) ? tempNode.getChildren().get(childIdx).getSupport() + 1 : 1;
				
				// create new node to put
				newNode = new FPNode(cursup, p, tempNode);
				
				// put newNode 
				if(cursup == 1) {
					// if tempNode doesn't have p as a child
					tempNode.putChild(newNode);
				} else {
					// if tempNode does have p as a child
					tempNode.getChildren().set(childIdx, newNode);
				}
				
				// put newNode in the header table
				int tableIdx = condHeaderTable.indexOf(p);
				FPNode tempNodeLink = condHeaderTable.get(tableIdx).getNodeLink();
				
				while(tempNodeLink.getNodeLink() != null) {
					tempNodeLink = tempNodeLink.getNodeLink();
				}
				
				tempNodeLink.setNodeLink(newNode);
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
