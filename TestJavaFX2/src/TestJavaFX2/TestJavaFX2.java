package TestJavaFX2;

import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import com.github.sarxos.webcam.Webcam;
import com.jhlabs.image.ContrastFilter;
import com.jhlabs.image.GammaFilter;
import com.jhlabs.image.ThresholdFilter;

public class TestJavaFX2 extends Application {

 public static void main(String[] args) {
     Application.launch(args);
 }

 private HBox taskbar;
 private AnchorPane view;
 private BufferedImage grabbedImage;
 private Webcam webcam; 
 private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
 private ObjectProperty<Image> imageGammaProperty = new SimpleObjectProperty<Image>();
 private ImageView imageView;
 private float gradientValue = 1.0f;
 private ImageView imageGammaView;
 private boolean stopCamera = true;

 @Override
 public void start(Stage stage) {
    
     stage.setTitle("OCR Test");
     stage.setWidth(770);
     stage.setHeight(730);

     BorderPane root = new BorderPane();
     Scene scene = new Scene(root, 720, 550, Color.LIGHTGRAY);
     stage.setScene(scene);

     stage.setOnHiding(new EventHandler<WindowEvent>() {

         @Override
         public void handle(WindowEvent event) {
             Platform.runLater(new Runnable() {

                 @Override
                 public void run() {
                     if(!stopCamera){
                    	 webcam.close();
                     }
                     System.exit(0);
                 }
             });
         }
     });
     
     taskbar = new HBox(10);
     taskbar.setPadding(new Insets(15, 30, 50, 30));
     taskbar.setPrefHeight(150);
     taskbar.setAlignment(Pos.CENTER);
     root.setBottom(taskbar);
//     view = new StackPane();
   view = new AnchorPane();
     root.setCenter(view);
     view.getChildren().add(new Text("Test OCR"));
		webcam = Webcam.getDefault();
     
     taskbar.getChildren().add(createButton("/icon-1.png", new Runnable() {
         	 public void run() {
         		 		
         		 		if(!stopCamera) return;
         		 
         		 		view.getChildren().clear();
                 		Dimension size = new Dimension(320, 240);
                 		webcam.setViewSize(size);
                 		webcam.open();
                 		imageView = new ImageView();
                        view.getChildren().add(imageView);
                        AnchorPane.setTopAnchor(imageView, 10.0);
                        AnchorPane.setLeftAnchor(imageView, 50.0);
                        
                 		imageGammaView = new ImageView();
                        view.getChildren().add(imageGammaView);
                        AnchorPane.setTopAnchor(imageGammaView, 10.0);
                        AnchorPane.setLeftAnchor(imageGammaView, 400.0);
                        
                        Slider slider = new Slider();
                        slider.setMin(0);
                        slider.setMax(2.0);
                        slider.setValue(gradientValue);
                        slider.setShowTickLabels(true);
                        slider.setShowTickMarks(true);
                        slider.setMajorTickUnit(1.0);
                        slider.setMinorTickCount(1);
                        slider.setBlockIncrement(0.1);
                        view.getChildren().add(slider);
                        AnchorPane.setTopAnchor(slider, 400.0);
                        AnchorPane.setLeftAnchor(slider, 320.0);
                        
                        final Label GradientValueLabel = new Label(Double.toString(slider.getValue()));
                        view.getChildren().add(GradientValueLabel);
                        AnchorPane.setTopAnchor(GradientValueLabel, 380.0);
                        AnchorPane.setLeftAnchor(GradientValueLabel, 340.0);
                        
                        
                        slider.valueProperty().addListener(new ChangeListener<Number>() {
                            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                            		gradientValue = new_val.floatValue();
                                    GradientValueLabel.setText(String.format("%.1f", new_val));
                            }
                        });
                        
/*                        Rectangle rect = new Rectangle(0, 0, 400, 100);
                        rect.setOpacity(0.1);
                        rect.setStroke(Color.WHITE);
                        rect.setStrokeWidth(2);
                        view.getChildren().add(rect);
                        AnchorPane.setTopAnchor(rect, 160.0);
                        AnchorPane.setLeftAnchor(rect, 200.0);*/
                        
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
                									Image imageGamma = SwingFXUtils.toFXImage(Filter(grabbedImage), null);
                									imageProperty.set(mainiamge);
                									imageGammaProperty.set(imageGamma);
                								}
                							});
                							grabbedImage.flush();
                							try {
                							    TimeUnit.MILLISECONDS.sleep(700);
                							} catch (InterruptedException e) {
                							    //Handle exception
                							}
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
              		imageGammaView.imageProperty().bind(imageGammaProperty);
       
             }
         }));

     taskbar.getChildren().add(createButton("/snapshoot.png", new Runnable() {

         public void run() {

       	    view.getChildren().clear();

       	    if(stopCamera)
        	{
       	    	Text t = new Text();
      	    	t.setText("Pls start camera first.");
      	    	t.setFont(new Font(26));
                AnchorPane.setTopAnchor(t, 200.0);
                AnchorPane.setLeftAnchor(t, 220.0);
                view.getChildren().add(t);
                return;
        	}
       	    
			grabbedImage = webcam.getImage();
        	stopCamera = true;
	/*		
	        ContrastFilter contrast = new ContrastFilter();
	        BufferedImage dest1=contrast.createCompatibleDestImage(grabbedImage,null);
	        contrast.filter(grabbedImage, dest1);
	        
	        GammaFilter   gamma = new GammaFilter();
	        BufferedImage dest2 = gamma.createCompatibleDestImage(dest1,null);
	        gamma.setGamma(1.1f);
	        gamma.filter(dest1, dest2);
	        
	        ThresholdFilter threshold = new ThresholdFilter();
	        BufferedImage dest3 = threshold.createCompatibleDestImage(dest2,null);
	        threshold.filter(dest2, dest3);

	        */
        	BufferedImage filteredimage = Filter(grabbedImage);
/*	        InvertFilter invert = new InvertFilter();
	        BufferedImage dest4 = invert.createCompatibleDestImage(dest3,null);
	        invert.filter(dest3, dest4);*/

	        
			Image mainiamge = SwingFXUtils.toFXImage(grabbedImage, null);
			Image finalimage = SwingFXUtils.toFXImage(filteredimage, null);
			
	        WritableImage crop = new WritableImage(finalimage.getPixelReader(),150, 150, 50, 50);

     		imageView = new ImageView(mainiamge);
     		ImageView finalImage = new ImageView(finalimage);
     		ImageView cropImage = new ImageView(crop);
            view.getChildren().add(imageView);
            view.getChildren().add(finalImage);
            view.getChildren().add(cropImage);
            AnchorPane.setBottomAnchor(cropImage, 100.0);
            AnchorPane.setLeftAnchor(cropImage, 170.0);
//            view.setAlignment(cropImage, Pos.BOTTOM_CENTER);
            Ocr ocr = new Ocr();
            ocr.loadTrainingImages();
            String ocrstring = ocr.process(SwingFXUtils.fromFXImage(crop, null));
            

  	    	Text t = new Text();
  	    	t.setText("OCR result: " + ocrstring);
  	    	t.setFont(new Font(30));
            view.getChildren().add(t);
            AnchorPane.setBottomAnchor(t, 20.0);
            AnchorPane.setLeftAnchor(t, 250.0);

            Path path = new Path();
            path.getElements().add(new MoveTo(340, 250));
            path.getElements().add(new LineTo(200,150));
            PathTransition pathTransition = new PathTransition();
            pathTransition.setDuration(Duration.millis(4000));
            pathTransition.setPath(path);
            pathTransition.setNode(imageView);
            pathTransition.play();
            ScaleTransition shrink = new ScaleTransition(Duration.millis(3000), imageView);
            shrink.setToX(0.5);
            shrink.setToY(0.5);
            shrink.play();
            
            Path path2 = new Path();
            path2.getElements().add(new MoveTo(340, 250));
            path2.getElements().add(new LineTo(550,150));
            PathTransition pathTransition2 = new PathTransition();
            pathTransition2.setDuration(Duration.millis(4000));
            pathTransition2.setPath(path2);
            pathTransition2.setNode(finalImage);
            pathTransition2.play();                 
            
            
            ScaleTransition shrink2 = new ScaleTransition(Duration.millis(3000), finalImage);
            shrink2.setToX(0.5);
            shrink2.setToY(0.5);
            shrink2.play();
         }
     }));
/*     
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

*/
     taskbar.getChildren().add(createButton("/icon-3.png", new Runnable() {

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
 
 private BufferedImage Filter(BufferedImage imagein) {

     ContrastFilter contrast = new ContrastFilter();
     BufferedImage contrastimage=contrast.createCompatibleDestImage(imagein,null);
     contrast.filter(imagein, contrastimage);
     
     GammaFilter   gamma = new GammaFilter();
     BufferedImage gammaimage = gamma.createCompatibleDestImage(contrastimage,null);
     gamma.setGamma(gradientValue);
     gamma.filter(contrastimage, gammaimage);
     
     ThresholdFilter threshold = new ThresholdFilter();
     BufferedImage filteredimage = threshold.createCompatibleDestImage(gammaimage,null);
     threshold.filter(gammaimage, filteredimage);
     return filteredimage;
 }
 
 
}
