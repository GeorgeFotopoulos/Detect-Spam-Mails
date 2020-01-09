public class DecisionTree {
	
	String word;
	int frequency;
	ID3node root;

	public void add(ID3node node) {
		if(root==null) {
			root=node;
			root.right=new ID3node(node.decision);
		} else {
			addRecursive(root, node);
		}
	}
	
	private void addRecursive(ID3node current, ID3node node) {
		if (current==null) {
			current = node;
			current.right = new ID3node(current.decision);
		} else {
			if(current.left==null) {
				current.left = node;
				current.left.right = new ID3node(node.decision);
			} else {
				addRecursive(current.left,node);
			}
		}
	}

}