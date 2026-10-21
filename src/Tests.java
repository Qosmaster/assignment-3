import graph.Graph;
import graph.Edge;
import graph.GraphFactory;
import mst.MST;
import connectivity.Connectivity;
import shortestpath.Dijkstra;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Tests {
    static int passed = 0;

    static void check(boolean correct, String name) {
        if (!correct) {
            throw new RuntimeException("FAILED: " + name);
        }
        passed++;
        System.out.println("PASS: " + name);
    }

    static boolean validOrder(Graph graph, List<Integer> order) {
        if (order.size() != graph.vertices) {
            return false;
        }
        int[] position = new int[graph.vertices];
        Arrays.fill(position, -1);
        for (int i = 0; i < order.size(); i++) {
            int vertex = order.get(i);
            if (position[vertex] != -1) {
                return false;
            }
            position[vertex] = i;
        }
        for (Edge edge : graph.edges) {
            if (position[edge.from] >= position[edge.to]) {
                return false;
            }
        }
        return true;
    }

    static boolean validComponents(Graph graph, List<List<Integer>> components) {
        HashSet<Integer> seen = new HashSet<>();
        for (List<Integer> group : components) {
            if (group.isEmpty()) {
                return false;
            }
            for (int vertex : group) {
                if (vertex < 0 || vertex >= graph.vertices || !seen.add(vertex)) {
                    return false;
                }
            }
        }
        return seen.size() == graph.vertices;
    }

    static void run() {
        passed = 0;
        Graph one = new Graph(1, false);
        check(MST.kruskal(one).connected && MST.kruskal(one).weight == 0
                && MST.prim(one).connected && MST.prim(one).weight == 0,
                "Single vertex MST");
        check(Dijkstra.shortestPaths(one, 0).distance[0] == 0, "Single vertex Dijkstra");
        Graph oneDirected = new Graph(1, true);
        check(validOrder(oneDirected, Connectivity.topologicalSort(oneDirected).order)
                && Connectivity.kosaraju(oneDirected).components.size() == 1,
                "Single vertex directed graph");

        Graph road = new Graph(4, false);
        road.addEdge(0, 1, 1);
        road.addEdge(1, 2, 2);
        road.addEdge(2, 3, 3);
        road.addEdge(0, 3, 10);
        road.addEdge(0, 2, 4);
        check(MST.kruskal(road).weight == 6 && MST.prim(road).weight == 6,
                "Correct MST weight");
        Dijkstra.PathResult paths = Dijkstra.shortestPaths(road, 0);
        check(paths.distance[3] == 6 && paths.pathTo(3).equals(Arrays.asList(0, 1, 2, 3)),
                "Correct shortest distance and path");

        Graph disconnected = new Graph(3, false);
        disconnected.addEdge(0, 1, 5);
        check(!MST.kruskal(disconnected).connected && !MST.prim(disconnected).connected,
                "Disconnected MST detection");
        check(Dijkstra.shortestPaths(disconnected, 0).distance[2] == Integer.MAX_VALUE
                && Dijkstra.shortestPaths(disconnected, 0).pathTo(2).isEmpty(),
                "Unreachable vertex");

        Graph equalWeights = new Graph(4, false);
        equalWeights.addEdge(0, 1, 1);
        equalWeights.addEdge(1, 2, 1);
        equalWeights.addEdge(2, 3, 1);
        equalWeights.addEdge(3, 0, 1);
        check(MST.kruskal(equalWeights).weight == 3
                && MST.prim(equalWeights).weight == 3,
                "Multiple valid MSTs and duplicate weights");

        Graph dag = new Graph(4, true);
        dag.addEdge(0, 1, 1);
        dag.addEdge(0, 2, 1);
        dag.addEdge(1, 3, 1);
        dag.addEdge(2, 3, 1);
        check(Connectivity.topologicalSort(dag).possible
                && validOrder(dag, Connectivity.topologicalSort(dag).order),
                "Valid DAG topological ordering");

        Graph cycle = new Graph(3, true);
        cycle.addEdge(0, 1, 1);
        cycle.addEdge(1, 2, 1);
        cycle.addEdge(2, 0, 1);
        check(!Connectivity.topologicalSort(cycle).possible, "Cycle detection");
        check(Connectivity.kosaraju(cycle).components.size() == 1
                && Connectivity.kosaraju(cycle).components.get(0).size() == 3,
                "SCC on a directed cycle");

        Graph components = new Graph(5, true);
        components.addEdge(0, 1, 1);
        components.addEdge(1, 0, 1);
        components.addEdge(1, 2, 1);
        components.addEdge(2, 3, 1);
        components.addEdge(3, 2, 1);
        components.addEdge(3, 4, 1);
        Connectivity.SCCResult scc = Connectivity.kosaraju(components);
        check(scc.components.size() == 3 && validComponents(components, scc.components),
                "Three SCCs and every vertex exactly once");

        Graph negative = new Graph(2, true);
        negative.addEdge(0, 1, -1);
        boolean rejected = false;
        try {
            Dijkstra.shortestPaths(negative, 0);
        } catch (IllegalArgumentException e) {
            rejected = true;
        }
        check(rejected, "Negative weights rejected by Dijkstra");

        int[] sizes = {10, 100, 1000};
        int[] edges = {30, 500, 5000};
        for (int i = 0; i < sizes.length; i++) {
            Graph city = GraphFactory.undirected(sizes[i], edges[i]);
            MST.MSTResult k = MST.kruskal(city);
            MST.MSTResult p = MST.prim(city);
            check(k.connected && p.connected && k.weight == p.weight,
                    "Matching MSTs for V=" + sizes[i]);
            Graph generatedDag = GraphFactory.dag(sizes[i], edges[i]);
            Connectivity.TopoResult t = Connectivity.topologicalSort(generatedDag);
            check(t.possible && validOrder(generatedDag, t.order),
                    "Valid generated DAG for V=" + sizes[i]);
            Graph generatedCycle = GraphFactory.cyclic(sizes[i], edges[i]);
            check(!Connectivity.topologicalSort(generatedCycle).possible,
                    "Generated cycle detected for V=" + sizes[i]);
            check(validComponents(generatedCycle, Connectivity.kosaraju(generatedCycle).components),
                    "Complete SCC partition for V=" + sizes[i]);
        }
        System.out.println("All " + passed + " tests passed.\n");
    }

    public static void main(String[] args) {
        run();
    }
}
