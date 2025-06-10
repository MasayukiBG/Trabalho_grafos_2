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
        if (G.V() == 0) {
        System.err.println("Grafo vazio - nenhum vértice para calcular");
        return;
    }
    
    try (PrintWriter escritor = new PrintWriter(new FileWriter("betweenness_centrality.txt"))) {
        int numVertices = G.V();
        double[] centralidade = new double[numVertices];
    
        for (int origem = 0; origem < numVertices; origem++) {
            // Inicialização da BFS
            int[] distancias = new int[numVertices];
            int[] numCaminhosMinimos = new int[numVertices];
            int[][] predecessores = new int[numVertices][numVertices]; // Máximo de predecessores por nó
            int[] contPredecessores = new int[numVertices]; // Contador de predecessores por nó
            
            Arrays.fill(distancias, -1);
            distancias[origem] = 0;
            numCaminhosMinimos[origem] = 1;
        
            // Fila para BFS
            int[] fila = new int[numVertices];
            int inicio = 0, fim = 0;
            fila[fim++] = origem;
        
            // Pilha para processamento
            int[] pilha = new int[numVertices];
            int topo = -1;
        
            // Fase forward (BFS)
            while (inicio < fim) {
                int verticeAtual = fila[inicio++];
                pilha[++topo] = verticeAtual;
            
                for (int vizinho : G.adj(verticeAtual)) {
                    // Se está descobrindo o vizinho pela primeira vez
                    if (distancias[vizinho] < 0) {
                        distancias[vizinho] = distancias[verticeAtual] + 1;
                        fila[fim++] = vizinho;
                    }
                    // Se este é um caminho mínimo para o vizinho através do vértice atual
                    if (distancias[vizinho] == distancias[verticeAtual] + 1) {
                        numCaminhosMinimos[vizinho] += numCaminhosMinimos[verticeAtual];
                        predecessores[vizinho][contPredecessores[vizinho]++] = verticeAtual;
                    }
                }
            }
            
            // Fase backward (acúmulo de dependência)
            double[] dependencia = new double[numVertices];
            while (topo >= 0) {
                int vertice = pilha[topo--];
                for (int i = 0; i < contPredecessores[vertice]; i++) {
                    int predecessor = predecessores[vertice][i];
                    double fracao = (double) numCaminhosMinimos[predecessor] / numCaminhosMinimos[vertice];
                    dependencia[predecessor] += fracao * (1 + dependencia[vertice]);
                }
                if (vertice != origem) {
                    centralidade[vertice] += dependencia[vertice];
                }
            }
        }
    
        // Escreve os resultados
        for (int v = 0; v < numVertices; v++) {
            double resultado = centralidade[v] / 2.0;
            String saida = String.format("Betweenness Centrality do Vértice %d: %.4f", v, resultado);
            System.out.println(saida);
            escritor.println(saida);
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
