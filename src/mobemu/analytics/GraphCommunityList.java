package mobemu.analytics;

import org.gephi.appearance.api.AppearanceController;
import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphCommunityList extends GraphData {
    public static final int BIG_GRAPH_ENTRY = 0;

    public static final int COMMUNITIES_EQUAL = 0;
    public static final int COMMUNITIES_INCREASE = 1;
    public static final int COMMUNITIES_DECREASE = 2;

    private GraphCommunityList History;

    private int CommunityLabel;

    public int count_isolated=0;

    public double total_communities=0;
    public double total_isolated=0;
    public double time_counter=0;

    public void print_state(){
        double comm = total_communities/time_counter;
        double isol = total_isolated/time_counter;
        System.out.println("times elapsed " + time_counter + " total comm " + total_communities + " average communities " + comm + " total iso " + total_isolated + " average isolated " + isol  );
    }

    public int getCommunityLabel() {
        return CommunityLabel;
    }

    public void setCommunityLabel(int communityLabel) {
        CommunityLabel = communityLabel;
    }



    HashMap<Integer,GraphCommunity> communityList;
//    node-map per integer;
    HashMap<Integer,List<Integer>> nodeCommunityMap;

    public GraphCommunityList() {
//        History = new GraphCommunityList();
        communityList=new HashMap<>();
        nodeCommunityMap=new HashMap<>();
    }

    @Override
    public void populateData(GraphMatrix graphMatrix) {

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        time_counter++;

        if (!communityList.isEmpty()){
            System.out.println("communityList LIST NOT CLEARED");
        }
        if (!nodeCommunityMap.isEmpty()){
            System.out.println("nodeCommunityMap LIST NOT CLEARED");
        }

//        processRandomMatrix(graphMatrix.getNoNodes(),workspace,0.2);

        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
        AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);

        processMatrix(graphMatrix, graphModel);
        Graph graph = graphModel.getGraph();

        Modularity modularity = new Modularity();
        modularity.execute(graphModel);
        modularity.setUseWeight(true);

        Column modColumn = graphModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);

//        graphMatrix.printGraphMatrix();

//        for (Node n : graph.getNodes()) {
//            System.out.println(" node " + n.getId() + " has mod " + n.getAttribute(modColumn));
//        }


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

//        System.out.println(" nodeCommunityMap.size() "+ nodeCommunityMap.size());
//        for (Integer key :  nodeCommunityMap.keySet()){
//            List<Integer> nodeSet = nodeCommunityMap.get(key);
//
//
//            System.out.print(" key " + key + " has " + nodeSet.size() + " elements ");
//            System.out.println("-------------------------------------------------------");
////            for (Integer node_no: nodeSet){
////                System.out.println(" node  " + node_no);
////            }
//        }


        pc.closeCurrentProject();
