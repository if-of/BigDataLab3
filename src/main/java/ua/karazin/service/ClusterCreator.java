package ua.karazin.service;

import ua.karazin.model.Cluster;
import ua.karazin.model.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ClusterCreator {

    private Random random = new Random(System.nanoTime());


    public List<Point> createRandomCenters(List<Point> points, int clusterCount) {
        double maxX = points.stream().mapToDouble(Point::getX).max().getAsDouble();
        double maxY = points.stream().mapToDouble(Point::getY).max().getAsDouble();

        ArrayList<Point> centers = new ArrayList<>();
        for (int i = 0; i < clusterCount; i++) {
            Point center = new Point(
                    random.nextInt((int) maxX + 1),
                    random.nextInt((int) maxY + 1)
            );
            centers.add(center);
        }
        return centers;
    }

    public List<Cluster> recreateClusters(List<Point> points, List<Point> centers) {
        List<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < centers.size(); i++) {
            clusters.add(new Cluster(centers.get(i), new ArrayList<>()));
        }

        for (int i = 0; i < points.size(); i++) {
            Cluster minCluster = null;
            double minLength = Double.MAX_VALUE;
            for (int j = 0; j < clusters.size(); j++) {
                double length = calculateLength(
                        points.get(i).getX(),
                        points.get(i).getY(),
                        clusters.get(j).getCenter().getX(),
                        clusters.get(j).getCenter().getY()
                );
                if (length < minLength) {
                    minLength = length;
                    minCluster = clusters.get(j);
                }
            }
            minCluster.addPoint(points.get(i));
        }
        return clusters;
    }

    public List<Point> recalculateCenters(List<Cluster> clusters) {
        return clusters.stream()
                .map(Cluster::getPoints)
                .map(this::recalculateCenter)
                .collect(Collectors.toList());
    }

    private Point recalculateCenter(List<Point> points) {
        double xSum = 0;
        double ySum = 0;
        for (Point point : points) {
            xSum += point.getX();
            ySum += point.getY();
        }
        double x = xSum / points.size();
        double y = ySum / points.size();
        return new Point(x, y);
    }

    private double calculateLength(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}
