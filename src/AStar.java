import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import jdk.internal.org.xml.sax.SAXNotRecognizedException;

import java.lang.reflect.Array;
import java.util.*;

public class AStar extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        int maxW = 720;
        int maxH = 480;

        // layouts
        BorderPane borderPane = new BorderPane();
        borderPane.setMaxHeight(maxH);
        borderPane.setMaxWidth(maxW);
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
                incRobotSize, decRobotSize, obstacleSizeLbl,
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
        rbCircle.setOnAction(e -> as.changeShape(0));
        rbTriangle.setOnAction(e -> as.changeShape(1));

        // scene & stage
        final Scene scene = new Scene(borderPane, maxW, maxH);
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
                    case F: as.collisionCheck(); break;
                    case P: as.solve(); break;
                }
            }
        });
    }

    static class AStarSimple extends Pane {
        public SNode start;
        public SNode goal;
        public double robotSize = 1;
        public Robot robot;
        public int shapeChoice = 0;
        public ArrayList<Polygon> obstacles = new ArrayList<>();

        // Directions for delta x,y when checking neighbors
        public enum Direction {

            TOPLEFT(-5, -5),
            TOPRIGHT(5, -5),
            BOTTOMLEFT(-5, 5),
            BOTTOMRIGHT(5, 5),
            LEFT(-5, 0),
            RIGHT(5, 0),
            UP(0, -5),
            DOWN(0, 5);

            public final int dx;
            public final int dy;

            Direction(int dx, int dy) {
                this.dx = dx;
                this.dy = dy;
            }
        }

        // TODO: When changing size check if you collide, if you do DO NOT increment
        public void incRobotSize(double step) {
            robotSize += step > 0 ? step : 1;
            robot.setScale(robotSize);
        }

        public void decRobotSize(double step) {
            robotSize -= step > 0 && (robotSize - step) >= 1 ? step : 0;
            robot.setScale(robotSize);
        }

        // TODO: transition shape definitions to Robot class
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
        public AStarSimple() {}

        public void collisionCheck() {
            System.out.println(robot.collides(Direction.RIGHT, obstacles));
        }

        public void spawnRobot() {
            if (robot == null) {
                robot = new Robot(start.getX(), start.getY(), BasicCircle());
            }
            changeShape();
            robot.setXY(start.getX(), start.getY());
        }

        public void spawnObstacles(int n) {
            obstacles.clear();

            // constants for threshold
            int cx = (int) getWidth() / 2;
            int cy = (int) getHeight() / 2;
            Random rng = new Random();
            for (int i = 0; i < n; ++i) {
                int cxl = cx / 2; // left
                int cxr = cx + cxl; // right
                int cyt = cy / 2; // top
                int cyb = cy + cyt; // bottom
                int x = cxl + rng.nextInt((cxr - cxl) - 1);
                int y = cyt + rng.nextInt((cyb - cyt) - 1);
                int scale = 5 + rng.nextInt(7);
                Polygon o = new Obstacles.Octagon(x, y, scale);
                obstacles.add(o);
            }

            getChildren().addAll(obstacles);
        }

        public void spawnSGSNodes() {
            // constants in integers for nice neat movement
            int w = (int) (getWidth() / 12);
            int h = (int) (getHeight() / 10);

            // start in TL, goal in BR
            start = new SNode(w, h);
            goal = new SNode((int) getWidth() - w, (int) getHeight() - h);
            goal.setColor(Color.DARKGOLDENROD);

            // add node shapes to scene for visualization
            getChildren().addAll(start.getShape(), goal.getShape());
        }

        public void newScenario() {
            // clear all shapes on the scene
            getChildren().clear();

            // spawn all required entities for a new scenario
            spawnSGSNodes();
            spawnObstacles(5);
            spawnRobot();
        }

        // should restart the same scenario, used to solve again (e.g. replay), or to solve when swapping shapes
        // TODO: Replay the scenario with updated (or not) parameters
        public void replayScenario() {
            // TODO: Replay solve for scenario. Restart solve altogether since this will be for when you change the robot shape
        }

        public void solve() {
            // our open & closed lists
            PriorityQueue<SNode> open = new PriorityQueue<>(new SNodeComparator());
            ArrayList<SNode> closed = new ArrayList<>();

            start.setG(0);
            start.setF(SNode.distanceTo(start, goal));

            open.add(start);

            while (!open.isEmpty()) {
                SNode current = open.poll();
                closed.add(current);
                robot.setXY(current.getX(), current.getY());

                if (current.equals(goal) || robot.getShape().contains(goal.getPoint2D()) || robot.hit(goal.getShape())) {
                    System.out.println("WE FOUND IT");
                    goal.setParent(current);
                    regurgitate(goal);
                    return;
                }

                for (Direction d : Direction.values()) {
                    SNode n = new SNode(robot.getX() + d.dx, robot.getY() + d.dy);
                    if (closed.contains(n) || robot.collides(d, obstacles)) continue;
                    System.out.println("FROM " + current + " TO " + n);

                    double tempG = current.getG() + SNode.distanceTo(current, n);

                    if (!open.contains(n)) {
                        open.add(n);
                    } else if (tempG >= n.getG()) {
                        continue;
                    }

                    n.setParent(current);
                    n.setG(tempG);
                    double tempH = SNode.distanceTo(n, goal) * (1.0 + (1.0 / 1000.0)); // tie breaking?
                    // n.setF(n.getG() + SNode.distanceTo(n, goal));
                    n.setF(n.getG() + tempH);
                    getChildren().add(n.getShape());
                }
            }
        }

        public void regurgitate(SNode n) {
            Polyline line = new Polyline();
            Path path = new Path();
            path.getElements().add(new MoveTo(goal.getX(), goal.getY()));
            path.getElements().add(new LineTo(n.getX(), n.getY()));

            line.getPoints().addAll(
                    Double.valueOf(n.getX()), Double.valueOf(n.getY())
            );
            while (n.getParent() != null) {
                n = n.getParent();
                line.getPoints().addAll(
                        Double.valueOf(n.getX()), Double.valueOf(n.getY())
                );
                path.getElements().add(new LineTo(n.getX(), n.getY()));
            }

            line.setStrokeWidth(5);
            line.setStroke(Color.RED);
            line.toFront();
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            getChildren().add(line);

            final PathTransition pathtransition = new PathTransition();
            pathtransition.setDuration(Duration.seconds(10));
            pathtransition.setDelay(Duration.seconds(0.5));
            pathtransition.setPath(path);
            pathtransition.setNode(robot.getShape());
            robot.getShape().toFront();
            pathtransition.setCycleCount(Timeline.INDEFINITE);
            pathtransition.setAutoReverse(true);
            pathtransition.play();
        }
    }
}
