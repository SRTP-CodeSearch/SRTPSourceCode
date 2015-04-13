package Node;

import java.util.*;

public class MethodNodeMap {
	public MethodNode lastRecentDecMethodNode;
	public HashMap<Integer, MethodNode> methodNodeMap;

	public MethodNodeMap() {
		this.methodNodeMap = new HashMap<Integer, MethodNode>();
	}

	public boolean checkNodeExist(String methodName, String param,
			String stringIndexOfClass) {
		MethodNode temp = new MethodNode(methodName, param, stringIndexOfClass);
		return this.methodNodeMap.containsKey(temp.hashCode());
	}

	public void addNode(String methodName, String param,
			String stringIndexOfClass) {
		MethodNode temp = new MethodNode(methodName, param, stringIndexOfClass);
		this.methodNodeMap.put(temp.hashCode(), temp);
	}

	public void addNode(MethodNode node) {
		this.methodNodeMap.put(node.hashCode(), node);
	}

	public MethodNode getNode(String methodName, String param,
			String stringIndexOfClass) {
		MethodNode temp = new MethodNode(methodName, param, stringIndexOfClass);
		return this.methodNodeMap.get(temp.hashCode());
	}

	public MethodNode getNode(int indexOfNode) {
		return this.methodNodeMap.get(indexOfNode);
	}

	public void printAllNodes() {
		for (MethodNode iter : methodNodeMap.values())
			System.out.println(iter);
	}

	public void printAllNodesIntIndex() {
		for (MethodNode iter : methodNodeMap.values())
			System.out.println(iter.hashCode() + "---" + iter.MethodName
					+ "---" + iter.stringIndexOfClass);
	}

	public Collection<MethodNode> getAllNodes() {
		return this.methodNodeMap.values();
	}
}
