package bloom;


import java.util.List;

public class SimpleHashFunctionFactory<TElem> extends HashFunctionFactory<TElem> {
    private final List<Integer> weights;
    private final List<Integer> offsets;
    private final List<Integer> modulus;
    private int iter;

    public static class UNSAFE_OverflowHashFunction<TElem> implements HashFunction<TElem> {

        private final int weight;
        private final int offset;
        private final int modulus;

        UNSAFE_OverflowHashFunction(int weight, int offset, int modulus) {
            this.weight = weight;
            this.offset = offset;
            this.modulus = modulus;
        }
        @Override
        public int hash(TElem val) {
            return ((this.weight * val.hashCode()) % this.offset + this.offset % this.weight) % this.modulus;
        }

        @Override
        public String toString() {
            return String.format("(%d * x MOD %d + %d MOD %d) MOD %d", weight, offset, offset, weight, modulus);
        }
    }

    SimpleHashFunctionFactory(int k) {
        super(k);
        this.weights = findLargePrimesGreaterThan(10000, k);
        this.offsets = findLargePrimesGreaterThan(100, k);
        this.modulus = findLargePrimesGreaterThan(1000, k);
        this.iter = 0;
    }
    @Override
    HashFunction<TElem> nextHashFunction() {
        int weight = this.weights.get(this.iter % k);
        int offset = this.offsets.get(this.iter % k);
        int modulus = this.modulus.get(this.iter % k);
        return new UNSAFE_OverflowHashFunction(weight, offset, modulus);
    }
}
