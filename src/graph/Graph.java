package graph;

import java.util.ArrayList;

public class Graph {
    public int vertices;
    public boolean directed;
    public ArrayList<Edge> edges = new ArrayList<>();
    public ArrayList<ArrayList<Edge>> adjacency = new ArrayList<>();

    public Graph(int vertices, boolean directed) {
        this.vertices = vertices;
        this.directed = directed;
        for (int i = 0; i < vertices; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int weight) {
        if (from < 0 || from >= vertices || to < 0 || to >= vertices) {
            throw new IllegalArgumentException("Invalid vertex");
        }
        edges.add(new Edge(from, to, weight));
        adjacency.get(from).add(new Edge(from, to, weight));
        if (!directed) {
            adjacency.get(to).add(new Edge(to, from, weight));
        }
    }
}
