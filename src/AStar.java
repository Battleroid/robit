import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AStar extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        // layouts
        BorderPane borderPane = new BorderPane();
        HBox hbox = new HBox(10);
        hbox.setPadding(new Insets(10));
        hbox.setStyle("-fx-background-color: #ddd;");
        borderPane.setBottom(hbox);

        // controls
        Button newSceneBtn = new Button("New Scenario");
        Button replaySceneBtn = new Button("Replay Scenario");
        Label robotSizeLbl = new Label("Robot Size:");
        Button incRobotSize = new Button("+");
        Button decRobotSize = new Button("-");
        Label obstacleSizeLbl = new Label("Obstacle size:");
        Button incObstacleSize = new Button("+");
        Button decObstacleSize = new Button("-");

        // add
        hbox.getChildren().addAll(newSceneBtn, replaySceneBtn, robotSizeLbl,
                incRobotSize, decRobotSize, obstacleSizeLbl, incObstacleSize, decObstacleSize);

        // a star
        AStarSimple as = new AStarSimple();
        as.setRobot(new Polygon(0, 0, 10, 0, 10, 10, 0, 10));
        borderPane.setCenter(as);

        // buttons
        newSceneBtn.setOnAction(e -> as.newScenario());
        replaySceneBtn.setOnAction(e -> as.replayScenario());
        incRobotSize.setOnAction(e -> as.incRobotSize(2));
        decRobotSize.setOnAction(e -> as.decRobotSize(2));
        incObstacleSize.setOnAction(e -> as.incObstacleSize(2));
        decObstacleSize.setOnAction(e -> as.decObstacleSize(2));

        // scene & stage
        final Scene scene = new Scene(borderPane, 720, 480);
        stage.setTitle("A* Pathfinding");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    static class AStarSimple extends Pane {
        SNode start;
        SNode goal;
        double robotSize = 10;
        double obstacleSize = 10;
        Shape robot;
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

        public void incRobotSize(double step) {
            this.robotSize += step > 0 ? step : 1;
        }

        public void decRobotSize(double step) {
            this.robotSize -= step > 0 && robotSize > 2 ? step : 0;
        }

        public void incObstacleSize(double step) {
            this.obstacleSize += step > 0 ? step : 1;
        }

        public void decObstacleSize(double step) {
            this.obstacleSize -= step > 0 && obstacleSize > 2 ? step : 0;
        }

        public class SNodeComparator implements Comparator<SNode> {
            @Override
            public int compare(SNode a, SNode b) {
                return Double.compare(a.getF(), b.getF());
            }
        }

        public void setRobot(Shape shape) {
            this.robot = shape;
        }

        public void newScenario() {
            // draw new scenario:
            // 1. randomize location of obstacles within middle of the pane
            // 2. establish start/goal points on left and right side
            // 3. immediately try to solve
            // for when you know, it works
        }

        public void replayScenario() {
            // for when I get animation working
        }

        protected void paint() {
            // ?
        }

        public void solve() {
            // do pathfinding
        }
    }
}
