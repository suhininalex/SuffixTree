package org.suhininalex.suffixtree;

public class EndToken extends Token {
    Integer idSequence;

    public EndToken(int idSequence) {
        super(null);
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
