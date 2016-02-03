package robot;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class Robot {
    private double x, y;
    private final Polygon polygon;

    public Robot(Polygon polygon) {
        this.polygon = polygon;
    }

    public Robot(double x, double y, Polygon polygon) {
        this.x = x;
        this.y = y;
        this.polygon = polygon;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void moveX(double dx) {
        this.x += dx;
        polygon.setTranslateX(x);
    }

    public void moveY(double dy) {
        this.y += dy;
        polygon.setTranslateY(y);
    }

    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
        polygon.setTranslateX(x);
        polygon.setTranslateY(y);
    }

    public void setScale(double size) {
        if (size >= 1) {
            polygon.setScaleX(size);
            polygon.setScaleY(size);
        }
    }

    public void resetScale() {
        polygon.setScaleX(1);
        polygon.setScaleY(1);
    }

    public boolean hit(Shape shape) {
        Shape xs = Shape.intersect(polygon, shape);
        if (xs.getBoundsInLocal().getWidth() != -1)
            return true; // width is > 0, therefore an intersection has occurred
        else
            return false;
    }
}
