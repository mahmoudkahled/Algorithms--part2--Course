import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Arrays;

public class BurrowsWheeler {
    private static char[] t;
    private static int[] next;
    private static int first_number;

    /*
    the next data structure is a key solution to the inverse transform function, as it is the only solution to make
    the complexity of the function doesnt exceed n + R , so this data structre make it is to easy to form the 
    next array that is used next to get the original input , this data structure helps me reduce the complexity
    from n ^2 to n + R , as every char passed to this data structre we pass with it its index that is needed further
    after we sort the char and forming next array
    */
    private static class Next implements Comparable<Next> {
        int index;
        char character;

        Next(char character, int index) {
            this.character = character;
            this.index = index;
        }

        public int compareTo(Next that) {
            int cmp = Integer.compare(this.character, that.character);
            if (cmp > 0) {
                return 1;
            } else if (cmp < 0) {
                return -1;
            }
            return 0;
        }
    }

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = "", inverse_s = "";
        StringBuilder sb = new StringBuilder();
        // read stream
        while (!BinaryStdIn.isEmpty()) {
            s = BinaryStdIn.readString();
        }
        BinaryStdIn.close();
        CircularSuffixArray cs = new CircularSuffixArray(s);
        t = new char[cs.length()];
        sb.append(s);
        /*
        the job of the string builder is that , why we make reverse , because it makes it easy to guess the last element
        in the sorted suffix give the index array only , so we make reverse to the string to begin counting from back
        this helps a lot to find the last char in every sorted suffix
        */
        sb.reverse();
        inverse_s = sb.toString();
        int char_index;
        for (int i = 0; i < cs.length(); i++) {
            if (cs.index(i) == 0) {
                // here we indicate that it is the original input is located in that row
                first_number = i;
            }
            /*
            a simple calculation to get the last char , we delete the index from the index of last element
            Ex if the string length is 12 , so last element in this string has index 11 , so we delete from
            that index , then wee add 1
            */
            char_index = (cs.length() - 1) - cs.index(i) + 1;
            if (char_index > (cs.length() - 1)) {
                // this line means that , this char is the last char in string
                char_index = char_index - 1 - (cs.length() - 1);
            }
            t[i] = inverse_s.charAt(char_index);
        }
        BinaryStdOut.write(first_number);
        for (int i = 0; i < t.length; i++) {
            BinaryStdOut.write(t[i]);
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        String s = "";
        int number = 0;
        StringBuilder sb = new StringBuilder();
        while (!BinaryStdIn.isEmpty()) {
            number = BinaryStdIn.readInt();
            s = BinaryStdIn.readString();
        }
        BinaryStdIn.close();
        Next[] next_array = new Next[s.length()];
        for (int i = 0; i < s.length(); i++) {
            next_array[i] = new Next(s.charAt(i), i);
        }
        Arrays.sort(next_array);
        sb.append(next_array[number].character);
        int next_number = number;
        while (sb.length() != (s.length() - 1)) {
            next_number = next_array[next_number].index;
            sb.append(next_array[next_number].character);
        }
        sb.append(s.charAt(number));
        BinaryStdOut.write(sb.toString());
        BinaryStdOut.close();
    }


    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            transform();
        } else if (args[0].equals("+")) {
            inverseTransform();
        } else {
            throw new IllegalArgumentException("Illegal Argument");
        }
    }

}
