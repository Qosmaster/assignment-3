package mst;

import graph.Graph;
import graph.Edge;
import dsu.DSU;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class MST {
    public static class MSTResult {
        public int weight;
        public boolean connected;
        public long operations;

        MSTResult(int weight, boolean connected, long operations) {
            this.weight = weight;
            this.connected = connected;
            this.operations = operations;
        }
    }

    public static MSTResult kruskal(Graph graph) {
        if (graph.directed) {
            throw new IllegalArgumentException("MST needs an undirected graph");
        }
        ArrayList<Edge> edges = new ArrayList<>(graph.edges);
        long[] comparisons = {0};
        edges.sort((a, b) -> {
            comparisons[0]++;
            return Integer.compare(a.weight, b.weight);
        });

        DSU dsu = new DSU(graph.vertices);
        int total = 0;
        int used = 0;
        long checked = 0;
        for (Edge edge : edges) {
            checked++;
            if (dsu.union(edge.from, edge.to)) {
                total += edge.weight;
                used++;
                if (used == graph.vertices - 1) {
                    break;
                }
            }
        }
        return new MSTResult(total, used == graph.vertices - 1,
                comparisons[0] + checked + dsu.operations);
    }

    public static MSTResult prim(Graph graph) {
        if (graph.directed) {
            throw new IllegalArgumentException("MST needs an undirected graph");
        }
        if (graph.vertices == 0) {
            return new MSTResult(0, true, 0);
        }
        boolean[] visited = new boolean[graph.vertices];
        long[] comparisons = {0};
        PriorityQueue<Edge> queue = new PriorityQueue<>((a, b) -> {
            comparisons[0]++;
            return Integer.compare(a.weight, b.weight);
        });
        queue.add(new Edge(-1, 0, 0));
        int count = 0;
        int total = 0;
        long checked = 0;

        while (!queue.isEmpty()) {
            Edge edge = queue.poll();
            checked++;
            if (visited[edge.to]) {
                continue;
            }
            visited[edge.to] = true;
            count++;
            total += edge.weight;
            for (Edge next : graph.adjacency.get(edge.to)) {
                checked++;
                if (!visited[next.to]) {
                    queue.add(next);
                }
            }
        }
        return new MSTResult(total, count == graph.vertices, checked + comparisons[0]);
    }
}
