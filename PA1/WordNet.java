/* Ethan Cox
 * 7/5/18
 * Princeton Algorithms II
 * PA1: WordNet
 * WordNet.java
 */

import java.util.Map;
import java.util.HashMap;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.DirectedCycle;

public class WordNet {
    
    private final Map<String, Bag<Integer>> nouns;
    private final Map<Integer, String> ancestors;
    private final Digraph dg;
    private final SAP sap;
    
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        // Initialize both maps
        nouns = new HashMap<String, Bag<Integer>>();
        ancestors = new HashMap<Integer, String>();

        // Fill in the nouns map
        createNouns(synsets);
        
        // Create an empty digraph
        dg = new Digraph(ancestors.size());
           
        // Add the directed edges to the Digraph
        createDigraph(hypernyms);
            
        sap = new SAP(dg);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new java.lang.IllegalArgumentException("word is null");
        return nouns.containsKey(word);
    }

    // distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        return sap.length(nouns.get(nounA), nouns.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path
    public String sap(String nounA, String nounB) {
        int ancestorId = sap.ancestor(nouns.get(nounA), nouns.get(nounB));
        String[] valueFields = ancestors.get(ancestorId).split(",");
        return valueFields[0];
    }
    
    // Helper function to create nouns Map
    private void createNouns(String synsets) {
        In in = new In(synsets);
        while (in.hasNextLine()) {
            String curString = in.readLine();
            String[] fields = curString.split(",");
            for (int i = 0; i < fields.length; i++) {
                fields[i] = fields[i].trim();
            }
               
            int id = Integer.parseInt(fields[0]);
            String synsetDefinition = fields[1] + "," + fields[2];
            ancestors.put(id, synsetDefinition);
                
            String[] synonyms = fields[1].split(" ");
            for (int i = 0; i < synonyms.length; i++) {
                synonyms[i] = synonyms[i].trim();
                Bag<Integer> bag = nouns.get(synonyms[i]);
                if (bag == null) {
                    Bag<Integer> newBag = new Bag<Integer>();
                    newBag.add(id);
                    nouns.put(synonyms[i], newBag);
                }
                else {
                    bag.add(id);
                }
            }
        }
    }
    
    // Helper function to create the edges in the digraph
    private void createDigraph(String hypernyms) {
        In hyper = new In(hypernyms);
        String edge = "";
        String[] edges;
        int hypernym = 0;
        int hyponym = 0;
        while (hyper.hasNextLine()) {
            edge = hyper.readLine();
            edges = edge.split(",");
            hyponym = Integer.parseInt(edges[0]);  // Store the hyponym (tail) for the directed edges
            for (int i = 1; i < edges.length; i++) {
                hypernym = Integer.parseInt(edges[i]);  // Store the hypernym (head) for the directed edge
                dg.addEdge(hyponym, hypernym);
            }
        }
        
        // check if Digraph is a rooted DAG
        if (!rootedDAG(dg))
            throw new java.lang.IllegalArgumentException("Graph is not a rooted DAG");
    }
    
    // Helper function to check if graph is a rooted DAG
    private boolean rootedDAG(Digraph d) {
        // Check if there is a cycle in the graph
        DirectedCycle dc = new DirectedCycle(d);
        if (dc.hasCycle())
            return false;
        
        // Check if there is one root vertex (one with no adjacent edges leaving it)
        int roots = 0;
        for (int vertex = 0; vertex < d.V(); vertex++) {
            if (d.outdegree(vertex) == 0)
                roots++;
        }
        if (roots != 1)
            return false;
        
        return true;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet("synsets3.txt", "hypernyms3InvalidCycle.txt");
        System.out.println(wn.nouns());
        System.out.println(wn.isNoun("d"));
        System.out.println(wn.distance("b", "e"));
        System.out.println(wn.sap("b", "e"));
    }
}













