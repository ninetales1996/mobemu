package mobemu.analytics;

import org.gephi.appearance.api.AppearanceController;
import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;

import java.io.BufferedWriter;
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

    public static int no_ev_env_increase = 0;
    public static int no_ev_env_decrease = 0;
    public static int no_ev_env_total = 0;
    public static int no_ev_comm_increase = 0;
    public static int no_ev_comm_decrease = 0;
    public static int no_ev_comm_total = 0;

    public static int MAX_COMMUNITIES = 15;

    private String traceName;
    private GraphCommunityList History;

    public boolean init=true;

    private int CommunityLabel;

    public int count_isolated=0;

    public double total_communities=0;
    public double total_isolated=0;
    public double time_counter=0;

    public double total_clustering=0;
    public double no_clustering=0;

    public void print_state(){
        double comm = total_communities/time_counter;
        double isol = total_isolated/time_counter;
        System.out.println("times elapsed " + time_counter + " total comm " + total_communities + " total iso " + total_isolated + " average communities " + comm + " average isolated " + isol  );
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

    public GraphCommunityList(String traceName) {
//        History = new GraphCommunityList();
        this.traceName=traceName;
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
//            for (Integer node_no: nodeSet){
//                System.out.println(" node  " + node_no);
//            }
//        }


        pc.closeCurrentProject();
/*TO REMOVE*/

        //populate first entry TO DO
        //close workspace
        GraphCommunity wholeGraphView= new GraphCommunity();
        wholeGraphView.populateData(graphMatrix);
        communityList.put(BIG_GRAPH_ENTRY,wholeGraphView);

        count_isolated =0;
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

                if (graphView.getAverageClusteringCoefficient()>0) {
                    total_clustering = total_clustering + graphView.getAverageClusteringCoefficient();
                    no_clustering = no_clustering + 1;
                }
//                System.out.println("community " + key + " has border size " + graphView.getBorderSize() + " with border weight " + graphView.getBorderTotalWeight());

                communityList.put(community_key,graphView);
                community_key++;
            }
            else {
//                GraphCommunity graphView= new GraphCommunity();
//                System.out.println("ISOLATED NODE");
                count_isolated++;
//              System.out.println("dummy group " + key + " for node "  + nodeList.get(0) );
//                communityList.put(key,graphView);
            }
        }

//        System.out.println("count iso " + count_isolated);

        total_communities = total_communities + community_key - 1;
        total_isolated = total_isolated + count_isolated;
//        System.out.println("total iso " + total_isolated);
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

        no_ev_env_total++;
        if (communityList.size() > History.communityList.size()){
            no_ev_env_increase++;
            setCommunityLabel(COMMUNITIES_INCREASE);
        }
        else if (History.communityList.size() > communityList.size()){
            no_ev_env_decrease++;
            setCommunityLabel(COMMUNITIES_DECREASE);
        }

//        System.out.println("community label at tick " + tick + " is " + getCommunityLabel());
//        for (Integer key : History.communityList.keySet()){
//            System.out.println( "History for key " + key + " at tick " + tick  + " is " + History.communityList.get(key).toString());
//        }


//        System.out.println(" number of communities evolution " + getCommunityLabel());
        if (getCommunityLabel()==COMMUNITIES_EQUAL) {
            for (Integer key : communityList.keySet()){
                no_ev_comm_total++;
                GraphCommunity graphCommunity = communityList.get(key);
//                System.out.println( " key " + key + "  tick " + tick  + graphCommunity.toString());
//                System.out.println(  " key "  + key + "history at "  + tick + " is  " + History.communityList.get(key).toString());
                if (graphCommunity.getSize()>0) {
                    if (graphCommunity.getSize() > History.communityList.get(key).getSize()) {
                        no_ev_comm_increase++;
                        graphCommunity.setLabelIncrease();
                        communityList.replace(key, graphCommunity);
//                        System.out.println("label set at key " + key);
                    }
                    else if (History.communityList.get(key).getSize()>graphCommunity.getSize()){
                        graphCommunity.setLabelDecrease();
                        no_ev_comm_decrease++;
                        communityList.replace(key, graphCommunity);
                    }
                }

//                if (graphCommunity.getSize()) {
//                    System.out.println( " key " + key + "  tick " + tick  + graphCommunity.toString());
//                }
//                else
//                    System.out.println( " key " + key + "  tick " + tick + " DUMMY NODE");
            }
            no_ev_comm_total--;
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
        BufferedWriter writer;
        Integer temp=0;

        System.out.println(" tick is " + tick);
//        try {
//            writer = new BufferedWriter(new FileWriter("Community_file_" + traceName + ".txt", true));
//
//            writer.write("tick is "+ tick );
//            if (getCommunityLabel()==COMMUNITIES_EQUAL) {
//                writer.write(" number of communities equal to " + (communityList.size()-1));
//                writer.write(" number of isolated nodes " + count_isolated);
//            }
//            else if (getCommunityLabel()==COMMUNITIES_INCREASE){
//                writer.write(" number of communities increased to " + (communityList.size()-1));
//                writer.write(" number of isolated nodes " + count_isolated);
//            }
//            else if (getCommunityLabel()==COMMUNITIES_DECREASE){
//                writer.write(" number of communities decrease to " + (communityList.size()-1));
//                writer.write(" number of isolated nodes " + count_isolated);
//            }
//
//            for (Integer key : communityList.keySet()) {
//                GraphCommunity graphCommunity = communityList.get(key);
//
//                if (graphCommunity.getSize() > 0) {
//                    writer.write("\n");
//                    writer.write(" community NUMBER " + key);
//                    writer.write(graphCommunity.toString());
//                }
//
//            }
//            writer.write(" total number of env events is "+ no_ev_env_total + " increase ev in no groups " + no_ev_env_increase + " decrease ev in no groups " + no_ev_env_decrease);
//            writer.write(" total number of community events is " + no_ev_comm_total + " community increase " + no_ev_comm_increase + " community decrease " +no_ev_comm_decrease);
//            writer.write(" end tick " + tick  + "\n" + "----------------" + "\n" );
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            writer = new BufferedWriter(new FileWriter("Community_file_all" + ".csv", true));

            if (init) {
                for (int i = 0; i < MAX_COMMUNITIES; i++) {
                    writer.write(GraphCommunity.columnStrings(i));
                }
                writer.write(";world_label");
                writer.write("\n");
                init=false;
            }

            for (Integer key : communityList.keySet()) {
                GraphCommunity graphCommunity = communityList.get(key);

                if (graphCommunity.getSize() > 0) {
                    writer.write(graphCommunity.toMLString());
                    temp++;
                }
            }
            for ( ; temp<MAX_COMMUNITIES;temp++){
                writer.write(GraphCommunity.toMLPadding());
            }
            writer.write(";"+getCommunityLabel());
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printEvents(){
        System.out.println(" total number of env events is "+ no_ev_env_total + " increase ev in no groups " + no_ev_env_increase + " decrease ev in no groups " + no_ev_env_decrease);
        System.out.println(" total number of community events is " + no_ev_comm_total + " community increase " + no_ev_comm_increase + " community decrease " +no_ev_comm_decrease);
    }
}
