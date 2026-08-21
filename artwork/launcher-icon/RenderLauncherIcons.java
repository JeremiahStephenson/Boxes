import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Regenerates BitShape's legacy and adaptive launcher icon PNGs.
 * Run with: java -Djava.awt.headless=true artwork/launcher-icon/RenderLauncherIcons.java
 */
public final class RenderLauncherIcons {
    private static final int SUPERSAMPLE = 4;
    private static final float DESIGN_SIZE = 108f;
    private static final Palette CURRENT = palette(
            "31D8E8", "08BBD3",
            "7890FF", "4967F2",
            "FF6976", "F34257",
            "FFD55A", "FFBA32");

    private RenderLauncherIcons() {}

    public static void main(String[] args) throws IOException {
        Path projectRoot = Path.of("").toAbsolutePath();
        if (args.length > 0 && args[0].equals("--palette-previews")) {
            writePalettePreviews(projectRoot.resolve("artwork/launcher-icon/previews"));
            return;
        }

        Path res = projectRoot.resolve("app/src/main/res");
        write(
                projectRoot.resolve("artwork/launcher-icon/play-store-icon.png"),
                512,
                Layer.PLAY_STORE,
                CURRENT);
        Map<String, Integer> legacySizes = Map.of(
                "mdpi", 48,
                "hdpi", 72,
                "xhdpi", 96,
                "xxhdpi", 144,
                "xxxhdpi", 192);
        Map<String, Integer> adaptiveSizes = Map.of(
                "mdpi", 108,
                "hdpi", 162,
                "xhdpi", 216,
                "xxhdpi", 324,
                "xxxhdpi", 432);

        for (String density : legacySizes.keySet()) {
            Path output = res.resolve("mipmap-" + density);
            Files.createDirectories(output);
            write(output.resolve("ic_launcher.png"), legacySizes.get(density), Layer.LEGACY, CURRENT);
            write(output.resolve("ic_launcher_background.png"), adaptiveSizes.get(density), Layer.BACKGROUND, CURRENT);
            write(output.resolve("ic_launcher_foreground.png"), adaptiveSizes.get(density), Layer.FOREGROUND, CURRENT);
            write(output.resolve("ic_launcher_monochrome.png"), adaptiveSizes.get(density), Layer.MONOCHROME, CURRENT);
        }
    }

    private static void writePalettePreviews(Path output) throws IOException {
        Files.createDirectories(output);
        write(
                output.resolve("palette-1-azure.png"),
                432,
                Layer.LEGACY,
                palette(
                        "31D8E8", "08BBD3",
                        "7890FF", "4967F2",
                        "FF6976", "F34257",
                        "FFD55A", "FFBA32"));
        write(
                output.resolve("palette-2-fresh.png"),
                432,
                Layer.LEGACY,
                palette(
                        "38D6C5", "14B8A6",
                        "78DE8D", "3DBE6C",
                        "FF8A4C", "F26332",
                        "FFD45A", "FFBA32"));
        write(
                output.resolve("palette-3-warm.png"),
                432,
                Layer.LEGACY,
                palette(
                        "44C7F4", "159DD1",
                        "F06ACF", "C93DAA",
                        "FF835C", "F0523E",
                        "FFE167", "F9BE34"));
    }

