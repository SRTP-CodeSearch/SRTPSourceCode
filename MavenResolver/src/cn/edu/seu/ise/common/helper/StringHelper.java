package cn.edu.seu.ise.common.helper;

import lombok.NonNull;

import com.google.common.base.Joiner;

/**
 * A helper class used to improve the use of class {@link String}.
 *
 * @author Dong Qiu
 *
 */
public class StringHelper {
	
	/** Empty String */
	public static final String EMPTY = "";
	
	/**
	 * Checks whether the string is an empty string. 
	 * 
	 * @param name  the string
	 * @return  {@code true} if the string is empty; 
	 * {@code false} otherwise
	 */
	public static boolean isEmpty(CharSequence cs) {
		return cs == null || EMPTY.equals(cs);
	}
	
	/**
     * <p>Checks if a CharSequence is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace; 
     * {@code false} otherwise
     */
	public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
	
	/**
     * <p>Checks if a CharSequence is not whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace; 
     * {@code false} otherwise
     */
	public static boolean isNotBlank(CharSequence cs) {
		return !isBlank(cs);
	}
	
	/**
	 * Capitalizes the first letter and lowercase the rest letters. 
	 * 
	 * @param name  the string to process
	 * @return  the processed string
	 */
	public static String capFirstLowerRest(String name) {
		int length = name.length();
		if(name == null || length == 0) {
			return name;
		}
		StringBuilder builder = new StringBuilder(length);
		builder.append(name.substring(0, 1).toUpperCase());
		builder.append(name.substring(1).toLowerCase());
		return builder.toString();
	}
	
	/**
	 * Concatenates array of objects by given separator. 
	 *  
	 * @param separator  the separator
	 * @param first  the first object to be concatenated
	 * @param second  the second object to be concatenated
	 * @param rest  the rest objects to be concatenated
	 * 
	 * @return  the concatenated string
	 */
	public static String concat(@NonNull String separator, @NonNull Object first, 
			@NonNull Object second, Object... rest) {
		Joiner joiner = Joiner.on(separator).skipNulls();
		return joiner.join(first, second, rest);
	}
	
	/**
	 * Concatenates iterable of objects by given separator. 
	 * 
	 * @param separator  the separator
	 * @param iterable  the iterable objects
	 * 
	 * @return  the concatenated string
	 */
	public static String concat(@NonNull String separator, @NonNull Iterable<?> iterable) {
		Joiner joiner = Joiner.on(separator).skipNulls();
		return joiner.join(iterable);
	}
}
