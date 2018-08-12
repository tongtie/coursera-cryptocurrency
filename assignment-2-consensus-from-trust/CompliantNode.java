import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

    private double p_graph;
    private double p_malicious;
    private double p_tXDistribution;
    private int numRounds;

    private boolean[] followees;

    private Set<Transaction> pendingTransactions;

    private boolean[] blackListed;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.p_graph = p_graph;
        this.p_malicious = p_malicious;
        this.p_tXDistribution = p_txDistribution;
        this.numRounds = numRounds;
    }

    public void setFollowees(boolean[] followees) {
        this.followees = followees;
        //Malicious node: be functionally dead and never actually broadcast any transactions.
        this.blackListed = new boolean[followees.length];
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    public Set<Transaction> sendToFollowers() {
        Set<Transaction> toSend = new HashSet<>(pendingTransactions);
        pendingTransactions.clear();
        return toSend;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        Set<Integer> senders = candidates.stream().map(c -> c.sender).collect(toSet());
        for (int i = 0; i < followees.length; i++) {
            // 根据恶意节点不参与广播判断恶意节点
            if (followees[i] && !senders.contains(i))
                blackListed[i] = true;
        }
        for (Candidate c : candidates) {
            if (!blackListed[c.sender]) {
                // 重新生成待广播的交易信息
                pendingTransactions.add(c.tx);
            }
        }
    }
}
