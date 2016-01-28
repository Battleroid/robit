import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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
        double startX, startY;
        double endX, endY;
        double w, h;
        List<Line> obstacles = new ArrayList<>();

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

        protected void paint() {
            // set constants
            w = getWidth() / 4f;
            h = getHeight() / 4f;

            // start & end points
            startX = w - (w / 2f);
            startY = h - (h / 2f);
            endX = w * 3 + (w / 2f);
            endY = h * 3 + (h / 2f);

            // for visualization
            addObstacle();
            addStartEnd();

            // find path
            boolean done = false;
            Node start = new Node(startX, startY);
            Node end = new Node(endX, endY);
            List<Node> closed = new ArrayList<>();
            SortedNodeList open = new SortedNodeList();
            while (!done) {
                // for now since I haven't gotten to this yet
                done = true;
            }
        }

        private void getNeighbors(Node node) {
            // do thing
        }

        private void addStartEnd() {
            Circle startDot = new Circle(startX, startY, 2, Color.BLACK);
            Circle endDot = new Circle(endX, endY, 2, Color.BLACK);
            getChildren().addAll(startDot, endDot);
        }

        private void addObstacle() {
            Line line = new Line(w * 2, h, w * 2, h * 3);
            line.setStrokeWidth(4);
            obstacles.add(line);
            getChildren().add(line);
        }
    }
}
