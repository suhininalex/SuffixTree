package org.suhininalex.suffixtree;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Node {
    private static final AtomicLong nextId = new AtomicLong(1);
    private final long id = nextId.incrementAndGet();

    private final Map<Object, Edge> edges = new HashMap<>();

    Edge parentEdge;
    Node suffixLink;

    Node(Edge parentEdge) {
        this.parentEdge = parentEdge;
    }

    Edge putEdge(Node terminal, List sequence, int k, int p){
        Edge edge = new Edge(this, terminal, sequence, k, p);
        edges.put(edge.getFirstToken(), edge);
        return edge;
    }

    Edge putEdge(List sequence, int k){
        Edge edge = new Edge(this, null, sequence, k, sequence.size()-1);
        edges.put(edge.getFirstToken(), edge);
        return edge;
    }

    Edge getEdge(Object token) {
        return edges.get(token);
    }

    void removeEdge(Edge edge){
        edges.remove(edge.getFirstToken());
    }

    private void printToStringBuilder(StringBuilder out, String prefix){
        out.append(prefix).append(this).append("\n");
        for (Edge edge : getEdges()){
            out.append(prefix).append(edge).append("\n");
            if (edge.terminal!=null) edge.terminal.printToStringBuilder(out, prefix + "    ");
        }
    }

    public String subTreeToString(){
        StringBuilder out = new StringBuilder();
        this.printToStringBuilder(out, "");
        return out.toString();
    }

    public Collection<Edge> getEdges(){
        return edges.values();
    }

    @Override
    public String toString() {
        return "Node("+id+") Parent("+(parentEdge==null ? "null" : parentEdge.parent.id)+")";
    }

    public Node getSuffixLink() {
        return suffixLink;
    }

    public Edge getParentEdge() {
        return parentEdge;
    }
}
