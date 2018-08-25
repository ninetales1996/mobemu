package mobemu;

import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.*;
import org.openide.util.Lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static mobemu.analytics.GraphData.processRandomMatrix;

public class MobEmuRand {

    public static void main(String[] args) {
           /* compute number of groups per wiring probability */
            int count = 100;
            double group_number = 0 ;
            double wiring = 0.5;
            int noNodes = 10;
            double total_clustering = 0;
            double no_clustering = 0;
            HashMap<Double,Double> wireGroups = new HashMap<>();
//            double wiring = 0.05;

                for (int i = 0 ; i < count ; i++){
                    ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
                    pc.newProject();
                    Workspace workspace = pc.getCurrentWorkspace();
                    HashMap<Integer, List<Integer>> nodeCommunityMap;

                    processRandomMatrix(noNodes, workspace, wiring);

                    GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
                    Graph graph = graphModel.getGraph();

                    Modularity modularity = new Modularity();
                    modularity.execute(graphModel);
                    modularity.setUseWeight(true);
                    ClusteringCoefficient clusteringCoefficient = new ClusteringCoefficient();

                    Column modColumn = graphModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);

    //                for (Node n : graph.getNodes()) {
    //                    System.out.println(n.getId() + " has mod " + n.getAttribute(modColumn) + " test " + Integer.valueOf((String) n.getId()));
    //                }

                    nodeCommunityMap = new HashMap<>();

                    for (Node n : graph.getNodes()) {
                        int modId = (int) n.getAttribute(modColumn);
                        if (nodeCommunityMap.containsKey(modId)) {
                            List<Integer> nodeSet = nodeCommunityMap.get(modId);
                            nodeSet.add(Integer.valueOf((String) n.getId()));
                            nodeCommunityMap.replace(modId, nodeSet);
                        } else {
                            List<Integer> nodeList = new ArrayList<>();
                            nodeList.add(Integer.valueOf((String) n.getId()));
                            nodeCommunityMap.put(modId, nodeList);
                        }
                        //add to node list per integer
                    }

                    for (Integer key : nodeCommunityMap.keySet()) {
                        group_number++;
    //                    List<Integer> nodeList = nodeCommunityMap.get(key);
    //                    System.out.print("MOD ID " + key);
    //                    for (int j = 0; j < nodeList.size(); j++) {
    //                        System.out.print(" " + nodeList.get(j) + " ");
    //                    }
    //                    System.out.println(" ");
                    }
                    clusteringCoefficient.execute(graphModel);
                    if (clusteringCoefficient.getAverageClusteringCoefficient()>0) {
                        total_clustering = total_clustering + clusteringCoefficient.getAverageClusteringCoefficient();
                        no_clustering = no_clustering + 1;
                    }
                    pc.closeCurrentProject();
                }

                double result = group_number/count;
                wireGroups.put(wiring,result);
                System.out.println(" iterated " + count + " times " + " with no of nodes " + noNodes + " with wiring " + wiring + " results in noOfGroups " + result );
                System.out.println(" avg clustering coef " + (total_clustering/no_clustering) );

    }
}

