package ua.karazin.util;

import ua.karazin.model.Point;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PointsLoaderUtil {

    public static List<Point> loadPoints(File file) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        String content = new String(encoded, StandardCharsets.UTF_8);

        return Arrays.stream(content.split("\\R++"))
                .map(String::trim)
                .map(line -> line.split("\\h++"))
                .map(PointsLoaderUtil::createPoint)
                .collect(Collectors.toList());
    }

    private static Point createPoint(String[] content) {
        int x = Integer.parseInt(content[0]);
        int y = Integer.parseInt(content[1]);
        return new Point(x, y);
    }
}
