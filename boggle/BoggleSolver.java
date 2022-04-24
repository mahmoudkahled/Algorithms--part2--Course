import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class BoggleSolver {
    private R_Tries trie = new R_Tries();
    private boolean[][] visited;
    private StringBuilder word = new StringBuilder();
    /*
    here we make 2 int arrays representing the  8 positions of cells around the current cell while we are traversing using DFS
    EX : cell() cell() cell() cell()
         *cell(x-1,y-1) *cell(x-1,y) *cell(x-1,y+1) cell()
         *cell(x,y-1) cell(x,y) *cell(x,y+1) cell()
         *cell(x+1,y-1) *cell(x+1,y) *cell(x+1,y+1) cell()
         cell() cell() cell() cell()
     */
    private int[] x_positions = {-1, -1, -1, 0, 0, 1, 1, 1};
    private int[] y_positions = {-1, 0, 1, -1, 1, -1, 0, 1};
    /*
     we are here making Visited_char Data Type to cuz in every recursion we want to maintain the values of the
     coordinates x and y fixed and safe and not changing while we are entering or outing the recursion using DFS
     */

    private static class Visited_char {
        int x;
        int y;

        public Visited_char() {

        }
    }

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        /*
        we used the R-Trie data structure because it is fast
        we here assign every word by its value (score) corresponding to the rules in the assignment to quickly
        retrieve the score of the word directly in the score of function
         */
        for (int i = 0; i < dictionary.length; i++) {
            if (dictionary[i].length() == 3 || dictionary[i].length() == 4) {
                trie.put(dictionary[i], 1);
            } else if (dictionary[i].length() == 5) {
                trie.put(dictionary[i], 2);
            } else if (dictionary[i].length() == 6) {
                trie.put(dictionary[i], 3);
            } else if (dictionary[i].length() == 7) {
                trie.put(dictionary[i], 5);
            } else if (dictionary[i].length() >= 8) {
                trie.put(dictionary[i], 11);

            } else {
                trie.put(dictionary[i], 0);
            }
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        // we make visited array to maintain we are not visiting the same cell again
        visited = new boolean[board.rows()][board.cols()];
        Visited_char coordinates = new Visited_char();
        SET<String> set = new SET<>();
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                coordinates.x = i;
                coordinates.y = j;
                visited[i][j] = true;
                // DFS
                getAllValidWords(board, set, trie, word, coordinates);
                // there is cases that there is some chars left in the object of string builder so we here ensure that
                // in the new iteration we are beginning with an object that is empty and begin from scratch the DFS
                // so we delete all the content of the object after the recursion function above finishes
                word.delete(0 , word.length());
                // making the cell not visited again for future DFS to be available
                visited[i][j] = false;
            }
        }
        return set;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!trie.contains(word)) {
            return 0;
        }
        return trie.get(word);
    }
    // recursion function
    private void getAllValidWords(BoggleBoard board, SET<String> set, R_Tries trie, StringBuilder word, Visited_char coordinates) {
        // check the special case if we have Q char in the board so it becomes (QU) in the String Builder Object and takes two char places
        if (board.getLetter(coordinates.x, coordinates.y) == 'Q') {
            word.append("QU");
        } else {
            // if it wasn't Q we reach the original case that we put the current char in the object just as that
            word.append(board.getLetter(coordinates.x, coordinates.y));
        }
        if (!trie.contains_substring(word.toString())) {
            // the Q char has two places in the object so it needs to delete two char places
            // so delete one char here and the other in the for loop
            if (word.charAt(word.length() - 2) == 'Q' && word.charAt(word.length() - 1) == 'U') {
                word.deleteCharAt(word.length() - 1);
            }
            return;
        }
        if (trie.contains(word.toString()) && !set.contains(word.toString()) && word.length() > 2) {
            set.add(word.toString());
        }
        // it has to be zero as in every recursion in the addition below we want only to add the coordinates values
        // for one of the locations in the positions array , and not be added to ant additional values
        int new_i = 0, new_j = 0;
        // this object will be the coordinates that will be assigned to the next recursion to avoid conflict
        Visited_char new_coordinates = new Visited_char();
        for (int counter = 0; counter < 8; counter++) {
            // we here reach one of the 8 positions around the current cell
            new_i = coordinates.x + x_positions[counter];
            new_j = coordinates.y + y_positions[counter];
            if (validate_index(board, new_i, new_j) && !visited[new_i][new_j]) {
                visited[new_i][new_j] = true;
                new_coordinates.x = new_i;
                new_coordinates.y = new_j;
                // begin a new DFS
                getAllValidWords(board, set, trie, word, new_coordinates);
                // if the char wasn't contained in the dictionary , so we have to delete it in the new iteration to put
                // a new char instead of it and continuing search in the dictionary
                word.deleteCharAt(word.length() - 1);
                // this is a very special case that result while DFS is working , that a char Q is left in the object
                // without deletion and that cause a wrong results afterthat , so here if this char still exist we delete it
                if(word.charAt(word.length() - 1) == 'Q'){
                    word.deleteCharAt(word.length() - 1);
                }
                visited[new_i][new_j] = false;
            }
            // restore the value again for the new iteration
            new_i = 0;
            new_j = 0;
        }
    }
    // this function to ensure that the values won't be negative of greater than the array bounds
    private boolean validate_index(BoggleBoard board, int i, int j) {
        if (i < 0 || i > (board.rows() - 1) || j < 0 || j > (board.cols() - 1)) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}