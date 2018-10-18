package test.andre.batch.item;

import java.util.List;

import javax.batch.api.chunk.AbstractItemWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogItemWriter extends AbstractItemWriter {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogItemWriter.class);

	@Override
	public void writeItems(List<Object> items) throws Exception {
		LOGGER.info("ItemWriter: {}", items);
	}

}