package com.pavelsklenar.prime;

/**
 * Utility class related to some prime number operations
 * 
 * @author pavel.sklenar
 * 
 */
public class PrimeNumberUtil {

	/**
	 * Check if an input integer number is prime or not
	 * 
	 * @see <a
	 *      href="http://java67.blogspot.cz/2014/01/how-to-check-if-given-number-is-prime.html">How
	 *      to Check if Given Number is Prime in Java</a>
	 * @param num
	 * @return
	 */
	public static boolean isPrime(int num) {
		if (num == 2 || num == 3) {
			return true;
		}
		if (num % 2 == 0 || num % 3 == 0) {
			return false;
		}
		for (int i = 3; i <= Math.sqrt(num); i += 2) {
			if (num % i == 0) {
				return false;
			}
		}
		return true;
	}

}
