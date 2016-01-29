import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.util.*;

public class AStar extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // a-star pane
        AStarPoint AStarPointPane = new AStarPoint();

        // borderpane
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(AStarPointPane);

        // scene & stage
        final Scene scene = new Scene(borderPane, 640, 480);
        stage.setTitle("A Star");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        AStarPointPane.paint();
    }

    // A* just using a single point and simple 'nodes'
    static class AStarPoint extends Pane {
        double dotSize = 2.0;
        double stepSize = 1.0;
        double startX, startY;
        double endX, endY;
        double w, h;
        List<Shape> obstacles = new ArrayList<>();

        private enum Direction {
            LEFT(-1, 0),
            RIGHT(1, 0),
            UP(0, -1),
            DOWN(0, 1);

            private final double dx;
            private final double dy;

            Direction(double dx, double dy) {
                this.dx = dx;
                this.dy = dy;
            }
        }

        private class SortedNodeList {
            private List<Node> list = new ArrayList<>();

            public SortedNodeList() {

            }

            public SortedNodeList(Node... nodes) {
                for (Node n : nodes) {
                    add(n);
                }
            }

            public Node getFirst() {
                return list.get(0);
            }

            public void clear() {
                list.clear();
            }

            public void add(Node node) {
                list.add(node);
                Collections.sort(list);
            }

            public void remove(Node node) {
                list.remove(node);
            }

            public int size() {
                return list.size();
            }

            public boolean contains(Node node) {
                return list.contains(node);
            }
        }


        private Node getNeighborNode(Node parent, Direction d) {
            Node child = new Node(parent.getX() + d.dx, parent.getY() + d.dy);
            Circle childCircle = child.toCircle();
            childCircle.setFill(Color.BLUE);
            getChildren().add(childCircle);

            // key is the obstacles list, not the shit we throw on the stage to check
            boolean collision = false;
            for (Shape enemy : obstacles) {
                if (enemy != childCircle) {
                    Shape intersection = Shape.intersect(enemy, childCircle); // creates intersection shape of the two shapes
                    if (intersection.getBoundsInLocal().getWidth() != -1) { // if the shape has no width, no intersection!
                        collision = true;
                    }
                }
            }

            getChildren().remove(childCircle);
            child.setObstacle(collision);

            return child;
        }

        private LinkedList<Node> getNeighbors(Node parent) {
            LinkedList<Node> neighbors = new LinkedList<>();
            for (Direction d : Direction.values()) {
                Node n = getNeighborNode(parent, d);
                n.setF(endX, endY); // TODO: need to pass end node to stay consistent with using nodes, not raw coords
                neighbors.add(n);
            }
            return neighbors;
        }

        protected void paint() {
            // set constants
            w = getWidth() / 4f;
            h = getHeight() / 4f;

            // start & end points
            startX = w - (w / 2f);
            startY = h - (h / 2f);
            endX = w * 3 + (w / 2f);
            endY = h * 3 + (h / 2f);

            // for convenience and visualization
            // TODO: ask for how many obstacles, randomize the types (say an octagon, convex poly, circle, etc)
            addObstacle();

            // begin find path
            boolean done = false;

            // start, goal nodes
            Node end = new Node(endX, endY);
            end.setF(end);
            Node start = new Node(startX, startY, end);
            Circle startCircle = start.toCircle();
            Circle endCircle = end.toCircle();
            endCircle.setFill(Color.DARKGOLDENROD);
            getChildren().addAll(startCircle, endCircle);

            // lists for searching
            List<Node> closed = new ArrayList<>();
            SortedNodeList open = new SortedNodeList(start);

            while (!done) {
                Node current = open.getFirst();

                // check if goal node
                if (current == end) {
                    done = true;
                    continue;
                }

                // move current node to closed list, we've already visited it
                closed.add(current);
                open.remove(current);

                for (Node n : getNeighbors(current)) {
                    // check
                }

                // TODO: temporary, remove when done
                done = true;
            }

            // TODO: Create final path here after we've finished searching
        }

        // TODO: Lines do not play well with intersection, possibly use Paths instead and convert to shapes?
        private void addObstacle() {
            Rectangle rect = new Rectangle(w, h, w * 2, h * 2);
            obstacles.add(rect);
            getChildren().add(rect);
        }
    }
}
