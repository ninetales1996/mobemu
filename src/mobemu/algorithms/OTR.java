package mobemu.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class OTR {

    static int UNDECIDED =-1;

    int nodeId ;
    double time_elapsed;

    double time_isolated;
    double time_in_group;

    double time_decided;
    double time_undecided;

    public int group_id;
    public int group_count;
    public boolean isolated;

    public int session;
    public int round;

    public boolean decided;

    public boolean event_predicted;
    public boolean event_happened;
    public boolean event_not_happened;

    public int count_event_predicted;
    public boolean count_event_happened;
    public boolean count_not_happened;

    public int messages_count;
    public int value;
    public int value_secondary;
    public HashMap<Integer, Integer> messages = new HashMap<Integer, Integer>();
    public List<OTRMessage> messages_secondary = new ArrayList<OTRMessage>();

    public OTR(int i, long startTime, long endTime) {
        time_decided = 0;
        time_undecided =0;
        time_elapsed = 0;
        time_in_group = 0;
        time_isolated = 0;
        nodeId = i;
        session = 1;
        round = 1;
        decided = false;
    }

    public void addMessage( HashMap<Integer, Integer> neighbour_messages){
        this.messages.putAll(neighbour_messages);
    }

    public boolean roundFinished(){
        if (this.messages.size()>((group_count*2)/3))
            return true;
        return false;
    }

    public void newRound(){
        int max = UNDECIDED;
        for (Integer i : messages.keySet()){
            if (i>max)
                max =i;
        }

        this.decided=false;
        messages.clear();
        this.value=max;
        round = round + 1;
        System.out.println("NEW ROUND " + round + " session " + session + " for " + nodeId + " in " + group_id + " with " + group_count + " nodes ");
        messages.put(nodeId,max);
    }


    public int valueDecided() {
        int temp = UNDECIDED;
        for (Integer i : messages.keySet()){
            if (temp==UNDECIDED){
                temp =i;
            }
            else if (temp!=i){
                return UNDECIDED;
            }
        }
        this.decided = true;
        this.value=temp;
        return temp;
    }

    public void decideByMessage( OTR neighbour){
        this.decided=true;
        this.value = neighbour.value;
        this.messages_count++;
    }

    public void exchangeMessages(OTR otrMet){
        if (otrMet.group_id!=this.group_id)
            return;

        if ((this.decided)&&(otrMet.decided))
            return;

        if (this.decided){
            if (!(otrMet.decided)){
                otrMet.decideByMessage(this);
                return;
            }
        }

        if (otrMet.decided){
            if (!(this.decided)){
                this.decideByMessage(otrMet);
                return;
            }
        }

        this.addMessage(otrMet.messages);
        this.messages_count++;
        otrMet.addMessage(this.messages);
        otrMet.messages_count++;

        if (this.roundFinished()){
            if (valueDecided()!=UNDECIDED){
                otrMet.decideByMessage(this);
                return;
            }
            else
                this.newRound();
        }
        if (otrMet.roundFinished()){
            if (otrMet.valueDecided()!=UNDECIDED){
                this.decideByMessage(otrMet);
                return;
            }
            else
                otrMet.newRound();
        }
    }


    public void OTR_new_session(int group_id, int group_count, boolean isolated){
        this.isolated = isolated;
        this.group_id=group_id;
        this.group_count = group_count;

        session=session+1;
//        System.out.println("new session id " + session + " group count in " + group_id + " is " + group_count );
        round=1;
        decided = false;
        int randomNum = ThreadLocalRandom.current().nextInt(0, 10);
        messages.clear();
        messages.put(nodeId,randomNum);
    }

    public void update_stats(){
        time_elapsed++;
        if (this.decided){
            time_decided++;
        }
        else{
            time_undecided++;
        }
        if(isolated){
            time_isolated++;
        }
        else{
            time_in_group++;
        }
    }

    public static void print_stats(OTR nodes[]){
        double total_time_in_group=0;
        double total_time_isolated=0;
        double total_time_decided=0;
        double total_time_undecided=0;
        int total_messages_count = 0;


        for (OTR node : nodes){
            total_time_in_group = total_time_in_group +node.time_in_group;
            total_time_isolated = total_time_isolated+node.time_isolated;
            total_time_decided = total_time_decided + node.time_decided;
            total_time_undecided = total_time_undecided + node.time_undecided;
            total_messages_count = total_messages_count + node.messages_count;
        }
        System.out.println("average total time in group " + (total_time_in_group/(nodes.length+1)) );
        System.out.println("average isolated time in group " + (total_time_isolated/(nodes.length+1)) );
        System.out.println("average decided time in group " + (total_time_decided/(nodes.length+1)) );
        System.out.println("average undecided time in group " + (total_time_undecided/(nodes.length+1)) );
        System.out.println("total messages count " + total_messages_count );
    }
}
