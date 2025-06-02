package se.su.inlupp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.util.regex.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class AppController {

  private Graph<Location> graph = new ListGraph<Location>();
  private ArrayList<Location> selectedLocations = new ArrayList<>();
  private MapMenu mapMenu;
  private ImageContainer imageContainer;
  private boolean isPlacingNode = false;
  private Stage stage;
  private boolean isUnsaved = false;

  public AppController(ImageContainer imageContainer, Stage stage) {
    this.imageContainer = imageContainer;
    this.stage = stage;
    stage.setOnCloseRequest(e -> {
      if (isUnsaved && !continueWithoutSaving()) {
        e.consume();
      }
    });
  }
  
  public void setMapMenu(MapMenu mapMenu) {
    this.mapMenu = mapMenu;
  }

  public void setImageContainer(ImageContainer imageContainer) {
    this.imageContainer = imageContainer;
  }

  public void loadMapImage(Image inputImage) {
    if (isUnsaved && !continueWithoutSaving()) {
      return;
    }

    Image image = inputImage;
    if (inputImage == null) {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("New Map");

      fileChooser.getExtensionFilters().addAll(
          new FileChooser.ExtensionFilter("GIF", "*.gif"));

      File selectedFile = fileChooser.showOpenDialog(stage);
      if (selectedFile == null || !selectedFile.exists()) {
        return;
      }
      image = new Image(selectedFile.toURI().toString());
      
      isUnsaved = true;
    }

    imageContainer.getImageContainer().setMaxSize(image.getWidth(), image.getHeight());
    imageContainer.getImageContainer().setMinSize(image.getWidth(), image.getHeight());

    BackgroundSize backgroundSize = new BackgroundSize(image.getWidth(), image.getHeight(), false, false, false, false);
    BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.CENTER, backgroundSize);
    Background background = new Background(backgroundImage);
    imageContainer.getImageContainer().setBackground(background);
    imageContainer.saveImage(image);
    mapMenu.setButtonsDisabled(false);
    stage.sizeToScene();

    selectedLocations = new ArrayList<>();
    graph = new ListGraph<Location>();
    render();
  }

  public void addNewPlace() {
    isPlacingNode = true;

    imageContainer.getImageContainer().setCursor(Cursor.CROSSHAIR);
    mapMenu.setNewPlaceButtonDisabled(true);

    imageContainer.getImageContainer().setOnMouseClicked(e -> {
      if (!isPlacingNode)
        return;

      isPlacingNode = false;
      double x = e.getX();
      double y = e.getY();

      imageContainer.getImageContainer().setCursor(Cursor.DEFAULT);
      mapMenu.setNewPlaceButtonDisabled(false);

      TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle("Name");
      dialog.setContentText("Name of Place:");

      Optional<String> result = dialog.showAndWait();
      result.ifPresent(name -> {
        Location location = new Location(this, name, x, y);
        for (Location l : graph.getNodes()) {
          if (l.equals(location)) {
            Alert alert = new Alert(AlertType.ERROR, "Name is the same as a current node");
            alert.show();
            return;
          }
        }
        graph.add(location);
        isUnsaved = true;
        render();
      });
    });
  }

  public void addNewConnection() {
    if (selectedLocations.size() != 2)
      return;

    Location loc1 = selectedLocations.get(0);
    Location loc2 = selectedLocations.get(1);

    if (graph.getEdgeBetween(loc1, loc2) != null) {
      return;
    }
    Optional<String[]> result = connectionDialog("New Connection", "Add new connection", true, true, "", "");
    if (result.isEmpty())
      return;
    String[] res = result.get();

    graph.connect(loc1, loc2, res[0], Integer.parseInt(res[1]));
    isUnsaved = true;

    render();

    setIsMarked(loc1);
    setIsMarked(loc2);
  }

  public void showConnection() {
    if (selectedLocations.size() != 2)
      return;
    Location loc1 = selectedLocations.getFirst();
    Location loc2 = selectedLocations.getLast();

    if (graph.getEdgeBetween(loc1, loc2) == null)
      return;
    Edge<Location> edge = graph.getEdgeBetween(loc1, loc2);
    connectionDialog("Show Connection", "Connection from " + loc1.getName() + " to " + loc2.getName(), false, false,
        edge.getName(), "" + edge.getWeight());
    return;
  }

  public void changeConnection() {
    if (selectedLocations.size() != 2) {
      return;
    }
    Location loc1 = selectedLocations.getFirst();
    Location loc2 = selectedLocations.getLast();
    if (graph.getEdgeBetween(loc1, loc2) == null) {
      return;
    }

    Edge<Location> edge = graph.getEdgeBetween(loc1, loc2);
    Optional<String[]> result = connectionDialog("Change Connection",
        "Connection from " + loc1.getName() + " to " + loc2.getName(), false, true, edge.getName(),
        "" + edge.getWeight());
    if (result.isEmpty())
      return;
    edge.setWeight(Integer.parseInt(result.get()[1]));

    isUnsaved = true;

    render();
  }

  public void findPath() {
    if (selectedLocations.size() != 2) {
      return;
    }
    Location loc1 = selectedLocations.getFirst();
    Location loc2 = selectedLocations.getLast();
    List<Edge<Location>> path;
    if (!graph.pathExists(loc1, loc2)) {
      Alert alert = new Alert(AlertType.ERROR, "No path exists!!1!");
      alert.show();
      return;
    }
    path = graph.getPath(loc1, loc2);
    // From till Destination
    String output = "";
    double total = 0;
    for (Edge<Location> e : path) {
      output += "to " + e.getDestination().getName() + " by " + e.getName() + " takes " + e.getWeight() + "\n";
      total += e.getWeight();
    }
    output += "total " + total;
    Alert alert = new Alert(AlertType.INFORMATION, output);
    alert.setHeaderText("The Path from " + loc1.getName() + " to " + loc2.getName() + ":");
    alert.show();
  }

  public void render() {
    imageContainer.getImageContainer().getChildren().removeAll(imageContainer.getImageContainer().getChildren());
    for (Location loc : graph.getNodes()) {
      loc.setLayoutX(loc.getX());
      loc.setLayoutY(loc.getY());

      imageContainer.getImageContainer().getChildren().add(loc);

      for (Edge<Location> edge : graph.getEdgesFrom(loc)) {
        Location destination = (Location) edge.getDestination();
        Line line = new Line(loc.getX(), loc.getY(), destination.getX(), destination.getY());
        imageContainer.getImageContainer().getChildren().addFirst(line);
      }
    }
  }

  private Optional<String[]> connectionDialog(String title, String header, boolean isNameEditable,
      boolean isTimeEditable, String defaultStringText, String defaultTimeText) {

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
    TextField nameInputField = new TextField(defaultStringText);
    grid.add(nameInputField, 1, 0);
    grid.add(new Label("Time:"), 0, 1);
    TextField timeInputField = new TextField(defaultTimeText);
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
    String name;
    try {
      name = input[0].trim();
    } catch (Exception e) {
      return Optional.empty();
    }
    int weight = 0;

    if (name.isBlank()) {
      createErrorPopup(AlertType.ERROR, "Fel input i namn", "skriv in ett namn.");
      return Optional.empty();
    }
    try {
      weight = Integer.parseInt(input[1].trim());
    } catch (Exception e) {
      createErrorPopup(AlertType.ERROR, "Fel input i tid", "Skriv in ett nummer som tid.");
      return Optional.empty();
    }
    if (weight <= 0) {
      createErrorPopup(AlertType.ERROR, "Fel input i tid", "Tiden får inte vara 0 eller negativt.");
      return Optional.empty();
    }
    return Optional.ofNullable(input);
  }

  private void createErrorPopup(AlertType type, String headerText, String contentText) {
    Alert error = new Alert(type);
    error.setHeaderText(headerText);
    error.setContentText(contentText);
    error.showAndWait();
  }

  public boolean continueWithoutSaving() {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Warning");
    alert.setHeaderText("You have unsaved changes");
    alert.setContentText("Continue anyway?");

    Optional<ButtonType> result = alert.showAndWait();

    return result.get() == ButtonType.OK ? true : false;
  }

  public void saveMap() {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Save Map");
    dialog.setHeaderText("Save map to .graph file");
    dialog.setContentText("Filename: ");

    // Traditional way to get the response value.
    Optional<String> result = dialog.showAndWait();

    if (!result.isPresent())
      return;

    String fileName = result.get().trim();
    String imagePath = imageContainer.getImage().getUrl();

    Path path = Paths.get(System.getProperty("user.dir"), fileName + ".graph");

    File file = path.toFile();

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

      writer.write(imagePath);
      writer.newLine();
      for (Location loc : graph.getNodes())
        writer.write(loc.getName() + ";" + loc.getX() + ";" + loc.getY() + ";");
      for (Location loc : graph.getNodes()) {
        for (Edge<Location> edge : graph.getEdgesFrom(loc)) {
          writer.newLine();
          writer.write(edge.getSource().getName() + ";" + edge.getDestination().getName() + ";" + edge.getName() + ";"
              + edge.getWeight());
        }
      }
    } catch (Exception e) {
      System.out.println("Exception Här pls");
    }

    isUnsaved = false;
    Alert alert = new Alert(AlertType.CONFIRMATION, "YES!");
    alert.show();
  }

  public void openMap() {
    if (isUnsaved && !continueWithoutSaving()) {
      return;
    }

    // file
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("New Map");

    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("GRAPH", "*.graph"));
    File file = fileChooser.showOpenDialog(stage);
    if (file == null || !file.exists()) {
      return;
    }
    
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      File imageFile = new File(reader.readLine());
      loadMapImage(new Image(imageFile.toString()));
      
      // line 1
      Pattern pattern = Pattern.compile("(\\w+);(\\d+.\\d+);(\\d+.\\d+);");
      Matcher matcher = pattern.matcher(reader.readLine());

      while (matcher.find()) {
        String name = matcher.group(1);
        double x = Double.parseDouble(matcher.group(2));
        double y = Double.parseDouble(matcher.group(3));
        Location l = new Location(this, name, x, y);
        graph.add(l);
      }
      // line 2
      String line;
      while ((line = reader.readLine()) != null) {
        String[] e = line.split(";");
        Location l1 = graph.getNode(e[0]);
        Location l2 = graph.getNode(e[1]);
        String name = e[2];
        int weight = Integer.parseInt(e[3]);
        if (graph.getEdgeBetween(l1, l2) == null) {
          graph.connect(l1, l2, name, weight);
        }
      }
      render();
    } catch (Exception e) {
      System.out.println(e);
    }

  }

  public void saveImage() {
    try {
      WritableImage image = imageContainer.getImageContainer().snapshot(null, null);
      BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
      ImageIO.write(bufferedImage, "png", new File("../capture.png"));
    } catch (IOException e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "IO Error");
      alert.showAndWait();
    }
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
