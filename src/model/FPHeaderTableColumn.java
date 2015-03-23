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
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append("[itemName: "+itemName);
		result.append(", nodeLink: ");
		FPNodeContainer temp = nodeLink;
		while(temp != null) {
			result.append(temp + ",");
			temp = temp.getNodeLink();
		}
		result.append("]\n");
		
		return result.toString();
	}
}
