package Node;

public class ProjectNode implements Node {
	public String projectName;
	public String fileLocation;
	public boolean parseState;

	public ProjectNode(String projectName, String fileLocation) {
		this.projectName = projectName;
		this.fileLocation = fileLocation;
		this.parseState = false;
	}

	public String toString() {
		return projectName + " " + fileLocation;
	}

	public String getString() {
		return projectName + "--" + fileLocation;
	}

	public int hashCode() {
		return (projectName + "--" + fileLocation).hashCode();
	}

}
