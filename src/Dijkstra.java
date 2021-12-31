import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;

public class Dijkstra {
    private ArrayList<ArrayList<String>> paths;

    private void DIJKSTRA(ArrayList<Vertice> vertices, Vertice s, Vertice t){
        initialize(vertices, s);
        PriorityQueue<Vertice> priorityQueue = new PriorityQueue<>(vertices);
        Vertice u;
        while(!priorityQueue.isEmpty()){
            u = priorityQueue.poll();
            if (u == t)
                break;
            for (Vertice v : u.getAdjacent())
                relax(u, v);
            priorityQueue = new PriorityQueue<>(new ArrayList<>(priorityQueue));
        }
    }

    //初始化，将起点距离设为0，其他为最大值
    private void initialize(ArrayList<Vertice> graph, Vertice s){
        for (Vertice vertice : graph) {
            vertice.setDistance(Integer.MAX_VALUE);
            vertice.setPre(null);
        }
        s.setDistance(0);
    }

    //释放，并标记上一结点
    private void relax(Vertice u,Vertice v){
        int t = u.getDistance() + u.getEdge1(v).getWeight();
        if (v.getDistance() > t){
            v.setDistance(t);
            if (v.getPre() != null){
                v.getPre().clear();
                v.setPre(u);
            }
            if (v.getPre() == null)
                v.setPre(u);
        }
        else if (v.getDistance() == t){
            v.setPre(u);
        }
    }

    //得到所有路径，按 站名，线路，站名 的方式存的
    private void setPaths(Vertice s, Vertice t, ArrayList<String> path){
        if (s != t){
            for (int i = 1; i < t.getPre().size(); i++){
                Vertice v = t.getPre().get(i);
                ArrayList<String> newPath = new ArrayList<>(path);
                //判断是否存在相同的结点，若存在则表示有并段，需要创建新的路径并存入同结点
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
        DIJKSTRA(vertice, s, t);
        ArrayList<String> path = new ArrayList<>();
        paths = new ArrayList<>();
        path.add(t.getName());
        paths.add(path);
        setPaths(s, t, path);
        infixPaths();
        return paths;
    }
}
