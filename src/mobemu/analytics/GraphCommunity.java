package mobemu.analytics;

import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.*;
import org.openide.util.Lookup;

import java.text.DecimalFormat;
import java.util.HashMap;

import static mobemu.analytics.GraphData.processMatrix;

public class GraphCommunity implements GraphPopulateData {

    public static final int COMMUNITY_EQUAL = 0;
    public static final int COMMUNITY_INCREASE = 1;
    public static final int COMMUNITY_DECREASE = 2;

    int size;
    double averageClusteringCoefficient;
    double pathLength;
    double diameter;
    int connectedComponentsCount;
    double density;
    double directed;
    double averageWeightedDegree;

    int Label;/*is community extending*/

    HashMap<Integer, Integer> borderNodes;
    int borderSize;
    int borderWeight;


    public GraphCommunity() {
        size=0;
        borderNodes = new HashMap<>();
        Label=COMMUNITY_EQUAL;
    }


    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.###");
        double scaledSize = size/100;
        double scaledBorderWeight = borderWeight/10000;
        double scaledPathLength = pathLength/10;
        double scaledWeightedDegree = averageWeightedDegree/1000;
        double scaledBorderSize = borderSize/100;

        return  " Label= " + labelToString(Label) +
                ", size= " + scaledSize +
                ", clusteringCoef= " + df.format(averageClusteringCoefficient) +
                ", pathLength= " + df.format(scaledPathLength) +
                ", diameter= " + df.format(diameter) +
                ", compCount= " + connectedComponentsCount +
                ", density= " + df.format(density) +
                ", directed= " + df.format(directed) +
                ", degree= " + df.format(scaledWeightedDegree) +
                ", borderS= " + df.format(scaledBorderSize) +
                ", borderW= " + df.format(scaledBorderWeight)
                + "\n";
    }

    public static String toMLPadding() {
        DecimalFormat df = new DecimalFormat("#.###");
        return false + ";" + 0 + ";" +df.format(0) + ";" +
                df.format(0) + ";" + df.format(0)+ ";" +
                0 + ";" + df.format(0) + ";" +
                0 + ";" + df.format(0) + ";" + df.format(0) +
                ";" + df.format(0);
    }

    public String toMLString() {
        DecimalFormat df = new DecimalFormat("#.###");
        return false + ";" + size + ";" +df.format(averageClusteringCoefficient) + ";" +
                df.format(pathLength) + ";" + df.format(diameter)+ ";" +
                connectedComponentsCount + ";" + df.format(density) + ";" +
                directed + ";" + df.format(averageWeightedDegree) + ";" + df.format(borderSize) +
                ";" + df.format(borderWeight);

    }

    public static String columnStrings(Integer key) {
        return "Label"+key.toString()+";size"+key.toString()+";clusteringCoef"+key.toString()+";pathLength"+key.toString()+
                ";diameter"+key.toString() + ";compCount"+key.toString()+";density"+key.toString()
                +";directed"+key.toString()+";degree"+key.toString()+";borderS"+key.toString()+";borderW"+key.toString();
    }

    public int isLabel() {
        return Label;
    }

    public void setLabelIncrease() {
        Label = COMMUNITY_INCREASE;
    }
    public void setLabelDecrease() { Label = COMMUNITY_DECREASE; }
    public void setLabelEqual() { Label = COMMUNITY_EQUAL; }

    public static String labelToString(int Label){
        switch (Label) {
            case COMMUNITY_DECREASE:
                return "community decrease";
            case COMMUNITY_INCREASE:
                return "community increase";
            case COMMUNITY_EQUAL:
                return "community equal";
            default:
                return "error";
        }
    }


    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    public int getBorderTotalWeight() {
        return borderWeight;
    }

    public void setBorderTotalWeight(int totalWeight) {
        this.borderWeight = totalWeight;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getAverageClusteringCoefficient() {
        return averageClusteringCoefficient;
    }

    public void setAverageClusteringCoefficient(double averageClusteringCoefficient) {
        this.averageClusteringCoefficient = averageClusteringCoefficient;
    }

    public double getPathLength() {
        return pathLength;
    }

    public void setPathLength(double pathLength) {
        this.pathLength = pathLength;
    }

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public int getConnectedComponentsCount() {
        return connectedComponentsCount;
    }

    public void setConnectedComponentsCount(int connectedComponentsCount) {
        this.connectedComponentsCount = connectedComponentsCount;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public double getDirected() {
        return directed;
    }

    public void setDirected(double directed) {
        this.directed = directed;
    }

    public double getAverageWeightedDegree() {
        return averageWeightedDegree;
    }

    public void setAverageWeightedDegree(double averageWeightedDegree) {
        this.averageWeightedDegree = averageWeightedDegree;
    }

    @Override
    public void populateData(GraphMatrix graphMatrix) {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

//        processRandomMatrix(100,workspace,0.10);

        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);

        processMatrix(graphMatrix,graphModel);

        setSize(graphMatrix.getNoNodes());

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


        setAverageClusteringCoefficient( clusteringCoefficient.getAverageClusteringCoefficient());
        setPathLength(distance.getPathLength());
        setDiameter(distance.getDiameter());
        setConnectedComponentsCount(connectedComponents.getConnectedComponentsCount());
        setDensity(density.getDensity());
        setAverageWeightedDegree(weightedDegree.getAverageDegree());



        pc.closeCurrentProject();
    }


    public void populateData(HashMap<Integer, Integer> borderNodes) {
        borderSize=0;
        borderWeight=0;
        for (Integer key : borderNodes.keySet()){
            borderSize++;
            Integer weight= borderNodes.get(key);
            borderWeight = borderWeight + weight;
        }

        this.borderNodes = borderNodes;
    }
}
