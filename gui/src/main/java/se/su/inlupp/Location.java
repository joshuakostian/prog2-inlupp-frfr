package se.su.inlupp;

import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.paint.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Location extends Pane {
  private Circle circle;
  private Label label;
  private double x;
  private double y;
  private final int size = 10;
  private boolean isMarked = false;

  public Location(AppController ctrl, String name, double x, double y) {
    this.x = x;
    this.y = y;

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

    // circle.setOnMouseClicked(click -> {
    // if (ctrl.getSelection().size() >= 2 && !isMarked)
    // return;
    // setIsMarked(!isMarked, ctrl);
    // });
    //
    circle.setOnMouseClicked(e -> ctrl.setIsMarked(this));

    getChildren().addAll(circle, label);
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

  // public void setIsMarked(boolean b, AppController ctrl) {
  // isMarked = b;
  // if (isMarked) {
  // circle.setFill(Color.BLUE);
  // ctrl.addSelection(this);
  // } else {
  // circle.setFill(Color.CRIMSON);
  // ctrl.removeSelection(this);
  // }
  // }
}
