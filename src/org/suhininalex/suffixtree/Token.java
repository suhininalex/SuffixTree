package org.suhininalex.suffixtree;

public class Token<T> {
    public final T source;

    public Token(T source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token)
            return source.equals(((Token) obj).source);
        else return false;
    }

    @Override
    public int hashCode() {
        return source.hashCode();
    }

    @Override
    public String toString() {
        return source.toString();
    }
}
