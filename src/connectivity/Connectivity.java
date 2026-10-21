package connectivity;

import graph.Graph;
import graph.Edge;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Connectivity {
    public static class TopoResult {
        public List<Integer> order;
        public boolean possible;
        public long operations;

        TopoResult(List<Integer> order, boolean possible, long operations) {
            this.order = order;
            this.possible = possible;
            this.operations = operations;
        }
    }

    public static class SCCResult {
        public List<List<Integer>> components;
        public long operations;

        SCCResult(List<List<Integer>> components, long operations) {
            this.components = components;
            this.operations = operations;
        }
    }

    public static TopoResult topologicalSort(Graph graph) {
        if (!graph.directed) {
            throw new IllegalArgumentException("Topological sort needs a directed graph");
        }
        int[] indegree = new int[graph.vertices];
        long operations = 0;
        for (Edge edge : graph.edges) {
            indegree[edge.to]++;
            operations++;
        }

        LinkedList<Integer> queue = new LinkedList<>();
        for (int i = 0; i < graph.vertices; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
            }
        }
        ArrayList<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int current = queue.removeFirst();
            order.add(current);
            operations++;
            for (Edge edge : graph.adjacency.get(current)) {
                indegree[edge.to]--;
                operations++;
                if (indegree[edge.to] == 0) {
                    queue.add(edge.to);
                }
            }
        }
        boolean possible = order.size() == graph.vertices;
        return new TopoResult(order, possible, operations);
    }

    public static void dfsFirst(Graph graph, int vertex, boolean[] visited,
                         List<Integer> order, long[] operations) {
        visited[vertex] = true;
        operations[0]++;
        for (Edge edge : graph.adjacency.get(vertex)) {
            operations[0]++;
            if (!visited[edge.to]) {
                dfsFirst(graph, edge.to, visited, order, operations);
            }
        }
        order.add(vertex);
    }

    public static void dfsSecond(Graph graph, int vertex, boolean[] visited,
                          List<Integer> component, long[] operations) {
        visited[vertex] = true;
        component.add(vertex);
        operations[0]++;
        for (Edge edge : graph.adjacency.get(vertex)) {
            operations[0]++;
            if (!visited[edge.to]) {
                dfsSecond(graph, edge.to, visited, component, operations);
            }
        }
    }

    public static SCCResult kosaraju(Graph graph) {
        if (!graph.directed) {
            throw new IllegalArgumentException("SCC needs a directed graph");
        }
        boolean[] visited = new boolean[graph.vertices];
        ArrayList<Integer> order = new ArrayList<>();
        long[] operations = {0};
        for (int i = 0; i < graph.vertices; i++) {
            if (!visited[i]) {
                dfsFirst(graph, i, visited, order, operations);
            }
        }

        Graph reversed = new Graph(graph.vertices, true);
        for (Edge edge : graph.edges) {
            reversed.addEdge(edge.to, edge.from, edge.weight);
            operations[0]++;
        }
        Arrays.fill(visited, false);
        ArrayList<List<Integer>> components = new ArrayList<>();
        for (int i = order.size() - 1; i >= 0; i--) {
            int vertex = order.get(i);
            if (!visited[vertex]) {
                ArrayList<Integer> component = new ArrayList<>();
                dfsSecond(reversed, vertex, visited, component, operations);
                components.add(component);
            }
        }
        return new SCCResult(components, operations[0]);
    }
}
