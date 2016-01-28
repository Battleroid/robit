import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

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
        boolean started = false;

        private double MDist(Point2D a, Point2D b) {
            return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
        }

        protected void paint() {
            // set constants
            w = getWidth() / 4f;
            h = getHeight() / 4f;

            // start & end
            startX = w - (w / 2f);
            startY = h - (h / 2f);
            endX = w * 3 + (w / 2f);
            endY = h * 3 + (h / 2f);

            addObstacle();
            addStartEnd();

            // find path
            boolean done = false;
            Point2D start = new Point2D(startX, startY);
            Point2D end = new Point2D(endX, endY);

            while (!done) {
                // search
            }
        }

        private void addStartEnd() {
            Circle startDot = new Circle(startX, startY, 2, Color.BLACK);
            Circle endDot = new Circle(endX, endY, 2, Color.BLACK);
            getChildren().addAll(startDot, endDot);
        }

        private void addObstacle() {
            Line line = new Line(w * 2, h, w * 2, h * 3);
            line.setStrokeWidth(4);
            getChildren().add(line);
        }
    }
}
