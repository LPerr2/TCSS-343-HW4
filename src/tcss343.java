package src;

import java.util.*;

public class tcss343 {

        /**
         * Main method that generates the sequence, calculates the target, and runs
         * the algorithms to find the subsequence that sums to the target.
         */
        public static void main(String[] args) {

            int[] rValues = {1_000, 1_000_000}; // Different ranges for random elements
            boolean[] vOptions = {true, false}; // Test for both TRUE and FALSE
            for (int range : rValues) {
                for (boolean v : vOptions) {
                    Driver(range, v);
                }
            }
        }

        /**
         * Helper method to test a given algorithm and measure its execution time and table space usage.
         */
        public static void Driver(int r, boolean v) {
            System.out.println("\nTesting with v = " + v + ", r = " + r);
            int n = 5; // Start with n = 5
            long maxTimeMs = 300_000; // Maximum allowed time in milliseconds

            while (true) {
                System.out.println("\nRunning tests for n = " + n);
                int[] S = generateRandomArray(n, r);
                int t = (v) ? generateTarget(S) : r + 1; // Generate t based on v (guaranteed or non-existent solution)

                // Run Brute Force Algorithm
                long startTime = System.currentTimeMillis();
                BF.Result bfResult = BF.BruteForce(S, t);
                long endTime = System.currentTimeMillis();
                long bfTimeMs = endTime - startTime;

                if (bfTimeMs > maxTimeMs) {
                    System.out.println("Brute Force exceeded time limit. Stopping tests for n = " + n);
                    break;
                }

                System.out.println("Brute Force Result: " + bfResult.exists);
                System.out.println("Brute Force Time: " + bfTimeMs + " ms");

                // Run Dynamic Programming Algorithm
                startTime = System.currentTimeMillis();
                DP.Result dpResult = DP.subsetSum(S, t);
                endTime = System.currentTimeMillis();
                long dpTimeMs = endTime - startTime;

                if (dpTimeMs > maxTimeMs) {
                    System.out.println("Dynamic Programming exceeded time limit. Stopping tests for n = " + n);
                    break;
                }

                System.out.println("Dynamic Programming Result: " + dpResult.found);
                System.out.println("Dynamic Programming Time: " + dpTimeMs + " ms");

                // Run Clever Algorithm
                startTime = System.currentTimeMillis();
                Clever.Result cleverResult = Clever.subsetSumClever(S, t);
                endTime = System.currentTimeMillis();
                long cleverTimeMs = endTime - startTime;

                if (cleverTimeMs > maxTimeMs) {
                    System.out.println("Clever Algorithm exceeded time limit. Stopping tests for n = " + n);
                    break;
                }

                System.out.println("Clever Algorithm Result: " + cleverResult.found);
                System.out.println("Clever Algorithm Time: " + cleverTimeMs + " ms");

                // Increase n for the next iteration
                n++;
            }

            System.out.println("Testing complete for v = " + v + ", r = " + r);
        }

        public static int[] generateRandomArray(int n, int r) {
            int[] S = new int[n];
            for (int i = 0; i < n; i++) {
                S[i] = (int) (Math.random() * r) + 1; // Random values from 1 to r
            }
            return S;
        }

        public static int generateTarget(int[] S) {
            int n = S.length;
            int subsetSize = (int) (Math.random() * n) + 1; // Random subset size
            int target = 0;

            for (int i = 0; i < subsetSize; i++) {
                target += S[(int) (Math.random() * n)];
            }

            return target;
        }

        /**
         * Generates a sequence of random integers of a specified size, where each
         * integer is sampled from the range 1 to the specified maximum value.
         * <p>
         * This method is used to create the random sequence of numbers that will be
         * used in the subset sum problem. Each element in the sequence is chosen
         * uniformly at random from the specified range.
         *
         * @param n the size of the sequence to generate
         * @param r the maximum value that any element in the sequence can take
         * @return an array of integers representing the random sequence
         */
        public static int[] generateRandomSequence(int n, int r) {
            Random rand = new Random();
            int[] S = new int[n];
            for (int i = 0; i < n; i++) {
                S[i] = rand.nextInt(r) + 1;
            }
            return S;
        }

