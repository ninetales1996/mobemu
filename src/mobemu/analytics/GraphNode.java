package mobemu.analytics;

import java.text.DecimalFormat;

public class GraphNode {
    double ClusteringCoefficient;
    double Degree;
    double EignvectorCentrality;
    double BetweenessCentrality;
    double ClosenessCentrality;
    double HarmonicCloseness;
    double Eccentricity;
    float Authority;
    float Hub;
//    double Strongness;
    double PageRank;
    boolean Label;

//    public double getStrongness() {
//        return Strongness;
//    }
//
//    public void setStrongness(double strongness) {
//        Strongness = strongness;
//    }

    public double getPageRank() {
        return PageRank;
    }

    public void setPageRank(double pageRank) {
        PageRank = pageRank;
    }

    public boolean isLabel() {
        return Label;
    }

    public void setLabel(boolean label) {
        Label = label;
    }

    public GraphNode() {
        Label = false;
        ClusteringCoefficient = 0;
        Degree = 0;
        EignvectorCentrality = 0;
        BetweenessCentrality = 0;
        ClosenessCentrality = 0;
        HarmonicCloseness = 0;
        Eccentricity = 0;
        Authority = 0;
        Hub = 0;
//        Strongness = 0;
        PageRank = 0;
        Label = false;
    }


    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.###");

        return  " Label= " + Label +
                " ClusteringCoefficient= " + df.format(ClusteringCoefficient) +
                ", Degree=" + df.format(Degree) +
                ", EignvectorCentrality=" + df.format(EignvectorCentrality) +
                ", BetweenessCentrality=" + df.format(BetweenessCentrality) +
                ", ClosenessCentrality=" + df.format(ClosenessCentrality) +
                ", HarmonicCloseness=" + df.format(HarmonicCloseness) +
                ", Eccentricity=" + df.format(Eccentricity) +
                ", Authority=" + df.format(Authority) +
                ", Hub=" + df.format(Hub) +
//                ", Strongness=" + Strongness +
                ", PageRank=" + df.format(PageRank)
                + "\n";
    }

    public static String toMLPadding() {
        DecimalFormat df = new DecimalFormat("#.###");
        return false + ";" + df.format(0) + ";" +df.format(0) + ";" +
                df.format(0) + ";" + df.format(0)+ ";" +
                df.format(0) + ";" + df.format(0) + ";" +
                df.format(0) + ";" + df.format(0) + ";" + df.format(0) +
                ";" + df.format(0);
    }

    public String toMLString() {
        DecimalFormat df = new DecimalFormat("#.###");
        return false + ";" + df.format(ClusteringCoefficient) + ";" +df.format(Degree) + ";" +
                df.format(EignvectorCentrality) + ";" + df.format(BetweenessCentrality)+ ";" +
                df.format(ClosenessCentrality) + ";" + df.format(HarmonicCloseness) + ";" +
                df.format(Eccentricity) + ";" + df.format(Authority) + ";" + df.format(Hub) +
                ";" + df.format(PageRank);

    }

    public static String columnStrings(Integer key) {
        return "Label"+key.toString()+";ClusteringCoefficient"+key.toString()+";Degree"+key.toString()+";EignvectorCentrality"+key.toString()+
                ";BetweenessCentrality"+key.toString() + ";ClosenessCentraliy"+key.toString()+";HarmonicCloseness"+key.toString()
                +";Eccentricity"+key.toString()+";Authority"+key.toString()+";Hub"+key.toString()+";PageRank"+key.toString();
    }

    public double getClusteringCoefficient() {
        return ClusteringCoefficient;
    }

    public void setClusteringCoefficient(double clusteringCoefficient) {
        ClusteringCoefficient = clusteringCoefficient;
    }

    public double getDegree() {
        return Degree;
    }

    public void setDegree(double degree) {
        Degree = degree;
    }

    public double getEignvectorCentrality() {
        return EignvectorCentrality;
    }

    public void setEignvectorCentrality(double eignvectorCentrality) {
        EignvectorCentrality = eignvectorCentrality;
    }

    public double getBetweenessCentrality() {
        return BetweenessCentrality;
    }

    public void setBetweenessCentrality(double betweenessCentrality) {
        BetweenessCentrality = betweenessCentrality;
    }

    public double getClosenessCentrality() {
        return ClosenessCentrality;
    }

    public void setClosenessCentrality(double closenessCentrality) {
        ClosenessCentrality = closenessCentrality;
    }

    public double getHarmonicCloseness() {
        return HarmonicCloseness;
    }

    public void setHarmonicCloseness(double harmonicCloseness) {
        HarmonicCloseness = harmonicCloseness;
    }

    public double getEccentricity() {
        return Eccentricity;
    }

    public void setEccentricity(double eccentricity) {
        Eccentricity = eccentricity;
    }

    public float getAuthority() {
        return Authority;
    }

    public void setAuthority(float authority) {
        Authority = authority;
    }

    public float getHub() {
        return Hub;
    }

    public void setHub(float hub) {
        Hub = hub;
    }

}
