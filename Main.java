package compSciClub;

import compSciClub.GameWorld;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

public class Main extends Application {
    private static AnimationTimer timer;
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(createContent(primaryStage));
		primaryStage.setTitle("Infinite Runner");
		primaryStage.show();
	}
	/*creates our scene, sets the world bounds and 
	adds our player to the scene*/
	public static Scene createContent(Stage st)
	{
		GameWorld gw = new GameWorld();
		
		Scene sc = new Scene(gw);
		sc.setOnKeyPressed(event -> gw.getKeys().put(event.getCode(), true));
		sc.setOnKeyReleased(event -> gw.getKeys().put(event.getCode(), false));
		timer = new AnimationTimer() {

			@Override
			public void handle(long now) {
			gw.update(st, sc);

			}

		};
		timer.start();
		return sc;
	}
	public static AnimationTimer getTimer()
	{
		return timer;
	}
}