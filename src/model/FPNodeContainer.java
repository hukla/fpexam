package model;

public class FPNodeContainer {
	private int support;
	private char item;
	private FPNodeContainer parent;
	private FPNodeContainer nodeLink;
	
	public FPNodeContainer() {
		support = 0;
		item = 0;
		parent = null;
		nodeLink = null;
	}
	
	public FPNodeContainer(int support, char item, FPNodeContainer parent) {
		this.support = support;
		this.item = item;
		this.parent = parent;
	}
	
	public int getSupport() {
		return support;
	}
	public void setSupport(int support) {
		this.support = support;
	}
	public char getItem() {
		return item;
	}
	public void setItem(char item) {
		this.item = item;
	}
	public FPNodeContainer getParent() {
		return parent;
	}
	public void setParent(FPNodeContainer parent) {
		this.parent = parent;
	}
	public FPNodeContainer getNodeLink() {
		return nodeLink;
	}
	public void setNodeLink(FPNodeContainer nodeLink) {
		this.nodeLink = nodeLink;
	}

	@Override
	public String toString() {
		return "[support=" + support + ", item=" + item + "]";
	}
	
}
