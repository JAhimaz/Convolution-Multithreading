package com.JAhimaz;

import java.util.Random;

import com.JAhimaz.ioh.InputHandling;

public class Main {

    static Random random = new Random();
    static Boolean randomValues = false;

    public static void main(String[] args) {
        AdminSetup();
    }

    public static void AdminSetup(){
        System.out.println("╔════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                            ║");
        System.out.println("║                    >>> Convulation Multithreading <<<                      ║");
        System.out.println("║                         >> ADMINISTRATOR SETUP <<                          ║");
        System.out.println("║                                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════╝");

        System.out.print("\nPlease Enter The Dimension of the Image (Square, so both sides are the same): ");
        int dimensionOfImage = InputHandling.Integer(1, 8192);

        System.out.print("\nHow Many Convulation Cycles Would You Like?: ");
        int cycles = InputHandling.Integer(1, 50);

        System.out.print("\nWould you like to use Random Numbers as the Image? (1 = Yes, 2 = No): ");
        int choice = InputHandling.Integer(1, 2);

        if(choice == 1){ randomValues = true; }

        System.out.println("\nThe Dimension of the Image is set to: " + dimensionOfImage);
        System.out.println("The Number of Convulation Cycles: " + cycles);
        System.out.println("Random Values: " + randomValues.toString().toUpperCase() + "\n");

        System.out.println("╔════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                            ║");
        System.out.println("║                  >>> STARTING CONVULATION PROCESS <<<                      ║");
        System.out.println("║                                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════╝");
        Convulation(dimensionOfImage, cycles);
    }

    public static void Convulation(int dimensionOfImage, int cycles){
        int[][] kernel = {{1,0,1},{0,1,0},{1,0,1}};
        int dimension = dimensionOfImage;                                                 
        int halfRowLength, halfColLength;
        int numberOfConv = cycles;                                               

        int[][] inputArray = new int[dimension][dimension];

        System.out.println("╔══════════════════════════╗");
        System.out.println("║                          ║");
        System.out.println("║  >>> Original Image <<<  ║");
        System.out.println("║                          ║");
        System.out.println("╚══════════════════════════╝");

        System.out.println("\n");
        if(!randomValues){
            // This iterates a value per position of the array. 
            int value = 0;
            for(int row = 0; row < dimension; row++){
                for(int col = 0; col < dimension; col++){
                    inputArray[row][col] = value;
                    System.out.print(inputArray[row][col] + " ");
                    value += 1;
                }
                System.out.println("");
            }
            System.out.println("");
        }else{
            // This generates random values between (0-255, similar to an colour range) for the image rather than iterating the first value. 
            for(int row = 0; row < dimension; row++){
                for(int col = 0; col < dimension; col++){
                    int value = random.nextInt(255 - 0);
                    inputArray[row][col] = value;
                    System.out.print(inputArray[row][col] + " ");
                }
                System.out.println("");
            }
            System.out.println("");
        }
        System.out.println("\n");
        System.out.println("Current Input Convulation Size: " + inputArray.length + "x" + inputArray[0].length);

        // create image array
        Image img = new Image(inputArray, dimension);

        int counter = 0;
        long start = System.currentTimeMillis();
        try {
            for (counter = 0; counter < numberOfConv; counter++){           // repeat convolution process based on how the value specify above

                int convRow = img.getMaxRow() - 2;
                int convCol = img.getMaxCol() - 2;

                int[][] outputArray = new int[convRow][convCol];

                halfRowLength = outputArray.length/2;
                halfColLength = outputArray.length/2;

                // create thread with the convolution object
                // each thread handles 1 side of the image with the required values passed to it
                // such as starting index for row and column and ending index for row and column
                // also passed with the image object and array to store convolutin output
                Thread q1 = new Thread(new Convolution(img, 0, halfRowLength + 1, 0, halfColLength + 1, outputArray, kernel));
                Thread q2 = new Thread(new Convolution(img, halfRowLength, img.getMaxRow() - 1, 0, halfColLength + 1, outputArray, kernel));
                Thread q3 = new Thread(new Convolution(img, 0, halfRowLength + 1, halfColLength, img.getMaxCol() -1, outputArray, kernel));
                Thread q4 = new Thread(new Convolution(img, halfRowLength, img.getMaxRow() - 1, halfColLength, img.getMaxCol() - 1, outputArray, kernel));

                q1.start(); q2.start(); q3.start(); q4.start();

                while(q1.isAlive() || q2.isAlive() || q3.isAlive() || q4.isAlive()){}

                System.out.println("════════════════════════════════════════════════════════════════════════════════");
                System.out.println("                Convolutional Cycle " + (counter + 1) + " has been completed");
                System.out.println("════════════════════════════════════════════════════════════════════════════════");

                System.out.println("\n");
                for (int x = 0; x < convRow; x++){
                    for (int y = 0; y < convCol; y++){
                        System.out.print(outputArray[x][y] + " ");
                    }
                    System.out.println("");
                }
                System.out.println("\n");

                // set new image value, change array values and set new value for row and column
                img.setNewPixel(outputArray);
                img.setNewSize(convRow);
                System.out.println("Current Output Convulation Size: " + outputArray.length + "x" + outputArray[0].length);
            }
            System.out.println("\nAll " + counter + "/" + numberOfConv + " Convulation Cycles Have Been Completed.");
            
        }catch(NegativeArraySizeException e){
            System.out.println("\nThe Image cannot be Convoluated any further.");
            System.out.println("Ended at Current Cycle: " + counter + "/" + numberOfConv + "\n");
        }

        long end = System.currentTimeMillis();
        System.out.println("\nTime Taken: " + (end - start) + "ms\n");
    }
}

