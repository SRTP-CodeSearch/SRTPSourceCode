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

	// ������invoke��use��extends��ϵ��ʱ�򣬿��ܹ�ϵ��ĳ���ڵ㲢δ����������Ҳ���ᱻ���������ʱ��û������fileLocation
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
