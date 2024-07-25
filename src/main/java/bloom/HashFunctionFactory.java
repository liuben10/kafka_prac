package bloom;

import java.util.ArrayList;
import java.util.List;

public abstract class HashFunctionFactory<T> {
    public interface HashFunction<T> {
        int hash(T val);
    }

    int k;
    HashFunctionFactory(int k) {
        this.k = k;
    }

    boolean isPrime(int n) {
        for(int i=2; i<n/2; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
    List<Integer> findLargePrimesGreaterThan(int bottom, int k) {
        List<Integer> primes = new ArrayList<>();
        while(k > 0) {
            if (isPrime(bottom)) {
                primes.add(bottom);
                k -= 1;
            }
            bottom += 1;
        }
        return primes;
    }
    abstract HashFunction<T> nextHashFunction();
}
