package Algorithms;
import Data_structures.Suffix_array;
import java.util.Objects;

// Find the longest substring that appears in at least k of n input strings
// (2 <= k <= n), via the suffix array + LCP + a sliding window over ranks.
//
// Approach:
//  1. Concatenate the strings, separating each with a UNIQUE sentinel that is
//     lexicographically smaller than any real character (distinct per boundary
//     so no suffix spans two source strings).
//  2. Build the suffix array + LCP over the combined string.
//  3. Color each suffix by which source string it came from (its start
//     position relative to the sentinel boundaries; the boundary marker also
//     serves as the color id).
//  4. Ignore suffixes that begin with a sentinel.
//  5. Slide a window over the sorted ranks. The substring shared by every
//     suffix in a window has length = min(lcp) strictly inside the window.
//     Keep a hashtable color -> count; grow the window until it covers >= k
//     distinct colors, then shrink to stay minimal, tracking the window-min
//     LCP as the candidate length. The minimum useful window size is k.
//     Keep the best (longest) candidate.
public class Longest_common_substring {

    // Return the longest substring present in at least k of the strings, or ""
    // if none. Ties: any maximal substring is acceptable.
    // Throw NullPointerException if strings is null or any element is null.
    // Throw IllegalArgumentException if k < 2 or k > strings.length.
    public static String longestCommon(String[] strings, int k) {
        return "";
    }
}

class Longest_common_substring_Main {
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
        // "abc" is the unique longest substring common to all three
        checkEquals("abc in all 3 (k=3)", "abc",
                Longest_common_substring.longestCommon(new String[]{"abcabc", "dabce", "xabcy"}, 3));

        // common to the first two only; third shares nothing
        checkEquals("abc in 2 of 3 (k=2)", "abc",
                Longest_common_substring.longestCommon(new String[]{"abcabc", "dabce", "xyz"}, 2));

        // classic pair
        checkEquals("Geeks (k=2)", "Geeks",
                Longest_common_substring.longestCommon(new String[]{"GeeksforGeeks", "GeeksQuiz"}, 2));

        // no shared substring -> empty
        checkEquals("nothing shared (k=2)", "",
                Longest_common_substring.longestCommon(new String[]{"aaa", "bbb"}, 2));

        // --- guards ---
        checkThrows("k too small", IllegalArgumentException.class,
                () -> Longest_common_substring.longestCommon(new String[]{"a", "b"}, 1));
        checkThrows("k too large", IllegalArgumentException.class,
                () -> Longest_common_substring.longestCommon(new String[]{"a", "b"}, 3));
        checkThrows("null array", NullPointerException.class,
                () -> Longest_common_substring.longestCommon(null, 2));
        checkThrows("null element", NullPointerException.class,
                () -> Longest_common_substring.longestCommon(new String[]{"a", null}, 2));

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}