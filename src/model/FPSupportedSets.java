package model;

public class FPSupportedSets {
	private char[] itemset;
	private int support;
	private FPSupportedSets nodelink;
	
	public FPSupportedSets() {
		itemset = null;
		support = 0;
		nodelink = null;
	}

	public FPSupportedSets(char[] itemset, int support, FPSupportedSets nodelink) {
		this.itemset = itemset;
		this.support = support;
		this.nodelink = nodelink;
	}

	public char[] getItemset() {
		return itemset;
	}

	public void setItemset(char[] itemset) {
		this.itemset = itemset;
	}

	public int getSupport() {
		return support;
	}

	public void setSupport(int support) {
		this.support = support;
	}

	public FPSupportedSets getNodelink() {
		return nodelink;
	}

	public void setNodelink(FPSupportedSets nodelink) {
		this.nodelink = nodelink;
	}
	
}
