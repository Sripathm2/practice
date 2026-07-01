package Algorithms;
import Data_structures.Suffix_array;
import Data_structures.Priority_queue;

import java.util.Arrays;
import java.util.Hashtable;
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
        if(strings == null){
            throw new NullPointerException();
        }
        if(k < 2 || k > strings.length){
            throw new IllegalArgumentException();
        }
        String encoded = "";
        Hashtable<Integer,Character> color = new Hashtable<Integer,Character>();
        Hashtable<Integer,Integer> color_count = new Hashtable<Integer, Integer>();
        int starting_char = 41;
        for(int index = 0; index <strings.length; index++){
            if(strings[index] == null){
                throw new NullPointerException();
            }
            color.put(index, (char)starting_char);
            color_count.put(index, 0);
            encoded += shiftby(strings[index], 41+strings.length) + (char)starting_char;
            starting_char += 1;
        }

        Suffix_array arr = new Suffix_array(encoded);
        String returnv = "";
        int longest_count_length = 0;
        Priority_queue<Integer> minimum_value_in_window = new Priority_queue<Integer>();
        int [] owner = new int[arr.length()];
        int [] lcparray = arr.lcpArray();
        int window_low = 0;
        int window_high = window_low + 1;
        
    
        for(int i =0;i < owner.length; i++){
            owner[i] = find_owner(arr.suffixAt(i), strings.length);
        }


        minimum_value_in_window.offer(lcparray[window_low]);
        color_count.put(owner[window_low], 1);

        // System.out.println(encoded);

        // for(int i = 0; i < arr.suffixArray().length; i ++ ){
        //     System.out.println( shiftby(arr.suffixAt(i), -1*(41+strings.length)) + "   " + lcparray[i] + "   " + i);
        // }

        while(window_low < arr.length() && window_high < arr.length()){
            if(arr.suffixAt(window_low).charAt(0) < 'A'+ 41+strings.length){
                // System.out.println(window_low + " case 1 " + window_high + "  " + minimum_value_in_window.toString() + "  " + color_count);
                //increase window low and window high as we need to ignore this string completly.
                minimum_value_in_window.remove(lcparray[window_low]);
                color_count.put(owner[window_low], color_count.get(owner[window_low])-1);
                window_low += 1;
                minimum_value_in_window.offer(lcparray[window_high]);
                color_count.put(owner[window_high], color_count.get(owner[window_high])+1);
                window_high += 1;
                
            }else if(!all_color(color_count, k) && window_high+1<arr.length()){ // case we dont have enough colors 
                // System.out.println(window_low + " case 2 " + window_high + "  " + minimum_value_in_window.toString() + "  " + color_count);
                minimum_value_in_window.offer(lcparray[window_high]);
                color_count.put(owner[window_high], color_count.get(owner[window_high])+1);
                window_high += 1;
            }else if(all_color(color_count, k)){
                // System.out.println(window_low + " case 3 " + window_high + "  " + minimum_value_in_window.toString() + "  " + color_count);
                minimum_value_in_window.remove(lcparray[window_low]);
                if(minimum_value_in_window.peek() > longest_count_length){
                    longest_count_length = minimum_value_in_window.peek();
                    returnv = arr.suffixAt(window_low).substring(0,longest_count_length);
                }
                minimum_value_in_window.offer(lcparray[window_low]);
                minimum_value_in_window.remove(lcparray[window_low]);
                color_count.put(owner[window_low], color_count.get(owner[window_low])-1);
                window_low += 1;
            }else{
                // System.out.println(window_low + " case 4 " + window_high + "  " + minimum_value_in_window.toString() + "  " + color_count);
                window_high += 1;
            }
        }
        return shiftby(returnv, -1*(41+strings.length));
    }

    private static boolean all_color(Hashtable<Integer,Integer> colors, int k){
        int[] tempArr = colors.values()
                          .stream()
                          .mapToInt(Integer::intValue)
                          .toArray();
        Arrays.sort(tempArr);
        return tempArr[colors.size()-k] != 0;
    }

    private static int find_owner(String input, int length){
        for(int i =0 ; i < length; i++){
            if(input.indexOf((char)(41+i))!= -1){
                return i;
            }
        }
        return -1;
    }

    private static String shiftby(String input, int shift){
        String returnv = "";
        for(int i =0; i<input.length(); i++){
            returnv += (char)(input.charAt(i)+shift);
        }
        return returnv;
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