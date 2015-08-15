package org.suhininalex.suffixtree;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SuffixTreeBasicTest {
    SuffixTree tree = new SuffixTree();;
    List<Token> sequence = new ArrayList<>();

    @Test
    public void testTestAndSplit_noSplit() {
        System.out.println("Test test and split (no split)");
        System.out.println(tree);
        tree.testAndSplit(tree.root, sequence, 0, 3, new Token<>('o'));
        System.out.println(tree);
    }

    @Test
    public void testTestAndSplit_split() {
        System.out.println("Test test and split (split)");
        System.out.println(tree);
        tree.testAndSplit(tree.root, sequence, 0, 3, new Token<>('q'));
        System.out.println(tree);
    }

    @Test
    public void testCanonize_needed() {
        System.out.println("Test canonize (needed)");
        tree.testAndSplit(tree.root, sequence, 0, 2, new Token<>('q'));
        System.out.println(tree);
        Tuple<Node,Integer> canonizeRes = tree.canonize(tree.root, sequence, 0, 4);
        System.out.println(canonizeRes.first + " | " + canonizeRes.second);
    }

    @Test
    public void testCanonize_unnecessary(){
        System.out.println("Test canonize (unnecessary)");
        tree.testAndSplit(tree.root, sequence, 0, 2, new Token<>('q'));
        System.out.println(tree);
        Tuple<Node,Integer> canonizeRes = tree.canonize(tree.root, sequence, 0, 1);
        System.out.println(canonizeRes.first + " | " + canonizeRes.second);
    }

    @Test
    public void testCanonize_preroot() {
        System.out.println("Test canonize (pre root/null)");
        tree.testAndSplit(tree.root, sequence, 1, 2, new Token<>('q'));
        System.out.println(tree);
        Tuple<Node,Integer> canonizeRes = tree.canonize(null, sequence, 0, 4);
        System.out.println(canonizeRes.first + " | " + canonizeRes.second);
    }

    @Before
    public void setUp(){
        char [] string = {'c','a','c','a','o'};
        for (char elem : string){
            sequence.add(new Token<>(elem));
        }
        sequence.add(new EndToken(0));
        tree.root.putEdge(sequence, 0);
        tree.root.putEdge(sequence, 1);
        System.out.println("-------------------------");
    }

}