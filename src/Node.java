import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class Node implements Comparable<Node> {
    private Node previous = null;
    private final double x, y;
    private double f, h;
    private double g = 10.0;
    private boolean blocking = false;

    public Node(double x, double y) {
        this.x = x;
        this.y = y;
        this.f = this.h = Double.MAX_VALUE;
    }

    public double getDistance(Node node) {
        return Math.abs(x - node.x) + Math.abs(y - node.y);
    }

    public double getF() {
        return f;
    }

    public void setF(Node node) {
        h = getDistance(node);
        f = g + h;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node node) {
        previous = node;
    }

    public boolean isObstacle() {
        return blocking;
    }

    public void toggleObstacle() {
        blocking = !blocking;
    }

    public Point2D toPoint2D() {
        return new Point2D(x, y);
    }

    public boolean containedBy(Polygon poly) {
        return poly.contains(toPoint2D());
    }

    @Override
    public int compareTo(Node node) {
        if (f < node.getF()) {
            return -1;
        } else if (f == node.getF()) {
            return 0;
        } else {
            return 1;
        }
    }
}
