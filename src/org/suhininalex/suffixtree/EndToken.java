package org.suhininalex.suffixtree;

public class EndToken {
    Integer idSequence;

    public EndToken(int idSequence) {
        this.idSequence = idSequence;
    }

    @Override
    public int hashCode() {
        return idSequence.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return idSequence.equals(obj);
    }

    @Override
    public String toString() {
        return "#"+idSequence;
    }
}
