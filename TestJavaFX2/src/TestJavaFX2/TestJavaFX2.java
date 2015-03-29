package TestJavaFX2;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Dimension;
//import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
//import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;

public class TestJavaFX2 extends Application {

 public static void main(String[] args) {
     Application.launch(args);
 }

 private HBox taskbar;
 private StackPane view;
 private BufferedImage grabbedImage;
 private Webcam webcam; 
 private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
 private ImageView imageView;
 private boolean stopCamera = false;

// private MediaPlayer mediaPlayer;

 @Override
 public void start(Stage stage) {
    
     stage.setTitle("OCR Test");
     stage.setWidth(800);
     stage.setHeight(900);

     BorderPane root = new BorderPane();
     Scene scene = new Scene(root, 720, 550, Color.LIGHTGRAY);
     stage.setScene(scene);

     taskbar = new HBox(10);
     taskbar.setPadding(new Insets(15, 30, 50, 30));
     taskbar.setPrefHeight(150);
     taskbar.setAlignment(Pos.CENTER);
     root.setBottom(taskbar);
     view = new StackPane();
     root.setCenter(view);
     view.getChildren().add(new Text("Test OCR"));
		webcam = Webcam.getDefault();
     
     taskbar.getChildren().add(createButton("/icon-1.png", new Runnable() {
         	 public void run() {
                 view.getChildren().clear(); // очищаем view

                 		Dimension size = new Dimension(320, 240);
                 		webcam.setViewSize(size);
                 		webcam.open();
                 		imageView = new ImageView();
                        view.getChildren().add(imageView);
                        
                        stopCamera = false;
                		Task<Void> task = new Task<Void>() {

                			@Override
                			protected Void call() throws Exception {
                				
                				while (!stopCamera) {
                					try {
                						if ((grabbedImage = webcam.getImage()) != null) {
                							Platform.runLater(new Runnable() {
                								@Override
                								public void run() {
                									Image mainiamge = SwingFXUtils.toFXImage(grabbedImage, null);
                									imageProperty.set(mainiamge);
                								}
                							});
                							grabbedImage.flush();
                						}
                					} catch (Exception e) {
                						e.printStackTrace();
                					}
                				}
                				webcam.close();
                				return null;
                			}
                		};

                		Thread th = new Thread(task);
                		th.setDaemon(true);
                		th.start();
                		imageView.imageProperty().bind(imageProperty);
       
             }
         }));

     taskbar.getChildren().add(createButton("/snapshoot.png", new Runnable() {

         public void run() {
        	stopCamera = true;
      		webcam.close();
       		Dimension size = new Dimension(640, 480);
      		webcam.setViewSize(size);
      		webcam.open();
			grabbedImage = webcam.getImage();
			Image mainiamge = SwingFXUtils.toFXImage(grabbedImage, null);
     		imageView = new ImageView();
			imageProperty.set(mainiamge);
    		imageView.imageProperty().bind(imageProperty);
      		webcam.close();
            changeView(imageView);
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
    stopCamera = true;
    view.getChildren().add(node);
}
 
 private static final double SCALE = 1.1;
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
