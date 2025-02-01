package primefinder;

import java.util.LinkedList;
import java.util.List;

public class PrimeFinderThread extends Thread{


	int a,b;
    private final Control controller;
	private final List<Integer> primes;
    private final Object monitor;

	public PrimeFinderThread(int a, int b, Object monitor, Control controller) {
		super();
        this.primes = new LinkedList<>();
		this.a = a;
		this.b = b;
        this.monitor = monitor;
        this.controller = controller;
	}

        @Override
        public void run() {
            for (int i = a; i < b; i++) {
                synchronized (monitor) {
                    while (controller.isPaused()) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                        }
                    }
                }

                if (isPrime(i)) {
                    primes.add(i);
                    System.out.println(i);
                }
            }
        }

        boolean isPrime(int n) {
	    boolean ans;
            if (n > 2) {
                ans = n%2 != 0;
                for(int i = 3;ans && i*i <= n; i+=2 ) {
                    ans = n % i != 0;
                }
            } else {
                ans = n == 2;
            }
	    return ans;
	}

	public List<Integer> getPrimes() {
		return primes;
	}

}
