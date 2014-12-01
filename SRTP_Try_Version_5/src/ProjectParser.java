
import java.util.Collection;
import java.util.List;
import java.util.Map;


import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

/**
 * The Parser is used for parsing the Java project 
 * @author 
 *
 */
public class ProjectParser extends AbstractParser {
	/* add the logger of the feature parser */
	//static Logger logger = Logger.getLogger(ProjectParser.class.getName());
	/* The scope of the project under analysis */
	private String[] sourcepathEntries;
	/* All classpaths used for parsing the source files */
	private String[] classpathEntries;
	/* The paths of all source files under analysis */
	private String[] sourcepaths;
	
	/**
	 * Constructor
	 * @param sourcepathEntries
	 * @param classpathEntries
	 */
	public ProjectParser(String projName,String fileLocation, String[] sourcepathEntries, 
			String[] classpathEntries, String[] sourcepaths) {
		super(projName,fileLocation);
		this.sourcepathEntries = sourcepathEntries;
		this.classpathEntries = classpathEntries;
		this.sourcepaths  = sourcepaths;
	}
	
	/**
	 * Constructor
	 * @param sourcepaths
	 * @param classpathEntries
	 */
	public ProjectParser(String projName, String fileLocation,String[] sourcepathEntries, 
			Collection<String> classpathEntries, Collection<String> sourcepaths) {
		super(projName,fileLocation);
		this.sourcepathEntries = sourcepathEntries;
		this.classpathEntries = classpathEntries.toArray(new String[classpathEntries.size()]);
		this.sourcepaths = sourcepaths.toArray(new String[sourcepaths.size()]);
	}

	@SuppressWarnings("rawtypes")
	public void parse() {
		// create a AST parser
		ASTParser pars = ASTParser.newParser(AST.JLS3);
		// set the environment for the AST parsers
		pars.setEnvironment(/*classpathEntries*/classpathEntries, /*sourcepathEntries*/sourcepathEntries, 
				/*encodings*/null, true);
		// enable binding
		pars.setResolveBindings(true);
		pars.setBindingsRecovery(true);
		pars.setStatementsRecovery(true);
		pars.setKind(ASTParser.K_COMPILATION_UNIT);
		// set the compiler option
		Map complierOptions= JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, complierOptions);
		pars.setCompilerOptions(complierOptions);
		pars.createASTs(sourcepaths, null, new String[0], requestor, null);
	}
}
