package Algorithms;
import Data_structures.Suffix_array;

import java.util.Arrays;
import java.util.Objects;

// Count the number of distinct non-empty substrings of a string using the
// suffix array + LCP array.
//
// Idea: every substring is a prefix of exactly one suffix. Over all suffixes
// there are n(n+1)/2 prefixes, but adjacent sorted suffixes share lcp[i]
// leading characters that get double-counted. So:
//     distinct = n(n+1)/2 - sum(lcp)
public class Unique_substrings {

    // Return the count of distinct non-empty substrings of s.
    // Throw NullPointerException if s is null.
    public static long countDistinct(String s) {
        if(s == null){
            throw new NullPointerException();
        } else if (s.length() == 0){
            return 0;
        }
        Suffix_array arr = new Suffix_array(s);
        int nsum = (s.length()*(s.length()+1))/2;
        int sumarr = Arrays.stream(arr.lcpArray()).sum();
        return nsum-sumarr;
    }
}

class Unique_substrings_Main {
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

    // brute force: collect all substrings in a set
    private static long bruteDistinct(String s) {
        java.util.HashSet<String> set = new java.util.HashSet<>();
        for (int i = 0; i < s.length(); i++)
            for (int j = i + 1; j <= s.length(); j++)
                set.add(s.substring(i, j));
        return set.size();
    }

    public static void main(String[] args) {
        checkEquals("banana", 15L, Unique_substrings.countDistinct("banana"));
        checkEquals("abab",   7L,  Unique_substrings.countDistinct("abab"));
        checkEquals("aaaa",   4L,  Unique_substrings.countDistinct("aaaa"));
        checkEquals("abc",    6L,  Unique_substrings.countDistinct("abc"));
        checkEquals("single", 1L,  Unique_substrings.countDistinct("x"));
        checkEquals("empty",  0L,  Unique_substrings.countDistinct(""));

        // cross-check against brute force on a few strings
        String[] cases = {"mississippi", "abracadabra", "aabbaabb", "xyzzyx"};
        boolean ok = true;
        for (String c : cases) {
            if (Unique_substrings.countDistinct(c) != bruteDistinct(c)) { ok = false; break; }
        }
        if (ok) { passed++; System.out.println("PASS: brute-force cross-check"); }
        else    { failed++; System.out.println("FAIL: brute-force cross-check"); }

        checkThrows("null", NullPointerException.class, () -> Unique_substrings.countDistinct(null));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}