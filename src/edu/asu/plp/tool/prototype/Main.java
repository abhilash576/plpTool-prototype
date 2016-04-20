package edu.asu.plp.tool.prototype;

import static edu.asu.plp.tool.prototype.util.Dialogues.showAlertDialogue;
import static edu.asu.plp.tool.prototype.util.Dialogues.showInfoDialogue;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import moore.fx.components.Components;
import moore.util.ExceptionalSubroutine;
import moore.util.Subroutine;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.io.FileUtils;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

import edu.asu.plp.tool.backend.EventRegistry;
import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.Assembler;
import edu.asu.plp.tool.backend.isa.Simulator;
import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;
import edu.asu.plp.tool.core.ISAModule;
import edu.asu.plp.tool.core.ISARegistry;
import edu.asu.plp.tool.exceptions.UnexpectedFileTypeException;
import edu.asu.plp.tool.prototype.model.ApplicationSetting;
import edu.asu.plp.tool.prototype.model.ApplicationThemeManager;
import edu.asu.plp.tool.prototype.model.OptionSection;
import edu.asu.plp.tool.prototype.model.PLPOptions;
import edu.asu.plp.tool.prototype.model.PLPProject;
import edu.asu.plp.tool.prototype.model.Project;
import edu.asu.plp.tool.prototype.model.QuickViewSection;
import edu.asu.plp.tool.prototype.model.SimpleASMFile;
import edu.asu.plp.tool.prototype.model.Submittable;
import edu.asu.plp.tool.prototype.model.Theme;
import edu.asu.plp.tool.prototype.model.ThemeRequestCallback;
import edu.asu.plp.tool.prototype.model.ThemeRequestEvent;
import edu.asu.plp.tool.prototype.util.Dialogues;
import edu.asu.plp.tool.prototype.view.CodeEditor;
import edu.asu.plp.tool.prototype.view.ConsolePane;
import edu.asu.plp.tool.prototype.view.CpuWindow;
import edu.asu.plp.tool.prototype.view.OutlineView;
import edu.asu.plp.tool.prototype.view.ProjectExplorerTree;
<<<<<<< HEAD
<<<<<<< HEAD
import edu.asu.plp.tool.prototype.model.OptionsFrame;
=======
=======
import edu.asu.plp.tool.prototype.view.QuickViewPanel;
import edu.asu.plp.tool.prototype.view.WatcherWindow;
>>>>>>> 545c8daa502b236a58a4d36646d4ab59d2443399
import edu.asu.plp.tool.prototype.view.menu.options.OptionsPane;
import edu.asu.plp.tool.prototype.view.menu.options.sections.ApplicationSettingsPanel;
import edu.asu.plp.tool.prototype.view.menu.options.sections.EditorSettingsPanel;
import edu.asu.plp.tool.prototype.view.menu.options.sections.ProgrammerSettingsPanel;
import edu.asu.plp.tool.prototype.view.menu.options.sections.SimulatorSettingsPanel;

>>>>>>> 999bc8d28400b44b63c91477649f4e96a6c851de
/**
 * Driver for the PLPTool prototype.
 * 
 * The driver's only responsibility is to launch the PLPTool Prototype window. This class
 * also defines the window and its contents.
 * 
 * @author Moore, Zachary
 * @author Hawks, Elliott
 * @author Nesbitt, Morgan
 * 
 */
public class Main extends Application implements Controller
{
	public static final String APPLICATION_NAME = "PLPTool";
	public static final long VERSION = 0;
	public static final long REVISION = 1;
	public static final int DEFAULT_WINDOW_WIDTH = 1280;
	public static final int DEFAULT_WINDOW_HEIGHT = 720;
	
	private Simulator activeSimulator;
	private Stage stage;
	private TabPane openProjectsPanel;
	private BidiMap<ASMFile, Tab> openFileTabs;
	private ObservableList<PLPLabel> activeNavigationItems;
	private ObservableList<Project> projects;
	private Map<Project, ProjectAssemblyDetails> assemblyDetails;
	private ProjectExplorerTree projectExplorer;
	private ConsolePane console;
	
	private ApplicationThemeManager applicationThemeManager;
	private OutlineView outlineView;
	
	public static void main(String[] args)
	{
		launch(args);
	}
	
	public static File findDiskObjectForASM(ASMFile activeFile)
	{
		Project project = activeFile.getProject();
		String path = project.getPathFor(activeFile);
		if (path == null)
			return null;
		
		return new File(path);
	}
	
	private void onTabActivation(ObservableValue<? extends Tab> value, Tab old,
			Tab current)
	{
		ASMFile previousASM = openFileTabs.getKey(current);
		if (previousASM != null)
			previousASM.contentProperty().removeListener(this::updateOutline);
		
		ASMFile asmFile = openFileTabs.getKey(current);
		if (asmFile != null)
		{
			String content = asmFile.getContent();
			List<PLPLabel> labels = PLPLabel.scrape(content);
			outlineView.setModel(FXCollections.observableArrayList(labels));
			asmFile.contentProperty().addListener(this::updateOutline);
		}
	}
	
	private void updateOutline(ObservableValue<? extends String> value, String old,
			String current)
	{
		List<PLPLabel> labels = PLPLabel.scrape(current);
		outlineView.setModel(FXCollections.observableArrayList(labels));
	}
	
	@Override
	public void start(Stage primaryStage)
	{
		this.stage = primaryStage;
		primaryStage.setTitle(APPLICATION_NAME + " V" + VERSION + "." + REVISION);
		
		ApplicationSettings.initialize();
		ApplicationSettings.loadFromFile("settings/plp-tool.settings");
		
		EventRegistry.getGlobalRegistry().register(new ApplicationEventBusEventHandler());
		
		applicationThemeManager = new ApplicationThemeManager();
		
		this.assemblyDetails = new HashMap<>();
		this.openFileTabs = new DualHashBidiMap<>();
		this.openProjectsPanel = new TabPane();
		this.projectExplorer = createProjectTree();
		outlineView = createOutlineView();
		console = createConsole();
		console.println(">> Console Initialized.");
		
		openProjectsPanel.getSelectionModel().selectedItemProperty()
				.addListener(this::onTabActivation);
		
		ScrollPane scrollableProjectExplorer = new ScrollPane(projectExplorer);
		scrollableProjectExplorer.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollableProjectExplorer.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollableProjectExplorer.setFitToHeight(true);
		scrollableProjectExplorer.setFitToWidth(true);
		
		// Left side holds the project tree and outline view
		SplitPane leftSplitPane = new SplitPane();
		leftSplitPane.orientationProperty().set(Orientation.VERTICAL);
		leftSplitPane.getItems().addAll(scrollableProjectExplorer, outlineView);
		leftSplitPane.setDividerPositions(0.5, 1.0);
		leftSplitPane.setMinSize(0, 0);
		
		// Right side holds the source editor and the output console
		SplitPane rightSplitPane = new SplitPane();
		rightSplitPane.orientationProperty().set(Orientation.VERTICAL);
		rightSplitPane.getItems().addAll(Components.wrap(openProjectsPanel),
				Components.wrap(console));
		rightSplitPane.setDividerPositions(0.75, 1.0);
		rightSplitPane.setMinSize(0, 0);
		
		// Container for the whole view (everything under the toolbar)
		SplitPane explorerEditorSplitPane = new SplitPane();
		explorerEditorSplitPane.getItems().addAll(Components.wrap(leftSplitPane),
				Components.wrap(rightSplitPane));
		explorerEditorSplitPane.setDividerPositions(0.225, 1.0);
		explorerEditorSplitPane.setMinSize(0, 0);
		
		SplitPane.setResizableWithParent(leftSplitPane, Boolean.FALSE);
		
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
		
		Scene scene = new Scene(Components.wrap(mainPanel), width, height);
		
		primaryStage.setScene(scene);
		
		String themeName = ApplicationSettings.getSetting(
				ApplicationSetting.APPLICATION_THEME).get();
		EventRegistry.getGlobalRegistry().post(new ThemeRequestEvent(themeName));
		
		primaryStage.show();
	}
	
