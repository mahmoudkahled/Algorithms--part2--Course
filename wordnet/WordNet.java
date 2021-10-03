import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.DirectedCycle;

import java.util.ArrayList;
import java.util.LinkedList;

public class WordNet {
    private ArrayList<String> ids;
    private Digraph graph;
    private ST<String, ArrayList<Integer>> synsetsids;
    private SAP sap;
    private DirectedCycle cycle;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }
        In in = new In(synsets);
        ids = new ArrayList<>();
        synsetsids = new ST<>();
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] splitted_ids = line.split(",");
            String[] new_splitted_ids = splitted_ids[1].split(" ");
            int synid = Integer.parseInt(splitted_ids[0]);
            ids.add(splitted_ids[1]);
            for (int i=0; i<new_splitted_ids.length; i++) {
                if (synsetsids.contains(new_splitted_ids[i])) {
                    synsetsids.get(new_splitted_ids[i]).add(synid);
                } else {
                    synsetsids.put(new_splitted_ids[i], new ArrayList<Integer>());
                    synsetsids.get(new_splitted_ids[i]).add(synid);
                }
            }
        }
        In in2 = new In(hypernyms);
        graph = new Digraph(ids.size());
        while (in2.hasNextLine()) {
            String line = in2.readLine();
            String[] splitted_ids = line.split(",");
            for (int i = 1; i < splitted_ids.length; i++) {
                graph.addEdge(Integer.parseInt(splitted_ids[0]), Integer.parseInt(splitted_ids[i]));
            }
        }
        cycle = new DirectedCycle(graph);
        if (cycle.hasCycle()) {
            throw new IllegalArgumentException();
        }
        sap = new SAP(graph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return synsetsids.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        return synsetsids.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException();
        }
        int z;
        if (isNoun(nounA) || isNoun(nounB)) {
            z = sap.length(synsetsids.get(nounA), synsetsids.get(nounB));
        } else {
            throw new IllegalArgumentException();
        }
        return z;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException();
        }
        int z;
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        } else {
            z = sap.ancestor(synsetsids.get(nounA), synsetsids.get(nounB));
        }
        String sap = ids.get(z);
        return sap;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordNet = new WordNet("synsets15.txt" , "hypernyms15Path.txt");
        System.out.println(wordNet.distance("a","b"));
    }
}
