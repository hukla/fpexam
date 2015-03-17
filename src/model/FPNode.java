package model;

public class FPNode {
	private FPNodeContainer node;
	private FPNodeContainer []children;
	
	public FPNode() {
		node = null;
		children = null;
	}
	
	public FPNode(FPNodeContainer newNode) {
		node = newNode;
	}
}
