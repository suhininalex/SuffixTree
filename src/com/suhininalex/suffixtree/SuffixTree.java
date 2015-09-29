package com.suhininalex.suffixtree;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class SuffixTree<Token> {

    final Node root = new Node(null);

    final Map<Long,List> sequences = new HashMap<>();

    private final AtomicLong sequenceFreeId = new AtomicLong(1);
    private long getNextFreeSequenceId(){
        return sequenceFreeId.incrementAndGet();
    }

    Tuple<Boolean, Node> testAndSplit(Node s, List sequence, int k, int p, Object t){
        if (k<=p) {
            Edge ga = s.getEdge(sequence.get(k));
            if (t.equals(ga.sequence.get(ga.k+p-k+1))) return new Tuple<>(true, s);
            else {
                Node r = new Node(ga);
                Edge newEdge = r.putEdge(
                        ga.terminal,
                        ga.sequence,
                        ga.k + p - k + 1,
                        ga.p
                );
                if (ga.terminal!=null) ga.terminal.parentEdge = newEdge;
                ga.terminal = r;
                ga.p = ga.k + p - k;
                return new Tuple<>(false, r);
            }
        } else {
            if (s.getEdge(t)==null) return new Tuple<>(false, s);
            else return new Tuple<>(true, s);
        }
    }

    Tuple<Node, Integer> canonize(Node s, List sequence, int k, int p){
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

    Tuple<Node, Integer> update(Node s, List sequence, int k, int i){
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

    void updateSequence(List sequence){
        Node s = root; int k = 0; int i = -1;
        while (i+1<sequence.size()){
            i=i+1;
            Tuple<Node, Integer> updateRes = update(s, sequence, k, i);
            s = updateRes.first;   k = updateRes.second;
            Tuple<Node, Integer> canonizeRes = canonize(s, sequence, k, i);
            s = canonizeRes.first; k = canonizeRes.second;
        }
    }

    void relabelAllParents(Edge edge){
        while (edge!=null && edge.parent!=root) {
            Edge parentEdge = edge.parent.parentEdge;
            if (parentEdge.sequence == edge.sequence) return;
            parentEdge.sequence = edge.sequence;
            parentEdge.k = edge.k - (parentEdge.p - parentEdge.k) - 1;
            parentEdge.p = edge.k - 1;
            edge = parentEdge;
        }
    }

    public void removeSequence(long id){
        List sequence = sequences.get(id);

        Tuple<Node, Integer> currentPoint = new Tuple<>(root, 0);
        do {
            Tuple<Node, Integer> canonized = canonize(currentPoint.first, sequence, currentPoint.second, sequence.size() - 2);
            currentPoint = removeEdge(canonized.first, sequence, canonized.second);
            Collection<Edge> edges = currentPoint.first.getEdges();
            if (!edges.isEmpty()) relabelAllParents(edges.iterator().next());
            currentPoint = new Tuple<>(currentPoint.first.suffixLink, currentPoint.second);
        } while (currentPoint.second<sequence.size()-1 || currentPoint.first!=null);
    }

    Tuple<Node, Integer> removeEdge(Node s, List sequence, int k){
        Edge edge = s.getEdge(sequence.get(k));
        s.removeEdge(edge);

        if (s!=root && s.getEdges().size()==1){
            Edge anotherChild = s.getEdges().iterator().next();
            Edge parentEdge = s.parentEdge;
            parentEdge.terminal = anotherChild.terminal;
            if (anotherChild.terminal != null) anotherChild.terminal.parentEdge = parentEdge;
            int parentEdgeLength = parentEdge.p - parentEdge.k + 1;
            parentEdge.k = anotherChild.k - parentEdgeLength;
            parentEdge.p = anotherChild.p;
            parentEdge.sequence = anotherChild.sequence;
            return new Tuple<>(parentEdge.parent, k-parentEdgeLength);
        }
        return new Tuple<>(s, k);
    }

    public boolean checkSequence(final List sequence){
        if (sequence==null || sequence.isEmpty()) return true;
        int k = 0;
        Node currentNode = root;
        do {
            Edge currentEdge = currentNode.getEdge(sequence.get(k));
            if (currentEdge==null) return false;
            for (int i = currentEdge.k; i <= currentEdge.p; i++) {
                if (!sequence.get(k).equals(currentEdge.sequence.get(i))) return false;
                k++;
                if (k >= sequence.size()) return true;
            }
            currentNode = currentEdge.terminal;
        } while (currentNode!=null);
        return false;
    }

    @SuppressWarnings("unchecked")
    public long addSequence(final List<Token> sequence){
        List tokens = new ArrayList();
        long idSequence = getNextFreeSequenceId();
        tokens.addAll(sequence);
        tokens.add(new EndToken(idSequence));
        sequences.put(idSequence, tokens);
        updateSequence(tokens);
        return idSequence;
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

    public Node getRoot() {
        return root;
    }
}
