import java.io.*;
import java.util.*;

class SourceNode {
    int sourceNode;
    int xCord;
    int yCord;

    SourceNode(int sourceNode, int xCord, int yCord){
        this.sourceNode= sourceNode;
        this.xCord=xCord;
        this.yCord=yCord;
    }
}

class DestinationNode {
    int destinationNode;
    int xCord;
    int yCord;

    DestinationNode(int destinationNode, int xCord, int yCord){
        this.destinationNode=destinationNode;
        this.xCord=xCord;
        this.yCord=yCord;
    }
}

class Edge {
    int source;
    int xCord;
    int yCord;
    int destination;

    public Edge(int source, int xCord, int yCord, int destination) {
        this.source = source;
        this.xCord = xCord;
        this.yCord = yCord;
        this.destination = destination;
    }
}

public class homework {
    private Map<Integer, List<Edge>> map = new HashMap<>();
    int gridX, gridY;
    int[][] dir = new int[8][3];
    String algorithmToBeUsed;
    SourceNode sourceNode;
    DestinationNode destinationNode;
    Map<String,Integer> totalCost = new LinkedHashMap<>();
    Map<String,ArrayList<ArrayList<Integer>>> tempListMap = new LinkedHashMap<>();
    ArrayList<String> finalPaths = new ArrayList<>();
    int finalCost;
    public static void main(String[] args) throws IOException {
        homework search= new homework();
        search.formGraph();
        search.searchGraph();
    }
    public void formGraph() throws IOException {
        ArrayList<String> arrList = new ArrayList<>();
        File file = new File("input.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null){
            arrList.add(st);
        }
        algorithmToBeUsed = arrList.get(0);
        gridX = Integer.parseInt(arrList.get(1).split("\\s")[0]);
        gridY = Integer.parseInt(arrList.get(1).split("\\s")[1]);
        String[] sourceArray = arrList.get(2).split("\\s");
        sourceNode=new SourceNode(Integer.parseInt(sourceArray[0]),Integer.parseInt(sourceArray[1]),Integer.parseInt(sourceArray[2]));
        String[] destinationArray = arrList.get(3).split("\\s");
        destinationNode=new DestinationNode(Integer.parseInt(destinationArray[0]),Integer.parseInt(destinationArray[1]),Integer.parseInt(destinationArray[2]));
        for(int j=5;j<arrList.size();j++){
            int sourceNode = Integer.parseInt(arrList.get(j).split("\\s")[0]);
            int xCord = Integer.parseInt(arrList.get(j).split("\\s")[1]);
            int yCord = Integer.parseInt(arrList.get(j).split("\\s")[2]);
            int destinationNode = Integer.parseInt(arrList.get(j).split("\\s")[3]);
            addEdge(sourceNode,xCord,yCord,destinationNode);
        }
        addEdge(destinationNode.destinationNode,destinationNode.xCord,destinationNode.yCord,-1);
    }
    public void addEdge(int source,int x,int y,int destination){
        Edge edge = new Edge(source,x,y,destination);
        if(!map.containsKey(source))
            addVertex(source);
        if(!map.containsKey(destination))
            addVertex(destination);
        map.get(source).add(edge);
        Edge edge1 = new Edge(destination,x,y,source);
        map.get(destination).add(edge1);
    }
    public void addVertex(int source){
        map.put(source, new LinkedList<Edge>());
    }
    public void searchGraph() throws IOException {
        if (algorithmToBeUsed.equals("BFS"))
            searchGraph_BFS();
        else if (algorithmToBeUsed.equals("UCS"))
            searchGraph_UCS();
        else
            searchGraph_A();
        writeOutput();
    }
    private void writeOutput() throws IOException {
        File tempFile = new File("output.txt");
        if(tempFile.exists())
            tempFile.delete();
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        if(finalPaths.size()==0){
            //System.out.println("In fail");
            writer.append("FAIL");
        }
        else {
            writer.append(finalCost + "");
            writer.newLine();
            writer.append(finalPaths.size() + "");
            writer.newLine();
            for (String s : finalPaths) {
                writer.append(s);
                writer.newLine();
            }
        }

        writer.close();
    }
    private ArrayList<String> getPath(Map<String, String> pred, int destinationX, int destinationY, Map<String,Integer> cost, int source, int destination) {
        ArrayList<ArrayList<Integer>> tempArray = new ArrayList<>();
        String p = destinationX+":"+destinationY;
        ArrayList<String> path = new ArrayList<>();
        int totalCost =0;
        path.add(p);
        while(p!=null && !p.equals("-1")){
            String s1=pred.get(p);
            if(s1!=null && !s1.equals("-1"))
                path.add(s1);
            p=s1;
        }
        Collections.reverse(path);
        for(String i:path){
            ArrayList<Integer> tempList = new ArrayList<>();
            Integer i1 = cost.get(i);
            totalCost+=i1;
            int xCord=Integer.parseInt(i.split(":")[0]);
            int yCord=Integer.parseInt(i.split(":")[1]);
            tempList.add(xCord);
            tempList.add(yCord);
            tempList.add(i1);
            tempArray.add(tempList);
        }
        tempListMap.put(source+":"+destination,tempArray);
        this.totalCost.put(source+":"+destination,totalCost);
        return path;
    }

