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
            // Arrays
            create("Two Sum", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/two-sum/"),
            create("Contains Duplicate", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/contains-duplicate/"),
            create("Maximum Subarray", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/maximum-subarray/"),
            create("Product of Array Except Self", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/product-of-array-except-self/"),
            create("Merge Sorted Array", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/merge-sorted-array/"),
            create("Move Zeroes", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/move-zeroes/"),
            create("Rotate Array", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/rotate-array/"),
            create("Majority Element", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/majority-element/"),
            create("Best Time to Buy and Sell Stock", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/best-time-to-buy-and-sell-stock/"),
            create("Find Pivot Index", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/find-pivot-index/"),
            create("3Sum", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/3sum/"),
            create("Container With Most Water", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/container-with-most-water/"),
            create("Trapping Rain Water", Difficulty.HARD, "Arrays", "https://leetcode.com/problems/trapping-rain-water/"),
            create("Next Permutation", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/next-permutation/"),
            create("Set Matrix Zeroes", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/set-matrix-zeroes/"),

            // Strings
            create("Valid Anagram", Difficulty.EASY, "Strings", "https://leetcode.com/problems/valid-anagram/"),
            create("Longest Common Prefix", Difficulty.EASY, "Strings", "https://leetcode.com/problems/longest-common-prefix/"),
            create("Group Anagrams", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/group-anagrams/"),
            create("Longest Palindromic Substring", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/longest-palindromic-substring/"),
            create("Longest Substring Without Repeating Characters", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/longest-substring-without-repeating-characters/"),
            create("Implement strStr()", Difficulty.EASY, "Strings", "https://leetcode.com/problems/implement-strstr/"),
            create("Palindrome Number", Difficulty.EASY, "Strings", "https://leetcode.com/problems/palindrome-number/"),
            create("Valid Palindrome", Difficulty.EASY, "Strings", "https://leetcode.com/problems/valid-palindrome/"),
            create("String Compression", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/string-compression/"),
            create("Reverse Words in a String", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/reverse-words-in-a-string/"),
            create("Minimum Window Substring", Difficulty.HARD, "Strings", "https://leetcode.com/problems/minimum-window-substring/"),
            create("Decode String", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/decode-string/"),
            create("Roman to Integer", Difficulty.EASY, "Strings", "https://leetcode.com/problems/roman-to-integer/"),
            create("Integer to Roman", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/integer-to-roman/"),
            create("Zigzag Conversion", Difficulty.MEDIUM, "Strings", "https://leetcode.com/problems/zigzag-conversion/"),

            // Linked List
            create("Reverse Linked List", Difficulty.EASY, "Linked List", "https://leetcode.com/problems/reverse-linked-list/"),
            create("Merge Two Sorted Lists", Difficulty.EASY, "Linked List", "https://leetcode.com/problems/merge-two-sorted-lists/"),
            create("Linked List Cycle", Difficulty.EASY, "Linked List", "https://leetcode.com/problems/linked-list-cycle/"),
            create("Remove Nth Node From End", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/remove-nth-node-from-end/"),
            create("Add Two Numbers", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/add-two-numbers/"),
            create("Intersection of Two Linked Lists", Difficulty.EASY, "Linked List", "https://leetcode.com/problems/intersection-of-two-linked-lists/"),
            create("Palindrome Linked List", Difficulty.EASY, "Linked List", "https://leetcode.com/problems/palindrome-linked-list/"),
            create("Reorder List", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/reorder-list/"),
            create("Copy List with Random Pointer", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/copy-list-with-random-pointer/"),
            create("Reverse Nodes in k-Group", Difficulty.HARD, "Linked List", "https://leetcode.com/problems/reverse-nodes-in-k-group/"),
            create("Sort List", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/sort-list/"),
            create("Rotate List", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/rotate-list/"),
            create("Swap Nodes in Pairs", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/swap-nodes-in-pairs/"),
            create("Odd Even Linked List", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/odd-even-linked-list/"),
            create("LRU Cache", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/lru-cache/"),

            // Trees
            create("Maximum Depth of Binary Tree", Difficulty.EASY, "Trees", "https://leetcode.com/problems/maximum-depth-of-binary-tree/"),
            create("Same Tree", Difficulty.EASY, "Trees", "https://leetcode.com/problems/same-tree/"),
            create("Invert Binary Tree", Difficulty.EASY, "Trees", "https://leetcode.com/problems/invert-binary-tree/"),
            create("Binary Tree Level Order Traversal", Difficulty.MEDIUM, "Trees", "https://leetcode.com/problems/binary-tree-level-order-traversal/"),
            create("Validate Binary Search Tree", Difficulty.MEDIUM, "Trees", "https://leetcode.com/problems/validate-binary-search-tree/"),
            create("Lowest Common Ancestor of BST", Difficulty.MEDIUM, "Trees", "https://leetcode.com/problems/lowest-common-ancestor-of-bst/"),
            create("Diameter of Binary Tree", Difficulty.EASY, "Trees", "https://leetcode.com/problems/diameter-of-binary-tree/"),
            create("Balanced Binary Tree", Difficulty.EASY, "Trees", "https://leetcode.com/problems/balanced-binary-tree/"),
            create("Path Sum", Difficulty.EASY, "Trees", "https://leetcode.com/problems/path-sum/"),
            create("Construct Binary Tree from Traversals", Difficulty.MEDIUM, "Trees", "https://leetcode.com/problems/construct-binary-tree-from-traversals/"),
            create("Serialize and Deserialize Binary Tree", Difficulty.HARD, "Trees", "https://leetcode.com/problems/serialize-and-deserialize-binary-tree/"),
            create("Kth Smallest Element in BST", Difficulty.MEDIUM, "Trees", "https://leetcode.com/problems/kth-smallest-element-in-bst/"),
            create("Binary Tree Right Side View", Difficulty.MEDIUM, "Trees", "https://leetcode.com/problems/binary-tree-right-side-view/"),
            create("Symmetric Tree", Difficulty.EASY, "Trees", "https://leetcode.com/problems/symmetric-tree/"),
            create("Flatten Binary Tree", Difficulty.MEDIUM, "Trees", "https://leetcode.com/problems/flatten-binary-tree/"),

            // Graphs
            create("Number of Islands", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/number-of-islands/"),
            create("Clone Graph", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/clone-graph/"),
            create("Course Schedule", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/course-schedule/"),
            create("Pacific Atlantic Water Flow", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/pacific-atlantic-water-flow/"),
            create("Rotting Oranges", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/rotting-oranges/"),
            create("Word Ladder", Difficulty.HARD, "Graphs", "https://leetcode.com/problems/word-ladder/"),
            create("Surrounded Regions", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/surrounded-regions/"),
            create("Graph Valid Tree", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/graph-valid-tree/"),
            create("Network Delay Time", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/network-delay-time/"),
            create("Cheapest Flights Within K Stops", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/cheapest-flights-within-k-stops/"),
            create("Accounts Merge", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/accounts-merge/"),
            create("Evaluate Division", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/evaluate-division/"),
            create("Alien Dictionary", Difficulty.HARD, "Graphs", "https://leetcode.com/problems/alien-dictionary/"),
            create("Minimum Height Trees", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/minimum-height-trees/"),
            create("Redundant Connection", Difficulty.MEDIUM, "Graphs", "https://leetcode.com/problems/redundant-connection/"),

            // Dynamic Programming
            create("Climbing Stairs", Difficulty.EASY, "Dynamic Programming", "https://leetcode.com/problems/climbing-stairs/"),
            create("House Robber", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/house-robber/"),
            create("Coin Change", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/coin-change/"),
            create("Longest Increasing Subsequence", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/longest-increasing-subsequence/"),
            create("Longest Common Subsequence", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/longest-common-subsequence/"),
            create("Partition Equal Subset Sum", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/partition-equal-subset-sum/"),
            create("Word Break", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/word-break/"),
            create("Unique Paths", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/unique-paths/"),
            create("Edit Distance", Difficulty.HARD, "Dynamic Programming", "https://leetcode.com/problems/edit-distance/"),
            create("Decode Ways", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/decode-ways/"),
            create("Target Sum", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/target-sum/"),
            create("Jump Game", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/jump-game/"),
            create("Jump Game II", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/jump-game-ii/"),
            create("Maximum Product Subarray", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/maximum-product-subarray/"),
            create("Distinct Subsequences", Difficulty.HARD, "Dynamic Programming", "https://leetcode.com/problems/distinct-subsequences/"),

            // Stack
            create("Valid Parentheses", Difficulty.EASY, "Stack", "https://leetcode.com/problems/valid-parentheses/"),
            create("Min Stack", Difficulty.MEDIUM, "Stack", "https://leetcode.com/problems/min-stack/"),
            create("Daily Temperatures", Difficulty.MEDIUM, "Stack", "https://leetcode.com/problems/daily-temperatures/"),
            create("Evaluate Reverse Polish Notation", Difficulty.MEDIUM, "Stack", "https://leetcode.com/problems/evaluate-reverse-polish-notation/"),
            create("Largest Rectangle in Histogram", Difficulty.HARD, "Stack", "https://leetcode.com/problems/largest-rectangle-in-histogram/"),
            create("Asteroid Collision", Difficulty.MEDIUM, "Stack", "https://leetcode.com/problems/asteroid-collision/"),
            create("Basic Calculator", Difficulty.HARD, "Stack", "https://leetcode.com/problems/basic-calculator/"),
            create("Online Stock Span", Difficulty.MEDIUM, "Stack", "https://leetcode.com/problems/online-stock-span/"),
            create("Next Greater Element I", Difficulty.EASY, "Stack", "https://leetcode.com/problems/next-greater-element-i/"),

            // Binary Search
            create("Binary Search", Difficulty.EASY, "Binary Search", "https://leetcode.com/problems/binary-search/"),
            create("Search Insert Position", Difficulty.EASY, "Binary Search", "https://leetcode.com/problems/search-insert-position/"),
            create("Search in Rotated Sorted Array", Difficulty.MEDIUM, "Binary Search", "https://leetcode.com/problems/search-in-rotated-sorted-array/"),
            create("Find Minimum in Rotated Sorted Array", Difficulty.MEDIUM, "Binary Search", "https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/"),
            create("Median of Two Sorted Arrays", Difficulty.HARD, "Binary Search", "https://leetcode.com/problems/median-of-two-sorted-arrays/"),
            create("Koko Eating Bananas", Difficulty.MEDIUM, "Binary Search", "https://leetcode.com/problems/koko-eating-bananas/"),
            create("Capacity To Ship Packages", Difficulty.MEDIUM, "Binary Search", "https://leetcode.com/problems/capacity-to-ship-packages/"),
            create("First Bad Version", Difficulty.EASY, "Binary Search", "https://leetcode.com/problems/first-bad-version/"),
            create("Find Peak Element", Difficulty.MEDIUM, "Binary Search", "https://leetcode.com/problems/find-peak-element/"),
            create("Single Element in Sorted Array", Difficulty.MEDIUM, "Binary Search", "https://leetcode.com/problems/single-element-in-sorted-array/"),

            // Heap
            create("Kth Largest Element in an Array", Difficulty.MEDIUM, "Heap", "https://leetcode.com/problems/kth-largest-element-in-an-array/"),
            create("Top K Frequent Elements", Difficulty.MEDIUM, "Heap", "https://leetcode.com/problems/top-k-frequent-elements/"),
            create("Merge K Sorted Lists", Difficulty.HARD, "Heap", "https://leetcode.com/problems/merge-k-sorted-lists/"),
            create("Find Median from Data Stream", Difficulty.HARD, "Heap", "https://leetcode.com/problems/find-median-from-data-stream/"),
            create("Task Scheduler", Difficulty.MEDIUM, "Heap", "https://leetcode.com/problems/task-scheduler/"),
            create("Last Stone Weight", Difficulty.EASY, "Heap", "https://leetcode.com/problems/last-stone-weight/"),
            create("K Closest Points to Origin", Difficulty.MEDIUM, "Heap", "https://leetcode.com/problems/k-closest-points-to-origin/"),
            create("Meeting Rooms II", Difficulty.MEDIUM, "Heap", "https://leetcode.com/problems/meeting-rooms-ii/"),
            create("Smallest Range", Difficulty.HARD, "Heap", "https://leetcode.com/problems/smallest-range/"),
            create("IPO", Difficulty.HARD, "Heap", "https://leetcode.com/problems/ipo/"),

            // Backtracking
            create("Subsets", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/subsets/"),
            create("Permutations", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/permutations/"),
            create("Combination Sum", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/combination-sum/"),
            create("Combination Sum II", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/combination-sum-ii/"),
            create("Letter Combinations of a Phone Number", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/letter-combinations-of-a-phone-number/"),
            create("N-Queens", Difficulty.HARD, "Backtracking", "https://leetcode.com/problems/n-queens/"),
            create("Word Search", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/word-search/"),
            create("Palindrome Partitioning", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/palindrome-partitioning/"),
            create("Generate Parentheses", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/generate-parentheses/"),
            create("Restore IP Addresses", Difficulty.MEDIUM, "Backtracking", "https://leetcode.com/problems/restore-ip-addresses/")
        );

        problemRepository.saveAll(problems);
    }

    private Problem create(String title, Difficulty difficulty, String topic, String link) {
        return Problem.builder()
                .title(title)
                .difficulty(difficulty)
                .topic(topic)
                .link(link)
                .build();
    }
}
