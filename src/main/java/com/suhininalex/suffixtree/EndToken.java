package com.suhininalex.suffixtree;

public class EndToken {
    Long idSequence;

    public EndToken(long idSequence) {
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