	private Node plpQuickRef()
	{
		// TODO: load this from a JSON file
		List<QuickViewSection> plp = new ArrayList<>();
		
		QuickViewSection instructionsRType = new QuickViewSection("R-Type Instructions");
		instructionsRType.addEntry("addu $rd, $rs, $rt", "rd = rs + rt");
		instructionsRType.addEntry("subu $rd, $rs, $rt", "rd = rs - rt");
		instructionsRType.addEntry("and $rd, $rs, $rt", "rd = rs & rt");
		instructionsRType.addEntry("or $rd, $rs, $rt", "rd = rs | rt");
		// TODO: add remaining R-Type instructions
		
		QuickViewSection instructionsIType = new QuickViewSection("I-Type Instructions");
		instructionsIType.addEntry("addiu $rt, $rs, imm", "rt = rs + SignExtend(imm)");
		instructionsIType.addEntry("andi $rt, $rs, imm", "rt = rs & ZeroExtend(imm)");
		instructionsIType.addEntry("ori $rt, $rs, imm", "rt = rs | ZeroExtend(imm)");
		// TODO: add remaining I-Type instructions
		
		QuickViewSection instructionsJType = new QuickViewSection("J-Type Instructions");
		instructionsJType.addEntry("j label", "PC = label");
		instructionsJType.addEntry("jal label", "ra = PC + 4; PC = label");
		
		QuickViewSection instructionsPsuedo = new QuickViewSection("Pseudo-Operations");
		instructionsPsuedo.addEntry("nop", "sll $0, $0, 0");
		instructionsPsuedo.addEntry("b label", "beq $0, $0, label");
		instructionsPsuedo.addEntry("move $rd, $rs", "or $rd, $0, $rs");
		// TODO: add remaining pseudo instructions
		
		QuickViewSection directives = new QuickViewSection("Assembler Directives");
		directives.addEntry(".org address",
				"Place subsequent statements starting from address");
		directives.addEntry("label:", "Label current memory location as label");
		directives.addEntry(".word value", "Write 32-bit value to the current address");
		directives.addEntry(".ascii \"string\"",
				"Place string starting from the current address");
		directives.addEntry(".asciiz \"string\"",
				"Place null-terminated string starting from the current address");
		directives.addEntry(".asciiw \"string\"",
				"Place word-aligned string starting from the current address");
		directives.addEntry(".space value",
				"Reserve value words starting from the current address");
		directives.addEntry(".equ symbol value",
				"Add a symbol and its associated value to the symbol table (a constant)");
		
		QuickViewSection registers = new QuickViewSection("Registers Usage Guide");
		registers.addEntry("$0, $zero", "The zero register");
		registers.addEntry("$1, $at", "Assembler temporary");
		registers.addEntry("$2-$3, $v0-$v1", "Return values");
		registers.addEntry("$4-$7, $a0-$a3", "Function arguments");
		registers.addEntry("$8-$17, $t0-$t9", "Temporaries");
		registers.addEntry("$18-$25, $s0-$s7", "Saved temporaries");
		registers.addEntry("$26-$27, $i0-$i1", "Interrupt temporaries");
		registers.addEntry("$28, $iv", "Interrupt vector");
		registers.addEntry("$29, $sp", "Stack pointer");
		registers.addEntry("$30, $ir", "Interrupt return address");
		registers.addEntry("$31, $ra", "Return address");
		
		QuickViewSection ioMap = new QuickViewSection("I/O Memory Map");
		ioMap.addEntry("0x00000000", "Boot ROM");
		ioMap.addEntry("0x10000000", "RAM");
		ioMap.addEntry("0xf0000000", "UART");
		ioMap.addEntry("0xf0100000", "Switches");
		ioMap.addEntry("0xf0200000", "LEDs");
		ioMap.addEntry("0xf0300000", "GPIO");
		ioMap.addEntry("0xf0400000", "VGA");
		ioMap.addEntry("0xf0500000", "PLPID");
		ioMap.addEntry("0xf0600000", "Timer");
		ioMap.addEntry("0xf0a00000", "Seven Segment Display");
		ioMap.addEntry("0xf0700000", "Interrupt Controller");
		
		plp.add(instructionsRType);
		plp.add(instructionsIType);
		plp.add(instructionsJType);
		plp.add(instructionsPsuedo);
		plp.add(directives);
		plp.add(registers);
		plp.add(ioMap);
		return new QuickViewPanel("PLP 5.2", plp);
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
	
	private File showExportDialogue(ASMFile exportItem)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export");
		fileChooser.setInitialFileName(exportItem.getName() + ".asm");
		
		String plp6Extension = "*" + PLPProject.FILE_EXTENSION;
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("PLP6 Project Files", plp6Extension),
				new ExtensionFilter("Legacy Project Files", "*.plp"),
				new ExtensionFilter("All PLP Project Files", "*.plp", plp6Extension),
				new ExtensionFilter("All Files", "*.*"));
		
		return fileChooser.showOpenDialog(stage);
	}
	
	private File showImportDialogue()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import ASM");
		
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("ASM Files", "*.asm"),
				new ExtensionFilter("All Files", "*.*"));
		
		return fileChooser.showOpenDialog(stage);
	}
	
	@Override
	public void openProject()
	{
		File selectedFile = showOpenDialogue();
		if (selectedFile != null)
		{
			openProjectFromFile(selectedFile);
		}
	}
	
	/**
	 * Loads the given file from disk using {@link Project#load(File)}, and adds the
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
			Project project = PLPProject.load(file);
			addProject(project);
		}
		catch (UnexpectedFileTypeException e)
		{
			showAlertDialogue(e, "The selected file could not be loaded");
		}
		catch (IOException e)
		{
			showAlertDialogue(e, "There was a problem loading the selected file");
		}
		catch (Exception e)
		{
			showAlertDialogue(e);
		}
	}
	
	private void addProject(Project project)
	{
		Project existingProject = getProjectByName(project.getName());
		if (existingProject != null)
		{
			if (existingProject.getPath().equals(project.getPath()))
			{
				// Projects are the same
				showInfoDialogue("This project is already open!");
				projectExplorer.expandProject(existingProject);
			}
			else
			{
				// Project with the same name already exists
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setGraphic(null);
				alert.setHeaderText(null);
				alert.setContentText("A project with the name \""
						+ project.getName()
						+ "\" already exists. In order to open this project, you must choose a different name."
						+ "\n\n"
						+ "Press OK to choose a new name, or Cancel to close this dialog.");
				
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK)
				{
					boolean renamed = renameProject(project);
					if (renamed)
						addProject(project);
				}
			}
		}
		else
		{
			projects.add(project);
		}
	}
	
	private boolean renameProject(Project project)
	{
		TextInputDialog dialog = new TextInputDialog(project.getName());
		dialog.setTitle("Rename Project");
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		dialog.setContentText("Enter a new name for the project:");
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
		{
			String newName = result.get();
			if (newName.equals(project.getName()))
			{
				showInfoDialogue("The new name must be different from the old name");
				return renameProject(project);
			}
			else
			{
				project.setName(newName);
			}
		}
		
		return false;
	}
	
	private Project getProjectByName(String name)
	{
		for (Project project : projects)
		{
			String projectName = project.getName();
			boolean namesAreNull = (projectName == null && name == null);
			if (namesAreNull || name.equals(projectName))
				return project;
		}
		
		return null;
	}
	
	private void navigateToLabel(PLPLabel label)
	{
		CodeEditor editor = getActiveCodeEditor();
		if (editor == null)
			throw new IllegalStateException("Cannot access active code editor");
		
		try
		{
			int lineNumber = label.getLineNumber();
			editor.jumpToLine(lineNumber);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}
	
	/**
	 * Creates a tab for the specified project, or selects the project, if the tab already
	 * exists.
	 * 
	 * @param project
	 *            The project to open
	 */
	private void openFile(ASMFile file)
	{
		file.contentProperty().addListener(this::updateOutline);
		String fileName = file.getName();
		
		System.out.println("Opening " + fileName);
		Tab tab = openFileTabs.get(file);
		
		if (tab == null)
		{
			// Create new tab
			CodeEditor content = createCodeEditor();
			tab = addTab(openProjectsPanel, fileName, content);
			openFileTabs.put(file, tab);
			
			// Set content
			if (file.getContent() != null)
				content.setText(file.getContent());
			else
				content.setText("");
			
			// Bind content
			file.contentProperty().bind(content);
		}
		
		// Activate the specified tab
		openProjectsPanel.getSelectionModel().select(tab);
	}
	
