import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class SNode {
    private static final double defaultSize = 2.0;
    private final double x, y;
    private double f, h;
    private static final double g = 10.0;
    private SNode parent = null;
    private boolean obstacle = false;

    public SNode(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public SNode(Point2D pt) {
        this.x = pt.getX();
        this.y = pt.getY();
    }

    public SNode(double x, double y, SNode goal) {
        this.x = x;
        this.y = y;
        setF(goal);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point2D getPoint2D() {
        return new Point2D(x, y);
    }

    public double distanceTo(SNode snode) {
        return Math.abs(x - snode.getX())  + Math.abs(y - snode.getY());
    }

    public double distanceTo(double x, double y) {
        return Math.abs(this.x - x)  + Math.abs(this.y - y);
    }

    public void setF(SNode goal) {
        h = distanceTo(goal.getX(), goal.getY());
        f = g + h;
    }

    public void setF(double x, double y) {
        h = distanceTo(x, y);
        f = g + h;
    }

    public double getF() {
        return f;
    }

    public void setH(SNode goal) {
        this.h = distanceTo(goal.getX(), goal.getY());
    }

    public double getH() {
        return h;
    }

    public double getG() {
        return g;
    }

    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    public boolean isObstacle() {
        return obstacle;
    }

    public Circle getShape() {
        return new Circle(x, y, defaultSize, Color.BLUE);
    }

    public Circle getShape(double size) {
        return new Circle(x, y, size, Color.BLUE);
    }

    public boolean containedBy(Shape p) {
        return p.contains(getPoint2D());
    }

    public SNode getParent() {
        return parent;
    }

    public void setParent(SNode parent) {
        this.parent = parent;
    }
}
