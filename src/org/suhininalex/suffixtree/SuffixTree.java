package org.suhininalex.suffixtree;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class SuffixTree<Token> {

    final Node root = new Node(null);

    final private Map<Long,List> sequences = new HashMap<>();

    //TODO improve sequence id distribution
    private final AtomicLong sequenceFreeId = new AtomicLong(1);
    private long getNextFreeSequenceId(){
        return sequenceFreeId.incrementAndGet();
    }

    protected Tuple<Boolean, Node> testAndSplit(Node s, List sequence, int k, int p, Object t){
        if (k<=p) {
            Edge ga = s.getEdge(sequence.get(k));
            if (t.equals(ga.sequence.get(ga.k+p-k+1))) return new Tuple<>(true, s);
            else {
                Node r = new Node(ga);
                r.putEdge(
                        ga.terminal,
                        ga.sequence,
                        ga.k + p - k + 1,
                        ga.p
                );
                ga.terminal = r;
                ga.p = ga.k + p - k;

                return new Tuple<>(false, r);
            }
        } else {
            if (s.getEdge(t)==null) return new Tuple<>(false, s);
            else return new Tuple<>(true, s);
        }
    }

    protected Tuple<Node, Integer> canonize(Node s, List sequence, int k, int p){
        if (s==null) { s = root; k=k+1; } //preroot replace
        if (p<k) return new Tuple<>(s,k);
        else {
            Edge ga = s.getEdge(sequence.get(k));
            while (ga.p-ga.k <= p-k){
                k = k + ga.p - ga.k + 1;
                s = ga.terminal;
                if (k<=p) ga = s.getEdge(sequence.get(k));
            }
            return new Tuple<>(s, k);
        }
    }

    protected Tuple<Node, Integer> update(Node s, List sequence, int k, int i){
        Node oldr = root;   Tuple<Boolean, Node> splitRes = testAndSplit(s, sequence, k, i-1, sequence.get(i));
        boolean endPoint = splitRes.first;  Node r = splitRes.second;
        while (!endPoint){
            r.putEdge(sequence, i);
            if (oldr != root) oldr.suffixLink = r;
            oldr = r;
            Tuple<Node, Integer> canonizeRes = canonize(s.suffixLink, sequence, k, i-1);
            s = canonizeRes.first; k = canonizeRes.second;
            splitRes = testAndSplit(s, sequence, k, i-1, sequence.get(i));
            endPoint = splitRes.first;  r = splitRes.second;
        }
        if (oldr != root) oldr.suffixLink = r;
        return new Tuple<>(s, k);
    }

    protected void updateSequence(List sequence){
        Node s = root; int k = 0; int i = -1;
        while (i+1<sequence.size()){
            i=i+1;
            Tuple<Node, Integer> updateRes = update(s, sequence, k, i);
            s = updateRes.first;   k = updateRes.second;
            Tuple<Node, Integer> canonizeRes = canonize(s, sequence, k, i);
            s = canonizeRes.first; k = canonizeRes.second;
        }
    }

    protected void removeSequenceFromEdge(Edge edge, List sequence){
        if (edge.sequence!=sequence) return;

        //removing leaf
        Node node = edge.parent;
        if (edge.terminal==null){
            node.removeEdge(edge);
        }

        Edge parentEdge = node.parentEdge;
        if (parentEdge!=null) {
            //relabelling parent
            Edge anotherChild = node.getEdges().iterator().next();
            parentEdge.sequence = anotherChild.sequence;
            parentEdge.k = anotherChild.k-parentEdge.p+parentEdge.k-1;
            parentEdge.p = anotherChild.k-1;

            //uniting
            if (node.getEdges().size()==1){
                parentEdge.terminal = anotherChild.terminal;
                parentEdge.p = parentEdge.p + anotherChild.p - anotherChild.k + 1;
            }
        }
    }

    protected void removeSequenceFromBranch(Node s, List sequence, int k){
        Tuple<Node,Integer> canonized = canonize(s, sequence, k, sequence.size() - 2);

        Node node = canonized.first;
        if (node.suffixLink!=null) {
            removeSequenceFromBranch(node.suffixLink, sequence, canonized.second);
        }

        Edge edge = node.getEdge(sequence.get(canonized.second));
        while (edge!=null && edge.sequence==sequence) {
            removeSequenceFromEdge(edge, sequence);
            edge = edge.parent.parentEdge;
        }
    }

//    public boolean checkSequence(final List sequence){
//        Node check
//    }

    @SuppressWarnings("unchecked")
    public long addSequence(final List sequence){
        List tokens = new ArrayList();
        tokens.addAll(sequence);
        long idSequence = getNextFreeSequenceId();
        sequences.put(idSequence, tokens);
        tokens.add(new EndToken(idSequence));
        updateSequence(tokens);
        return idSequence;
    }

    public void removeSequence(long id){
        List sequence = sequences.get(id);
        if (sequence == null) throw new IllegalStateException("There are no such sequence!");
        removeSequenceFromBranch(root, sequence, 0);
        sequences.remove(id);
    }

    @SuppressWarnings("unchecked")
    public List<Token> getSequence(long id){
        List<Token> sequence = sequences.get(id);
        if (sequence==null) throw new IllegalStateException("No such sequence!");
        return Collections.unmodifiableList(sequence.subList(0, sequence.size()-1));
    }

    @Override
    public String toString() {
        return root.subTreeToString();
    }
}
