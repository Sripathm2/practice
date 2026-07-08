package Data_structures;
import java.util.Objects;

// Hash table using separate chaining: each bucket holds a singly linked
// chain of entries. Null keys and null values are not permitted.
public class Hash_table_chaining<K, V> {
    private static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;
        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Object[] buckets;   // each slot is an Entry<K,V> chain head, or null
    private int size;
    private int capacity;

    private static final int DEFAULT_CAPACITY = 16;

    // Construct an empty table with the default capacity.
    public Hash_table_chaining() {
        this(DEFAULT_CAPACITY);
    }

    // Construct an empty table with the given initial capacity.
    // Throw IllegalArgumentException if initialCapacity < 1.
    public Hash_table_chaining(int initialCapacity) {
        if(initialCapacity < 1){
            throw new IllegalArgumentException();
        }
        this.buckets = new Object[initialCapacity];
        this.size = 0;
        this.capacity = initialCapacity;
        for(int index=0; index < this.capacity; index ++){
            this.buckets[index] = null;
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
        int hashindex = (key.hashCode() & 0x7fffffff) % this.capacity;
        if(this.buckets[hashindex] == null){
            this.buckets[hashindex] = new Entry<K,V>(key, value, null);
            this.size += 1;
        }else{
            Entry<K, V> temp = (Entry<K,V>) this.buckets[hashindex];
            while(temp.next!=null){
                if(temp.key.equals(key)){
                    V returnV = temp.value;
                    temp.value = value;
                    return returnV;
                }
                temp= temp.next;
            }
            if(temp.key.equals(key)){
                V returnV = temp.value;
                temp.value = value;
                return returnV;
            }else{
                temp.next = new Entry<K,V>(key, value, null);
                this.size += 1;
            }
        }
        return null;
    }

    // Return the value mapped to key, or null if key is absent.
    // Throw NullPointerException if key is null.
    @SuppressWarnings("unchecked")
    public V get(K key) {
        if(key == null){
            throw new NullPointerException();
        }
        int hashindex = (key.hashCode() & 0x7fffffff) % this.capacity;
        if(this.buckets[hashindex] == null){
            return null;
        }else{
            Entry<K, V> temp = (Entry<K,V>) this.buckets[hashindex];
            while(temp!=null){
                if(temp.key.equals(key)){
                    return temp.value;
                }
                temp= temp.next;
            }
        }
        return null;
    }

    // Return true if key has a mapping.
    // Throw NullPointerException if key is null.
    @SuppressWarnings("unchecked")
    public boolean containsKey(K key) {
        if(key == null){
            throw new NullPointerException();
        }
        int hashindex = (key.hashCode() & 0x7fffffff) % this.capacity;
        if(this.buckets[hashindex] == null){
            return false;
        }else{
            Entry<K, V> temp = (Entry<K,V>) this.buckets[hashindex];
            while(temp!=null){
                if(temp.key.equals(key)){
                    return true;
                }
                temp= temp.next;
            }
        }
        return false;
    }

    // Remove the mapping for key.
    // Return the removed value, or null if key was absent.
    // Throw NullPointerException if key is null.
    @SuppressWarnings("unchecked")
    public V remove(K key) {
        if(key == null){
            throw new NullPointerException();
        } else if (this.containsKey(key) == false){
            return null;
        } else{
            int hashindex = (key.hashCode() & 0x7fffffff) % this.capacity;
            Entry<K, V> temp = (Entry<K,V>) this.buckets[hashindex];
            if(temp.key.equals(key)){
                this.buckets[hashindex] = temp.next;
                this.size -= 1;
                return temp.value;
            }
            while(temp.next!=null){
                if(temp.next.key.equals(key)){
                    V returnv = temp.next.value;
                    temp.next = temp.next.next;
                    this.size -= 1;
                    return returnv;
                }
                temp= temp.next;
            }
            return null;
        }
    }

    // Return number of mappings.
    public int size() {
        return this.size;
    }

    // Return true if empty.
    public boolean isEmpty() {
        return this.size == 0;
    }

    // Return "{k1=v1, k2=v2, ...}" over all mappings, order unspecified.
    // Empty table returns "{}".
    @Override
    @SuppressWarnings("unchecked")
    public String toString() {
        String output = "{";
        for(int index = 0; index < this.buckets.length; index++){
            if(this.buckets[index] != null){
                Entry<K, V> temp = (Entry<K,V>) this.buckets[index];
                while(temp!=null){
                    output += temp.key.toString() + "=" + temp.value.toString();
                    output += ", ";
                    temp= temp.next;
                }
            }
        }
        if(this.size > 0){
            output = output.substring(0, output.length()-2);
        }
        output += "}";
        return output;
    }
}

class Hash_table_chaining_Main {
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
        Hash_table_chaining<String, Integer> h = new Hash_table_chaining<>();
        checkEquals("empty size",        0,     h.size());
        checkTrue ("empty isEmpty",             h.isEmpty());
        checkEquals("empty get",         null,  h.get("a"));
        checkEquals("empty containsKey", false, h.containsKey("a"));
        checkEquals("empty remove",      null,  h.remove("a"));
        checkEquals("empty toString",    "{}",  h.toString());

