import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PhysicsSim extends Application
{
	public void start(Stage stage) throws Exception // default function for the Application class that must be overriden
	{
		// Load the FXML file.
		Parent parent = FXMLLoader.load(
		getClass().getResource("PhysicsSim.fxml"));

		// Build the scene graph.
		Scene scene = new Scene(parent);

		// Display our window, using the scene graph.
		stage.setTitle("Physics Simulations");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

	public static void main(String[] args)
	{
		// Launch the application.
		launch(args);
	}
}