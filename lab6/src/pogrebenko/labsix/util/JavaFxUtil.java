package pogrebenko.labsix.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pogrebenko.labsix.view.ViewRoutes;

import java.io.IOException;

/**
 * Frontend util methods that may be used in various labs.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.0
 * @since 1.0
 */
public class JavaFxUtil {
    /**
     * Private constructor that throws an exception to prevent instantiation.
     */
    private JavaFxUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates "browse for file" dialog.
     *
     * @param label      label message of the dialog.
     * @param extensions extensions to browse for.
     * @return chosen file.
     */
    public static FileChooser browseForFile(String label, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(label, extensions));

        return fileChooser;
    }

    /**
     * Returns FXML loader for given window.
     *
     * @param route route of view to load.
     * @return FXML loader for given window.
     */
    public static FXMLLoader getSceneLoader(ViewRoutes route) {
        return new FXMLLoader(route.getURL());
    }

    public static Stage getNewStage(Parent root, String title) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        return stage;
    }

    /**
     * Opens new window with given title, and sets it's scene to "root" scene.
     *
     * @param root  root scene to display in the new window.
     * @param title title of the new window.
     */
    public static void openNewWindow(Parent root, String title) {
        getNewStage(root, title).show();
    }

    /**
     * Opens new window with view from given FXML route and given title.
     * No settings or fields is set here, so new window will be "clean".
     *
     * @param route route of view to load.
     * @param title title of the new window.
     */
    public static void openCleanWindow(ViewRoutes route, String title) throws IOException {
        FXMLLoader loader = getSceneLoader(route);
        openNewWindow(loader.load(), title);
    }

    /**
     * Closes window via it's event.
     *
     * @param event event of the window to close.
     */
    public static void closeWindow(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