        // --- Put new returns null, get back ---
        checkEquals("put a new",   null, h.put("a", 1));
        checkEquals("put b new",   null, h.put("b", 2));
        checkEquals("put c new",   null, h.put("c", 3));
        checkEquals("size 3",      3,    h.size());
        checkTrue ("not empty",    !h.isEmpty());
        checkEquals("get a",       1,    h.get("a"));
        checkEquals("get b",       2,    h.get("b"));
        checkEquals("get c",       3,    h.get("c"));

        // --- Update existing returns old value, size unchanged ---
        checkEquals("put a update returns old", 1, h.put("a", 100));
        checkEquals("get a updated",            100, h.get("a"));
        checkEquals("size after update",        3, h.size());

        // --- containsKey ---
        checkTrue ("contains a",  h.containsKey("a"));
        checkEquals("contains z", false, h.containsKey("z"));

        // --- Remove ---
        checkEquals("remove b returns val", 2,   h.remove("b"));
        checkEquals("get b after remove",   null, h.get("b"));
        checkEquals("contains b after remove", false, h.containsKey("b"));
        checkEquals("size after remove",    2,   h.size());
        checkEquals("remove b again",       null, h.remove("b"));

        // --- Null safety ---
        final Hash_table_chaining<String, Integer> hn = new Hash_table_chaining<>();
        checkThrows("put null key",   NullPointerException.class, () -> hn.put(null, 1));
        checkThrows("put null value", NullPointerException.class, () -> hn.put("a", null));
        checkThrows("get null",       NullPointerException.class, () -> hn.get(null));
        checkThrows("contains null",  NullPointerException.class, () -> hn.containsKey(null));
        checkThrows("remove null",    NullPointerException.class, () -> hn.remove(null));

        // --- Forced collisions + resize: small capacity, many integer keys ---
        // Integer.hashCode() == value, so keys sharing value % capacity collide.
        Hash_table_chaining<Integer, Integer> big = new Hash_table_chaining<>(4);
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

        // remove half, confirm the rest survive
        for (int i = 0; i < N; i += 2) big.remove(i);
        checkEquals("size after removing evens", N / 2, big.size());
        boolean oddsOk = true, evensGone = true;
        for (int i = 0; i < N; i++) {
            if (i % 2 == 1 && !Objects.equals(i * 10, big.get(i))) oddsOk = false;
            if (i % 2 == 0 && big.get(i) != null)                  evensGone = false;
        }
        checkTrue("odd keys survive removal of evens", oddsOk);
        checkTrue("even keys gone", evensGone);

        // --- Same key updated many times stays single mapping ---
        Hash_table_chaining<String, Integer> upd = new Hash_table_chaining<>();
        for (int i = 0; i < 50; i++) upd.put("k", i);
        checkEquals("repeated update size 1", 1, upd.size());
        checkEquals("repeated update final",  49, upd.get("k"));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}