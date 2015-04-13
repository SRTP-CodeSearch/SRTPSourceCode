package Node;

import java.util.*;

public class ProjectNodeMap {
	// 保存最后一个project，用来表示当前的project
	public ProjectNode lastRecentNode;
	public HashMap<Integer, ProjectNode> projectNodeMap;

	public ProjectNodeMap() {
		this.projectNodeMap = new HashMap<Integer, ProjectNode>();
	}

	public boolean checkNodeExist(String projectName, String fileLocation) {
		ProjectNode temp = new ProjectNode(projectName, fileLocation);
		return this.projectNodeMap.containsKey(temp.hashCode());
	}

	public void addNode(String projectName, String fileLocation) {
		ProjectNode temp = new ProjectNode(projectName, fileLocation);
		this.projectNodeMap.put(temp.hashCode(), temp);
		lastRecentNode = temp;
	}

	public void addNode(ProjectNode node) {
		this.projectNodeMap.put(node.hashCode(), node);
		lastRecentNode = node;
	}

	public ProjectNode getNode(String projectName, String fileLocation) {
		ProjectNode temp = new ProjectNode(projectName, fileLocation);
		return this.projectNodeMap.get(temp.hashCode());
	}

	public ProjectNode getNode(int indexOfNode) {
		return this.projectNodeMap.get(indexOfNode);
	}

	public Collection<ProjectNode> getAllNodes() {
		return this.projectNodeMap.values();
	}

	public void printAllNodes() {
		for (ProjectNode iter : this.projectNodeMap.values()) {
			System.out.println(iter);
		}
	}

	public void printAllNodesIntIndex() {
		for (ProjectNode iter : this.projectNodeMap.values())
			System.out.println(iter.hashCode() + "---" + iter.projectName);
	}
}
