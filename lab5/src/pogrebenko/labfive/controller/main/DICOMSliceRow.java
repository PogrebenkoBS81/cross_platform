package pogrebenko.labfive.controller.main;

import java.security.InvalidParameterException;

/**
 * Represents the  DICOM row value in listview.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.0
 * @since 1.0
 */
class DICOMSliceRow {
    private String sliceLabel;
    private Double sliceLocation;
    private Integer sliceSize;

    public DICOMSliceRow(Double sliceLocation, Integer sliceSize) {
        setSliceLocation(sliceLocation);
        setSliceSize(sliceSize);
        setSliceLabel(String.format("Location: %f; Images: %d", getSliceLocation(), getSliceSize()));
    }

    public String getSliceLabel() {
        return this.sliceLabel;
    }

    public void setSliceLabel(String sliceLabel) {
        this.sliceLabel = sliceLabel;
    }

    public Double getSliceLocation() {
        return this.sliceLocation;
    }

    public void setSliceLocation(Double sliceLocation) {
        this.sliceLocation = sliceLocation;
    }

    public Integer getSliceSize() {
        return this.sliceSize;
    }

    public void setSliceSize(Integer sliceSize) {
        if (sliceSize < 0) {
            throw new InvalidParameterException("Slice size cannot be below 0!");
        }

        this.sliceSize = sliceSize;
    }
}