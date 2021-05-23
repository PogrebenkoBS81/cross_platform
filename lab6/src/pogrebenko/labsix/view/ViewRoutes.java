package pogrebenko.labsix.view;

import java.net.URL;

/**
 * Routes of the different windows of the app.
 * Enum is used to ensure that only given routes will be used.
 *
 * @author Pogrebenko Vasily, BS-81
 * @version 1.3.0
 * @since 1.2
 */
public enum ViewRoutes {
    // Initialize paths to the every view in the project.
    APP_INIT_SCREEN("app_init.fxml"),
    APP_MAIN_SCREEN("app_main.fxml");

    private final URL url;

    /**
     * Constructs view url.
     *
     * @param url url of the view.
     */
    ViewRoutes(final String url) {
        this.url = getClass().getResource(url);
    }

    /**
     * Returns the url of the view.
     *
     * @return url of the view.
     */
    public URL getURL() {
        return url;
    }

    /**
     * Returns the path to the view.
     *
     * @return string representation of the view url.
     */
    @Override
    public String toString() {
        return url.toString();
    }
}