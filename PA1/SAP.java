/* Ethan Cox
 * 7/5/18
 * Princeton Algorithms II
 * PA1: WordNet
 * SAP.java
 */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    
    private final Digraph dg;
    private int length = -1;
    private int ancestor = -1;
    
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null)
            throw new java.lang.IllegalArgumentException("Argument is null");
        this.dg = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v < 0 || v > dg.V())
            throw new IllegalArgumentException("v is out of bounds");
        if (w < 0 || w > dg.V())
            throw new IllegalArgumentException("w is out of bounds");
        bfs(v, w);
        return length;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v < 0 || v > dg.V())
            throw new IllegalArgumentException("v is out of bounds");
        if (w < 0 || w > dg.V())
            throw new IllegalArgumentException("w is out of bounds");
        bfs(v, w);
        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null)
            throw new IllegalArgumentException("v contains no vertices");
        if (w == null)
            throw new IllegalArgumentException("w contains no vertices");
        for (int a : v) {
            if (a < 0 || a > dg.V() - 1) throw new java.lang.IllegalArgumentException("v is out of bounds");   
        }
        for (int b : w) {
            if (b < 0 || b > dg.V() - 1) throw new java.lang.IllegalArgumentException("w is out of bounds");   
        }
        bfs(v, w);
        return length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null)
            throw new IllegalArgumentException("v contains no vertices");
        if (w == null)
            throw new IllegalArgumentException("w contains no vertices");
        for (int a : v) {
            if (a < 0 || a > dg.V() - 1) throw new java.lang.IllegalArgumentException("v is out of bounds");   
        }
        for (int b : w) {
            if (b < 0 || b > dg.V() - 1) throw new java.lang.IllegalArgumentException("w is out of bounds");   
        }
        bfs(v, w);
        return ancestor;
    }
    
    private void bfs(int v, int w) {
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(dg, v);
        if (bfsV.hasPathTo(w)) {
            length = bfsV.distTo(w);
        }
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(dg, w);
        int curlen = -1, minlen = -1;
        for (int vertex = 0; vertex < dg.V(); vertex++) {
            if (bfsV.hasPathTo(vertex) && bfsW.hasPathTo(vertex)) {
                curlen = bfsV.distTo(vertex) + bfsW.distTo(vertex);
                if (minlen == -1) {
                    minlen = curlen;
                    ancestor = vertex;
                }
                else {
                    if (curlen < minlen) {
                        minlen = curlen;
                        ancestor = vertex;
                    }
                }
            }
        }
        length = minlen;
        if (length == -1)
            ancestor = -1;
    }
    
    private void bfs(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(dg, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(dg, w);
        int curlen = -1, minlen = -1;
        for (int vertex = 0; vertex < dg.V(); vertex++) {
            if (bfsV.hasPathTo(vertex) && bfsW.hasPathTo(vertex)) {
                curlen = bfsV.distTo(vertex) + bfsW.distTo(vertex);
                if (minlen == -1) {
                    minlen = curlen;
                    ancestor = vertex;
                }
                else {
                    if (curlen < minlen) {
                        minlen = curlen;
                        ancestor = vertex;
                    }
                }
            }
        }
        length = minlen;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}












