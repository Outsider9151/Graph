import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class SubwayGraph {
    private static Graph subway = null;

    public static void buildGraph() {
        if (subway != null)
            return;
        File file = new File("src/Data/subway.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            subway = new Graph();
            for (int i = 0; i < 15; i++) {
                //先把始发站存进去
                String line = br.readLine();
                String info = br.readLine();
                StringTokenizer st = new StringTokenizer(info, "，");
                String name = st.nextToken().trim();
                Vertice station = subway.getStation(name);
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
                        if (!temp.equals("--"))
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
            }
            Vertice t1 = subway.getStation("宜山路");
            Vertice t2 = subway.getStation("虹桥路");
            Edge ege = new Edge(t1, t2, "Line 4", 2);
            t1.getEdges().add(ege);
            t2.getEdges().add(ege);
            t1.getAdjacent().add(t2);
            t2.getAdjacent().add(t1);
            System.out.println("The subway graph is generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void DiPath(){
        Scanner input = new Scanner(System.in);
        System.out.println("Please input where you want to start:");
        Vertice s = subway.getStation(input.nextLine());
        while (s == null) {
            System.out.println("This station does not exist, Please input again!");
            input = new Scanner(System.in);
            s = subway.getStation(input.nextLine());
        }
        System.out.println("Please input where you want to go:");
        Vertice t = subway.getStation(input.nextLine());
        while (t == null){
            System.out.println("This station does not exist, Please input again!");
            input = new Scanner(System.in);
            t = subway.getStation(input.nextLine());
        }
        ArrayList<ArrayList<String>> paths = subway.getDiPaths(s, t);
        for (ArrayList<String> path : paths) {
            int time = subway.getTime(t);
            ArrayList<String> finalPath = subway.getFinalPath(path);
            String route = subway.route(finalPath);
            int transferTime = subway.transferTime();
            printRoute(route, time, transferTime);
        }
    }

    private static void DiMiddlePath(){
        int totalTime = 0;
        int time = 0;
        Scanner input = new Scanner(System.in);
        System.out.println("Please input the stations and split them:");
        String t = input.nextLine();
        StringTokenizer st = new StringTokenizer(t);
        Vertice m = subway.getStation(st.nextToken());
        while (st.hasMoreTokens()){
            Vertice n = subway.getStation(st.nextToken());
            if (m != n) {
                ArrayList<ArrayList<String>> paths = subway.getDiPaths(m, n);
                System.out.println("从" + m.getName() + "到" + n.getName() + ":");
                for (ArrayList<String> path : paths) {
                    time = subway.getTime(n);
                    ArrayList<String> finalPath = subway.getFinalPath(path);
                    String route = subway.route(finalPath);
                    int transferTime = subway.transferTime();
                    printRoute(route, time, transferTime);
                }
                m = n;
                totalTime += time;
            }
            else {
                System.out.println("从" + m.getName() + "到" + n.getName() + ":");
                String line = m.getEdges().get(0).getLine();
                System.out.println(line);
                for (int i = 1; i < m.getEdges().size(); i++){
                    if (!m.getEdges().get(i).getLine().equals(line)) {
                        line = m.getEdges().get(i).getLine();
                        System.out.println(line);
                    }
                }
            }
            System.out.println();
        }
        System.out.println("预计总共需要时间：" + totalTime + "分钟");
    }

    private static void DiFile(){
        File file = new File("src/Data/test.txt");
        //File file = new File("src/Data/test.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter out = new BufferedWriter(new FileWriter("src/Out/out.txt"));
            //BufferedWriter out = new BufferedWriter(new FileWriter("src/Out/out_test.txt"));
            String t;
            while((t = br.readLine()) != null){
                StringTokenizer st = new StringTokenizer(t);
                Vertice m = subway.getStation(st.nextToken());
                int totalTime = 0;
                int time = 0;
                while (st.hasMoreTokens()){
                    Vertice n = subway.getStation(st.nextToken());
                    if (m != n) {
                        ArrayList<ArrayList<String>> paths = subway.getDiPaths(m, n);
                        out.write("从" + m.getName() + "到" + n.getName() + ":" + "\n");
                        time = subway.getTime(n);
                        out.write("预计所需时间：" + time + " 分钟" + "\n");
                        for (ArrayList<String> path : paths) {
                            ArrayList<String> finalPath = subway.getFinalPath(path);
                            String route = subway.route(finalPath);
                            int transferTime = subway.transferTime();
                            out.write(route + "\n");
                            //out.write("预计所需时间：" + time + " 分钟" + "\n");
                            out.write("预计换乘次数：" + transferTime + " 次" + "\n");
                        }
                        m = n;
                        totalTime += time;
                    }
                    else {
                        out.write("从" + m.getName() + "到" + n.getName() + ":" + "\n");
                        String line = m.getEdges().get(0).getLine();
                        out.write(line + "\n");
                        for (int i = 1; i < m.getEdges().size(); i++){
                            if (!m.getEdges().get(i).getLine().equals(line)) {
                                line = m.getEdges().get(i).getLine();
                                out.write(line + "\n");
                            }
                        }
                    }
                    //out.write("\n");
                }
                out.write("预计总共需要时间：" + totalTime + "分钟" + "\n");
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void BFPath(){
        Scanner input = new Scanner(System.in);
        System.out.println("Please input where you want to start:");
        Vertice s = subway.getStation(input.nextLine());
        while (s == null) {
            System.out.println("This station does not exist, Please input again!");
            input = new Scanner(System.in);
            s = subway.getStation(input.nextLine());
        }
        System.out.println("Please input where you want to go:");
        Vertice t = subway.getStation(input.nextLine());
        while (t == null){
            System.out.println("This station does not exist, Please input again!");
            input = new Scanner(System.in);
            t = subway.getStation(input.nextLine());
        }
        ArrayList<ArrayList<String>> paths = subway.getBFPaths(s, t);
        for (ArrayList<String> path : paths) {
            int time = subway.getTime(t);
            ArrayList<String> finalPath = subway.getFinalPath(path);
            String route = subway.route(finalPath);
            int transferTime = subway.transferTime();
            printRoute(route, time, transferTime);
        }
    }

    private static void BFFile(){
        File file = new File("src/Data/test.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter out = new BufferedWriter(new FileWriter("src/Out/outbf.txt"));
            String t;
            while((t = br.readLine()) != null){
                StringTokenizer st = new StringTokenizer(t);
                Vertice m = subway.getStation(st.nextToken());
                int totalTime = 0;
                int time = 0;
                while (st.hasMoreTokens()){
                    Vertice n = subway.getStation(st.nextToken());
                    if (m != n) {
                        ArrayList<ArrayList<String>> paths = subway.getBFPaths(m, n);
                        out.write("从" + m.getName() + "到" + n.getName() + ":" + "\n");
                        time = subway.getTime(n);
                        out.write("预计所需时间：" + time + " 分钟" + "\n");
                        for (ArrayList<String> path : paths) {
                            ArrayList<String> finalPath = subway.getFinalPath(path);
                            String route = subway.route(finalPath);
                            int transferTime = subway.transferTime();
                            out.write(route + "\n");
                            out.write("预计换乘次数：" + transferTime + " 次" + "\n");
                        }
                        m = n;
                        totalTime += time;
                    }
                    else {
                        out.write("从" + m.getName() + "到" + n.getName() + ":" + "\n");
                        String line = m.getEdges().get(0).getLine();
                        out.write(line + "\n");
                        for (int i = 1; i < m.getEdges().size(); i++){
                            if (!m.getEdges().get(i).getLine().equals(line)) {
                                line = m.getEdges().get(i).getLine();
                                out.write(line + "\n");
                            }
                        }
                    }
                }
                out.write("预计总共需要时间：" + totalTime + "分钟" + "\n");
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void BFMiddlePath(){
        int totalTime = 0;
        int time = 0;
        Scanner input = new Scanner(System.in);
        System.out.println("Please input the stations and split them:");
        String t = input.nextLine();
        StringTokenizer st = new StringTokenizer(t);
        Vertice m = subway.getStation(st.nextToken());
        while (st.hasMoreTokens()){
            Vertice n = subway.getStation(st.nextToken());
            if (m != n) {
                ArrayList<ArrayList<String>> paths = subway.getBFPaths(m, n);
                System.out.println("从" + m.getName() + "到" + n.getName() + ":");
                for (ArrayList<String> path : paths) {
                    time = subway.getTime(n);
                    ArrayList<String> finalPath = subway.getFinalPath(path);
                    String route = subway.route(finalPath);
                    int transferTime = subway.transferTime();
                    printRoute(route, time, transferTime);
                }
                m = n;
                totalTime += time;
            }
            else {
                System.out.println("从" + m.getName() + "到" + n.getName() + ":");
                String line = m.getEdges().get(0).getLine();
                System.out.println(line);
                for (int i = 1; i < m.getEdges().size(); i++){
                    if (!m.getEdges().get(i).getLine().equals(line)) {
                        line = m.getEdges().get(i).getLine();
                        System.out.println(line);
                    }
                }
            }
            System.out.println();
        }
        System.out.println("预计总共需要时间：" + totalTime + "分钟");
    }

    private static void printRoute(String route, int time, int transferTime){
        System.out.println(route);
        System.out.println("预计所需时间：" + time + " 分钟");
        System.out.println("预计换乘次数：" + transferTime + " 次");
    }

    private static void menu(){
        System.out.println("Shortest Time\n0 quit");
        System.out.println("Dijkstra:\n1 File\n2 no middle\n3 with middle");
        System.out.println("Bellman-Ford:\n4 File\n5 no middle\n6 with middle");
    }

    private static void command(){
        Scanner input = new Scanner(System.in);
        while(true){
            System.out.print("Your command:");
            String command = input.nextLine();
            switch (command){
                case "0":
                    return;
                case "1":
                    DiFile();
                    break;
                case "2":
                    DiPath();
                    break;
                case "3":
                    DiMiddlePath();
                    break;
                case "4":
                    BFFile();
                    break;
                case "5":
                    BFPath();
                    break;
                case "6":
                    BFMiddlePath();
                    break;
                default:
                    System.out.println("Your command is invalid, please input again!");
            }
        }
    }

    public static void main(String[] args){
        buildGraph();
        menu();
        command();
    }
}
