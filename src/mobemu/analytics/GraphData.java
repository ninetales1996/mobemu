package mobemu.analytics;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.generator.plugin.RandomGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import java.util.HashMap;

public abstract class GraphData implements GraphPopulateData {

    abstract void updateLabel(long tick);
    abstract void updateHistory();
    abstract void clearHistory();
    abstract void feedToMLEngine(long tick);


    public void process(GraphMatrix graphMatrix, long tick)  {
        this.populateData(graphMatrix);
        this.updateLabel(tick);
        this.updateHistory();
        this.feedToMLEngine(tick);
        this.clearHistory();
    }

    public void restore(){
        this.clearHistory();;
    }

    public static void processMatrix(GraphMatrix graphMatrix, GraphModel graphModel){

        HashMap<Integer, Node> tempNode= new HashMap<>();
        Graph graph = graphModel.getGraph();

        for ( int i = 0; i < graphMatrix.getNoNodes(); i++){
//            System.out.println("node put "+i);
            Node n = graphModel.factory().newNode(String.valueOf(i));
            tempNode.put(i,n);
            graph.addNode(n);
            n.setLabel("Node " + i);
        }

        for ( int i = 0; i < graphMatrix.getNoNodes(); i++){
            for (int j=0; j<i;j++){
                if (graphMatrix.hasValue(i,j)){

                    Edge e =  graphModel.factory().newEdge(tempNode.get(i), tempNode.get(j), 0, graphMatrix.getValue(i,j), false);
                    graph.addEdge(e);
                }
            }
        }

//        for (Edge e : graph.getEdges()) {
//            System.out.println(e.getSource().getId() + " -> " + e.getTarget().getId() + " with weight " + e.getWeight());
//        }
    }


    public static void processRandomMatrix(int noNodes, Workspace workspace, double wiring){
        Container container = Lookup.getDefault().lookup(Container.Factory.class).newContainer();
        RandomGraph randomGraph = new RandomGraph();
        randomGraph.setNumberOfNodes(noNodes);
        randomGraph.setWiringProbability(wiring);
        randomGraph.generate(container.getLoader());

        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        importController.process(container, new DefaultProcessor(), workspace);

    }
}
