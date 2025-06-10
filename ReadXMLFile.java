import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ReadXMLFile {
public void calcularBetweennessCentrality(Graph G) {
    // Verifica se o grafo tem vértices
    if (G.V() == 0) {
        System.err.println("Grafo vazio - nenhum vértice para calcular");
        return;
    }

    try (PrintWriter writer = new PrintWriter(new FileWriter("betweenness_centrality.txt"))) {
        int V = G.V();
        double[] centrality = new double[V];

        for (int s = 0; s < V; s++) {
            // BFS initialization
            int[] distances = new int[V];
            int[] numShortestPaths = new int[V];
            int[][] predecessors = new int[V][V]; // Max V predecessors per node
            int[] predCount = new int[V]; // Count of predecessors per node
            
            Arrays.fill(distances, -1);
            distances[s] = 0;
            numShortestPaths[s] = 1;

            // Queue for BFS
            int[] queue = new int[V];
            int front = 0, rear = 0;
            queue[rear++] = s;

            // Stack for processing
            int[] stack = new int[V];
            int top = -1;

            // Forward pass (BFS)
            while (front < rear) {
                int v = queue[front++];
                stack[++top] = v;

                for (int w : G.adj(v)) {
                    // If discovering w for the first time
                    if (distances[w] < 0) {
                        distances[w] = distances[v] + 1;
                        queue[rear++] = w;
                    }
                    // If this is a shortest path to w through v
                    if (distances[w] == distances[v] + 1) {
                        numShortestPaths[w] += numShortestPaths[v];
                        predecessors[w][predCount[w]++] = v;
                    }
                }
            }

            // Backward pass (dependency accumulation)
            double[] dependency = new double[V];
            while (top >= 0) {
                int w = stack[top--];
                for (int i = 0; i < predCount[w]; i++) {
                    int v = predecessors[w][i];
                    double fraction = (double) numShortestPaths[v] / numShortestPaths[w];
                    dependency[v] += fraction * (1 + dependency[w]);
                }
                if (w != s) {
                    centrality[w] += dependency[w];
                }
            }
        }

        // Output results (same format as excentricidade)
        for (int v = 0; v < V; v++) {
            double finalValue = centrality[v] / 2; // Undirected graph adjustment
            String linhaSaida = String.format("Betweenness Centrality do Vértice %d: %.4f", v, finalValue);
            System.out.println(linhaSaida);
            writer.println(linhaSaida);
        }

        System.out.println("\n=========================================\n");

    } catch (Exception e) {
        System.err.println("Erro ao escrever no arquivo de betweenness centrality: " + e.getMessage());
        e.printStackTrace();
    }
}

    public static void main(String[] args) {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Graph graph = null;

        try {
            File file = new File("LesMiserables.gexf");
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            // Total de vértices
            NodeList verticesNodeList = doc.getElementsByTagName("nodes");
            Node verticesNode = verticesNodeList.item(0);
            Element verticesElement = (Element) verticesNode;
            String countVerticesStr = verticesElement.getAttribute("count");
            int totalDeVertices = (int) Double.parseDouble(countVerticesStr);
            System.out.println("Total de Vértices: " + totalDeVertices);

            // Total de arestas
            NodeList arestasNodeList = doc.getElementsByTagName("edges");
            Node arestasNode = arestasNodeList.item(0);
            Element arestasElement = (Element) arestasNode;
            String countArestasStr = arestasElement.getAttribute("count");
            int totalDeArestas = (int) Double.parseDouble(countArestasStr);
            System.out.println("Total de Arestas: " + totalDeArestas);

            // Instanciar o grafo
            graph = new Graph(totalDeVertices);

            // Adicionar as arestas ao grafo
            NodeList edgeList = doc.getElementsByTagName("edge");

            for (int i = 0; i < edgeList.getLength(); i++){
                Node node = edgeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element edge = (Element) node;
                    String source = edge.getAttribute("source");
                    String target = edge.getAttribute("target");

                    int sourceVertex = (int) Double.parseDouble(source);
                    int targetVertex = (int) Double.parseDouble(target);
                    graph.addEdge(sourceVertex, targetVertex);
                }
            }

            // Chamar as funções de cálculo e salvamento em arquivo
            ReadXMLFile reader = new ReadXMLFile();
            if (graph.V() > 0) {
                reader.calcularBetweennessCentrality(graph);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Falhou ao ler o arquivo XML e construir o grafo!");
        }
    }
}