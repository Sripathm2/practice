package Algorithms;
import java.util.Objects;

// Reverse a string using recursion: peel off one character, reverse the rest,
// and put the peeled character on the far end.
public class Recursive_string_reversal {

    // Return s reversed, computed recursively. Empty string -> "".
    // Throw NullPointerException if s is null.
    public static String reverse(String s){
        if(s == null){
            throw new NullPointerException();
        }
        int n = s.length();
        if(n < 2){
            return s;
        }
        int l = 0;
        int r = n-1;
        return s.charAt(r) + reverse(s.substring(l+1, r)) + s.charAt(l);
    }
}

class Recursive_string_reversal_Main {
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
        // --- Base cases ---
        checkEquals("empty",  "",  Recursive_string_reversal.reverse(""));
        checkEquals("single", "a", Recursive_string_reversal.reverse("a"));

        // --- Basic ---
        checkEquals("ab",     "ba",    Recursive_string_reversal.reverse("ab"));
        checkEquals("abc",    "cba",   Recursive_string_reversal.reverse("abc"));
        checkEquals("hello",  "olleh", Recursive_string_reversal.reverse("hello"));

        // --- Palindrome unchanged ---
        checkEquals("racecar", "racecar", Recursive_string_reversal.reverse("racecar"));

        // --- Spaces, digits, symbols ---
        checkEquals("with spaces", "cba dcb a",
                Recursive_string_reversal.reverse("a bcd abc"));
        checkEquals("mixed", "!321cba",
                Recursive_string_reversal.reverse("abc123!"));

        // --- Double reverse is identity ---
        String s = "recursion is fun";
        checkEquals("double reverse == original", s,
                Recursive_string_reversal.reverse(Recursive_string_reversal.reverse(s)));

        // --- Null safety ---
        checkThrows("null", NullPointerException.class,
                () -> Recursive_string_reversal.reverse(null));

        // --- Cross-check against StringBuilder.reverse on random strings ---
        java.util.Random rng = new java.util.Random(3);
        String alphabet = "abcdefgh 12!";
        boolean ok = true;
        for (int t = 0; t < 200 && ok; t++) {
            int len = rng.nextInt(40);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) sb.append(alphabet.charAt(rng.nextInt(alphabet.length())));
            String in = sb.toString();
            String expected = new StringBuilder(in).reverse().toString();
            if (!expected.equals(Recursive_string_reversal.reverse(in))) ok = false;
        }
        if (ok) { passed++; System.out.println("PASS: cross-check vs StringBuilder.reverse"); }
        else    { failed++; System.out.println("FAIL: cross-check vs StringBuilder.reverse"); }

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}