        /**
         * Generates a target sum based on a random subset of the provided sequence.
         * The target sum is the sum of a random subset of the sequence, ensuring
         * that a valid solution (a subset of elements summing to the target) always exists.
         *
         * @param S the sequence of integers from which the target will be generated
         * @return the target sum, which is the sum of a random subset of the sequence
         */
        public static int generateTargetSum(int[] S) {
            Random rand = new Random();
            int sum = 0;
            int subsetSize = rand.nextInt(S.length) + 1; // Random subset size from 1 to n
            for (int i = 0; i < subsetSize; i++) {
                sum += S[rand.nextInt(S.length)];
            }
            return sum;
        }

        /**
         * Generates a target sum that is larger than the sum of all elements in the sequence,
         * ensuring that no subset of the sequence will sum up to this target.
         *
         * @param S the sequence of integers whose total sum will be used to generate the target
         * @return a target sum that is greater than the sum of all elements in the sequence
         */
        public static int generateLargeTarget(int[] S) {
            int totalSum = 0;
            for (int num : S) {
                totalSum += num;
            }
            return totalSum + new Random().nextInt(1000) + 1; // Ensure the target is larger
        }

    public static class DP {

        // Result class to store the outcome and indices of the subset
        public static class Result {
            boolean found;
            List<Integer> indices;

            public Result(boolean found, List<Integer> indices) {
                this.found = found;
                this.indices = indices;
            }

            @Override
            public String toString() {
                return "Result: " + found + ", Indices: " + indices;
            }
        }

        // Dynamic Programming solution for Subset Sum
        public static Result subsetSum(int[] S, int t) {
            int n = S.length;

            // DP table: dp[i][j] = true if subset of first i elements has sum j
            boolean[][] dp = new boolean[n + 1][t + 1];

            // Base case: A sum of 0 is always possible with the empty subset
            for (int i = 0; i <= n; i++) {
                dp[i][0] = true;
            }

            // Fill the DP table
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= t; j++) {
                    if (j >= S[i - 1]) {
                        dp[i][j] = dp[i - 1][j] || dp[i - 1][j - S[i - 1]];
                    } else {
                        dp[i][j] = dp[i - 1][j];
                    }
                }
            }

            // Check if a subset exists for the target sum
            if (!dp[n][t]) {
                return new Result(false, new ArrayList<>());
            }

            // Backtrack to find the indices of the subset
            List<Integer> indices = new ArrayList<>();
            int sum = t;
            for (int i = n; i > 0 && sum > 0; i--) {
                if (!dp[i - 1][sum]) {
                    indices.add(i - 1); // Include this element
                    sum -= S[i - 1];   // Reduce the remaining sum
                }
            }

