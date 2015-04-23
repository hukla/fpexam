package model;

import java.util.Vector;

public class FPNode {
	private int support;
	private String item;
	private FPNode parent;
	private FPNode nodeLink;
	private Vector<FPNode> children = new Vector<FPNode>();

	public FPNode() {
		support = 0;
		parent = null;
		nodeLink = null;
		item = "";
	}

	public FPNode(int support, String item, FPNode parent) {
		this.support = support;
		this.item = item;
		this.parent = parent;
		this.nodeLink = null;
	}

	public FPNode(String item, FPNode nodeLink) {
		this.item = item;
		this.nodeLink = nodeLink;
	}

	public FPNode(String item, int support) {
		this.item = item;
		this.support = support;
	}

	public FPNode(String item) {
		this.item = item;
	}

	public FPNode getParent() {
		return parent;
	}

	public void setParent(FPNode parent) {
		this.parent = parent;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}
	
	public int getSupport() {
		return support;
	}


	public void setSupport(int support) {
		this.support = support;
	}

	public FPNode getNodeLink() {
		return nodeLink;
	}

	public void setNodeLink(FPNode nodeLink) {
		this.nodeLink = nodeLink;
	}

	public Vector<FPNode> getChildren() {
		return children;
	}

	public void setChildren(Vector<FPNode> children) {
		this.children = children;
	}

	public void putChild(FPNode child) {
		children.add(child);
		child.setParent(this);
	}

	public int getChildIdx(String item) {
		int childIdx = -1;

		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).getItem() == item) {
				childIdx = i;
				break;
			}
		}

		return childIdx;
	}

	public FPNode getChild(String item) {
		return children.get(getChildIdx(item));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FPNode[" + support + "," + item + "]\n");
		builder.append(children);
		return builder.toString();
	}

}
