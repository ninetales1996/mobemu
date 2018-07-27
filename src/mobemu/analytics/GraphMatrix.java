package mobemu.analytics;


import java.util.HashMap;
import java.util.List;

public class GraphMatrix {

    public static final int NOT_MODIFIED = 0;
    public static final int MODIFIED = 1;

    public static final int MINIMUM_WEIGHT = 5;
    public static final int NULL_WEIGHT = 0;
    public static final int DUMMY_WEIGHT = 7;

    private int alpha;
    private int tally;
    private int minimumSparseCoef;
    private int count = 0; /* count is used to see how many edges do we have (if it makes sense to consider a graph analyze)*/
    private int noNodes;
    private int[][] weights;

    public GraphMatrix(int alpha, int tally, int noNodes){
        this.alpha = alpha;
        this.tally = tally;
        this.minimumSparseCoef = tally / 5;
        this.noNodes = noNodes;
        this.weights = new int[noNodes][noNodes];

        for ( int i = 0; i < noNodes; i++) {
            for (int j = 0; j < noNodes; j++) {
                weights[i][j] = NULL_WEIGHT; /* 0 is both NOT_MODIFIED and weight value here TO DO modify for consistency */
                if (i==j)
                    weights[i][j]=DUMMY_WEIGHT;
            }
        }
    }

    public GraphMatrix(GraphMatrix graphMatrix, List<Integer> nodeSet) {
        this.weights = new int[nodeSet.size()][nodeSet.size()];
        this.noNodes = nodeSet.size();

        for (int i = 0; i < nodeSet.size(); i++) {
            for (int j = 0; j < nodeSet.size(); j++) {
                if (nodeSet.get(i) >= nodeSet.get(j)) {
                    this.weights[i][j] = graphMatrix.getValue(nodeSet.get(i), nodeSet.get(j));
                }
            }
        }
    }

    public int getValue(int i,int j){
        return weights[i][j];
    }

    public boolean hasValue(int i,int j){
        if (weights[i][j]==NULL_WEIGHT)
            return false;
        return true;
    }

    public int getNoNodes() {
        return noNodes;
    }

    public int getAgedEdgeCount(){
        return count;
    }

    public void printEdgeCount(int agedEdgeCount){
//        System.out.println("size i " +weights);
//        System.out.println("size j " +weights[weights.length].length);
//        System.out.println("no nodes" + noNodes);
        int count_mod=0;
        int weight_count=0;
        int noErrors=0;

        for (int i=0; i < noNodes; i++){
            for (int j = 0; j < noNodes; j++){
                if ((i==j)&&(weights[i][j]!=DUMMY_WEIGHT)){
                    noErrors++;
                }

                if ((i>j) && (weights[i][j] > NULL_WEIGHT)){
                    weight_count++;
                }
                if ((j>i) && weights[i][j] == MODIFIED){
                    count_mod++;
                }
            }
        }

        if (weight_count!=agedEdgeCount){
            System.out.println("incorret edge count");
        }
        else {
            System.out.println("correct edge count");
        }

        System.out.println(" mod " + count_mod + " no " + weight_count +  " errors " + noErrors);

    }

    public void setAgingCoefficients(int alpha, int tally){
        this.alpha = alpha;
        this.tally = tally;
    }

    public void printGraphMatrix(){
        for ( int i = 0; i < noNodes; i++) {
            for (int j = 0; j < noNodes; j++) {
                System.out.print(" " + weights[i][j] + " ");
            }
            System.out.println(" ");
        }
        for ( int i = 0; i < noNodes; i++) {
            System.out.print("***");
        }
        System.out.println(" ");
        System.out.println("there are " + count + " elements ");
    }

    /* lower graph matrix is used for weights computation while upper matrix is a set of flags for memorize the status of node encounters
       (if node i already met node j at time);
       flag is modified or not flag for tick and w is weight of connection:
       0fff
       w0ff
       ww0f
       www0*/

