package Node;

import java.util.*;

/*
 * 用与每个Node的内容有关的整型数索引来表示节点
 * */
public class RelMap {
	public class PairsOfNode {
		Integer node1;
		Integer node2;

		public PairsOfNode(int node1, int node2) {
			this.node1 = node1;
			this.node2 = node2;
		}

		public int hashCode() {
			return (node1.toString() + " " + node2.toString()).hashCode();
		}

		public String toString() {
			return node1.toString() + "------->" + node2.toString();
		}

		public int getNode1Index() {
			return node1.intValue();
		}

		public int getNode2Index() {
			return node2.intValue();
		}
	}

	public HashMap<Integer, PairsOfNode> containRelMap;

	public RelMap() {
		this.containRelMap = new HashMap<Integer, PairsOfNode>();
	}

	// 在这里统一规定，关系的开始节点为Node1，关系的结束节点为Node2
	public void addOneRel(int indexOfNode1, int indexOfNode2) {
		PairsOfNode pairs = new PairsOfNode(indexOfNode1, indexOfNode2);
		this.containRelMap.put(pairs.hashCode(), pairs);
	}

	public void addOneRel(String indexOfNode1, String indexOfNode2) {
		this.addOneRel(indexOfNode1.hashCode(), indexOfNode2.hashCode());
	}

	public void addOneRel(Node node1, Node node2) {
		PairsOfNode pairs = new PairsOfNode(node1.hashCode(), node2.hashCode());
		this.containRelMap.put(pairs.hashCode(), pairs);
	}

	public PairsOfNode getOneRel(int indexOfNode1, int indexOfNode2) {
		PairsOfNode temp = new PairsOfNode(indexOfNode1, indexOfNode2);
		return this.containRelMap.get(temp.hashCode());
	}

	public PairsOfNode getOneRel(String indexOfNode1, String indexOfNode2) {
		return this.getOneRel(indexOfNode1.hashCode(), indexOfNode2.hashCode());
	}

	public Collection<PairsOfNode> getAll() {
		return this.containRelMap.values();
	}

	// 用整型数的索引去表示所有的关系
	public void printAllRel() {
		for (PairsOfNode iter : this.containRelMap.values()) {
			System.out.println(iter);
		}
	}
	// public boolean contiansRel(int indexOfNode1,int indexOfNode2){
	// PairsOfNode temp = new PairsOfNode(indexOfNode1,indexOfNode2);
	// return this.containRelMap.containsKey(temp.hashCode());
	// }

}