    private void initializeDirections(int x2, int y2) {
        dir[0][0] = x2;
        dir[0][1] = y2+1;
        dir[1][0] = x2;
        dir[1][1] = y2-1;
        dir[2][0] = x2+1;
        dir[2][1] = y2;
        dir[3][0] = x2-1;
        dir[3][1] = y2;
        dir[4][0] = x2+1;
        dir[4][1] = y2+1;
        dir[5][0] = x2-1;
        dir[5][1] = y2+1;
        dir[6][0] = x2+1;
        dir[6][1] = y2-1;
        dir[7][0] = x2-1;
        dir[7][1] = y2-1;
        if(algorithmToBeUsed.equals("BFS")){
            for(int i=0;i<8;i++){
                dir[i][2]=1;
            }
        }
        else{
            for(int i=0;i<4;i++){
                dir[i][2]=10;
            }
            for(int i=4;i<8;i++){
                dir[i][2]=14;
            }
        }
    }

    private void getFinalPath(Map<Integer, Integer> pred) {
        int finalCost =0;
        Integer p = -1;
        ArrayList<Integer> path = new ArrayList<>();
        ArrayList<ArrayList<Integer>> list;
        path.add(p);
        while(p!=null){
            Integer s1=pred.get(p);
            if(s1!=null)
                path.add(s1);
            p=s1;
        }

        Collections.reverse(path);
        for(int i=0;i<path.size()-1;i++) {
            list = tempListMap.get(path.get(i) + ":" + path.get(i + 1));
            finalCost+=totalCost.get(path.get(i)+":"+path.get(i+1));
            String str = "";
            for (ArrayList<Integer> arr : list) {
                String str1="";
                for (Integer x : arr) {
                    str1=str1+x+" ";
                }
                str=path.get(i)+" "+str1;
                finalPaths.add(str);
            }
        }
        this.finalCost=finalCost;
    }

