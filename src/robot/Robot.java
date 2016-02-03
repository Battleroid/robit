package robot;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class Robot {
    private double x, y;
    private Shape shape;

    public Robot(Polygon shape) {
        this.shape = shape;
    }

    public Robot(double x, double y, Shape shape) {
        this.x = x;
        this.y = y;
        this.shape = shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
        resetScale();
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void moveX(double dx) {
        this.x += dx;
        shape.setTranslateX(x);
    }

    public void moveY(double dy) {
        this.y += dy;
        shape.setTranslateY(y);
    }

    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
        shape.setTranslateX(x);
        shape.setTranslateY(y);
    }

    public void setScale(double size) {
        if (size >= 1) {
            shape.setScaleX(size);
            shape.setScaleY(size);
        }
    }

    public void resetScale() {
        shape.setScaleX(1);
        shape.setScaleY(1);
    }

    public Shape getShape() {
        return shape;
    }

    public boolean hit(Shape shape) {
        Shape xs = Shape.intersect(this.shape, shape);
        if (xs.getBoundsInLocal().getWidth() != -1)
            return true; // width is > 0, therefore an intersection has occurred
        else
            return false;
    }
}
