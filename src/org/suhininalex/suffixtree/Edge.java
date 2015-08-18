package org.suhininalex.suffixtree;

import org.jetbrains.annotations.Nullable;

import java.util.List;
public class Edge {
    Node parent;
    Node terminal;

    List sequence;
    int k;
    int p;

    protected Edge(Node parent, Node terminal, List sequence, int k, int p) {
        this.parent = parent;
        this.terminal = terminal;
        this.sequence = sequence;
        this.k = k;
        this.p = p;
    }

    @Override
    public String toString() {
        return sequence.subList(k, p+1).toString() + "  " +  k + " " + p + " |" + sequence.hashCode();
//        return sequence.subList(k, p+1).toString()+"  |  "+sequence.hashCode();
    }

    Object getFirstToken(){
        return sequence.get(k);
    }
}