    private static void write(Path output, int size, Layer layer, Palette palette) throws IOException {
        int renderSize = size * SUPERSAMPLE;
        BufferedImage source = new BufferedImage(renderSize, renderSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = source.createGraphics();
        applyQuality(graphics);
        graphics.scale(renderSize / DESIGN_SIZE, renderSize / DESIGN_SIZE);
        draw(graphics, layer, palette);
        graphics.dispose();

        BufferedImage result = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D downsample = result.createGraphics();
        applyQuality(downsample);
        downsample.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        downsample.drawImage(source, 0, 0, size, size, null);
        downsample.dispose();
        ImageIO.write(result, "png", output.toFile());
        System.out.println(output + " (" + size + "x" + size + ")");
    }

    private static void draw(Graphics2D graphics, Layer layer, Palette palette) {
        if (layer == Layer.PLAY_STORE) {
            // Google Play applies its own rounded mask and shadow to this full-square artwork.
            drawBackground(graphics);
            drawCenteredTiles(graphics, false, palette);
        } else if (layer == Layer.LEGACY) {
            graphics.clip(new Ellipse2D.Float(2, 2, 104, 104));
            drawBackground(graphics);
            drawCenteredTiles(graphics, false, palette);
        } else if (layer == Layer.BACKGROUND) {
            drawBackground(graphics);
        } else {
            // Adaptive layers are 108 units, while Android masks the centered 72-unit region.
            graphics.translate(18, 18);
            graphics.scale(2d / 3d, 2d / 3d);
            drawCenteredTiles(graphics, layer == Layer.MONOCHROME, palette);
        }
    }

    private static void drawCenteredTiles(Graphics2D graphics, boolean monochrome, Palette palette) {
        // Reduce the mark by 13%, then offset it right for optical centering.
        graphics.translate(8.85, 7.02);
        graphics.scale(0.87, 0.87);
        drawTiles(graphics, monochrome, palette);
    }

    private static void drawBackground(Graphics2D graphics) {
        graphics.setPaint(new RadialGradientPaint(
                54f,
                46.5f,
                78f,
                new float[] {0f, 0.58f, 1f},
                new Color[] {color("17243A"), color("101A2A"), color("080D18")}));
        graphics.fill(new Rectangle2D.Float(0, 0, DESIGN_SIZE, DESIGN_SIZE));
    }

    private static void drawTiles(Graphics2D graphics, boolean monochrome, Palette palette) {
        fill(graphics, rectangle(23, 14.5f), palette.primaryStart(), palette.primaryEnd(), monochrome);
        fill(graphics, rectangle(43, 14.5f), palette.secondaryStart(), palette.secondaryEnd(), monochrome);
        fill(graphics, upperCurve(63, 14.5f), palette.accentStart(), palette.accentEnd(), monochrome);

        fill(graphics, rectangle(23, 34.5f), palette.highlightStart(), palette.highlightEnd(), monochrome);
        fill(graphics, lowerCurve(63, 34.5f), palette.primaryStart(), palette.primaryEnd(), monochrome);

        fill(graphics, rectangle(23, 54.5f), palette.secondaryStart(), palette.secondaryEnd(), monochrome);
        fill(graphics, rectangle(43, 54.5f), palette.secondaryStart(), palette.secondaryEnd(), monochrome);
        fill(graphics, upperCurve(63, 54.5f), palette.accentStart(), palette.accentEnd(), monochrome);

        fill(graphics, rectangle(23, 74.5f), palette.primaryStart(), palette.primaryEnd(), monochrome);
        fill(graphics, triangle(), palette.accentStart(), palette.accentEnd(), monochrome);
        fill(graphics, lowerCurve(63, 74.5f), palette.highlightStart(), palette.highlightEnd(), monochrome);
    }

    private static Shape rectangle(float x, float y) {
        return new Rectangle2D.Float(x, y, 19, 19);
    }

    private static Shape upperCurve(float x, float y) {
        Path2D.Float path = new Path2D.Float();
        path.moveTo(x, y);
        path.lineTo(x + 1, y);
        path.curveTo(x + 11.5, y, x + 20, y + 8.5, x + 20, y + 19);
        path.lineTo(x, y + 19);
        path.closePath();
        return path;
    }

    private static Shape lowerCurve(float x, float y) {
        Path2D.Float path = new Path2D.Float();
        path.moveTo(x, y);
        path.lineTo(x + 20, y);
        path.lineTo(x + 20, y + 1);
        path.curveTo(x + 20, y + 11, x + 12, y + 19, x + 2, y + 19);
        path.lineTo(x, y + 19);
        path.closePath();
        return path;
    }

    private static Shape triangle() {
        Path2D.Float path = new Path2D.Float();
        path.moveTo(43, 93.5);
        path.lineTo(62, 93.5);
        path.lineTo(62, 74.5);
        path.closePath();
        return path;
    }

    private static void fill(
            Graphics2D graphics,
            Shape shape,
            Color start,
            Color end,
            boolean monochrome) {
        if (monochrome) {
            graphics.setColor(Color.WHITE);
        } else {
            Rectangle2D bounds = shape.getBounds2D();
            graphics.setPaint(new LinearGradientPaint(
                    (float) bounds.getMinX(),
                    (float) bounds.getMinY(),
                    (float) bounds.getMaxX(),
                    (float) bounds.getMaxY(),
                    new float[] {0f, 1f},
                    new Color[] {start, end}));
        }
        graphics.fill(shape);
    }

    private static void applyQuality(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    }

    private static Color color(String hex) {
        return new Color(Integer.parseInt(hex, 16));
    }

    private static Palette palette(
            String primaryStart,
            String primaryEnd,
            String secondaryStart,
            String secondaryEnd,
            String accentStart,
            String accentEnd,
            String highlightStart,
            String highlightEnd) {
        return new Palette(
                color(primaryStart),
                color(primaryEnd),
                color(secondaryStart),
                color(secondaryEnd),
                color(accentStart),
                color(accentEnd),
                color(highlightStart),
                color(highlightEnd));
    }

    private record Palette(
            Color primaryStart,
            Color primaryEnd,
            Color secondaryStart,
            Color secondaryEnd,
            Color accentStart,
            Color accentEnd,
            Color highlightStart,
            Color highlightEnd) {}

    private enum Layer {
        PLAY_STORE,
        LEGACY,
        BACKGROUND,
        FOREGROUND,
        MONOCHROME
    }
}
