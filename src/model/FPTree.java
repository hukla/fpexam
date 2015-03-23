package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FPTree {
	private List<FPHeaderTableColumn> headerTable = new ArrayList<FPHeaderTableColumn>();
	private FPNode root;
	private Map<Character, Integer> columncounts = new HashMap<Character, Integer>();
	private FPSupportedSets tempsets;

	private int tempindex = 0;
	private int numOfNodes;

	public FPTree() {
		root = null;
		tempsets = null;
		numOfNodes = 0;
	}

	public FPTree(String[] DB, Map<Character, Integer> flist) {
		root = new FPNode();
		root.setNode(null);

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
		
		System.out.println(headerTable);
	}

	int tableHasItem(char item) {
		for (int i = 0; i < headerTable.size(); i++) {
			if (headerTable.get(i).getItemName() == item) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(
				"FFFFFFFFFFFFFFFFFFFP TREEEEEEEEEEEEEEEEEEEEEEE\n");
		result.append(root);

		return result.toString();
	}
}
