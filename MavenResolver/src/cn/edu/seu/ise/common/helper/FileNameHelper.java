package cn.edu.seu.ise.common.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import lombok.NonNull;

/**
 * File name helper class. 
 * 
 * @author Dong Qiu
 *
 */
public class FileNameHelper {
	
	/**
	 * Adds files with different name within the original file list. 
	 * 
	 * @param originalFiles  the list of original files
	 * @param addedFiles  the list of files to be added
	 * @return  the list of files after addition
	 */
	public static List<File> addFileWithDiffName(@NonNull List<File> originalFiles, 
			@NonNull List<File> addedFiles) {
		if(addedFiles.isEmpty()) {
			return originalFiles;
		}
		Set<String> uniqueNames = new HashSet<>();
		for(File originalFile : originalFiles) {
			uniqueNames.add(originalFile.getName());
		}
		for(File addedFile : addedFiles) {
			if(uniqueNames.contains(addedFile.getName())) {
				continue;
			}
			originalFiles.add(addedFile);
		}
		return originalFiles;
	}

	/**
	 * Converts list of files to list of paths. 
	 * 
	 * @param files  the list of files
	 * @return  the list of paths
	 */
	public static List<String> filesToPathList(@NonNull Collection<File> files) {
		List<String> sourcepaths = new ArrayList<>();
		if(files.size() == 0) {
			return sourcepaths;
		}
		for(File file : files) {
			sourcepaths.add(file.getAbsolutePath());
		}
		return sourcepaths;
	}

	/**
	 * Converts list of files to array of paths. 
	 * 
	 * @param files  the list of files
	 * @return  the array of paths
	 */
	public static String[] filesToPathArray(@NonNull Collection<File> files) {
		List<String> sourcepaths = filesToPathList(files);
		return sourcepaths.toArray(new String[sourcepaths.size()]);
	}
	
	/**
	 * Gets the base name from the file path. 
	 * 
	 * @param filePath  the file path
	 * @return  the file base name
	 */
	public static String getBase(String filePath) {
		File file = new File(filePath);
		return FilenameUtils.getBaseName(file.getName());
	}
	
}
