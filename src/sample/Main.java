package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class Main extends Application
{
    static ArrayList<String> labels = new ArrayList<>();
    static ArrayList<Memory> memoryArrayList = new ArrayList<>();
    static ArrayList<Instruction> instructionArrayList = new ArrayList<>();
    static int C,Z;

    static HashMap<String, Integer> labelsMap = new HashMap<>();
    static int currentMemAddress = 0, currentInsAddress = 0;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("sample.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);

        Controller controller = loader.getController();
        controller.stage = primaryStage;

        primaryStage.setTitle("Toy");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
