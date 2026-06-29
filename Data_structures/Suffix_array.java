package Data_structures;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

// Suffix array over a string: the start indices of all suffixes, sorted
// lexicographically. Stores only the indices (the string reconstructs any
// suffix on demand) plus the LCP array. Compact alternative to a suffix tree.
//
// LCP convention: lcp[i] = length of the longest common prefix of the suffixes
// at sorted ranks i and i-1 (adjacent in sorted order). lcp[0] = 0.
public class Suffix_array {
    private final String text;
    private final int n;
    private int[] sa;    // sorted suffix start indices, length n
    private int[] lcp;   // lcp[i] = LCP(suffix at rank i, suffix at rank i-1); length n
    private ArrayList<String> suffixesArray;  

    // Build the suffix array and LCP array for text.
    // Throw NullPointerException if text is null.
    public Suffix_array(String text) {
        this.text = text;   // leave NPE/field setup for you; n/sa/lcp to be built
        this.n = 0;
        this.suffixesArray = new ArrayList<String>();
        int index = 0;
        for(index = 0; index < this.text.length(); index ++){
            this.suffixesArray.add(this.text.substring(index));
        }

        this.suffixesArray.sort(null);
        this.sa = new int[this.suffixesArray.size()];
        this.lcp = new int[this.suffixesArray.size()];

        index = 0;
        for(String s: this.suffixesArray){
            this.sa[index] = this.text.lastIndexOf(s);
            index += 1;
        }

        index = 1;
        if(text.length() > 0){
            this.lcp[0] = 0;
            for(index = 1; index < this.lcp.length; index ++){
                this.lcp[index] = this.commontill(this.suffixesArray.get(index), this.suffixesArray.get(index-1));
            }
        }
    }

    private int commontill(String s1, String s2){
        if (s1.charAt(0)!= s2.charAt(0)) {
            return 0;
        }
        int index = 0;
        while(s1.length() > index && s2.length() > index && s1.charAt(index) == s2.charAt(index)){
            index += 1;
        }
        return index;
    }


    // Return the number of suffixes (== text length).
    public int length() {
        return this.suffixesArray.size();
    }

    // Return the suffix array: sorted suffix start indices, length n.
    public int[] suffixArray() {
        return this.sa;
    }

    // Return the LCP array aligned to the suffix array, length n. lcp[0] == 0.
    public int[] lcpArray() {
        return this.lcp;
    }

    // Return the suffix beginning at sorted rank r (0-based). For tests/debugging.
    // Throw IndexOutOfBoundsException if rank not in [0, n).
    public String suffixAt(int rank) {
        if(rank < 0 || rank >= this.suffixesArray.size()){
            throw new IndexOutOfBoundsException();
        }
        return this.suffixesArray.get(rank);
    }

    // Return "[suffix0, suffix1, ...]" in sorted order. Empty string -> "[]".
    @Override
    public String toString() {
        return this.suffixesArray.toString();
    }
}

class Suffix_array_Main {
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
        // --- banana ---
        // suffixes sorted: a(5), ana(3), anana(1), banana(0), na(4), nana(2)
        Suffix_array ban = new Suffix_array("banana");
        checkEquals("banana length", 6, ban.length());
        checkEquals("banana sa",
                Arrays.toString(new int[]{5, 3, 1, 0, 4, 2}),
                Arrays.toString(ban.suffixArray()));
        checkEquals("banana lcp",
                Arrays.toString(new int[]{0, 1, 3, 0, 0, 2}),
                Arrays.toString(ban.lcpArray()));
        checkEquals("banana rank0", "a",      ban.suffixAt(0));
        checkEquals("banana rank3", "banana", ban.suffixAt(3));

        // --- abab ---
        // sorted: ab(2), abab(0), b(3), bab(1)
        Suffix_array ab = new Suffix_array("abab");
        checkEquals("abab sa",
                Arrays.toString(new int[]{2, 0, 3, 1}),
                Arrays.toString(ab.suffixArray()));
        checkEquals("abab lcp",
                Arrays.toString(new int[]{0, 2, 0, 1}),
                Arrays.toString(ab.lcpArray()));

        // --- all-same ---
        // aaaa sorted: a(3), aa(2), aaa(1), aaaa(0)
        Suffix_array aaaa = new Suffix_array("aaaa");
        checkEquals("aaaa sa",
                Arrays.toString(new int[]{3, 2, 1, 0}),
                Arrays.toString(aaaa.suffixArray()));
        checkEquals("aaaa lcp",
                Arrays.toString(new int[]{0, 1, 2, 3}),
                Arrays.toString(aaaa.lcpArray()));

        // --- single char ---
        Suffix_array one = new Suffix_array("x");
        checkEquals("single length", 1, one.length());
        checkEquals("single sa", Arrays.toString(new int[]{0}), Arrays.toString(one.suffixArray()));
        checkEquals("single lcp", Arrays.toString(new int[]{0}), Arrays.toString(one.lcpArray()));

        // --- empty ---
        Suffix_array empty = new Suffix_array("");
        checkEquals("empty length", 0, empty.length());
        checkEquals("empty sa", Arrays.toString(new int[]{}), Arrays.toString(empty.suffixArray()));
        checkEquals("empty toString", "[]", empty.toString());

        // --- bounds / null ---
        final Suffix_array b2 = ban;
        checkThrows("rank -1", IndexOutOfBoundsException.class, () -> b2.suffixAt(-1));
        checkThrows("rank n",  IndexOutOfBoundsException.class, () -> b2.suffixAt(6));
        checkThrows("null text", NullPointerException.class, () -> new Suffix_array(null));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}