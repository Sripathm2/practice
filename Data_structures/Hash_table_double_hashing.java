package Data_structures;
import java.util.Objects;

// Hash table using open addressing with double hashing:
//   position = (H1(key) + x * alpha) mod capacity,  alpha = H2(key) (forced nonzero).
// Deletion is lazy: a removed slot is marked with a tombstone so probe chains
// stay intact for other keys; tombstones are reusable on insert and purged on resize.
// Null keys and null values are not permitted. Capacity is kept prime.
public class Hash_table_double_hashing<K, V> {
    private static class Entry<K, V> {
        final K key;
        V value;
        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Object[] table;        // each slot: null (empty), TOMBSTONE (deleted), or Entry<K,V>
    private int size;              // live mappings only (excludes tombstones)
    private int capacity;
    private int P;

    private static final int DEFAULT_CAPACITY = 11;   // prime
    private static final double MAX_LOAD_FACTOR = 0.5;
    private static final Object TOMBSTONE = new Object();

    // Construct an empty table with the default capacity.
    public Hash_table_double_hashing() {
        this(DEFAULT_CAPACITY);
    }

    // Construct an empty table with capacity >= the given value (rounded up to a prime).
    // Throw IllegalArgumentException if initialCapacity < 1.
    public Hash_table_double_hashing(int initialCapacity) {
        if(initialCapacity < 1){
            throw new IllegalArgumentException();
        }
        this.capacity = initialCapacity;
        this.size = 0;
        this.P = 0;
        this.recomputeP();
        this.table = new Object[this.capacity];
        for(int index = 0; index < this.capacity; index ++){
            this.table[index] = null;
        }
    }

    private int gethash1(K key){
        int hashindex = (key.hashCode() & 0x7fffffff) % this.capacity;
        return hashindex;
    }

    private int gethash2(K key){
        int alpha = P - ((key.hashCode() & 0x7fffffff) % P);
        return alpha;
    }

    private int get_postition(K key, int x){
        long position = ((long) gethash1(key) + (long) x * gethash2(key)) % this.capacity;
        return (int) ((position + this.capacity) % this.capacity);
    }


    // Call this immediately after capacity is set (constructor AND resize).
    private void recomputeP(){
        int p = this.capacity - 1;
        while(p > 1 && !isPrime(p)) p--;
        this.P = (p > 1) ? p : 1;   // tiny tables fall back to step=1 (linear probing, still coprime)
    }

