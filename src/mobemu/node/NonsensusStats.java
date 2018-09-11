package mobemu.node;

import mobemu.algorithms.Nonsensus;

public class NonsensusStats {


    public NonsensusStats() {

    }

    public static int computeNumberOfDecisions(Nonsensus[] nodes){
        int numberOfDecisions=0;
        int numMin=Integer.MAX_VALUE;
        int numberOfCorrectNumbers=0;
        int totalOverhead=0;

        int numberHealthyUndecided=0;
        int encountersHealthyUndecided=0;

        for (Nonsensus node : nodes){
            if (node.getInitVal()<numMin)
                numMin=node.getInitVal();
        }
        for (Nonsensus node : nodes) {
//			System.out.println("node id " + node.getId());
//			System.out.println("node decisions " + node.getDecision());
//			System.out.println("node own decisions " + node.getOwnDecision());
//			System.out.println("node init val " + node.getInitVal());
//			System.out.println("node val " + node.getVal());
//			System.out.println("node seq number " + node.getSeqNumber());
//			System.out.println("number of encounters " + node.getNoOfEncounters());
//			System.out.println("number of temp elems " + node.noOfTempElems());
//			System.out.println("number of own elems "  + node.noOfOwnElems());
//			System.out.println("number of data elems " + node.noOfDataElems());

            totalOverhead=totalOverhead +  node.noOfDataElems();

//			node.printTempMemoryNonsensus();
//			node.printOwnMessageNonsensus();

            if (node.getOwnDecision()){
                System.out.println("!!!Decided Node!!!");
                System.out.println("node id " + node.getId());
                System.out.println("node decisions " + node.getDecision());
                System.out.println("node own decisions " + node.getOwnDecision());
                System.out.println("node init val " + node.getInitVal());
                System.out.println("node val " + node.getVal());
                System.out.println("node seq number " + node.getSeqNumber());
                System.out.println("number of encounters " + node.getNoOfEncounters());
                System.out.println("!!!Messages!!!");
                node.printOwnMessageNonsensus();
            }

            if (((!(node.getDecision()))&&(node.getVal()==numMin))){
                numberHealthyUndecided=numberHealthyUndecided+1;
                encountersHealthyUndecided=encountersHealthyUndecided+node.getNoOfEncounters();
            }

            if (node.getDecision()){
                numberOfDecisions=numberOfDecisions+1;
            }
            if (node.getVal()==numMin){
                numberOfCorrectNumbers=numberOfCorrectNumbers+1;
            }
        }

        System.out.println("healthy undecided " + numberHealthyUndecided);
        System.out.println("number of encounters healthy undecided " + encountersHealthyUndecided);
        System.out.println("total Overhead " + totalOverhead);
        System.out.println("CORRECT NUMBERS " +  numberOfCorrectNumbers);
        return numberOfDecisions;
    }
}
