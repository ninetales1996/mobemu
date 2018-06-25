package mobemu;

import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;

public class MobEmuEvaluate {
    public static void main(String[] args) {

       ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
//        pc.newProject();
//
//        Workspace workspace = pc.getCurrentWorkspace();
//
//        System.out.println("workspace\n");
//
//        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
//        Container container;
//
//        try {
//            File file = new File("LesMiserables.gexf");
//            Scanner input = new Scanner(file);
//            while (input.hasNextLine()){
//                System.out.println(input.nextLine());
//            }
//
//            container = importController.importFile(file);
//            container.getLoader().setEdgeDefault(EdgeDirectionDefault.UNDIRECTED);
//            importController.process(container,new DefaultProcessor(), workspace);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
       GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
//
//        YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
//        layout.setGraphModel(graphModel);
//        layout.resetPropertiesValues();
//        layout.setOptimalDistance(200f);
//
//        layout.initAlgo();
//
//        for (int i=0; i<100 && layout.canAlgo();i++){
//            layout.goAlgo();
//        }
//
//        GraphDistance distance = new GraphDistance();
//
//        Modularity modularity = new Modularity();
//        modularity.execute(graphModel);
//
//
//        ExportController ec =Lookup.getDefault().lookup(ExportController.class);
//
//
//        try {
//            ec.exportFile(new File("test.pdf"));
//        }
//        catch (IOException ex) {
//            ex.printStackTrace();
//            return;
//        }
    }
}
