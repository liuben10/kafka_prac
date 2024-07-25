package bloom;

public class BloomFilter<TElem, THashFunctionFactory extends HashFunctionFactory<TElem>> {
    public static class BitArray {
        public boolean[] bits;
        public int size;
        BitArray(int size) {
            bits = new boolean[size];
            this.size = size;
        }

        public BitArray and(BitArray b) {
            int newsize = Math.max(b.size, this.size);
            BitArray nb = new BitArray(newsize);
            for(int i=0; i<newsize; i++) {
                if (i < this.bits.length && i < b.bits.length) {
                    nb.bits[i] = this.bits[i] && b.bits[i];
                } else {
                    nb.bits[i] = false;
                }
            }
            return nb;
        }

        public BitArray or(BitArray b) {
            int newsize = Math.max(b.size, this.size);
            BitArray nb = new BitArray(newsize);
            for(int i=0; i<newsize; i++) {
                if (i < this.bits.length && i < b.bits.length) {
                    nb.bits[i] = this.bits[i] || b.bits[i];
                } else if (i < this.bits.length) {
                    nb.bits[i] = this.bits[i];
                } else {
                    nb.bits[i] = b.bits[i];
                }
            }
            return nb;
        }

        public BitArray xor(BitArray b) {
            int newsize = Math.max(b.size, this.size);
            BitArray nb = new BitArray(newsize);
            for(int i=0; i<newsize; i++) {
                if (i < this.bits.length && i < b.bits.length) {
                    nb.bits[i] = this.bits[i] ^ b.bits[i];
                } else if (i < this.bits.length) {
                    nb.bits[i] = this.bits[i];
                } else {
                    nb.bits[i] = b.bits[i];
                }
            }
            return nb;
        }

        public void writeNum(int n) {
            int i = 0;
            while(n > 0) {
                this.bits[i] = (n | 1) == 1;
                i += 1;
                n >>= 1;
            }
        }

        public void write(int i, boolean val) {
            this.bits[i] = val;
        }

        public boolean isset(int i) {
            return this.bits[i];
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bits.length; i++) {
                sb.append(bits[i] ? "1" : "0");
            }
            return sb.toString();
        }
    }

    BitArray bits;
    THashFunctionFactory factory;
    private final int size;
    private final int k;

    BloomFilter(int m, int k, THashFunctionFactory hashFunctionFactory) {
        this.bits = new BitArray(m);
        this.size = m;
        this.k = k;
        this.factory = hashFunctionFactory;
    }

    void add(TElem elem) {
        int i = 0;
        while(i < k) {
            HashFunctionFactory.HashFunction<TElem> hf = this.factory.nextHashFunction();
            System.out.println(hf);
            int hashval = hf.hash(elem);
            int pos = hashval % this.size;
            this.bits.write(pos, true);
            i += 1;
        }
    }

    boolean posContains(TElem elem) {
        int i = 0;
        boolean contained = true;
        while(i < k) {
            HashFunctionFactory.HashFunction<TElem> hf = this.factory.nextHashFunction();
            int hashval = hf.hash(elem);
            int pos = hashval % this.size;
            contained = contained && this.bits.isset(pos);
            i += 1;
        }
        return contained;
    }

    public static void main(String[] args) {
        SimpleHashFunctionFactory<String> shfact = new SimpleHashFunctionFactory<>(10);

        BloomFilter<String, SimpleHashFunctionFactory<String>> bf = new BloomFilter<>(1000, 10, shfact);
        bf.add("Bob");
        System.out.println(String.format("Is Bob contained: %s", bf.posContains("Bob")));
        System.out.println(String.format("Is Sally contained: %s", bf.posContains("Sally")));
    }
}
