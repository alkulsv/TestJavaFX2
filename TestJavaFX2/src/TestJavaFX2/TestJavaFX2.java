package TestJavaFX2;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TestJavaFX2 extends Application {

 public static void main(String[] args) {
     Application.launch(args);
 }

 private HBox taskbar;
 private StackPane view;
// private MediaPlayer mediaPlayer;

 @Override
 public void start(Stage stage) {
    
     stage.setTitle("Java FX Demo");

     BorderPane root = new BorderPane();
     Scene scene = new Scene(root, 720, 550, Color.LIGHTGRAY);
     stage.setScene(scene);

     taskbar = new HBox(10);
     taskbar.setPadding(new Insets(10, 30, 50, 30));
     taskbar.setPrefHeight(150);
     taskbar.setAlignment(Pos.CENTER);
     root.setBottom(taskbar);
     view = new StackPane();
     root.setCenter(view);
     view.getChildren().add(new Text("Hello from JavaFX..."));
     
//     mediaPlayer = new MediaPlayer(new Media("file:///c:/10.mp4"));
     taskbar.getChildren().add(createButton("/icon-1.png", new Runnable() {
             public void run() {
//                 changeView(new MediaView(mediaPlayer));
//                 mediaPlayer.play();
                 view.getChildren().clear(); // очищаем view
                 
                 final Rectangle rect1 = new Rectangle(0, 0, 100, 100);
                 rect1.setArcHeight(20);
                 rect1.setArcWidth(20);
                 rect1.setFill(Color.RED);
                 FadeTransition ft = new FadeTransition(Duration.millis(3000), rect1);
                 ft.setFromValue(1.0);
                 ft.setToValue(0.1);
                 ft.setCycleCount(Timeline.INDEFINITE);
                 ft.setAutoReverse(true);

                 Path path = new Path();
                 path.getElements().add(new MoveTo(20,20));
                 path.getElements().add(new CubicCurveTo(380, 0, 380, 120, 200, 120));
                 path.getElements().add(new CubicCurveTo(0, 120, 0, 240, 380, 240));
                 PathTransition pathTransition = new PathTransition();
                 pathTransition.setDuration(Duration.millis(4000));
                 pathTransition.setPath(path);
                 pathTransition.setNode(rect1);
                 pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
                 pathTransition.setCycleCount(Timeline.INDEFINITE);
                 pathTransition.setAutoReverse(true);
                 
                 view.getChildren().add(rect1);
                 ft.play();
                 pathTransition.play();                 
             }
         }));

     taskbar.getChildren().add(createButton("/icon-2.png", new Runnable() {

         public void run() {
        	 ObservableList<PieChart.Data> pieChartData =
                     FXCollections.observableArrayList(
                     new PieChart.Data("Grapefruit", 13),
                     new PieChart.Data("Oranges", 25),
                     new PieChart.Data("Plums", 10),
                     new PieChart.Data("Pears", 22),
                     new PieChart.Data("Apples", 30));
             final PieChart chart = new PieChart(pieChartData);
             chart.setTitle("Imported Fruits");
             changeView(chart);
         }
     }));

     taskbar.getChildren().add(createButton("/icon-3.png", new Runnable() {

         public void run() {
             Accordion accordion = new Accordion();
             for (int i = 1; i <= 4; i++) {
                 TitledPane t1 = new TitledPane("Image " + i,
                         new ImageView(new Image(getClass().getResource("/icon-" + i + ".png").toString())));
                 accordion.getPanes().add(t1);
             }
             changeView(accordion);
         }
     }));
     
     taskbar.getChildren().add(createButton("/icon-4.png", new Runnable() {
         public void run() {
             final WebView web = new WebView();
             final WebEngine we = web.getEngine();
             we.load("http://google.com");
             changeView(web);
         }
     }));

     taskbar.getChildren().add(createButton("/icon-5.png", new Runnable() {

         public void run() {
             ListView<String> listView = new ListView<String>();
             listView.setItems(FXCollections.observableArrayList(
                     "-fx-background-color: green;",
                     "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, aqua 0%, red 100%);",
                     "-fx-background-color: transparent;",
                     "-fx-opacity: 0.3;",
                     "-fx-opacity: 1;"));
             taskbar.styleProperty().bind(listView.getSelectionModel().selectedItemProperty());
             changeView(listView);
         }
     }));
     
     
     stage.show();
 }

private void changeView(Node node) {
    view.getChildren().clear();
//    mediaPlayer.stop();
    view.getChildren().add(node);
}
 
 private static final double SCALE = 1.3;
 private static final double DURATION = 300;

 private Node createButton(String iconName, final Runnable action) {
     ImageView node = new ImageView(new Image(getClass().getResource(iconName).toString()));
     final ScaleTransition animationGrow = new ScaleTransition(Duration.millis(DURATION), node);
     animationGrow.setToX(SCALE);
     animationGrow.setToY(SCALE);
     final ScaleTransition animationShrink = new ScaleTransition(Duration.millis(DURATION), node);
     animationShrink.setToX(1);
     animationShrink.setToY(1);
     final Reflection effect = new Reflection();
     node.setEffect(effect);
     node.setOnMouseClicked(new EventHandler<MouseEvent>() {
         public void handle(MouseEvent event) {
             action.run();
         }
     });
     node.setOnMouseEntered(new EventHandler<MouseEvent>() {
         public void handle(MouseEvent event) {
             animationShrink.stop();
             animationGrow.playFromStart();
         }
     });
     
     node.setOnMouseExited(new EventHandler<MouseEvent>() {
         public void handle(MouseEvent event) {
             animationGrow.stop();
             animationShrink.playFromStart();
         }
     });
     return node;
 } 
}
