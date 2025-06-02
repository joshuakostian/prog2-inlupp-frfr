package se.su.inlupp;

import javafx.scene.layout.Pane;

import java.util.Comparator;

import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.paint.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Location extends Pane {
  private Circle circle;
  private Label label;
  private String name;
  private double x;
  private double y;
  private final int size = 10;
  private boolean isMarked = false;

  public Location(AppController ctrl, String name, double x, double y) {
    this.x = x;
    this.y = y;
    this.name = name;

    this.setPrefSize(size, size);
    this.setMinSize(size, size);
    this.setMaxSize(size, size);

    this.circle = new Circle(size, Color.CRIMSON);

    this.label = new Label(name);
    this.label.setFont(Font.font("System", FontWeight.BOLD, 14));
    this.label.setTextFill(Color.BLACK);
    this.label.setLayoutX(-20);
    this.label.setLayoutY(-30);
    // fyfan vad jag hatar dropshadow. dumma carl!!
    this.label.setEffect(new DropShadow(2, Color.WHITESMOKE));

    circle.setOnMouseClicked(e -> ctrl.setIsMarked(this));

    getChildren().addAll(circle, label);
  }

  public String getName() {
    return name;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public boolean getIsMarked() {
    return isMarked;
  }

  public void changeMarkedStatus() {
    isMarked = !isMarked;
  }

  public void setColor(Color color) {
    circle.setFill(color);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true; // samma objekt
    if (o == null || getClass() != o.getClass())
      return false;
    Location other = (Location) o;

    if (this.label.getText().equalsIgnoreCase(other.label.getText())) {
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return label.getText().toLowerCase().hashCode();
  }

  @Override
  public String toString() {
    return name;
  }
}
