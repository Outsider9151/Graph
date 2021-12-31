import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BellmanFord {
    ConcurrentLinkedQueue<Vertice> queue = new ConcurrentLinkedQueue<>();
    private ArrayList<ArrayList<String>> paths;
    private void BF(ArrayList<Vertice> vertices, Vertice s){
        initialize(vertices, s);
        queue.add(s);
        s.setVisited(true);
        Vertice u;
        while (!queue.isEmpty()){
            u = queue.poll();
            u.setVisited(false);
            for (Vertice v : u.getAdjacent())
                relax(u, v);
        }
    }

    private void initialize(ArrayList<Vertice> graph, Vertice s){
        for (Vertice vertice : graph) {
            vertice.setDistance(Integer.MAX_VALUE);
            vertice.setPre(null);
            vertice.setVisited(false);
        }
        s.setDistance(0);
    }

    //释放，若有边被释放，结点的distance发生变化，则将其存入队列中
    private void relax(Vertice u,Vertice v){
        int t = u.getDistance() + u.getEdge1(v).getWeight();
        if (v.getDistance() > t){
            v.setDistance(t);
            if (v.inVisited()) {
                queue.add(v);
                v.setVisited(true);
            }
            if (v.getPre() != null){
                v.getPre().clear();
                v.setPre(u);
            }
            if (v.getPre() == null)
                v.setPre(u);
        }
        else if (v.getDistance() == t)
            v.setPre(u);
    }

    private void setPaths(Vertice s, Vertice t, ArrayList<String> path){
        if (s != t){
            for (int i = 1; i < t.getPre().size(); i++){
                Vertice v = t.getPre().get(i);
                ArrayList<String> newPath = new ArrayList<>(path);
                if (v == t.getPre().get(i - 1))
                    newPath.add(v.getEdge2(t).getLine());
                else
                    newPath.add(v.getEdge1(t).getLine());
                newPath.add(v.getName());
                paths.add(newPath);
                setPaths(s, v, newPath);
            }
            path.add(t.getPre().get(0).getEdge1(t).getLine());
            path.add(t.getPre().get(0).getName());
            setPaths(s, t.getPre().get(0), path);
        }
    }

    private void infixPaths(){
        for (ArrayList<String> path : paths) {
            Stack<String> temp = new Stack<>();
            temp.addAll(path);
            path.clear();
            while (!temp.empty())
                path.add(temp.pop());
        }
    }

    ArrayList<ArrayList<String>> getPaths(ArrayList<Vertice> vertice, Vertice s, Vertice t){
        BF(vertice, s);
        ArrayList<String> path = new ArrayList<>();
        paths = new ArrayList<>();
        path.add(t.getName());
        paths.add(path);
        setPaths(s, t, path);
        infixPaths();
        return paths;
    }
}
