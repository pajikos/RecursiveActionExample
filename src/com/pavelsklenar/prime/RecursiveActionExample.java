package com.pavelsklenar.prime;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

/**
 * Class is able to get prime numbers within defined limit, using user input
 * parameters and possible parallelism
 * 
 * @author pavel.sklenar
 *
 */
public class RecursiveActionExample {

	private static final Logger LOG = Logger.getLogger(RecursiveActionExample.class.getName());

	public static void main(String[] args) {
		if (args.length < 2) {
			exit("At least type of processor and limit must be specified.");
		} else {
			LOG.info("Params " + Arrays.toString(args));
		}
		int inputLimit = getArgAsInt(args, 0);

		PrimeNumberProcessor processor = null;

		if ("s".equals(args[1])) {
			processor = new SimpleProcessor();
		} else if ("m".equals(args[1])) {
			switch (args.length) {
			case 2:
				processor = new ParallelPrimeNumberProcessor(1000);
				break;
			case 3:
				processor = new ParallelPrimeNumberProcessor(getArgAsInt(args, 2));
				break;
			case 4:
				processor = new ParallelPrimeNumberProcessor(getArgAsInt(args, 2), getArgAsInt(args, 3));
				break;
			}
		} else {
			exit("Unsupported processor type " + args[1]);
		}

		long startTime = System.nanoTime();
		Set<Integer> result = processor.process(inputLimit);
		long endTime = System.nanoTime();
		LOG.info("" + ((endTime - startTime) / 1000_000f) + " millis.");
		LOG.info(String.format("Found %s prime numbers with limit %s in %.3f seconds.", result.size(), inputLimit,
				(endTime - startTime) / 1000_000_000f));

	}

	/**
	 * Exit this application with a goodbye message
	 * 
	 * @param message
	 */
	private static void exit(String message) {
		LOG.info(message);
		System.exit(1);
	}

	/**
	 * Get a defined arg as {@link String} if present, otherwise return null
	 * 
	 * @param args
	 * @param index
	 * @return
	 */
	private static String getArg(String[] args, int index) {
		if (args != null && (args.length - 1) >= index) {
			return args[index];
		}
		return null;
	}

	/**
	 * Get a defined arg as int, if not a number or missing, throw a
	 * {@link NumberFormatException}
	 * 
	 * @param args
	 * @param index
	 * @return
	 */
	private static int getArgAsInt(String[] args, int index) {
		try {
			return Integer.parseInt(getArg(args, index));
		} catch (NumberFormatException e) {
			exit("Argument " + args[index] + " at index " + index + " must be an integer, exception " + e.getMessage());
			throw e;
		}
	}

}

interface PrimeNumberProcessor {
	/**
	 * Get all prime numbers within limit
	 * 
	 * @param limit
	 * @return
	 */
	Set<Integer> process(int limit);
}

/**
 * Use parallelism to get all prime number within limit
 * 
 * @author pavel.sklenar
 *
 */
class ParallelPrimeNumberProcessor implements PrimeNumberProcessor {
	private int threshold;
	private ForkJoinPool fjPool;

	/**
	 * Create a new instance of {@code ParallelProcessor} with
	 * 
	 * @param threshold
	 *            count of numbers to be processed by one task
	 * @param parallelism
	 *            number of threads to be used to process a defined task
	 */
	public ParallelPrimeNumberProcessor(int threshold, int parallelism) {
		this(threshold);
		fjPool = new ForkJoinPool(parallelism);

	}

	/**
	 * Create a new instance of {@code ParallelProcessor} with parallelism equal
	 * to {@link java.lang.Runtime#availableProcessors}
	 * 
	 * @param threshold
	 *            count of numbers to be processed by one task
	 */
	public ParallelPrimeNumberProcessor(int threshold) {
		this.threshold = threshold;
		fjPool = new ForkJoinPool();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pavelsklenar.prime.PrimeNumberProcessor#process(int)
	 */
	@Override
	public Set<Integer> process(int limit) {
		Set<Integer> data = new ConcurrentSkipListSet<Integer>();
		// Set<Integer> data = ConcurrentHashMap.newKeySet();

		PrimeRecursiveAction action = new PrimeRecursiveAction(data, 0, limit, threshold);
		fjPool.invoke(action);
		// return new TreeSet<Integer>(data);
		return data;
	}
}

/**
 * Simple one thread processor to get prime numbers within defined limit
 * 
 * @author pavel.sklenar
 *
 */
class SimpleProcessor implements PrimeNumberProcessor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pavelsklenar.prime.PrimeNumberProcessor#process(int)
	 */
	@Override
	public Set<Integer> process(int limit) {
		Set<Integer> data = new LinkedHashSet<Integer>();
		for (int i = 0; i < limit; i++) {
			if (PrimeUtil.isPrime(i)) {
				data.add(i);
			}
		}
		return data;
	}
}