import graph.Graph;
import graph.Edge;
import graph.GraphFactory;
import mst.MST;
import connectivity.Connectivity;
import shortestpath.Dijkstra;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.function.Supplier;

public class Main {
    static void measure(PrintWriter writer, String name, String graphType,
                        int vertices, int edges, Supplier<Long> algorithm) {
        for (int i = 0; i < 3; i++) {
            algorithm.get();
        }
        long totalTime = 0;
        long operations = 0;
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            operations = algorithm.get();
            totalTime += System.nanoTime() - start;
        }
        double averageMs = totalTime / 5.0 / 1000000.0;
        writer.printf(Locale.US, "%s,%s,%d,%d,%.6f,%d%n",
                name, graphType, vertices, edges, averageMs, operations);
        System.out.printf(Locale.US, "%-18s V=%4d E=%5d  time=%8.4f ms  ops=%d%n",
                name, vertices, edges, averageMs, operations);
    }

    static void runGraphSet(PrintWriter writer, int vertices, int edges) {
        Graph roads = GraphFactory.undirected(vertices, edges);
        Graph dag = GraphFactory.dag(vertices, edges);
        Graph cyclic = GraphFactory.cyclic(vertices, edges);
        measure(writer, "Kruskal", "Undirected", vertices, edges,
                () -> MST.kruskal(roads).operations);
        measure(writer, "Prim", "Undirected", vertices, edges,
                () -> MST.prim(roads).operations);
        measure(writer, "Dijkstra", "Undirected", vertices, edges,
                () -> Dijkstra.shortestPaths(roads, 0).operations);
        measure(writer, "Kosaraju", "Cyclic directed", vertices, edges,
                () -> Connectivity.kosaraju(cyclic).operations);
        measure(writer, "Topological DAG", "DAG", vertices, edges,
                () -> Connectivity.topologicalSort(dag).operations);
        measure(writer, "Topological Cycle", "Cyclic directed", vertices, edges,
                () -> Connectivity.topologicalSort(cyclic).operations);
    }

    public static void main(String[] args) throws IOException {
        Tests.run();
        Graph example = GraphFactory.undirected(10, 30);
        MST.MSTResult kruskal = MST.kruskal(example);
        MST.MSTResult prim = MST.prim(example);
        Dijkstra.PathResult shortest = Dijkstra.shortestPaths(example, 0);
        System.out.println("City example:");
        System.out.println("Kruskal MST cost: " + kruskal.weight);
        System.out.println("Prim MST cost: " + prim.weight);
        System.out.println("Connected: " + kruskal.connected);
        System.out.println("Shortest routes from district 0:");
        for (int i = 0; i < example.vertices; i++) {
            String distance = shortest.distance[i] == Integer.MAX_VALUE
                    ? "unreachable" : String.valueOf(shortest.distance[i]);
            System.out.println("To " + i + ": " + distance + " via " + shortest.pathTo(i));
        }
        Graph oneWay = GraphFactory.dag(10, 30);
        Graph cycle = GraphFactory.cyclic(10, 30);
        System.out.println("DAG topological order: " + Connectivity.topologicalSort(oneWay).order);
        System.out.println("Cycle found: " + !Connectivity.topologicalSort(cycle).possible);
        System.out.println("Strongly connected components: " + Connectivity.kosaraju(cycle).components);
        System.out.println();

        new File("results/tables").mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter("results/tables/benchmark.csv"))) {
            writer.println("algorithm,graph_type,vertices,edges,average_ms,operations");
            int[] vertices = {10, 100, 1000};
            int[] edges = {30, 500, 5000};
            for (int i = 0; i < vertices.length; i++) {
                runGraphSet(writer, vertices[i], edges[i]);
            }
        }

        System.out.println("\nEdge sweep with V=100:");
        try (PrintWriter writer = new PrintWriter(new FileWriter("results/tables/edge_sweep.csv"))) {
            writer.println("algorithm,graph_type,vertices,edges,average_ms,operations");
            int[] edges = {150, 300, 500, 1000};
            for (int count : edges) {
                runGraphSet(writer, 100, count);
            }
        }
        System.out.println("\nSaved benchmark.csv and edge_sweep.csv");
    }
}
