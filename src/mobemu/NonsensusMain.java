package mobemu;

import mobemu.algorithms.Nonsensus;
import mobemu.node.NonsensusMessage;
import mobemu.node.NonsensusStats;
import mobemu.parsers.UPB;
import mobemu.trace.Parser;

import java.util.List;

public class NonsensusMain {


    public static void main(String[] args) {
        Parser parser = new UPB(UPB.UpbTrace.UPB2011);

        // print some trace statistics
        double duration = (double) (parser.getTraceData().getEndTime() - parser.getTraceData().getStartTime()) / (Parser.MILLIS_PER_MINUTE * 60);
        System.out.println("Trace duration in hours: " + duration);
        System.out.println("Trace contacts: " + parser.getTraceData().getContactsCount());
        System.out.println("Trace contacts per hour: " + (parser.getTraceData().getContactsCount() / duration));
        System.out.println("Nodes: " + parser.getNodesNumber());

        long seed = 0;
        Nonsensus[] nodes = new Nonsensus[parser.getNodesNumber()];

        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Nonsensus(i, nodes.length, parser.getContextData().get(i), parser.getSocialNetwork()[i],
                    10000, 100, seed, parser.getTraceData().getStartTime(), parser.getTraceData().getEndTime());
        }

        //start_sim
        List<NonsensusMessage> messages = Nonsensus.runTr(nodes, parser.getTraceData());

        System.out.println("simulation over");
        System.out.println(nodes[0].getName());
        System.out.println("nodes decided " + NonsensusStats.computeNumberOfDecisions(nodes) );
    }
}
