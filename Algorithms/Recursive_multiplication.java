package Algorithms;
import java.util.Objects;

// Multiply two integers using recursion (no * operator) — repeated addition,
// reducing the problem toward a base case. Handles negative operands and zero.
public class Recursive_multiplication {

    // Return a * b, computed recursively. Returns long to avoid int overflow.
    public static long multiply(int a, int b) {
        if(a==0){
            return 0;
        }else if(a < 0){
            return (multiply(a+1, b) - b);
        }else{
            long multiplyvalue = multiply(a/2, b);
            if(a%2 ==0){
                return multiplyvalue + multiplyvalue;
            }else{
                return multiplyvalue + multiplyvalue + b;
            }
        }
    }
}

class Recursive_multiplication_Main {
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

    public static void main(String[] args) {
        // --- Zero ---
        checkEquals("0 * 5",  0L, Recursive_multiplication.multiply(0, 5));
        checkEquals("5 * 0",  0L, Recursive_multiplication.multiply(5, 0));
        checkEquals("0 * 0",  0L, Recursive_multiplication.multiply(0, 0));

        // --- Positives ---
        checkEquals("3 * 4",  12L, Recursive_multiplication.multiply(3, 4));
        checkEquals("4 * 3",  12L, Recursive_multiplication.multiply(4, 3));
        checkEquals("7 * 1",  7L,  Recursive_multiplication.multiply(7, 1));
        checkEquals("1 * 7",  7L,  Recursive_multiplication.multiply(1, 7));
        checkEquals("9 * 9",  81L, Recursive_multiplication.multiply(9, 9));

        // --- Signs ---
        checkEquals("-3 * 4",  -12L, Recursive_multiplication.multiply(-3, 4));
        checkEquals("3 * -4",  -12L, Recursive_multiplication.multiply(3, -4));
        checkEquals("-3 * -4", 12L,  Recursive_multiplication.multiply(-3, -4));
        checkEquals("-7 * 1",  -7L,  Recursive_multiplication.multiply(-7, 1));
        checkEquals("1 * -7",  -7L,  Recursive_multiplication.multiply(1, -7));

        // --- Overflow-safe (int * int would overflow int, long must hold it) ---
        checkEquals("100000 * 100000", 10000000000L,
                Recursive_multiplication.multiply(100000, 100000));

        // --- Brute-force cross-check over a range ---
        boolean ok = true;
        for (int a = -20; a <= 20 && ok; a++)
            for (int b = -20; b <= 20; b++)
                if (Recursive_multiplication.multiply(a, b) != (long) a * b) { ok = false; break; }
        if (ok) { passed++; System.out.println("PASS: brute-force cross-check [-20,20]"); }
        else    { failed++; System.out.println("FAIL: brute-force cross-check [-20,20]"); }

        System.out.println();
        System.out.println("=== " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) System.exit(1);
    }
}