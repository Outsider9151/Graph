public class Edge {
    private final Vertice v1;
    private final Vertice v2;
    private final String line;
    private final int weight;

    public Edge(Vertice v1, Vertice v2, String line, int weight){
        this.v1 = v1;
        this.v2 = v2;
        this.line = line;
        this.weight = weight;
    }

    public String getLine(){
        return line;
    }

    public int getWeight(){
        return weight;
    }

    public Vertice otherSide(Vertice v){
        if (v == v1)
            return v2;
        else if (v == v2)
            return v1;
        else
            return null;
    }
}
