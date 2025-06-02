// PROG2 VT2025, inlämningsuppgift, del 2
// Grupp 269
// Ville Viljanen vivi8475
// Joshua Kostian 5833
// Carl Thomasson cath8913

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
