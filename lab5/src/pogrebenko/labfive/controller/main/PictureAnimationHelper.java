package pogrebenko.labfive.controller.main;

import pogrebenko.labfive.model.DICOMImage;

import javax.management.InvalidAttributeValueException;
import java.util.List;

/**
 * Represents current slide position in the slice of images.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.0
 * @since 1.0
 */
public class PictureAnimationHelper {
    private List<DICOMImage> slice;
    private int currSlide;

    public PictureAnimationHelper(List<DICOMImage> currentSlice) throws InvalidAttributeValueException {
        setCurrentSlice(currentSlice);
    }

    // Since configuration can be changed in the middle of the process, SYNCHRONIZED is required.
    public synchronized void setCurrentSlice(List<DICOMImage> slice) throws InvalidAttributeValueException {
        if (slice.size() < 1) {
            throw new InvalidAttributeValueException("Slice must have at least 1 image.");
        }

        // If new slice were given - set it, and set curr idx to 0.
        this.slice = slice;
        this.currSlide = 0;
    }

    // Same as above.
    public synchronized void incrementCurrentSlide() {
        // TODO: Replace it with java util atomic.
        this.currSlide++;
        // If slice is ended - start from the beginning.
        if (currSlide >= slice.size()) {
            this.currSlide = 0;
        }
    }

    // Same as above.
    public synchronized DICOMImage getCurrentSlide() {
        return slice.get(this.currSlide);
    }
}
