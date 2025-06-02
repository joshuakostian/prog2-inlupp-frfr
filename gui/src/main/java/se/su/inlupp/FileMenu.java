// PROG2 VT2025, inlämningsuppgift, del 2
// Grupp 269
// Ville Viljanen vivi8475
// Joshua Kostian 5833
// Carl Thomasson cath8913

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

    newMap.setOnAction(e -> ctrl.loadMapImage(null));
    saveMap.setOnAction(e -> ctrl.saveMap());
    exit.setOnAction(e -> {
      if (ctrl.continueWithoutSaving()) stage.close();
    });
    openMap.setOnAction(e->ctrl.openMap());
    saveImage.setOnAction(e-> ctrl.saveImage());

    menu.getItems().addAll(newMap, openMap, saveMap, saveImage, exit);

    bar = new MenuBar();
    bar.getMenus().add(menu);
  }

  public MenuBar getFileMenu() {
    return bar;
  }



}
