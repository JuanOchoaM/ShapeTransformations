import java.awt.*;
import javax.swing.*;
import java.util.Scanner;


public class Shape3D {


    private double screenSize = 30;
    private double distance = 60;
    private double[][] points;
    private int[][] edges;
    private double[] viewpoint = {6, 8, 7.5};
    private double[] viewaxis = {0, 0, -1};
    private double[] xe = {1, 0, 0};
    private double[][] screen;// = new double[2][2];
    private int N;// = 500;
    private int[][] screenPoints;// = new int[8][2];


    public Shape3D() { // default constructor of cube
    this.points = new double[][] {
        {-1, 1, -1}, {1, 1, -1}, {1, -1, -1}, {-1, -1, -1},
        {-1, 1, 1}, {1, 1, 1}, {1, -1, 1}, {-1, -1, 1}
    };
    this.edges = new int[][] {
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {4, 5}, {5, 6}, {6, 7}, {7, 4},
        {0, 4}, {1, 5}, {2, 6}, {3, 7}
    };
    this.N = 500;
    this.screenPoints = new int[points.length][2];
    project();
}




    public Shape3D(double[][] points, int[][] edges) { // 3D shape constructor
        this.points = points;
        this.edges = edges;
        this.N = 500;
        this.screenPoints = new int[points.length][2];
        project();
    }






    public void setViewpoint(double x, double y, double z) {
        viewpoint[0] = x;
        viewpoint[1] = y;
        viewpoint[2] = z;
        project();
    }


    public void setScreenSize(double screenSize) {
        this.screenSize = screenSize;
        project();
    }


    public void setDistance(double distance) {
        this.distance = distance;
        project();
    }


    public void setNumPixels(int numPixels) {
        this.N = numPixels;
        screenPoints = new int[8][2];
        project();
    }


    public int[][] getScreenPoints() {
        return screenPoints;
    }


    public int[][] getEdges() {
        return edges;
    }




    public void translate(double tx, double ty, double tz) {
        for (int i = 0; i < points.length; i++) {
            points[i][0] += tx;
            points[i][1] += ty;
            points[i][2] += tz;
        }
        project();
    }


    public void scale(double sx, double sy, double sz, double cx, double cy, double cz) {
        for (int i = 0; i < points.length; i++) {
            points[i][0] = cx + sx * (points[i][0] - cx);
            points[i][1] = cy + sy * (points[i][1] - cy);
            points[i][2] = cz + sz * (points[i][2] - cz);
        }
        project();
    }


    public void rotateX(double angle, double cx, double cy, double cz) {
        double rad = Math.toRadians(angle);
        double cosAngle = Math.cos(rad);
        double sinAngle = Math.sin(rad);


        for (int i = 0; i < points.length; i++) {
            double[] p = points[i];
            double dx = p[0] - cx;
            double dy = p[1] - cy;
            double dz = p[2] - cz;


            points[i][1] = cy + (dy * cosAngle - dz * sinAngle);
            points[i][2] = cz + (dy * sinAngle + dz * cosAngle);
        }
        project();
    }


    public void rotateY(double angle, double cx, double cy, double cz) {
        double rad = Math.toRadians(angle);
        double cosAngle = Math.cos(rad);
        double sinAngle = Math.sin(rad);


        for (int i = 0; i < points.length; i++) {
            double[] p = points[i];
            double dx = p[0] - cx;
            double dy = p[1] - cy;
            double dz = p[2] - cz;


            points[i][0] = cx + (dx * cosAngle + dz * sinAngle);
            points[i][2] = cz + (-dx * sinAngle + dz * cosAngle);
        }
        project();
    }


    public void rotateZ(double angle, double cx, double cy, double cz) {
        double rad = Math.toRadians(angle);
        double cosAngle = Math.cos(rad);
        double sinAngle = Math.sin(rad);


        for (int i = 0; i < points.length; i++) {
            double[] p = points[i];
            double dx = p[0] - cx;
            double dy = p[1] - cy;
            double dz = p[2] - cz;


            points[i][0] = cx + (dx * cosAngle - dy * sinAngle);
            points[i][1] = cy + (dx * sinAngle + dy * cosAngle);
        }
        project();
    }




