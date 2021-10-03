import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int max_distance = Integer.MIN_VALUE;
        int distance = 0;
        String outcast = "null";
        for (int i = 0; i < nouns.length; i++) {
            if (wordnet.isNoun(nouns[i])) {
                for (int j = 0; j < nouns.length; j++) {
                    distance += wordnet.distance(nouns[i], nouns[j]);
                }
                if (distance > max_distance) {
                    max_distance = distance;
                    outcast = nouns[i];
                }
            }
            distance = 0;
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
