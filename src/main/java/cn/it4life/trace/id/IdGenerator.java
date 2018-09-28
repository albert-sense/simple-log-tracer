package cn.it4life.trace.id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @ClassName: IdGenerator
 * @Description: 
 * @author it4life
 *
 */
public class IdGenerator {
	
	private static Logger logger = LoggerFactory.getLogger(IdGenerator.class);

	private static long sequence = 0L;
	private static long twepoch = 1417422396093L;
	private static long workerIdBits = 5L;
	private static long datacenterIdBits = 5L;
	private static long maxWorkerId = -1L ^ (-1L << workerIdBits);
	private static long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
	private static long sequenceBits = 12L;
	private static long workerIdShift = sequenceBits;
	private static long datacenterIdShift = sequenceBits + workerIdBits;
	private static long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
	private static long sequenceMask = -1L ^ (-1L << sequenceBits);
	private static long lastTimestamp = -1L;

	public synchronized static long nextId(long workerId, long datacenterId) {
		if (workerId > maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(
					String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
		}
		if (datacenterId > maxDatacenterId || datacenterId < 0) {
			throw new IllegalArgumentException(
					String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
		}
		long timestamp = timeGen();
		if (timestamp < lastTimestamp) {
			logger.error("当前时间不等晚于上一次的时间点.");
			throw new RuntimeException(String.format(
					"Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
		}
		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & sequenceMask;
			if (sequence == 0) {
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else {
			sequence = 0L;
		}
		lastTimestamp = timestamp;
		return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift)
				| (workerId << workerIdShift) | sequence;
	}

	private static long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	protected static long timeGen() {
		return System.currentTimeMillis();
	}

}