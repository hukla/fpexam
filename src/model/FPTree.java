package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FPTree {
	private List<FPHeaderTableColumn> headerTable = new ArrayList<FPHeaderTableColumn>();
	private FPNode root;
	private int minsup;
	
	public FPTree() {
		root = null;
	}

	public FPTree(String[] DB, Map<Character, Integer> flist, int minsup) {
		root = new FPNode();
		root.setNode(null);
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
				for (; !(tempNode.getChildren().isEmpty()) && j < tempNode.getChildren().size(); j++) {
					if (tempNode.getChildren().get(j).getNode().getItem() == item) {
						cursup = tempNode.getChildren().get(j).getNode().getSupport() + 1;
						break;
					}
				}

				newNode = new FPNode(new FPNodeContainer(cursup, item, tempNode.getNode()));

				if (cursup == 1) {
					tempNode.putChild(newNode);

					int tableIdx = tableHasItem(item);
					if (tableIdx == -1) {
						headerTable.add(new FPHeaderTableColumn(item));
						headerTable.get(headerTable.size()-1).setNodeLink(newNode.getNode());
					} else {
						FPNodeContainer tempNodeLink = headerTable.get(tableIdx).getNodeLink();
						while (tempNodeLink.getNodeLink() != null) {
							tempNodeLink = tempNodeLink.getNodeLink();
						}
						tempNodeLink.setNodeLink(newNode.getNode());
					}
					
					tempNode = newNode;
				} else {
					tempNode.getChildren().get(j).getNode().setSupport(cursup);
					
					tempNode = tempNode.getChildren().get(j);
				}
				
			}
		}
		
//		System.out.println(headerTable);
	}

	public FPTree(List<FPHeaderTableColumn> headerTable, FPNode root, int minsup) {
		this.headerTable = headerTable;
		this.root = root;
		this.minsup = minsup;
	}

	int tableHasItem(char item) {
		for (int i = 0; i < headerTable.size(); i++) {
			if (headerTable.get(i).getItemName() == item) {
				return i;
			}
		}
		return -1;
	}
	
	FPNode hasSinglePrefixPath() {
		FPNode tail = root;
		//TODO
		return tail;
	}

	public void growth() {
		FPNode z;
		if((z = hasSinglePrefixPath()) != null) {
			// if tree contains a single prefix path Z
			List<FPHeaderTableColumn> tempHtable = headerTable;
			int i = 0;
			for(; i < headerTable.size(); i++) {
				if(headerTable.get(i).getItemName() == z.getNode().getItem()) {
					break;
				}
			}
			tempHtable.remove(i);

			FPTree subtree = new FPTree(tempHtable, z, minsup);
			subtree.growth();
			generateARs();
		} else {
			//TODO generate pattern????
		}
	}
	
	public void generateARs() {
		// TODO
	}
	
	
	public List<FPHeaderTableColumn> getHeaderTable() {
		return headerTable;
	}

	public void setHeaderTable(List<FPHeaderTableColumn> headerTable) {
		this.headerTable = headerTable;
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
