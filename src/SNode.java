import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class SNode implements Comparable<SNode> {
    public static final double d = 10.0;
    public static final double d2 = 14.0;
    public static final double defaultSize = 2.0;
    private final int x, y;
    private double f, h;
    private double g = d;
    private SNode parent = null;
    private boolean obstacle = false;
    private Color color = Color.LIGHTBLUE;
    private Point2D pt;

    public SNode(int x, int y) {
        this.x = x;
        this.y = y;
        this.pt = new Point2D(x, y);
        this.f = this.g = this.h = Double.MAX_VALUE;
    }

    public SNode(Point2D pt) {
        this.x = (int) pt.getX();
        this.y = (int) pt.getY();
        this.pt = new Point2D(x, y);
    }

    public SNode(int x, int y, SNode goal) {
        this.x = x;
        this.y = y;
        this.pt = new Point2D(x, y);
        setF(goal);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point2D getPoint2D() {
        // return new Point2D(x, y);
        return pt;
    }

    static public double distanceTo(SNode f, SNode t) {
        double dx = Math.abs(f.x - t.x);
        double dy = Math.abs(f.y-  t.y);
        return d * (dx + dy) + (d2 - 2 * d) * Math.min(dx, dy);
    }

    public double distanceTo(SNode snode) {
        return Math.abs(x - snode.getX())  + Math.abs(y - snode.getY());
    }

    public double distanceTo(double x, double y) {
        return Math.abs(this.x - x)  + Math.abs(this.y - y);
    }

    /**
     * Sets parent to previous node, calculates heuristic based on goal node. Adds default cost to previous node cost.
     * @param previous node
     * @param goal node
     */
    public void setF(SNode previous, SNode goal) {
        this.parent = previous;
        h = distanceTo(goal);
        g = previous.getG() + d;
        f = g + h;
    }

    public void setF(SNode goal) {
        h = distanceTo(goal.getX(), goal.getY());
        f = g + h;
    }

    public void setF(double x, double y) {
        h = distanceTo(x, y);
        f = g + h;
    }

    public void setF(double f) {
        this.f = f;
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

    public void setG(double g) {
        this.g = g;
    }

    public double getG() {
        return g;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    public boolean isObstacle() {
        return obstacle;
    }

    public Circle getShape() {
        return new Circle(x, y, defaultSize, color);
    }

    public Circle getShape(double size) {
        return new Circle(x, y, size, color);
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

    public boolean sameAs(SNode s) {
        return getX() == s.getX() && getY() == s.getY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SNode)) return false;
        SNode s = (SNode) o;
        return (this.x == s.x) && (this.y == s.y);
    }

    @Override
    public String toString() {
        return new String("SN " + getX() + ":" + getY());
    }

    public void setH(double h) {
        this.h = h;
    }

    @Override
    public int compareTo(SNode o) {
        if (f < o.getF()) {
            return -1;
        } else if (f == o.getF()) {
            return 0;
        } else {
            return 1;
        }
    }
}
