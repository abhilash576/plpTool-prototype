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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import moore.fx.components.Components;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.asu.plp.tool.prototype.view.ConsolePane;


public abstract class OptionsFrame extends Pane {
	
	static CheckBox fontCheckBox = new CheckBox();
	static Label fontNamelabel = new Label();
	static ComboBox<String> cmbFontName = new ComboBox<String>();
	static Label fontSizelabel = new Label();
	static ComboBox<Integer> cmbFontSize = new ComboBox<Integer>();
    static TextArea txtFontPreview = new TextArea();
    static Label simulatorlabel = new Label();
    static Slider slider = new Slider();
    static CheckBox cbs = new CheckBox();
    static CheckBox cbs2 = new CheckBox();
    static CheckBox cbs3 = new CheckBox();
    static CheckBox cbs4 = new CheckBox();
    static CheckBox cbs5 = new CheckBox();
    static CheckBox cbs6 = new CheckBox();
    static CheckBox cbp = new CheckBox();
    static Label programlabel = new Label();
    static TextField programtext = new TextField ();
    static Label programlabel2 = new Label();
    static TextField programtext2 = new TextField ();
    static Label programlabel3 = new Label();
    static CheckBox cbp2 = new CheckBox();
    static CheckBox cbm = new CheckBox();
    static CheckBox cbm2  = new CheckBox();
    static CheckBox cbv = new CheckBox();
    static CheckBox cbv2 = new CheckBox();
	static CheckBox cbv3 = new CheckBox();
	
	public static void options() {
		Stage optionsWindowStage = new Stage();
		Parent myPane = optionFrameTabs();
		Scene scene = new Scene(myPane, 450, 400);
		optionsWindowStage.setTitle("PLP Options");
		optionsWindowStage.setScene(scene);
		optionsWindowStage.setResizable(false);
		optionsWindowStage.show();
		}