    public void searchGraph_BFS(){
        int node = sourceNode.sourceNode;
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> visitedNodes = new ArrayList<>();
        Map<Integer, Integer> pred = new HashMap<>();
        Map<Integer, String> startPositions = new HashMap<>();
        Queue<String> queueInternal;
        Map<String, String> predInternal;
        Map<String, Integer> costInternal;
        startPositions.put(node, sourceNode.xCord + ":" + sourceNode.yCord);
        queue.add(node);
        visitedNodes.add(node);
        while (!queue.isEmpty()) {
            node = queue.remove();
            //visitedNodes.add(node);
            if (node == -1) {
                getFinalPath(pred);
                queue.clear();
                break;
            }
            List<Edge> currentList = map.get(node);
            for (Edge edge : currentList) {
                int edgeDestination = edge.destination;
                if (!visitedNodes.contains(edgeDestination)) {
                    String start = startPositions.get(node);
                    int currentX = Integer.parseInt(start.split(":")[0]);
                    int currentY = Integer.parseInt(start.split(":")[1]);
                    int destinationX = edge.xCord;
                    int destinationY = edge.yCord;
                    int[][] matrix = new int[gridX][gridY];
                    int costOfDirection = 0;
                    queueInternal = new LinkedList<>();
                    predInternal = new LinkedHashMap<>();
                    costInternal = new LinkedHashMap<>();
                    costInternal.put(sourceNode.xCord + ":" + sourceNode.yCord, 0);
                    if (node != sourceNode.sourceNode) {
                        costInternal.put(currentX + ":" + currentY, 1);
                    }
                    queueInternal.add(currentX + ":" + currentY);
                    predInternal.put(currentX + ":" + currentY, "-1");

                    matrix[currentX][currentY] = 1;
                    while (!queueInternal.isEmpty()) {
                        String s = queueInternal.poll();
                        int xTemp = Integer.parseInt(s.split(":")[0]);
                        int yTemp = Integer.parseInt(s.split(":")[1]);
                        if (xTemp == destinationX && yTemp == destinationY) {
                            getPath(predInternal, destinationX, destinationY, costInternal, edge.source, edgeDestination);
                            break;
                        }
                        matrix[xTemp][yTemp] = 1;
                        initializeDirections(xTemp, yTemp);
                        for (int i = 0; i < 8; i++) {
                            int tempx = dir[i][0];
                            int tempy = dir[i][1];
                            if (tempx >= gridX)
                                tempx = gridX - 1;
                            if (tempy >= gridY)
                                tempy = gridY - 1;
                            if (tempx <= 0)
                                tempx = 0;
                            if (tempy <= 0)
                                tempy = 0;
                            if (matrix[tempx][tempy] != 1) {
                                xTemp = tempx;
                                yTemp = tempy;
                                costOfDirection = dir[i][2];
                                matrix[xTemp][yTemp] = 1;
                                String temp = xTemp + ":" + yTemp;
                                queueInternal.add(temp);
                                predInternal.put(temp, s);
                                costInternal.put(temp, costOfDirection);
                            }
                        }
                    }
                    //System.out.println("Popped "+edgeDestination+" from "+edge.source);
                    visitedNodes.add(edgeDestination);
                    queue.add(edgeDestination);
                    pred.put(edgeDestination, edge.source);
                    startPositions.put(edgeDestination, edge.xCord + ":" + edge.yCord);
                }
            }
        }
    }

