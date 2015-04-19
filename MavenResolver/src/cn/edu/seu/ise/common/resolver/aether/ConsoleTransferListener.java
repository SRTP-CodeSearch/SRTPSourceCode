package cn.edu.seu.ise.common.resolver.aether;

import static org.eclipse.aether.transfer.TransferEvent.RequestType.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.MetadataNotFoundException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transfer listener that logs uploads/downloads to the console.
 * 
 * @author Dong Qiu
 *
 */
public class ConsoleTransferListener extends AbstractTransferListener {
	
	/** Logger */
	private static Logger logger = LoggerFactory.getLogger(ConsoleTransferListener.class.getName());

	@Override
	public void transferSucceeded(TransferEvent event) {
		TransferResource resource = event.getResource();
		long contentLength = event.getTransferredBytes();
		if (contentLength >= 0) {
			String type = (event.getRequestType() == PUT ? "Uploaded" : "Downloaded");
			String len = contentLength >= 1024 ? toKB(contentLength) + " KB"
					: contentLength + " B";
	
			String throughput = "";
			long duration = System.currentTimeMillis() - resource.getTransferStartTime();
			if (duration > 0) {
				long bytes = contentLength - resource.getResumeOffset();
				DecimalFormat format = new DecimalFormat("0.0",
						new DecimalFormatSymbols(Locale.ENGLISH));
				double kbPerSec = (bytes / 1024.0) / (duration / 1000.0);
				throughput = " at " + format.format(kbPerSec) + " KB/sec";
			}
	
			logger.info("{}: {} from {} ({},{})", type, resource.getResourceName(), 
					resource.getRepositoryUrl(), len, throughput);
		}
	}

	@Override
	public void transferFailed(TransferEvent event) {
		if (event.getException() instanceof MetadataNotFoundException) {
			return;
		}
		logger.error(event.getException().getMessage());
	}

	@Override
	public void transferCorrupted(TransferEvent event) {
		logger.error(event.getException().getMessage());
	}

	/**
	 * Transforms the byte to kiloByte. 
	 * 
	 * @param bytes  the byte 
	 * @return  the kiloByte
	 */
	protected long toKB(long bytes) {
		return (bytes + 1023) / 1024;
	}
}
