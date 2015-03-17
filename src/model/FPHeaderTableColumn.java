package model;

public class FPHeaderTableColumn {
	private char itemName;
	private FPNodeContainer nodeLink = null;
	
	public FPHeaderTableColumn (char item) {
		itemName = item;
	}

	public char getItemName() {
		return itemName;
	}

	public void setItemName(char itemName) {
		this.itemName = itemName;
	}

	public FPNodeContainer getNodeLink() {
		return nodeLink;
	}

	public void setNodeLink(FPNodeContainer nodeLink) {
		this.nodeLink = nodeLink;
	}
	
	
}
