import java.util.Arrays;

public class CircularSuffixArray {
    private String string;
    private int[] index;

    /*
    this data structure is very useful and considered the key solution to the suffix array , as we every suffix
    we store with it is index so when we sort the suffixes we can construct the index array very easy from
    this data structure

    we implement comparable so as to the make this data structure is able to be sorted by the sort function
    the compare function works as follow we compare each char in two suffixes until we found one is bigger
    smaller or equal , to find the biggest or smallest suffix between two suffixes
    
    */
    private static class CircularSuffix implements Comparable<CircularSuffix> {
        private int index, length;
        private String suffix;

        CircularSuffix(String suffix, int index) {
            this.index = index;
            this.length = suffix.length();
            this.suffix = suffix;
        }

        int charAt(int i) {
            int index_suffix = i + index;
            if (index_suffix >= length) {
                index_suffix = index_suffix - length;
            }
            int value = this.suffix.charAt(index_suffix);
            return value;
        }

        public int compareTo(CircularSuffix that) {
            for (int i = 0; i < length; i++) {
                int compare = Integer.compare(this.charAt(i), that.charAt(i));
                if (compare > 0) {
                    return 1;
                } else if (compare < 0) {
                    return -1;
                }
            }
            return 0;
        }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Illegal Argument");
        }
        string = s;
        index = new int[length()];
        CircularSuffix[] cs = new CircularSuffix[length()];
        for (int i = 0; i < length(); i++) {
            cs[i] = new CircularSuffix(s, i);
        }
        Arrays.sort(cs);
        for (int i = 0; i < length(); i++) {
            index[i] = cs[i].index;
        }
    }

    // length of s
    public int length() {
        return string.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i > (length() - 1)) {
            throw new IllegalArgumentException("index is outside its prescribed range");
        }
        return index[i];
    }


    // unit testing (required)
    public static void main(String[] args) {
        String s = "HNH";
        CircularSuffixArray c = new CircularSuffixArray(s);
        System.out.println("the length of s is: " + c.length());
        for (int i = 0; i < c.length(); i++) {
            System.out.println("the index of " + i + " th sorted suffix is: " + c.index(i));
        }
    }
}
