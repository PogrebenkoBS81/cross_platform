package pogrebenko.labsix.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class MainController {
    private static final Logger LOGGER = LoggerWrapper.getLogger();

    private String logPath;
    private ArrayList<Double[]> plotData;

    private int selectedXAxis = 7;
    private int selectedYAxis = 2;

    private ScatterChart<Number, Number> scDataPlot; // Value injected by FXMLLoader

    @FXML // fx:id="apPlotSite"
    private AnchorPane apPlotSite; // Value injected by FXMLLoader

    @FXML
    void onXSelected(ActionEvent event) {
        RadioButton selectedRadioButton = (RadioButton) event.getSource();
        selectedXAxis = Integer.parseInt(selectedRadioButton.getId());

        LOGGER.info("New X axis selected: " + selectedXAxis);
    }

    @FXML
    void onYSelected(ActionEvent event) {
        RadioButton selectedRadioButton = (RadioButton) event.getSource();
        selectedYAxis = Integer.parseInt(selectedRadioButton.getId());

        LOGGER.info("New Y axis selected: " + selectedXAxis);
    }

    @FXML
    void onDataDraw(ActionEvent event) {
        LOGGER.info(String.format("Drawing data by axis X: %d and Y: %d ", selectedXAxis, selectedYAxis));

        drawScatterPlot();
    }

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        LOGGER.info("Initializing....");

        LOGGER.info("Creating scatter chart...");
        scDataPlot = new ScatterChart<>(new NumberAxis(), new NumberAxis());
        scDataPlot.setPrefSize(apPlotSite.getPrefWidth(), apPlotSite.getPrefHeight());

        LOGGER.info("Adding scatter chart to plot");
        apPlotSite.getChildren().add(scDataPlot);
    }

    private ChartSeries prepareDataSeries() {
        LOGGER.info("Preparing data series...");

        XYChart.Series<Number, Number> positiveSeries = new XYChart.Series<>();
        positiveSeries.setName("Positive series");

        XYChart.Series<Number, Number> negativeSeries = new XYChart.Series<>();
        negativeSeries.setName("Negative series");

        for (var stringPoints : getPlotData()) {
            Double x = stringPoints[getSelectedXAxis()];
            Double y = stringPoints[getSelectedYAxis()];

            if (x == null || y == null) {
                LOGGER.warning("Null value appeared during plot drawing!");

                continue;
            }

            XYChart.Data<Number, Number> data = new XYChart.Data<>(x, y);

            if (stringPoints[stringPoints.length - 1].equals(1.0)) {
                positiveSeries.getData().add(data);
            } else {
                negativeSeries.getData().add(data);
            }
        }

        return new ChartSeries(positiveSeries, negativeSeries);
    }

    private void drawScatterPlot() {
        LOGGER.warning("Drawing new scatter plot...");

        scDataPlot.getData().clear();
        ChartSeries plotSeries = prepareDataSeries();

        scDataPlot.getData().add(plotSeries.positiveSeries);
        scDataPlot.getData().add(plotSeries.negativeSeries);
    }

    public void prepareController() throws IOException {
        LOGGER.info("Preparing controller...");

        LoggerWrapper.getWrapper().addFileHandler(getLogFilePath());
        drawScatterPlot();
    }


    public void setLogPath(String logPath) {
        LOGGER.finest("Setting log file path...");

        this.logPath = logPath;
    }

    public String getLogFilePath() {
        LOGGER.finest("Retrieving log file path...");

        return logPath;
    }

    private Double parseDataDouble(String toParse) {
        try {
            return Double.parseDouble(toParse);
        } catch (NumberFormatException | NullPointerException e) {
            LOGGER.warning("Invalid double value to parse from string: " + toParse);
        }

        return null;
    }

    public void setStringPlotData(ArrayList<String[]> plotData) {
        LOGGER.finest("Setting plot data...");
        ArrayList<Double[]> doubleData = new ArrayList<>();

        for (String[] dataRow : plotData) {
            doubleData.add(
                    Arrays.stream(dataRow)
                            .map(this::parseDataDouble)
                            .toArray(Double[]::new)
            );
        }

        setPlotData(doubleData);
    }

    private int getSelectedXAxis() {
        return selectedXAxis - 1;
    }

    private int getSelectedYAxis() {
        return selectedYAxis - 1;
    }

    public ArrayList<Double[]> getPlotData() {
        LOGGER.finest("Retrieving plot data...");

        return this.plotData;
    }

    public void setPlotData(ArrayList<Double[]> plotData) {
        LOGGER.finest("Setting plot data...");

        this.plotData = plotData;
    }
}

class ChartSeries {
    public final XYChart.Series<Number, Number> positiveSeries;
    public final XYChart.Series<Number, Number> negativeSeries;

    ChartSeries(XYChart.Series<Number, Number> positiveSeries, XYChart.Series<Number, Number> negativeSeries) {
        this.positiveSeries = positiveSeries;
        this.negativeSeries = negativeSeries;
    }
}
