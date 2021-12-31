import java.util.ArrayList;

public class Graph {
    //图中所有顶点
    public ArrayList<Vertice> vertices = new ArrayList<>();
    public Graph(){}
    private int transferTime;

    //增加顶点
    Vertice addVertice(Vertice pre, String name, String line, String time1, String time2){
        Vertice newStation = getStation(name);
        //图中不存在该顶点，就创建
        if (newStation == null){
            newStation = new Vertice(name);
            vertices.add(newStation);
        }
        //加边
        pre.getAdjacent().add(newStation);
        newStation.getAdjacent().add(pre);
        int weight = getWeight(time1, time2);
        Edge edge = new Edge(pre, newStation, line, weight);
        pre.getEdges().add(edge);
        newStation.getEdges().add(edge);
        return newStation;
    }

    Vertice getStation(String name){
        for (Vertice v : vertices)
            if (v.getName().equals(name))
                return v;
        return null;
    }

    private int getWeight(String start, String end){
        int l1 = start.length();
        int l2 = end.length();
        int time = (int) end.charAt(l2 - 1) - (int) start.charAt(l1 - 1) +
                     10 * ((int) end.charAt(l2 - 2) - start.charAt(l1 - 2));
        if (time < 0)
            time += 60;
        return time;
    }

    ArrayList<ArrayList<String>> getDiPaths(Vertice s, Vertice t){
        ArrayList<String> path = new ArrayList<>();
        Dijkstra dijkstra = new Dijkstra();
        if (s == t) {
            path.add(s.getName());
            ArrayList<ArrayList<String>> paths = new ArrayList<>();
            paths.add(path);
            return paths;
        }
        return dijkstra.getPaths(vertices, s, t);
    }

    ArrayList<String> getFinalPath(ArrayList<String> path){
        transferTime = 0;
        String line1 = path.get(1);
        ArrayList<String> finalPath = new ArrayList<>();
        finalPath.add(path.get(0));
        finalPath.add(line1);
        for (int i = 1; i < path.size(); i = i + 2){
            String line2 = path.get(i);
            String station = path.get(i - 1);
            if (!line2.equals(line1)){
                line1 = line2;
                finalPath.add(station);
                finalPath.add(line2);
                transferTime++;
            }
        }
        finalPath.add(path.get(path.size() - 1));
        return finalPath;
    }

    int transferTime(){
        return transferTime;
    }

    int getTime(Vertice t){
        return t.getDistance();
    }

    String route(ArrayList<String> path){
        StringBuilder route = new StringBuilder(path.get(0));
        for (int i = 1; i < path.size(); i++)
            route.append("-").append(path.get(i));
        return route.toString();
    }

    ArrayList<ArrayList<String>> getBFPaths(Vertice s, Vertice t){
        ArrayList<String> path = new ArrayList<>();
        BellmanFord bf = new BellmanFord();
        if (s == t) {
            path.add(s.getName());
            ArrayList<ArrayList<String>> paths = new ArrayList<>();
            paths.add(path);
            return paths;
        }
        return bf.getPaths(vertices, s, t);
    }
}
