import java.util.*;

public class EmergencyAmbulanceOptimization 
{

    // Travel Cost Matrix (Adjacency Matrix)
    static int[][] costMatrix = 
    {
            {0, 15, 25, 35},
            {15, 0, 30, 28},
            {25, 30, 0, 20},
            {35, 28, 20, 0}
    };

    // Location names
    static String[] locations = 
    {
            "Hospital",
            "Emergency Location B",
            "Emergency Location C",
            "Emergency Location D"
    };
    
    // ============================================
    // Greedy Route Optimization
    // ============================================
    public static String greedyEAROP(int[][] dist) 
    {
        int n = dist.length, current = 0, total = 0;
        boolean[] visited = new boolean[n];
        visited[0] = true;
        StringBuilder route = new StringBuilder(locations[0]);

        for (int count = 1; count < n; count++) 
        {
            int next = -1, min = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) 
            {
                if (!visited[i] && dist[current][i] < min) 
                {
                    min = dist[current][i];
                    next = i;
                }
            }
            visited[next] = true;
            total += dist[current][next];
            current = next;
            route.append(" -> ").append(locations[current]);
        }
        total += dist[current][0];
        route.append(" -> ").append(locations[0]);
        return "Greedy Ambulance Route: " + route + " | Total Cost: " + total;
    }
    
    // ============================================
    // Dynamic Programming Route Optimization
    // ============================================
    public static String dynamicProgrammingEAROP(int[][] dist) 
    {
        int n = dist.length;
        int VISITED_ALL = (1 << n) - 1;
        int[][] memo = new int[n][1 << n];
        String[][] paths = new String[n][1 << n];
        for (int[] row : memo)
        {
            Arrays.fill(row, -1);
        }
        int cost = dynamicProgrammingEAROPHelper(0, 1, dist, memo, VISITED_ALL, paths);
        String route = locations[0] + paths[0][1] + " -> " + locations[0];
        return "Dynamic Programming Ambulance Route: " + route + " | Total Cost: " + cost;
    }

    private static int dynamicProgrammingEAROPHelper(
    int pos, int mask, int[][] dist, int[][] memo,
    int VISITED_ALL, String[][] paths) 
    {
        if (mask == VISITED_ALL) 
        {
            paths[pos][mask] = "";
            return dist[pos][0];
        }
        if (memo[pos][mask] != -1) 
        {
            return memo[pos][mask];
        }
        int best = Integer.MAX_VALUE;
        String bestPath = "";
        for (int city = 1; city < dist.length; city++) 
        {
            if ((mask & (1 << city)) == 0) 
            {
                int newMask = mask | (1 << city);
                int cost = dist[pos][city] +
                           dynamicProgrammingEAROPHelper(city, newMask, dist, memo,
                           VISITED_ALL, paths);
                if (cost < best) 
                {
                    best = cost;
                    bestPath = " -> " + locations[city] + paths[city][newMask];
                }
            }
        }
        memo[pos][mask] = best;
        paths[pos][mask] = bestPath;
        return best;
    }

    static int bestBacktrackingCost;
    static String bestBacktrackingPath;
    
    // ============================================
    // Backtracking Route Optimization
    // ============================================
    public static String backtrackingEAROP(int[][] dist) 
    {
        boolean[] visited = new boolean[dist.length];
        visited[0] = true;
        bestBacktrackingCost = Integer.MAX_VALUE;
        bestBacktrackingPath = "";
        StringBuilder path = new StringBuilder(locations[0]);
        earopBacktracking(0, dist, visited, dist.length, 1, 0, path);
        return "Backtracking Ambulance Route: " + bestBacktrackingPath +
        " | Total Cost: " + bestBacktrackingCost;
    }

    private static int earopBacktracking(
    int pos, int[][] dist, boolean[] visited, int n,
    int count, int cost, StringBuilder path) 
    {

        if (count == n) 
        {
            int total = cost + dist[pos][0];
            if (total < bestBacktrackingCost) 
            {
                bestBacktrackingCost = total;
                bestBacktrackingPath = path + " -> " + locations[0];
            }
            return total;
        }

        int best = Integer.MAX_VALUE;
        for (int i = 1; i < n; i++) 
        {
            if (!visited[i]) 
            {
                visited[i] = true;
                int len = path.length();
                path.append(" -> ").append(locations[i]);
                best = Math.min(best, earopBacktracking(
                        i, dist, visited, n, count + 1,
                        cost + dist[pos][i], path));
                path.setLength(len);
                visited[i] = false;
            }
        }
        return best;
    }

    static int bestDivideCost;
    static String bestDividePath;
    
    // ============================================
    // Divide and Conquer Route Optimization
    // ============================================
    public static String divideAndConquerEAROP(int[][] dist) 
    {
        boolean[] visited = new boolean[dist.length];
        visited[0] = true;
        bestDivideCost = Integer.MAX_VALUE;
        bestDividePath = "";
        divideAndConquerHelper(0, visited, 0, dist, dist.length,
            new StringBuilder(locations[0]));
        return "Divide & Conquer Ambulance Route: " + bestDividePath +
        " | Total Cost: " + bestDivideCost;
    }

    private static int divideAndConquerHelper(
    int pos, boolean[] visited, int currentCost,
    int[][] dist, int n, StringBuilder path)
    {
        if (allVisited(visited)) 
        {
            int total = currentCost + dist[pos][0];
            if (total < bestDivideCost) 
            {
                bestDivideCost = total;
                bestDividePath = path + " -> " + locations[0];
            }
            return total;
        }

        int minimum = Integer.MAX_VALUE;
        for (int i = 1; i < n; i++) 
        {
            if (!visited[i]) 
            {
                visited[i] = true;
                int len = path.length();
                path.append(" -> ").append(locations[i]);
                minimum = Math.min(minimum, divideAndConquerHelper(
                        i, visited, currentCost + dist[pos][i],
                        dist, n, path));
                path.setLength(len);
                visited[i] = false;
            }
        }
        return minimum;
    }

    private static boolean allVisited(boolean[] visited) 
    {
        for (boolean value : visited)
        {
            if (!value) 
            {
                return false;
            }
        }
        return true;
    }
    
    // ============================================
    // Insertion Sort
    // ============================================
    public static String insertionSort(int[] arr) 
    {
        for (int i = 1; i < arr.length; i++) 
        {
            int key = arr[i], j = i - 1;
            while (j >= 0 && arr[j] > key) 
            {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
        return Arrays.toString(arr);
    }
    
    // ============================================
    // Binary Search
    // ============================================
    public static String binarySearch(int[] arr, int target) 
    {
        int left = 0, right = arr.length - 1;
        while (left <= right) 
        {
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) return String.valueOf(mid);
            if (arr[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return "-1";
    }
    
    // ============================================
    // Min-Heap
    // ============================================
    static class MinHeap 
    {
        private PriorityQueue<Integer> heap = new PriorityQueue<>();
        public void insert(int value) 
        { 
            heap.offer(value); 
        }

        public int extractMin() 
        {
            if (heap.isEmpty()) throw new NoSuchElementException("Heap is empty");
            return heap.poll();
        }
    }
    // ============================================
    // Splay Tree
    // ============================================
    static class SplayTree 
    {
        class Node 
        {
            int value;
            Node left, right;
            Node(int value) 
            { 
                this.value = value; 
            }
        }
        private Node root;

        private Node rotateRight(Node x) 
        {
            Node y = x.left;
            x.left = y.right;
            y.right = x;
            return y;
        }

        private Node rotateLeft(Node x) 
        {
            Node y = x.right;
            x.right = y.left;
            y.left = x;
            return y;
        }

        private Node splay(Node root, int value) 
        {
            if (root == null || root.value == value) 
            {
                return root;
            }
            if (value < root.value) 
            {
                if (root.left == null)
                {
                    return root;
                }
                if (value < root.left.value) 
                {
                    root.left.left = splay(root.left.left, value);
                    root = rotateRight(root);
                } 
                else if (value > root.left.value) 
                {
                    root.left.right = splay(root.left.right, value);
                    if (root.left.right != null)
                    {
                        root.left = rotateLeft(root.left);
                    }
                }
                return root.left == null ? root : rotateRight(root);
            } 
            else 
            {
                if (root.right == null)
                {
                    return root;
                }
                if (value > root.right.value) 
                {
                    root.right.right = splay(root.right.right, value);
                    root = rotateLeft(root);
                } 
                else if (value < root.right.value) 
                {
                    root.right.left = splay(root.right.left, value);
                    if (root.right.left != null)
                    {
                        root.right = rotateRight(root.right);
                    }
                }
                return root.right == null ? root : rotateLeft(root);
            }
        }

        public void insert(int value) 
        {
            if (root == null) 
            {
                root = new Node(value);
                return;
            }
            root = splay(root, value);
            if (root.value == value)
            {
            return;
            }
            Node node = new Node(value);
            if (value < root.value) 
            {
                node.right = root;
                node.left = root.left;
                root.left = null;
            } 
            else 
            {
                node.left = root;
                node.right = root.right;
                root.right = null;
            }
            root = node;
        }

        public boolean search(int value) 
        {
            root = splay(root, value);
            return root != null && root.value == value;
        }
    }
    // ============================================
    // Driver Method
    // ============================================
    public static void main(String[] args) 
    {
        System.out.println(greedyEAROP(costMatrix) + "\n");
        System.out.println(dynamicProgrammingEAROP(costMatrix) + "\n");
        System.out.println(backtrackingEAROP(costMatrix) + "\n");
        System.out.println(divideAndConquerEAROP(costMatrix) + "\n");

        int[] arr = {8, 3, 5, 1, 9, 2};
        insertionSort(arr);
        System.out.println("Sorted Emergency Response Times: " + Arrays.toString(arr) + "\n");
        System.out.println("Binary Search (Response Time 5 found at index): "
            + binarySearch(arr, 5) + "\n");

        MinHeap heap = new MinHeap();
        heap.insert(10); heap.insert(3); heap.insert(15);
        System.out.println("Min-Heap Extract Minimum Priority Value: " + heap.extractMin() + "\n");

        SplayTree tree = new SplayTree();
        tree.insert(20); tree.insert(10); tree.insert(30);
        System.out.println("Splay Tree Search (Emergency Case 10 found): " + tree.search(10));
    }
}
