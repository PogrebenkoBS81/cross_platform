package pogrebenko.labfive.model;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import pogrebenko.loggerwrapper.LoggerWrapper;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FTPClientLoader implements Closeable {
    private static final Logger LOGGER = LoggerWrapper.getLogger();
    private FTPClient ftpClient;

    public FTPClientLoader(String host, int port, String username, String pass) throws IOException {
        setFTPConnection(host, port, pass, username);
    }

    public void setFTPConnection(String host, int port, String pass, String username) throws IOException {
        LOGGER.info(
                String.format("Creating ftp connection, host: %s; port: %s; username: %s", host, port, username)
        );

        ftpClient = new FTPClient();
        ftpClient.connect(host, port);
        ftpClient.login(username, pass);
        // All connections are established by client.
        ftpClient.enterLocalPassiveMode();
        // Ensure connection to stay alive.
        ftpClient.sendNoOp();
    }

    public List<String> listDirectoryFiles(String hostPath) throws IOException {
        LOGGER.info("Listing files on host machine via FTP, host dir to list: " + hostPath);
        List<String> listedFiles = Arrays.stream(ftpClient.listFiles(hostPath))
                .filter(FTPFile::isFile)
                .map(FTPFile::getName)
                .collect(Collectors.toList());
        LOGGER.info("Number of listed files on host machine: " + listedFiles.size());

        return listedFiles;
    }

    public boolean downloadFile(String hostFile, String toSave, boolean isOverwrite) throws IOException {
        LOGGER.finest(String.format(
                "Downloading file '%s' via FTP; save location: '%s'; file overwrite: %b ",
                hostFile,
                toSave,
                isOverwrite
                )
        );
        File downloadedFile = new File(toSave);

        if (downloadedFile.createNewFile() || isOverwrite) {
            ftpClient.retrieveFile(hostFile, new FileOutputStream(toSave, false));

            return true;
        }

        LOGGER.finest("File was not created: " + toSave);

        return false;
    }

    public void close() throws IOException {
        LOGGER.info("Closing ftp connection...");

        if (!ftpClient.logout()) {
            LOGGER.severe("Error during FTP logout!");
        }

        ftpClient.disconnect();
    }
}
