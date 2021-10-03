import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;



public class SAP {
    private Digraph graph;
    private BreadthFirstDirectedPaths first_vertex;
    private BreadthFirstDirectedPaths second_vertex;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException();
        }
        graph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (!isValid(v) || !isValid(w)) {
            throw new IllegalArgumentException();
        }
        if (ancestor(v, w) == -1) {
            return -1;
        }
        int distance;
        distance = first_vertex.distTo(ancestor(v, w)) + second_vertex.distTo(ancestor(v, w));
        return distance;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (!isValid(v) || !isValid(w)) {
            throw new IllegalArgumentException();
        }
        int max_path_length = Integer.MAX_VALUE;
        int path_lenth;
        int ancestor = -1;
        first_vertex = new BreadthFirstDirectedPaths(graph, v);
        second_vertex = new BreadthFirstDirectedPaths(graph, w);
        for (int vertex = 0; vertex < graph.V(); vertex++) {
            if (first_vertex.hasPathTo(vertex) && second_vertex.hasPathTo(vertex)) {
                path_lenth = first_vertex.distTo(vertex) + second_vertex.distTo(vertex);
                if (path_lenth < max_path_length) {
                    max_path_length = path_lenth;
                    ancestor = vertex;
                }
            }
        }
        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if(v == null || w == null){
            throw new IllegalArgumentException();
        }
        if (!isValid(v) || !isValid(w)) {
            throw new IllegalArgumentException();
        }
        if (ancestor(v, w) == -1) {
            return -1;
        }
        int distance;
        distance = first_vertex.distTo(ancestor(v, w)) + second_vertex.distTo(ancestor(v, w));
        return distance;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if(v == null || w == null){
            throw new IllegalArgumentException();
        }
        if (!isValid(v) || !isValid(w)) {
            throw new IllegalArgumentException();
        }
        int max_path_length = Integer.MAX_VALUE;
        int path_lenth;
        int ancestor = -1;
        first_vertex = new BreadthFirstDirectedPaths(graph, v);
        second_vertex = new BreadthFirstDirectedPaths(graph, w);
        for (int vertex = 0; vertex < graph.V(); vertex++) {
            if (first_vertex.hasPathTo(vertex) && second_vertex.hasPathTo(vertex)) {
                path_lenth = first_vertex.distTo(vertex) + second_vertex.distTo(vertex);
                if (path_lenth < max_path_length) {
                    max_path_length = path_lenth;
                    ancestor = vertex;
                }
            }
        }
        return ancestor;
    }

    private boolean isValid(int x) {
        return !(x < 0 || x > this.graph.V());
    }

    private boolean isValid(Iterable<Integer> x) {
        for (Integer w : x) {
            if(w == null){
                return false;
            }
            if (w < 0 || w > this.graph.V()) {
                return false;
            }
        }
        return true;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