    private void project() {
        double[] viewup = {0, 1, 0}; // Y-axis as a default view-up vector


        // Calculate the camera coordinate system (Xe, Ye, Ze)
        double[] ze = normalize(subtract(viewpoint, new double[]{0, 0, 0}));
        double[] xe = normalize(cross(viewup, ze));
        double[] ye = cross(ze, xe);


        double d = 60; // viewing distance (60 cm)
        double s = 30; // screen size (30 cm)


        for (int i = 0; i < points.length; i++) {
            double[] p = subtract(points[i], viewpoint);
            double x = dot(p, xe);
            double y = dot(p, ye);
            double z = dot(p, ze);


            // Perspective projection
            double xp = (distance * x) / (z + distance);
            double yp = (distance * y) / (z + distance);


            // Convert to screen coordinates
            screenPoints[i][0] = (int) ((xp / screenSize + 0.5) * N);
            screenPoints[i][1] = (int) ((yp / screenSize + 0.5) * N);
        }
    }


    private double[] subtract(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] - b[i];
        }
        return result;
    }


    private double[] cross(double[] a, double[] b) {
        return new double[]{
            a[1] * b[2] - a[2] * b[1],
            a[2] * b[0] - a[0] * b[2],
            a[0] * b[1] - a[1] * b[0]
        };
    }


    private double dot(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }


    private double[] normalize(double[] v) {
        double norm = 0;
        for (double d : v) {
            norm += d * d;
        }
        norm = Math.sqrt(norm);


        double[] result = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            result[i] = v[i] / norm;
        }
        return result;
    }
} // Cube


class Shape3DApp {
    public static void main(String[] args) {
//        Shape3D cube = new Shape3D();
/*
        // Apply transformations
        cube.translate(2, 0, 0);
        cube.scale(1.5, 1.5, 1.5, 0, 0, 0);
        cube.rotateX(45, 0, 0, 0);
        cube.rotateY(30, 0, 0, 0);
        cube.rotateZ(15, 0, 0, 0);
*/
        double[][] tetrahedronPoints = {
            {0, 1, 0}, {-1, -1, 1}, {1, -1, 1}, {0, -1, -1}
        };


        int[][] tetrahedronEdges = {
            {0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3}, {2, 3}
        };
        Shape3D cube = new Shape3D(tetrahedronPoints, tetrahedronEdges);
        cube.translate(2, 0, 0);
        cube.scale(1.5, 1.5, 1.5, 0, 0, 0);
        cube.rotateX(45, 0, 0, 0);
        cube.rotateY(30, 0, 0, 0);
        cube.rotateZ(15, 0, 0, 0);




        Scanner scanner = new Scanner(System.in);
        boolean exit = false;


        while (!exit) {
            System.out.println("\nMenu:");
            System.out.println("1. Change viewpoint");
            System.out.println("2. Change screen size");
            System.out.println("3. Change distance from the screen");
            System.out.println("4. Change number of pixels");
            System.out.println("5. Apply transformations");
            System.out.println("6. Display projected 3D shape");
            System.out.println("0. Exit");


            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();


            switch (choice) {
            case 1:
                System.out.print("Enter viewpoint (x y z): ");
                double vx = scanner.nextDouble();
                double vy = scanner.nextDouble();
                double vz = scanner.nextDouble();
                cube.setViewpoint(vx, vy, vz);
                break;


            case 2:
                System.out.print("Enter screen size: ");
                double screenSize = scanner.nextDouble();
                cube.setScreenSize(screenSize);
                break;


            case 3:
                System.out.print("Enter distance from the screen: ");
                double distance = scanner.nextDouble();
                cube.setDistance(distance);
                break;


            case 4:
                System.out.print("Enter number of pixels: ");
                int numPixels = scanner.nextInt();
                cube.setNumPixels(numPixels);
                break;


            case 5:
                cube.translate(2, 0, 0);
                cube.scale(3.0, 3.0, 3.0, 0, 0, 0);
                cube.rotateX(45, 0, 0, 0);
                cube.rotateY(30, 0, 0, 0);
                cube.rotateZ(15, 0, 0, 0);
                break;


            case 6:
                JFrame frame = new JFrame("3DShape Projection");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 600);
                frame.add(new Shape3DPanel(cube));
                frame.setVisible(true);
                break;


            case 0:
                exit = true;
                break;


            default:
                System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
}


class Shape3DPanel extends JPanel {
    private final Shape3D cube;


    public Shape3DPanel(Shape3D cube) {
        this.cube = cube;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        // Draw the cube's edges
        int[][] screenPoints = cube.getScreenPoints();
        int[][] edges = cube.getEdges();
        for (int[] edge : edges) {
            int x1 = screenPoints[edge[0]][0];
            int y1 = screenPoints[edge[0]][1];
            int x2 = screenPoints[edge[1]][0];
            int y2 = screenPoints[edge[1]][1];
            g.drawLine(x1, y1, x2, y2);
        }
    }
}