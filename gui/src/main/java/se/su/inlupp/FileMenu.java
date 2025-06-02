package se.su.inlupp;

import java.io.File;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileMenu {
  private final MenuBar bar;

  public FileMenu(AppController ctrl, Stage stage) {
    Menu menu = new Menu("File");

    MenuItem newMap = new MenuItem("New Map");
    MenuItem openMap = new MenuItem("Open");
    MenuItem saveMap = new MenuItem("Save");
    MenuItem saveImage = new MenuItem("Save Image");
    MenuItem exit = new MenuItem("Exit");

    newMap.setOnAction(e -> addNewMap(ctrl, stage));
    saveMap.setOnAction(e -> ctrl.saveMap());
    exit.setOnAction(e -> stage.close());

    menu.getItems().addAll(newMap, openMap, saveMap, saveImage, exit);

    bar = new MenuBar();
    bar.getMenus().add(menu);
  }

  public MenuBar getFileMenu() {
    return bar;
  }

  public void addNewMap(AppController ctrl, Stage stage) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("New Map");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("GIF", "*.gif"),
        new FileChooser.ExtensionFilter("GRAPH", "*.graph"));

    File selectedFile = fileChooser.showOpenDialog(stage);
    if (selectedFile != null) {
      ctrl.loadMapImage(selectedFile);
    }
  }

}
