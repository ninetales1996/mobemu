/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobemu;

import mobemu.algorithms.Epidemic;
import mobemu.node.Message;
import mobemu.node.Node;
import mobemu.node.Stats;
import mobemu.parsers.UPB;
import mobemu.trace.Parser;

import java.util.List;

/**
 * Main class for MobEmu.
 *
 * @author Radu
 */
public class MobEmu {
/*test commit wke*/
    public static void main(String[] args) {
        Parser parser = new UPB(UPB.UpbTrace.UPB2011);
        boolean compute = true;

        // print some trace statistics
        double duration = (double) (parser.getTraceData().getEndTime() - parser.getTraceData().getStartTime()) / (Parser.MILLIS_PER_MINUTE * 60);
        System.out.println("Trace duration in hours: " + duration);
        System.out.println("Trace contacts: " + parser.getTraceData().getContactsCount());
        System.out.println("Trace contacts per hour: " + (parser.getTraceData().getContactsCount() / duration));
        System.out.println("Nodes: " + parser.getNodesNumber());

        // initialize Epidemic nodes
        long seed = 0;
        boolean dissemination = false;
        Node[] nodes = new Node[parser.getNodesNumber()];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Epidemic(i, nodes.length, parser.getContextData().get(i), parser.getSocialNetwork()[i],
                    10000, 100, seed, parser.getTraceData().getStartTime(), parser.getTraceData().getEndTime(), dissemination, false);
        }

        // run the trace


        if (compute) {
            Node.runAnalytics(nodes.length, parser.getTraceData());
            System.out.println(nodes[0].getName());
        }
        else {
            // print opportunistic algorithm statistics
            List<Message> messages = Node.runTrace(nodes, parser.getTraceData(), false, dissemination, seed);
            System.out.println(nodes[0].getName());
            System.out.println("Messages: " + messages.size());
            System.out.println("" + Stats.computeHitRate(messages, nodes, dissemination));
            System.out.println("" + Stats.computeDeliveryCost(messages, nodes, dissemination));
            System.out.println("" + Stats.computeDeliveryLatency(messages, nodes, dissemination));
            System.out.println("" + Stats.computeHopCount(messages, nodes, dissemination));
        }
    }
}
