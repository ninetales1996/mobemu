package mobemu;

import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.*;
import org.openide.util.Lookup;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static mobemu.analytics.GraphData.processRandomMatrix;

public class MobEmuEvaluate {
    public static void main(String[] args) throws IOException {

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

//    node-map per integer;
        HashMap<Integer,List<Integer>> nodeCommunityMap;

        processRandomMatrix(9,workspace,0.05);

        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);

//        processMatrix(graphMatrix,graphModel);

        Graph graph = graphModel.getGraph();
        System.out.println("no of edges is " + graph.getEdgeCount() + " no of nodes is " + graph.getNodeCount());


        ClusteringCoefficient clusteringCoefficient = new ClusteringCoefficient();
        GraphDistance distance = new GraphDistance();
        ConnectedComponents connectedComponents = new ConnectedComponents();
        Degree myDegree = new Degree();
        EigenvectorCentrality eignv = new EigenvectorCentrality();
        GraphDensity density = new GraphDensity();
        Hits hits = new Hits();
        PageRank pageRank = new PageRank();
        WeightedDegree weightedDegree = new WeightedDegree();


        Modularity modularity = new Modularity();
        modularity.execute(graphModel);
        modularity.setUseWeight(true);


        Column modColumn = graphModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);

        for (Node n : graph.getNodes()) {
            System.out.println(n.getId() + " has mod " + n.getAttribute(modColumn) + " test " + Integer.valueOf((String)n.getId()));
        }

        nodeCommunityMap = new HashMap<>();

        for (Node n : graph.getNodes()) {
            int modId = (int) n.getAttribute(modColumn);
            if (nodeCommunityMap.containsKey(modId)){
                List<Integer> nodeSet = nodeCommunityMap.get(modId);
                nodeSet.add(Integer.valueOf((String) n.getId()));
                nodeCommunityMap.replace(modId,nodeSet);
            }
            else {
                List<Integer> nodeList = new ArrayList<>();
                nodeList.add(Integer.valueOf((String) n.getId()));
                nodeCommunityMap.put(modId,nodeList);
            }
            //add to node list per integer
        }

        for (Integer key : nodeCommunityMap.keySet()) {
            List<Integer> nodeList = nodeCommunityMap.get(key);
            System.out.print("MOD ID " + key);
            for(int i=0;i<nodeList.size();i++){
                System.out.print(" " + nodeList.get(i) + " ");
            }
            System.out.println(" ");
        }




        clusteringCoefficient.execute(graphModel);
        distance.execute(graphModel);
        connectedComponents.execute(graphModel);
        myDegree.execute(graphModel);
        eignv.execute(graphModel);
        density.execute(graphModel);
        hits.execute(graphModel);
        pageRank.execute(graphModel);
        weightedDegree.execute(graphModel);


        System.out.println("avg clustering data " + clusteringCoefficient.getAverageClusteringCoefficient());
        System.out.println("distance path length " + distance.getPathLength());
        System.out.println("distance diameter " + distance.getDiameter());
        System.out.println("get component count " + connectedComponents.getConnectedComponentsCount());
        System.out.println("get densitiy " + density.getDensity());
        System.out.println("get directed " + density.getDirected());
//        System.out.println("get hits epsilon " + hits.getEpsilon());
//        System.out.println("get hits undirected " + hits.getUndirected());
        System.out.println("get page rank probability " + pageRank.getProbability());
//        System.out.println("get page rank epsilon " + pageRank.getEpsilon());
//        System.out.println("get page rank directed " + pageRank.getDirected());
        System.out.println("avg weighted degree " + weightedDegree.getAverageDegree());


        Column betweeennessColumn = graphModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
        Column closenessColumn = graphModel.getNodeTable().getColumn(GraphDistance.CLOSENESS);
        Column eccentricityColumn = graphModel.getNodeTable().getColumn(GraphDistance.ECCENTRICITY);
        Column harmonicColumn = graphModel.getNodeTable().getColumn(GraphDistance.HARMONIC_CLOSENESS);
        Column strongColumn = graphModel.getNodeTable().getColumn(ConnectedComponents.STRONG);
        Column weakColumn = graphModel.getNodeTable().getColumn(ConnectedComponents.WEAKLY);
        Column avgDegree = graphModel.getNodeTable().getColumn(Degree.AVERAGE_DEGREE);
        Column normalDegree = graphModel.getNodeTable().getColumn(Degree.DEGREE);
        Column eignvColumn = graphModel.getNodeTable().getColumn(EigenvectorCentrality.EIGENVECTOR);
        Column hitsAuthorityColumn = graphModel.getNodeTable().getColumn(Hits.AUTHORITY);
        Column hitsHubColumn = graphModel.getNodeTable().getColumn(Hits.HUB);
        Column pageRankColumn = graphModel.getNodeTable().getColumn(PageRank.PAGERANK);
        Column weightDegreeColumn = graphModel.getNodeTable().getColumn(WeightedDegree.WDEGREE);
        Column clusteringColumn = graphModel.getNodeTable().getColumn(ClusteringCoefficient.CLUSTERING_COEFF);

//        for (Node n : graph.getNodes()) {
//            System.out.println(n.getId() + " has strong " + n.getAttribute(strongColumn));
////            System.out.println(n.getId() + " has weak " + n.getAttribute(weakColumn));
//            System.out.println(n.getId() + " has rank " + n.getAttribute(pageRankColumn));
//
//        }
        System.out.println("clustering " + clusteringCoefficient.getTriangesReuslts());


        PrintWriter writer = new PrintWriter("test_file.txt", "UTF-8");
        writer.println("The first line");
        writer.println("The second line");
        writer.close();

        FileWriter writer2 = new FileWriter("test_file.txt", true);
        writer2.write("third line");
        writer2.write("fourth_line");


        writer2.close();

        /*MODULARITY!!!!!!!!!*/
//        Modularity modularity = new Modularity();
//        modularity.execute(graphModel);
//        modularity.setUseWeight(true);
//
//        Column modColumn = graphModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);
//

        /*MODULARITY!!!!!!!!!*/


//        for (Node n : graph.getNodes()) {
//            System.out.println(n.getId() + " has eignv degree of " + n.getAttribute(eignvColumn));
//        }

//        for (Node n : graph.getNodes()) {
//            System.out.println(n.getId() + " has normal degree of " + n.getAttribute(normalDegree));
//        }



//
//        for (Node n : graph.getNodes()) {
//            System.out.println(n.getId() + " has betweness centrality of " + n.getAttribute(betweeennessColumn));
//        }
//
//        for (Node n : graph.getNodes()) {
//            System.out.println(n.getId() + " has clustering centrality of " + n.getAttribute(clusteringColumn));
//        }
//
//        for (Node n : graph.getNodes()) {
//            System.out.println(n.getId() + " has closeness centrality of " + n.getAttribute(closenessColumn));
//        }
//
//        for (Node n : graph.getNodes()) {
//            System.out.println(n.getId() + " has eccentiricy centrality of " + n.getAttribute(eccentricityColumn));
//        }
//
//        for (Node n : graph.getNodes()) {
//            System.out.println(n.getId() + " has harmonic centrality of " + n.getAttribute(harmonicColumn));
//        }


    }
}
