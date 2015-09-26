package com.suhininalex.suffixtree;

import java.util.List;
public class Edge {
    Node parent;
    Node terminal;

    List sequence;
    int k;
    int p;

    Edge(Node parent, Node terminal, List sequence, int k, int p) {
        this.parent = parent;
        this.terminal = terminal;
        this.sequence = sequence;
        this.k = k;
        this.p = p;
    }

    @Override
    public String toString() {
        return sequence.subList(k, p+1).toString();
    }

    Object getFirstToken(){
        return sequence.get(k);
    }

    public Node getParent() {
        return parent;
    }

    public Node getTerminal() {
        return terminal;
    }

    public List getSequence() {
        return sequence;
    }

    public int getBegin() {
        return k;
    }

    public int getEnd() {
        return p;
    }
}
