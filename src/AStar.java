import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class AStar extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        int initialW = 720;
        int initialH = 480;

        // controls
        ToolBar tb = new ToolBar();
        Button newSceneBtn = new Button("New Scenario");
        Button solveBtn = new Button("Solve");
        Button tglShape = new Button("Toggle Shape");
        Label robotSizeLbl = new Label("Robot Size:");
        Button incRobotSize = new Button("Inc");
        Button decRobotSize = new Button("Dec");
        Label stepSizeLbl = new Label("Step Size:");
        Button incStepSize = new Button("Inc");
        Button decStepSize = new Button("Dec");

        // add
        tb.getItems().addAll(
                newSceneBtn, solveBtn, tglShape, robotSizeLbl, incRobotSize, decRobotSize,
                stepSizeLbl, incStepSize, decStepSize
        );

        // a star
        StackPane sp = new StackPane();
        sp.setPrefWidth(initialW);
        sp.setPrefHeight(initialH);
        sp.minHeight(initialH);
        sp.minWidth(initialW);
        AStarSimple as = new AStarSimple();
        sp.getChildren().add(as);
        BorderPane tbPane = new BorderPane();
        tbPane.setTop(tb);
        sp.getChildren().add(tbPane);

        // buttons & actions
        newSceneBtn.setOnAction(e -> as.newScenario());
        solveBtn.setOnAction(e -> as.solve());
        incRobotSize.setOnAction(e -> {
            as.incRobotSize(0.25);
            as.cleanup();
        });
        decRobotSize.setOnAction(e -> {
            as.decRobotSize(0.25);
            as.cleanup();
        });
        tglShape.setOnMousePressed(e -> {
            as.toggleShape();
            as.cleanup();
        });
        incStepSize.setOnAction(e -> as.incStepSize(1));
        decStepSize.setOnAction(e -> as.decStepSize(1));

        // scene & stage
        final Scene scene = new Scene(sp, initialW, initialH);
        stage.setTitle("A* Pathfinding");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        // listeners
        scene.widthProperty().addListener(l -> {
            as.cleanup();
        });
        scene.heightProperty().addListener(l -> {
            as.cleanup();
        });

        // sample key usage, remove when done testing
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case P: as.solve(); break;
                }
            }
        });
    }

    static class AStarSimple extends Pane {
        // objs
        public SNode start;
        public SNode goal;
        public Robot robot;

        // misc
        public double robotSize = 1;
        public int shapeChoice = 0;
        public int stepSize = 5;

        // obstacles
        public ArrayList<Polygon> obstacles = new ArrayList<>();

        // Directions for delta x,y when checking neighbors
        public enum Direction {

            TOPLEFT(-1, -1),
            TOPRIGHT(1, -1),
            BOTTOMLEFT(-1, 1),
            BOTTOMRIGHT(1, 1),
            LEFT(-1, 0),
            RIGHT(1, 0),
            UP(0, -1),
            DOWN(0, 1);

            public final int dx;
            public final int dy;

            Direction(int dx, int dy) {
                this.dx = dx;
                this.dy = dy;
            }
        }

        public void incRobotSize(double step) {
            double before = robotSize;
            robotSize += step > 0 ? step : 1;
            robot.setScale(robotSize);

            // depracated; screen is cleared for any change whatsoever, better to be safe than sorry though
            for (Polygon o : obstacles) {
                if (intersecting(robot.getShape(), o)) {
                    robotSize = before;
                    robot.setScale(before);
                }
            }
        }

        public void decRobotSize(double step) {
            robotSize -= step > 0 && (robotSize - step) >= 1 ? step : 0;
            robot.setScale(robotSize);
        }

        public void incStepSize(int step) {
            stepSize += (stepSize + step) <= 10 ? step : 0;
            System.out.println(stepSize);
        }

        public void decStepSize(int step) {
            stepSize -= step > 0 && (stepSize - step) >= 2 ? step : 0;
            System.out.println(stepSize);
        }

        // TODO: transition shape definitions to Robot class
        // Equilateral triangle default
        public Polygon EquilateralTriangle() {
            return EquilateralTriangle(20);
        }

        // Equilateral triangle
        public Polygon EquilateralTriangle(double scale) {
            if (scale <= 10) scale = 10;
            double w = scale;
            double h = scale;
            double[] points = {
                    -0.866 * scale, scale,
                    0, -0.5 * scale,
                    0.866 * scale, scale
            };
            Polygon triangle = new Polygon(points);
            triangle.setFill(Color.VIOLET);
            triangle.toBack();
            return triangle;
        }

        // Basic circle default
        public Shape BasicCircle() {
            return BasicCircle(10);
        }

        // Basic circle
        public Shape BasicCircle(double radius) {
            if (radius <= 10) radius = 10;
            Circle circle = new Circle(radius);
            circle.setFill(Color.VIOLET);
            circle.toBack();
            return circle;
        }

        public class SNodeComparator implements Comparator<SNode> {
            @Override
            public int compare(SNode a, SNode b) {
                return Double.compare(a.getF(), b.getF());
            }
        }

        public void toggleShape() {
            if (shapeChoice == 0) {
                changeShape(1);
            } else {
                changeShape(0);
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

        public boolean intersecting(Shape a, Shape b) {
            Shape i = Shape.intersect(a, b);
            return i.getBoundsInLocal().getHeight() != -1;
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

                // create bounds for obstacles
                int cxl = cx / 2; // left
                int cxr = cx + cxl; // right
                int cyt = cy / 2; // top
                int cyb = cy + cyt; // bottom

                // create coordinates and scale value for polygon
                int x = cxl + rng.nextInt((cxr - cxl) - 1);
                int y = cyt + rng.nextInt((cyb - cyt) - 1);
                int scale = 5 + rng.nextInt(7);
                int deg = rng.nextInt(360);
                Color c = new Color(
                        (Math.random() * 255) / 255.0,
                        (Math.random() * 255) / 255.0,
                        (Math.random() * 255) / 255.0,
                        1.0
                );

                // until I replace it with reflection, this'll work
                Polygon[] pool = new Polygon[] {
                        new Obstacles.Octagon(x, y, scale),
                        new Obstacles.Pentagon(x, y, scale)
                };

                // create polygon, check if touching start/end
                Polygon o = pool[rng.nextInt(pool.length)];
                o.setFill(c);
                o.setRotate(deg);

                // if the obstacle touches anything important we will try this iteration again
                if (intersecting(o, start.getShape())
                        || intersecting(o, goal.getShape())
                        || intersecting(o, robot.getShape())) {
                    i--;
                    continue;
                }

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
            // cleanup all shapes on the scene
            getChildren().clear();

            // spawn all required entities for a new scenario
            spawnSGSNodes();
            spawnRobot();
            spawnObstacles(5);
        }

        public void cleanup() {
            getChildren().clear();
            obstacles.clear();
            spawnSGSNodes();
            spawnRobot();
        }

        public void solve() {
            if (obstacles.size() == 0 || robot == null) {
                return;
            }

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

                if (current.equals(goal) || robot.getShape().contains(goal.getPoint2D())
                        || intersecting(current.getShape(), goal.getShape())
                        || robot.hit(goal.getShape())) {
                    goal.setParent(current);
                    regurgitate(goal);
                    return;
                }

                for (Direction d : Direction.values()) {
                    SNode n = new SNode(robot.getX() + (stepSize * d.dx), robot.getY() + (stepSize * d.dy));
                    System.out.println(n);

                    // create tentative G
                    double tempG = current.getG() + SNode.distanceTo(current, n);

                    // not entirely sure if necessary
//                    if (open.contains(n) && n.getG() < tempG) {
//                        open.remove(n);
//                        closed.add(n);
//                        continue;
//                    }

                    // closed AND tentative G is greater than node G, or skip if obstacle in way
                    if ((closed.contains(n) && tempG >= n.getG()) || robot.collides(d, obstacles)) continue;

                    if (!open.contains(n) || tempG < n.getG()) {
                        n.setParent(current);
                        n.setG(tempG);
                        double tempH = n.getG() + SNode.distanceTo(n, goal);
                        tempH *= (1.0 + (1.0 / 10000.0)); // tie breaking
                        n.setF(tempH);

                        // shape junk
                        Shape nShape = n.getShape();
                        nShape.setFill(Color.LIGHTBLUE);

                        if (!open.contains(n)) {
                            open.add(n);
                            getChildren().add(nShape);
                        }
                    }
                }
            }
        }

        public void regurgitate(SNode n) {
            // Polyline for visualization, path for transition
            Polyline line = new Polyline();
            Path path = new Path();

            // path, move to initial goal point and work backwards
            path.getElements().add(new MoveTo(goal.getX(), goal.getY()));
            path.getElements().add(new LineTo(n.getX(), n.getY()));

            // line, same with path, start with end node and work backwards
            line.getPoints().addAll(
                    Double.valueOf(n.getX()), Double.valueOf(n.getY())
            );

            // continue adding pieces of the path until we run into null parent (starting point)
            while (n.getParent() != null) {
                n = n.getParent();
                line.getPoints().addAll(
                        Double.valueOf(n.getX()), Double.valueOf(n.getY())
                );
                path.getElements().add(new LineTo(n.getX(), n.getY()));
            }

            // spiffy up our line
            line.setStrokeWidth(4);
            line.setStroke(Color.RED);
            line.toFront();
            line.setStrokeLineCap(StrokeLineCap.ROUND);
            getChildren().add(line);

            // create a basic looping transition of the path
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
