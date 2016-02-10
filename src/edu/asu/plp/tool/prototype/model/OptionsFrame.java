package edu.asu.plp.tool.prototype.model;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import moore.fx.components.Components;
 
public abstract class OptionsFrame extends Pane {
   
	public static void options() {
		Stage optionsWindowStage = new Stage();
		Parent myPane = optionFrameTabs();
		Scene scene = new Scene(myPane, 450, 400);
		optionsWindowStage.setTitle("PLP Options");
		optionsWindowStage.setScene(scene);
		optionsWindowStage.setResizable(false);
		optionsWindowStage.show();
		}

        private static Parent optionFrameTabs() {
        BorderPane border = new BorderPane();
		border.setPadding(new Insets(20));
		GridPane grid = new GridPane();
		HBox buttons = new HBox(10);
		grid.setHgap(10);
		grid.setVgap(30);
		grid.setPadding(new Insets(0, 0, 0, 0));

          TabPane tabPane = new TabPane();
		 
		  //font Tabs
		  Tab fontTab = new Tab();
		  fontTab.setText("Font");
		  VBox font_vBox = new VBox();
		  CheckBox cbf = new CheckBox("Enable sysntax highlighting");
		  cbf.setStyle("-fx-padding: 2;");
		  cbf.setIndeterminate(false);
		  cbf.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
		  });
		  
		  Label fontlabel = new Label("Font:");
		  fontlabel.setStyle("-fx-padding: 2;");
		  
		    String Monospaced ="Monospaced";
			ComboBox<String> comboBox = new ComboBox<String>();
			comboBox.getItems().addAll(Monospaced);
			comboBox.setValue(Monospaced);
			comboBox.setOnAction((event) ->{
				// TODO: Add Event for Checkbox
			  });
			
		   
		   Label fontlabel2 = new Label("Font size:");
           fontlabel2.setStyle("-fx-padding: 2;");
           
			   String num ="8";
			   String num2 ="10";
			   String num3 ="12";
				ComboBox<String> comboBox2 = new ComboBox<String>();
				comboBox2.getItems().addAll(num,num2,num3);
				comboBox2.setValue(num3);
				comboBox2.setOnAction((event) ->{
					// TODO: Add Event for comboBox
				  });
				
				TextArea fontText = new TextArea();
				fontText.setText("The quick brown fox jumps over the lazy dog");
				fontText.setOnScroll((event) ->{
					// TODO: Add Event for comboBox
				  });
				
		  font_vBox.getChildren().addAll(cbf,fontlabel,comboBox,fontlabel2,comboBox2,fontText);
		  fontTab.setContent(font_vBox);
		  tabPane.getTabs().add(fontTab);
		 
		 // Simulator Tab
		  Tab tabSimulator = new Tab();
		  tabSimulator.setText("Simulator");
		  VBox simulator_vBox = new VBox();
		  
		  Label simulatorlabel = new Label("Simulation speed (millisecond/cycle):");
		  simulatorlabel.setStyle("-fx-padding: 2;");
		  
		  Slider slider = new Slider();
		  slider.setShowTickMarks(true);
		  slider.setShowTickLabels(true);
		  slider.setMajorTickUnit(100f);
		  slider.setMax(1000);
		  slider.setMinorTickCount(50);
		  slider.setValue(100);
		  slider.setStyle("-fx-padding: 10;");
		  slider.setOnMouseMoved((event) ->{
			// TODO: Add Event for slider
		  });
		  
		  CheckBox cbs = new CheckBox("Allow execution of none-instruction (error #270)");
		  cbs.setIndeterminate(false);
		  cbs.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
		  });
		  
		  CheckBox cbs2 = new CheckBox("Assume zero on reads from uninitialized memory(error #257)");
		  cbs2.setIndeterminate(false);
		  cbs2.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
		  });
		  
		  CheckBox cbs3 = new CheckBox("Print extra information on evaluation errors");
		  cbs3.setIndeterminate(false);
		  cbs3.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
		  });
		  
		  CheckBox cbs4 = new CheckBox("Refersh IDE when simulation is being run");
		  cbs4.setIndeterminate(false);
		  cbs4.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
		  });
		  
		  CheckBox cbs5 = new CheckBox("Highlight instruction line pointed by PC");
		  cbs5.setIndeterminate(false);
		  cbs5.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
		  });
		  
		  CheckBox cbs6 = new CheckBox("Functional simulation");
		  cbs6.setIndeterminate(false);
		  cbs6.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
		  });
		  
		  simulator_vBox.getChildren().addAll(simulatorlabel,slider,cbs,cbs2,cbs3,cbs4,cbs5,cbs6);
		  simulator_vBox.setSpacing(10);
		  tabSimulator.setContent(simulator_vBox);
		  tabPane.getTabs().add(tabSimulator);
		  //Programer Tab
		  Tab tabProgramer = new Tab();
		  tabProgramer.setText("Programer");
		  VBox programer_vBox = new VBox();
		  
		  CheckBox cbp = new CheckBox("Program in chunks");
		  cbp.setStyle("-fx-padding: 2;");
		  cbp.setIndeterminate(false);
		  cbp.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
		  });
		  
		  Label programlabel = new Label("Maximum chunk size:");
		  programlabel.setStyle("-fx-padding: 2;");
		  
		  TextField programtext = new TextField ();
		  programtext.setText("2048");
		  programtext.setStyle("-fx-padding: 2;");
		  programtext.setOnAction((event) ->{
				// TODO: Add Event 
			  });
		  
	      Label programlabel2 = new Label("Receive timeout(ms):");
	      
	      TextField programtext2 = new TextField ();
		  programtext2.setText("500");
		  programtext2.setStyle("-fx-padding: 2;");
		  programtext2.setOnAction((event) ->{
			// TODO: Add Event for  TextField
		  });
		  
		  Label programlabel3 = new Label("Changing any of the values above may break the programmer");
		  programlabel.setStyle("-fx-padding: 2;");
		  
		  CheckBox cbp2 = new CheckBox("Autodetect Serial Ports");
		  cbp2.setIndeterminate(false);
		  cbp2.setStyle("-fx-padding: 2;");
		  cbp2.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
		  });
		  
		  programer_vBox.getChildren().addAll(cbp,programlabel,programtext,programlabel2,programtext2, programlabel3, cbp2);
		  programer_vBox.setSpacing(10);
		  tabProgramer.setContent(programer_vBox);
		  tabPane.getTabs().add(tabProgramer);
		  
		  //Miscellaneous Tab
		  
		  Tab tabMiscellaneous = new Tab();
		  tabMiscellaneous.setText("Miscellaneous");
		  VBox miscellaneous_vBox = new VBox();
		  
		  CheckBox cbm = new CheckBox("Ask before autoloading modules during startup");
		  cbm.setIndeterminate(false);
		  cbm.setStyle("-fx-padding: 10;");
		  cbm.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
		  });
		  
		  CheckBox cbm2 = new CheckBox("Ask for ISA for new projects");
		  cbm2.setIndeterminate(false);
		  cbm2.setStyle("-fx-padding: 10;");
		  cbm.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
		  });
		  
		  miscellaneous_vBox.getChildren().addAll(cbm,cbm2);
		  miscellaneous_vBox.setSpacing(10);
		  tabMiscellaneous.setContent(miscellaneous_vBox);
		  tabPane.getTabs().add(tabMiscellaneous);
		  
		  // View Tab
		  Tab tabView = new Tab();
		  tabView.setText("View");
		  VBox view_vbox = new VBox();
		  CheckBox cbv = new CheckBox("Disable the view of Project Explorer");
		  cbv.setIndeterminate(false);
		  cbv.setStyle("-fx-padding: 10;");
		  cbv.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
		  });
		  
		  CheckBox cbv2 = new CheckBox("Disable the view of Toolbar");
		  cbv2.setIndeterminate(false);
		  cbv2.setStyle("-fx-padding: 10;");
		  cbv2.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
		  });
		  
		  CheckBox cbv3 = new CheckBox("Disable the view of console");
		  cbv3.setIndeterminate(false);
		  cbv3.setStyle("-fx-padding: 10;");
		  cbv3.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
		  });
		  
		  view_vbox.getChildren().addAll(cbv,cbv2,cbv3);
		  view_vbox.setSpacing(10);
		  tabView.setContent(view_vbox);
		
		  tabPane.getTabs().add(tabView);
		  tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
        Button createProject = new Button("Apply");
		createProject.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{ // TODO: Add Event for Button
				}
			
			});

		createProject.setDefaultButton(true);
		Button cancelCreate = new Button("Close");
		cancelCreate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				Stage stage = (Stage) cancelCreate.getScene().getWindow();
				stage.close();
			}
		});

		grid.add(tabPane, 0, 0);
		border.setCenter(grid);

		buttons.getChildren().addAll(createProject, cancelCreate);
		buttons.setAlignment(Pos.BASELINE_RIGHT);
		border.setBottom(buttons);

		return Components.wrap(border);

		}
}