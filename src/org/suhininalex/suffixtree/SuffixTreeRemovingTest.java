package org.suhininalex.suffixtree;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SuffixTreeRemovingTest {

    SuffixTree tree = new SuffixTree();;
    List sequence1 = new ArrayList<>();
    List sequence2 = new ArrayList<>();

    @Test
    public void testSimpleRemoveSequence() {
        System.out.println("Test simple remove sequence");
        Long id1 = tree.addSequence(sequence1);
        Long id2 = tree.addSequence(sequence2);

        System.out.println(tree);
        tree.removeSequence(id2);
        System.out.println(tree);
    }

//    @Test
    /*
        Test with visualvm
        (OK)
    */
//    public void testMemoryLeakWhenRemoveSequence() {
//        System.out.println("Test complex remove sequence");
//        tree.addSequence(sequence1);
//        System.out.println(tree);
//        for (int i=0;i<1000000000; i++){
//            long id = tree.addSequence(sequence2);
//            tree.removeSequence(id);
//        }
//        System.out.println(tree);
//    }

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


        System.out.println("-------------------------");
    }
}