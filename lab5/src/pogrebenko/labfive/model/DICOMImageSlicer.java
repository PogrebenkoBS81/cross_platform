package pogrebenko.labfive.model;

import pogrebenko.loggerwrapper.LoggerWrapper;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class DICOMImageSlicer {
    private static final Logger LOGGER = LoggerWrapper.getLogger();
    private Map<Double, List<String>> slices = new HashMap<>();

    public DICOMImageSlicer(List<String> DICOMFilePaths) throws Exception {
        runSlicing(DICOMFilePaths);
    }

    private void runSlicing(List<String> DICOMFilePaths) throws Exception {
        // Slices {"location" : [<file path>, <file path>]}
        // Images loaded into memory only when they are required.
        LOGGER.info("Creating images slices from listed files...");
        Map<Double, List<String>> newSlices = new HashMap<>();
        // Create slices by Slice location.
        for (String filepath : DICOMFilePaths) {
            DICOMImage newImage = getDICOMFromPath(filepath);

            if (!newSlices.containsKey(newImage.SliceLocation)) {
                newSlices.put(newImage.SliceLocation, new ArrayList<>());
            }

            newSlices.get(newImage.SliceLocation).add(filepath);
        }

        slices = newSlices;
        LOGGER.info("Slices created: " + slices.size());
    }

    private DICOMImage getDICOMFromPath(String filepath) throws Exception {
        LOGGER.finest("Retrieving DICOMImage from file: " + filepath);
        File DICOMFile = new File(filepath);

        String fileDir = DICOMFile.getAbsoluteFile().getParent();
        String fileName = DICOMFile.getName();

        return new DICOMImage(fileDir, fileName);
    }

    public Map<Double, List<String>> getSlices() {
        LOGGER.finest("Retrieving slices... ");

        return slices;
    }

    public List<DICOMImage> getSliceByLocation(Double sliceLocation) throws Exception {
        LOGGER.info("Retrieving slice by its location... ");
        List<String> slicePaths = slices.get(sliceLocation);
        List<DICOMImage> sliceImages = new ArrayList<>();

        for (String imagePath : slicePaths) {
            sliceImages.add(getDICOMFromPath(imagePath));
        }
        // Sort images, so animation will be in the correct order.
        Collections.sort(sliceImages);

        return sliceImages;
    }
}
