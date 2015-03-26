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
	}
	
	public FPNode(int support, char item, FPNode parent) {
		this.support = support;
		this.item = item;
		this.parent = parent;
		nodeLink = null;
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
	}

	@Override
	public String toString() {
		return "FPNode [support=" + support + ", item=" + item + ", parent="
				+ parent + ", nodeLink=" + nodeLink + ", children=" + children
				+ "]";
	}

}
