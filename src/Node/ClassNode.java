package Node;

public class ClassNode implements Node {
	public String className;
	public String packageName;
	public String classType;
	public String fileLocation;
	public boolean parseState;

	public ClassNode(String className, String packageName, String classType,
			String fileLocation) {
		this.className = className;
		this.packageName = packageName;
		this.classType = classType;
		this.parseState = false;
		this.fileLocation = fileLocation;
	}

	// 当解析invoke，use，extends关系的时候，可能关系的某个节点并未被解析或者也不会被解析，这个时候没有它的fileLocation
	public ClassNode(String className, String packageName, String classType) {
		this(className, packageName, classType, "");
	}

	public String toString() {
		return className + " " + packageName + " " + classType + " "
				+ fileLocation;
	}

	public String getString() {
		return className + "--" + packageName + "--" + classType;
	}

	public int hashCode() {
		return (className + "--" + packageName + "--" + classType).hashCode();
	}
}
