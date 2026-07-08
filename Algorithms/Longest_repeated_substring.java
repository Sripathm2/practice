package Algorithms;
import Data_structures.Suffix_array;

import java.util.Objects;

// Find the longest substring that occurs at least twice in a string.
//
// Idea: a repeated substring is a common prefix of two distinct suffixes.
// The longest one has length max(lcp). Recover it as that many leading
// characters of the suffix at the rank where the max occurs.
// Ties: any maximal substring is acceptable; return the first found.
public class Longest_repeated_substring {

    // Return the longest substring occurring >= 2 times in s, or "" if none.
    // Throw NullPointerException if s is null.
    public static String longestRepeated(String s) {
        if(s == null){
            throw new NullPointerException();
        } else if (s.length() == 0){
            return "";
        }
        Suffix_array arr = new Suffix_array(s);
        String returns = "";
        int count = 0;
        int [] lcp_copy = arr.lcpArray();
        for(int index=0; index < lcp_copy.length; index++){
            if(lcp_copy[index] > count){
                count = lcp_copy[index];
                returns = arr.suffixAt(index).substring(0,count);
            }
        }
        return returns;
    }
}

class Longest_repeated_substring_Main {
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
        // unique longest answers
        checkEquals("banana -> ana", "ana", Longest_repeated_substring.longestRepeated("banana"));
        checkEquals("aaaa -> aaa",   "aaa", Longest_repeated_substring.longestRepeated("aaaa"));
        checkEquals("abcabcx -> abc","abc", Longest_repeated_substring.longestRepeated("abcabcx"));
        checkEquals("mississippi -> issi", "issi",
                Longest_repeated_substring.longestRepeated("mississippi"));

        // no repeat -> empty
        checkEquals("abc -> empty", "", Longest_repeated_substring.longestRepeated("abc"));
        checkEquals("single -> empty", "", Longest_repeated_substring.longestRepeated("x"));
        checkEquals("empty -> empty", "", Longest_repeated_substring.longestRepeated(""));

        checkThrows("null", NullPointerException.class,
                () -> Longest_repeated_substring.longestRepeated(null));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}