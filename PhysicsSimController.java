import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import javafx.scene.canvas.*; // Needed for GraphicsContext object
import javafx.scene.paint.Color; // For changing color: "Color.Black"
import javafx.animation.*; // AnimationTimer
import javafx.stage.FileChooser; // Used for File Chooser Dialog box
import javafx.stage.Stage;
import javafx.scene.shape.*;
import javafx.scene.control.Alert;	// used for alerts
import javafx.scene.control.Alert.AlertType;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

import physics.*;

public class PhysicsSimController {
	
	// root node
	
    @FXML
    private BorderPane root;
	
	// Menu toolbar menu items
	
    @FXML
    private CheckMenuItem runMenuItem;

    @FXML
    private CheckMenuItem pauseMenuItem;
	
	// Tab Controls
	
    @FXML
    private TabPane tabPane;

    @FXML
    private Tab sceneTab;
	
	// particle tab controllers

    @FXML
    private Tab particleTab;
	
    @FXML
    private ComboBox<String> particleSelection1;

    @FXML
    private TextField positionX;

    @FXML
    private TextField velocityX;

    @FXML
    private TextField accelerationX;

    @FXML
    private TextField positionY;

    @FXML
    private TextField velocityY;

    @FXML
    private TextField accelerationY;

    @FXML
    private Button updateButton1;

    @FXML
    private Button deleteButton1;
	
	// circular tab controllers
	
    @FXML
    private Tab circularTab;

    @FXML
    private ComboBox<String> particleSelection2;

    @FXML
    private TextField centerX;
	
    @FXML
    private TextField centerY;

    @FXML
    private TextField radius;

    @FXML
    private TextField velocityC;

    @FXML
    private TextField theta;

    @FXML
    private Button updateButton2;

    @FXML
    private Button deleteButton2;
	
	// canvas
	
    @FXML
    private Canvas canvas;
	
	private GraphicsContext gc;// GraphicsContext allows me to draw to the canvas
	private long lastNanoTime; // allows to start the timer
	private AnimationTimer animation; // animation timeline
	private File saveFile; // File that the program should save state to (uploaded from file)
	String selected; // the currently selected particle
	int currentID; // the current ID that should be created
	Map<String,Motion> system; // holds all the particles and their motions
	File lastSession; // stores the filename for the last session
	
	double width;
	double height;
	Vector origin;
	
	
	public void initialize()
	{
		// initializing the canvas and some variables
		system = new HashMap<String,Motion>();
		gc =  canvas.getGraphicsContext2D(); 
		setOrigin();
		paintCanvas();
		currentID=0;
		selected = "N/A";
		
		// tying particleselection1 and particle selection2 together, so what happens to one will happen to the other.
		particleSelection2.setSelectionModel(particleSelection1.getSelectionModel());
		particleSelection2.setItems(particleSelection1.getItems());
		
		// creating the animation loop
		animation = new AnimationTimer()
		{
			public void handle(long currentNanoTime){
				if (system.isEmpty()) 
					return;
				
				double t = (currentNanoTime - lastNanoTime) / 1000000000.0;
				for ( Motion m : system.values() ) {
					m.move(t);
				}
				updateParticleTab();
				paintCanvas();
				lastNanoTime = currentNanoTime;
				
			}
		};
		
		lastSession = new File("lastsession");
		if (lastSession.exists())
			readFromFile(lastSession);
		
	}
	
	/*
		DRAW + UI FUNCTIONS
	*/
	
	private void setOrigin() {
		width = canvas.getWidth();
		height = canvas.getHeight();
		origin = new Vector (width/2,height/2);
	}
	
