# Algorithms — Implementation Notes

Idea, complexity, and notes for each algorithm.

## Contents
1. [Unique_substrings](#unique_substrings)
2. [Longest_repeated_substring](#longest_repeated_substring)
3. [Longest_common_substring](#longest_common_substring)

---

## Unique_substrings

Count the distinct non-empty substrings of a string.

### Idea
Every substring is a prefix of exactly one suffix. Across all suffixes there are `n(n+1)/2` prefixes — every substring counted *with* duplicates. Adjacent suffixes in sorted order share `lcp[i]` leading characters, and those shared prefixes are exactly the double-counts, so subtract them: `distinct = n(n+1)/2 - sum(lcp)`. The naive alternative (enumerate every substring into a hash set) is O(n³) time and O(n²) memory; this identity replaces all of it with one sum.

### Complexity
O(n log n) to build the suffix array, O(n) for Kasai's LCP, O(n) to sum — dominated by the build.

### Notes
Use a `long` for the count and the sum: `n(n+1)/2` overflows `int` well before n gets large.

---

## Longest_repeated_substring

Find the longest substring that occurs at least twice.

### Idea
A repeated substring is a common prefix of two *distinct* suffixes, so the longest one has length `max(lcp)`. Recover it as that many leading characters of the suffix at the rank where the maximum sits.

### Complexity
O(n log n), dominated by the suffix-array build; the scan for the maximum LCP is O(n).

### Notes
If `max(lcp) == 0` nothing repeats → return empty. Ties (several substrings of the same maximal length) — any is acceptable; return the first found.

---

## Longest_common_substring

Find the longest substring appearing in at least k of n input strings (2 ≤ k ≤ n).

### Idea
Concatenate the strings with a unique sentinel between each — a character lexicographically smaller than any real one, and distinct per boundary so no suffix straddles two source strings. Build the suffix array + LCP over the combined string, then color each suffix by which source string it came from (its start position relative to the boundaries; the boundary marker doubles as the color id) and skip suffixes that begin with a sentinel. Slide a window over the sorted ranks: the substring shared by every suffix in a window has length equal to the minimum LCP strictly inside that window. Keep a hashtable `color -> count`; grow the window until it covers ≥ k distinct colors, then shrink to stay minimal, tracking the window-min LCP as the candidate length. Keep the longest candidate. The smallest useful window is size k.

### Complexity
O(N log N) for the suffix array over the total combined length N; the windowed scan is O(N) with a monotonic deque (or sparse table) supplying the window-minimum LCP.

### Notes
Sentinels must be smaller than every real character *and* mutually distinct, or suffixes from different strings could compare as equal across a boundary. k = n is the all-strings case; k = 2 is "shared by any pair." If no window ever reaches k colors, there is no common substring → return empty.