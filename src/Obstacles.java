import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Created by casey on 2/3/16.
 */
public class Obstacles {
    static class Rectangle extends Polygon {
        public Rectangle(double x, double y, double scale) {
            getPoints().addAll(
                    0d, 0d,
                    10d * scale, 0d,
                    10d * scale, 10d * scale,
                    0d, 10d * scale
            );

            // stroke & fill
            setStrokeWidth(1);
            setStroke(Color.BLACK);
            setFill(Color.LIGHTGRAY);

            // translate
            this.setTranslateX(x);
            this.setTranslateY(y);
        }
    }

    static class Pentagon extends Polygon {
        public Pentagon(double x, double y, double scale) {
            getPoints().addAll(
                    0d, -5d * scale,
                    10d * scale, 5d * scale,
                    7d * scale, 15d * scale,
                    -7d * scale, 15d * scale,
                    -10d * scale, 5d * scale
            );

            // stroke & fill
            setStrokeWidth(1);
            setStroke(Color.BLACK);
            setFill(Color.LIGHTGRAY);

            // set translated location based on x,y coordinates
            this.setTranslateX(x);
            this.setTranslateY(y);
        }
    }
}
