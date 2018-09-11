package mobemu.node;


import mobemu.algorithms.Nonsensus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NonsensusMessage extends Message {
    private int val;
    private int seqNumber;
    private boolean decision;
    private int noEncounters;

    public NonsensusMessage(int source, long timeStamp, int val, int seqNumber, boolean decision, int noEncounters) {
        this.source=source;
        this.timestamp=timeStamp;
        this.val=val;
        this.seqNumber=seqNumber;
        this.decision=decision;
        this.noEncounters=noEncounters;
    }

    public void printMessage(){
        Calendar currentDay = Calendar.getInstance();

        System.out.println("******************");
        System.out.println("val " + val);
        System.out.println("seqNumber " + seqNumber);
        if (decision)
            System.out.println("DECISION " );
        else
            System.out.println("NOT DECISION " );

        currentDay.setTimeInMillis(timestamp);
        System.out.println("day " + currentDay.DATE);
        System.out.println("hour " + currentDay.HOUR);
        System.out.println("no of encounters " + noEncounters);
        System.out.println("******************");
    }

    public int getNoEncounters(){
        return noEncounters;
    }

    public int getSeqNumber(){
        return seqNumber;
    }

    public int getVal(){
        return val;
    }

    public boolean getDecision(){
        return decision;
    }

    public static List<NonsensusMessage> generateMessages(Nonsensus[] nodes, long tick) {
        int nodeCount = nodes.length;
        List<NonsensusMessage> result = new ArrayList<>();

        for (int i = 0; i < nodeCount; i++) {
            System.out.println("Messages: " + nodes[i].getVal());
        }

        for (int i = 0; i < nodeCount; i++) {

            result.add(nodes[i].addOwnMessage(new NonsensusMessage(i, tick, nodes[i].getVal(), nodes[i].getSeqNumber(), nodes[i].getDecision(),0)));
            result.add(nodes[i].addMemoryMessage(new NonsensusMessage(i, tick, nodes[i].getVal(), nodes[i].getSeqNumber(), nodes[i].getDecision(),0)));
        }


        return result;
    }

}
