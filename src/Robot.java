import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;

import java.util.ArrayList;

public class Robot {
    private int x, y;
    private Shape shape;

    public Robot(Polygon shape) {
        this.shape = shape;
    }

    public Robot(int x, int y, Shape shape) {
        this.x = x;
        this.y = y;
        this.shape = shape;
        resetScale();
    }

    public void setShape(Shape shape) {
        this.shape = shape;
        resetScale();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
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

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
        shape.setTranslateX(x);
        shape.setTranslateY(y);
    }

    public void setScale(double size) {
        if (size >= 1) {
            Scale scale = new Scale(size, size);
            shape.getTransforms().clear();
            shape.getTransforms().add(scale);
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
        int bx = x;
        int by = y;

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