    private static boolean isPrime(int n){
        if(n < 2) return false;
        if(n < 4) return true;            // 2, 3
        if(n % 2 == 0) return false;
        for(int i = 3; (long) i * i <= n; i += 2){
            if(n % i == 0) return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private void resize(){
        
        Object[] oldtable = this.table;
        this.capacity *= 2;
        this.size = 0;
        this.recomputeP();
        this.table = new Object[this.capacity];
        for(int index = 0; index < this.capacity; index ++){
            this.table[index] = null;
        }

        for(int index = 0; index < oldtable.length; index ++){
            if(oldtable[index] != null && !oldtable[index].equals(TOMBSTONE)){
                Entry<K,V> temp = (Entry<K,V>)oldtable[index];
                this.put(temp.key, temp.value);
            }
        }
    }


    // Insert or update the mapping for key.
    // Return the previous value mapped to key, or null if key was absent.
    // Throw NullPointerException if key or value is null.
    @SuppressWarnings("unchecked")
    public V put(K key, V value) {
        if(key == null || value == null){
            throw new NullPointerException();
        }

        if(((this.size*1.0)/(this.capacity*1.0)) >= MAX_LOAD_FACTOR){
            this.resize();
        }

        int x = 0;
        int tombstone_position = -1;
        int current_index = -1;

        do{
            current_index = this.get_postition(key, x);
            if(this.table[current_index] == null){
                this.table[current_index] = new Entry<K,V>(key, value);
                if(tombstone_position != -1){
                    this.table[tombstone_position] = this.table[current_index];
                    this.table[current_index] = TOMBSTONE;
                }
                this.size += 1;
                return null;
            } else if(this.table[current_index].equals(TOMBSTONE)){
                tombstone_position = current_index;
            } else if(((Entry<K,V>)this.table[current_index]).key.equals(key)){
                Entry<K,V> temp = (Entry<K,V>)this.table[current_index];
                V returnV = temp.value;
                temp.value = value;
                if(tombstone_position != -1){
                    this.table[tombstone_position] = this.table[current_index];
                    this.table[current_index] = TOMBSTONE;
                }
                return returnV;
            }
            x += 1;
        }while(true);
    }

    // Return the value mapped to key, or null if key is absent.
    // Throw NullPointerException if key is null.
    public V get(K key) {
        if(key == null){
            throw new NullPointerException();
        }
        int x = 0;
        int tombstone_position = -1;
        int current_index = -1;

        do{
            current_index = this.get_postition(key, x);
            if(this.table[current_index] == null){
                return null;
            } else if(this.table[current_index].equals(TOMBSTONE)){
                tombstone_position = current_index;
            } else if(((Entry<K,V>)this.table[current_index]).key.equals(key)){
                Entry<K,V> temp = (Entry<K,V>)this.table[current_index];
                V returnV = temp.value;
                if(tombstone_position != -1){
                    this.table[tombstone_position] = this.table[current_index];
                    this.table[current_index] = TOMBSTONE;
                }
                return returnV;
            }
            x += 1;
        }while(true);
    }

    // Return true if key has a mapping.
    // Throw NullPointerException if key is null.
    public boolean containsKey(K key) {
        if(key == null){
            throw new NullPointerException();
        }
        int x = 0;
        int tombstone_position = -1;
        int current_index = -1;

        do{
            current_index = this.get_postition(key, x);
            if(this.table[current_index] == null){
                return false;
            } else if(this.table[current_index].equals(TOMBSTONE)){
                tombstone_position = current_index;
            } else if(((Entry<K,V>)this.table[current_index]).key.equals(key)){
                Entry<K,V> temp = (Entry<K,V>)this.table[current_index];
                V returnV = temp.value;
                if(tombstone_position != -1){
                    this.table[tombstone_position] = this.table[current_index];
                    this.table[current_index] = TOMBSTONE;
                }
                return true;
            }
            x += 1;
        }while(true);
    }

    // Remove the mapping for key.
    // Return the removed value, or null if key was absent.
    // Throw NullPointerException if key is null.
    public V remove(K key) {
        if(key == null){
            throw new NullPointerException();
        }
        if(!this.containsKey(key)){
            return null;
        }
        int x = 0;
        int current_index = -1;

        do{
            current_index = this.get_postition(key, x);
            if(((Entry<K,V>)this.table[current_index]).key.equals(key)){
                Entry<K,V> temp = (Entry<K,V>)this.table[current_index];
                V returnV = temp.value;
                this.table[current_index] = TOMBSTONE;
                this.size -= 1;
                return returnV;
            }
            x += 1;
        }while(true);
    }

    // Return number of live mappings.
    public int size() {
        return this.size;
    }

    // Return true if empty.
    public boolean isEmpty() {
        return this.size == 0;
    }

    // Return "{k1=v1, k2=v2, ...}" over all live mappings, order unspecified.
    // Empty table returns "{}".
    @Override
    public String toString() {
        String output = "{";
        for(int index = 0; index < this.table.length; index++){
            if(this.table[index] != null){
                Entry<K, V> temp = (Entry<K,V>) this.table[index];
                output += temp.key.toString() + "=" + temp.value.toString();
                output += ", ";
            }
        }
        if(this.size > 0){
            output = output.substring(0, output.length()-2);
        }
        output += "}";
        return output;
    }
}

class Hash_table_double_hashing_Main {
    private static int passed = 0;
    private static int failed = 0;

    private static void checkEquals(String name, Object expected, Object actual) {
        if (Objects.equals(expected, actual)) {
            passed++;
            System.out.println("PASS: " + name);
        } else {
            failed++;
            System.out.println("FAIL: " + name + " — expected <" + expected + ">, got <" + actual + ">");
        }
    }

    private static void checkTrue(String name, boolean cond) {
        if (cond) { passed++; System.out.println("PASS: " + name); }
        else      { failed++; System.out.println("FAIL: " + name); }
    }

    private static void checkThrows(String name, Class<? extends Throwable> expected, Runnable r) {
        try {
            r.run();
            failed++;
            System.out.println("FAIL: " + name + " — expected " + expected.getSimpleName() + ", none thrown");
        } catch (Throwable t) {
            if (expected.isInstance(t)) {
                passed++;
                System.out.println("PASS: " + name);
            } else {
                failed++;
                System.out.println("FAIL: " + name + " — expected " + expected.getSimpleName()
                        + ", got " + t.getClass().getSimpleName());
            }
        }
    }

    public static void main(String[] args) {
        // --- Empty ---
        Hash_table_double_hashing<String, Integer> h = new Hash_table_double_hashing<>();
        checkEquals("empty size",        0,     h.size());
        checkTrue ("empty isEmpty",             h.isEmpty());
        checkEquals("empty get",         null,  h.get("a"));
        checkEquals("empty containsKey", false, h.containsKey("a"));
        checkEquals("empty remove",      null,  h.remove("a"));
        checkEquals("empty toString",    "{}",  h.toString());

        // --- Put new returns null, get back ---
        checkEquals("put a new", null, h.put("a", 1));
        checkEquals("put b new", null, h.put("b", 2));
        checkEquals("put c new", null, h.put("c", 3));
        checkEquals("size 3",    3,    h.size());
        checkEquals("get a",     1,    h.get("a"));
        checkEquals("get b",     2,    h.get("b"));
        checkEquals("get c",     3,    h.get("c"));

        // --- Update existing ---
        checkEquals("put a update returns old", 1, h.put("a", 100));
        checkEquals("get a updated",            100, h.get("a"));
        checkEquals("size after update",        3, h.size());

        // --- containsKey / remove ---
        checkTrue ("contains a",  h.containsKey("a"));
        checkEquals("contains z", false, h.containsKey("z"));
        checkEquals("remove b",   2,    h.remove("b"));
        checkEquals("get b gone", null, h.get("b"));
        checkEquals("size 2",     2,    h.size());
        checkEquals("remove b again", null, h.remove("b"));

        // --- Null safety ---
        final Hash_table_double_hashing<String, Integer> hn = new Hash_table_double_hashing<>();
        checkThrows("put null key",   NullPointerException.class, () -> hn.put(null, 1));
        checkThrows("put null value", NullPointerException.class, () -> hn.put("a", null));
        checkThrows("get null",       NullPointerException.class, () -> hn.get(null));
        checkThrows("contains null",  NullPointerException.class, () -> hn.containsKey(null));
        checkThrows("remove null",    NullPointerException.class, () -> hn.remove(null));

        // --- TOMBSTONE behaviour: keys that collide on H1 form a probe chain ---
        // capacity 11, Integer.hashCode()==value, so 1, 12, 23 all map to H1 = 1.
        // Removing the middle of the chain must NOT cut off the rest of the chain.
        Hash_table_double_hashing<Integer, String> tomb = new Hash_table_double_hashing<>(11);
        tomb.put(1,  "one");
        tomb.put(12, "twelve");
        tomb.put(23, "twentythree");
        checkEquals("chain get 1",  "one",         tomb.get(1));
        checkEquals("chain get 12", "twelve",       tomb.get(12));
        checkEquals("chain get 23", "twentythree",  tomb.get(23));
        checkEquals("remove middle of chain", "twelve", tomb.remove(12));
        // the key test: 23 was probed-to AFTER 12; with a tombstone it stays reachable
        checkEquals("get 1 after tombstone",  "one",        tomb.get(1));
        checkEquals("get 23 after tombstone", "twentythree", tomb.get(23));
        checkEquals("get 12 gone",            null,          tomb.get(12));
        checkEquals("size after one removal", 2,             tomb.size());
        // reuse the tombstone slot with a new colliding key
        checkEquals("put 34 reuse tombstone", null, tomb.put(34, "thirtyfour"));
        checkEquals("get 34",                 "thirtyfour", tomb.get(34));
        checkEquals("size after reuse",       3,            tomb.size());

        // --- Forced collisions + resize: many integer keys, small start capacity ---
        Hash_table_double_hashing<Integer, Integer> big = new Hash_table_double_hashing<>(5);
        final int N = 200;
        for (int i = 0; i < N; i++) {
            checkTrue("insert " + i + " is new", big.put(i, i * 10) == null);
        }
        checkEquals("size after N inserts", N, big.size());
        boolean allFound = true;
        for (int i = 0; i < N; i++) {
            if (!Objects.equals(i * 10, big.get(i))) { allFound = false; break; }
        }
        checkTrue("all N retrievable after resize", allFound);

        for (int i = 0; i < N; i += 2) big.remove(i);
        checkEquals("size after removing evens", N / 2, big.size());
        boolean oddsOk = true, evensGone = true;
        for (int i = 0; i < N; i++) {
            if (i % 2 == 1 && !Objects.equals(i * 10, big.get(i))) oddsOk = false;
            if (i % 2 == 0 && big.get(i) != null)                  evensGone = false;
        }
        checkTrue("odd keys survive after evens removed", oddsOk);
        checkTrue("even keys gone", evensGone);
        // re-insert into the holes (tombstone reuse at scale)
        for (int i = 0; i < N; i += 2) big.put(i, i * 100);
        checkEquals("size after re-insert", N, big.size());
        boolean reinsertOk = true;
        for (int i = 0; i < N; i += 2) {
            if (!Objects.equals(i * 100, big.get(i))) { reinsertOk = false; break; }
        }
        checkTrue("re-inserted evens retrievable", reinsertOk);

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}