/*TO REMOVE*/

        //populate first entry TO DO
        //close workspace
        GraphCommunity wholeGraphView= new GraphCommunity();
        wholeGraphView.populateData(graphMatrix);
        communityList.put(BIG_GRAPH_ENTRY,wholeGraphView);

        int community_key =1;
        for (Integer key : nodeCommunityMap.keySet()) {

            List<Integer> nodeList = nodeCommunityMap.get(key);
            if (nodeList.size() > 1){
                GraphMatrix communityGraphMatrix = new GraphMatrix(graphMatrix,nodeList);
//                System.out.println("matrix " + key + " with size " + nodeList.size());
//            communityGraphMatrix.printGraphMatrix();
                HashMap<Integer,Integer> borderNodes = GraphMatrix.computeBorderNodes(graphMatrix,nodeList);

//                System.out.println("community key " +community_key + " for " + key );
//            for (Integer key_border :borderNodes.keySet()){
//                Integer weight = borderNodes.get(key_border);
//                System.out.println("community " + key + " has border " + key_border + " with weight "+ weight);
//            }

                GraphCommunity graphView= new GraphCommunity();
                graphView.populateData(communityGraphMatrix);
                graphView.populateData(borderNodes);

//                System.out.println("community " + key + " has border size " + graphView.getBorderSize() + " with border weight " + graphView.getBorderTotalWeight());

                communityList.put(community_key,graphView);
                community_key++;
            }
            else {
//                GraphCommunity graphView= new GraphCommunity();
                count_isolated++;
//              System.out.println("dummy group " + key + " for node "  + nodeList.get(0) );
//                communityList.put(key,graphView);
            }
        }

        total_communities = total_communities + community_key - 1;
        total_isolated = total_isolated + count_isolated;
        //for entries
            //obtain graph map from node map
            //obtain links-map from node map
            //new community
            //add link-map to community
            //new workspace
            //process graph matrix -> graph model
            //communityList.populateData(graphMatrix_i)
            //add node to community-map
            //close workspace

    }

    @Override
    void updateLabel(long tick) {

        if (History == null) {
            System.out.println("History NUll in update Label");
            return;
        }


        setCommunityLabel(COMMUNITIES_EQUAL);

//        System.out.println( "community list size " + communityList.size() + " vs " + History.communityList.size());

        if (communityList.size() > History.communityList.size()){
            setCommunityLabel(COMMUNITIES_INCREASE);
        }
        else if (History.communityList.size() > communityList.size()){
            setCommunityLabel(COMMUNITIES_DECREASE);
        }

//        System.out.println("community label at tick " + tick + " is " + getCommunityLabel());
//        for (Integer key : History.communityList.keySet()){
//            System.out.println( "History for key " + key + " at tick " + tick  + " is " + History.communityList.get(key).toString());
//        }


//        System.out.println(" number of communities evolution " + getCommunityLabel());
        if (getCommunityLabel()==COMMUNITIES_EQUAL) {
            for (Integer key : communityList.keySet()){
                GraphCommunity graphCommunity = communityList.get(key);
//                System.out.println( " key " + key + "  tick " + tick  + graphCommunity.toString());
//                System.out.println(  " key "  + key + "history at "  + tick + " is  " + History.communityList.get(key).toString());
                if (graphCommunity.getSize()>0) {
                    if (graphCommunity.getSize() > History.communityList.get(key).getSize()) {
                        graphCommunity.setLabel(true);
                        communityList.replace(key, graphCommunity);
//                        System.out.println("label set at key " + key);
                    }
                }
//                if (graphCommunity.getSize()) {
//                    System.out.println( " key " + key + "  tick " + tick  + graphCommunity.toString());
//                }
//                else
//                    System.out.println( " key " + key + "  tick " + tick + " DUMMY NODE");
            }
        }
    }

    @Override
    void updateHistory() {
        this.History = null;
        this.History = new GraphCommunityList(communityList,count_isolated);
    }

    public GraphCommunityList(HashMap<Integer,GraphCommunity> communityList,int count_isolated) {
        this.communityList = (HashMap) communityList.clone();
        this.count_isolated = count_isolated;
    }

    @Override
    void clearHistory() {
        count_isolated=0;
        communityList.clear();
        nodeCommunityMap.clear();
    }

    @Override
    void feedToMLEngine(long tick) {
        FileWriter writer;

//        System.out.println(" tick is " + tick);
        try {
            writer = new FileWriter("Community_file.txt", true);


            if (getCommunityLabel()==COMMUNITIES_EQUAL) {

                writer.write("tick is " + tick + " number of communities equal to " + communityList.size());
                writer.write("tick is " + tick + " number of isolated nodes " + count_isolated + "\n" );
            }
            else if (getCommunityLabel()==COMMUNITIES_INCREASE){
                writer.write("tick is " + tick + " number of communities increase to " + communityList.size());
                writer.write("tick is " + tick + " number of isolated nodes " + count_isolated + "\n" );
            }

            else if (getCommunityLabel()==COMMUNITIES_DECREASE){
                writer.write("tick is " + tick + " number of communities decrease to " + communityList.size());
                writer.write("tick is " + tick + " number of isolated nodes " + count_isolated + "\n" );
            }

            for (Integer key : communityList.keySet()) {
                GraphCommunity graphCommunity = communityList.get(key);

                if (graphCommunity.getSize() > 0)
                    writer.write("tick is " + tick + " community NUMBER " + key + " " + graphCommunity.toString());
            }
            writer.write("\n" );
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
