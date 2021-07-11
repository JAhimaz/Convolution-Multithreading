package com.JAhimaz;

import java.util.Random;

import com.JAhimaz.ioh.InputHandling;

public class Main {

    // Static variables for referencing throughout the program.
    static Random random = new Random(); // For generating Random Values
    static Boolean randomValues = false; // Boolean on whether or not Random Values will be generated

    // Main Method
    public static void main(String[] args) {
        AdminSetup();
    }

    // Setup for the program, end-user enters values.
    public static void AdminSetup(){
        System.out.println("╔════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                            ║");
        System.out.println("║                    >>> convolution Multithreading <<<                      ║");
        System.out.println("║                         >> ADMINISTRATOR SETUP <<                          ║");
        System.out.println("║                                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════╝");

        // Dimension of the initial image ranging from 1x1 to 8192x8192.
        // Larger dimensions may take a longer time to accomplish as only 4 threads are in use.
        System.out.print("\nPlease Enter The Dimension of the Image (Square, so both sides are the same): ");
        int dimensionOfImage = InputHandling.Integer(1, 8192); 
        
        // The number of convolution cycles the end-user would like to go through (Errors will be caught when
        // The program exceeds possible cycle limits).
        System.out.print("\nHow Many convolution Cycles Would You Like?: ");
        int cycles = InputHandling.Integer(1, 50);
        

        // Simple choice on whether or not the user would want incrementing numbers as the values or
        // random numbers from 0-255 (representing an image).
        System.out.print("\nWould you like to use Random Numbers as the Image? (1 = Yes, 2 = No): ");
        int choice = InputHandling.Integer(1, 2);


        // Sets random values to true
        if(choice == 1){ randomValues = true; }


        // Prompt telling the end-user all the information they have entered.
        System.out.println("\nThe Dimension of the Image is set to: " + dimensionOfImage);
        System.out.println("The Number of convolution Cycles: " + cycles);
        System.out.println("Random Values: " + randomValues.toString().toUpperCase() + "\n");

        System.out.println("╔════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                            ║");
        System.out.println("║                  >>> STARTING CONVOLUTION PROCESS <<<                      ║");
        System.out.println("║                                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════╝");

        // Passing in, inputted variables to the convolution
        convolutionProgram(dimensionOfImage, cycles);
    }

    public static void convolutionProgram(int dimensionOfImage, int cycles){
        // The Kernel used for convolution
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
        // Inputs the size of the initial image (A square so obviously both sides)
        System.out.println("Current Input convolution Size: " + inputArray.length + "x" + inputArray[0].length);

        // Creats an Array for the iamge
        Image img = new Image(inputArray, dimension);

        // Counter for the number of convolutions, reason this is outside is because it's accessed inside the catch statement
        int counter = 0;

        // Start the timer to measure the time it takes to go through the convolution process.
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

                // Starts all threads
                q1.start(); q2.start(); q3.start(); q4.start();

                while(q1.isAlive() || q2.isAlive() || q3.isAlive() || q4.isAlive()){}

                System.out.println("════════════════════════════════════════════════════════════════════════════════");
                System.out.println("                Convolutional Cycle " + (counter + 1) + " has been completed");
                System.out.println("════════════════════════════════════════════════════════════════════════════════");

                // Prints the output for each convolution
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
                System.out.println("Current Output convolution Size: " + outputArray.length + "x" + outputArray[0].length);
            }
            System.out.println("\nAll " + counter + "/" + numberOfConv + " convolution Cycles Have Been Completed.");
            
        }catch(NegativeArraySizeException e){
            // This catches if there is any convolutions that cannot be done due to the size of the image being too small
            System.out.println("\nThe Image cannot be Convoluated any further.");
            System.out.println("Ended at Current Cycle: " + counter + "/" + numberOfConv + "\n");
        }

        // Ends the timer, to calculate the time it took.
        long end = System.currentTimeMillis();
        System.out.println("\nTime Taken: " + (end - start) + "ms\n");
    }
}

class Image{                                                    
    // includes methods to get number of rows and columns, pixel value
    private int[][] pixels;                                     
    private int maxRow;
    private int maxCol;

    // Constructor
    Image (int[][] pixels, int sizeNum) {       
        this.pixels = pixels;
        this.maxCol = sizeNum;
        this.maxRow = sizeNum;
    }

    // GETTERS

    public int getMaxRow() { return maxRow; }
    public int getMaxCol() { return maxCol; }
    public int getPixel(int x, int y) { return pixels[x][y]; }

    // SETTERS

    public void setNewSize(int size) { this.maxRow = size; this.maxCol = size; }
    public void setNewPixel(int[][] pixels) { this.pixels = pixels; }

}

class Convolution implements Runnable{ 
    private Image img;                                                                       
    private int startRow, endRow, startCol, endCol;
    private int[][] quadrantOutputArray, kernel;

    // Constructor
    Convolution(Image img, int startRow, int endRow, int startCol, int endCol, int[][] outputArray, int[][] kernel) {
        this.img = img;
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.quadrantOutputArray = outputArray;
        this.kernel = kernel;
    }

    // Performing the Convolution
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