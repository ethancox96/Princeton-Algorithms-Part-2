/* Ethan Cox
 * 7/13/18
 * Princeton Algorithms II
 * PA2: Seam Carver
 * SeamCarver.java
 */

import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {
    
    private Picture pic;
    private int width;
    private int height;
    private double[][] energy;
    
    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new java.lang.IllegalArgumentException("argument to constructor is null");
        pic = new Picture(picture);
        width = pic.width();
        height = pic.height();
        energy = new double[width][height];
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                energy[x][y] = energy(x, y);
            }
        }
    }
    
    // current picture
    public Picture picture() {
        Picture p = new Picture(pic);
        return p;
    }
    
    // width of current picture
    public int width() {
        return width;
    }
    
    // height of current picture
    public int height() {
        return height;
    }
    
    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width())
            throw new java.lang.IllegalArgumentException("x is out of range");
        if (y < 0 || y >= height())
            throw new java.lang.IllegalArgumentException("y is out of range");
        if (atXBorder(x) || atYBorder(y))
            return 1000;
        double dX = deltaX(x, y);
        double dY = deltaY(x, y);
        return Math.sqrt(dX + dY);
    }
    
    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int[] horizontalSeam = findVerticalSeam();
        transpose();
        return horizontalSeam;
    }
    
    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] energyTo = new double[width][height];   // will hold smallest energy path to each entry
        int[] edgeTo = new int[height];                    // will hold smallest energy path
        
        // initialiaze energy path array
        for (int j = 0; j < height(); j++) {
            for (int i = 0; i < width(); i++) {
                if (j == 0) {
                    energyTo[i][j] = energy[i][j];   // top row has initial energyTo it equal to its energy
                }
                else
                    energyTo[i][j] = Double.POSITIVE_INFINITY;   // initialize every other value to Infinity
            }
        }
        
        // Calculate smallest energy path to each entry in energyTo array
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                relax(x, y, energyTo, edgeTo);
            }
        }
        
        // find the seam with the smallest total energy
        findSeam(energyTo, edgeTo);
        
        return edgeTo;
    }
    
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null)
            throw new java.lang.IllegalArgumentException("null seam");
        if (seam.length != width())
            throw new java.lang.IllegalArgumentException("invalid seam length");
        if (!validateSeam(seam))
            throw new java.lang.IllegalArgumentException("invalid seam");
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }
    
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null)
            throw new java.lang.IllegalArgumentException("null seam");
        if (seam.length != height())
            throw new java.lang.IllegalArgumentException("invalid seam length");
        if (!validateSeam(seam))
            throw new java.lang.IllegalArgumentException("invalid seam");
        Picture p = new Picture(width - 1, height);
        for (int y = 0; y < height(); y++) {
            int k = 0;
            for (int x = 0; x < width(); x++) {
                if (x != seam[y]) {
                    p.set(k, y, pic.get(x, y));
                    k++;
                }
            }
        }
        this.pic = p;
        width = pic.width();
        height = pic.height();
    }
    
    private boolean atXBorder(int x) {
        if (x == 0)
            return true;
        else if (x == pic.width()-1)
            return true;
        return false;
    }
    
    private boolean atYBorder(int y) {
        if (y == 0)
            return true;
        else if (y == pic.height()-1)
            return true;
        return false;
    }
    
    private double deltaX(int x, int y) {
        Color col1 = pic.get(x-1, y);
        Color col2 = pic.get(x+1, y);
        int b1 = col1.getBlue(), b2 = col2.getBlue();
        int g1 = col1.getGreen(), g2 = col2.getGreen();
        int r1 = col1.getRed(), r2 = col2.getRed();
        int blue = b1 - b2;
        int green = g1 - g2;
        int red = r1 - r2;
        return Math.pow(blue, 2) + Math.pow(green, 2) + Math.pow(red, 2);
    }
    
    private double deltaY(int x, int y) {
        Color col1 = pic.get(x, y-1);
        Color col2 = pic.get(x, y+1);
        int b1 = col1.getBlue(), b2 = col2.getBlue();
        int g1 = col1.getGreen(), g2 = col2.getGreen();
        int r1 = col1.getRed(), r2 = col2.getRed();
        int blue = b1 - b2;
        int green = g1 - g2;
        int red = r1 - r2;
        return Math.pow(blue, 2) + Math.pow(green, 2) + Math.pow(red, 2);
    }
    
    private void relax(int x, int y, double[][] energyTo, int[] edgeTo) {
        //System.out.println("x = " + x + " y = " + y + " energyTo = " + energyTo[x][y]);
        if (width() == 1) {
            for (int i = 0; i < height(); i++)
                edgeTo[i] = 0;
        } else {
            if (x == 0) {
                if (y < height-1) {
                    if (energyTo[x][y+1] >= energyTo[x][y] + energy[x][y+1]) {
                        energyTo[x][y+1] = energyTo[x][y] + energy[x][y+1];
                    }
                    if (energyTo[x+1][y+1] >= energyTo[x][y] + energy[x+1][y+1]) {
                        energyTo[x+1][y+1] = energyTo[x][y] + energy[x+1][y+1];
                    }
                }
            } else if (x == width-1) {
                if (y < height-1) {
                    if (energyTo[x][y+1] >= energyTo[x][y] + energy[x][y+1]) {
                        energyTo[x][y+1] = energyTo[x][y] + energy[x][y+1];
                    }
                    if (energyTo[x-1][y+1] >= energyTo[x][y] + energy[x-1][y+1]) {
                        energyTo[x-1][y+1] = energyTo[x][y] + energy[x-1][y+1];
                    }
                }
            } else {
                if (y < height-1) {
                    if (energyTo[x][y+1] >= energyTo[x][y] + energy[x][y+1]) {
                        energyTo[x][y+1] = energyTo[x][y] + energy[x][y+1];
                    }
                    if (energyTo[x-1][y+1] >= energyTo[x][y] + energy[x-1][y+1]) {
                        energyTo[x-1][y+1] = energyTo[x][y] + energy[x-1][y+1];
                    }
                    if (energyTo[x+1][y+1] >= energyTo[x][y] + energy[x+1][y+1]) {
                        energyTo[x+1][y+1] = energyTo[x][y] + energy[x+1][y+1];
                    }
                }
            }
        }
    }
    
    private void findSeam(double[][] energyTo, int[] edgeTo) {
        if (width() != 1 && height() != 1) {
            double min = energyTo[0][height()-2];
            for (int a = 0; a < width(); a++) {
                if (energyTo[a][height()-2] < min) {
                    min = energyTo[a][height()-2];
                    edgeTo[height()-2] = a;
                    edgeTo[height()-1] = a;
                }
            }
            
            int index;
            for (int i = height()-3; i > 0; i--) {
                index = edgeTo[i+1];
                if (index == 0) {
                    double a = energyTo[index][i];
                    double b = energyTo[index+1][i];
                    if (a < b)
                        edgeTo[i] = index;
                    else
                        edgeTo[i] = index+1;
                } else if (index == width()-1) {
                    double a = energyTo[index][i];
                    double b = energyTo[index-1][i];
                    if (a < b)
                        edgeTo[i] = index;
                    else
                        edgeTo[i] = index-1;
                } else {
                    double a = energyTo[index-1][i];
                    double b = energyTo[index][i];
                    double c = energyTo[index+1][i];
                    if (a < b) {
                        if (a < c) {
                            edgeTo[i] = index-1;
                        } else if (c < a) {
                            edgeTo[i] = index+1;
                        } else if (a == c) {
                            edgeTo[i] = index-1;
                        }
                    } else {
                        if (b < c) {
                            edgeTo[i] = index;
                        } else if (c < b) {
                            edgeTo[i] = index+1;
                        } else if (b == c) {
                            edgeTo[i] = index;
                        }
                    }
                }
            }
            
            edgeTo[0] = edgeTo[1];
        }
    }
    
    private void transpose() {
        int temp = width;
        width = height;
        height = temp;
        Picture transposedPicture = new Picture(width, height);
        double[][] newEnergy = new double[width][height];
        for (int i = 0; i < height; i++)
            for (int k = 0; k < width; k++) {
                transposedPicture.set(k, i, pic.get(i, k));
                newEnergy[k][i] = energy[i][k];
            }
        energy = newEnergy;
        pic = transposedPicture;
    }
    
    private boolean validateSeam(int[] seam) {
        for (int i = 0; i < seam.length-1; i++) {
            if (Math.abs(seam[i] - seam[i+1]) > 1) {
                return false;
            }
        }
        return true;
    }
    
    public static void main(String[] args) {
        
    }
}














