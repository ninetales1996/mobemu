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


    int size;
    double averageClusteringCoefficient;
    double pathLength;
    double diameter;
    int connectedComponentsCount;
    double density;
    double directed;
    double averageWeightedDegree;

    boolean Label;/*is community extending*/

    HashMap<Integer, Integer> borderNodes;
    int borderSize;
    int borderWeight;


    public GraphCommunity() {
        size=0;
        borderNodes = new HashMap<>();
        Label=false;
    }


    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.###");

        return  " Label= " + Label +
                ", size=" + size +
                ", clusteringCoef=" + df.format(averageClusteringCoefficient) +
                ", pathLength=" + df.format(pathLength) +
                ", diameter=" + df.format(diameter) +
                ", compCount=" + connectedComponentsCount +
                ", density=" + df.format(density) +
                ", directed=" + df.format(directed) +
                ", degree=" + df.format(averageWeightedDegree) +
                ", borderS=" + df.format(borderSize) +
                ", borderW=" + df.format(borderWeight)
                + "\n";
    }

    public boolean isLabel() {
        return Label;
    }

    public void setLabel(boolean label) {
        Label = label;
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
