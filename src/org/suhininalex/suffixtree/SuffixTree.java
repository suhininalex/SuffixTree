package org.suhininalex.suffixtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SuffixTree {
    final private List<List<Token>> sequences = new ArrayList<>();
    final Node root = new Node(null);

    Tuple<Boolean, Node> testAndSplit(Node s, List<Token> sequence, int k, int p, Token t){
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

    Tuple<Node, Integer> canonize(Node s, List<Token> sequence, int k, int p){
        if (s==null) { s = root; k=k+1; }
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

    Tuple<Node, Integer> update(Node s, List<Token> sequence, int k, int i){
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

    //TODO проблема с id последовательностей
    //при изменении порядка id может меняется
    void addSequence(List<Token> sequence){
        sequences.add(sequence);
        int idSequence = sequences.size()-1;
        sequence.add(new EndToken(idSequence));
        Node s = root; int k = 0; int i = -1;
        while (i+1<sequence.size()){
            i=i+1;
            Tuple<Node, Integer> updateRes = update(s, sequence, k, i);
            s = updateRes.first;   k = updateRes.second;
            Tuple<Node, Integer> canonizeRes = canonize(s, sequence, k, i);
            s = canonizeRes.first; k = canonizeRes.second;
        }
    }

    //Only for existing sequences
//    Tuple<Edge, Integer> getEdgeForSequence(Node startNode, List<Token> sequence, int k, int p){
//        Tuple<Node,Integer> canonized = canonize(startNode, sequence, k, p-1);
//        return new Tuple<>(canonized.first.getEdge(sequence.get(canonized.second)),;
//    }

    //Only for existing sequences
    void removeSequenceFromEdge(Edge edge, List<Token> sequence){
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

    //TODO canonize после следования по суффиксной ссылке
    void removeSequenceFromBranch(Node s, List<Token> sequence, int k){
        System.out.println("----------------");
        System.out.println("Begin: "+s+" k "+k);
        Tuple<Node,Integer> canonized = canonize(s, sequence, k, sequence.size() - 2);
        Node node = canonized.first;
        Edge edge = node.getEdge(sequence.get(canonized.second));
        System.out.println("Removing: "+canonized.first+" edge "+edge);
        while (edge!=null) {
            removeSequenceFromEdge(edge, sequence);
            edge = edge.parent.parentEdge;
        }
        if (node.suffixLink!=null) {
            removeSequenceFromBranch(node.suffixLink, sequence, canonized.second);
        }
    }

    @Override
    public String toString() {
        return root.getSubTree();
    }
}