<<<<<<< HEAD
<<<<<<< HEAD
	private void saveProject(MouseEvent event)
	{
		try
		{
			getActiveProject().save();
		}
		catch (IOException e)
		{
			// TODO report exception to user
			e.printStackTrace();
		}
	}
	
	private void saveAll()
	{
		for(Project project : projects)
		{
			try
			{
				project.save();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
=======
>>>>>>> 999bc8d28400b44b63c91477649f4e96a6c851de
	private void saveProjectAs()
=======
	@Override
	public void saveActiveProjectAs()
>>>>>>> 545c8daa502b236a58a4d36646d4ab59d2443399
	{
		Stage createProjectStage = new Stage();
		Parent myPane = saveAsMenu();
		Scene scene = new Scene(myPane, 600, 350);
		createProjectStage.setTitle("Save Project As");
		createProjectStage.setScene(scene);
		createProjectStage.setResizable(false);
		createProjectStage.show();
	}
	
	private Parent saveAsMenu()
	{
		BorderPane border = new BorderPane();
		border.setPadding(new Insets(20));
		GridPane grid = new GridPane();
		HBox buttons = new HBox(10);
		grid.setHgap(10);
		grid.setVgap(30);
		grid.setPadding(new Insets(10, 10, 10, 10));
		
		Label projectName = new Label();
		projectName.setText("New Project Name: ");
		projectName.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		TextField projTextField = new TextField();
		projTextField.setText("Project Name");
		projTextField.requestFocus();
		projTextField.setPrefWidth(200);
		
		Label selectedProject = new Label();
		selectedProject.setText("Save Project: \"" + getActiveProject().getName()
				+ "\" as :");
		selectedProject.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		Label projectLocation = new Label();
		projectLocation.setText("Location: ");
		projectLocation.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		TextField projLocationField = new TextField();
		projTextField.setPrefWidth(200);
		
		Button browseLocation = new Button();
		browseLocation.setText("Browse");
		browseLocation.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				String chosenLocation = "";
				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setTitle("Choose Project Location");
				File file = directoryChooser.showDialog(null);
				if (file != null)
				{
					chosenLocation = file.getAbsolutePath().concat(
							File.separator + projTextField.getText());
					projLocationField.setText(chosenLocation);
				}
				
			}
		});
		
		Button saveAsButton = new Button("Save");
		saveAsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				String projectName = projTextField.getText();
				String projectLocation = projLocationField.getText();
				if (projectName == null || projectName.trim().isEmpty())
				{
					Dialogues.showInfoDialogue("You entered an invalid Project Name");
				}
				else if (projectLocation == null || projectLocation.trim().isEmpty())
				{
					Dialogues.showInfoDialogue("You entered an invalid Project Location");
					
				}
				else
				{
					Project activeProject = getActiveProject();
					try
					{
						activeProject.saveAs(projectLocation);
					}
					catch (IOException ioException)
					{
						Dialogues.showAlertDialogue(ioException);
						ioException.printStackTrace();
					}
					Stage stage = (Stage) saveAsButton.getScene().getWindow();
					stage.close();
				}
			}
		});
		saveAsButton.setDefaultButton(true);
		Button cancelCreate = new Button("Cancel");
		cancelCreate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				Stage stage = (Stage) cancelCreate.getScene().getWindow();
				stage.close();
			}
		});
		
		grid.add(projectName, 0, 0);
		grid.add(projTextField, 1, 0);
		// grid.add(selectedProject, 0, 1);
		grid.add(projectLocation, 0, 2);
		grid.add(projLocationField, 1, 2);
		grid.add(browseLocation, 2, 2);
		
		border.setTop(selectedProject);
		border.setCenter(grid);
		
		buttons.getChildren().addAll(saveAsButton, cancelCreate);
		buttons.setAlignment(Pos.BASELINE_RIGHT);
		border.setBottom(buttons);
		
		return Components.wrap(border);
	}
	
	private List<PLPLabel> scrapeLabelsInActiveTab()
	{
		Tab selectedTab = openProjectsPanel.getSelectionModel().getSelectedItem();
		if (selectedTab == null)
			return Collections.emptyList();
		else
		{
			ASMFile activeASM = openFileTabs.getKey(selectedTab);
			String content = activeASM.getContent();
			return PLPLabel.scrape(content);
		}
	}
	
	private CodeEditor createCodeEditor()
	{
		return new CodeEditor();
	}
	
	private Tab addTab(TabPane panel, String projectName, Node contentPanel)
	{
		Tab tab = new Tab();
		tab.setText(projectName);
		tab.setContent(contentPanel);
		tab.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event event)
			{
				ASMFile asmFile = openFileTabs.removeValue(tab);
				if (asmFile != null)
				{
					asmFile.contentProperty().removeListener(Main.this::updateOutline);
				}
			}
		});
		tab.setOnSelectionChanged(new EventHandler<Event>() {
			@Override
			public void handle(Event event)
			{
				ASMFile activeFile = openFileTabs.getKey(tab);
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
	
	private OutlineView createOutlineView()
	{
		List<PLPLabel> activeLabels = scrapeLabelsInActiveTab();
		activeNavigationItems = FXCollections.observableArrayList(activeLabels);
		
		OutlineView outlineView = new OutlineView(activeNavigationItems);
		outlineView.setOnAction(this::navigateToLabel);
		
		return outlineView;
	}
	
	/**
	 * Restore all projects from a persistent data store, and call
	 * {@link #openProject(String, String)} for each
	 */
	private void loadOpenProjects()
	{
		// TODO: replace with actual content
		try
		{
			PLPProject project;
			project = PLPProject.load(new File("examples/PLP Projects/memtest.plp"));
			projects.add(project);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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
		
		projectExplorer.setOnFileDoubleClicked(this::openFile);
		
		return projectExplorer;
	}
	
	/**
	 * Creates a horizontal toolbar containing controls to:
	 * <ul>
	 * <li>Create a new PLPProject
	 * <li>Add a new file
	 * <li>Save the current project
	 * <li>Open a new PLPProject
	 * <li>Assemble the current project
	 * </ul>
	 * 
	 * @return a Parent {@link Node} representing the PLP toolbar
	 */
	private Parent createToolbar()
	{
		MainToolbar toolbar = new MainToolbar(this);
		return toolbar;
	}
	
<<<<<<< HEAD
	private void onRunProjectClicked()
	{
		Project activeProject = getActiveProject();
		
		ProjectAssemblyDetails details = assemblyDetails.get(activeProject);
		if (details != null && !details.isDirty())
		{
			run(activeProject);
		}
		else
		{
			// TODO: handle "Project Not Assembled" case
			throw new UnsupportedOperationException("Not yet implemented");
		}
	}
	
	private void run(Project project)
	{
		Optional<ISAModule> optionalISA = project.getISA();
		if (optionalISA.isPresent())
		{
			ISAModule isa = optionalISA.get();
			Simulator simulator = isa.getSimulator();
			simulator.run();
		}
		else
		{
			// TODO: handle "no compatible ISA" case
			throw new UnsupportedOperationException("Not yet implemented");
		}
	}
<<<<<<< HEAD

	private Parent createMenuBar()
	{
		MenuBar menuBar = new MenuBar();
		
		// Menu Items under "File"
		Menu file = new Menu("File");
		MenuItem itemNew = new MenuItem("New PLP Project");
		itemNew.setGraphic(new ImageView(new Image("menu_new.png")));
		itemNew.setAccelerator(
				new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		itemNew.setOnAction((event) -> {
			createNewProject();
		});
		
		MenuItem itemOpen = new MenuItem("Open PLP Project");
		itemOpen.setGraphic(new ImageView(new Image("toolbar_open.png")));
		itemOpen.setAccelerator(
				new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		itemOpen.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSave = new MenuItem("Save");
		itemSave.setGraphic(new ImageView(new Image("toolbar_save.png")));
		itemSave.setAccelerator(
				new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		itemSave.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSaveAs = new MenuItem("Save As");
		itemSaveAs.setOnAction((event) -> {
			// TODO: Add Event for menu item
			saveProjectAs();
		});
		
		MenuItem itemSaveAll = new MenuItem("Save All");
		itemSaveAll.setAccelerator(new KeyCodeCombination(KeyCode.A,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemSaveAll.setOnAction((event) -> {
			// TODO: Add Event for menu item
			saveAll();
		});
		
		MenuItem itemPrint = new MenuItem("Print");
		itemPrint.setAccelerator(
				new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
		itemPrint.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemExit = new MenuItem("Exit");
		itemExit.setAccelerator(
				new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
		itemExit.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		file.getItems().addAll(itemNew, new SeparatorMenuItem(), itemOpen, itemSave,
				itemSaveAs, itemSaveAll, new SeparatorMenuItem(), itemPrint, new SeparatorMenuItem(),
				itemExit);
				
		// Menu Items under "Edit"
		Menu edit = new Menu("Edit");
		MenuItem itemCopy = new MenuItem("Copy");
		itemCopy.setAccelerator(
				new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
		itemCopy.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemCut = new MenuItem("Cut");
		itemCut.setAccelerator(
				new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
		itemCut.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemPaste = new MenuItem("Paste");
		itemPaste.setAccelerator(
				new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
		itemPaste.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemFandR = new MenuItem("Find and Replace");
		itemFandR.setAccelerator(
				new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
		itemFandR.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemUndo = new MenuItem("Undo");
		itemUndo.setAccelerator(
				new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
		itemUndo.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemRedo = new MenuItem("Redo");
		itemRedo.setAccelerator(
				new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
		itemRedo.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		edit.getItems().addAll(itemCopy, itemCut, itemPaste, new SeparatorMenuItem(),
				itemFandR, new SeparatorMenuItem(), itemUndo, itemRedo);
				
		// Menu Items under "View"
		Menu view = new Menu("View");
		CheckMenuItem cItemToolbar = new CheckMenuItem("Toolbar");
		cItemToolbar.setAccelerator(new KeyCodeCombination(KeyCode.T,
				KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
		cItemToolbar.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		CheckMenuItem cItemProjectPane = new CheckMenuItem("Project Pane");
		cItemProjectPane.setAccelerator(new KeyCodeCombination(KeyCode.P,
				KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
		cItemProjectPane.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		CheckMenuItem cItemOutputPane = new CheckMenuItem("Output Pane");
		cItemOutputPane.setAccelerator(new KeyCodeCombination(KeyCode.O,
				KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
		cItemOutputPane.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemClearOutput = new MenuItem("Clear Output Pane");
		itemClearOutput.setAccelerator(
				new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
		itemClearOutput.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		view.getItems().addAll(cItemToolbar, cItemProjectPane, cItemOutputPane,
				itemClearOutput);
		cItemToolbar.setSelected(true);
		cItemProjectPane.setSelected(true);
		cItemOutputPane.setSelected(true);
		
		// Menu Items Under "Project"
		Menu project = new Menu("Project");
		MenuItem itemAssemble = new MenuItem("Assemble");
		itemAssemble.setGraphic(new ImageView(new Image("toolbar_assemble.png")));
		itemAssemble.setAccelerator(new KeyCodeCombination(KeyCode.F2));
		itemAssemble.setOnAction((event) -> {
			console.println("Assemble Menu Item Clicked");
			Project activeProject = getActiveProject();
			assemble(activeProject);
		});
		
		MenuItem itemSimulate = new MenuItem("Simulate");
		itemSimulate.setGraphic(new ImageView(new Image("toolbar_simulate.png")));
		itemSimulate.setAccelerator(new KeyCodeCombination(KeyCode.F3));
		itemSimulate.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemPLPBoard = new MenuItem("Program PLP Board...");
		itemPLPBoard.setGraphic(new ImageView(new Image("toolbar_program.png")));
		itemPLPBoard.setAccelerator(
				new KeyCodeCombination(KeyCode.F4, KeyCombination.SHIFT_DOWN));
		itemPLPBoard.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemQuickProgram = new MenuItem("Quick Program");
		itemQuickProgram.setAccelerator(new KeyCodeCombination(KeyCode.F4));
		itemQuickProgram.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemNewASM = new MenuItem("New ASM File...");
		itemNewASM.setOnAction((event) -> {
			createASMFile(null);
			// TODO: Check this implementation, doesnt look correct
		});
		
		MenuItem itemImportASM = new MenuItem("Import ASM File...");
		itemImportASM.setOnAction((event) -> {
			File importTarget = showImportDialogue();
			try
			{
				String content = FileUtils.readFileToString(importTarget);
				Project activeProject = getActiveProject();
				String name = importTarget.getName();
				
				// TODO: account for non-PLP source files
				ASMFile asmFile = new PLPSourceFile(activeProject, name);
				asmFile.setContent(content);
				activeProject.add(asmFile);
				activeProject.save();
			}
			catch (Exception exception)
			{
				Dialogues.showAlertDialogue(exception, "Failed to import asm");
			}
		});
		
		MenuItem itemExportASM = new MenuItem("Export Selected ASM File...");
		itemExportASM.setOnAction((event) -> {
			ASMFile activeFile = getActiveFile();
			if (activeFile == null)
			{
				// XXX: possible feature: select file from a list or dropdown
				String message = "No file is selected! Open the file you wish to export, or select it in the ProjectExplorer.";
				Dialogues.showInfoDialogue(message);
			}
			
			File exportTarget = showExportDialogue(activeFile);
			if (exportTarget == null)
				return;
			
			if (exportTarget.isDirectory())
			{
				String exportPath = exportTarget.getAbsolutePath() 
						+ activeFile.constructFileName();
				exportTarget = new File(exportPath);
				
				String message = "File will be exported to " + exportPath;
				Optional<ButtonType> result = Dialogues.showConfirmationDialogue(message);
				
				if (result.get() != ButtonType.OK)
				{
					// Export was canceled
					return;
				}
			}
			
			if (exportTarget.exists())
			{
				String message = "The specified file already exists. Press OK to overwrite this file, or cancel to cancel the export.";
				Optional<ButtonType> result = Dialogues.showConfirmationDialogue(message);
				
				if (result.get() != ButtonType.OK)
				{
					// Export was canceled
					return;
				}
			}
			
			String fileContents = activeFile.getContent();
			try
			{
				FileUtils.write(exportTarget, fileContents);
			}
			catch (Exception exception)
			{
				Dialogues.showAlertDialogue(exception, "Failed to export asm");
			}
		});
		
		MenuItem itemRemoveASM = new MenuItem("Remove Selected ASM File from Project");
		itemRemoveASM.setAccelerator(
				new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
		itemRemoveASM.setOnAction((event) -> {
			removeActiveFile();
		});
		
		MenuItem itemCurrentAsMain = new MenuItem(
				"Set Current Open File as Main Program");
		itemCurrentAsMain.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		project.getItems().addAll(itemAssemble, itemSimulate, itemPLPBoard,
				itemQuickProgram, new SeparatorMenuItem(), itemNewASM, itemImportASM,
				itemExportASM, itemRemoveASM, new SeparatorMenuItem(), itemCurrentAsMain);
				
		// Menu Items Under "Tools"
		Menu tools = new Menu("Tools");
		MenuItem itemOptions = new MenuItem("Options");
		itemOptions.setOnAction((event) -> {
			OptionsFrame.options();
		});
		
		Menu modules = new Menu("Modules");
		MenuItem itemModuleManager = new MenuItem("Module Manager...");
		itemModuleManager.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemLoadJar = new MenuItem("Load Module JAR File...");
		itemLoadJar.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemClearCache = new MenuItem("Clear Module Auto-Load Cache");
		itemClearCache.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSerialTerminal = new MenuItem("Serial Terminal");
		itemSerialTerminal.setAccelerator(
				new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));
		itemSerialTerminal.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemNumConverter = new MenuItem("Number Converter");
		itemNumConverter.setAccelerator(new KeyCodeCombination(KeyCode.F12));
		itemNumConverter.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		modules.getItems().addAll(itemModuleManager, itemLoadJar, itemClearCache);
		tools.getItems().addAll(itemOptions, modules, new SeparatorMenuItem(),
				itemSerialTerminal, itemNumConverter);
				
		// Menu Items Under "Simulation"
		Menu simulation = new Menu("Simulation");
		MenuItem itemStep = new MenuItem("Step");
		itemStep.setGraphic(new ImageView(new Image("toolbar_step.png")));
		itemStep.setAccelerator(new KeyCodeCombination(KeyCode.F5));
		itemStep.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemReset = new MenuItem("Reset");
		itemReset.setGraphic(new ImageView(new Image("toolbar_reset.png")));
		itemReset.setAccelerator(new KeyCodeCombination(KeyCode.F9));
		itemReset.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemRun = new MenuItem("Run");
		itemRun.setAccelerator(new KeyCodeCombination(KeyCode.F7));
		itemRun.setOnAction(this::onRunProjectClicked);
		Menu cyclesSteps = new Menu("Cycles/Steps");
		MenuItem itemOne = new MenuItem("1");
		itemOne.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD1, KeyCombination.ALT_DOWN));
		itemOne.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemFive = new MenuItem("5");
		itemFive.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD2, KeyCombination.ALT_DOWN));
		itemFive.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemTwenty = new MenuItem("20");
		itemTwenty.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD3, KeyCombination.ALT_DOWN));
		itemTwenty.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemHundred = new MenuItem("100");
		itemHundred.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD4, KeyCombination.ALT_DOWN));
		itemHundred.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemFiveThousand = new MenuItem("5000");
		itemFiveThousand.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD5, KeyCombination.ALT_DOWN));
		itemFiveThousand.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemClearBreakpoints = new MenuItem("Clear Breakpoints");
		itemClearBreakpoints.setAccelerator(
				new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN));
		itemClearBreakpoints.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		Menu views = new Menu("Views");
		MenuItem itemCpuView = new MenuItem("CPU View");
		itemCpuView.setAccelerator(new KeyCodeCombination(KeyCode.C,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemCpuView.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemCpuWindow = new MenuItem("Watcher Window");
		itemCpuWindow.setAccelerator(new KeyCodeCombination(KeyCode.W,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemCpuWindow.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSimControlWindow = new MenuItem("Simulation Control Window");
		itemSimControlWindow.setAccelerator(
				new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
		itemSimControlWindow.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		Menu toolsSubMenu = new Menu("Tools");
		MenuItem itemioRegistry = new MenuItem("I/O Registry");
		itemioRegistry.setAccelerator(new KeyCodeCombination(KeyCode.R,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemioRegistry.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemASMView = new MenuItem("ASM View");
		itemASMView.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemCreateMemVis = new MenuItem("Create a PLP CPU Memory Visualizer");
		itemCreateMemVis.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemRemoveMemVis = new MenuItem(
				"Remove Memory Visualizers from Project");
		itemRemoveMemVis.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemDisplayBus = new MenuItem("Display Bus Monitor Timing Diagram");
		itemDisplayBus.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		Menu ioDevices = new Menu("I/O Devices");
		MenuItem itemLedArray = new MenuItem("LED Array");
		itemLedArray.setGraphic(new ImageView(new Image("toolbar_sim_leds.png")));
		itemLedArray.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD1, KeyCombination.CONTROL_DOWN));
		itemLedArray.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSwitches = new MenuItem("Switches");
		itemSwitches.setGraphic(new ImageView(new Image("toolbar_sim_switches.png")));
		itemSwitches.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD2, KeyCombination.CONTROL_DOWN));
		itemSwitches.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSevenSeg = new MenuItem("Seven Segments");
		itemSevenSeg.setGraphic(new ImageView(new Image("toolbar_sim_7segments.png")));
		itemSevenSeg.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD3, KeyCombination.CONTROL_DOWN));
		itemSevenSeg.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemUART = new MenuItem("UART");
		itemUART.setGraphic(new ImageView(new Image("toolbar_sim_uart.png")));
		itemUART.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD4, KeyCombination.CONTROL_DOWN));
		itemUART.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemVGA = new MenuItem("VGA");
		itemVGA.setGraphic(new ImageView(new Image("toolbar_sim_vga.png")));
		itemVGA.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD5, KeyCombination.CONTROL_DOWN));
		itemVGA.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemPLPID = new MenuItem("PLPID");
		itemPLPID.setGraphic(new ImageView(new Image("toolbar_sim_plpid.png")));
		itemPLPID.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD6, KeyCombination.CONTROL_DOWN));
		itemPLPID.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemGPIO = new MenuItem("GPIO");
		itemGPIO.setGraphic(new ImageView(new Image("toolbar_sim_gpio.png")));
		itemGPIO.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD7, KeyCombination.CONTROL_DOWN));
		itemGPIO.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemExitSim = new MenuItem("ExitSimulation");
		itemExitSim.setAccelerator(new KeyCodeCombination(KeyCode.F11));
		itemExitSim.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		cyclesSteps.getItems().addAll(itemOne, itemFive, itemTwenty, itemHundred,
				itemFiveThousand);
		views.getItems().addAll(itemCpuView, itemCpuWindow, itemSimControlWindow);
		toolsSubMenu.getItems().addAll(itemioRegistry, itemASMView,
				new SeparatorMenuItem(), itemCreateMemVis, itemRemoveMemVis,
				itemDisplayBus);
		ioDevices.getItems().addAll(itemLedArray, itemSwitches, itemSevenSeg, itemUART,
				itemVGA, itemPLPID, itemGPIO);
		simulation.getItems().addAll(itemStep, itemReset, new SeparatorMenuItem(),
				itemRun, cyclesSteps, itemClearBreakpoints, new SeparatorMenuItem(),
				views, toolsSubMenu, ioDevices, new SeparatorMenuItem(), itemExitSim);
				
		// Menu Items Under "Help"
		Menu help = new Menu("Help");
		MenuItem itemQuickRef = new MenuItem("Quick Reference");
		itemQuickRef.setAccelerator(new KeyCodeCombination(KeyCode.F1));
		itemQuickRef.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemOnlineManual = new MenuItem("Online Manual");
		itemOnlineManual.setOnAction((event) -> {
			onlineManualWeb();
		});
		
		MenuItem itemReportIssue = new MenuItem("Report Issue (Requires Google Account");
		itemReportIssue.setOnAction((event) -> {
			reportIssuesWeb();
		});
		
		MenuItem itemGoogleIssues = new MenuItem("Open Google Code Issues Page");
		itemGoogleIssues.setOnAction((event) -> {
			openIssuesPageWeb();
		});
		
		MenuItem itemAboutPLP = new MenuItem("About PLP Tool...");
		itemAboutPLP.setOnAction((event) -> {
			aboutPLPToolWeb();
		});
		
		MenuItem itemSWLicense = new MenuItem("Third Party Software License");
		itemSWLicense.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		help.getItems().addAll(itemQuickRef, itemOnlineManual, new SeparatorMenuItem(),
				itemReportIssue, itemGoogleIssues, new SeparatorMenuItem(), itemAboutPLP,
				itemSWLicense);
				
		menuBar.getMenus().addAll(file, edit, view, project, tools, simulation, help);
		
		return Components.wrap(menuBar);
	}
	
	private void onOpenProjectClicked(MouseEvent event)
	{
		console.println("Open Project Clicked");
		openProjectFromFile();
	}
=======
>>>>>>> 999bc8d28400b44b63c91477649f4e96a6c851de
	
=======
>>>>>>> 545c8daa502b236a58a4d36646d4ab59d2443399
	private Parent createMenuBar()
	{
		PLPToolMenuBarPanel menuBar = new PLPToolMenuBarPanel(this);
		return menuBar;
	}
	
	private void assemble(Project project)
	{
		Optional<ISAModule> optionalISA = project.getISA();
		if (optionalISA.isPresent())
		{
			ISAModule isa = optionalISA.get();
			Assembler assembler = isa.getAssembler();
			assemble(assembler, project);
		}
		else
		{
			// TODO: handle "no compatible ISA" case
			throw new UnsupportedOperationException("Not yet implemented");
		}
	}
	
	private void assemble(Assembler assembler, Project project)
	{
		try
		{
			ASMImage assembledImage = assembler.assemble(project);
			ProjectAssemblyDetails details = getAssemblyDetailsFor(project);
			details.setAssembledImage(assembledImage);
		}
		catch (AssemblerException exception)
		{
			console.error(exception.getLocalizedMessage());
		}
	}
	
	private ProjectAssemblyDetails getAssemblyDetailsFor(Project activeProject)
	{
		ProjectAssemblyDetails details = assemblyDetails.get(activeProject);
		
		if (details == null)
		{
			details = new ProjectAssemblyDetails(activeProject);
			assemblyDetails.put(activeProject, details);
		}
		
		return details;
	}
	
	private Project getActiveProject()
	{
		ASMFile activeFile = getActiveFile();
		if (activeFile == null)
			return null;
		else
			return activeFile.getProject();
	}
	
	private ASMFile getActiveFileInTabPane()
	{
		Tab selectedTab = openProjectsPanel.getSelectionModel().getSelectedItem();
		return openFileTabs.getKey(selectedTab);
	}
	
	private CodeEditor getActiveCodeEditor()
	{
		Tab activeTab = openProjectsPanel.getSelectionModel().getSelectedItem();
		if (activeTab == null)
			return null;
		
		Node tabContents = activeTab.getContent();
		if (tabContents != null)
			return (CodeEditor) tabContents;
		else
			return null;
	}
	
	private ASMFile getActiveFileInProjectExplorer()
	{
		Pair<Project, ASMFile> selection = projectExplorer.getActiveSelection();
		if (selection == null)
			return null;
		
		ASMFile selectedFile = selection.getValue();
		return selectedFile;
	}
	
	private ASMFile getActiveFile()
	{
		ASMFile selectedFile = getActiveFileInTabPane();
		if (selectedFile == null)
			return getActiveFileInProjectExplorer();
		else
			return selectedFile;
	}
	
	private ASMCreationPanel createASMMenu()
	{
<<<<<<< HEAD
<<<<<<< HEAD
		BorderPane border = new BorderPane();
		border.setPadding(new Insets(20));
		GridPane grid = new GridPane();
		HBox buttons = new HBox(10);
		grid.setHgap(10);
		grid.setVgap(30);
		grid.setPadding(new Insets(10, 10, 10, 10));
		
		Label ASMFileName = new Label();
		ASMFileName.setText("File Name: ");
		ASMFileName.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		TextField nameText = new TextField();
		nameText.setText("");
		nameText.requestFocus();
		nameText.setPrefWidth(200);
		
		Label projectName = new Label();
		projectName.setText("Add to Project: ");
		projectName.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		TextField projectText = new TextField();
		projectText.setText(getActiveProject().getName());
		projectText.setPrefWidth(200);
		
		Button create = new Button();
		create.setText("Create");
		create.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				String projectName = projectText.getText();
				String fileName = nameText.getText();
				
				if (projectName.trim().isEmpty() || getProjectByName(projectName).equals(null))
				{
					Dialogues.showInfoDialogue("You entered an invalid Project Name");
					
				}
				
				if (fileName == null || fileName.trim().isEmpty())
				{
					Dialogues.showInfoDialogue("You entered an invalid File Name");
				}
				
				if (!fileName.contains(".asm"))
				{
					fileName = fileName.concat(".asm");
				}
				
				PLPSourceFile createASM = new PLPSourceFile(getProjectByName(projectName),
						fileName);
				getProjectByName(projectName).add(createASM);
				openFile(createASM);
				
				Stage stage = (Stage) create.getScene().getWindow();
				stage.close();
			}
			
		});
		
		grid.add(ASMFileName, 0, 0);
		grid.add(nameText, 1, 0);
		grid.add(projectName, 0, 1);
		grid.add(projectText, 1, 1);
		
		border.setCenter(grid);
		buttons.getChildren().add(create);
		buttons.setAlignment(Pos.BASELINE_RIGHT);
		border.setBottom(buttons);
=======
=======
>>>>>>> 545c8daa502b236a58a4d36646d4ab59d2443399
		ASMCreationPanel createASMMenu = new ASMCreationPanel(this::createASM);
		for (Project project : projects)
		{
			String projectName = project.getName();
			createASMMenu.addProjectName(projectName);
		}
		return createASMMenu;
	}
	
	private void createASM(ASMCreationDetails details)
	{
		String projectName = details.getProjectName();
		String fileName = details.getFileName();
<<<<<<< HEAD
>>>>>>> 999bc8d28400b44b63c91477649f4e96a6c851de
=======
>>>>>>> 545c8daa502b236a58a4d36646d4ab59d2443399
		
		Project project = getProjectByName(projectName);
		if (project != null)
		{
			SimpleASMFile createASM = new SimpleASMFile(project, fileName);
			project.add(createASM);
			openFile(createASM);
		}
		else
		{
			String message = "The project \"" + projectName + "\" could not be found";
			throw new IllegalArgumentException(message);
		}
	}
	
	@Override
	public void createNewProject()
	{
		Stage createProjectStage = new Stage();
		ProjectCreationPanel projectCreationPanel = projectCreateMenu();
		projectCreationPanel.setFinallyOperation(createProjectStage::close);
		
		Scene scene = new Scene(projectCreationPanel, 450, 350);
		createProjectStage.setTitle("Create New PLP Project");
		createProjectStage.setScene(scene);
		createProjectStage.setResizable(false);
		createProjectStage.show();
	}
	
	private ProjectCreationPanel projectCreateMenu()
	{
		ProjectCreationPanel projectCreationPanel = new ProjectCreationPanel();
		projectCreationPanel.addProjectType("PLP6", this::createProject);
		projectCreationPanel.addProjectType("PLP5 (Legacy)", this::createLegacyProject);
		projectCreationPanel.setSelectedType("PLP6");
		return projectCreationPanel;
	}
	
	private void createLegacyProject(ProjectCreationDetails details)
	{
		PLPProject project = new PLPProject(details.getProjectName());
		project.setPath(details.getProjectLocation());
		
		String sourceName = details.getMainSourceFileName();
		SimpleASMFile sourceFile = new SimpleASMFile(project, sourceName);
		project.add(sourceFile);
		tryAndReport(project::saveLegacy);
		projects.add(project);
		openFile(sourceFile);
	}
	
	private void createProject(ProjectCreationDetails details)
	{
		PLPProject project = new PLPProject(details.getProjectName());
		project.setPath(details.getProjectLocation());
		
		String sourceName = details.getMainSourceFileName();
		SimpleASMFile sourceFile = new SimpleASMFile(project, sourceName);
		project.add(sourceFile);
		tryAndReport(project::save);
		projects.add(project);
		openFile(sourceFile);
	}
	
	private void tryAndReport(ExceptionalSubroutine subroutine)
	{
		try
		{
			subroutine.perform();
		}
		catch (Exception exception)
		{
			Dialogues.showAlertDialogue(exception);
		}
	}
	
	public void showEmulationWindow()
	{
		Stage createEmulationStage = new Stage();
		EmulationWindow emulationWindow = new EmulationWindow();
		
		Scene scene = new Scene(emulationWindow, 1275, 700);
		createEmulationStage.setTitle("Emulation Window");
		createEmulationStage.setScene(scene);
		// createEmulationStage.setResizable(false);
		createEmulationStage.show();
	}
	
	public void openCpuViewWindow()
	{
		Stage createCpuStage = new Stage();
		CpuWindow cpuWindowView = new CpuWindow();
		
		Scene scene = new Scene(cpuWindowView, 1200, 700);
		createCpuStage.setTitle("PLP CPU Core Simulation");
		createCpuStage.setScene(scene);
		createCpuStage.show();
	}
	
	private boolean optionsMenuOkSelected(List<Submittable> submittables)
	{
		for (Submittable submittable : submittables)
		{
			if (!submittable.isValid())
				return false;
		}
		return true;
	}
	
	private HashMap<OptionSection, Pane> createOptionsMenuModel(
			List<Submittable> submittables)
	{
		HashMap<OptionSection, Pane> model = new LinkedHashMap<>();
		
		addApplicationOptionSettings(model, submittables);
		addEditorOptionSettings(model, submittables);
		addASimulatorOptionSettings(model, submittables);
		addProgrammerOptionSettings(model, submittables);
		
		// TODO Accept new things
		
		return model;
	}
	
	private void addApplicationOptionSettings(HashMap<OptionSection, Pane> model,
			List<Submittable> submittables)
	{
		PLPOptions applicationSection = new PLPOptions("Application");
		
		ObservableList<String> applicationThemeNames = FXCollections
				.observableArrayList();
		applicationThemeNames.addAll(applicationThemeManager.getThemeNames());
		
		// TODO acquire editor theme names
		// TODO add filters, disabling sounds retarded. Just filter and put non adjacent
		// at bottom
		ObservableList<String> editorThemeNames = FXCollections.observableArrayList();
		editorThemeNames.addAll("eclipse", "tomorrow", "xcode", "ambiance", "monokai",
				"twilight");
		
		ApplicationSettingsPanel applicationPanel = new ApplicationSettingsPanel(
				applicationThemeNames, editorThemeNames);
		submittables.add(applicationPanel);
		
		model.put(applicationSection, applicationPanel);
	}
	
	private void addEditorOptionSettings(HashMap<OptionSection, Pane> model,
			List<Submittable> submittables)
	{
		PLPOptions editorSection = new PLPOptions("Editor");
		ObservableList<String> fontNames = getAvailableFontNames();
		
		// TODO acquire editor modes
		ObservableList<String> editorModes = FXCollections.observableArrayList();
		editorModes.addAll("plp");
		
		EditorSettingsPanel editorPanel = new EditorSettingsPanel(fontNames, editorModes);
		submittables.add(editorPanel);
		
		model.put(editorSection, editorPanel);
	}
	
	private ObservableList<String> getAvailableFontNames()
	{
		ObservableList<String> fontNames = FXCollections.observableArrayList();
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		java.awt.Font[] fonts = graphicsEnvironment.getAllFonts();
		for (java.awt.Font font : fonts)
		{
			String fontName = font.getFontName();
			fontNames.add(fontName);
		}
		
		return fontNames;
	}
	
	private void addASimulatorOptionSettings(HashMap<OptionSection, Pane> model,
			List<Submittable> submittables)
	{
		PLPOptions simulatorSection = new PLPOptions("Simulator");
		
		SimulatorSettingsPanel simulatorPanel = new SimulatorSettingsPanel();
		submittables.add(simulatorPanel);
		
		model.put(simulatorSection, simulatorPanel);
	}
	
	private void addProgrammerOptionSettings(HashMap<OptionSection, Pane> model,
			List<Submittable> submittables)
	{
		PLPOptions programmerSection = new PLPOptions("Programmer");
		
		ProgrammerSettingsPanel programmerPanel = new ProgrammerSettingsPanel();
		submittables.add(programmerPanel);
		
		model.put(programmerSection, programmerPanel);
	}
	
	public class ApplicationEventBusEventHandler
	{
		private ApplicationEventBusEventHandler()
		{
			
		}
		
		@Subscribe
		public void applicationThemeRequestCallback(ThemeRequestCallback event)
		{
			if (event.requestedTheme().isPresent())
			{
				Theme applicationTheme = event.requestedTheme().get();
				try
				{
					stage.getScene().getStylesheets().clear();
					stage.getScene().getStylesheets().add(applicationTheme.getPath());
					return;
				}
				catch (MalformedURLException e)
				{
					console.warning("Unable to load application theme "
							+ applicationTheme.getName());
					return;
				}
			}
			
			console.warning("Unable to load application theme.");
		}
		
		@Subscribe
		public void deadEvent(DeadEvent event)
		{
			System.out.println("Dead Event");
			System.out.println(event.getEvent());
		}
	}
	
	@Override
	public void saveActiveProject()
	{
		Project activeProject = getActiveProject();
		tryAndReport(activeProject::save);
	}
	
	@Override
	public void printActiveFile()
	{
		CodeEditor activeEditor = getActiveCodeEditor();
		if (activeEditor == null)
		{
			Dialogues.showActionFailedDialogue("No file is open!");
			return;
		}
		
		PrinterJob printAction = PrinterJob.createPrinterJob();
		if (printAction == null)
		{
			Dialogues.showActionFailedDialogue("Unable to access system print utilities");
			return;
		}
		
		boolean notCancelled = printAction.showPrintDialog(stage);
		if (notCancelled)
		{
			boolean success = printAction.printPage(activeEditor);
			if (success)
				printAction.endJob();
			else
				Dialogues.showActionFailedDialogue("Print may have failed");
		}
	}
	
	@Override
	public void createNewASM()
	{
		if (projects.isEmpty())
		{
			Dialogues
					.showInfoDialogue("There are not projects open, please create a project first.");
		}
		else
		{
			Stage createASMStage = new Stage();
			ASMCreationPanel asmCreationMenu = createASMMenu();
			asmCreationMenu.setFinallyOperation(createASMStage::close);
			
			Scene scene = new Scene(asmCreationMenu, 450, 200);
			createASMStage.setTitle("New ASMFile");
			createASMStage.setScene(scene);
			createASMStage.setResizable(false);
			createASMStage.show();
		}
	}
	
	@Override
	public void importASM()
	{
		File importTarget = showImportDialogue();
		try
		{
			String content = FileUtils.readFileToString(importTarget);
			Project activeProject = getActiveProject();
			String name = importTarget.getName();
			
			ASMFile asmFile = new SimpleASMFile(activeProject, name);
			asmFile.setContent(content);
			activeProject.add(asmFile);
			activeProject.save();
		}
		catch (Exception exception)
		{
			Dialogues.showAlertDialogue(exception, "Failed to import asm");
		}
	}
	
	@Override
	public void exportASM()
	{
		// XXX: Consider moving this to a component
		ASMFile activeFile = getActiveFile();
		if (activeFile == null)
		{
			// XXX: possible feature: select file from a list or dropdown
			String message = "No file is selected! Open the file you wish to export, or select it in the ProjectExplorer.";
			Dialogues.showInfoDialogue(message);
			return;
		}
		
		File exportTarget = showExportDialogue(activeFile);
		if (exportTarget == null)
			return;
		
		if (exportTarget.isDirectory())
		{
			String exportPath = exportTarget.getAbsolutePath()
					+ activeFile.constructFileName();
			exportTarget = new File(exportPath);
			
			String message = "File will be exported to " + exportPath;
			Optional<ButtonType> result = Dialogues.showConfirmationDialogue(message);
			
			if (result.get() != ButtonType.OK)
			{
				// Export was canceled
				return;
			}
		}
		
		if (exportTarget.exists())
		{
			String message = "The specified file already exists. Press OK to overwrite this file, or cancel to cancel the export.";
			Optional<ButtonType> result = Dialogues.showConfirmationDialogue(message);
			
			if (result.get() != ButtonType.OK)
			{
				// Export was canceled
				return;
			}
		}
		
		String fileContents = activeFile.getContent();
		try
		{
			FileUtils.write(exportTarget, fileContents);
		}
		catch (Exception exception)
		{
			Dialogues.showAlertDialogue(exception, "Failed to export asm");
		}
	}
	
	@Override
	public void removeASM()
	{
		ASMFile activeFile = getActiveFile();
		if (activeFile == null)
		{
			// XXX: possible feature: select file from a list or dropdown
			String message = "No file is selected! Select the file you wish to remove in the ProjectExplorer, then click remove.";
			Dialogues.showInfoDialogue(message);
			return;
		}
		
		File removalTarget = findDiskObjectForASM(activeFile);
		if (removalTarget == null)
		{
			// XXX: show a confirmation dialogue to confirm removal
			String message = "Unable to locate file on disk. "
					+ "The asm \""
					+ activeFile.getName()
					+ "\" will be removed from the project \""
					+ activeFile.getProject().getName()
					+ "\" but it is suggested that you verify the deletion from disk manually.";
			Dialogues.showInfoDialogue(message);
			Project activeProject = activeFile.getProject();
			activeProject.remove(activeFile);
			return;
		}
		
		if (removalTarget.isDirectory())
		{
			// XXX: show a confirmation dialogue to confirm removal
			String message = "The path specified is a directory, but should be a file. "
					+ "The asm \""
					+ activeFile.getName()
					+ "\" will be removed from the project \""
					+ activeFile.getProject().getName()
					+ "\" but it is suggested that you verify the deletion from disk manually.";
			Exception exception = new IllegalStateException(
					"The path to the specified ASMFile is a directory, but should be a file.");
			Dialogues.showAlertDialogue(exception, message);
			return;
		}
		else
		{
			String message = "The asm \"" + activeFile.getName()
					+ "\" will be removed from the project \""
					+ activeFile.getProject().getName() + "\" and the file at \""
					+ removalTarget.getAbsolutePath() + "\" will be deleted.";
			Optional<ButtonType> result = Dialogues.showConfirmationDialogue(message);
			
			if (result.get() != ButtonType.OK)
			{
				// Removal was canceled
				return;
			}
		}
		
		if (!removalTarget.exists())
		{
			String message = "Unable to locate file on disk. The file will be removed from the project, but it is suggested that you verify the deletion from disk manually.";
			Dialogues.showInfoDialogue(message);
		}
		
		try
		{
			boolean wasRemoved = removalTarget.delete();
			if (!wasRemoved)
				throw new Exception("The file \"" + removalTarget.getAbsolutePath()
						+ "\" was not deleted.");
		}
		catch (Exception exception)
		{
			Dialogues
					.showAlertDialogue(
							exception,
							"Failed to delete asm from disk. It is suggested that you verify the deletion from disk manually.");
		}
	}
	
	@Override
	public void setMainASMFile()
	{
		ASMFile activeFile = getActiveFile();
		if (activeFile == null)
		{
			Dialogues.showActionFailedDialogue("No file is selected!");
			return;
		}
		
		Project activeProject = activeFile.getProject();
		String message = "The file \"" + activeFile.getName()
				+ "\" will be used as the main file for the project \""
				+ activeProject.getName() + "\"";
		Optional<ButtonType> result = Dialogues.showConfirmationDialogue(message);
		
		if (result.get() == ButtonType.OK)
		{
			int index = activeProject.indexOf(activeFile);
			Collections.swap(activeProject, 0, index);
		}
	}
	
	@Override
	public void exit()
	{
		stage.close();
		Platform.exit();
	}
	
	@Override
	public void clearConsole()
	{
		console.clear();
	}
	
	@Override
	public void clearAllBreakpoints()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void showNumberConverter()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void assembleActiveProject()
	{
		Project activeProject = getActiveProject();
		assemble(activeProject);
	}
	
	@Override
	public void simulateActiveProject()
	{
		Project activeProject = getActiveProject();
		String projectType = activeProject.getType();
		Optional<ISAModule> module = ISARegistry.get().lookupByProjectType(projectType);
		
		if (module.isPresent())
		{
			ISAModule isa = module.get();
			activeSimulator = isa.getSimulator();
		}
		else
		{
			String message = "No simulator is available for the project type: ";
			message += projectType;
			Dialogues.showAlertDialogue(new IllegalStateException(message));
		}
		
		// TODO: open associated views? emulation window?
	}
	
	@Override
	public void downloadActiveProjectToBoard()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void stepSimulation()
	{
		performIfActive(activeSimulator::step);
	}
	
	private void performIfActive(Subroutine subroutine)
	{
		try
		{
			subroutine.perform();
		}
		catch (Exception exception)
		{
			throw new IllegalStateException("No simulator is active!", exception);
		}
	}
	
	@Override
	public void triggerSimulationInterrupt()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void resetSimulation()
	{
		performIfActive(activeSimulator::reset);
	}
	
	@Override
	public void runSimulation()
	{
		Project activeProject = getActiveProject();
		
		ProjectAssemblyDetails details = getAssemblyDetailsFor(activeProject);
		if (!details.isDirty())
		{
			activeSimulator.loadProgram(details.getAssembledImage());
			performIfActive(activeSimulator::run);
		}
		else
		{
			// TODO: handle "Project Not Assembled" case
			throw new UnsupportedOperationException("Not yet implemented");
		}
	}
	
	@Override
	public void changeSimulationSpeed(int requestedSpeed)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void stopSimulation()
	{
		performIfActive(activeSimulator::pause);
		performIfActive(activeSimulator::reset);
		activeSimulator = null;
		// TODO: deactivate simulation views (e.g. Emulation Window)
	}
	
	@Override
	public void loadModule()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void clearModuleCache()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void showQuickReference()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void showOnlineManual()
	{
		String webAddress = "https://code.google.com/p/progressive-learning-platform/wiki/UserManual";
		openWebPage(webAddress);
	}
	
	private void openWebPage(String webAddress)
	{
		try
		{
			if (Desktop.isDesktopSupported())
			{
				URI location = new URI(webAddress);
				Desktop.getDesktop().browse(location);
			}
			else
			{
				String cause = "This JVM does not support Desktop. Try updating Java to the latest version.";
				throw new Exception(cause);
			}
		}
		catch (Exception exception)
		{
			String recoveryMessage = "There was a problem opening the following webpage:"
					+ "\n" + webAddress;
			Dialogues.showAlertDialogue(exception, recoveryMessage);
		}
	}
	
	@Override
	public void reportIssue()
	{
		String webAddress = "https://github.com/zcmoore/plpTool-prototype/issues";
		openWebPage(webAddress);
	}
	
	@Override
	public void showIssuesPage()
	{
		String webAddress = "https://github.com/zcmoore/plpTool-prototype/issues/new";
		openWebPage(webAddress);
	}
	
	@Override
	public void showAboutPLPTool()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void showThirdPartyLicenses()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void showOptionsMenu()
	{
		List<Submittable> submittables = new ArrayList<>();
		Map<OptionSection, Pane> optionsMenuModel = createOptionsMenuModel(submittables);
		
		OptionsPane optionsPane = new OptionsPane(optionsMenuModel);
		Scene popupScene = new Scene(optionsPane);
		
		Stage popupWindow = new Stage(StageStyle.DECORATED);
		popupWindow.setTitle("Settings");
		popupWindow.initModality(Modality.WINDOW_MODAL);
		popupWindow.initOwner(stage);
		popupWindow.setScene(popupScene);
		
		popupWindow.setMinWidth(stage.getScene().getWidth() / 2);
		popupWindow.setMinHeight(stage.getScene().getHeight()
				- (stage.getScene().getHeight() / 3));
		
		popupScene.getStylesheets().addAll(stage.getScene().getStylesheets());
		
		optionsPane.setOkAction(() -> {
			if (optionsMenuOkSelected(submittables))
			{
				submittables.forEach(submittable -> submittable.submit());
				popupWindow.close();
			}
		});
		optionsPane.setCancelAction(() -> {
			popupWindow.close();
		});
		
		popupWindow.setOnCloseRequest((windowEvent) -> {
			popupWindow.close();
		});
		popupWindow.show();
	}
	
	@Override
	public void showWatcherWindow()
	{
		Stage stage = new Stage();
		WatcherWindow watcherWindow = new WatcherWindow();
		
		Scene scene = new Scene(watcherWindow, 888, 500);
		stage.setTitle("Watcher Window");
		stage.setScene(scene);
		stage.show();
	}
	
	@Override
	public void showModuleManager()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void saveAll()
	{
		for (Project project : projects)
		{
			tryAndReport(project::save);
		}
	}
}