package pogrebenko.labfive.controller.main;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import pogrebenko.labfive.model.DICOMImage;

import javax.management.InvalidAttributeValueException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Represents the DICOM picture drawer.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.0
 * @since 1.0
 */
class PictureDrawer {
    private final PictureAnimationHelper animationHelper;
    Timer timer;
    private Canvas canvasImage;
    private Label taImageData;

    PictureDrawer(List<DICOMImage> currentSlice, Canvas canvasImage, Label taImageData)
            throws InvalidAttributeValueException {
        animationHelper = new PictureAnimationHelper(currentSlice);
        setCanvas(canvasImage);
        setImageDataLabel(taImageData);
    }

    public void run() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> drawImage());
            }
        };
        timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 27);
    }

    public void stop() {
        timer.cancel();
        // runLater to ensure that image will be clear.
        Platform.runLater(() -> {
            clearImage();
            taImageData.setText("");
        });
    }

    private double getXCenterCoords(Image image) {
        return (canvasImage.getWidth() - image.getWidth()) / 2;
    }

    private double getYCenterCoords(Image image) {
        return (canvasImage.getHeight() - image.getHeight()) / 2;
    }

    private void drawImage() {
        // Since underlying helper is synchronized, worst possible scenario is
        // changed configuration between getCurrentSlide() and incrementCurrentSlide()
        // And this will only result in that new slide will start from it's next image
        // (or from its 0, is slice size is 1)
        DICOMImage currentImg = animationHelper.getCurrentSlide();
        animationHelper.incrementCurrentSlide();

        Image image = SwingFXUtils.toFXImage(currentImg.Image, null);
        GraphicsContext ctx = canvasImage.getGraphicsContext2D();

        setImageDataText(
                "Image data:" + "\n" +
                        "File Name: " + currentImg.FileName + "\n" +
                        "Series Number: " + currentImg.SeriesNumber + "\n" +
                        "Acqu Number: " + currentImg.AcquNumber + "\n" +
                        "Inst Number: " + currentImg.InstNumber + "\n"
        );

        // Center the image
        // TODO: add check if image bigger than canvas.
        ctx.drawImage(
                image,
                getXCenterCoords(image),
                getYCenterCoords(image)
        );
    }

    private void clearImage() {
        GraphicsContext ctx = canvasImage.getGraphicsContext2D();
        ctx.clearRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
    }

    public void setCurrentSlice(List<DICOMImage> currentSlice) throws InvalidAttributeValueException {
        animationHelper.setCurrentSlice(currentSlice);
    }

    public void setImageDataText(String data) {
        taImageData.setText(data);
    }

    public void setCanvas(Canvas canvasImage) {
        this.canvasImage = canvasImage;
    }

    public void setImageDataLabel(Label taImageData) {
        this.taImageData = taImageData;
    }
}
