package model;

import java.util.ArrayList;
import java.util.List;

public class FPNode {
	private FPNodeContainer node;
	private List<FPNode> children = new ArrayList<FPNode>();
	
	public FPNode() {
		node = null;
	}
	
	public FPNode(FPNodeContainer newNode) {
		node = newNode;
	}

	public FPNodeContainer getNode() {
		return node;
	}

	public void setNode(FPNodeContainer node) {
		this.node = node;
	}

	public List<FPNode> getChildren() {
		return children;
	}

	public void setChildren(List<FPNode> children) {
		this.children = children;
	}

	public FPNode hasChild(char item) {
		FPNode result = null;
		
		for(FPNode c : children) {
			if(c.getNode().getItem() == item) {
				result = c;
			}
		}
		
		return result;
	}
	
	public void putChild(FPNode child) {
		children.add(child);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[ node : " + node);
		result.append(", children (\n");
		for(FPNode c : children) {
			result.append(c + "");
		}
		result.append(" )]\n");
		return result.toString();
	}
	
}
