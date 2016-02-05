import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;

import java.util.ArrayList;

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
        resetScale();
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

    // TODO: transforms need to be on origin (center), or in case of triangle possibly corner
    public void setScale(double size) {
        if (size >= 1) {
            Scale scale = new Scale(size, size);
            shape.getTransforms().clear();
            shape.getTransforms().add(scale);
            // TODO: need to adjust for origin EVENTUALLY using middle point of bounding box
        }
    }

    public void resetScale() {
        shape.getTransforms().clear();
    }

    public Shape getShape() {
        return shape;
    }

    public boolean collides(AStar.AStarSimple.Direction direction, ArrayList<Polygon> polygons) {
        // get before coordinates
        double bx = x;
        double by = y;

        // do temporary move in direction
        moveX(direction.dx);
        moveY(direction.dy);

        // check shapes for intersection
        boolean collision = false;
        for (Polygon p : polygons) {
            if (hit(p)) {
                collision = true;
                break;
            }
        }

        // move back
        setXY(bx, by);

        return collision;
    }

    public boolean hit(Shape shape) {
        Shape xs = Shape.intersect(this.shape, shape);
        if (xs.getBoundsInLocal().getWidth() != -1)
            return true; // width is > 0, therefore an intersection has occurred
        else
            return false;
    }
}
