import csv
from pathlib import Path
import matplotlib.pyplot as plt

root = Path(__file__).parent
plots = root / "results" / "plots"
plots.mkdir(parents=True, exist_ok=True)

names = ["Kruskal", "Prim", "Dijkstra", "Kosaraju", "Topological DAG", "Topological Cycle"]


def load_data(filename):
    with (root / "results" / "tables" / filename).open(newline="") as file:
        return list(csv.DictReader(file))


def draw(filename, x, output, title, subtitle):
    data = load_data(filename)
    plt.figure(figsize=(10, 6))
    for name in names:
        rows = sorted((r for r in data if r["algorithm"] == name), key=lambda r: int(r[x]))
        plt.plot([int(r[x]) for r in rows], [float(r["average_ms"]) for r in rows],
                 marker="o", linewidth=2, markersize=5, label=name)
    plt.title(title, fontsize=16, pad=17)
    plt.xlabel("Vertices (V)" if x == "vertices" else "Edges (E)")
    plt.ylabel("Average time (milliseconds)")
    plt.grid(True, alpha=0.3)
    plt.legend(loc="upper left", fontsize=9)
    if x == "vertices":
        plt.xscale("log")
        plt.xticks([10, 100, 1000], ["10", "100", "1000"])
    else:
        plt.xticks([150, 300, 500, 1000])
    plt.gcf().text(0.5, 0.01, subtitle, ha="center", fontsize=9)
    plt.tight_layout(rect=[0, 0.04, 1, 1])
    plt.savefig(plots / output, dpi=180)
    plt.close()


draw("benchmark.csv", "vertices", "time_vs_vertices.png",
     "Execution time vs. number of vertices", "V/E: 10/30, 100/500, 1000/5000  |  5 timed runs each")
draw("edge_sweep.csv", "edges", "time_vs_edges.png",
     "Execution time vs. number of edges", "V = 100 fixed  |  E = 150, 300, 500, 1000  |  5 timed runs each")
print("Saved two plots in results/plots/")