    public void searchGraph_UCS(){
        int node = sourceNode.sourceNode;
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        Map<Integer, Integer> priorityQueueMapExternal = new LinkedHashMap<>();
        List<Integer> visitedNodes = new ArrayList<>();
        Map<Integer, Integer> pred = new HashMap<>();
        Map<Integer, String> startPositions = new HashMap<>();
        PriorityQueue<Integer> queueInternal;
        Map<String, Integer> priorityQueueMap;
        Map<String, String> predInternal;
        Map<String, Integer> costInternal;
        startPositions.put(node, sourceNode.xCord + ":" + sourceNode.yCord);
        priorityQueueMapExternal.put(node, 0);
        queue.add(0);
        while (!queue.isEmpty()) {
            int nodeCost = queue.remove();
            for (Map.Entry<Integer, Integer> entry : priorityQueueMapExternal.entrySet()) {
                int key = entry.getKey();
                if (entry.getValue() == nodeCost && !visitedNodes.contains(key)) {
                    node = key;
                    break;
                }
            }
            visitedNodes.add(node);
            if (node == -1) {
                getFinalPath(pred);
                queue.clear();
                break;
            }
            List<Edge> currentList = map.get(node);
            for (Edge edge : currentList) {
                int edgeDestination = edge.destination;
                if (!visitedNodes.contains(edgeDestination)) {
                    String start = startPositions.get(node);
                    int currentX = Integer.parseInt(start.split(":")[0]);
                    int currentY = Integer.parseInt(start.split(":")[1]);
                    int destinationX = edge.xCord;
                    int destinationY = edge.yCord;
                    int pathCost = 0;
                    int[][] matrix = new int[gridX][gridY];
                    int costOfDirection = 0;
                    queueInternal = new PriorityQueue<>();
                    predInternal = new LinkedHashMap<>();
                    costInternal = new LinkedHashMap<>();
                    priorityQueueMap = new LinkedHashMap<>();
                    costInternal.put(sourceNode.xCord + ":" + sourceNode.yCord, 0);
                    if (node != sourceNode.sourceNode) {
                        costInternal.put(currentX + ":" + currentY, Math.abs(node - pred.get(node)));
                    }
                    queueInternal.add(0);
                    priorityQueueMap.put(currentX + ":" + currentY, 0);
                    predInternal.put(currentX + ":" + currentY, "-1");
                    while (!queueInternal.isEmpty()) {
                        int c = queueInternal.poll();
                        int xTemp = -1;
                        int yTemp = -1;
                        for (Map.Entry<String, Integer> entry : priorityQueueMap.entrySet()) {
                            if (entry.getValue() == c) {
                                String key = entry.getKey();
                                int xTemp1 = Integer.parseInt(key.split(":")[0]);
                                int yTemp1 = Integer.parseInt(key.split(":")[1]);
                                if (matrix[xTemp1][yTemp1] != 2) {
                                    xTemp = xTemp1;
                                    yTemp = yTemp1;
                                    matrix[xTemp][yTemp] = 2;
                                    break;
                                }
                            }
                        }
                        String s = xTemp + ":" + yTemp;
                        if (xTemp == destinationX && yTemp == destinationY) {
                            getPath(predInternal, destinationX, destinationY, costInternal, edge.source, edgeDestination);
                            pathCost = priorityQueueMap.get(xTemp + ":" + yTemp);
                            break;
                        }
                        initializeDirections(xTemp, yTemp);
                        for (int i = 0; i < 8; i++) {
                            int tempx = dir[i][0];
                            int tempy = dir[i][1];
                            if (tempx >= gridX)
                                tempx = gridX - 1;
                            if (tempy >= gridY)
                                tempy = gridY - 1;
                            if (tempx <= 0)
                                tempx = 0;
                            if (tempy <= 0)
                                tempy = 0;
                            if (matrix[tempx][tempy] != 2) {
                                xTemp = tempx;
                                yTemp = tempy;
                                costOfDirection = dir[i][2];
                                //matrix[xTemp][yTemp] = 1;
                                String temp = xTemp + ":" + yTemp;
                                //predInternal.put(temp, s);
                                //costInternal.put(temp, costOfDirection);
                                int pathCost1 = costOfDirection + priorityQueueMap.get(s);
                                if(priorityQueueMap.containsKey(temp)){
                                    if(priorityQueueMap.get(temp)>pathCost1){
                                        priorityQueueMap.put(temp,pathCost1);
                                        queueInternal.add(pathCost1);
                                        predInternal.put(temp, s);
                                        costInternal.put(temp, costOfDirection);
                                    }
                                }
                                else {
                                    priorityQueueMap.put(temp, pathCost1);
                                    queueInternal.add(pathCost1);
                                    predInternal.put(temp, s);
                                    costInternal.put(temp, costOfDirection);
                                }
                                //priorityQueueMap.put(temp, pathCost1);
                                //queueInternal.add(pathCost1);
                            }
                        }
                    }
                    int diff = 0;
                    if(edgeDestination!=-1)
                        diff = Math.abs(edgeDestination - edge.source);
                    //pred.put(edgeDestination, edge.source);
                    int dist = priorityQueueMapExternal.get(edge.source);
                    int totalCost = pathCost + diff + dist;
                    //System.out.println("Total cost for "+edgeDestination+" is "+totalCost+" from "+edge.source);
                    if(priorityQueueMapExternal.containsKey(edgeDestination)){
                        if(priorityQueueMapExternal.get(edgeDestination)>totalCost){
                            priorityQueueMapExternal.put(edgeDestination,totalCost);
                            queue.add(totalCost);
                            startPositions.put(edgeDestination, edge.xCord + ":" + edge.yCord);
                            pred.put(edgeDestination, edge.source);
                        }
                    }
                    else{
                        priorityQueueMapExternal.put(edgeDestination, totalCost);
                        queue.add(totalCost);
                        startPositions.put(edgeDestination, edge.xCord + ":" + edge.yCord);
                        pred.put(edgeDestination, edge.source);
                    }
                   // priorityQueueMapExternal.put(edgeDestination, totalCost);
                    //visitedNodes.add(edgeDestination);
                    //queue.add(totalCost);
                    //startPositions.put(edgeDestination, edge.xCord + ":" + edge.yCord);
                }
            }
        }
    }

