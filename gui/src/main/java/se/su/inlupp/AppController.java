package se.su.inlupp;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class AppController {

  private final Pane imageContainer;
  private final Graph<Location> graph = new ListGraph<Location>();
  private ArrayList<Location> selectedLocations = new ArrayList<>();
  private MapMenu mapMenu;
  private boolean isPlacingNode = false;

  public AppController(Pane imageContainer) {
    this.imageContainer = imageContainer;
  }

  public void setMapMenu(MapMenu mapMenu) {
    this.mapMenu = mapMenu;
  }

  public void loadMapImage(File file) {
    Image image = new Image(file.toURI().toString());
    imageContainer.setMaxSize(image.getWidth(), image.getHeight());
    imageContainer.setMinSize(image.getWidth(), image.getHeight());

    BackgroundSize backgroundSize = new BackgroundSize(image.getWidth(), image.getHeight(), false, false, false, false);
    BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.CENTER, backgroundSize);
    Background background = new Background(backgroundImage);
    imageContainer.setBackground(background);

    mapMenu.setButtonsDisabled(false);
  }

  public void addNewPlace() {
    isPlacingNode = true;

    imageContainer.setCursor(Cursor.CROSSHAIR);
    mapMenu.setNewPlaceButtonDisabled(true);

    imageContainer.setOnMouseClicked(e -> {
      if (!isPlacingNode)
        return;

      isPlacingNode = false;
      double x = e.getX();
      double y = e.getY();

      imageContainer.setCursor(Cursor.DEFAULT);
      mapMenu.setNewPlaceButtonDisabled(false);

      TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle("Name");
      dialog.setContentText("Name of Place:");

      Optional<String> result = dialog.showAndWait();
      result.ifPresent(name -> {
        Location location = new Location(this, name, x, y);
        graph.add(location);

        render();
      });
    });
  }

  public void addNewConnection() {
    if (selectedLocations.size() != 2)
      return;

    Optional<String[]> result = connectionDialog("New Connection", "Add new connection", true, true);
    if (result.isEmpty())
      return;

    String[] res = result.get();
    Location loc1 = selectedLocations.get(0);
    Location loc2 = selectedLocations.get(1);

    graph.connect(loc1, loc2, res[0], Integer.parseInt(res[1]));

    render();

    setIsMarked(loc1);
    setIsMarked(loc2);
    // loc1.setIsMarked(false, this);
    // loc2.setIsMarked(false, this);
  }

  public void showConnection() {
    if (selectedLocations.size() != 2)
      return;
    Location loc1 = selectedLocations.getFirst();
    Location loc2 = selectedLocations.getLast();

    if (graph.getEdgeBetween(loc1, loc2) == null)
      return;

  }

  public void render() {
    imageContainer.getChildren().removeAll(imageContainer.getChildren());
    for (Location loc : graph.getNodes()) {
      loc.setLayoutX(loc.getX());
      loc.setLayoutY(loc.getY());

      imageContainer.getChildren().add(loc);

      for (Edge<Location> edge : graph.getEdgesFrom(loc)) {
        Location destination = (Location) edge.getDestination();
        Line line = new Line(loc.getX(), loc.getY(), destination.getX(), destination.getY());
        imageContainer.getChildren().addLast(line);
      }
    }
  }

  private Optional<String[]> connectionDialog(String title, String header, boolean isNameEditable,
      boolean isTimeEditable) {

    Dialog<String[]> dialog = new Dialog<>();
    dialog.setTitle(title);
    dialog.setHeaderText(header);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
    okButton.setDisable(true);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.add(new Label("Name:"), 0, 0);
    TextField nameInputField = new TextField();
    grid.add(nameInputField, 1, 0);
    grid.add(new Label("Time:"), 0, 1);
    TextField timeInputField = new TextField();
    grid.add(timeInputField, 1, 1);

    nameInputField.setEditable(isNameEditable);
    timeInputField.setEditable(isTimeEditable);

    nameInputField.textProperty().addListener((obs, oldText, newText) -> {
      okButton.setDisable(newText.trim().isEmpty());
    });

    timeInputField.textProperty().addListener((obs, oldText, newText) -> {
      okButton.setDisable(newText.trim().isEmpty());
    });

    dialog.getDialogPane().setContent(grid);
    dialog.setResultConverter(
        button -> button == ButtonType.OK ? new String[] { nameInputField.getText(), timeInputField.getText() }
            : null);

    String[] input = dialog.showAndWait().orElse(null);

    String name = input[0].trim();
    int weight = 0;

    if (name.isBlank()) {
      createErrorPopup(AlertType.ERROR, "Fel input i namn", "skriv in ett namn.");
      return null;
    }
    try {
      weight = Integer.parseInt(input[1].trim());
    } catch (Exception e) {
      createErrorPopup(AlertType.ERROR, "Fel input i tid", "Skriv in ett nummer som tid.");
      return null;
    }
    if (weight <= 0) {
      createErrorPopup(AlertType.ERROR, "Fel input i tid", "Tiden får inte vara 0 eller negativt.");
      return null;
    }
    return Optional.ofNullable(dialog.showAndWait().orElse(null));
  }

  private void createErrorPopup(AlertType type, String headerText, String contentText) {
    Alert error = new Alert(type);
    error.setHeaderText(headerText);
    error.setContentText(contentText);
    error.showAndWait();
  }

  public ArrayList<Location> getSelection() {
    return selectedLocations;
  }

  public void addSelection(Location loc) {
    if (selectedLocations.size() >= 2)
      return;
    selectedLocations.add(loc);
  }

  public void removeSelection(Location loc) {
    selectedLocations.remove(loc);
  }

  public void setIsMarked(Location loc) {
    if (getSelection().size() >= 2 && !loc.getIsMarked())
      return;
    loc.changeMarkedStatus();
    if (loc.getIsMarked()) {
      loc.setColor(Color.BLUE);
      addSelection(loc);
    } else {
      loc.setColor(Color.CRIMSON);
      removeSelection(loc);
    }
  }
}
