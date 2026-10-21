package shortestpath;

import graph.Graph;
import graph.Edge;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {
    public static class PathResult {
        public int[] distance;
        public int[] previous;
        public long operations;

        PathResult(int[] distance, int[] previous, long operations) {
            this.distance = distance;
            this.previous = previous;
            this.operations = operations;
        }

        public List<Integer> pathTo(int target) {
            ArrayList<Integer> path = new ArrayList<>();
            if (distance[target] == Integer.MAX_VALUE) {
                return path;
            }
            int current = target;
            while (current != -1) {
                path.add(current);
                current = previous[current];
            }
            Collections.reverse(path);
            return path;
        }
    }

    public static PathResult shortestPaths(Graph graph, int start) {
        if (start < 0 || start >= graph.vertices) {
            throw new IllegalArgumentException("Invalid starting vertex");
        }
        for (Edge edge : graph.edges) {
            if (edge.weight < 0) {
                throw new IllegalArgumentException("Dijkstra needs non-negative weights");
            }
        }

        int[] distance = new int[graph.vertices];
        int[] previous = new int[graph.vertices];
        Arrays.fill(distance, Integer.MAX_VALUE);
        Arrays.fill(previous, -1);
        distance[start] = 0;
        long[] comparisons = {0};
        PriorityQueue<int[]> queue = new PriorityQueue<>((a, b) -> {
            comparisons[0]++;
            return Integer.compare(a[1], b[1]);
        });
        queue.add(new int[]{start, 0});
        long checked = 0;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            checked++;
            int vertex = current[0];
            if (current[1] != distance[vertex]) {
                continue;
            }
            for (Edge edge : graph.adjacency.get(vertex)) {
                checked++;
                int newDistance = distance[vertex] + edge.weight;
                if (newDistance < distance[edge.to]) {
                    distance[edge.to] = newDistance;
                    previous[edge.to] = vertex;
                    queue.add(new int[]{edge.to, newDistance});
                }
            }
        }
        return new PathResult(distance, previous, checked + comparisons[0]);
    }
}