    public void update(int observed_id,int observer_id){

        /*error defies our graph logic*/
        if (observed_id==observer_id)
            return;/*error that defies our graph logic. node cannot encounter itself*/

        /*we enter matrix logic. i and j easier to track*/
        int i = observed_id;
        int j = observer_id;

        if (i>j) {
            if (weights[j][i]==MODIFIED)/*TO DO CAP AT MAXIMUM!!!*/
                return;/*already modified at this timestamp*/

            if (weights[i][j]==NULL_WEIGHT)
                weights[i][j] = minimumSparseCoef;
            else
                weights[i][j]=(weights[i][j]*alpha+tally)/(alpha+1);
            weights[j][i]=MODIFIED;
        }
        else {
            if (weights[i][j]==MODIFIED)
                return;/*already modified for this step*/

            if (weights[j][i]==NULL_WEIGHT)
                weights[j][i] = minimumSparseCoef;
            else
                weights[j][i] = (weights[j][i] * alpha + tally) / (alpha + 1);
            weights[i][j]=MODIFIED;
        }
    }


    public void ageFormula(boolean sparse){
        this.count=0;
        for ( int i = 0; i < noNodes; i++) {
            for (int j = i+1; j < noNodes; j++) {
                if (weights[i][j]==MODIFIED){
                    count++;
                    weights[i][j]=NOT_MODIFIED;
                }
                else if (weights[j][i]!=NULL_WEIGHT){
                    count++;

                    if (sparse){
                        if ( weights[j][i] > minimumSparseCoef )
                            weights[j][i]=(weights[j][i]*alpha)/(alpha+1);
                    }
                    else { /* graph is dense */
                        weights[j][i] = (weights[j][i] * alpha) / (alpha + 1);

                        if ((weights[j][i] < MINIMUM_WEIGHT)) {
                            weights[j][i] = NULL_WEIGHT;/* forget contact history if is lower than trashold*/
                            count--;
                        }
                    }
                }
            }
        }
    }

    public void tickInit() {
        for ( int i = 0; i < noNodes; i++) {
            for (int j = i+1; j < noNodes; j++) {
                weights[i][j] = NOT_MODIFIED;
            }
        }
    }

    public void track(int observed_id, int observer_id, int i, int j, long tick) {
        if ( ((i==observed_id) && (j==observer_id)) || ((j==observed_id) && (i==observer_id)) ){
            System.out.println( "node " + i + " and node " + j + " met at time" + tick + " weight and status are " + weights[i][j] + " *** " + weights[j][i]);
        }
    }

    public void trackAge(int x, int y, long tick) {

        if ((weights[x][y] == 0) && (weights[y][x] == 0))
            return;

        System.out.println("weight at " + tick  + " for nodes " + x + " and " + y +" after age is " + weights[x][y] + " *** " + weights[y][x]);
    }

    public static HashMap<Integer,Integer> computeBorderNodes(GraphMatrix graphMatrix, List<Integer> nodeSet) {
        HashMap<Integer,Integer> borderNodes = new HashMap<>();

        for ( int i = 0; i < graphMatrix.noNodes; i++) {
            for (int j = 0; j<i; j++){
                if ((nodeSet.contains(i)) && (!(nodeSet.contains(j))) && graphMatrix.hasValue(i,j)){
                    if (borderNodes.containsKey(i)) {
                        borderNodes.replace(i,borderNodes.get(i) + graphMatrix.getValue(i,j));
                    }
                    else
                        borderNodes.put(i, graphMatrix.getValue(i, j));
                }
                if ((nodeSet.contains(j)) && (!(nodeSet.contains(i))) && graphMatrix.hasValue(i,j)){
                    if (borderNodes.containsKey(j)) {
                        borderNodes.replace(j,borderNodes.get(j) + graphMatrix.getValue(i,j));
                    }
                    else
                        borderNodes.put(j, graphMatrix.getValue(i, j));
                }
            }
        }

        return borderNodes;

    }
}



