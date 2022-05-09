import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.ArrayList;


public class MoveToFront {
    private static final int R = 256;
    private static char[] ordered_sequence = new char[R];
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        StringBuilder in = new StringBuilder();
        for(int i = 0; i < R ; i++){
            ordered_sequence[i] = (char) i;
        }
        while (!BinaryStdIn.isEmpty()){
            in.append(BinaryStdIn.readChar());
        }
        BinaryStdIn.close();
        int[] out = new int[in.length()];
        int y = 0;
        for(int x = 0 ; x < in.length() ; x++){
            // searching for the char in the array
            for(int i = 0 ; i < R ; i++){
                if(in.charAt(x) == ordered_sequence[i]){
                    y = i;
                    break;
                }
            }
            // save the index of the char
            out[x] = y;
            char temp = ordered_sequence[y];
            // shift array to right
            for(int i = (y - 1) ; i >= 0  ; i--){
               ordered_sequence[i + 1] = ordered_sequence[i];
            }
            // move that char to front
            ordered_sequence[0] = temp;
        }
        for (int j : out) {
            BinaryStdOut.write(j, 8);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        ArrayList<Integer> al = new ArrayList<>();
        for(int i = 0; i < R ; i++){
            ordered_sequence[i] = (char) i;
        }
        while (!BinaryStdIn.isEmpty()){
            al.add((int)BinaryStdIn.readChar());
        }
        BinaryStdIn.close();
        char[] out = new char[al.size()];
        int y = 0;
        for(int x = 0 ; x < al.size() ; x++){
            y = al.get(x);
            /*
            in the decoder case we are given some integers not chars , so now know the index , simply we go into
            the array to that index take its char and move it to front of the array as simple as that
            */
            char temp = ordered_sequence[y];
            // get chat to print it to standard output
            out[x] = ordered_sequence[y];
            // move array to right
            for(int i = (y - 1) ; i >= 0  ; i--){
                ordered_sequence[i + 1] = ordered_sequence[i];
            }
            // move char to front
            ordered_sequence[0] = temp;
        }
        for (int j : out) {
            BinaryStdOut.write(j, 8);
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        } else if (args[0].equals("+")) {
            decode();
        } else {
            throw new IllegalArgumentException("You passed Illegal Argument, please pass - or +");
        }
    }
}
