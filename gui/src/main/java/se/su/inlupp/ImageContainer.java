package se.su.inlupp;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ImageContainer {

  private final Pane container;

  public ImageContainer(Stage stage) {
    container = new Pane();
  }

  public Pane getImageContainer() {
    return container;
  }

}
