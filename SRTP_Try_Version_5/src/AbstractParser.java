
public abstract class AbstractParser  {

	/* The feature requestor generated by feature parser */
	protected Requestor requestor;
	
	public AbstractParser(String projName , String fileLoaction) {
		this.requestor = new Requestor(projName,fileLoaction);
	}

	// return the feature requestor
	public Requestor getRequestor() {
		return requestor;
	}
	
}