	private void paintCanvas() {
		
		// draw the axes
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, width, height);// Clears canvas
		gc.setStroke(Color.GREEN);
		gc.setLineWidth(1);
		gc.strokeLine(0,origin.getY(),width,origin.getY()); // draw x axis (along y = 0)
		gc.strokeLine(origin.getX(),0,origin.getX(),height); // draw y axis (along x = 0)
		
		
		// draw all the particles
		if (!system.isEmpty()) {
			for ( Map.Entry<String, Motion> e : system.entrySet() ) {
				drawParticle(e.getValue(), e.getKey());
			}
		}
		
	}
	
	private void drawParticle(Motion m, String id){
		final double LINEWIDTH = 2; // the width of the vectors
		final double PARTICLERADIUS = 10; // the radius of the particles
		
		// set the velocity and acceleration relative to the center
		Vector center = m.getParticle().getPosition();
		Vector velocity = center.add(m.getParticle().getVelocity()); 
		Vector acceleration = center.add(m.getParticle().getAcceleration());
		
		gc.setLineWidth(LINEWIDTH);
		
		// Draw the Velocity Vector 
		// start from the and x and y axes
		// the y axis is inverted so it needs to be subtracted rather than added.
		gc.setStroke(Color.RED);
		gc.strokeLine(origin.getX()+center.getX(), origin.getY()-center.getY(), origin.getX()+velocity.getX(), origin.getY()-velocity.getY());
		
		// Draw the Acceleration Vector
		gc.setStroke(Color.BLUE);
		gc.strokeLine(origin.getX()+center.getX(), origin.getY()-center.getY(), origin.getX()+acceleration.getX(), origin.getY()-acceleration.getY());
		
		// Draw the Particle
		gc.setFill(Color.GREEN);
		gc.fillOval(origin.getX() + center.getX() - PARTICLERADIUS/2, 
			origin.getY() - center.getY() - PARTICLERADIUS/2, // y-axis is inverted
			PARTICLERADIUS,
			PARTICLERADIUS);
		
		// Draw a blue ring around the circle if it is selected
		if (id == selected) {
			gc.setLineWidth(1);
			gc.setStroke(Color.BLUE);
			gc.strokeOval(origin.getX() + center.getX() - PARTICLERADIUS/2, 
				origin.getY() - center.getY() - PARTICLERADIUS/2, // y-axis is inverted
				PARTICLERADIUS,
				PARTICLERADIUS);
		}
	}
	
	private void updateParticleTab() {
		Particle p = system.get(selected).getParticle();
		
		// Update all of fields in particle tab to reflect the currently selected particle
		positionX.setText(Double.toString(p.getPosition().getX()));
		positionY.setText(Double.toString(p.getPosition().getY()));
		velocityX.setText(Double.toString(p.getVelocity().getX()));
		velocityY.setText(Double.toString(p.getVelocity().getY()));
		accelerationX.setText(Double.toString(p.getAcceleration().getX()));
		accelerationY.setText(Double.toString(p.getAcceleration().getY()));
	}
	
	private void updateCircularTab() {
		
		// again, in the future, this could be avoided by designing my classes so that this doesn't happen.
		CircularMotion c = (CircularMotion)system.get(selected);
		centerX.setText(Double.toString(c.getCenter().getX()));
		centerY.setText(Double.toString(c.getCenter().getY()));
		radius.setText(Double.toString(c.getRadius()));
		velocityC.setText(Double.toString(c.getVelocity()));
		theta.setText(Double.toString(c.getPosition()));
	}
	
	private void setParticleTabActive(boolean b) {
		positionX.setDisable(!b);
		positionY.setDisable(!b);
		velocityX.setDisable(!b);
		velocityY.setDisable(!b);
		accelerationX.setDisable(!b);
		accelerationY.setDisable(!b);
	}
	
	/*
		EVENT HANDLERS + LISTENERS
	*/
	
	public void updateSelection() {
		// if there is nothing in the combobox, set selected to N/A
		if (particleSelection1.getItems().isEmpty())
		{
			selected = "N/A";
			return;
		}
		
		// in case the selection is null (in the case of removing the first particle), move the selection to the next available
		if (particleSelection1.getSelectionModel().getSelectedItem() == null)
			particleSelection1.getSelectionModel().selectNext();
		
		// update the selected particle to what ever is selected in the combo box
		selected = particleSelection1.getSelectionModel().getSelectedItem();
		
		// if the particle is an instance of Circular motion, make particle tab not editable, enable the circular tab, and update the circular tab
		// otherwise, allow edits on the particle tab and disable the circularTab, and switch to particle if the circulartab is currently active
		if (system.get(selected) instanceof CircularMotion ) { // use of instance of may call for a redesign of the inheritance structure
			setParticleTabActive(false);
			circularTab.setDisable(false);
			updateCircularTab();
		}
		else
		{
			setParticleTabActive(true);
			circularTab.setDisable(true);
			if (circularTab.isSelected())
				tabPane.getSelectionModel().select(particleTab);
		}
		
		// in any case, update the particle tab and canvas
		particleTab.setDisable(false);
		updateParticleTab();
		paintCanvas();
	}
	
	public void insert2DMotion() {
		// change the selected particle to a new id, add a new particle to the system with the new id, and update the canvas
		selected = Integer.toString(currentID);
		currentID++;
		system.put(selected,new TwoDMotion());
		paintCanvas();
		
		// activate, update, and set the current tab to the particle tab
		// update the combobox in the particle tab to select the new particle.
		particleTab.setDisable(false);
		particleSelection1.getItems().addAll(selected);
		particleSelection1.getSelectionModel().selectLast();
	}
	
	public void insertCircularMotion() {
		// change the selected particle to a new id, add a new particle to the system with the new id, and update the canvas
		selected = Integer.toString(currentID);
		currentID++;
		system.put(selected,new CircularMotion());
		
		// activate and update the particle tab 
		// update the combobox in the particle tab to select the new particle.
		particleTab.setDisable(false);
		particleSelection2.getItems().addAll(selected);
		particleSelection2.getSelectionModel().selectLast();
		
		// activate, update, and set the current tab to the circulartab
		// update the combobox in the particle tab to select the new particle.
		
		paintCanvas();
		
	}
	
	public void updateMotion() {
		
		if (!circularTab.isDisable()){
			try {
				Vector C = new Vector(Double.parseDouble(centerX.getText()),Double.parseDouble(centerY.getText()));
				double R = Double.parseDouble(radius.getText());
				double v = Double.parseDouble(velocityC.getText());
				double position = Double.parseDouble(theta.getText());
				system.put(selected,new CircularMotion(C, R, v, position));
				paintCanvas();
			}
			catch (NumberFormatException | ZeroRadiusException e) {
				updateCircularTab();
			}

		}
		else {
			try {
				Vector r = new Vector(Double.parseDouble(positionX.getText()),Double.parseDouble(positionY.getText()));
				Vector v = new Vector(Double.parseDouble(velocityX.getText()),Double.parseDouble(velocityY.getText()));
				Vector a = new Vector(Double.parseDouble(accelerationX.getText()),Double.parseDouble(accelerationY.getText()));
				system.put(selected,new TwoDMotion(new Particle(r,v,a)));
				paintCanvas();
			}
			catch (NumberFormatException e) {
				updateParticleTab();
			}
		}
	}
	
	public void removeParticle(){
		// remove particle from system and combobox
		
		// Notes:
		// particleSelection1 automatically calls updateSelection(), as it changes the selected particle automatically.
		// for future reference, there are two types of remove for combobox.getItems(), which returns an observable list, which further inherits list: remove(int) and remove(obj). 
		// If you use a combobox of type <int> this function gets confusing
		system.remove(selected);
		particleSelection1.getItems().remove(selected); 
		
		// if there are no more particles, switch tab to scene and disable the particle tab and circular motion tabs, otherwise update the selected particle to the 
		// next available, and update the selected particle.
		if (system.isEmpty()) {
			tabPane.getSelectionModel().select(sceneTab);
			particleTab.setDisable(true);
			circularTab.setDisable(true);
			currentID = 0;
		}
		paintCanvas();
	}

	public void clearSystem() {
		if (system.isEmpty())
			return;
		
		// reset the current id, clear the hashmap and combobox list, switch to the scenetab and disable the particle and rotation panes, pause the animation, and repaint the canvas
		currentID = 0;
		system.clear();
		particleSelection1.getItems().clear();
		tabPane.getSelectionModel().select(sceneTab);
		particleTab.setDisable(true);
		circularTab.setDisable(true);
		stopAnimation();
		paintCanvas();
	}
	
	
	public void runAnimation() // Event Handler that allows animation to run.
	{
		// reset the timer and start the animation
		lastNanoTime = System.nanoTime();
		animation.start();
		
		// check the appropriate menu button
		runMenuItem.setSelected(true);
		pauseMenuItem.setSelected(false);
		
		// set the menu buttons on the particle and rotation tabs to be clickable
		updateButton1.setDisable(true);
		updateButton2.setDisable(true);
		deleteButton1.setDisable(true);
		deleteButton2.setDisable(true);
		
		centerX.setDisable(true);
		centerY.setDisable(true);
		radius.setDisable(true);
		velocityC.setDisable(true);
		theta.setDisable(true);
	}
	
	public void stopAnimation() // Event Handler that allows animation to pause.
	{
		// stop the menu button 
		animation.stop();
		
		// check the appropriate menu button
		pauseMenuItem.setSelected(true);
		runMenuItem.setSelected(false);
		
		// set the menu buttons on the particle and rotation tabs to not be clickable
		updateButton1.setDisable(false);
		updateButton2.setDisable(false);
		deleteButton1.setDisable(false);
		deleteButton2.setDisable(false);
		
		centerX.setDisable(false);
		centerY.setDisable(false);
		radius.setDisable(false);
		velocityC.setDisable(false);
		theta.setDisable(false);
	}
	
	public void save()
	{
		if (saveFile == null || !saveFile.exists()) {
			saveAs();
			return;
		}
		else
			writeToFile(saveFile);
		
	}
	
	public void saveAs()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save");
		File newFile = fileChooser.showSaveDialog(new Stage());
		if (newFile == null)
			return;
		writeToFile(newFile);
	}
	
	public void loadFile() throws Exception
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load");
		File loadedFile = fileChooser.showOpenDialog(new Stage());
		
		// In the future, confirm that the user wants to load a new project
		// in the future import files
		
		clearSystem();
		readFromFile(loadedFile);
	}
	
	
	public void exit() {
		 Stage stage = (Stage) root.getScene().getWindow();
		writeToFile(lastSession);
		stage.close();
	}
	
	// HELPER FUNCTIONS
	void writeToFile(File f) {
		ObjectOutputStream ostream;
		try {
			ostream = new ObjectOutputStream (new FileOutputStream(f));
			if (!system.isEmpty()) {
				for ( Motion m : system.values() ) {
					if (m instanceof TwoDMotion)
						ostream.writeInt(0);
					else
						ostream.writeInt(1);
					ostream.writeObject(m);
				}
			}
			ostream.close();
			saveFile = f;
		}
		catch (IOException e)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Write Error");
			alert.setHeaderText("Could not write to file.");
			alert.setContentText("Verify that the file is not corrupt.");
			alert.showAndWait();
		}
	}
	
	void readFromFile(File f) {
		ObjectInputStream istream;
		try {
			istream = new ObjectInputStream (new FileInputStream(f));
			while (istream.available() > 0)
			{
				if (istream.readInt() == 0)
					system.put(Integer.toString(currentID),(TwoDMotion)istream.readObject());
				else
					system.put(Integer.toString(currentID),(CircularMotion)istream.readObject());
				particleSelection1.getItems().addAll(Integer.toString(currentID));
				tabPane.getSelectionModel().select(sceneTab);
				currentID++;
			}
			particleSelection1.getSelectionModel().selectLast();
			paintCanvas();
			istream.close();
			saveFile = f;
		}
		catch (InvalidClassException | ClassNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Read Error");
			alert.setHeaderText("Could not read to file.");
			alert.setContentText("File is not encoded in the correct format. Verify that the file is not corrupt.");
			alert.showAndWait();
		}
		catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Read Error");
			alert.setHeaderText("Could not read to file.");
			alert.setContentText("Verify that the file is not corrupt.");
			alert.showAndWait();
		}
	}
}
