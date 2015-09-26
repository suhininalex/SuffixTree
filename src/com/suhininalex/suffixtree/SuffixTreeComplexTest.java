package com.suhininalex.suffixtree;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SuffixTreeComplexTest extends TestCase {
    int sequencesAmount = 100;
    int sequencesLength = 1000;
    int sequencesRemovals = 50;

    @Test
    public void testComplexRemoveSequence() {
        System.out.println("Complex tree testing");
        SuffixTree tree = new SuffixTree();
        List<List<Character>> sequences = generateRandomLists(sequencesAmount, sequencesLength);
        List<Long> sequencesId = new ArrayList<>();
        for (List sequence : sequences) {
            Long id = tree.addSequence(sequence);
            sequencesId.add(id);
        }

        System.out.print("Testing added sequences...");
        checkSequences(tree, sequences);
        System.out.println("OK");

        //—писок дл€ удалени€
        List<Long> removedId = new ArrayList<>();
        List<List> removedSequences = new ArrayList<>();
        for (int i=0; i<sequencesRemovals; i++){
            int index = getRandom(sequencesId.size());
            removedId.add(sequencesId.get(index));
            sequencesId.remove(index);
            sequences.remove(index);
        }

        //ѕолучение внутренних ссылок на удаленные последовательности
        for (long id : removedId) {
            removedSequences.add((List) tree.sequences.get(id));
        }


        System.out.print("Removing...");
        for (Long id : removedId){
            tree.removeSequence(id);
        }
        System.out.println("OK");


        System.out.print("Check other existance...");
        checkSequences(tree, sequences);
        System.out.println("OK");


        System.out.print("Check relabelling and edges removal...");
        for (List sequence : removedSequences){
            assertEquals(true, checkNoSequence(tree.root, sequence));
        }
        System.out.println("OK");

    }

    private static void checkSequences(SuffixTree tree, List<List<Character>> sequences){
        for (List sequence : sequences) {
            assertEquals("Some suffixies are missing!", true,checkAllSuffixies(tree, sequence)) ;
        }
    }

    private static boolean checkNoSequence(Node node, List sequence){
        if (node==null) return true;
        for (Edge edge : node.getEdges()){
            if (edge.sequence==sequence) {
                System.out.println(node + "|" + edge);
                return false;
            }
            if (!checkNoSequence(edge.terminal, sequence)) return false;
        }
        return true;
    }

    private static boolean checkAllSuffixies(SuffixTree tree, List sequence){
        for (int i=0; i<sequence.size(); i++){
            if (!tree.checkSequence(sequence.subList(i, sequence.size()))) return false;
        }
        return true;
    }

    private static List<List<Character>> generateRandomLists(int amount, int length){
        List<List<Character>> lists = new ArrayList<>();
        for (int i=0; i<amount; i++){
            lists.add(generateRandomList(length));
        }
        return lists;
    }

    private static List<Character> generateRandomList(int length){
        List<Character> result = new ArrayList<>();
        for (int i = 0; i<length; i++) {
            result.add(randChar());
        }
        return result;
    }

    private static char randChar () {
        int rnd = (int) (Math.random() * 26); // or use Random or whatever
        char base = 'a';
        return (char) (base + rnd);
    }

    private static int getRandom(int maxInt){
        return (int)(Math.random()*maxInt);
    }
}