            return new Result(true, indices);
        }
    }

    public static class Clever {

        // Result class to store boolean outcome and indices
        public static class Result {
            boolean found;
            List<Integer> indices;

            public Result(boolean found, List<Integer> indices) {
                this.found = found;
                this.indices = indices;
            }

            @Override
            public String toString() {
                return "Result: " + found + ", Indices: " + indices;
            }
        }

        // Clever Algorithm for Subset Sum
        public static Result subsetSumClever(int[] S, int t) {
            int n = S.length;

            // Split indices into two halves
            int mid = n / 2;
            List<Integer> L = new ArrayList<>();
            List<Integer> H = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (i <= mid) L.add(i);
                else H.add(i);
            }

            // Compute table T (all subsets of L)
            Map<Integer, List<Integer>> T = computeSubsets(S, L, t);

            // If any subset in T exactly equals t, return it
            for (Map.Entry<Integer, List<Integer>> entry : T.entrySet()) {
                if (entry.getKey() == t) {
                    return new Result(true, entry.getValue());
                }
            }

            // Compute table W (all subsets of H)
            Map<Integer, List<Integer>> W = computeSubsets(S, H, t);

            // If any subset in W exactly equals t, return it
            for (Map.Entry<Integer, List<Integer>> entry : W.entrySet()) {
                if (entry.getKey() == t) {
                    return new Result(true, entry.getValue());
                }
            }

            // Sort W by weight
            List<Map.Entry<Integer, List<Integer>>> sortedW = new ArrayList<>(W.entrySet());
            sortedW.sort(Comparator.comparingInt(Map.Entry::getKey));

            // For each subset in T, find the best match in W
            for (Map.Entry<Integer, List<Integer>> entryT : T.entrySet()) {
                int weightT = entryT.getKey();
                if (weightT > t) continue;

                // Binary search in sorted W for the best match
                int remaining = t - weightT;
                List<Integer> bestMatch = binarySearchForBestMatch(sortedW, remaining);
                if (bestMatch != null) {
                    List<Integer> combined = new ArrayList<>(entryT.getValue());
                    combined.addAll(bestMatch);
                    return new Result(true, combined);
                }
            }

            // No valid subsets found
            return new Result(false, new ArrayList<>());
        }

        // Helper to compute subsets and their sums for a given set of indices
        private static Map<Integer, List<Integer>> computeSubsets(int[] S, List<Integer> indices, int t) {
            Map<Integer, List<Integer>> subsets = new HashMap<>();
            int size = indices.size();
            int totalSubsets = 1 << size; // 2^size subsets

            for (int mask = 0; mask < totalSubsets; mask++) {
                int sum = 0;
                List<Integer> subset = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    if ((mask & (1 << i)) != 0) { // Check if the ith bit is set
                        int index = indices.get(i);
                        sum += S[index];
                        subset.add(index);
                    }
                }
                if (sum <= t) {
                    subsets.put(sum, subset);
                }
            }
            return subsets;
        }

        // Helper for binary search to find the best match in W
        private static List<Integer> binarySearchForBestMatch(List<Map.Entry<Integer, List<Integer>>> sortedW, int target) {
            int left = 0, right = sortedW.size() - 1;
            List<Integer> bestMatch = null;

            while (left <= right) {
                int mid = (left + right) / 2;
                int weight = sortedW.get(mid).getKey();

                if (weight == target) {
                    return sortedW.get(mid).getValue();
                } else if (weight < target) {
                    bestMatch = sortedW.get(mid).getValue(); // Update best match
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }

            return bestMatch;
        }
    }

    public static class BF {

        // Function to solve Subset Sum using Breadth-First Search approach
        public static Result BruteForce(int[] S, int t) {
            Set<Integer> partialSums = new HashSet<>();
            List<Integer> indices = new ArrayList<>();
            partialSums.add(0); // Start with the empty subset sum

            // Iterate over each number in the array
            for (int i = 0; i < S.length; i++) {
                int num = S[i];
                Set<Integer> newSums = new HashSet<>();

                // Add current number to all existing partial sums
                for (int partialSum : partialSums) {
                    int newSum = partialSum + num;

                    // If the target is achieved, collect the indices and return
                    if (newSum == t) {
                        indices.add(i);
                        return new Result(true, indices);
                    }

                    newSums.add(newSum);
                }

                // Merge new sums into partial sums and record index if necessary
                partialSums.addAll(newSums);
                indices.add(i);
            }

            // If no subset matches the target, return false
            return new Result(false, new ArrayList<>());
        }

        // Result class to store boolean outcome and indices
        public static class Result {
            boolean exists;
            List<Integer> indices;

            public Result(boolean exists, List<Integer> indices) {
                this.exists = exists;
                this.indices = indices;
            }

            @Override
            public String toString() {
                return "Result: " + exists;
            }
        }
    }
}