        private static  Parent optionFrameTabs() {
        BorderPane border = new BorderPane();
		border.setPadding(new Insets(20));
		GridPane grid = new GridPane();
		HBox buttons = new HBox(10);
		grid.setHgap(10);
		grid.setVgap(30);
		
        TabPane tabPane = new TabPane();
        AnchorPane fontAnchorPane = new AnchorPane();
		  //font Tabs
          Tab fontTab = new Tab();
		  fontTab.setText("Font");
		 
		  // font first check box
		  fontCheckBox.setText("Enable sysntax highlighting");
		  fontCheckBox.setIndeterminate(false);
		  AnchorPane.setTopAnchor(fontCheckBox, 10.0);
		  AnchorPane.setLeftAnchor(fontCheckBox, 10.0);
	      fontCheckBox.setOnAction((event) ->{
			// TODO: Add Event for Checkbox
			  boolean selected = fontCheckBox.isSelected();
		
			 });
		 //font name label
		  fontNamelabel.setText("Font:");
		  AnchorPane.setTopAnchor(fontNamelabel, 50.0);
		  AnchorPane.setLeftAnchor(fontNamelabel, 10.0);
		  
		   // font name ComboBox<
		  String Monospaced ="Monospaced";
		  List<String> familiesList = Font.getFontNames();
	      ObservableList<String> familiesObservableList = 
	                FXCollections.observableArrayList(familiesList);
	      cmbFontName.setItems(familiesObservableList);
	      cmbFontName.setValue(Monospaced);
	      cmbFontName.setEditable(true);  
	      cmbFontName.setPrefWidth(320);
	      AnchorPane.setTopAnchor(cmbFontName, 50.0);
		  AnchorPane.setLeftAnchor(cmbFontName, 75.0);
		  cmbFontName.valueProperty().addListener(new ChangeListener<String>() {
       	   @Override 
       	   public void changed(ObservableValue ov, String t, String t1) { 
       	   txtFontPreview.setFont(new Font(t1, Integer.parseInt(cmbFontSize.getValue().toString()))); 
       	   } 
       	   });
	        
	       // font size label
	       fontSizelabel.setText("Font Size:");
	       AnchorPane.setTopAnchor(fontSizelabel, 100.0);
		   AnchorPane.setLeftAnchor(fontSizelabel, 10.0);
           //ComboBox<Integer> cmbFontSize = new ComboBox<Integer>();
           int num = 8;
           int num2 = 10;
           int num3 = 11;
           int num4 = 12;
           int num5 = 13;
           int num6 = 14;
           int num7 = 16;
           int num8 = 24;
           int num9 = 36;
           int num10 = 48;
           int num11 = 72;
           cmbFontSize.getItems().addAll(num, num2, num3,num4,num5,num6,num7,num8,num9,num10,num11);
           cmbFontSize.setEditable(true);  
           cmbFontSize.setValue(num4);
           cmbFontSize.setPrefWidth(320);
           AnchorPane.setTopAnchor(cmbFontSize, 100.0);
		   AnchorPane.setLeftAnchor(cmbFontSize, 75.0);
		   cmbFontSize.valueProperty().addListener(new ChangeListener<Integer>() {
        	   public void changed(ObservableValue ov, Integer  t, Integer  t1) { 
        	   txtFontPreview.setFont(new Font(cmbFontName.getValue().toString(),t1)); 
        	   }

        	    });
           
				// Text Font Preview 
			//TextArea txtFontPreview = new TextArea();
				txtFontPreview.setText("The quick brown fox jumps over the lazy dog");
				txtFontPreview.setPrefSize(400,140);
				txtFontPreview.setFont(new Font("Monospaced", 12));
				//AnchorPane.setBottomAnchor(txtFontPreview, 20.0);
				AnchorPane.setTopAnchor(txtFontPreview, 150.0);
				AnchorPane.setLeftAnchor(txtFontPreview, 10.0);
				
	        fontAnchorPane.getChildren().addAll(fontCheckBox,fontNamelabel,cmbFontName,fontSizelabel,cmbFontSize,txtFontPreview);
		    fontTab.setContent(fontAnchorPane);
		    tabPane.getTabs().add(fontTab);
		 
		  
		 // Simulator Tab
		  Tab tabSimulator = new Tab();
		  tabSimulator.setText("Simulator");
		  AnchorPane  simulatorAnchorPane= new AnchorPane();
		  
		  // simulator label
		  simulatorlabel.setText("Simulation speed (millisecond/cycle):");
		  AnchorPane.setTopAnchor(simulatorlabel, 10.0);
		  AnchorPane.setLeftAnchor(simulatorlabel, 10.0);
		  
		  // simulator slider
		  slider.setShowTickMarks(true);
		  slider.setShowTickLabels(true);
		  slider.setMajorTickUnit(100f);
		  slider.setMax(1000);
		  slider.setMinorTickCount(50);
		  slider.setValue(100);
		  AnchorPane.setLeftAnchor(slider, 10.0);  
	      AnchorPane.setTopAnchor(slider, 2.0);  
	      AnchorPane.setRightAnchor(slider, 10.0);  
	      AnchorPane.setBottomAnchor(slider, 180.0); 
		  slider.setOnMouseMoved((event) ->{
			// TODO: Add Event for slider
		  });
		  
		  
		   // Allow execution of none-instruction (error #270) checkBox 
		  cbs.setText("Allow execution of none-instruction (error #270)");
		  cbs.setIndeterminate(false);
		  AnchorPane.setTopAnchor(cbs, 100.0);
		  AnchorPane.setLeftAnchor(cbs, 10.0);
		
		  // Assume zero on reads from uninitialized memory checkBox
		  cbs2.setText("Assume zero on reads from uninitialized memory(error #257)");
		  cbs2.setIndeterminate(false);
		  AnchorPane.setTopAnchor(cbs2, 130.0);
		  AnchorPane.setLeftAnchor(cbs2, 10.0);
		  
		  // Print extra information on evaluation errors checkBox
		  cbs3.setText("Print extra information on evaluation errors");
		  cbs3.setIndeterminate(false);
		  AnchorPane.setTopAnchor(cbs3, 160.0);
		  AnchorPane.setLeftAnchor(cbs3, 10.0);
		  
		  // Refersh IDE when simulation is being run checKBox
		  cbs4.setText("Refersh IDE when simulation is being run");
		  cbs4.setIndeterminate(false);
		  AnchorPane.setTopAnchor(cbs4, 190.0);
		  AnchorPane.setLeftAnchor(cbs4, 10.0);
		  
		  // Highlight instruction line pointed by PC checkBox
		  cbs5.setText("Highlight instruction line pointed by PC");
		  cbs5.setIndeterminate(false);
		  AnchorPane.setTopAnchor(cbs5, 220.0);
		  AnchorPane.setLeftAnchor(cbs5, 10.0);
		 
		  // Functional simulation CheckBox
		  cbs6.setText("Functional simulation");
		  cbs6.setIndeterminate(false);
		  AnchorPane.setTopAnchor(cbs6, 250.0);
		  AnchorPane.setLeftAnchor(cbs6, 10.0);
		  
		  simulatorAnchorPane.getChildren().addAll(simulatorlabel,slider,cbs,cbs2,cbs3,cbs4,cbs5,cbs6);
	      tabSimulator.setContent(simulatorAnchorPane);
		  tabPane.getTabs().add(tabSimulator);
		  
		  //Programer Tab
		  Tab tabProgramer = new Tab();
		  tabProgramer.setText("Programer");
          AnchorPane programerAnchorPane= new AnchorPane();
		  
          // Program in chunks CheckBox
		  cbp.setText("Program in chunks");
		  cbp.setIndeterminate(false);
          AnchorPane.setTopAnchor(cbp, 10.0);
		  AnchorPane.setLeftAnchor(cbp, 10.0);
		  
		 
		  // Maximum chunk size label
		  programlabel.setText("Maximum chunk size:");
		  AnchorPane.setTopAnchor(programlabel, 40.0);
		  AnchorPane.setLeftAnchor(programlabel, 10.0);
		  
		  // Maximum chunk size text field
		  programtext.setText("2048");
		  programtext.setPrefWidth(250);
		  AnchorPane.setTopAnchor(programtext, 40.0);
		  AnchorPane.setLeftAnchor(programtext, 150.0);
		  programtext.setOnAction((event) ->{
				// TODO: Add Event 
			  });
		  
		  // Receive timeout label
	      programlabel2.setText("Receive timeout(ms):");
	      AnchorPane.setTopAnchor(programlabel2, 80.0);
		  AnchorPane.setLeftAnchor(programlabel2, 10.0);
		  
		  // Receive timeout text field
	      programtext2.setText("500");
		  programtext2.setPrefWidth(250);
		  AnchorPane.setTopAnchor(programtext2, 80.0);
		  AnchorPane.setLeftAnchor(programtext2, 150.0);
		  programtext2.setOnAction((event) ->{
			// TODO: Add Event for  TextField
		  });
		  
		  
		  Label programlabel3 = new Label("Changing any of the values above may break the programmer");
		  AnchorPane.setTopAnchor(programlabel3, 120.0);
		  AnchorPane.setLeftAnchor(programlabel3, 10.0);
		  
		  // Autodetect Serial Ports CheckBox
		  cbp2.setText("Autodetect Serial Ports");
		  cbp2.setIndeterminate(false);
		  AnchorPane.setTopAnchor(cbp2, 160.0);
		  AnchorPane.setLeftAnchor(cbp2, 10.0);
		  
		  programerAnchorPane.getChildren().addAll(cbp,programlabel,programtext,programlabel2,programtext2, programlabel3, cbp2);
		  tabProgramer.setContent(programerAnchorPane);
		  tabPane.getTabs().add(tabProgramer);
		  
		  
		  //Miscellaneous Tab
		  Tab tabMiscellaneous = new Tab();
		  tabMiscellaneous.setText("Miscellaneous");
		  AnchorPane miscellaneousAnchorPane = new AnchorPane();
		  
		  // Miscellaneous tab first CheckBox
		  cbm.setText("Ask before autoloading modules during startup");
		  cbm.setIndeterminate(false);
		  AnchorPane.setTopAnchor(cbm, 10.0);
		  AnchorPane.setLeftAnchor(cbm, 10.0);
		 
		  // Miscellaneous tab second CheckBox
		  cbm2.setText("Ask for ISA for new projects");
		  cbm2.setIndeterminate(false);
		  AnchorPane.setTopAnchor(cbm2, 40.0);
		  AnchorPane.setLeftAnchor(cbm2, 10.0);
		 
		  miscellaneousAnchorPane.getChildren().addAll(cbm,cbm2);
		  tabMiscellaneous.setContent(miscellaneousAnchorPane);
		  tabPane.getTabs().add(tabMiscellaneous);
		  
		  // View Tab
		  Tab tabView = new Tab();
		  tabView.setText("View");
		  AnchorPane viewAnchorPane = new AnchorPane();
		  
		 // view tab first CheckBox
		  cbv.setText("Hide the view of Project Explorer");
		  cbv.setIndeterminate(false);
		  AnchorPane.setTopAnchor(cbv, 10.0);
		  AnchorPane.setLeftAnchor(cbv, 10.0);
		 
		
		  // Disable the view of Toolbar CheckBox
		  cbv2.setText("Hide the view of Toolbar");
		  cbv2.setIndeterminate(false);
		  AnchorPane.setTopAnchor(cbv2, 40.0);
		  AnchorPane.setLeftAnchor(cbv2, 10.0);
		  
		  
		  //Disable the view of Console CheckBox
		  cbv3.setText("Hide the view of Console");
		  cbv3.setIndeterminate(false);
		  AnchorPane.setTopAnchor(cbv3, 70.0);
		  AnchorPane.setLeftAnchor(cbv3, 10.0);
		  
		 
		  viewAnchorPane.getChildren().addAll(cbv,cbv2,cbv3);
		  tabView.setContent(viewAnchorPane);
		  tabPane.getTabs().add(tabView);
		  tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		  
		 // Apply Button
	    Button apply = new Button("Apply");
		 
          apply.setOnAction((event) ->{
		// TODO: Add Event for Button
        	 
			});
	
        
       
          apply.setDefaultButton(true);
          
         // Close Button
		Button cancel = new Button("Close");
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				Stage stage = (Stage) cancel.getScene().getWindow();
				stage.close();
			}
		});

		grid.add(tabPane, 0, 0);
	    border.setCenter(grid);

		buttons.getChildren().addAll(apply, cancel);
		buttons.setAlignment(Pos.BASELINE_RIGHT);
		border.setBottom(buttons);

		return Components.wrap(border);

		}
 
}