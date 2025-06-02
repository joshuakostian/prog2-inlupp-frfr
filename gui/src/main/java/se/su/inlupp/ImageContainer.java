package se.su.inlupp;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ImageContainer {

  private final Pane container;
  private Image backgroundImage;

  public ImageContainer(Stage stage) {
    container = new Pane();
  }

  public Pane getImageContainer() {
    return container;
  }

  public void saveImage(Image image) {
    this.backgroundImage = image;
  }

  public Image getImage() {
    return backgroundImage;
  }
}
