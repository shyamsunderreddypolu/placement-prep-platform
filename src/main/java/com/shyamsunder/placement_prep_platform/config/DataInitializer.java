package com.shyamsunder.placement_prep_platform.config;

import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.entity.Problem;
import com.shyamsunder.placement_prep_platform.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProblemRepository problemRepository;

    @Override
    public void run(String... args) throws Exception {
        if (problemRepository.count() == 0) {
            seedProblems();
        }
    }

    private void seedProblems() {
        List<Problem> problems = List.of(
            // Arrays & Patterns
            create("Two Sum", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/two-sum/", "Two Pointers"),
            create("Contains Duplicate", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/contains-duplicate/", "Hash Set"),
            create("Maximum Subarray", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/maximum-subarray/", "Kadane Algorithm"),
            create("Product of Array Except Self", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/product-of-array-except-self/", "Prefix Sum"),
            create("Merge Sorted Array", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/merge-sorted-array/", "Two Pointers"),
            create("Move Zeroes", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/move-zeroes/", "Two Pointers"),
            create("Rotate Array", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/rotate-array/", "Two Pointers"),
            create("Majority Element", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/majority-element/", "Boyer-Moore Voting"),
            create("Best Time to Buy and Sell Stock", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/best-time-to-buy-and-sell-stock/", "Sliding Window"),
            create("Find Pivot Index", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/find-pivot-index/", "Prefix Sum"),
            create("3Sum", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/3sum/", "Two Pointers"),
            create("Container With Most Water", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/container-with-most-water/", "Two Pointers"),
            create("Trapping Rain Water", Difficulty.HARD, "Arrays", "https://leetcode.com/problems/trapping-rain-water/", "Two Pointers"),
            create("Next Permutation", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/next-permutation/", "Two Pointers"),
            create("Set Matrix Zeroes", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/set-matrix-zeroes/", "Matrix Traversal"),

            // Strings & Patterns
            create("Valid Anagram", Difficulty.EASY, "Strings", "https://leetcode.com/problems/valid-anagram/", "Hash Map"),
            create("Longest Common Prefix", Difficulty.EASY, "Strings", "https://leetcode.com/problems/longest-common-prefix/", "String Matching"),
            create("Group Anagrams", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/group-anagrams/", "Hash Map"),
            create("Longest Palindromic Substring", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/longest-palindromic-substring/", "Expand Around Center"),
            create("Longest Substring Without Repeating Characters", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/longest-substring-without-repeating-characters/", "Sliding Window"),
            create("Implement strStr()", Difficulty.EASY, "Strings", "https://leetcode.com/problems/implement-strstr/", "Two Pointers"),
            create("Palindrome Number", Difficulty.EASY, "Strings", "https://leetcode.com/problems/palindrome-number/", "Two Pointers"),
            create("Valid Palindrome", Difficulty.EASY, "Strings", "https://leetcode.com/problems/valid-palindrome/", "Two Pointers"),
            create("String Compression", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/string-compression/", "Two Pointers"),
            create("Reverse Words in a String", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/reverse-words-in-a-string/", "Two Pointers"),
            create("Minimum Window Substring", Difficulty.HARD, "Strings", "https://leetcode.com/problems/minimum-window-substring/", "Sliding Window"),
            create("Decode String", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/decode-string/", "Monotonic Stack"),
            create("Roman to Integer", Difficulty.EASY, "Strings", "https://leetcode.com/problems/roman-to-integer/", "Hash Map"),
            create("Integer to Roman", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/integer-to-roman/", "Greedy"),
            create("Zigzag Conversion", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/zigzag-conversion/", "Matrix Traversal"),

            // Linked List & Patterns
            create("Reverse Linked List", Difficulty.EASY, "Linked List", "https://leetcode.com/problems/reverse-linked-list/", "Two Pointers"),
            create("Merge Two Sorted Lists", Difficulty.EASY, "Linked List", "https://leetcode.com/problems/merge-two-sorted-lists/", "Two Pointers"),
            create("Linked List Cycle", Difficulty.EASY, "Linked List", "https://leetcode.com/problems/linked-list-cycle/", "Fast & Slow Pointers"),
            create("Remove Nth Node From End", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/remove-nth-node-from-end/", "Fast & Slow Pointers"),
            create("Add Two Numbers", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/add-two-numbers/", "Two Pointers"),
            create("Intersection of Two Linked Lists", Difficulty.EASY, "Linked List", "https://leetcode.com/problems/intersection-of-two-linked-lists/", "Two Pointers"),
            create("Palindrome Linked List", Difficulty.EASY, "Linked List", "https://leetcode.com/problems/palindrome-linked-list/", "Fast & Slow Pointers"),
            create("Reorder List", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/reorder-list/", "Fast & Slow Pointers"),
            create("Copy List with Random Pointer", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/copy-list-with-random-pointer/", "Hash Map"),
            create("Reverse Nodes in k-Group", Difficulty.HARD, "Linked List", "https://leetcode.com/problems/reverse-nodes-in-k-group/", "Two Pointers"),
            create("Sort List", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/sort-list/", "Fast & Slow Pointers"),
            create("Rotate List", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/rotate-list/", "Two Pointers"),
            create("Swap Nodes in Pairs", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/swap-nodes-in-pairs/", "Two Pointers"),
            create("Odd Even Linked List", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/odd-even-linked-list/", "Two Pointers"),
            create("LRU Cache", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/lru-cache/", "Doubly Linked List & Hash Map"),

            // Trees & Patterns
            create("Maximum Depth of Binary Tree", Difficulty.EASY, "Trees", "https://leetcode.com/problems/maximum-depth-of-binary-tree/", "BFS / DFS"),
            create("Same Tree", Difficulty.EASY, "Trees", "https://leetcode.com/problems/same-tree/", "BFS / DFS"),
            create("Invert Binary Tree", Difficulty.EASY, "Trees", "https://leetcode.com/problems/invert-binary-tree/", "BFS / DFS"),
            create("Binary Tree Level Order Traversal", Difficulty.MEDIUM, "Trees", "https://leetcode.com/problems/binary-tree-level-order-traversal/", "BFS / DFS"),
            create("Validate Binary Search Tree", Difficulty.MEDIUM, "Trees", "https://leetcode.com/problems/validate-binary-search-tree/", "BFS / DFS"),
            create("Lowest Common Ancestor of BST", Difficulty.MEDIUM, "Trees", "https://leetcode.com/problems/lowest-common-ancestor-of-bst/", "BFS / DFS"),
            create("Diameter of Binary Tree", Difficulty.EASY, "Trees", "https://leetcode.com/problems/diameter-of-binary-tree/", "BFS / DFS"),
            create("Balanced Binary Tree", Difficulty.EASY, "Trees", "https://leetcode.com/problems/balanced-binary-tree/", "BFS / DFS"),
            create("Path Sum", Difficulty.EASY, "Trees", "https://leetcode.com/problems/path-sum/", "BFS / DFS"),
            create("Construct Binary Tree from Traversals", Difficulty.MEDIUM, "Trees", "https://leetcode.com/problems/construct-binary-tree-from-traversals/", "BFS / DFS"),
            create("Serialize and Deserialize Binary Tree", Difficulty.HARD, "Trees", "https://leetcode.com/problems/serialize-and-deserialize-binary-tree/", "BFS / DFS"),
            create("Kth Smallest Element in BST", Difficulty.MEDIUM, "Trees", "https://leetcode.com/problems/kth-smallest-element-in-bst/", "BFS / DFS"),
            create("Binary Tree Right Side View", Difficulty.MEDIUM, "Trees", "https://leetcode.com/problems/binary-tree-right-side-view/", "BFS / DFS"),
            create("Symmetric Tree", Difficulty.EASY, "Trees", "https://leetcode.com/problems/symmetric-tree/", "BFS / DFS"),
            create("Flatten Binary Tree", Difficulty.MEDIUM, "Trees", "https://leetcode.com/problems/flatten-binary-tree/", "BFS / DFS"),

            // Graphs & Patterns
            create("Number of Islands", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/number-of-islands/", "BFS / DFS"),
            create("Clone Graph", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/clone-graph/", "BFS / DFS"),
            create("Course Schedule", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/course-schedule/", "Topological Sort"),
            create("Pacific Atlantic Water Flow", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/pacific-atlantic-water-flow/", "BFS / DFS"),
            create("Rotting Oranges", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/rotting-oranges/", "BFS / DFS"),
            create("Word Ladder", Difficulty.HARD, "Graphs", "https://leetcode.com/problems/word-ladder/", "BFS / DFS"),
            create("Surrounded Regions", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/surrounded-regions/", "BFS / DFS"),
            create("Graph Valid Tree", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/graph-valid-tree/", "Union Find"),
            create("Network Delay Time", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/network-delay-time/", "Dijkstra Algorithm"),
            create("Cheapest Flights Within K Stops", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/cheapest-flights-within-k-stops/", "Dijkstra Algorithm"),
            create("Accounts Merge", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/accounts-merge/", "Union Find"),
            create("Evaluate Division", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/evaluate-division/", "BFS / DFS"),
            create("Alien Dictionary", Difficulty.HARD, "Graphs", "https://leetcode.com/problems/alien-dictionary/", "Topological Sort"),
            create("Minimum Height Trees", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/minimum-height-trees/", "Topological Sort"),
            create("Redundant Connection", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/redundant-connection/", "Union Find"),

            // Dynamic Programming & Patterns
            create("Climbing Stairs", Difficulty.EASY, "Dynamic Programming", "https://leetcode.com/problems/climbing-stairs/", "Dynamic Programming"),
            create("House Robber", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/house-robber/", "Dynamic Programming"),
            create("Coin Change", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/coin-change/", "Dynamic Programming"),
            create("Longest Increasing Subsequence", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/longest-increasing-subsequence/", "Dynamic Programming"),
            create("Longest Common Subsequence", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/longest-common-subsequence/", "Dynamic Programming"),
            create("Partition Equal Subset Sum", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/partition-equal-subset-sum/", "Dynamic Programming"),
            create("Word Break", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/word-break/", "Dynamic Programming"),
            create("Unique Paths", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/unique-paths/", "Dynamic Programming"),
            create("Edit Distance", Difficulty.HARD, "Dynamic Programming", "https://leetcode.com/problems/edit-distance/", "Dynamic Programming"),
            create("Decode Ways", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/decode-ways/", "Dynamic Programming"),
            create("Target Sum", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/target-sum/", "Dynamic Programming"),
            create("Jump Game", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/jump-game/", "Greedy"),
            create("Jump Game II", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/jump-game-ii/", "Greedy"),
            create("Maximum Product Subarray", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/maximum-product-subarray/", "Kadane Algorithm"),
            create("Distinct Subsequences", Difficulty.HARD, "Dynamic Programming", "https://leetcode.com/problems/distinct-subsequences/", "Dynamic Programming"),

            // Stack & Patterns
            create("Valid Parentheses", Difficulty.EASY, "Stack", "https://leetcode.com/problems/valid-parentheses/", "Monotonic Stack"),
            create("Min Stack", Difficulty.MEDIUM, "Stack", "https://leetcode.com/problems/min-stack/", "Monotonic Stack"),
            create("Daily Temperatures", Difficulty.MEDIUM, "Stack", "https://leetcode.com/problems/daily-temperatures/", "Monotonic Stack"),
            create("Evaluate Reverse Polish Notation", Difficulty.MEDIUM, "Stack", "https://leetcode.com/problems/evaluate-reverse-polish-notation/", "Monotonic Stack"),
            create("Largest Rectangle in Histogram", Difficulty.HARD, "Stack", "https://leetcode.com/problems/largest-rectangle-in-histogram/", "Monotonic Stack"),
            create("Asteroid Collision", Difficulty.MEDIUM, "Stack", "https://leetcode.com/problems/asteroid-collision/", "Monotonic Stack"),
            create("Basic Calculator", Difficulty.HARD, "Stack", "https://leetcode.com/problems/basic-calculator/", "Monotonic Stack"),
            create("Online Stock Span", Difficulty.MEDIUM, "Stack", "https://leetcode.com/problems/online-stock-span/", "Monotonic Stack"),
            create("Next Greater Element I", Difficulty.EASY, "Stack", "https://leetcode.com/problems/next-greater-element-i/", "Monotonic Stack"),

            // Binary Search & Patterns
            create("Binary Search", Difficulty.EASY, "Binary Search", "https://leetcode.com/problems/binary-search/", "Binary Search"),
            create("Search Insert Position", Difficulty.EASY, "Binary Search", "https://leetcode.com/problems/search-insert-position/", "Binary Search"),
            create("Search in Rotated Sorted Array", Difficulty.MEDIUM, "Binary Search", "https://leetcode.com/problems/search-in-rotated-sorted-array/", "Binary Search"),
            create("Find Minimum in Rotated Sorted Array", Difficulty.MEDIUM, "Binary Search", "https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/", "Binary Search"),
            create("Median of Two Sorted Arrays", Difficulty.HARD, "Binary Search", "https://leetcode.com/problems/median-of-two-sorted-arrays/", "Binary Search"),
            create("Koko Eating Bananas", Difficulty.MEDIUM, "Binary Search", "https://leetcode.com/problems/koko-eating-bananas/", "Binary Search"),
            create("Capacity To Ship Packages", Difficulty.MEDIUM, "Binary Search", "https://leetcode.com/problems/capacity-to-ship-packages/", "Binary Search"),
            create("First Bad Version", Difficulty.EASY, "Binary Search", "https://leetcode.com/problems/first-bad-version/", "Binary Search"),
            create("Find Peak Element", Difficulty.MEDIUM, "Binary Search", "https://leetcode.com/problems/find-peak-element/", "Binary Search"),
            create("Single Element in Sorted Array", Difficulty.MEDIUM, "Binary Search", "https://leetcode.com/problems/single-element-in-sorted-array/", "Binary Search"),

            // Heap & Patterns
            create("Kth Largest Element in an Array", Difficulty.MEDIUM, "Heap", "https://leetcode.com/problems/kth-largest-element-in-an-array/", "Top-K Heap"),
            create("Top K Frequent Elements", Difficulty.MEDIUM, "Heap", "https://leetcode.com/problems/top-k-frequent-elements/", "Top-K Heap"),
            create("Merge K Sorted Lists", Difficulty.HARD, "Heap", "https://leetcode.com/problems/merge-k-sorted-lists/", "Top-K Heap"),
            create("Find Median from Data Stream", Difficulty.HARD, "Heap", "https://leetcode.com/problems/find-median-from-data-stream/", "Top-K Heap"),
            create("Task Scheduler", Difficulty.MEDIUM, "Heap", "https://leetcode.com/problems/task-scheduler/", "Top-K Heap"),
            create("Last Stone Weight", Difficulty.EASY, "Heap", "https://leetcode.com/problems/last-stone-weight/", "Top-K Heap"),
            create("K Closest Points to Origin", Difficulty.MEDIUM, "Heap", "https://leetcode.com/problems/k-closest-points-to-origin/", "Top-K Heap"),
            create("Meeting Rooms II", Difficulty.MEDIUM, "Heap", "https://leetcode.com/problems/meeting-rooms-ii/", "Top-K Heap"),
            create("Smallest Range", Difficulty.HARD, "Heap", "https://leetcode.com/problems/smallest-range/", "Top-K Heap"),
            create("IPO", Difficulty.HARD, "Heap", "https://leetcode.com/problems/ipo/", "Top-K Heap"),

            // Backtracking & Patterns
            create("Subsets", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/subsets/", "Backtracking"),
            create("Permutations", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/permutations/", "Backtracking"),
            create("Combination Sum", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/combination-sum/", "Backtracking"),
            create("Combination Sum II", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/combination-sum-ii/", "Backtracking"),
            create("Letter Combinations of a Phone Number", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/letter-combinations-of-a-phone-number/", "Backtracking"),
            create("N-Queens", Difficulty.HARD, "Backtracking", "https://leetcode.com/problems/n-queens/", "Backtracking"),
            create("Word Search", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/word-search/", "Backtracking"),
            create("Palindrome Partitioning", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/palindrome-partitioning/", "Backtracking"),
            create("Generate Parentheses", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/generate-parentheses/", "Backtracking"),
            create("Restore IP Addresses", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/restore-ip-addresses/", "Backtracking")
        );

        problemRepository.saveAll(problems);
    }

    private Problem create(String title, Difficulty difficulty, String topic, String link, String pattern) {
        return Problem.builder()
                .title(title)
                .difficulty(difficulty)
                .topic(topic)
                .link(link)
                .pattern(pattern)
                .build();
    }
}
