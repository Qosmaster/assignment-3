package graph;

import java.util.Random;

public class GraphFactory {
    static Random random(int vertices, int edges, int type) {
        return new Random(2026L + vertices * 31L + edges * 17L + type);
    }

    public static Graph undirected(int vertices, int edges) {
        if (vertices < 1 || edges < vertices - 1 || edges > vertices * (vertices - 1) / 2) {
            throw new IllegalArgumentException("Invalid graph size");
        }
        Graph graph = new Graph(vertices, false);
        boolean[][] used = new boolean[vertices][vertices];
        Random random = random(vertices, edges, 1);
        for (int i = 1; i < vertices; i++) {
            graph.addEdge(i - 1, i, 1 + random.nextInt(20));
            used[i - 1][i] = true;
            used[i][i - 1] = true;
        }
        while (graph.edges.size() < edges) {
            int a = random.nextInt(vertices);
            int b = random.nextInt(vertices);
            if (a != b && !used[a][b]) {
                graph.addEdge(a, b, 1 + random.nextInt(20));
                used[a][b] = true;
                used[b][a] = true;
            }
        }
        return graph;
    }

    public static Graph dag(int vertices, int edges) {
        if (vertices < 1 || edges < 0 || edges > vertices * (vertices - 1) / 2) {
            throw new IllegalArgumentException("Invalid DAG size");
        }
        Graph graph = new Graph(vertices, true);
        boolean[][] used = new boolean[vertices][vertices];
        Random random = random(vertices, edges, 2);
        while (graph.edges.size() < edges) {
            int a = random.nextInt(vertices);
            int b = random.nextInt(vertices);
            if (a < b && !used[a][b]) {
                graph.addEdge(a, b, 1 + random.nextInt(20));
                used[a][b] = true;
            }
        }
        return graph;
    }

    public static Graph cyclic(int vertices, int edges) {
        if (vertices < 3 || edges < 3 || edges > vertices * (vertices - 1)) {
            throw new IllegalArgumentException("Invalid cyclic graph size");
        }
        Graph graph = new Graph(vertices, true);
        boolean[][] used = new boolean[vertices][vertices];
        Random random = random(vertices, edges, 3);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        used[0][1] = true;
        used[1][2] = true;
        used[2][0] = true;
        while (graph.edges.size() < edges) {
            int a = random.nextInt(vertices);
            int b = random.nextInt(vertices);
            if (a != b && !used[a][b]) {
                graph.addEdge(a, b, 1 + random.nextInt(20));
                used[a][b] = true;
            }
        }
        return graph;
    }
}
