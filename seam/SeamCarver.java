import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdRandom;

import java.awt.Color;

public class SeamCarver {
    private Picture local_picture;
    private double[][] energy;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        local_picture = new Picture(picture);
        energy = new double[local_picture.width()][local_picture.height()];
        for (int x = 0; x < local_picture.width(); x++) {
            for (int y = 0; y < local_picture.height(); y++) {
                energy[x][y] = energy(x, y);
            }
        }
    }

    // current picture
    public Picture picture() {
        return new Picture(local_picture);
    }

    // width of current picture
    public int width() {
        return local_picture.width();
    }

    // height of current picture
    public int height() {
        return local_picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x >= local_picture.width() || y >= local_picture.height() || x < 0 || y < 0) {
            throw new IllegalArgumentException();
        }
        if ((x > 0 && x < (width() - 1)) && (y > 0 && y < (height() - 1))) {
            int red1, green1, blue1, rx, gx, bx, red2, green2, blue2;
            double energy, deltax, deltay;
            Color color1, color2;
            // Calculate the RGB of pixel (x+1 , y) , (x-1 , y)
            color1 = local_picture.get(x + 1, y);
            color2 = local_picture.get(x - 1, y);
            red1 = color1.getRed();
            green1 = color1.getGreen();
            blue1 = color1.getBlue();
            red2 = color2.getRed();
            green2 = color2.getGreen();
            blue2 = color2.getBlue();
            rx = red1 - red2;
            gx = green1 - green2;
            bx = blue1 - blue2;
            deltax = Math.pow(rx, 2) + Math.pow(gx, 2) + Math.pow(bx, 2);
            // Calculate the RGB of pixel (x+1 , y+1) , (x , y-1)
            color1 = local_picture.get(x, y + 1);
            color2 = local_picture.get(x, y - 1);
            red1 = color1.getRed();
            green1 = color1.getGreen();
            blue1 = color1.getBlue();
            red2 = color2.getRed();
            green2 = color2.getGreen();
            blue2 = color2.getBlue();
            rx = red1 - red2;
            gx = green1 - green2;
            bx = blue1 - blue2;
            deltay = Math.pow(rx, 2) + Math.pow(gx, 2) + Math.pow(bx, 2);
            energy = Math.sqrt(deltax + deltay);
            return energy;
        } else {
            return 1000;
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int random1 = 0;
        double least_energy = Double.POSITIVE_INFINITY;
        int[] horizontal_seam = new int[local_picture.width()];
        for(int i = 1 ; i < local_picture.height() ; i++){
            if(energy[1][i] < least_energy){
                least_energy = energy[1][i];
                random1 = i;
            }
        }
        horizontal_seam[0] = random1;
        for (int i = 1; i < local_picture.width(); i++) {
            if ((random1 > 0) && (random1 < (height() - 1))) {
                random1 = comparison_3edges((i), (random1 - 1), (i), (random1), (i), (random1 + 1));
                horizontal_seam[i] = random1;
            }
        }
        return horizontal_seam;

    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int random1 = 0;
        double least_energy = Double.POSITIVE_INFINITY;
        int[] vertical_seam = new int[local_picture.height()];
        for(int i = 1 ; i < local_picture.width() ; i++){
            if(energy[i][1] < least_energy){
                least_energy = energy[i][1];
                random1 = i;
            }
        }
        vertical_seam[0] = random1;
        for (int i = 1; i < local_picture.height(); i++) {
            if ((random1 > 0) && (random1 < (width() - 1))) {
                random1 = comparison_3edges((random1 - 1), (i), (random1), (i), (random1 + 1), (i));
                vertical_seam[i] = random1;
            }
        }
        return vertical_seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }
        if (local_picture.height() <= 1) {
            throw new IllegalArgumentException();
        }
        if (seam.length > local_picture.width() || seam.length < local_picture.width()) {
            throw new IllegalArgumentException();
        }
        if(seam[0] < 0 || seam[0] >= local_picture.height()){
            throw new IllegalArgumentException();
        }
        for (int i = 1; i < seam.length; i++) {
            if (seam[i] >= local_picture.height()) {
                throw new IllegalArgumentException();
            }
            if (seam[i] < 0) {
                throw new IllegalArgumentException();
            }
            if (((seam[i] - seam[i - 1]) > 1) || ((seam[i - 1] - seam[i]) > 1)) {
                throw new IllegalArgumentException();
            }
        }
        int i = 0;
        Picture resized_picture = new Picture(local_picture.width(), local_picture.height() - 1);
        double[][] shift_array = new double[local_picture.width()][local_picture.height() - 1];
        for (int x = 0; x < local_picture.width(); x++) {
            for (int y = 0, z = 0; y < local_picture.height(); y++, z++) {
                if (y == seam[i]) {
                    z--;
                    continue;
                } else {
                    shift_array[x][z] = energy[x][y];
                    resized_picture.set(x, z, local_picture.get(x, y));
                }
            }
            i++;
        }
        this.energy = shift_array;
        this.local_picture = resized_picture;


    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }
        if (local_picture.width() <= 1) {
            throw new IllegalArgumentException();
        }
        if (seam.length > local_picture.height() || seam.length < local_picture.height()) {
            throw new IllegalArgumentException();
        }
        if(seam[0] < 0 || seam[0] >= local_picture.width()){
            throw new IllegalArgumentException();
        }
        for (int i = 1; i < seam.length; i++) {
            if (seam[i] >= local_picture.width()) {
                throw new IllegalArgumentException();
            }
            if (seam[i] < 0) {
                throw new IllegalArgumentException();
            }
            if (((seam[i] - seam[i - 1]) > 1) || ((seam[i - 1] - seam[i]) > 1)) {
                throw new IllegalArgumentException();
            }

        }
        int i = 0;
        Picture resized_picture = new Picture(local_picture.width() - 1, local_picture.height());
        double[][] shift_array = new double[local_picture.width() - 1][local_picture.height()];
        for (int y = 0; y < local_picture.height(); y++) {
            for (int x = 0, z = 0; x < local_picture.width(); x++, z++) {
                if (x == seam[i]) {
                    z--;
                    continue;
                } else {
                    shift_array[z][y] = energy[x][y];
                    resized_picture.set(z, y, local_picture.get(x, y));
                }
            }
            i++;
        }
        this.energy = shift_array;
        this.local_picture = resized_picture;
    }

    private int comparison_3edges(int x1, int y1, int x2, int y2, int x3, int y3) {
        if (energy[x1][y1] < energy[x2][y2] && energy[x1][y1] < energy[x3][y3]) {
            if (x1 == x2 && x1 == x3) {
                return y1;
            } else {
                return x1;
            }
        }
        if (energy[x2][y2] < energy[x1][y1] && energy[x2][y2] < energy[x3][y3]) {
            if (x1 == x2 && x1 == x3) {
                return y2;
            } else {
                return x2;
            }
        }
        if (energy[x2][y2] < energy[x1][y1] && energy[x2][y2] == energy[x3][y3]) {
            if (x1 == x2 && x1 == x3) {
                return StdRandom.uniform(y2, y3);
            } else {
                return StdRandom.uniform(x2, x3);
            }
        }
        if (energy[x3][y3] < energy[x2][y2] && energy[x3][y3] < energy[x1][y1]) {
            if (x1 == x2 && x1 == x3) {
                return y3;
            } else {
                return x3;
            }
        }
        if (energy[x1][y1] == energy[x2][y2] && energy[x1][y1] == energy[x3][y3]) {
            if (x1 == x2 && x1 == x3) {
                return StdRandom.uniform(y1, y3);
            } else {
                return StdRandom.uniform(x1, x3);
            }
        }
        return -1;
    }



    //  unit testing (optional)
    public static void main(String[] args) {

    }

}
