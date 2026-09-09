package com.shyamsunder.placement_prep_platform.config;

import com.shyamsunder.placement_prep_platform.entity.Difficulty;
import com.shyamsunder.placement_prep_platform.entity.Problem;
import com.shyamsunder.placement_prep_platform.entity.Role;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.repository.ProblemRepository;
import com.shyamsunder.placement_prep_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedAdminUser();
        seedProblems();
    }

    private void seedAdminUser() {
        if (userRepository.findByEmail("admin@placementprep.com").isEmpty()) {
            User admin = User.builder()
                    .name("System Administrator")
                    .email("admin@placementprep.com")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .branch("Computer Science")
                    .graduationYear(2026)
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
        }
    }

    private void seedProblems() {
        if (problemRepository.count() > 0) {
            return;
        }

        List<Problem> problems = List.of(
            // Arrays & Patterns
            create("Two Sum", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/two-sum/", "Two Pointers"),
            create("3Sum", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/3sum/", "Two Pointers"),
            create("Container With Most Water", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/container-with-most-water/", "Two Pointers"),
            create("Trapping Rain Water", Difficulty.HARD, "Arrays", "https://leetcode.com/problems/trapping-rain-water/", "Two Pointers"),
            create("Best Time to Buy and Sell Stock", Difficulty.EASY, "Arrays", "https://leetcode.com/problems/best-time-to-buy-and-sell-stock/", "Sliding Window"),
            create("Longest Substring Without Repeating Characters", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/longest-substring-without-repeating-characters/", "Sliding Window"),
            create("Minimum Size Subarray Sum", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/minimum-size-subarray-sum/", "Sliding Window"),
            create("Sliding Window Maximum", Difficulty.HARD, "Arrays", "https://leetcode.com/problems/sliding-window-maximum/", "Sliding Window"),
            create("Subarray Sum Equals K", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/subarray-sum-equals-k/", "Prefix Sum"),
            create("Product of Array Except Self", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/product-of-array-except-self/", "Prefix Sum"),
            create("Maximum Subarray", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/maximum-subarray/", "Kadane Algorithm"),
            create("Merge Intervals", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/merge-intervals/", "Intervals"),
            create("Insert Interval", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/insert-interval/", "Intervals"),
            create("Non-overlapping Intervals", Difficulty.MEDIUM, "Arrays", "https://leetcode.com/problems/non-overlapping-intervals/", "Intervals"),

            // Two Pointers & Fast/Slow Pointers
            create("Valid Palindrome", Difficulty.EASY, "Two Pointers", "https://leetcode.com/problems/valid-palindrome/", "Two Pointers"),
            create("Remove Duplicates from Sorted Array", Difficulty.EASY, "Two Pointers", "https://leetcode.com/problems/remove-duplicates-from-sorted-array/", "Two Pointers"),
            create("Move Zeroes", Difficulty.EASY, "Two Pointers", "https://leetcode.com/problems/move-zeroes/", "Two Pointers"),
            create("Sort Colors", Difficulty.MEDIUM, "Two Pointers", "https://leetcode.com/problems/sort-colors/", "Two Pointers"),
            create("4Sum", Difficulty.MEDIUM, "Two Pointers", "https://leetcode.com/problems/4sum/", "Two Pointers"),
            create("Linked List Cycle", Difficulty.EASY, "Two Pointers", "https://leetcode.com/problems/linked-list-cycle/", "Fast & Slow Pointers"),
            create("Linked List Cycle II", Difficulty.MEDIUM, "Two Pointers", "https://leetcode.com/problems/linked-list-cycle-ii/", "Fast & Slow Pointers"),
            create("Find the Duplicate Number", Difficulty.MEDIUM, "Two Pointers", "https://leetcode.com/problems/find-the-duplicate-number/", "Fast & Slow Pointers"),
            create("Happy Number", Difficulty.EASY, "Two Pointers", "https://leetcode.com/problems/happy-number/", "Fast & Slow Pointers"),
            create("Middle of the Linked List", Difficulty.EASY, "Two Pointers", "https://leetcode.com/problems/middle-of-the-linked-list/", "Fast & Slow Pointers"),

            // Linked List & Patterns
            create("Reverse Linked List", Difficulty.EASY, "Linked List", "https://leetcode.com/problems/reverse-linked-list/", "In-place Reversal"),
            create("Reverse Linked List II", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/reverse-linked-list-ii/", "In-place Reversal"),
            create("Merge Two Sorted Lists", Difficulty.EASY, "Linked List", "https://leetcode.com/problems/merge-two-sorted-lists/", "In-place Reversal"),
            create("Remove Nth Node From End of List", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/remove-nth-node-from-end-of-list/", "Two Pointers"),
            create("Reorder List", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/reorder-list/", "In-place Reversal"),
            create("LRU Cache", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/lru-cache/", "In-place Reversal"),
            create("Palindrome Linked List", Difficulty.EASY, "Linked List", "https://leetcode.com/problems/palindrome-linked-list/", "Fast & Slow Pointers"),
            create("Copy List with Random Pointer", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/copy-list-with-random-pointer/", "In-place Reversal"),
            create("Add Two Numbers", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/add-two-numbers/", "In-place Reversal"),
            create("Flatten a Multilevel Doubly Linked List", Difficulty.MEDIUM, "Linked List", "https://leetcode.com/problems/flatten-a-multilevel-doubly-linked-list/", "In-place Reversal"),

            // Tree & Patterns
            create("Maximum Depth of Binary Tree", Difficulty.EASY, "Tree", "https://leetcode.com/problems/maximum-depth-of-binary-tree/", "Tree DFS"),
            create("Same Tree", Difficulty.EASY, "Tree", "https://leetcode.com/problems/same-tree/", "Tree DFS"),
            create("Invert Binary Tree", Difficulty.EASY, "Tree", "https://leetcode.com/problems/invert-binary-tree/", "Tree DFS"),
            create("Binary Tree Level Order Traversal", Difficulty.MEDIUM, "Tree", "https://leetcode.com/problems/binary-tree-level-order-traversal/", "Tree BFS"),
            create("Lowest Common Ancestor of a Binary Tree", Difficulty.MEDIUM, "Tree", "https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/", "Tree DFS"),
            create("Validate Binary Search Tree", Difficulty.MEDIUM, "Tree", "https://leetcode.com/problems/validate-binary-search-tree/", "Tree DFS"),
            create("Kth Smallest Element in a BST", Difficulty.MEDIUM, "Tree", "https://leetcode.com/problems/kth-smallest-element-in-a-bst/", "Tree DFS"),
            create("Construct Binary Tree from Preorder and Inorder Traversal", Difficulty.MEDIUM, "Tree", "https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/", "Tree DFS"),
            create("Binary Tree Maximum Path Sum", Difficulty.HARD, "Tree", "https://leetcode.com/problems/binary-tree-maximum-path-sum/", "Tree DFS"),
            create("Serialize and Deserialize Binary Tree", Difficulty.HARD, "Tree", "https://leetcode.com/problems/serialize-and-deserialize-binary-tree/", "Tree BFS"),
            create("Subtree of Another Tree", Difficulty.EASY, "Tree", "https://leetcode.com/problems/subtree-of-another-tree/", "Tree DFS"),
            create("Diameter of Binary Tree", Difficulty.EASY, "Tree", "https://leetcode.com/problems/diameter-of-binary-tree/", "Tree DFS"),

            // Graph & Patterns
            create("Number of Islands", Difficulty.MEDIUM, "Graph", "https://leetcode.com/problems/number-of-islands/", "Graph BFS/DFS"),
            create("Clone Graph", Difficulty.MEDIUM, "Graph", "https://leetcode.com/problems/clone-graph/", "Graph BFS/DFS"),
            create("Max Area of Island", Difficulty.MEDIUM, "Graph", "https://leetcode.com/problems/max-area-of-island/", "Graph BFS/DFS"),
            create("Course Schedule", Difficulty.MEDIUM, "Graph", "https://leetcode.com/problems/course-schedule/", "Topological Sort"),
            create("Course Schedule II", Difficulty.MEDIUM, "Graph", "https://leetcode.com/problems/course-schedule-ii/", "Topological Sort"),
            create("Rotting Oranges", Difficulty.MEDIUM, "Graph", "https://leetcode.com/problems/rotting-oranges/", "Graph BFS/DFS"),
            create("Pacific Atlantic Water Flow", Difficulty.MEDIUM, "Graph", "https://leetcode.com/problems/pacific-atlantic-water-flow/", "Graph BFS/DFS"),
            create("Surrounded Regions", Difficulty.MEDIUM, "Graph", "https://leetcode.com/problems/surrounded-regions/", "Graph BFS/DFS"),
            create("Word Ladder", Difficulty.HARD, "Graph", "https://leetcode.com/problems/word-ladder/", "Graph BFS/DFS"),
            create("Network Delay Time", Difficulty.MEDIUM, "Graph", "https://leetcode.com/problems/network-delay-time/", "Graph BFS/DFS"),

            // Dynamic Programming & Patterns
            create("Climbing Stairs", Difficulty.EASY, "Dynamic Programming", "https://leetcode.com/problems/climbing-stairs/", "Dynamic Programming"),
            create("Coin Change", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/coin-change/", "Dynamic Programming"),
            create("House Robber", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/house-robber/", "Dynamic Programming"),
            create("House Robber II", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/house-robber-ii/", "Dynamic Programming"),
            create("Longest Palindromic Substring", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/longest-palindromic-substring/", "Dynamic Programming"),
            create("Palindromic Substrings", Difficulty.MEDIUM, "Dynamic Programming", "https://leetcode.com/problems/palindromic-substrings/", "Dynamic Programming"),
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
