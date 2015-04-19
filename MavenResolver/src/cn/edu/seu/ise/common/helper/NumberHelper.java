package cn.edu.seu.ise.common.helper;

import java.math.BigDecimal;

/**
 * A helper class used to handle number related calculations. 
 * 
 * @author Dong Qiu
 *
 */
public class NumberHelper {
	
	/**
	 * Rounds a double value to the given length. 
	 * 
	 * @param doubleValue  the double value
	 * @param length  the length of the double value
	 * @return  the rounded value
	 */
	public static double round(double doubleValue, int length) {
		BigDecimal bigDecimal = new BigDecimal(doubleValue);
		return bigDecimal.setScale(length, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
