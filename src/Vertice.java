import java.util.ArrayList;

public class Vertice implements Comparable<Vertice>{
    private final String name;
    private final ArrayList<Edge> edges = new ArrayList<>();
    private final ArrayList<Vertice> adjacent = new ArrayList<>();
    private ArrayList<Vertice> pre;
    private int distance;
    private boolean visited;    //用来记录顶点是否在BellmanFord算法的队列中

    public Vertice(String name){
        this.name = name;
    }

    //为优先队列中顶点的排序，用distance来排
    @Override
    public int compareTo(Vertice v){
        return this.distance - v.distance;
    }

    public String getName(){
        return name;
    }

    public ArrayList<Edge> getEdges(){
        return edges;
    }

    public ArrayList<Vertice> getAdjacent(){
        return adjacent;
    }

    public ArrayList<Vertice> getPre(){
        return pre;
    }

    public int getDistance(){
        return distance;
    }

    void setDistance(int distance){
        this.distance = distance;
    }

    void setPre(Vertice pre){
        if (pre == null)
            this.pre = null;
        else {
            if (this.pre == null)
                this.pre = new ArrayList<>();
            this.pre.add(pre);
        }
    }

    Edge getEdge1(Vertice v){
        for (Edge e : edges)
            if (e.otherSide(v) == this)
                return e;
        return null;
    }

    Edge getEdge2(Vertice v){
        ArrayList<Edge> temp = new ArrayList<>();
        for (Edge e : edges){
            if (e.otherSide(v) == this){
                temp.add(e);
            }
        }
        return temp.get(1);
    }

    void setVisited(boolean visited){
        this.visited = visited;
    }

    boolean inVisited(){
        return !visited;
    }
}
