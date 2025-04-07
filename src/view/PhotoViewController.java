package view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the photo viewer window.
 * Displays a single photo with its details (caption, date, tags) and allows navigating to previous or next photo in the album.
 */
public class PhotoViewController {
    @FXML
    private ImageView photoImageView;
    @FXML
    private Label captionLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label tagsLabel;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;

    /** The album containing the photos being viewed. */
    private Album album;
    /** The index of the currently displayed photo in the album. */
    private int index;

    /**
     * Initializes the photo viewer for a given album and photo index.
     * Displays the photo and its details, and sets navigation button states.
     * @param album the Album containing the photo sequence
     * @param index the index of the photo to display initially
     */
    public void start(Album album, int index) {
        this.album = album;
        this.index = index;
        updatePhoto();
    }

    /**
     * Displays the previous photo in the album, if available.
     */
    @FXML
    private void handlePrevPhoto() {
        if (index > 0) {
            index--;
            updatePhoto();
        }
    }

    /**
     * Displays the next photo in the album, if available.
     */
    @FXML
    private void handleNextPhoto() {
        if (index < album.getPhotos().size() - 1) {
            index++;
            updatePhoto();
        }
    }

    /**
     * Updates the image view and detail labels to show the photo at the current index.
     */
    private void updatePhoto() {
        Photo photo = album.getPhotos().get(index);
        File file = new File(photo.getFilePath());
        if (file.exists()) {
            Image image = new Image(file.toURI().toString());
            photoImageView.setImage(image);
        } else {
            photoImageView.setImage(null);
        }
        // Set caption, date, and tags
        String captionText = photo.getDescription().isEmpty() ? "(No Caption)" : photo.getDescription();
        captionLabel.setText("Caption: " + captionText);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        dateLabel.setText("Date: " + photo.getDate().format(dtf));
        if (photo.getTags().isEmpty()) {
            tagsLabel.setText("Tags: (None)");
        } else {
            String tagsText = String.join(", ", photo.getTags().stream().map(Tag::toString).toArray(CharSequence[]::new));
            tagsLabel.setText("Tags: " + tagsText);
        }
        // Update navigation buttons
        prevButton.setDisable(index <= 0);
        nextButton.setDisable(index >= album.getPhotos().size() - 1);
    }
}