    public void searchGraph_A(){
        int node = sourceNode.sourceNode;
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        Map<Integer, Integer> priorityQueueMapExternal = new LinkedHashMap<>();
        List<Integer> visitedNodes = new ArrayList<>();
        Map<Integer, Integer> pred = new HashMap<>();
        Map<Integer, String> startPositions = new HashMap<>();
        PriorityQueue<Integer> queueInternal;
        Map<String, Integer> priorityQueueMap;
        Map<String, String> predInternal;
        Map<String, Integer> costInternal;
        startPositions.put(node, sourceNode.xCord + ":" + sourceNode.yCord);
        priorityQueueMapExternal.put(node, 0);
        queue.add(0);
        while (!queue.isEmpty()) {
            int nodeCost = queue.remove();
            for (Map.Entry<Integer, Integer> entry : priorityQueueMapExternal.entrySet()) {
                int key = entry.getKey();
                if (entry.getValue() == nodeCost && !visitedNodes.contains(key)) {
                    node = key;
                    break;
                }
            }
            visitedNodes.add(node);
            if (node == -1) {
                getFinalPath(pred);
                queue.clear();
                break;
            }
            List<Edge> currentList = map.get(node);
            for (Edge edge : currentList) {
                int edgeDestination = edge.destination;
                if (!visitedNodes.contains(edgeDestination)) {
                    String start=startPositions.get(node);
                    int currentX = Integer.parseInt(start.split(":")[0]);
                    int currentY = Integer.parseInt(start.split(":")[1]);
                    int destinationX = edge.xCord;
                    int destinationY = edge.yCord;
                    int pathCost = 0;
                    int[][] matrix = new int[gridX][gridY];
                    int costOfDirection = 0;
                    queueInternal = new PriorityQueue<>();
                    predInternal = new LinkedHashMap<>();
                    costInternal = new LinkedHashMap<>();
                    priorityQueueMap = new LinkedHashMap<>();
                    costInternal.put(sourceNode.xCord + ":" + sourceNode.yCord, 0);
                    if (node != sourceNode.sourceNode) {
                        costInternal.put(currentX + ":" + currentY, Math.abs(node - pred.get(node)));
                    }
                    queueInternal.add(0);
                    priorityQueueMap.put(currentX + ":" + currentY, 0);
                    predInternal.put(currentX + ":" + currentY, "-1");
                    while (!queueInternal.isEmpty()) {
                        int c = queueInternal.poll();
                        int xTemp = -1;
                        int yTemp = -1;
                        for (Map.Entry<String, Integer> entry : priorityQueueMap.entrySet()) {
                            if (entry.getValue() == c) {
                                String key = entry.getKey();
                                int xTemp1 = Integer.parseInt(key.split(":")[0]);
                                int yTemp1 = Integer.parseInt(key.split(":")[1]);
                                if (matrix[xTemp1][yTemp1] != 2) {
                                    xTemp = xTemp1;
                                    yTemp = yTemp1;
                                    matrix[xTemp][yTemp] = 2;
                                    break;
                                }
                            }
                        }
                        String s = xTemp + ":" + yTemp;
                        if (xTemp == destinationX && yTemp == destinationY) {
                            getPath(predInternal, destinationX, destinationY, costInternal, edge.source, edgeDestination);
                            pathCost = priorityQueueMap.get(xTemp + ":" + yTemp);
                            break;
                        }
                        initializeDirections(xTemp, yTemp);
                        for (int i = 0; i < 8; i++) {
                            int tempx = dir[i][0];
                            int tempy = dir[i][1];
                            if (tempx >= gridX)
                                tempx = gridX - 1;
                            if (tempy >= gridY)
                                tempy = gridY - 1;
                            if (tempx <= 0)
                                tempx = 0;
                            if (tempy <= 0)
                                tempy = 0;
                            if (matrix[tempx][tempy] != 2) {
                                xTemp = tempx;
                                yTemp = tempy;
                                int distance = (int) Math.floor(Math.sqrt(Math.pow((destinationX - xTemp), 2) + Math.pow((destinationY - yTemp), 2)));
                                costOfDirection = dir[i][2];
                               // matrix[xTemp][yTemp] = 1;
                                String temp = xTemp + ":" + yTemp;
                               // predInternal.put(temp, s);
                                //costInternal.put(temp, costOfDirection);
                                int prevDistance = 0;
                                if(!s.equals(currentX+":"+currentY)){
                                    int x = Integer.parseInt(s.split(":")[0]);
                                    int y = Integer.parseInt(s.split(":")[1]);
                                    prevDistance = (int) Math.floor(Math.sqrt(Math.pow((destinationX - x), 2) + Math.pow((destinationY - y), 2)));
                                }
                                int pathCost1 = costOfDirection + priorityQueueMap.get(s) + distance - prevDistance;
                                if(priorityQueueMap.containsKey(temp)){
                                    if(priorityQueueMap.get(temp)>pathCost1){
                                        priorityQueueMap.put(temp,pathCost1);
                                        queueInternal.add(pathCost1);
                                        predInternal.put(temp, s);
                                        costInternal.put(temp, costOfDirection);
                                    }
                                }
                                else {
                                    priorityQueueMap.put(temp, pathCost1);
                                    queueInternal.add(pathCost1);
                                    predInternal.put(temp, s);
                                    costInternal.put(temp, costOfDirection);
                                }
                            }
                        }
                    }
                    int prevDist = 0;
                    if(edge.source!=sourceNode.sourceNode)
                        prevDist = Math.abs(edge.source-destinationNode.destinationNode);
                    int diff =0;
                    int diffToFinal = 0;
                    if(edgeDestination!=-1) {
                        diff = Math.abs(edgeDestination - edge.source);
                        diffToFinal = Math.abs(edgeDestination - destinationNode.destinationNode);
                    }
                    //pred.put(edgeDestination, edge.source);
                    int dist = priorityQueueMapExternal.get(edge.source);
                    int totalCost = pathCost + diff + dist + diffToFinal - prevDist;
                    // System.out.println("Total cost for "+edgeDestination+" is "+totalCost);
                    if(priorityQueueMapExternal.containsKey(edgeDestination)){
                        if(priorityQueueMapExternal.get(edgeDestination)>totalCost){
                            priorityQueueMapExternal.put(edgeDestination,totalCost);
                            queue.add(totalCost);
                            startPositions.put(edgeDestination, edge.xCord + ":" + edge.yCord);
                            pred.put(edgeDestination, edge.source);
                        }
                    }
                    else{
                        priorityQueueMapExternal.put(edgeDestination, totalCost);
                        queue.add(totalCost);
                        startPositions.put(edgeDestination, edge.xCord + ":" + edge.yCord);
                        pred.put(edgeDestination, edge.source);
                    }
                    //visitedNodes.add(edgeDestination);
                   // queue.add(totalCost);
                    //startPositions.put(edgeDestination, edge.xCord + ":" + edge.yCord);
                }
            }
        }
    }
}
