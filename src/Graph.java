import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Graph {

    private ArrayList<Vertex> vertices = new ArrayList<>();
    private boolean closure[][];
    private HashMap<Vertex,Integer> distances;
    private HashMap<Vertex,Vertex> path;
    private HashMap<Vertex,Boolean> visited;
    private Stack<Vertex> route;
    private Queue<Vertex> queue;
    private static final int INFINITE = Integer.MAX_VALUE;
    private boolean directed;
    private int lastDijkstraWeight = 0; 

    public Graph() {
    	this(false);
    }
    		
    public Graph(boolean directed){
        distances = new HashMap<>();
        visited = new HashMap<>();
        path = new HashMap<>();
        route = new Stack<>();
        queue = new LinkedList<>();
        this.directed = directed;
    }
    
    public int addVertex(Vertex v) {
        if (vertices.contains(v)) return vertices.indexOf(v);
        else{
            vertices.add(v);
            int index = vertices.indexOf(v);
            v.setIndex(index);
            return index;
        }
    }

    public boolean addNeighbor(Vertex originVertex, Vertex neighborVertex, int weight){
        return addNeighbor(originVertex.getIndex(), neighborVertex.getIndex(), weight);
    }

    public boolean addNeighbor(int vertexIndex, int neighborIndex, int weight){
        if(directed) {
            vertices.get(vertexIndex).addNeighborOrIncreaseWeight(vertices.get(neighborIndex), weight);
            /*System.out.println("Adding neighbour: " + vertices.get(vertexIndex).getName() + " -> " +
                    vertices.get(neighborIndex).getName());*/
        } else{
            vertices.get(vertexIndex).addNeighborOrIncreaseWeight(vertices.get(neighborIndex), weight);
            vertices.get(neighborIndex).addNeighborOrIncreaseWeight(vertices.get(vertexIndex), weight);
        }
        return true;
    }
    
    public void printVertices(){
        System.out.println(vertices);
    }
    
    public void printVerticesAndNeighbors(){
        for (Vertex v: vertices){
            v.printNeighbors();
            System.out.println("");
        }
    }

    public void printVertexAndNeighbors(int index){
        this.vertices.get(index).printNeighbors();
        System.out.println("");
    }

    public void printStatistics(){
        System.out.println("Number of Vertices: " + vertices.size());
        int edges = 0;
        HashMap<Vertex, Integer> countOutput = new HashMap<>();
        HashMap<Vertex, Integer> countInput = new HashMap<>();

        for(Vertex v: vertices){
            int neighbor = v.getNeighborsSize();
            countOutput.put(v, neighbor); //Conta quantidade de saída por vertices
            edges += neighbor;

            HashMap<Vertex,Integer> neighbors = v.getNeighbors();
            if (neighbors != null) {
                if (neighbors.size() > 0) {
                    for (HashMap.Entry<Vertex, Integer> entry : neighbors.entrySet()) {
                        if(countInput.containsKey(entry.getKey())){
                            countInput.put(entry.getKey(), countInput.get(entry.getKey()) + 1);
                        } else{
                            countInput.put(entry.getKey(),1);
                        }
                    }
                }
            }
        }
        if(directed) System.out.println("Number de Edges: " + edges);
        else System.out.println("Number de Edges: " + edges/2);

        //Ordena o Map para imprimir os 20 maiores
        Map<Vertex, Integer> resultCountOutput = countOutput.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        System.out.println("20 Vertices with biggest number of outputs: ");
        int count = 0;
        for(Map.Entry<Vertex, Integer> result: resultCountOutput.entrySet()){
            System.out.println("    " + result.getKey().getIndex() +" - " + result.getKey() + " - " + result.getValue());
            count++;
            if (count==20) break;
        }

        //Ordena o Map para imprimir os 20 maiores
        Map<Vertex, Integer> resultCountInput = countInput.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        System.out.println("20 Vertices with biggest number of inputs: ");
        count = 0;
        for(Map.Entry<Vertex, Integer> result: resultCountInput.entrySet()){
            System.out.println("    " + result.getKey().getIndex() +" - " + result.getKey() + " - " + result.getValue());
            count++;
            if (count==20) break;
        }
    }

    public boolean removeNeighbor(int vertexIndex, int neighborIndex){
        this.vertices.get(vertexIndex).removeNeighbor(this.vertices.get(neighborIndex));
        return true;
    }

    public int getNeighborsSizeFromVertex(int vertex){
        return this.vertices.get(vertex).getNeighborsSize();
    }

    public void Warshall(boolean printDebug){
        int size = vertices.size();

        closure = new boolean[size][size];
        //Constroi a primeira Matriz como Matriz de Adjacencia
        for (int i=0; i<size;i++){
            for (int j=0; j<size; j++){
                closure[i][j] = vertices.get(i).isNeighbor(vertices.get(j));
            }
        }

        //Executa o algoritmo de Warshall
        for (int k=0; k<size; k++){
            for (int i=0; i< size; i++){
                if (closure[i][k]){
                    for (int j=0; j<size; j++){
                        closure[i][j] = closure[i][j] || closure[k][j];
                    }
                }
            }
        }
        //Imprime
        if (printDebug) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    System.out.print(closure[i][j] + "-");
                }
                System.out.println("");
            }
        }
    }

    public boolean isReachable(int originIndex, int destinationIndex){
        return closure[originIndex][destinationIndex];
    }

    //Finds the non-visited position (visited=false) with the lowest value of distance)
    private Vertex findVertex(){
        Vertex foundVertex = null;
        int foundValue = INFINITE;
        for(Vertex v: vertices){
            if(!visited.containsKey(v)){
                if(distances.containsKey(v)){
                    if(distances.get(v) == 0){
                        foundVertex = v;
                        foundValue = 0;
                    }
                    else {
                        if ((distances.get(v)) < foundValue) {
                            foundVertex = v;
                            foundValue = (distances.get(v));
                        }
                    }
                }
            }
        }
        return foundVertex;
    }

    public void Dijkstra(int originIndex, int destinationIndex){
        Dijkstra(vertices.get(originIndex), vertices.get(destinationIndex));
    }
    
    public void Dijkstra(Vertex originVertex, Vertex destinationVertex){
        distances.clear();
        path.clear();
        visited.clear();
        route.clear();
        //Sets distance 0 for the origin Vertex
        distances.put(originVertex ,0);

        boolean search = true;
        Vertex currentVertex;
        while(search){

            //Finds the non-visited position (visited=false) with the lowest value of distance)
            Vertex foundVertex = findVertex();

            //If the foundVertex is equals to the destination, stops the search
            if(foundVertex == destinationVertex) search = false;

            //Sets the foundVertex as visited(=true) and load it into the current Vertex
            visited.put(foundVertex,true);
            currentVertex = foundVertex;

            //Analyses all the non visited neighbor vertices to the current
            HashMap<Vertex,Integer> neighbors = currentVertex.getNeighbors();
            if(neighbors != null)
                for (Map.Entry<Vertex,Integer> neighbor: neighbors.entrySet()){
                    //if(!visited.containsKey(neighbor.getKey())){

                        //If neighbor exists in the path...this is done to avoid if calculations with null
                        if(distances.containsKey(neighbor.getKey())) {

                            //If the current distance + neighbor weight is smaller than the distance to the neighbor
                            //Sets neighbor distance as = current distance + neighbor weight
                            if((distances.get(currentVertex) + neighbor.getValue()) < distances.get(neighbor.getKey())){
                                distances.put(neighbor.getKey(), distances.get(currentVertex) + neighbor.getValue());

                                //Sets the path to get to the neighbor (current)
                                path.put(neighbor.getKey(),currentVertex);

                            }
                        } else{
                            distances.put(neighbor.getKey(), distances.get(currentVertex) + neighbor.getValue());
                            path.put(neighbor.getKey(),currentVertex);
                        }
                    //}
                }

/*            System.out.println(distances);
            System.out.println(visited);
            System.out.println(path);
            System.out.println("--------");*/
        }

        //After the search is done prints the result
        //Starts backwards and use a stack to pop each node.
        currentVertex = destinationVertex;


        while (true){
            if(currentVertex == originVertex) {
                route.push(originVertex);
                break;
            }
            route.push(currentVertex);
            currentVertex = path.get(currentVertex);
        }
        /**
         * The correct weight is calculated here because when finding the largest path value
         * the neighbor two nodes away can miss the value update
         * Is the test graph, when node 3 is found as the largest path, node 1 is updated,
         * but node 2, 4 and 5 keeps with the old value. Resulting in the right path, but wrong
         * final weight.
         */
        int size = route.size();
        Vertex current = route.pop();
        int weight = 0;
        //System.out.print("Dijkstra Result: ");
        for(int i=0; i<size-1; i++){
            //System.out.print(current + "->");
            Vertex precursor = current;
            current = route.pop();
            weight += precursor.getNeighborWeight(current);
        }
        //System.out.print(current);
        //System.out.println(" - Weight: "+ weight);
        lastDijkstraWeight = weight;

        distances.clear();
        visited.clear();
        path.clear();
        route.clear();
    }

    public boolean depthSearch(int originVertex, int destinationVertex){
        return depthSearch(vertices.get(originVertex), vertices.get(destinationVertex));
    }

    public boolean depthSearch(Vertex originVertex, Vertex destinationVertex){
        if(originVertex == destinationVertex){
            route.add(originVertex);
            //System.out.println("Depth Search Result: " + route);
            route.clear();
            return true;
        }
        else{
            if(!route.contains(originVertex)){
               route.add(originVertex);
               for(Map.Entry<Vertex,Integer> neighbor: originVertex.getNeighbors().entrySet()){
                   if(depthSearch(neighbor.getKey(),destinationVertex)) return true;
               }
            }
        }
        return false;
    }

    public boolean breadthSearch(int originVertex, int destinationVertex){
        queue.add(vertices.get(originVertex));
        return breadthSearch(vertices.get(destinationVertex));
    }

    public boolean breadthSearch(Vertex destinationVertex){
        if(queue.size() == 0){
            return false;
        }
        else{
            Vertex v = queue.remove();
            route.add(v);
            if (v == destinationVertex){
                System.out.println("Breadth Search Result: " + route);
                queue.clear();
                route.clear();
                return true;
            }
            HashMap<Vertex,Integer> neighbors = v.getNeighbors();
            for(HashMap.Entry<Vertex,Integer> neighbor: neighbors.entrySet()){
                if(!route.contains(neighbor.getKey()) && !queue.contains(neighbor.getKey())){
                    queue.add(neighbor.getKey());
                }
            }
        }
        return breadthSearch(destinationVertex);
    }

    public void findNeighborsAtDistance(int originVertex, int distance){
        findNeighborsAtDistance(vertices.get(originVertex), distance);
    }

    public void findNeighborsAtDistance(Vertex originVertex, int distance){
        int count = 0;
        boolean search = true;
        Stack<Vertex> stack = new Stack<>();
        ArrayList<Vertex> result = new ArrayList<>();
        ArrayList<Vertex> checked = new ArrayList<>();
        stack.push(originVertex);
        checked.add(originVertex);

        while(search){
            count ++;
            while(!stack.isEmpty()) {
                HashMap<Vertex,Integer> neighbors = stack.pop().getNeighbors();
                for (HashMap.Entry<Vertex, Integer> neighbor : neighbors.entrySet()) {
                    if(!checked.contains(neighbor.getKey()) && !result.contains(neighbor.getKey())) result.add(neighbor.getKey());
                    checked.add(neighbor.getKey());
                }

            }
            if(count == distance) search = false;
            else {
                stack.addAll(result);
                result.clear();
            }
        }
        System.out.println("Neighbors at distance " + distance + ": " + result);
    }

    public Graph primMinimumCostSpanningThree(int originIndex){
        return primMinimumCostSpanningThree(vertices.get(originIndex));
    }

    public Graph primMinimumCostSpanningThree(Vertex originVertex){
        ArrayList<Vertex> visited = new ArrayList<>();

        Graph result = new Graph(false);
        for(Vertex v: vertices){
            Vertex vCopy = new Vertex(v.getName());
            result.addVertex(vCopy);
        }
        result.addVertex(originVertex);

        Vertex current = originVertex;
        int totalWeight = 0;
        while (visited.size() != vertices.size()){
            visited.add(current);
            //System.out.println(visited);

            int foundValue = INFINITE;
            Vertex foundVertex = null;
            Vertex foundOrigin = null;
            for(Vertex v: visited){
                HashMap<Vertex,Integer> neighbors = v.getNeighbors();

                for (Map.Entry<Vertex, Integer> neighbor: neighbors.entrySet()){
                    if(!visited.contains(neighbor.getKey())) {
                        if (neighbor.getValue() < foundValue) {
                            foundValue = neighbor.getValue();
                            foundVertex = neighbor.getKey();
                            foundOrigin = v;
                        }
                    }
                }
            }
            if(foundVertex != null) {
                result.addNeighbor(foundOrigin, foundVertex, foundValue);
                totalWeight += foundValue;
                current = foundVertex;
            }
        }
        System.out.println("Total weight for the PRIM Minimum Cost Spanning Three: " +  totalWeight);
        result.printVerticesAndNeighbors();
        return result;
    }

    public ArrayList<Graph> fracamenteConectados(){
        Vertex originVertex = vertices.get(0);
        Stack<Vertex> stack = new Stack<>();
        ArrayList<Vertex> result = new ArrayList<>();
        ArrayList<Graph> grafos = new ArrayList<>();
        ArrayList<Vertex> checked = new ArrayList<>();
        stack.push(originVertex);
        checked.add(originVertex);

        Graph g = new Graph(false);
        g.addVertex(originVertex);
        grafos.add(g);
        while(checked.size() < vertices.size()){
            while(!stack.isEmpty()) {

                HashMap<Vertex,Integer> neighbors = stack.pop().getNeighbors();
                for (HashMap.Entry<Vertex, Integer> neighbor : neighbors.entrySet()) {
                    if(!checked.contains(neighbor.getKey()) && !result.contains(neighbor.getKey())){
                        g.addVertex(neighbor.getKey());
                        g.addNeighbor(originVertex,neighbor.getKey(),neighbor.getValue());
                        result.add(neighbor.getKey());
                        checked.add(neighbor.getKey());
                    }
                }
            }
            if(result.size() > 0) {
                stack.addAll(result);
                result.clear();
            } else{

                for(Vertex v: vertices){
                    if (!checked.contains(v)) {
                        originVertex = v;
                        checked.add(originVertex);
                        stack.push(originVertex);
                        g = new Graph(false);
                        g.addVertex(originVertex);
                        grafos.add(g);
                        break;
                    }
                }
            }
        }
        return grafos;
    }
    
    public void savePajekFile(String destinationFolderPath) {
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss");
    	String destinationFile = destinationFolderPath + "\\graph-" + df.format(new Date()) + ".pajek";
    	
    	try(PrintWriter writter = new PrintWriter(destinationFile)){
    		//Vertices header
    		writter.println("*Vertices " + vertices.size());
    		
    		//Vertices body
        	for(Vertex v : vertices)
        		writter.println(v.getIndex() + " \"" + v.getName() + "\"");
        	
        	//Edges/Arcs header
        	writter.println("*" + (directed ? "Arcs" : "Edges"));
        	
        	//Edges/Arcs body
        	for(Vertex v : vertices) {
        		for (Map.Entry<Vertex, Integer> neighbor: v.getNeighbors().entrySet()) {
        			//If not directed, do not create redundant neighbors
        			if(!directed && neighbor.getKey().getIndex() < v.getIndex())
        				continue;
        			
        			writter.println(v.getIndex() + " " + neighbor.getKey().getIndex() + " " + neighbor.getValue());
        		}
        	}
        	
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    public void readPajekFile(String readFilePath) {
    	int step = 1; //The pajek reading process takes 4 steps
    	int verticesQuantity = 0;
    	int verticesCreated = 0;
    	
    	try(BufferedReader br = new BufferedReader(new FileReader(readFilePath))) {
    		for(String line; (line = br.readLine()) != null; ) {
    			
    			switch(step) {
    			case 1: //Read vertices quantity
    				verticesQuantity = Integer.valueOf(line.split(" ")[1]);
    				step++;
    				break;
    				
    			case 2: //Create vertices and add to graph
    				Vertex v = new Vertex(line.substring(line.indexOf("\"")+1, line.lastIndexOf("\"")));
    				this.addVertex(v);

    				if(++verticesCreated == verticesQuantity)
    					step++;
    				break;
    				
    			case 3: //Set the graph to directed or not directed
    				this.directed = line.equalsIgnoreCase("*arcs");
    				step++;
    				break;
    				
    			case 4: //Create edges/arcs
    				String[] split = line.split(" ");
    				this.addNeighbor(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]));
    				break;
    			}
    		}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public static Graph randomGraph(int nodes, int edges, boolean connected){
        Graph g = new Graph(false);
        Set<Vertex> usedVertices = new HashSet<>();
        ArrayList<Vertex> unusedVertices = new ArrayList<>();
        Random random = new Random();

        //Generates the vertices
        for (int i=0; i<nodes; i++){
            Vertex v = new Vertex(String.valueOf(i));
            g.addVertex(v);
            unusedVertices.add(v);
        }

        //Random a first vertex from total, adds to used nodes and remove from unused
        int currentVertex = random.nextInt(nodes);
        if(connected)
        {
            usedVertices.add(g.vertices.get(currentVertex));
            unusedVertices.remove(g.vertices.get(currentVertex));
        }

        int createdEdges = 0;
        while(createdEdges < edges){
            int newVertex = -1;
            while(newVertex==-1) {
                int i = random.nextInt(nodes);
                if (i != currentVertex && !g.vertices.get(currentVertex).isNeighbor(g.vertices.get(i))) {
                    if(connected)
                    {
                        if(unusedVertices.size() > 0) {
                            if (unusedVertices.contains(g.vertices.get(i))) {
                                newVertex = i;
                                g.addNeighbor(currentVertex, newVertex, 1);
                                createdEdges++;
                            }
                        } else
                        {
                            newVertex = i;
                            g.addNeighbor(currentVertex, newVertex, 1);
                            createdEdges ++;
                        }
                    }
                    else
                    {
                        newVertex = i;
                        g.addNeighbor(currentVertex, newVertex, 1);
                        createdEdges ++;
                    }
                }
            }
            if(connected){
                usedVertices.add(g.vertices.get(newVertex));
                unusedVertices.remove(g.vertices.get(newVertex));
                int n = random.nextInt(usedVertices.size());
                int count = 0;
                for (Vertex v: usedVertices){
                    if(count == n){
                        currentVertex = v.getIndex();
                        break;
                    }
                    count++;
                }
            } else currentVertex = random.nextInt(nodes);
        }
        try{
            if(!connected && g.checkConnected()) return Graph.randomGraph(nodes,edges,connected);
        }catch (Exception e){
            System.out.println("Will never fall here");
        }

        return g;
    }
    
    public boolean checkConnected() throws Exception {
    	if(directed) {
    		throw new Exception("Only works for non directed graphs!");
    	}
    	
    	//Try every pair to check if origin can reach destiny
    	for(int origin = 0; origin < vertices.size()-1; origin++) {
    		for(int destiny = origin+1; destiny < vertices.size(); destiny++) {

    			//If any pair is not reachable, the graph is not connected
    			if(!depthSearch(origin, destiny)) {
    				return false;
    			}
    		}
    	}
    	
    	return true;
    }
    
    public boolean checkEulerian() {
    	//Zero or two odd vertices
    	int oddCount = 0;
    	
    	for(int i = 0; i < vertices.size(); i++) {
    		if(vertices.get(i).getNeighborsSize() % 2 == 1)
    			oddCount++;
    		
    		if(oddCount > 2)
    			return false;
    	}
    	
    	return oddCount == 0 || oddCount == 2;
    }
    
    public void calculateCloseness() {
    	List<Double> closeness = new ArrayList<>();
    	
    	//For each vertice, calculate its closeness
    	for(int i = 0; i < vertices.size(); i++) {
    		double c = 0;
    		for(int j = 0; j < vertices.size(); j++) {
    			Dijkstra(j, i);
    			c += lastDijkstraWeight;
    		}
    		c = 1.0/c;
    		closeness.add(c);
    	}
    	
    	System.out.println("Printing all closeness values:");
    	for(int i = 0; i < vertices.size(); i++) {
    		System.out.println("Vertice " + i + ": " + closeness.get(i));
    	}
    }
}
