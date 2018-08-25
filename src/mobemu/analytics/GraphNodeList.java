package mobemu.analytics;

import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.*;
import org.openide.util.Lookup;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class GraphNodeList extends GraphData{
    HashMap<Integer,GraphNode> nodeList;
    private GraphNodeList History;
    double averageClusteringCoefficient;
    int count = 0;
    private String traceName;
    boolean clusteringCoefficientRaised;
    public static int no_events = 0;
    public static int no_events_label = 0;
    public static int no_global_events =0;
    public static  int no_global_clust =0;

    public static int MAX_NODES = 99;
    public boolean init=true;

    public GraphNodeList( String traceName) {
        this.traceName = traceName;
        nodeList = new HashMap<>();
    }

    @Override
    public void populateData(GraphMatrix graphMatrix) {
        count ++;
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);

        Graph graph = graphModel.getGraph();

        processMatrix(graphMatrix, graphModel);

        ClusteringCoefficient clusteringCoefficient = new ClusteringCoefficient();
        GraphDistance distance = new GraphDistance();
        ConnectedComponents connectedComponents = new ConnectedComponents();
        Degree myDegree = new Degree();
        EigenvectorCentrality eignv = new EigenvectorCentrality();
        GraphDensity density = new GraphDensity();
        Hits hits = new Hits();
        PageRank pageRank = new PageRank();
        WeightedDegree weightedDegree = new WeightedDegree();

        clusteringCoefficient.execute(graphModel);
        distance.execute(graphModel);
        connectedComponents.execute(graphModel);
        myDegree.execute(graphModel);
        eignv.execute(graphModel);
        density.execute(graphModel);
        hits.execute(graphModel);
        pageRank.execute(graphModel);
        weightedDegree.execute(graphModel);

        this.averageClusteringCoefficient = clusteringCoefficient.getAverageClusteringCoefficient();
        Column clusteringColumn = graphModel.getNodeTable().getColumn(ClusteringCoefficient.CLUSTERING_COEFF);
        Column betweennessColumn = graphModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
        Column closenessColumn = graphModel.getNodeTable().getColumn(GraphDistance.CLOSENESS);
        Column eccentricityColumn = graphModel.getNodeTable().getColumn(GraphDistance.ECCENTRICITY);
        Column harmonicColumn = graphModel.getNodeTable().getColumn(GraphDistance.HARMONIC_CLOSENESS);
        Column strongColumn = graphModel.getNodeTable().getColumn(ConnectedComponents.STRONG);
        Column eignvColumn = graphModel.getNodeTable().getColumn(EigenvectorCentrality.EIGENVECTOR);
        Column hitsAuthorityColumn = graphModel.getNodeTable().getColumn(Hits.AUTHORITY);
        Column hitsHubColumn = graphModel.getNodeTable().getColumn(Hits.HUB);
        Column pageRankColumn = graphModel.getNodeTable().getColumn(PageRank.PAGERANK);
        Column weightDegreeColumn = graphModel.getNodeTable().getColumn(WeightedDegree.WDEGREE);

        for (Node n : graph.getNodes()){
            GraphNode newGraphNode = new GraphNode();

            newGraphNode.setBetweenessCentrality((Double) n.getAttribute(betweennessColumn));
            newGraphNode.setClosenessCentrality((Double) n.getAttribute(closenessColumn));
            newGraphNode.setClusteringCoefficient((Double) n.getAttribute(clusteringColumn));
            newGraphNode.setDegree((Double) n.getAttribute(weightDegreeColumn));
            newGraphNode.setEccentricity((Double) n.getAttribute(eccentricityColumn));
            newGraphNode.setEignvectorCentrality((Double) n.getAttribute(eignvColumn));
            newGraphNode.setHarmonicCloseness((Double) n.getAttribute(harmonicColumn));
            newGraphNode.setAuthority((Float) n.getAttribute(hitsAuthorityColumn));
            newGraphNode.setHub((Float) n.getAttribute(hitsHubColumn));
            newGraphNode.setPageRank((Double) n.getAttribute(pageRankColumn));

            nodeList.put(Integer.valueOf((String)n.getId()),newGraphNode);
        }
        pc.closeCurrentProject();

//        for (Integer key : nodeList.keySet()) {
//            System.out.println("!!!time is " + count);
//            GraphNode graphNode = nodeList.get(key);
//            System.out.print(" print live for " + key);
//            System.out.println(graphNode.toString());
//            if (History ==null)
//                System.out.println("History null");
//            else {
//                System.out.print(" History print for " + key);
//                GraphNode historyNode = History.nodeList.get(key);
//                System.out.println(historyNode.toString());
//            }
//        }
    }

    @Override
    void updateLabel(long tick) {
        if (History == null)
            return;

        no_global_events++;
        clusteringCoefficientRaised=false;
        if (this.averageClusteringCoefficient>History.averageClusteringCoefficient){
            no_global_clust++;
            clusteringCoefficientRaised=true;
        }

        for (Integer key : nodeList.keySet()) {
            no_events++;
            GraphNode graphNode = nodeList.get(key);

//            System.out.println("degree for node " + key +" at "+tick+" is " + graphNode.getDegree());
//            System.out.println("history degree for node " + key +" at "+tick+" is " + History.nodeList.get(key).getDegree());

            if (graphNode.getClusteringCoefficient() > History.nodeList.get(key).getClusteringCoefficient()) {
//                System.out.println("label set for key" + key);
                no_events_label++;
                graphNode.setLabel(true);
            }
        }
    }

    @Override
    void updateHistory() {
        this.History = null;
        this.History = new GraphNodeList(nodeList,averageClusteringCoefficient);
    }

    public GraphNodeList(HashMap<Integer,GraphNode> nodeList,double averageClusteringCoefficient) {
        this.nodeList = (HashMap) nodeList.clone();
        this.averageClusteringCoefficient = averageClusteringCoefficient;
    }

    @Override
    void clearHistory() {
        nodeList.clear();
    }

    @Override
    void feedToMLEngine(long tick){
        BufferedWriter writer = null;
        Integer temp=0;
//        try {
//            writer = new BufferedWriter( new FileWriter("Node_file_" + traceName  +".txt", true));
//            writer.write("tick starting " + tick);
//            for (Integer key : nodeList.keySet()) {
//                GraphNode node = nodeList.get(key);
//                writer.write(" node " + key + " attribute " + node.toString());
//            }
//            writer.write("no events " +no_events);
//            writer.write("no events label " +no_events_label);
//            writer.write("\n");
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            writer = new BufferedWriter( new FileWriter("Node_file_all" + ".csv", true));

            if (init) {
                for (int i = 0; i < MAX_NODES; i++) {
                    writer.write(GraphNode.columnStrings(i));
                }
                writer.write(";world_label");

                writer.write("\n");
                init=false;
            }

            for (Integer key : nodeList.keySet()) {
                GraphNode node = nodeList.get(key);
                writer.write(node.toMLString());
                temp++;
            }

            for (; temp< MAX_NODES; temp++){
                writer.write(GraphNode.toMLPadding());
            }
            if (clusteringCoefficientRaised)
                 writer.write(";"+1);
            else
                writer.write(";"+0);

            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printEvents(){
        System.out.println(" system global no events " + no_global_events);
        System.out.println(" system labeled no events " + no_global_clust);
        System.out.println(" node no events " +no_events);
        System.out.println(" node no events label " +no_events_label);
    }
}
