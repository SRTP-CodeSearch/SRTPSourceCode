package Node;

import java.util.*;

public class ClassNodeMap {
	// 保存最近一次Class声明的ClassNode，用来在method声明的时候创建method的contain关系
	public ClassNode lastRecentDecClassNode;
	public HashMap<Integer, ClassNode> classNodeMap;

	public ClassNodeMap() {
		this.classNodeMap = new HashMap<Integer, ClassNode>();
	}

	public boolean checkNodeExist(String className, String packageName,
			String classType) {
		ClassNode temp = new ClassNode(className, packageName, classType);
		return this.classNodeMap.containsKey(temp.hashCode());
	}

	public void addNode(String className, String packageName, String classType,
			String fileLocation) {
		ClassNode temp = new ClassNode(className, packageName, classType,
				fileLocation);
		this.classNodeMap.put(temp.hashCode(), temp);
		lastRecentDecClassNode = temp;
	}

	public void addNode(String className, String packageName, String classType) {
		ClassNode temp = new ClassNode(className, packageName, classType);
		this.classNodeMap.put(temp.hashCode(), temp);
	}

	public void addNode(ClassNode node) {
		this.classNodeMap.put(node.hashCode(), node);
		lastRecentDecClassNode = node;
	}

	public ClassNode getNode(String className, String packageName,
			String classType) {
		ClassNode temp = new ClassNode(className, packageName, classType);
		return this.classNodeMap.get(temp.hashCode());
	}

	public ClassNode getNode(int indexOfNode) {
		return this.classNodeMap.get(indexOfNode);
	}

	public Collection<ClassNode> getAllNodes() {
		return this.classNodeMap.values();
	}

	public void printAllNodes() {
		for (ClassNode iter : this.classNodeMap.values()) {
			System.out.println(iter);
		}
	}

	public void printAllNodesIntIndex() {
		for (ClassNode iter : this.classNodeMap.values()) {
			System.out.println(iter.hashCode());
		}
	}

	public void printAllNodesIndex() {
		for (ClassNode iter : this.classNodeMap.values()) {
			System.out.println(iter.hashCode() + "---" + iter);
		}
	}
}
