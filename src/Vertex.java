import java.util.HashMap;
import java.util.Map;

public class Vertex
{
    private String name;
    private int index;
    private HashMap<Vertex,Integer> neighbors = new HashMap<>();

    public int getIndex() {
        return index;
    }

    public Vertex(String name) {
        this.name = name;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public boolean addNeighborOrIncreaseWeight(Vertex v, Integer weight){
        if(neighbors.containsKey(v)) {
            neighbors.put(v, neighbors.get(v) + 1);
            return true;
        }
        this.neighbors.put(v,weight);
        return true;
    }

    public boolean isNeighbor(Vertex v){
        if (neighbors.get(v) != null) return true;
        else return false;
    }

    public void printNeighbors(){
        System.out.print(String.valueOf(this.getIndex()) + "("+ this.getName() + ") - ");
        for (Map.Entry<Vertex, Integer> adjacencia: neighbors.entrySet()){
            System.out.print(adjacencia.getKey().getName() + ":" + adjacencia.getValue() + ",");
        }
    }

    public boolean removeNeighbor(Vertex v){
        try{
            this.neighbors.remove(v);
            return true;
        } catch (Exception e){
            System.out.println("ERROR - " + e);
            return false;
        }
    }

    public int getNeighborsSize(){
        return this.neighbors.size();
    }

    public int getNeighborWeight(Vertex neighbor){
        return neighbors.get(neighbor);
    }

    public HashMap<Vertex, Integer> getNeighbors() {
        return neighbors;
    }

    @Override
    public String toString(){
        return this.name;
    }

    //Override the equals method so there's no two Vertices with the same name.
    //Could be done using other data structure? Sure it could...but it is what it is
    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        if (!Vertex.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Vertex other = (Vertex) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }

        return true;
    }
}
