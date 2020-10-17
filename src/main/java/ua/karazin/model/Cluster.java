package ua.karazin.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Cluster {

    private Point center;
    private List<Point> points;

    public void addPoint(Point point) {
        if (points == null) {
            points = new ArrayList<>();
        }
        points.add(point);
    }
}
