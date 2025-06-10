import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final int V; // número de vértices
    private final List<Integer>[] adj; // listas de adjacência

    @SuppressWarnings("unchecked")
    public Graph(int V) {
        this.V = V;
        this.adj = (List<Integer>[]) new ArrayList[V];
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    // Retorna o número de vértices
    public int V() {
        return V;
    }

    // Adiciona uma aresta entre dois vértices (grafo não dirigido)
    public void addEdge(int v, int w) {
        adj[v].add(w);
        adj[w].add(v);
    }

    // Retorna a lista de adjacência de um vértice
    public Iterable<Integer> adj(int v) {
        return adj[v];
    }

    // Executa uma BFS a partir do vértice 's'
    // Retorna um array com as distâncias mínimas até todos os outros vértices
    public int[] bfs(int s) {
        int[] dist = new int[V];
        for (int i = 0; i < V; i++) {
            dist[i] = -1; // -1 indica que o vértice ainda não foi visitado
        }

        dist[s] = 0;
        List<Integer> fila = new ArrayList<>();
        fila.add(s);

        int inicio = 0;
        while (inicio < fila.size()) {
            int v = fila.get(inicio++);
            for (int w : adj[v]) {
                if (dist[w] == -1) {
                    dist[w] = dist[v] + 1;
                    fila.add(w);
                }
            }
        }

        return dist;
    }
}
