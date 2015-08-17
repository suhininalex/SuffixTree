package org.suhininalex.suffixtree;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SuffixTreeRemovingTest {

    SuffixTree tree = new SuffixTree();;
    List sequence1 = new ArrayList<>();
    List sequence2 = new ArrayList<>();

//    @Test
//    public void testGetEdgeForSequence() throws Exception {
//        System.out.println("Test find last edge for existing sequence");
//        System.out.println(tree);
//
//        System.out.println("Searching for "+sequence1);
//        Edge finalEdge = tree.getEdgeForSequence(sequence1);
//        System.out.println(finalEdge.parent);
//        System.out.println(finalEdge);
//    }

//    @Test
//    public void testSimpleLeafRemove() throws Exception {
//        System.out.println("Test simple leaf removing");
//        System.out.println(tree);
//        Edge finalEdge = tree.getEdgeForSequence(sequence1);
//        tree.removeSequenceFromEdge(finalEdge, sequence1);
//        System.out.println(tree);
//    }

    @Test
    public void testSimpleRemoveSequence() throws Exception {
        System.out.println("Test simple remove sequence");
        System.out.println(tree);
        tree.removeSequenceFromBranch(tree.root,sequence1, 0);
        System.out.println(tree);
    }

    @Before
    public void setUp(){
        char [] string = {'c','a','c','a','o'};
        for (char elem : string){
            sequence1.add(elem);
        }

        char [] string2 = {'c','a','c','a','o','a'};
        for (char elem : string2){
            sequence2.add(elem);
        }

        tree.addSequence(sequence1);
        tree.addSequence(sequence2);
        System.out.println("-------------------------");
    }
}