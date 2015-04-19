package cn.edu.seu.ise.common.config;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log4j configuration. 
 * 
 * @author Dong Qiu
 *
 */
public class Log4jConfig {
	
	/** Logger */
	private static Logger logger = LoggerFactory.getLogger(Log4jConfig.class.getName());
	
	/** Default path of the log4j config file */
	private final static String DEFAULT_CONFIG = "config/log4j.properties"; 
	
	/**
	 * Initial the log4j config.
	 */
    public static void initLog4j() {
        try {
            Properties props = new Properties();
            props.load(Log4jConfig.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG));
            PropertyConfigurator.configure(props);
        } catch (Exception e) {
        	logger.error("Fail to load the log4j configuration. \n{}", e);
        }
     }
}
