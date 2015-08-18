package org.suhininalex.suffixtree;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class SuffixTree<Token> {

    final Node root = new Node(null);

    final protected Map<Long,List> sequences = new HashMap<>();

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

    /**
     *
     * @return edge parent if there was no uniting else another child parent
     */
    protected Node removeSequenceFromEdge(Edge edge, List sequence){

        System.out.println("removing edge: "+edge+" from "+edge.parent);
        System.out.println(this);
        if (edge.sequence!=sequence) return null;

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
                System.out.println("removing node "+anotherChild.parent);

                parentEdge.terminal = anotherChild.terminal;
                if (parentEdge.terminal != null) parentEdge.terminal.parentEdge = parentEdge;

                parentEdge.p = parentEdge.p + anotherChild.p - anotherChild.k + 1;
                anotherChild.parent.clear();
            }
        }
        return null;
    }

    protected void relabelAllParents(Edge edge){
        while (edge!=null && edge.parent!=root) {
//            System.out.println("relabeling parent of " + edge.parent + "edge " + edge);
            Edge parentEdge = edge.parent.parentEdge;
            if (parentEdge.sequence == edge.sequence) return;
            parentEdge.sequence = edge.sequence;
            parentEdge.k = edge.k - (parentEdge.p - parentEdge.k) - 1;
            parentEdge.p = edge.k - 1;
            edge = parentEdge;
        }
    }

    protected void removeSequence2(long id){
        List sequence = sequences.get(id);

        Tuple<Node, Integer> currentPoint = new Tuple<>(root, 0);
        do {
            Tuple<Node, Integer> canonized = canonize(currentPoint.first, sequence, currentPoint.second, sequence.size() - 2);
//            System.out.println("canonized: " + canonized.first + " " + canonized.second);
            currentPoint = removeEdge(sequence, canonized.first, canonized.second);
//            System.out.println("newpoint: "+currentPoint.first + " " + currentPoint.second);
//            System.out.println(this);
            Collection<Edge> edges = currentPoint.first.getEdges();
            if (!edges.isEmpty()) relabelAllParents(edges.iterator().next());
//            System.out.println("current edge: "+currentEdge);


            currentPoint = new Tuple<>(currentPoint.first.suffixLink, currentPoint.second);
        } while (currentPoint.second<sequence.size()-1 || currentPoint.first!=null);
    }

    protected Tuple<Node, Integer> removeEdge(List sequence, Node s, int k){
        Edge edge = s.getEdge(sequence.get(k));
//        System.out.println("removing edge: "+edge + " from " + edge.parent);

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

    protected void removeSequenceFromBranch(Node s, List sequence, int k){
//        System.out.println("removing from branch: "+s+" k="+k);

        Tuple<Node,Integer> canonized = canonize(s, sequence, k, sequence.size() - 2);

        Node node = canonized.first;
//        System.out.println("canonized: " + canonized.first + "k "+canonized.second);

        Edge edge = node.getEdge(sequence.get(canonized.second));
//        System.out.println("removing chain: " + node + " " + edge);
        while (edge!=null) {
            removeSequenceFromEdge(edge, sequence);
            edge = edge.parent.parentEdge;
        }

//        if (canonized.second<sequence.size()-1 || s.suffixLink!=null)
//            removeSequenceFromBranch(node.suffixLink, sequence, canonized.second);



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
