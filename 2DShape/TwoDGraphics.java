import java.io.File;
import java.lang.Math.*;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class TwoDGraphics {

    public static Line[] datalines;
    public static int num;

    public TwoDGraphics() {
        datalines = new Line[1000]; // assuming a maximum of 1000 lines
        num = 0;
    }

    public static void inputLines(String filename) {
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\s+");
                double x1 = Double.parseDouble(parts[0]);
                double y1 = Double.parseDouble(parts[1]);
                double x2 = Double.parseDouble(parts[2]);
                double y2 = Double.parseDouble(parts[3]);
                Line lineObj = new Line(x1, y1, x2, y2);
                datalines[num++] = lineObj;
            }
            scanner.close();
            System.out.println("File found: " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
        }
    }


    public static void applyTransformation(Matrix matrix) {
        for (int i = 0; i < num; i++) {
            datalines[i].transform(matrix);
        }
    }

    public static void displayPixels(Line[] datalines, int num) {
        JFrame frame = new JFrame("TwoDGraphics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel() {
                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    for (int i = 0; i < num; i++) {
                        Line line = datalines[i];
                        g.setColor(Color.GREEN);
                        g.drawLine((int) Math.round(line.getX1()), (int) Math.round(line.getY1()), (int) Math.round(line.getX2()), (int) Math.round(line.getY2()));
                    }
                }
            };

        frame.add(panel);
        frame.setSize(1000, 1000);
        frame.setVisible(true);
    }


    public static void outputLines(String filename) {
        try {
            FileWriter fileWriter = new FileWriter(filename);
            for (int i = 0; i < num; i++) {
                fileWriter.write(datalines[i].toString() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + filename);
        }
    }

    public static void basicTranslate(double tx, double ty) {
        Matrix translationMatrix = new Matrix(new double[][]{
            {1, 0, tx}, //tx
            {0, 1, -ty}, //ty
            {0, 0, 1}
        });
        applyTransformation(translationMatrix);
    }

    public static void basicScale(double sx, double sy) {
        Matrix scalingMatrix = new Matrix(new double[][]{
            {sx, 0, 0},
            {0, sy, 0},
            {0, 0, 1}
        });
        applyTransformation(scalingMatrix);
    }

    public static void basicRotate(double angle) {
        double radians = Math.toRadians(angle);
        Matrix rotationMatrix = new Matrix(new double[][]{
            {Math.cos(radians), -Math.sin(radians), 0},
            {Math.sin(radians), Math.cos(radians), 0},
            {0, 0, 1}
        });
        applyTransformation(rotationMatrix);
    }

    public static void scale(double sx, double sy, double cx, double cy) {
        Matrix translation1 = new Matrix(new double[][]{
            {1, 0, -cx}, //-cx
            {0, 1, cy}, //-cy
            {0, 0, 1}
        });
        Matrix scalingMatrix = new Matrix(new double[][]{
            {sx, 0, 0},
            {0, sy, 0},
            {0, 0, 1}
        });
        Matrix translation2 = new Matrix(new double[][]{
            {1, 0, cx}, //cx
            {0, 1, -cy}, //cy
            {0, 0, 1}
        });
        Matrix transformationMatrix = translation2.multiply(scalingMatrix).multiply(translation1);
        applyTransformation(transformationMatrix);
    }

    public static void rotate(double angle, double cx, double cy) {
        double radians = Math.toRadians(angle);
        Matrix translation1 = new Matrix(new double[][]{
            {1, 0, -cx}, //-cx
            {0, 1, cy}, //-cy
            {0, 0, 1}
        });
        Matrix rotationMatrix = new Matrix(new double[][]{
            {Math.cos(radians), -Math.sin(radians), 0},
            {Math.sin(radians), Math.cos(radians), 0},
            {0, 0, 1}
        });
        Matrix translation2 = new Matrix(new double[][]{
            {1, 0, cx}, //cx
            {0, 1, -cy}, //cy
            {0, 0, 1}
        });
        Matrix transformationMatrix = translation2.multiply(rotationMatrix).multiply(translation1);
        applyTransformation(transformationMatrix);
    }


    public static class Matrix{
        private final int rows;
        private final int cols;
        private final double[][] data;

        public Matrix(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
            this.data = new double[rows][cols];
        }

        public Matrix(double[][] data) {
            this.rows = data.length;
            this.cols = data[0].length;
            this.data = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    this.data[i][j] = data[i][j];
                }
            }
        }

        public Matrix multiply(Matrix other) {
            if (this.cols != other.rows) {
                throw new IllegalArgumentException("Matrix dimensions are not compatible for multiplication.");
            }
            Matrix result = new Matrix(this.rows, other.cols);
            for (int i = 0; i < result.rows; i++) {
                for (int j = 0; j < result.cols; j++) {
                    double sum = 0;
                    for (int k = 0; k < this.cols; k++) {
                        sum += this.data[i][k] * other.data[k][j];
                    }
                    result.data[i][j] = sum;
                }
            }
            return result;
        }

        public int getRows() {
            return rows;
        }

        public int getCols() {
            return cols;
        }

        public double get(int i, int j) {
            return data[i][j];
        }

        public double[][] getData() {
            double[][] result = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    result[i][j] = data[i][j];
                }
            }
            return result;
        }

        public void set(int i, int j, double value) {
            data[i][j] = value;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    sb.append(data[i][j]);
                    sb.append(" ");
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    } // matrix class

    public static class Line {
        private double x1;
        private double y1;
        private double x2;
        private double y2;

        public Line(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public double getX1() {
            return x1;
        }

        public double getY1() {
            return y1;
        }

        public double getX2() {
            return x2;
        }

        public double getY2() {
            return y2;
        }

        public void transform(Matrix matrix) {
            double[][] data = matrix.getData();
            double newX1 = data[0][0] * x1 + data[0][1] * y1 + data[0][2];
            double newY1 = data[1][0] * x1 + data[1][1] * y1 + data[1][2];
            double newX2 = data[0][0] * x2 + data[0][1] * y2 + data[0][2];
            double newY2 = data[1][0] * x2 + data[1][1] * y2 + data[1][2];
            x1 = newX1;
            y1 = newY1;
            x2 = newX2;
            y2 = newY2;
        }

        @Override
        public String toString() {
            return x1 + " " + y1 + " " + x2 + " " + y2;
        }
    } // Line class

    public static void main(String[] args) {
        TwoDGraphics twoDGraphics = new TwoDGraphics();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Input lines from file");
            System.out.println("2. Apply Transformation");
            System.out.println("3. Display Pixels");
            System.out.println("4. Output lines to file");
            System.out.println("5. Basic Translate");
            System.out.println("6. Basic Scale");
            System.out.println("7. Basic Rotate");
            System.out.println("8. Scale");
            System.out.println("9. Rotate");
            System.out.println("10. Exit");
            int choice = sc.nextInt();
            switch (choice) {
            case 1:
                System.out.print("Please insert file: ");
                String file = sc.next();
                inputLines(file);
                break;
            case 2:
                System.out.println("Enter transformation matrix: ");
                System.out.println("Example Identity Matrix:\n{1, 0, 0}\n{0, 1, 0}\n{0, 0, 1}");
                System.out.println("Example:\n{1, 0, 100}\n{0, 1, 100}\n{0, 0, 1}");
                System.out.println("Please input 9 numbers (from top left to bottom right:)");
                double m1 = sc.nextDouble();
                double m2 = sc.nextDouble();
                double m3 = sc.nextDouble();
                double m4 = sc.nextDouble();
                double m5 = sc.nextDouble();
                double m6 = sc.nextDouble();
                double m7 = sc.nextDouble();
                double m8 = sc.nextDouble();
                double m9 = sc.nextDouble();

                Matrix inputMatrix = new Matrix(new double[][]{
                    {m1, m2, m3},
                    {m4, m5, m6},
                    {m7, m8, m9}
                });
                applyTransformation(inputMatrix);
                break;
            case 3:
                System.out.println("Picture displayed");
                displayPixels(datalines, num);
                break;
            case 4:
                System.out.print("Enter file name: ");
                String outputFilename = sc.next();
                outputLines(outputFilename);
                System.out.println(num + " lines written to file.");
                break;
            case 5:
                System.out.println("Basic Translate:");
                System.out.print("Enter Tx: ");
                double Tx = sc.nextDouble();
                System.out.print("Enter Ty: ");
                double Ty = sc.nextDouble();
                basicTranslate(Tx, Ty);
                break;
            case 6:
                System.out.println("Basic Scale:");
                System.out.print("Enter Sx: ");
                double Sx = sc.nextDouble();
                System.out.print("Enter Sy: ");
                double Sy = sc.nextDouble();
                basicScale(Sx, Sy);
                break;
            case 7:
                System.out.println("Basic Rotate:");
                System.out.print("Enter angle (in degrees): ");
                double angle = sc.nextDouble();
                basicRotate(angle);
                break;
            case 8:
                System.out.println("Scale: ");
                System.out.print("Enter Sx: ");
                double Sx2 = sc.nextDouble();
                System.out.print("Enter Sy: ");
                double Sy2 = sc.nextDouble();
                System.out.print("Enter Cx: ");
                int Cx = sc.nextInt();
                System.out.print("Enter Cy: ");
                int Cy = sc.nextInt();
                scale(Sx2, Sy2, Cx, Cy);
                break;
            case 9:
                System.out.println("Rotate:");
                System.out.print("Enter angle (in degrees): ");
                double rotate = sc.nextDouble();
                System.out.print("Enter a Cx: ");
                int Cx1 = sc.nextInt();
                System.out.print("Enter a Cy: ");
                int Cy1 = sc.nextInt();
                rotate(rotate, Cx1, Cy1);
                break;
            case 10:
                System.out.println("Exiting...");
                System.exit(0);
            default:
                System.out.println("Invalid choice. Try again.");
            }
        }
    }
} // class 2DGraphics
