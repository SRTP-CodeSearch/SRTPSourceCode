package Node;

public class MethodNode implements Node {
	public String MethodName;
	public String param;
	public String stringIndexOfClass;
	public boolean parseState;

	public MethodNode(String MethodName, String param, String stringIndexOfClass) {
		this.MethodName = MethodName;
		this.param = param;
		this.parseState = false;
		this.stringIndexOfClass = stringIndexOfClass;
	}

	public String toString() {
		return MethodName + " " + param + " " + stringIndexOfClass;
	}

	public String getString() {
		return MethodName + "--" + param + "--" + stringIndexOfClass;
	}

	public int hashCode() {
		return (MethodName + "--" + param + "--" + stringIndexOfClass)
				.hashCode();
	}
}
