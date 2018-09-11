package mobemu.algorithms;

import mobemu.node.*;
import mobemu.trace.Contact;
import mobemu.trace.Trace;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class Nonsensus extends Node {


    private int initVal;
    private int val;
    private int seqNumber;
    private boolean decision;
    private boolean ownDecision;
    private int noOfOTR;
    private int popularity;

    protected List<NonsensusMessage> dataMemoryNonsensus; // history of the data memory of the current node
    protected List<NonsensusMessage> ownMessagesNonsensus; // list of messages generated by the current node
    protected List<NonsensusMessage> tempMemoryNonsensus; // useful data of the current node

    public Nonsensus(int id, int nodes, Context context, boolean[] socialNetwork, int dataMemorySize,
                     int exchangeHistorySize, long seed, long traceStart, long traceEnd) {

        super(id, nodes, context, socialNetwork, dataMemorySize, exchangeHistorySize, seed, traceStart, traceEnd);

        this.dataMemoryNonsensus = new ArrayList<>();
        this.ownMessagesNonsensus = new ArrayList<>();
        this.tempMemoryNonsensus = new ArrayList<>();
        this.noOfOTR = (nodes*2)/3;
        this.val = ThreadLocalRandom.current().nextInt(1,4);
        this.initVal=this.val;
        this.seqNumber = 1;
        this.decision = false;
        this.popularity=0;
    }

    public int getPopularity(){
        return popularity;
    }

    public void increasePopularity(){
        this.popularity=popularity+1;
    }


    public void printOwnMessageNonsensus(){
        for (NonsensusMessage message : this.ownMessagesNonsensus){
            message.printMessage();
        }
    }

    public void printTempMemoryNonsensus(){
        for (NonsensusMessage message : this.tempMemoryNonsensus){
            if (message.getDecision()){
                message.printMessage();
            }
        }
    }


    public int noOfOwnElems(){
        return ownMessagesNonsensus.size();
    }

    public int noOfDataElems(){
        return dataMemoryNonsensus.size();
    }

    public int noOfTempElems(){
        return tempMemoryNonsensus.size();
    }

    public int getInitVal(){
        return initVal;
    }

    public String getName() {
        return "Nonsensus";
    }

    public  int getVal(){
        return val;
    }

    public int getSeqNumber(){
        return seqNumber;
    }

    public boolean getDecision(){
        return decision;
    }

    public int getNoOfOTR(){
        return noOfOTR;
    }

    public boolean getOwnDecision(){
        return ownDecision;
    }

    public int getNoOfEncounters(){
        int sum = 0;
        for(int i : encounters) {
            sum += i;
        }
        return sum;
    }

    public NonsensusMessage addMemoryMessage(NonsensusMessage message) {
        tempMemoryNonsensus.add(message);
        dataMemoryNonsensus.add(message);
        return message;
    }

    public NonsensusMessage addOwnMessage(NonsensusMessage message) {
        ownMessagesNonsensus.add(message);
        return message;
    }


    private int minVal(int minVal){
        for (NonsensusMessage message : this.tempMemoryNonsensus){
            if (message.getVal()<minVal){
                minVal=message.getVal();
            }
        }
        return minVal;
    }

    public boolean tryDecide(){

        if (this.tempMemoryNonsensus.size() > this.noOfOTR){
            return true;
        }

        return false;
    }

    public boolean canDecide(int val){
        for (NonsensusMessage message : this.tempMemoryNonsensus) {
            if (message.getVal()!=val){
                return false;
            }
        }
        return true;
    }


    public int removeOldData(int seq_number) {
//    	int data_size=tempMemoryNonsensus.size();
        int deleted_items=0;

        for (int i = 0; i < tempMemoryNonsensus.size(); i++) {


            int seqLocalNumber=tempMemoryNonsensus.get(i).getSeqNumber();
            boolean decision=tempMemoryNonsensus.get(i).getDecision();
            if ((seqLocalNumber<seqNumber)&&(!decision)){
                tempMemoryNonsensus.remove(i);
                deleted_items++;
            }
        }

        return deleted_items;
    }


    public int removeAllData() {
//    	int data_size=tempMemoryNonsensus.size();
        int deleted_items=0;

        for (int i = 0; i < tempMemoryNonsensus.size(); i++) {

            boolean decision=tempMemoryNonsensus.get(i).getDecision();

            if (!decision){
                tempMemoryNonsensus.remove(i);
                deleted_items++;
            }
        }

        return deleted_items;
    }

    public NonsensusMessage generateNewSequence(boolean decision, boolean ownDecision, int val, long currentTime, int seqNo) {
        this.decision=decision;
        this.val=val;
        //if new sequence number or own decision . do this in order to remember if taken a decision in a delayed round

        if ((ownDecision)&&(decision)){
            this.ownDecision=ownDecision;
        }

        if (ownDecision){
            this.seqNumber=seqNo;
        }

        if  ((!decision)&&(!ownDecision))
            this.seqNumber=seqNo;


        NonsensusMessage message=new NonsensusMessage(this.id, currentTime, val, seqNo, decision, this.getNoOfEncounters());
        this.addMemoryMessage(message);

        if (ownDecision)
            this.addOwnMessage(message);

        if (this.decision)
            this.removeAllData();


        else
            this.removeOldData(this.seqNumber);

        return message;
    }


    public static List<NonsensusMessage> runTr(Nonsensus[] nodes, Trace trace) {
        int messageCopies = nodes.length;
        int messageCount = nodes.length;

        int contactCount = trace.getContactsCount();
        long startTime = trace.getStartTime();
        long endTime = trace.getEndTime();
        long sampleTime = trace.getSampleTime();

        Calendar currentDay = Calendar.getInstance();
        Calendar generationTime = Calendar.getInstance();
        int previousDay = -1;
        boolean generate = true;
        Random messageRandom = new Random(0);

        List<NonsensusMessage> messages = new ArrayList<>();

        for (long tick = startTime; tick < endTime; tick += sampleTime) {
            int count = 0;


            currentDay.setTimeInMillis(tick);


            if (generate){
                generationTime = Message.generateMessageTime(messageRandom.nextDouble());

                if (generationTime.get(Calendar.HOUR) == currentDay.get(Calendar.HOUR)){
                    System.out.println("generation day " + currentDay.get(Calendar.DATE));
                    System.out.println("generation hour " + currentDay.get(Calendar.HOUR));
                    messages.addAll(NonsensusMessage.generateMessages(nodes,tick));
                    generate=false;
                }
            }

            for (int i = 0; i < contactCount; i++) {
                Contact contact = trace.getContactAt(i);

                if (contact.getStart() <= tick && contact.getEnd() >= tick) {

                    // there is a contact.
                    count++;

                    Nonsensus observer = nodes[contact.getObserver()];
                    Nonsensus observed = nodes[contact.getObserved()];

                    long contactDuration = 0;
                    boolean newContact = (contact.getStart() == tick);
                    if (newContact) {
                        contactDuration = contact.getEnd() - contact.getStart() + sampleTime;
                    }

                    observer.increasePopularity();
                    observed.increasePopularity();
                    // run
                    observer.run(observed, tick, contactDuration, newContact, tick - startTime, sampleTime);
                }
            }

            // remove unused contacts.
            for (int i = count - 1; i >= 0; i--) {
                if (trace.getContactAt(i).getEnd() == tick) {
                    trace.removeContactAt(i);
                }
            }

            contactCount = trace.getContactsCount();
        }

        return messages;
    }

    protected void onDataExchange(Node encounteredNode, long contactDuration, long currentTime) {

        if (!(encounteredNode instanceof Nonsensus)) {
            return;
        }

        Nonsensus nonsensusEncounteredNode = (Nonsensus) encounteredNode;

        // download each message in the encountered node's data memory that is not in the current node's memory
        for (NonsensusMessage message : nonsensusEncounteredNode.tempMemoryNonsensus) {

            insertMessage(message, nonsensusEncounteredNode, currentTime);
        }
    }

    protected boolean insertMessage(NonsensusMessage message, Nonsensus from, long currentTime) {
        // return if the message is already in the data memory or has been generated by the node itself
        if (dataMemoryNonsensus.contains(message) || ownMessagesNonsensus.contains(message) || (this.decision)) {
            return false;
        }

        if (message.getSeqNumber()<this.seqNumber)
            return false;
        // increase total number of messages delivered
        messagesExchanged++;

        exchangeHistoryReceived.add(new ExchangeHistory(currentTime, message, from.id, id, from.battery.getCurrentLevel()));
        from.exchangeHistorySent.add(new ExchangeHistory(currentTime, message, id, from.id, battery.getCurrentLevel()));


        if (message.getDecision()){
//			System.out.println("get decision in node " + this.getId());
//			System.out.println("get decision from " + from.getId());

            this.generateNewSequence(true,false,message.getVal(),currentTime,message.getSeqNumber());

            removeOldData(this.seqNumber);
            messagesReceived++;
        }

        else if (message.getSeqNumber()>this.seqNumber){
//			System.out.println("bigger seq no " + this.getId());
//			System.out.println("bigger seq no encountered " + from.getId());
            this.generateNewSequence(false, false, this.minVal(message.getVal()), currentTime, message.getSeqNumber());
            messagesReceived++;
        }
        else if(message.getSeqNumber()==this.seqNumber){

            if (tempMemoryNonsensus.size() >= this.noOfOTR) {
                if (canDecide(message.getVal())){
//        			System.out.println("cand Decide node " + this.getId());
//        			System.out.println("cand Decide encountered " + from.getId());
                    this.generateNewSequence(true, true, this.val, currentTime, this.seqNumber+1);
                }
                else{
//        			System.out.println("cannot Decide node newseq " + this.getId());
//        			System.out.println("cannnot Decide encountered newseq " + from.getId());
//        			System.out.println("cannnot Decide encountered sequence " + from.getSeqNumber());
//        			System.out.println("cannnot Decide my encountered sequence " + this.getSeqNumber());
                    this.generateNewSequence(false, true, this.minVal(message.getVal()), currentTime, this.seqNumber+1);
                }
            }
            else
                this.addMemoryMessage(message);
        }


        return true;
    }


}
