package org.suhininalex.suffixtree;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SuffixTreeCreationTest {
    SuffixTree tree = new SuffixTree();;
    List<Token> sequence = new ArrayList<>();
    List<Token> sequence2 = new ArrayList<>();

    @Test
    public void testUpdate() {
        System.out.println("Test simple update");
        tree.update(tree.root,sequence,0,0);
        System.out.println(tree);
    }

    @Test
         public void testAddSequence() {
        System.out.println("Test addSequence cacao");
        tree.addSequence(sequence);
        System.out.println(tree);
    }

    @Test
    public void testAddSequences() {
        System.out.println("Test addSequences");
        tree.addSequence(sequence2);
        tree.addSequence(sequence);
        System.out.println(tree);
    }

    @Before
    public void setUp(){
        char [] string = {'c','a','c','a','o'};
        for (char elem : string){
            sequence.add(new Token<>(elem));
        }

        char [] string2 = {'c','a','c','a','o','a'};
        for (char elem : string2){
            sequence2.add(new Token<>(elem));
        }
        System.out.println("-------------------------");
    }
}