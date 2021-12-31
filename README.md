# Graph
A course project of data structure in Fudan University, using Dijkstra and Bellman-Ford to find the shortest path of Shanghai metro System.
# Project2 说明文档
语言：JAVA

环境：IntelliJ IDEA

---
## 1. 地铁图的存储
- 邻接链表法：给每个顶点创建了一个链表，存与它相邻的顶点
- 具体：将excel文件改为txt格式，以逗号分隔同时将10号线和11号线改成同样的格式，在每个line之间加上”Separate“以作区分，然后循环15次读入，单独处理了10号线和11号线的双向问题，在所有线路载入后，给4号线的宜山路和虹桥路加上边
```java
for (int i = 0; i < 15; i++) {
    //先把始发站存进去
    String line = br.readLine();
    String info = br.readLine();
    StringTokenizer st = new StringTokenizer(info, "，");
    String name = st.nextToken().trim();
    Vertice station = subway.getNewStation(name);
    if (station == null) {
        station = new Vertice(name);
        subway.vertices.add(station);
    }
    String time1 = st.nextToken().trim();
    String time2;
    Vertice pre;
    //单独处理有两个方向的10号线和11号线
    if (i == 9 || i == 10) {
        //开始分开的站
        Vertice divStation = null;
        String time3 = "";
        //该循环仅处理第一个方向
        while (true) {
            pre = station;
            info = br.readLine();
            st = new StringTokenizer(info, "，");
            name = st.nextToken().trim();
            time2 = st.nextToken().trim();
            String temp = st.nextToken().trim();
            //第一个方向结束的标志
            if (time2.equals("--"))
                break;
            if (!temp.equals("--")
                time3 = temp;
            //开始分开的站
            if (temp.equals("--") && divStation == null)
                divStation = pre;
            station = subway.addVertice(pre, name, line, time1, time2);
            time1 = time2;
        }
        pre = divStation;
        time1 = time3;
        while (!info.equals("Separate")) {
            st = new StringTokenizer(info, "，");
            name = st.nextToken().trim();
            String temp = st.nextToken();
            time2 = st.nextToken().trim();
            station = subway.addVertice(pre, name, line, time1, time2);
            time1 = time2;
            pre = station;
            info = br.readLine();
        }
    } else {
        while (true) {
            info = br.readLine();
            if (info.equals("Separate"))
                break;
            st = new StringTokenizer(info, "，");
            name = st.nextToken().trim();
            time2 = st.nextToken().trim();
            pre = station;
            station = subway.addVertice(pre, name, line, time1, time2);
            time1 = time2;
        }
}
Vertice t1 = subway.getStation("宜山路");
Vertice t2 = subway.getStation("虹桥路");
Edge ege = new Edge(t1, t2, "Line 4", 2);
t1.getEdges().add(ege);
t2.getEdges().add(ege);
t1.getAdjacent().add(t2);
t2.getAdjacent().add(t1);
```

## 2. Dijkstra算法
- 用优先队列，依据各顶点distance的大小，对顶点排序，使每次队首都是其中distance最小的。循环释放与其相连的边。当循环到终点时，提前结束循环。
- relax的时候用ArrayList标记上一顶点pre，如果distance相等则增加一个pre，若小于原distance则重设distance，并将pre清除并加入该顶点。
- 为得到所有最短路径，在确定路径时使用了递归以保证可以遍历所有的pre顶点
```java
//算法主体部分
private void DIJKSTRA(ArrayList<Vertice> vertices, Vertice s, Vertice t){
    initialize(vertices, s);
    PriorityQueue<Vertice> priorityQueue = new PriorityQueue<>(vertices);
    Vertice u;
    while(!priorityQueue.isEmpty()){
        u = priorityQueue.poll();
        if (u == t)
            break;
        for (Vertice v : u.getAdjacent();
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

//路径保存，按站名，路线名保存
private void setPaths(Vertice s, Vertice t, ArrayList<String> path){
    if (s != t){
        for (int i = 1; i < t.getPre().size(); i++){
            Vertice v = t.getPre().get(i);
            ArrayList<String> newPath = new ArrayList<>(path);
            //因为不存在三条线路的并段，所以若pre中出现相同顶点，则分别给出两条线路
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
```

## 3. Bellman-Ford 算法
- 用队列简单优化了一下，因为只有distance发生变化的顶点才会影响到其他顶点，而distance没有变化的顶点并不会影响到其他顶点，故用一个队列维护每次distance发生变化的顶点，直到队列为空则意味着没有边需要被释放了，最短路径已找到。因队列中出现重复结点是没有意义的，所以给顶点添加了一个属性visited来记录它是否在队列中
- 因Bellman-Ford算法是在不断逼近最短路径，故只有最后一次释放后得到的路径才是真正的最短路径。
- 为记录所有的最短路径，也因为Bellman-Ford算法核心也是relax，和Dijkstra一样，因此用的也是同样的方法来记录所有最短路径。这里不再赘述。
```java
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
```

