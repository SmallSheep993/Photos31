package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import view.Album;
import view.Photo;
import view.AlbumList;
import view.AlbumListHelper;

/**
 * Controller for the user's album list screen.
 * Allows the user to create, delete, rename albums, perform searches, and open albums.
 */
public class AlbumController {
    @FXML
    private ListView<Album> albumListView;

    /** The list of albums for the current user. */
    private AlbumList albumList;
    /** The username of the current user. */
    private String username;

    /**
     * Initializes the album list controller. Sets up double-click handler for opening albums.
     */
    @FXML
    private void initialize() {
        // Double-click an album to open its photo view
        albumListView.setOnMouseClicked((MouseEvent click) -> {
            if (click.getClickCount() == 2) {
                Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
                if (selectedAlbum != null) {
                    openPhotoView(selectedAlbum);
                }
            }
        });
    }

    /**
     * Starts the album list view for a given user by loading that user's albums from storage.
     * @param username the username whose albums are to be loaded
     */
    public void start(String username) {
        this.username = username;
        albumList = AlbumListHelper.load(username);
        albumListView.getItems().setAll(albumList.getAlbums());
    }

    /**
     * Handles the creation of a new album. Prompts for an album name and adds it if valid.
     */
    @FXML
    private void handleCreateAlbum() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Album");
        dialog.setHeaderText("Create New Album");
        dialog.setContentText("Enter album name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            name = name.trim();
            if (name.isEmpty()) {
                showAlert("Album name cannot be empty!");
            } else if (albumList.albumExists(name)) {
                showAlert("Album \"" + name + "\" already exists.");
            } else {
                Album album = new Album(name);
                albumList.addAlbum(album);
                albumListView.getItems().add(album);
                AlbumListHelper.save(username, albumList);
            }
        });
    }

    /**
     * Handles the deletion of the selected album.
     * Removes the album from the list and saves changes.
     */
    @FXML
    private void handleDeleteAlbum() {
        Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum != null) {
            // Prevent deletion of stock album for stock user
            if (username.equals("stock") && selectedAlbum.getName().equals("stock")) {
                showAlert("Cannot delete the stock album.");
                return;
            }
            albumList.removeAlbum(selectedAlbum);
            albumListView.getItems().remove(selectedAlbum);
            AlbumListHelper.save(username, albumList);
        }
    }

    /**
     * Handles the renaming of the selected album.
     * Prompts the user for a new album name and updates it if valid.
     */
    @FXML
    private void handleRenameAlbum() {
        Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum == null) {
            showAlert("No album selected.");
            return;
        }
        // Prevent renaming of stock album for stock user
        if (username.equals("stock") && selectedAlbum.getName().equals("stock")) {
            showAlert("Cannot rename the stock album.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog(selectedAlbum.getName());
        dialog.setTitle("Rename Album");
        dialog.setHeaderText("Rename Album \"" + selectedAlbum.getName() + "\"");
        dialog.setContentText("Enter new album name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            newName = newName.trim();
            if (newName.isEmpty()) {
                showAlert("Album name cannot be empty!");
            } else if (albumList.albumExists(newName) && !newName.equalsIgnoreCase(selectedAlbum.getName())) {
                showAlert("Album \"" + newName + "\" already exists.");
            } else {
                // Perform rename
                selectedAlbum.setName(newName);
                AlbumListHelper.save(username, albumList);
                albumListView.refresh();
            }
        });
    }

    /**
     * Handles searching photos by tags.
     * Prompts the user for a tag query and opens a new album view with all photos matching the query.
     */
    @FXML
    private void handleSearchByTag() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Photos by Tag");
        dialog.setHeaderText("Search Photos by Tag");
        dialog.setContentText("Enter tag search query (e.g., tag=value or tag1=value1 AND tag2=value2):");
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) {
            return;
        }
        String query = result.get().trim();
        if (query.isEmpty()) {
            return;
        }
        // Determine search type (AND, OR, or single)
        boolean hasAnd = query.toLowerCase().contains(" and ");
        boolean hasOr = query.toLowerCase().contains(" or ");
        if (hasAnd && hasOr) {
            showAlert("Invalid query. Use either AND or OR (not both).");
            return;
        }
        // Parse query into tag name-value pairs
        String name1, value1, name2 = null, value2 = null;
        if (hasAnd || hasOr) {
            String delimiter = hasAnd ? " and " : " or ";
            String[] parts = query.split("(?i)" + delimiter);
            if (parts.length != 2) {
                showAlert("Invalid tag query format.");
                return;
            }
            String part1 = parts[0].trim();
            String part2 = parts[1].trim();
            if (!part1.contains("=") || !part2.contains("=")) {
                showAlert("Tag query must be in name=value format.");
                return;
            }
            String[] kv1 = part1.split("=", 2);
            String[] kv2 = part2.split("=", 2);
            name1 = kv1[0].trim();
            value1 = kv1.length > 1 ? kv1[1].trim() : "";
            name2 = kv2[0].trim();
            value2 = kv2.length > 1 ? kv2[1].trim() : "";
            if (name1.isEmpty() || value1.isEmpty() || name2.isEmpty() || value2.isEmpty()) {
                showAlert("Tag name and value cannot be empty.");
                return;
            }
        } else {
            if (!query.contains("=")) {
                showAlert("Tag query must be in name=value format.");
                return;
            }
            String[] kv = query.split("=", 2);
            name1 = kv[0].trim();
            value1 = kv.length > 1 ? kv[1].trim() : "";
            if (name1.isEmpty() || value1.isEmpty()) {
                showAlert("Tag name and value cannot be empty.");
                return;
            }
        }
        // Search across all albums for matching photos
        List<Photo> resultPhotos = new ArrayList<>();
        Set<String> seenFiles = new HashSet<>();
        for (Album alb : albumList.getAlbums()) {
            for (Photo photo : alb.getPhotos()) {
                boolean match = false;
                if (hasAnd) {
                    // Conjunctive query
                    if (photo.hasTag(name1, value1) && photo.hasTag(name2, value2)) {
                        match = true;
                    }
                } else if (hasOr) {
                    // Disjunctive query
                    if (photo.hasTag(name1, value1) || photo.hasTag(name2, value2)) {
                        match = true;
                    }
                } else {
                    // Single tag query
                    if (photo.hasTag(name1, value1)) {
                        match = true;
                    }
                }
                if (match) {
                    String path = photo.getFilePath();
                    if (!seenFiles.contains(path)) {
                        seenFiles.add(path);
                        resultPhotos.add(photo);
                    }
                }
            }
        }
        // Display search results similar to album view
        Album searchAlbum = new Album("Search Results");
        for (Photo p : resultPhotos) {
            searchAlbum.addPhoto(p);
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photo.fxml"));
            Parent root = loader.load();
            PhotoController controller = loader.getController();
            controller.start(searchAlbum, username, albumList);
            Stage stage = (Stage) albumListView.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles searching photos by a date range.
     * Prompts the user for a start and end date and opens a new album view with all photos in that range.
     */
    @FXML
    private void handleSearchByDate() {
        TextInputDialog startDialog = new TextInputDialog();
        startDialog.setTitle("Search Photos by Date");
        startDialog.setHeaderText("Search Photos by Date Range");
        startDialog.setContentText("Enter start date (MM/DD/YYYY):");
        Optional<String> startResult = startDialog.showAndWait();
        if (!startResult.isPresent()) {
            return;
        }
        TextInputDialog endDialog = new TextInputDialog();
        endDialog.setTitle("Search Photos by Date");
        endDialog.setHeaderText("Search Photos by Date Range");
        endDialog.setContentText("Enter end date (MM/DD/YYYY):");
        Optional<String> endResult = endDialog.showAndWait();
        if (!endResult.isPresent()) {
            return;
        }
        String startStr = startResult.get().trim();
        String endStr = endResult.get().trim();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(startStr, df);
            endDate = LocalDate.parse(endStr, df);
        } catch (DateTimeParseException e) {
            showAlert("Invalid date format. Please use MM/DD/YYYY.");
            return;
        }
        if (endDate.isBefore(startDate)) {
            showAlert("End date must be on or after start date.");
            return;
        }
        // Search photos within date range
        List<Photo> resultPhotos = new ArrayList<>();
        Set<String> seenFiles = new HashSet<>();
        for (Album alb : albumList.getAlbums()) {
            for (Photo photo : alb.getPhotos()) {
                LocalDate photoDate = photo.getDate().toLocalDate();
                if ((photoDate.isEqual(startDate) || photoDate.isAfter(startDate)) &&
                    (photoDate.isEqual(endDate) || photoDate.isBefore(endDate))) {
                    String path = photo.getFilePath();
                    if (!seenFiles.contains(path)) {
                        seenFiles.add(path);
                        resultPhotos.add(photo);
                    }
                }
            }
        }
        // Display search results
        Album searchAlbum = new Album("Search Results");
        for (Photo p : resultPhotos) {
            searchAlbum.addPhoto(p);
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photo.fxml"));
            Parent root = loader.load();
            PhotoController controller = loader.getController();
            controller.start(searchAlbum, username, albumList);
            Stage stage = (Stage) albumListView.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles logging out from a user session. Returns to the login screen without exiting the program.
     */
    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) albumListView.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            stage.setScene(new Scene(root, 400, 300));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the photo view for a given album in the current stage.
     * @param album the Album to open and display
     */
    private void openPhotoView(Album album) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photo.fxml"));
            Parent root = loader.load();
            PhotoController controller = loader.getController();
            controller.start(album, username, albumList);
            Stage stage = (Stage) albumListView.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays an error alert with the specified message.
     * @param message the message to display
     */
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
