package ua.karazin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import ua.karazin.model.Cluster;
import ua.karazin.model.Point;
import ua.karazin.service.ClusterCreator;
import ua.karazin.util.PointsLoaderUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {

    private static final String TOTAL_STEP_COUNT_STRING = "Total step count: ";

    private Random random = new Random(System.nanoTime());
    private ClusterCreator clusterCreator = new ClusterCreator();

    private List<Color> colors = new ArrayList<>();
    private List<Point> centers;
    private List<Point> points;
    private List<Cluster> clusters;

    private int numberOfClusters;
    private int totalStepCount;
    private boolean isFinished;

    private double xFactor;
    private double yFactor;


    @FXML
    private BorderPane rootPane;
    @FXML
    private TextField numberOfClustersTextField;
    @FXML
    private Pane dotsPane;
    @FXML
    private Label totalStepCountLabel;
    @FXML
    private TextField stepCountTextField;

    @FXML
    private void onSetDataButtonClick(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
            points = PointsLoaderUtil.loadPoints(file);
            double maxX = points.stream().mapToDouble(Point::getX).max().getAsDouble();
            double maxY = points.stream().mapToDouble(Point::getY).max().getAsDouble();
            double paneMaxX = dotsPane.getWidth();
            double paneMaxY = dotsPane.getHeight();
            xFactor = paneMaxX / maxX;
            yFactor = paneMaxY / maxY;

            isFinished = false;
            totalStepCount = 0;
            totalStepCountLabel.setText(TOTAL_STEP_COUNT_STRING + totalStepCount);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error with loading points").showAndWait();
            return;
        }

        numberOfClusters = 1;
        createColors();
        centers = clusterCreator.createRandomCenters(points, numberOfClusters);
        clusters = clusterCreator.recreateClusters(points, centers);
        drawClusters();
    }

    @FXML
    private void onStartProcessingButtonClick(ActionEvent event) {
        String numberOfClustersText = numberOfClustersTextField.getText();
        try {
            numberOfClusters = Integer.parseInt(numberOfClustersText);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Wrong number value: " + numberOfClustersText).showAndWait();
            return;
        }

        createColors();
        centers = clusterCreator.createRandomCenters(points, numberOfClusters);
        clusters = clusterCreator.recreateClusters(points, centers);
        drawClusters();

        totalStepCount = 1;
        totalStepCountLabel.setText(TOTAL_STEP_COUNT_STRING + totalStepCount);
        isFinished = false;
    }

    @FXML
    private void doClasterizationStepButtonClick(ActionEvent event) {
        if (isFinished) {
            return;
        }

        String stepCountText = stepCountTextField.getText();
        int stepCount;
        try {
            stepCount = Integer.parseInt(stepCountText);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Wrong number value: " + stepCountText).showAndWait();
            return;
        }

        for (int i = 0; i < stepCount; i++) {
            doClusterizationStep();
            totalStepCount++;
            totalStepCountLabel.setText(TOTAL_STEP_COUNT_STRING + totalStepCount);
            if (isFinished) {
                return;
            }
        }
        drawClusters();

    }

    @FXML
    private void finishClusterizationButtonClick(ActionEvent event) {
        if (isFinished) {
            return;
        }

        while (!isFinished) {
            doClusterizationStep();
            totalStepCount++;
            totalStepCountLabel.setText(TOTAL_STEP_COUNT_STRING + totalStepCount);
        }
        drawClusters();
    }

    private void doClusterizationStep() {
        List<Point> localCenters = clusterCreator.recalculateCenters(clusters);
        clusters = clusterCreator.recreateClusters(points, localCenters);

        boolean isAllSame = true;
        for (int i = 0; i < localCenters.size(); i++) {
            if (!localCenters.get(i).equals(centers.get(i))) {
                isAllSame = false;
            }
        }

        if (isAllSame) {
            new Alert(Alert.AlertType.INFORMATION, "Clasterization was finished: "+totalStepCount+" steps")
                    .showAndWait();
            isFinished = true;
        }
        centers = localCenters;
    }


    private void drawClusters() {
        dotsPane.getChildren().clear();

        List<Circle> dots = new ArrayList<>();
        for (int i = 0; i < clusters.size(); i++) {
            Color color = colors.get(i);
            Point center = clusters.get(i).getCenter();
            dots.add(createDot(
                    center.getX() * xFactor,
                    center.getY() * yFactor,
                    5,
                    color
            ));

            for (Point point : clusters.get(i).getPoints()) {
                dots.add(createDot(
                        point.getX() * xFactor,
                        point.getY() * yFactor,
                        1,
                        color
                ));
            }
        }

        dotsPane.getChildren().addAll(dots);
    }

    private Circle createDot(double x, double y, int size, Color color) {
        Circle circle = new Circle(size);
        circle.setFill(color);
        circle.setTranslateX(x);
        circle.setTranslateY(y);
        return circle;
    }

    private void createColors() {
        colors = new ArrayList<>();
        colors.add(Color.GREEN);
        colors.add(Color.INDIGO);
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.CYAN);
        colors.add(Color.MAGENTA);

        for (int i = colors.size(); i < numberOfClusters; i++) {
            Color color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);
            colors.add(color);
        }
    }
}
