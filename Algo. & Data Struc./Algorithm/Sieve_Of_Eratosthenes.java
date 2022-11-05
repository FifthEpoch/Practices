import java.util.Arrays;

public class Sieve_Of_Eratosthenes {
	
	public static boolean[] isPrime;
	
	public static int[] sieveOfEra(int n) {
		
		int[] nums;
		int primeCounter = 0;
		
		if (n < 2) {
			return nums = new int[0];
		} else {
			
			// Initialize arrays
			
			nums     = new int[n + 1];
			for (int i = 0; i < nums.length; i++) {
				nums[i] = i;
			}
			
			isPrime    = new boolean[n + 1];
			Arrays.fill(isPrime, Boolean.TRUE);
			isPrime[0] = false;
			isPrime[1] = false;
		}

		for (int i = 2; i < nums.length; i++) {
			
			if (isPrime(i)) {
				
				crossOutMutiples(i, n);
				primeCounter++;
				
			} else {
				isPrime[i] = false;
			}
		}
		
		int[] primes = new int[primeCounter];
		int index 	 = 0;
		for (int i = 0; i < nums.length; i++) {
			if (isPrime[i]) {
				primes[index++] = nums[i];
			}
		}
		
		return primes;
	}
	
	public static void crossOutMutiples(int i, int n) {
		for (int j = i + i; j <= n; j+=i) {
			isPrime[j] = false;
		}
	}
	
	public static boolean isPrime(int x) {
		
		if (!isPrime[x]) {
			return false;
		}
		
		for (int i = x - 1; i > 1; i--) {
			if (x % i == 0) {
				return false;
			}
		}
		return true;
	}
	
	public static void main (String[] args) {
		System.out.println(Arrays.toString(sieveOfEra(34))); // returns every prime nums up to 34
		System.out.println(Arrays.toString(sieveOfEra(1)));	 // returns an empty array
	}
	
}
