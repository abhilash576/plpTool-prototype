package edu.asu.plp.tool.prototype;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import moore.fx.components.Components;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import edu.asu.plp.tool.exceptions.UnexpectedFileTypeException;
import edu.asu.plp.tool.prototype.model.PLPProject;
import edu.asu.plp.tool.prototype.model.PLPSourceFile;
import edu.asu.plp.tool.prototype.view.CodeEditor;
import edu.asu.plp.tool.prototype.view.ConsolePane;
import edu.asu.plp.tool.prototype.view.ProjectExplorerTree;

/**
 * Driver for the PLPTool prototype.
 * 
 * The driver's only responsibility is to launch the PLPTool Prototype window. This class
 * also defines the window and its contents.
 * 
 * @author Moore, Zachary
 *
 */
public class Main extends Application
{
	public static final String APPLICATION_NAME = "PLPTool";
	public static final long VERSION = 0;
	public static final long REVISION = 1;
	public static final int DEFAULT_WINDOW_WIDTH = 1280;
	public static final int DEFAULT_WINDOW_HEIGHT = 720;
	public static boolean simMode = false;
	
	private Stage stage;
	private TabPane openProjectsPanel;
	private BidiMap<PLPSourceFile, Tab> openProjects;
	private ObservableList<PLPProject> projects;
	private ProjectExplorerTree projectExplorer;
	private ConsolePane console;
	
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage)
	{
		this.stage = primaryStage;
		primaryStage.setTitle(APPLICATION_NAME + " V" + VERSION + "." + REVISION);
		
		this.openProjects = new DualHashBidiMap<>();
		this.openProjectsPanel = new TabPane();
		this.projectExplorer = createProjectTree();
		Parent outlineView = createOutlineView();
		console = createConsole();
		console.println(">> Console Initialized.");
		
		// Left side holds the project tree and outline view
		SplitPane leftSplitPane = new SplitPane();
		leftSplitPane.orientationProperty().set(Orientation.VERTICAL);
		leftSplitPane.getItems().addAll(Components.passiveScroll(projectExplorer),
				Components.wrap(outlineView));
		leftSplitPane.setDividerPositions(0.5, 1.0);
		
		// Right side holds the source editor and the output console
		SplitPane rightSplitPane = new SplitPane();
		rightSplitPane.orientationProperty().set(Orientation.VERTICAL);
		rightSplitPane.getItems().addAll(Components.wrap(openProjectsPanel),
				Components.wrap(console));
		rightSplitPane.setDividerPositions(0.75, 1.0);
		
		// Container for the whole view (everything under the toolbar)
		SplitPane explorerEditorSplitPane = new SplitPane();
		explorerEditorSplitPane.getItems().addAll(Components.wrap(leftSplitPane),
				Components.wrap(rightSplitPane));
		explorerEditorSplitPane.setDividerPositions(0.2, 1.0);
		
		loadOpenProjects();
		
		Parent menuBar = createMenuBar();
		Parent toolbar = createToolbar();
		BorderPane mainPanel = new BorderPane();
		VBox topContainer = new VBox();
		topContainer.getChildren().add(menuBar);
		topContainer.getChildren().add(toolbar);
		mainPanel.setTop(topContainer);
		mainPanel.setCenter(explorerEditorSplitPane);
		
		int width = DEFAULT_WINDOW_WIDTH;
		int height = DEFAULT_WINDOW_HEIGHT;
		primaryStage.setScene(new Scene(Components.wrap(mainPanel), width, height));
		primaryStage.show();
	}
	
	private File showOpenDialogue()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		
		String plp6Extension = "*" + PLPProject.FILE_EXTENSION;
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("PLP6 Project Files", plp6Extension),
				new ExtensionFilter("Legacy Project Files", "*.plp"),
				new ExtensionFilter("All PLP Project Files", "*.plp", plp6Extension),
				new ExtensionFilter("All Files", "*.*"));
		
		return fileChooser.showOpenDialog(stage);
	}
	
	private void openProjectFromFile()
	{
		File selectedFile = showOpenDialogue();
		if (selectedFile != null)
		{
			openProjectFromFile(selectedFile);
		}
	}
	
	/**
	 * Loads the given file from disk using {@link PLPProject#load(File)}, and adds the
	 * project to the project explorer.
	 * <p>
	 * If the project is already in the project explorer, a message will be displayed
	 * indicating the project is already open, and the project will be expanded in the
	 * project tree.
	 * <p>
	 * If the project is not in the tree, but a project with the same name is in the tree,
	 * then a message will be displayed indicating that a project with the same name
	 * already exists, and will ask if the user would like to rename one of the projects.
	 * If not, the dialogue will be closed and the project will not be opened.
	 * 
	 * @param file
	 *            The file or directory (PLP6 only) containing the project to be opened
	 */
	private void openProjectFromFile(File file)
	{
		try
		{
			PLPProject project = PLPProject.load(file);
			if (this.containsProjectWithName(project.getName()))
			{
				// TODO: display 'project with name 'x' already exists' message
				// TODO: if the project is already loaded, display 'project is already
				// open' message, and expand project in the projectExplorer
			}
			else
			{
				projects.add(project);
			}
		}
		catch (UnexpectedFileTypeException e)
		{
			alert(e, "The selected file could not be loaded");
		}
		catch (IOException e)
		{
			alert(e, "There was a problem loading the selected file");
		}
		catch (Exception e)
		{
			alert(e);
		}
	}
	
	private void alert(Exception exception)
	{
		alert(exception, "An error has occurred!");
	}
	
	private void alert(Exception exception, String message)
	{
		String context = exception.getMessage();
		boolean valid = (context != null && !context.isEmpty());
		context = (valid) ? "Cause: " + context : null;
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Exception Dialog");
		alert.setHeaderText(message);
		alert.setContentText(context);
		alert.setGraphic(null);
		
		String exceptionText = getStackTraceAsString(exception);
		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(false);
		
		alert.getDialogPane().setExpandableContent(textArea);
		alert.showAndWait();
	}
	
	private String getStackTraceAsString(Exception exception)
	{
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		exception.printStackTrace(printWriter);
		
		return stringWriter.toString();
	}
	
	private boolean containsProjectWithName(String name)
	{
		for (PLPProject project : projects)
		{
			String projectName = project.getName();
			boolean namesAreNull = (projectName == null && name == null);
			if (namesAreNull || name.equals(projectName))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Creates a tab for the specified project, or selects the project, if the tab already
	 * exists.
	 * 
	 * @param project
	 *            The project to open
	 */
	private void openFile(PLPSourceFile file)
	{
		String fileName = file.getName();
		
		System.out.println("Opening " + fileName);
		Tab tab = openProjects.get(file);
		
		if (tab == null)
		{
			// Create new tab
			CodeEditor content = createCodeEditor();
			tab = addTab(openProjectsPanel, fileName, content);
			openProjects.put(file, tab);
		}
		
		// Activate the specified tab
		openProjectsPanel.getSelectionModel().select(tab);
	}
	
	private void saveProjectFile()
	{		
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PLP files (*.plp)", "*.plp"));
		//fileChooser.setInitialFileName(openProjects.getKey(index).getName());
		File file = fileChooser.showSaveDialog(null);
		System.out.println(file);
		System.out.println(openProjects.get(file).getContent().toString());
		//projects.get(index).save();
	}
	
	private CodeEditor createCodeEditor()
	{
		return new CodeEditor();
		/*
		 * try { CodeEditor editor = new CodeEditor(); File syntaxFile = new
		 * File("resources/languages/plp.syn"); editor.setSyntaxHighlighting(syntaxFile);
		 * return editor; } catch (IOException e) { e.printStackTrace(); return new
		 * CodeEditor(); }
		 */
	}
	
	private Tab addTab(TabPane panel, String projectName, Node contentPanel)
	{
		Tab tab = new Tab();
		tab.setText(projectName);
		tab.setContent(Components.wrap(contentPanel));
		tab.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event event)
			{
				openProjects.removeValue(tab);
			}
		});
		tab.setOnSelectionChanged(new EventHandler<Event>() {
			@Override
			public void handle(Event event)
			{
				PLPSourceFile activeFile = openProjects.getKey(tab);
				if (activeFile != null)
					projectExplorer.setActiveFile(activeFile);
			}
		});
		panel.getTabs().add(tab);
		
		return tab;
	}
	
	private ConsolePane createConsole()
	{
		ConsolePane console = new ConsolePane();
		ContextMenu contextMenu = new ContextMenu();
		
		MenuItem clearConsoleItem = new MenuItem("Clear");
		clearConsoleItem.setOnAction(e -> console.clear());
		contextMenu.getItems().add(clearConsoleItem);
		
		console.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
			contextMenu.show(console, event.getScreenX(), event.getScreenY());
			event.consume();
		});
		console.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY)
				contextMenu.hide();
		});
		
		return console;
	}
	
	private Parent createOutlineView()
	{
		// TODO: replace with relevant outline window
		return Components.wrap(new TextArea());
	}
	
	/**
	 * Restore all projects from a persistent data store, and call
	 * {@link #openProject(String, String)} for each
	 */
	private void loadOpenProjects()
	{
		// TODO: replace with actual content
	}
	
	/**
	 * Creates a project tree to display all known projects, and their contents. The tree
	 * orders projects as first level elements, with their folders and files being
	 * children elements.
	 * <p>
	 * This method is responsible for adding all appropriate listeners to allow navigation
	 * of the tree (and display it appropriately), including setting the background and
	 * any other applicable css attributes.
	 * <p>
	 * The returned tree will open a file in the {@link #openProjectsPanel} when a
	 * fileName is double-clicked.
	 * 
	 * @return A tree-view of the project explorer
	 */
	private ProjectExplorerTree createProjectTree()
	{
		projects = FXCollections.observableArrayList();
		ProjectExplorerTree projectExplorer = new ProjectExplorerTree(projects);
		
		PLPProject project = new PLPProject("Assignment1");
		project.add(new PLPSourceFile(project, "main.asm"));
		project.add(new PLPSourceFile(project, "sorting.asm"));
		project.add(new PLPSourceFile(project, "division.asm"));
		projects.add(project);
		
		project = new PLPProject("Assignment2");
		project.add(new PLPSourceFile(project, "main.asm"));
		project.add(new PLPSourceFile(project, "uart_utilities.asm"));
		projects.add(project);
		
		projectExplorer.setOnFileDoubleClicked(this::openFile);
		
		return projectExplorer;
	}
	
	/**
	 * Creates a horizontal toolbar containing controls to:
	 * <ul>
	 * <li>Create a new project
	 * <li>Add a new file
	 * <li>Save the current project
	 * <li>Open a new project
	 * <li>Assemble the current project
	 * </ul>
	 * 
	 * @return a Parent {@link Node} representing the PLP toolbar
	 */
	private Parent createToolbar()
	{
		HBox toolbar = new HBox();
		toolbar.setPadding(new Insets(1.5, 0, 1, 5));
		toolbar.setSpacing(5);
		ObservableList<Node> buttons = toolbar.getChildren();
		
		EventHandler<MouseEvent> listener;
		Node button;
		
		DropShadow lBlueShadow = new DropShadow();
		lBlueShadow.setColor(Color.LIGHTBLUE);
		DropShadow dBlueShadow = new DropShadow();
		dBlueShadow.setColor(Color.DARKBLUE);
		
		
		// TODO: replace event handlers with actual content
		Node projectButton = new ImageView("toolbar_new.png");
		projectButton.addEventHandler(MouseEvent.MOUSE_ENTERED, 
			    new EventHandler<MouseEvent>() {
			        @Override public void handle(MouseEvent e) {
			            projectButton.setEffect(lBlueShadow);
			        }
			});
			//Removing the shadow when the mouse cursor is off
			projectButton.addEventHandler(MouseEvent.MOUSE_EXITED, 
			    new EventHandler<MouseEvent>() {
			        @Override public void handle(MouseEvent e) {
			            projectButton.setEffect(null);
			        }
			});
			projectButton.addEventHandler(MouseEvent.MOUSE_PRESSED, 
				    new EventHandler<MouseEvent>() {
				        @Override public void handle(MouseEvent e) {
				        	console.println("New Project Clicked");
				        	projectButton.setEffect(dBlueShadow);
				        }
				});
			projectButton.addEventHandler(MouseEvent.MOUSE_RELEASED, 
				    new EventHandler<MouseEvent>() {
				        @Override public void handle(MouseEvent e) {
				        	projectButton.setEffect(lBlueShadow);
				        }
				});
		buttons.add(projectButton);
		
		Node newFileButton = new ImageView("menu_new.png");
		listener = (event) -> console.println("New File Clicked");
		newFileButton.setOnMouseClicked(listener);
		buttons.add(newFileButton);
		
		button = new ImageView("toolbar_open.png");
		listener = this::onOpenProjectClicked;
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		button = new ImageView("toolbar_save.png");
		listener = this::onSaveProjectClicked;
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_assemble.png");
		listener = (event) -> console.println("Assemble Project Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_simulate.png");
		listener = (event) -> (
				onSimProjectClicked(event, toolbar)
				);
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_program.png");
		listener = (event) -> console.println("Program Project Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		button = new ImageView("toolbar_step.png");
		listener = (event) -> console.println("Step Through Project Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_run.png");
		listener = (event) -> console.println("Run Project Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_reset.png");
		listener = (event) -> console.println("Reset Sim Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_remote.png");
		listener = (event) -> console.println("Floating Sim Control Window Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		//I Think we are putting all these buttons onto one page
		//but until that happens I laid them all out, just in case
		
		button = new ImageView("toolbar_cpu.png");
		listener = (event) -> console.println("CPU View");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_watcher.png");
		listener = (event) -> console.println("Watcher Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_sim_leds.png");
		listener = (event) -> console.println("LED's Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_sim_switches.png");
		listener = (event) -> console.println("Switches Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_sim_7segments.png");
		listener = (event) -> console.println("7 Seg Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_sim_uart.png");
		listener = (event) -> console.println("UART Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_sim_vga.png");
		listener = (event) -> console.println("VGA Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_sim_plpid.png");
		listener = (event) -> console.println("PLPID Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_sim_gpio.png");
		listener = (event) -> console.println("GPIO Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_exclamation.png");
		listener = (event) -> console.println("Interupt Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		for(int x = 9; x<=23; x++ )
		{
			DropShadow ds = new DropShadow();
			toolbar.getChildren().get(x).setEffect(ds);
			toolbar.getChildren().get(x).setDisable(true);
		}
		
		return Components.wrap(toolbar);
	}
	
	private Parent createMenuBar()
	{
		MenuBar menuBar = new MenuBar();
		
		//Menu Items under "File"
		Menu file = new Menu("File");
		MenuItem itemNew = new MenuItem("New PLP Project");
		itemNew.setGraphic(new ImageView(new Image("menu_new.png")));
		itemNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        itemNew.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemOpen = new MenuItem("Open PLP Project");
		itemOpen.setGraphic(new ImageView(new Image("toolbar_open.png")));
		itemOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        itemOpen.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemSave = new MenuItem("Save");
		itemSave.setGraphic(new ImageView(new Image("toolbar_save.png")));
		itemSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        itemSave.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemSaveAs = new MenuItem("Save As");
		itemSaveAs.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        itemSaveAs.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemPrint = new MenuItem("Print");
		itemPrint.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
        itemPrint.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemExit = new MenuItem("Exit");
		itemExit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        itemExit.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
        
		file.getItems().addAll(itemNew, new SeparatorMenuItem(), itemOpen, itemSave, itemSaveAs, new SeparatorMenuItem(), itemPrint, new SeparatorMenuItem(), itemExit);
		
		//Menu Items under "Edit"
		Menu edit = new Menu("Edit");
		MenuItem itemCopy = new MenuItem("Copy");
		itemCopy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        itemCopy.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemCut = new MenuItem("Cut");
		itemCut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        itemCut.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemPaste = new MenuItem("Paste");
		itemPaste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        itemPaste.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemFandR = new MenuItem("Find and Replace");
		itemFandR.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
        itemFandR.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemUndo = new MenuItem("Undo");
		itemUndo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        itemUndo.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemRedo = new MenuItem("Redo");
		itemRedo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        itemRedo.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		edit.getItems().addAll(itemCopy, itemCut, itemPaste, new SeparatorMenuItem(), itemFandR, new SeparatorMenuItem(), itemUndo, itemRedo);
		
		//Menu Items under "View"
		Menu view = new Menu("View");
		CheckMenuItem cItemToolbar = new CheckMenuItem("Toolbar");
		cItemToolbar.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
        cItemToolbar.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		CheckMenuItem cItemProjectPane = new CheckMenuItem("Project Pane");
		cItemProjectPane.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
        cItemProjectPane.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		CheckMenuItem cItemOutputPane = new CheckMenuItem("Output Pane");
		cItemOutputPane.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
        cItemOutputPane.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemClearOutput = new MenuItem("Clear Output Pane");
		itemClearOutput.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
        itemClearOutput.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
        
		view.getItems().addAll(cItemToolbar, cItemProjectPane, cItemOutputPane, itemClearOutput);
		cItemToolbar.setSelected(true);
		cItemProjectPane.setSelected(true);
		cItemOutputPane.setSelected(true);
		
		
		//Menu Items Under "Project"
		Menu project = new Menu("Project");
		MenuItem itemAssemble = new MenuItem("Assemble");
		itemAssemble.setGraphic(new ImageView(new Image("toolbar_assemble.png")));
		itemAssemble.setAccelerator(new KeyCodeCombination(KeyCode.F2));
        itemAssemble.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemSimulate = new MenuItem("Simulate");
		itemSimulate.setGraphic(new ImageView(new Image("toolbar_simulate.png")));
		itemSimulate.setAccelerator(new KeyCodeCombination(KeyCode.F3));
        itemSimulate.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemPLPBoard = new MenuItem("Program PLP Board...");
		itemPLPBoard.setGraphic(new ImageView(new Image("toolbar_program.png")));
		itemPLPBoard.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.SHIFT_DOWN));
        itemPLPBoard.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemQuickProgram = new MenuItem("Quick Program");
		itemQuickProgram.setAccelerator(new KeyCodeCombination(KeyCode.F4));
        itemQuickProgram.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemNewASM = new MenuItem("New ASM File...");
        itemNewASM.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemImportASM = new MenuItem("Import ASM File...");
        itemImportASM.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemExportASM = new MenuItem("Export Selected ASM File...");
        itemExportASM.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemRemoveASM = new MenuItem("Remove Selected ASM File from Project");
		itemRemoveASM.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        itemRemoveASM.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemCurrentAsMain = new MenuItem("Set Current Open File as Main Program");
        itemCurrentAsMain.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		project.getItems().addAll(itemAssemble, itemSimulate, itemPLPBoard, itemQuickProgram, new SeparatorMenuItem(), itemNewASM, 
				itemImportASM, itemExportASM, itemRemoveASM, new SeparatorMenuItem(), itemCurrentAsMain);
		
		//Menu Items Under "Tools"
		Menu tools = new Menu("Tools");
		MenuItem itemOptions = new MenuItem("Options");
        itemOptions.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		Menu modules = new Menu("Modules");
		MenuItem itemModuleManager = new MenuItem("Module Manager...");
        itemModuleManager.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemLoadJar = new MenuItem("Load Module JAR File...");
        itemLoadJar.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemClearCache = new MenuItem("Clear Module Auto-Load Cache");
        itemClearCache.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemSerialTerminal = new MenuItem("Serial Terminal");
		itemSerialTerminal.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));
        itemSerialTerminal.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemNumConverter = new MenuItem("Number Converter");
		itemNumConverter.setAccelerator(new KeyCodeCombination(KeyCode.F12));
        itemNumConverter.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		modules.getItems().addAll(itemModuleManager, itemLoadJar, itemClearCache);
		tools.getItems().addAll(itemOptions, modules, new SeparatorMenuItem(), itemSerialTerminal, itemNumConverter);
		
		//Menu Items Under "Simulation"
		Menu simulation = new Menu("Simulation");
		MenuItem itemStep = new MenuItem("Step");
		itemStep.setGraphic(new ImageView(new Image("toolbar_step.png")));
		itemStep.setAccelerator(new KeyCodeCombination(KeyCode.F5));
        itemStep.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemReset = new MenuItem("Reset");
		itemReset.setGraphic(new ImageView(new Image("toolbar_reset.png")));
		itemReset.setAccelerator(new KeyCodeCombination(KeyCode.F9));
        itemReset.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemRun = new MenuItem("Run");
		itemRun.setAccelerator(new KeyCodeCombination(KeyCode.F7));
        itemRun.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		Menu cyclesSteps = new Menu("Cycles/Steps");
		MenuItem itemOne = new MenuItem("1");
		itemOne.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD1, KeyCombination.ALT_DOWN));
        itemOne.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemFive = new MenuItem("5");
		itemFive.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD2, KeyCombination.ALT_DOWN));
        itemFive.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemTwenty = new MenuItem("20");
		itemTwenty.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD3, KeyCombination.ALT_DOWN));
        itemTwenty.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemHundred = new MenuItem("100");
		itemHundred.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD4, KeyCombination.ALT_DOWN));
        itemHundred.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemFiveThousand = new MenuItem("5000");
		itemFiveThousand.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD5, KeyCombination.ALT_DOWN));
        itemFiveThousand.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemClearBreakpoints = new MenuItem("Clear Breakpoints");
		itemClearBreakpoints.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN));
        itemClearBreakpoints.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		Menu views = new Menu("Views");
		MenuItem itemCpuView = new MenuItem("CPU View");
		itemCpuView.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        itemCpuView.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemCpuWindow = new MenuItem("Watcher Window");
		itemCpuWindow.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        itemCpuWindow.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemSimControlWindow = new MenuItem("Simulation Control Window");
		itemSimControlWindow.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        itemSimControlWindow.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		Menu toolsSubMenu = new Menu("Tools");
		MenuItem itemioRegistry = new MenuItem("I/O Registry");
		itemioRegistry.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        itemioRegistry.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemASMView = new MenuItem("ASM View");
        itemASMView.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemCreateMemVis = new MenuItem("Create a PLP CPU Memory Visualizer");
        itemCreateMemVis.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemRemoveMemVis = new MenuItem("Remove Memory Visualizers from Project");
        itemRemoveMemVis.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemDisplayBus = new MenuItem("Display Bus Monitor Timing Diagram");
        itemDisplayBus.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		Menu ioDevices = new Menu("I/O Devices");
		MenuItem itemLedArray = new MenuItem("LED Array");
		itemLedArray.setGraphic(new ImageView(new Image("toolbar_sim_leds.png")));
		itemLedArray.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD1, KeyCombination.CONTROL_DOWN));
        itemLedArray.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemSwitches = new MenuItem("Switches");
		itemSwitches.setGraphic(new ImageView(new Image("toolbar_sim_switches.png")));
		itemSwitches.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD2, KeyCombination.CONTROL_DOWN));
        itemSwitches.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemSevenSeg = new MenuItem("Seven Segments");
		itemSevenSeg.setGraphic(new ImageView(new Image("toolbar_sim_7segments.png")));
		itemSevenSeg.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD3, KeyCombination.CONTROL_DOWN));
        itemSevenSeg.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemUART = new MenuItem("UART");
		itemUART.setGraphic(new ImageView(new Image("toolbar_sim_uart.png")));
		itemUART.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD4, KeyCombination.CONTROL_DOWN));
        itemUART.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemVGA = new MenuItem("VGA");
		itemVGA.setGraphic(new ImageView(new Image("toolbar_sim_vga.png")));
		itemVGA.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD5, KeyCombination.CONTROL_DOWN));
        itemVGA.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemPLPID = new MenuItem("PLPID");
		itemPLPID.setGraphic(new ImageView(new Image("toolbar_sim_plpid.png")));
		itemPLPID.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD6, KeyCombination.CONTROL_DOWN));
        itemPLPID.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemGPIO = new MenuItem("GPIO");
		itemGPIO.setGraphic(new ImageView(new Image("toolbar_sim_gpio.png")));
		itemGPIO.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD7, KeyCombination.CONTROL_DOWN));
        itemGPIO.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemExitSim = new MenuItem("ExitSimulation");
		itemExitSim.setAccelerator(new KeyCodeCombination(KeyCode.F11));
        itemExitSim.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		cyclesSteps.getItems().addAll(itemOne, itemFive, itemTwenty, itemHundred, itemFiveThousand);
		views.getItems().addAll(itemCpuView, itemCpuWindow, itemSimControlWindow);
		toolsSubMenu.getItems().addAll(itemioRegistry, itemASMView, new SeparatorMenuItem(), itemCreateMemVis, itemRemoveMemVis, itemDisplayBus);
		ioDevices.getItems().addAll(itemLedArray, itemSwitches, itemSevenSeg, itemUART, itemVGA, itemPLPID, itemGPIO);
		simulation.getItems().addAll(itemStep, itemReset, new SeparatorMenuItem(), itemRun, cyclesSteps, itemClearBreakpoints, new SeparatorMenuItem(), views, toolsSubMenu, ioDevices, new SeparatorMenuItem(), itemExitSim);
		
		//Menu Items Under "Help"
		Menu help = new Menu("Help");
		MenuItem itemQuickRef = new MenuItem ("Quick Reference");
		itemQuickRef.setAccelerator(new KeyCodeCombination(KeyCode.F1));
        itemQuickRef.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemOnlineManual = new MenuItem ("Online Manual");
        itemOnlineManual.setOnAction((event)-> {
        	onlineManualWeb();
        });
		MenuItem itemReportIssue = new MenuItem ("Report Issue (Requires Google Account");
        itemReportIssue.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemGoogleIssues = new MenuItem ("Open Google Code Issues Page");
        itemGoogleIssues.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemAboutPLP = new MenuItem ("About PLP Tool...");
        itemAboutPLP.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		MenuItem itemSWLicense = new MenuItem ("Third Party Software License");
        itemSWLicense.setOnAction((event)-> {
        //TODO: Add Event for menu item
        });
		help.getItems().addAll(itemQuickRef, itemOnlineManual, new SeparatorMenuItem(), itemReportIssue, itemGoogleIssues, new SeparatorMenuItem(), itemAboutPLP, itemSWLicense);
		
		
		menuBar.getMenus().addAll(file, edit, view, project, tools, simulation, help);
		
		return Components.wrap(menuBar);
	}
	
	private void onOpenProjectClicked(MouseEvent event)
	{
		console.println("Open Project Clicked");
		openProjectFromFile();
	}
	
	private void onSaveProjectClicked(MouseEvent event)
	{
		console.println("Save Project Button Clicked");
		saveProjectFile();
	}
	
	private void onSimProjectClicked(MouseEvent event, HBox toolbar)
	{
		DropShadow ds = new DropShadow();
		if(!simMode)
		{
			for(int x = 9; x<=23; x++ )
			{
				toolbar.getChildren().get(x).setEffect(null);
				toolbar.getChildren().get(x).setDisable(false);
			}
			simMode = true;
		}
		else
		{
			for(int x = 9; x<=23; x++ )
			{
				toolbar.getChildren().get(x).setEffect(ds);
				toolbar.getChildren().get(x).setDisable(true);
			}
			simMode = false;
		}
	}
	
	private void onlineManualWeb()
	{
		try{
			
			if(Desktop.isDesktopSupported())
			{
			  Desktop.getDesktop().browse(new URI("https://code.google.com/p/progressive-learning-platform/wiki/UserManual"));
			}
			}catch(Exception e){
				e.printStackTrace();
			}
	}
}
