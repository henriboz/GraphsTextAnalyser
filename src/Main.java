import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    private static Graph graph;
    private static int origin, destination, distance;
    private static final String BOOK = "C:\\Users\\Henrique Boz\\Desktop\\book.txt";
    private static final String PAJEK_DESTINATION_FOLDER_PATH = "F:\\temp";

    public static void main(String[] args) {
        menu();
    }
    
    public static void menu(){
        int choice = -1;
        while(choice != 0){
            clearConsole();
            System.out.println("1 - Read book");
            System.out.println("2 - Print Statistics");
            System.out.println("3 - Depth Search");
            System.out.println("4 - Breadth Search");
            System.out.println("5 - Find Neighbors At Distance");
            System.out.println("6 - Dijkstra");
            System.out.println("8 - Test Graph");
            System.out.println("9 - Print Vertices and Neighbors");
            System.out.println("10 - PRIM Minimum Cost Spanning Three");
            System.out.println("11 - Weakly connected");
            System.out.println("12 - Save Pajek to disk");
            System.out.print("Please enter your choice: ");
            Scanner scanner = new Scanner(System.in);
            choice = scanner.nextInt();

            switch (choice) {
                case 0:
                    break;
                case 1:
                    graph = new Graph(true);
                    readFiles();
                    break;
                case 2:
                    graph.printStatistics();
                    break;
                case 3:
                    readOrigin(scanner);
                    readDestination(scanner);
                    graph.depthSearch(origin,destination);
                    break;
                case 4:
                    readOrigin(scanner);
                    readDestination(scanner);
                    graph.breadthSearch(origin,destination);
                    break;
                case 5:
                    readOrigin(scanner);
                    readDistance(scanner);
                    graph.findNeighborsAtDistance(origin,distance);
                    break;
                case 6:
                    readOrigin(scanner);
                    readDestination(scanner);
                    graph.Dijkstra(origin,destination);
                    break;
                case 8:
                    loadTestGraph();
                    break;
                case 9:
                    graph.printVerticesAndNeighbors();
                    break;
                case 10:
                    readOrigin(scanner);
                    //Graph g = graph.primMinimumCostSpanningThree(origin);
                    graph.primMinimumCostSpanningThree(origin);
                    break;
                case 11:
                    for(Graph fraco: graph.fracamenteConectados()){
                        System.out.println("------------------------");
                        fraco.printVertices();
                    }
                    break;
                case 12:
                	graph.savePajekToDisk(PAJEK_DESTINATION_FOLDER_PATH);
                	break;
                default:
                    System.out.println("Invalid Option!");

            }
        }
    }

    public static void readFiles() {
        FileVisitor<Path> fileProcessor = new ProcessFile(graph);
        try {
            Files.walkFileTree(Paths.get(BOOK), fileProcessor);
        } catch (IOException e){
            System.out.println("ERROR - " + e);
        }
    }

    public static void loadTestGraph(){
        graph = new Graph(false);
        Vertex v0 = new Vertex("0");
        Vertex v1 = new Vertex("1");
        Vertex v2 = new Vertex("2");
        Vertex v3 = new Vertex("3");
        Vertex v4 = new Vertex("4");
        Vertex v5 = new Vertex("5");
        Vertex v6 = new Vertex("6");

        graph.addVertex(v0);
        graph.addVertex(v1);
        graph.addVertex(v2);
        graph.addVertex(v3);
        graph.addVertex(v4);
        graph.addVertex(v5);
        graph.addVertex(v6);

        graph.addNeighbor(v0,v1,1);
        graph.addNeighbor(v1,v2,1);
        graph.addNeighbor(v2,v6,1);
        graph.addNeighbor(v3,v4,1);
        graph.addNeighbor(v4,v5,1);
        graph.addNeighbor(v5,v6,1);
        graph.addNeighbor(v3,v6,1);
        graph.addNeighbor(v6,v4,1);
    }

    public static void readOrigin(Scanner scanner){
        System.out.print("Insert the origin Vertex: ");
        origin = scanner.nextInt();
    }
    
    public static void readDestination(Scanner scanner){
        System.out.print("Insert the destination Vertex: ");
        destination = scanner.nextInt();
    }
    
    public static void readDistance(Scanner scanner){
        System.out.print("Insert the distance: ");
        distance = scanner.nextInt();
    }

    public final static void clearConsole(){
        try{
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                //Runtime.getRuntime().exec("cls");
            	System.out.println("");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {}
    }
}