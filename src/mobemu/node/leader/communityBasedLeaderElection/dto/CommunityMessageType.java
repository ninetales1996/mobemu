package mobemu.node.leader.communityBasedLeaderElection.dto;

/**
 * Created by radu on 1/15/2017.
 */
public enum CommunityMessageType {
    AddRequest,
    AddResponse,
    AddedNode,
    RemoveRequest,
    RemovedNode,
    LeaderProposal,
    LeaderElected
}
