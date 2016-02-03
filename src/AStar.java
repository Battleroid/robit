import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
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
        final ToggleGroup shapeGroup = new ToggleGroup();
        Label robotShapeLbl = new Label("Robot Shape:");
        RadioButton rbCircle = new RadioButton("Circle");
        RadioButton rbTriangle = new RadioButton("Triangle");
        rbCircle.setToggleGroup(shapeGroup);
        rbTriangle.setToggleGroup(shapeGroup);
        rbCircle.setSelected(true);

        // add
        hbox.getChildren().addAll(newSceneBtn, replaySceneBtn, robotSizeLbl,
                incRobotSize, decRobotSize, obstacleSizeLbl, incObstacleSize, decObstacleSize,
                robotShapeLbl, rbCircle, rbTriangle
        );

        // a star
        AStarSimple as = new AStarSimple();
        borderPane.setCenter(as);

        // buttons
        newSceneBtn.setOnAction(e -> as.newScenario());
        replaySceneBtn.setOnAction(e -> as.replayScenario());
        incRobotSize.setOnAction(e -> as.incRobotSize(0.25));
        decRobotSize.setOnAction(e -> as.decRobotSize(0.25));
        incObstacleSize.setOnAction(e -> as.incObstacleSize(0.25));
        decObstacleSize.setOnAction(e -> as.decObstacleSize(0.25));
        rbCircle.setOnAction(e -> as.changeShape(0));
        rbTriangle.setOnAction(e -> as.changeShape(1));

        // scene & stage
        final Scene scene = new Scene(borderPane, 720, 480);
        stage.setTitle("A* Pathfinding");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // sample key usage, remove when done testing
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case A: as.robot.moveX(-1); break;
                    case D: as.robot.moveX(1); break;
                    case W: as.robot.moveY(-1); break;
                    case S: as.robot.moveY(1); break;
                    case Q: as.robot.setScale(3); break;
                    case E: as.robot.resetScale(); break;
                }
            }
        });
    }

    static class AStarSimple extends Pane {
        public SNode start;
        public SNode goal;
        public double robotSize = 1;
        public double obstacleSize = 1;
        public Robot robot;
        public int shapeChoice = 0;
        public List<Shape> obstacles = new ArrayList<>();

        // Directions for delta x,y when checking neighbors
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
            robotSize += step > 0 ? step : 1;
            robot.setScale(robotSize);
        }

        public void decRobotSize(double step) {
            robotSize -= step > 0 && (robotSize - step) >= 1 ? step : 0;
            robot.setScale(robotSize);
        }

        // TODO: implement scaling for obstacles
        public void incObstacleSize(double step) {
            this.obstacleSize += step > 0 ? step : 1;
        }

        public void decObstacleSize(double step) {
            this.obstacleSize -= step > 0 && obstacleSize > 2 ? step : 0;
        }

        // Equilateral triangle default
        public Shape EquilateralTriangle() {
            return EquilateralTriangle(20);
        }

        // Equilateral triangle
        public Shape EquilateralTriangle(double scale) {
            if (scale <= 10) scale = 10;
            double[] points = {
                    0.5 * scale, 0,
                    0, 0.866 * scale,
                    scale, 0.866 * scale
            };
            Shape triangle = new Polygon(points);
            triangle.setStroke(Color.BLACK);
            triangle.setStrokeWidth(1);
            triangle.setFill(Color.AQUA);
            return triangle;
        }

        // Basic circle default
        public Shape BasicCircle() {
            return BasicCircle(10);
        }

        // Basic circle
        public Shape BasicCircle(double radius) {
            if (radius <= 10) radius = 10;
            double x = radius;
            double y = radius;
            Circle circle = new Circle(radius, x, y);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(1);
            circle.setFill(Color.AQUA);
            return circle;
        }

        public class SNodeComparator implements Comparator<SNode> {
            @Override
            public int compare(SNode a, SNode b) {
                return Double.compare(a.getF(), b.getF());
            }
        }

        public void changeShape() {
            changeShape(shapeChoice);
        }

        public void changeShape(int choice) {
            Shape before = robot.getShape();
            switch (choice) {
                case 0: robot.setShape(BasicCircle()); break;
                case 1: robot.setShape(EquilateralTriangle()); break;
            }
            getChildren().remove(before);
            shapeChoice = choice;
            robot.setXY(start.getX(), start.getY());
            robot.setScale(robotSize);
            getChildren().add(robot.getShape());
        }

        // constructors and steps for creating pane
        public AStarSimple() {
            newScenario();
        }

        public void spawnRobot() {
            if (robot == null) {
                robot = new Robot(start.getX(), start.getY(), BasicCircle());
            }
            changeShape();
            robot.setXY(start.getX(), start.getY());
        }

        public void spawnObstacles(int n) {
            this.obstacles.clear();
            for (int i = 0; i < n; ++i) {
                // TODO: create and randomly place obstacle within middle 60% of pane
            }
        }

        public void spawnSGSNodes() {
            // constants in integers for nice neat movement
            int w = (int) (getWidth() / 8);
            int h = (int) (getHeight() / 4);

            // start in TL, goal in BR
            start = new SNode(w, h);
            goal = new SNode((int) getWidth() - w, (int) getHeight() - h);
            goal.setColor(Color.DARKGOLDENROD);

            // add to scene
            getChildren().addAll(start.getShape(), goal.getShape());
        }

        public void newScenario() {
            // clear all shapes on the scene
            getChildren().clear();

            // spawn all required entities for a new scenario
            spawnSGSNodes();
            spawnObstacles(3);
            spawnRobot();
        }

        // should restart the same scenario, used to solve again (e.g. replay), or to solve when swapping shapes
        // TODO: Replay the scenario with updated (or not) parameters
        public void replayScenario() {
            // TODO: Replay solve for scenario. Restart solve altogether since this will be for when you change the robot shape
        }

        public void solve() {
            // TODO: Should solve do this in an animation?
        }
    }
}
