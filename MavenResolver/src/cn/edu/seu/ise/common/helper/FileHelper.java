package cn.edu.seu.ise.common.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.NonNull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class used to improve the use of class {@link File}. 
 * 
 * @author Dong Qiu
 *
 */
public class FileHelper {
	
	/** Logger */
	private static Logger logger = LoggerFactory.getLogger(FileHelper.class.getName());
	
	/**
	 * Checks whether the given file is a valid directory.
	 * 
	 * @param file  the file to validate
	 * @return {@code true} if the given file is a valid directory, 
	 * and {@code false} if the given file is not a valid directory 
	 */
	public static boolean isValidDir(@NonNull File dir) {
		if(!dir.exists()) {
			return false;
		} 
		if(!dir.isDirectory()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks whether the given path is a valid directory. 
	 * <p>
	 * An invocation of this method behaves in exactly the same way as the invocation
	 * <pre>
	 * {@link #isValidDir(File)}(new {@link File}(path));
	 * </pre>
	 * </p>
	 * 
	 * @param dirPath  the path to validate
	 * @return {@code true} if the given directory path is valid, 
	 * and {@code false} if the given path is not valid
	 */
	public static boolean isValidDir(@NonNull String dirPath) {
		return isValidDir(new File(dirPath));
	}
	
	/**
	 * Checks whether the given file is valid. 
	 * <p>
	 * A valid file needs to satisfy two conditions:
	 * (1) The file exists;
	 * (2) The file is not a directory.
	 * </p>
	 * 
	 * @param file  the file to validate
	 * @return  {@code true} if the given file is a valid file, 
	 * and {@code false} if the given file is not a valid file 
	 */
	public static boolean isValidFile(@NonNull File file) {
		if(!file.exists()) {
			return false;
		} 
		if(file.isDirectory()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks whether the given path is a valid file. 
	 * <p>
	 * An invocation of this method behaves in exactly the same way as the invocation
	 * <pre>
	 * {@link #isValidFile(File)}(new {@link File}(path));
	 * </pre>
	 * </p>
	 * 
	 * @param filePath  the path to validate
	 * @return {@code true} if the given file path is valid, 
	 * and {@code false} if the given file path is not valid
	 */
	public static boolean isValidFile(@NonNull String filePath) {
		return isValidFile(new File(filePath));
	}
	
	/**
	 * Checks whether the path is valid for output. 
	 * <p>
	 * A valid output file path involves two situations: 
	 * (1) If the file of given path exists, it is a file;
	 * (2) If the file of given path does not exist, 
	 * a file can be created successfully. 
	 * </p>
	 * 
	 * @param filePath  the file path to validate
	 * @return  {@code true} if the given file path is valid, 
	 * and {@code false} if the given file path is not valid 
	 */
	public static boolean canWrite(@NonNull String filePath) {
		File file = new File(filePath);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.warn("Fail to create file {}, \n{}", filePath, e);
				return false;
			}
		}
		if(!file.canWrite()) {
	        return false;
	    }
	    /* Java lies on Windows */
	    try {
	        new FileOutputStream(file, true).close();
	    } catch (IOException e) {
	        logger.error("{} is not writable: {}", filePath, e.getLocalizedMessage());
	        return false;
	    }
	    return true;
	}
	
	/**
	 * Cleans the content of a file quietly. 
	 * 
	 * @param file  the file to be cleaned
	 */
	public static void cleanFileQuitely(@NonNull File file) {
		if(!file.exists()) {
			return;
		}
		try(PrintWriter writer = new PrintWriter(file)) {
			writer.close();
		} catch (FileNotFoundException e) {
			logger.error("Fail to clean file {}. \n{}", file.getAbsolutePath(), e);
		}
	}
	
	/**
	 * Gets all directory names of a file (Root directory is not included). 
	 * 
	 * @param filePath  the file path
	 * @return  the list of directory names
	 */
	public static List<String> getParentDirNames(String filePath) {
		List<String> dirNames = new ArrayList<>();
		File parent = new File(filePath).getParentFile();
		while(parent != null) {
			if(!parent.getName().equals("")) {
				dirNames.add(parent.getName());
			}
			parent = parent.getParentFile();
		}
		return dirNames;
	}
	
	/**
	 * Clean a directory quietly. 
	 * <p>
	 * It does not deleting the directory. 
	 * </p>
	 * @param directory
	 */
	public static void cleanDirQuietly(@NonNull File directory) {
		if(!directory.exists()) {
			return;
		}
		if(!directory.isDirectory()) {
			return;
		}
 		try {
			FileUtils.cleanDirectory(directory);
		} catch (IOException e) {
			logger.warn("Fail to clean directory {}, \n{}", directory.getAbsolutePath(), e);
		}
	}
	
	/**
	 * Gets char array from the file. 
	 * 
	 * @param file  the file to be read
	 * @param charsetName  the charset name
	 * @return  the char array
	 */
	public static char[] getCharArray(@NonNull File file, @NonNull String charsetName) 
			throws IOException {
		StringBuilder builder = new StringBuilder();
		String line = null;
		try(InputStreamReader isr = new InputStreamReader(new FileInputStream(file), charsetName); 
				BufferedReader reader = new BufferedReader(isr)) {
            while ((line = reader.readLine()) != null) {
            	builder.append(line).append("\n");
            }
            return builder.toString().toCharArray();
		} catch (IOException e) {
			throw new IOException("Fail to get char array from file " + file.getAbsolutePath(), e);
		} 
	}
	
	/**
	 * Gets char array from the file. 
	 * <p>
	 * It uses UTF-8 as default charset. 
	 * </p>
	 * 
	 * @param file  the file to be read
	 * @return  the char array
	 * @throws IOException 
	 */
	public static char[] getCharArray(@NonNull File file) throws IOException {
		return getCharArray(file, "UTF-8");
	}
	
	/**
	 * Gets the char array from the file of given path.
	 * <p>
	 * An invocation of this method behaves in exactly the same way as the invocation
	 * <pre>
	 * {@link #getCharArray(File)}(new {@link File}(path));
	 * </pre>
	 * </p>
	 * 
	 * @param path  the path of the file to be visited
	 * @return  char array of the file
	 * @throws IOException 
	 */
	public static char[] getCharArray(@NonNull String path, @NonNull String charsetName) 
			throws IOException {
		return getCharArray(new File(path), charsetName);
	}
	
	/**
	 * Gets the char array from the file of given path.
	 * <p>
	 * An invocation of this method behaves in exactly the same way as the invocation
	 * <pre>
	 * {@link #getCharArray(String, String)};
	 * </pre>
	 * </p>
	 * 
	 * @param path  the path of the file to be visited
	 * @throws IOException 
	 */
	public static char[] getCharArray(@NonNull String path) throws IOException {
		return getCharArray(path, "UTF-8");
	}
	
	/**
	 * Gets clean source code from the file. 
	 * <p>
	 * The clean source code does not contain comments, blank lines and white spaces. 
	 * </p>
	 *  
	 * @param file  the source file to be visited
	 * @return  the clean string of source code
	 * @throws IOException 
	 */
	public static String getCleanSourceCode(@NonNull File file) throws IOException {
		IScanner scanner = ToolFactory.createScanner(/*tokenizeComments*/false, 
				/*tokenizeWhiteSpace*/false, /*recordLineSeparator*/false, 
				/*sourceLevel*/"1.8", /*complianceLevel*/"1.8");
		scanner.setSource(getCharArray(file, "UTF-8"));
		StringBuilder builder = new StringBuilder();
		while (true) {
		    int tokenID = 0;
			try {
				tokenID = scanner.getNextToken();
			} catch (InvalidInputException e) {
				builder.append(UnicodeHelper.charsToHex(scanner.getCurrentTokenSource()));
			}
		    if (tokenID != ITerminalSymbols.TokenNameEOF) {
		    	builder.append(new String(scanner.getRawTokenSource()));
		    } else {
		    	break;
		    }
		}
		return builder.toString();
	}
	
	/**
	 * Gets lines of code of given java file (without count any comments).  
	 * 
	 * @param file  the java file
	 * @return  the line of code
	 * @throws IOException 
	 */
	public static int getLoC(@NonNull File file) {
		IScanner scanner = ToolFactory.createScanner(/*tokenizeComments*/false, 
				/*tokenizeWhiteSpace*/false, /*recordLineSeparator*/true, 
				/*sourceLevel*/"1.8", /*complianceLevel*/"1.8");
		Set<Integer> lines = new HashSet<>();
		try {
			scanner.setSource(getCharArray(file, "UTF-8"));
		} catch (IOException e1) {
			return 0;
		}
		while (true) {
		    int tokenID = 0;
			try {
				tokenID = scanner.getNextToken();
			} catch (InvalidInputException e) {
				// ignore the token that can not be recognized
			}
		    if (tokenID != ITerminalSymbols.TokenNameEOF) {
		    	lines.add(scanner.getLineNumber(scanner.getCurrentTokenStartPosition()));
		    	lines.add(scanner.getLineNumber(scanner.getCurrentTokenEndPosition()));
		    } else {
		    	break;
		    }
		}
		return lines.size();
	}
	
	/**
	 * Prepares the file by given source path. 
	 * 
	 * @param path  the file path
	 * @return  the file
	 */
	public static File getFile(@NonNull String path) {
		File file = new File(path);
		// if file exist, return the file
		if(file.exists()) {
			if(file.isFile()) {
				return file;
			}
			if(file.isDirectory()) {
				throw new IllegalArgumentException(path + "is not a file path");
			}
		}
		File parentDir = file.getParentFile();
		if(!parentDir.exists()) {
			parentDir.mkdirs();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			logger.error("Fail to create file {}, \n{}", file.getAbsolutePath(), e);
		}
		return file;
	}

	/**
	 * Prepares sub file under array of directories. 
	 * <p>
	 * If the size of files is n, then the front n-1 strings are the names of the sub directories, 
	 * and the last one string is the name of the file.
	 * </p>
	 * @param parentDir  the file of parent directory
	 * @param fileNames  names of directory and file names
	 * @return  the created file
	 * @throws IOException
	 */
	public static File getFile(@NonNull File parentDir, @NonNull String... fileNames) {
		File file = parentDir;
		if(fileNames.length == 1) {
			return prepareFile(file, fileNames[0]);
		} 
		if(fileNames.length > 1) {
			for(int i=0; i<fileNames.length-1; i++) {
				file = prepareDir(file, fileNames[i]);
			}
			file = prepareFile(file, fileNames[fileNames.length-1]);
		}
		return file;
	}
	
	/**
	 * Prepares the directory by given path. 
	 * 
	 * @param dirPath  the directory path
	 * @return  the directory
	 */
	public static File getDir(@NonNull String dirPath) {
		File file = new File(dirPath);
		// if file exist, return the file
		if(file.exists()) {
			if(file.isDirectory()) {
				return file;
			}
			if(file.isFile()) {
				throw new IllegalArgumentException(dirPath + "is not a directory path");
			}
		}
		file.mkdirs();
		return file;
 	}

	/**
	 * Prepares multiple levels of directories under the specified parent directory.
	 * 
	 * @param parentDir  the parent directory 
	 * @param dirNames  the list of directory names
	 * @return  the file reference of the created child directory
	 * @throws IOException
	 */
	public static File getDir(@NonNull File parentDir, @NonNull String... dirNames) {
		File subDir = parentDir; 
		for(String dirName : dirNames) {
			subDir = prepareDir(subDir, dirName);
		}
		return subDir;
	}
	
	/**
	 * Gets all sub files under the given parent directory. 
	 * 
	 * @param parentDir  the parent directory
	 * @param recursive  indicates if it searches in the parent directory recursively
	 * 
	 * @return  the list of sub files found
	 */
	public static List<File> listSubFiles(@NonNull File parentDir, boolean recursive) {
		if(!parentDir.exists()) {
			return new ArrayList<File>();
		}
		return new ArrayList<>(FileUtils.listFiles(parentDir, null, recursive));
	}
	
	/**
	 * Gets all files with given file name under parent directory. 
	 * 
	 * @param parentDir  the parent directory to search in
	 * @param fileName  the name of the file to be searched
	 * @param recursive  indicates if it searches in the parent directory recursively
	 * @return  the list of sub files found
	 */
	public static List<File> listSubFiles(@NonNull File parentDir, @NonNull String fileName, 
			boolean recursive) {
		return new ArrayList<>(FileUtils.listFiles(parentDir, new NameFileFilter(fileName), 
				recursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE));
	}

	/**
	 * Gets all files with given extensions under parent directory. 
	 * 
	 * @param parentDir  the parent directory
	 * @param extensions  the file extensions, it can be null
	 * @return  the list of files
	 */
	public static List<File> listSubFiles(@NonNull File parentDir, @NonNull String[] extensions, 
			boolean recursive) {
		return new ArrayList<File>(FileUtils.listFiles(parentDir, extensions, recursive));
	}
	
	/**
	 * Gets paths of all files with given extensions under parent directory.
	 * 
	 * @param parentDir  the parent directory
	 * @param extensions  the file extensions
	 * @return  the array of file paths
	 */
	public static List<String> listSubFilePaths(@NonNull File parentDir, @NonNull String[] extensions, 
			boolean recursive) {
		return FileNameHelper.filesToPathList(listSubFiles(parentDir, extensions, recursive));
	}

	/**
	 * Gets sub-directories within a given directory. 
	 * 
	 * @param parentDir  the parent directory to search in
	 * @param recursive  indicates if it searches in the parent directory recursively
	 * @return  the list of sub-directories found
	 */
	public static List<File> listSubDirs(@NonNull File parentDir, boolean recursive) {
		if(!parentDir.exists()) {
			return new ArrayList<File>();
		}
		List<File> subDirs = new ArrayList<File>();
		
		if(recursive) {
			listSubDirs(subDirs, parentDir);
		} else {
			if(parentDir.listFiles() == null) {
				return subDirs;
			} 
			for(File subFile : parentDir.listFiles()) {
				if(subFile.isDirectory()) {
					subDirs.add(subFile);
				}
			}
		}
		return subDirs;
	}
	
	/**
	 * Checks whether the parent directory contains the specified name of the directory.
	 * 
	 * @param parentDir  the parent directory
	 * @param dirName  the name of directory to be searched
	 * @param recursive  indicates if it searches in the parent directory recursively
	 * @return  {@code true} if the directory with given name is found; otherwise
	 */
	public static boolean containsDir(@NonNull File parentDir, @NonNull String dirName, 
			boolean recursive) {
		if(!isValidDir(parentDir)) {
			return false;
		}
		List<File> subDirs = listSubDirs(parentDir, recursive);
		if(subDirs == null || subDirs.size() == 0) {
			return false;
		}
		for(File subDir : subDirs) {
			if(dirName.equalsIgnoreCase(subDir.getName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks whether the parent directory contains the specified name of the file.
 	 * 
	 * @param parentDir  the parent directory
	 * @param fileName  the name of file to be searched
	 * @param recursive  indicates if it searches in the parent directory recursively
	 * 
	 * @return  {@code true} if the file with given name is found; otherwise
	 */
	public static boolean containsFile(@NonNull File parentDir, @NonNull String fileName, 
			boolean recursive) {
		if(!isValidDir(parentDir)) {
			return false;
		}
		List<File> subFiles = listSubFiles(parentDir, fileName, recursive);
		if(subFiles.size() == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks whether the parent directory contains the file with given extensions.
	 * 
	 * @param parentDir  the parent directory to search in
	 * @param extensions  the array of extensions
	 * @param recursive  indicates if it searches in the parent directory recursively
	 * 
	 * @return  {@code true} if the file with given extensions is found; otherwise
	 */
	public static boolean containsFile(@NonNull File parentDir, @NonNull String[] extensions, 
			boolean recursive) {
		if(extensions.length == 0) {
			throw new NullPointerException("The extensions must contain at lease one element");
		}
		if(!isValidDir(parentDir)) {
			return false;
		}
		List<File> subFiles = listSubFiles(parentDir, extensions, recursive);
		if(subFiles.size() == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the base file name from given file path. 
	 * 
	 * @param filePath  the file path 
	 * @return  the file base name
	 */
	public static String getFileBaseName(@NonNull String filePath) {
		return FilenameUtils.getBaseName(new File(filePath).getName());
	}
	
	/**
	 * Write line content into file quietly. 
	 * 
	 * @param file  the file to write
	 * @param line   the string to be written into the file
	 * @param append  {@code true} if append the content to the file, 
	 * {@code false} if write the string to a empty file
	 * @return  {@code true} if the write operation succeeds,
	 * and {@code false if the write operation} fails
	 */
	public static boolean writeLineQuietly(@NonNull File file, @NonNull String line, 
			boolean append) {
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(file, append))) {
			bw.write(line);
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (IOException e) {
			logger.error("Fail to write content into file {}. \n{}", file.getAbsolutePath(), e);
			return false;
		}
		return true;
	}
	
	/**
	 * Reads lines from file to set quietly.
	 * 
	 * @param file  the file to read
	 * @return  the set of lines
	 */
	public static Set<String> readLinesQuietly(@NonNull File file) {
		Set<String> lines = new HashSet<String>();
		if(!file.exists()) {
			return lines;
		}
		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null; 
			while((line = reader.readLine()) != null) {
				lines.add(line);
			}
			reader.close();
		} catch(IOException e) {
			logger.warn("Fail to read content from {}. \n{}", file.getAbsolutePath(), e);
		}
		return lines;
	}
	
	/**
	 * Cleans the list of files according the set of ignored file names.
	 * 
	 * @param files  the list of files
	 * @param fileNames  the name of the ignored files
	 * @return  the cleaned list of files
	 */
	public static List<File> removeByNames(@NonNull List<File> files, @NonNull Set<String> fileNames) {
		if(files.size() == 0 || fileNames.size() == 0) {
			return files;
		}
		List<File> cleanedFiles = new ArrayList<File>();
		for(File file : files) {
			if(!fileNames.contains(file.getName())) {
				cleanedFiles.add(file);
			} else {
				logger.info("{} is ignored ...", file.getName());
			}
		}
		return cleanedFiles;
	}
	
	/**
	 * Selected the list of files according the set of included file names.
	 * 
	 * @param files  the list of files
	 * @param fileNames  the name of the ignored files
	 * @return  the selected list of files
	 */
	public static List<File> selectByNames(@NonNull List<File> files, @NonNull Set<String> fileNames) {
		if(files.size() == 0 || fileNames.size() == 0) {
			return files;
		}
		List<File> selectedFiles = new ArrayList<File>();
		for(File file : files) {
			if(fileNames.contains(file.getName())) {
				selectedFiles.add(file);
				logger.info("{} is included ...", file.getName());
			} else {
				logger.info("{} is ignored ...", file.getName());
			}
		}
		return selectedFiles;
	}
	
	/**
	 * Gets path array of the unique files. 
	 * <p>
	 * The uniqueness check in this process is strong since only file hash is 
	 * used to determine the file uniqueness. 
	 * </p>
	 * 
	 * @param files  the list of files to validate the uniqueness
	 * @return  the path array
	 */
	public static List<File> uniqueByFileHash(@NonNull List<File> files) {
		Set<String> fileHash = new HashSet<>();
		List<File> uniqueFiles = new ArrayList<>();
		String hash = null;
		for(File file : files) {
			try {
				hash = HashHelper.getFileHash(file);
			} catch (IOException e) {
				logger.error("Fail to calculate hash from file {}, \n{}", 
						file.getAbsolutePath(), e);
			}
			if(hash != null) {
				if(!fileHash.contains(hash)) {
					uniqueFiles.add(file);
					fileHash.add(hash);
				}
			}
		}
		return uniqueFiles;
	}

	/**
	 * Gets path array of the unique files. 
	 * <p>
	 * The uniqueness check in this process is strong since before comparing the file hash, 
	 * the file is preprocessed by the removing the comments, blank lines and white spaces inside the file. 
	 * </p>
	 * 
	 * @param files  the list of files to validate the uniqueness
	 * @return  the path array
	 */
	public static List<File> uniqueByCleanFileHash(@NonNull List<File> files) {
		Set<String> fileHash = new HashSet<>();
		List<File> uniqueFiles = new ArrayList<>();
		String hash = null; 
		for(File file : files) {
			try {
				hash = HashHelper.getHash(FileHelper.getCleanSourceCode(file));
			} catch (IOException e) {
				logger.error("Fail to get clean source code from file {}, \n{}", 
						file.getAbsolutePath(), e);
			}
			if(hash != null) {
				if(!fileHash.contains(hash)) {
					uniqueFiles.add(file);
					fileHash.add(hash);
				}
			}
		}
		return uniqueFiles;
	}
	
	/**
	 * Gets the file distribution map by the name extension. 
	 * 
	 * @param sourceDir  the source directory
	 * @return  the file distribution map
	 */
	public static Map<String, List<File>> getFileMap(@NonNull File sourceDir) {
		Map<String, List<File>> fileMap = new HashMap<>();
		List<File> files = FileHelper.listSubFiles(sourceDir, true);
		for(File file : files) {
			String extension = StringUtils.lowerCase(FilenameUtils.getExtension(file.getName()));
			MapHelper.updateListMap(fileMap, extension, file);
		}
		return fileMap;
	}

	/**
	 * Prepares the directory under the specified parent directory.
	 * <p>
	 * If the {@code parentDir} does not exist, try to create this directory. 
	 * If the sub directory whose name is {@code childDirName} does not exist, 
	 * also try to create the sub directory. 
	 * If the sub directory exits, get the file reference.  
	 * </p>
	 * @param parentDir  the parent directory 
	 * @param dirName  the name of child directory
	 * @return  the file reference of the created child directory
	 */
	private static File prepareDir(@NonNull File parentDir, @NonNull String dirName) {
		if(!parentDir.exists()) {
			parentDir.mkdirs();
		}
		File subDir = new File(parentDir, dirName);
		if(!subDir.exists()) {
			subDir.mkdir();
		}
		return subDir;
	}

	/**
	 * Prepares the file under the specified parent directory.
	 * <p>
	 * If the {@code parentDir} does not exist, try to create this directory. 
	 * If the sub file whose name is {@code childFileName} does not exist, 
	 * also try to create the sub file. 
	 * If this sub file exits, get the file reference.  
	 * </p>
	 * @param parentDir  the parent directory 
	 * @param fileName  the name of child file
	 * @return  the file reference of the created child file
	 * @throws IOException 
	 */
	private static File prepareFile(@NonNull File parentDir, @NonNull String fileName) {
		if(!parentDir.exists()) {
			parentDir.mkdirs();
		}
		File subFile = new File(parentDir, fileName);
		if(!subFile.exists()) {
			try {
				subFile.createNewFile();
			} catch (IOException e) {
				logger.error("Fail to create file {}, \n{}", subFile.getAbsolutePath(), e);
			}
		}
		return subFile;
	}

	/**
	 * Finds sub-directories within a given directory. 
	 * 
	 * @param subDirs  the list of sub-directories found
	 * @param parentDir  the directory to search in
	 */
	private static void listSubDirs(@NonNull List<File> subDirs, @NonNull File parentDir) {
		File[] foundDirs = parentDir.listFiles((FileFilter)DirectoryFileFilter.INSTANCE);
		if(foundDirs != null) {
			for(File subDir : foundDirs) {
				subDirs.add(subDir);
				listSubDirs(subDirs, subDir);
			}
		}
	}
}
