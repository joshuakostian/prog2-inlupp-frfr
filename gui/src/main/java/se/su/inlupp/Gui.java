package se.su.inlupp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Gui extends Application {

  public void start(Stage stage) {

    // Root
    BorderPane root = new BorderPane();
    ImageContainer imageContainer = new ImageContainer(stage);

    AppController ctrl = new AppController(imageContainer, stage);

    MenuBar fileMenu = new FileMenu(ctrl, stage).getFileMenu();
    MapMenu mapMenu = new MapMenu(ctrl, stage);
    ctrl.setMapMenu(mapMenu);

    VBox top = new VBox();
    top.getChildren().addAll(fileMenu, mapMenu.getMapMenu());

    root.setTop(top);
    root.setCenter(imageContainer.getImageContainer());

    // Draw Scene
    Scene scene = new Scene(root);

    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
