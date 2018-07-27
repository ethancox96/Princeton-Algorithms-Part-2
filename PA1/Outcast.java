/* Ethan Cox
 * 7/5/18
 * Princeton Algorithms II
 * PA1: WordNet
 * Outcast.java
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    
    private final WordNet wn;
    
    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wn = wordnet;
    }
    
    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int maxDiff = 0;
        int diff = 0;
        String outcast = nouns[0];
        for (String noun : nouns) {
            for (int i = 0; i < nouns.length; i++) {
                diff += wn.distance(noun, nouns[i]);
            }
            if (diff > maxDiff) {
                maxDiff = diff;
                outcast = noun;
            }
            diff = 0;
        }
        return outcast;
    }
    
    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}












