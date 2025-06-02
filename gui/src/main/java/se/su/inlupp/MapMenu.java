package se.su.inlupp;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MapMenu {

  private final HBox menu;
  private final ArrayList<Button> buttons = new ArrayList<>();
  private final Button newPlace;

  public MapMenu(AppController ctrl, Stage stage) {
    menu = new HBox();
    menu.setSpacing(5);
    menu.setAlignment(Pos.CENTER);

    Button findPath = new Button("Find Path");
    Button showConnection = new Button("Show Connection");
    newPlace = new Button("New Place");
    Button newConnection = new Button("New Connection");
    Button changeConnection = new Button("Change Connection");

    newPlace.setOnAction(e -> ctrl.addNewPlace());
    newConnection.setOnAction(e -> ctrl.addNewConnection());
    showConnection.setOnAction(e -> ctrl.showConnection());
    changeConnection.setOnAction(e -> ctrl.changeConnection());
    findPath.setOnAction(e -> ctrl.findPath());

    buttons.addAll(List.of(findPath, showConnection, newPlace, newConnection, changeConnection));
    menu.getChildren().addAll(findPath, showConnection, newPlace, newConnection, changeConnection);
    setButtonsDisabled(true);

  }

  public HBox getMapMenu() {
    return menu;
  }

  public void setButtonsDisabled(Boolean value) {
    buttons.forEach(b -> b.setDisable(value));
  }

  public void setNewPlaceButtonDisabled(Boolean value) {
    newPlace.setDisable(value);
  }
}