class Image{                                                    // Image class that holds the array with all the pixel value
    // includes methods to get number of rows and columns, pixel value
    private int[][] pixels;                                     // and change value in the array and size of the array
    private int maxRow;
    private int maxCol;

    Image (int[][] pixels, int sizeNum) {       // initialize image object
        this.pixels = pixels;
        this.maxCol = sizeNum;
        this.maxRow = sizeNum;
    }

    public int getMaxRow() {                    // get max row number
        return maxRow;
    }

    public int getMaxCol() {                    // get max col number
        return maxCol;
    }

    public int getPixel(int x, int y) {         // get pixel value
        return pixels[x][y];
    }

    public void setNewSize(int size) {          // set new row and col value
        this.maxRow = size;
        this.maxCol = size;
    }

    public void setNewPixel(int[][] pixels) {   // change array value
        this.pixels = pixels;
    }


}

class Convolution implements Runnable{                          // convolution class to conduct the convolution process
    // uses the same convolution code from the appendix in the assignement question
    private Image img;                                          // does the convolution process using values from a part of the image 
    private int[][] imgArray;                                   // writes the value into a reference of the outputArray.
    private int startRow;
    private int endRow;
    private int startCol;
    private int endCol;
    private int[][] quadrantOutputArray;
    private int[][] kernel;

    Convolution(Image img, int startRow, int endRow, int startCol, int endCol, int[][] outputArray, int[][] kernel) {
        this.img = img;
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.quadrantOutputArray = outputArray;
        this.kernel = kernel;
    }

    // Perform convolution (multiply the data from image with the kernel)
    @Override
    public void run(){

        for (int row = startRow + 1; row <= endRow - 1; row++){
            for (int col = startCol + 1; col <= endCol - 1; col++){
                int topLeft = kernel[0][0] * img.getPixel(row-1, col-1);
                int top = kernel[0][1] * img.getPixel(row - 1, col - 1);
                int topRight = kernel[0][2] * img.getPixel(row - 1, col + 1);
                int left = kernel[1][0] * img.getPixel(row, col - 1);
                int centre = kernel[1][1] * img.getPixel(row, col);
                int right = kernel[1][2] * img.getPixel(row, col + 1);
                int bottomLeft = kernel[2][0] * img.getPixel(row + 1, col - 1);
                int bottom = kernel[2][1] * img.getPixel(row+1, col+1);
                int bottomRight = kernel[2][2] * img.getPixel(row + 1, col + 1);
                int outputValue = topLeft + top + topRight + left + centre + right + bottomLeft + bottom + bottomRight;
                quadrantOutputArray[row - 1][col - 1] = outputValue;
            }
        }
    }
}