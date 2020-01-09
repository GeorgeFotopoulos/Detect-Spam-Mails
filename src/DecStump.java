public class DecStump {
	
	ABnode root;
	
	DecStump(String name, int decision) {
		root = new ABnode(name);
		root.left = new ABnode(-1 * decision);
		root.right = new ABnode(decision);
	}
	
}