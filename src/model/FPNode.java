package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FPNode {
	private int support;
	private char item;
	private FPNode parent;
	private FPNode nodeLink;
	private Vector<FPNode> children = new Vector<FPNode>(); 
	
	public FPNode() {
		support = 0;
		parent = null;
		nodeLink = null;
		item = ' ';
	}
	
	public FPNode(int support, char item, FPNode parent) {
		this.support = support;
		this.item = item;
		this.parent = parent;
		this.nodeLink = null;
	}
	
	public FPNode(char item, FPNode nodeLink) {
		this.item = item;
		this.nodeLink = nodeLink;
	}
	
	public FPNode(char item, int support) {
		this.item = item;
		this.support = support;
	}
	
	public FPNode(char item) {
		this.item = item;
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


	public FPNode getParent() {
		return parent;
	}


	public void setParent(FPNode parent) {
		this.parent = parent;
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
	
	public int getChildIdx(char item) {
		int childIdx = -1;
		
		for(int i = 0; i < children.size(); i++) {
			if(children.get(i).getItem() == item) {
				childIdx = i;
				break;
			}
		}
		
		return childIdx;
	}
	
	public FPNode getChild(char item) {
		return children.get(getChildIdx(item));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FPNode["+support+","+item+"]\n");
		builder.append(children);
		return builder.toString();
	}

}
