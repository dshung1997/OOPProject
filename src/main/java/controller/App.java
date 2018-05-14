package controller;

import com.jfoenix.controls.JFXButton;
import controller.login.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.employee.Employee;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class App extends Application {

    public static SceneManager sceneManager;
    public static DataManager dataManager;

    public static String defaultPath;

    private static Stage primaryStage;

    private static Employee user;

    public static Employee getUser() {
        return user;
    }

    public static void setUser(Employee user) {
        App.user = user;
    }

    @Override
    public void start(Stage _primaryStage) throws Exception{


        primaryStage = _primaryStage;
        //primaryStage.initStyle(StageStyle.UNDECORATED);

        dataManager = new DataManager();
        dataManager.readData();

        sceneManager = new SceneManager();

        setDefaultPath();

        loadLoginWindow();

//              File f = new File(getClass().getResource("/Data/products.txt").toURI());

//        FileReader fr = new FileReader(f);
//        char [] a = new char[50];
//        fr.read(a); // doc noi dung toi mang
//        for(char c : a)
//            System.out.print(c); //in tung ky tu mot
//        fr.close();




    }

    private void setDefaultPath()
    {
//        File f = new File(String.valueOf(getClass().getResourceAsStream("/image/default.png")));

//        File f = new File(String.valueOf((getClass().getResource("/image/default.png"))));
//        defaultPath = f.getAbsolutePath();

        //System.out.println("default path:" + defaultPath);

        File f1 = new File(FinalPaths.IMAGE+"default.png");
        defaultPath = f1.getAbsolutePath();

//        System.out.println("default path:" + f1.getAbsolutePath());
    }

    public static String[] getEnumConstants(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }


    public static int displayConfirmBox(String _text)
    {
        AtomicInteger selection = new AtomicInteger(0);

        JFXButton yesButton;
        JFXButton noButton;
        Label textLabel;

        Stage window = new Stage(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Confirm");
//        window.setMinWidth(350);

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FinalPaths.CONFIRM_BOX));
        AnchorPane confirmBoxLayout = null;
        try {
            confirmBoxLayout = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(confirmBoxLayout);
        yesButton = (JFXButton) scene.lookup("#yesButton");
        noButton = (JFXButton) scene.lookup("#noButton");
        textLabel = (Label) scene.lookup("#textLabel");

        yesButton.setOnAction(e -> {
            selection.set(1);
            window.close();
        });

        noButton.setOnAction(e -> {
            selection.set(2);
            window.close();
        });

        textLabel.setText(_text);

        window.setScene(scene);

        window.showAndWait();
        return selection.get();
    }

    public static void loadMainWindow()
    {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FinalPaths.MAIN_STAGE));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setTitle("MediaOne");

        MainStageController mainStageController = fxmlLoader.getController();
        sceneManager.setMainStageController(mainStageController);

        Scene mainScene = new Scene(root);
        sceneManager.setPaneContent("Dashboard");


        primaryStage.setOnCloseRequest(e -> {
            e.consume();

            int selectionQuit = displayConfirmBox("Are you sure to quit ?");
            if(selectionQuit == 1)
            {
                dataManager.writeData();
                primaryStage.close();
            }
        });
        primaryStage.getIcons().add(new Image(App.class.getResourceAsStream(FinalPaths.ICON)));

        primaryStage.setScene(mainScene);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void loadLoginWindow()
    {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FinalPaths.LOGIN));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setTitle("MediaOne");

        Scene loginScene = new Scene(root, 500, 300);

        LoginController loginController = fxmlLoader.getController();
        loginController.setStage(primaryStage);

        primaryStage.setOnCloseRequest(e -> {
            e.consume();

            int selectionQuit = displayConfirmBox("Are you sure to quit ?");
            if(selectionQuit == 1)
                primaryStage.close();

        });
        primaryStage.getIcons().add(new Image(App.class.getResourceAsStream(FinalPaths.ICON)));
        primaryStage.setScene(loginScene);
        primaryStage.setMaximized(false);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private static String getResourcePath() {
        try {
            URI resourcePathFile = System.class.getResource("").toURI();
            String resourcePath = Files.readAllLines(Paths.get(resourcePathFile)).get(0);
            URI rootURI = new File("").toURI();
            URI resourceURI = new File(resourcePath).toURI();
            URI relativeResourceURI = rootURI.relativize(resourceURI);
            return relativeResourceURI.getPath();
        } catch (Exception e) {
            return null;
        }
    }

    public static void displayAlertingBox(String text)
    {
        Stage window = new Stage(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Alert");
        window.setMinWidth(300);

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(FinalPaths.ALERT_BOX));
        AnchorPane alertBoxLayout = null;
        try {
            alertBoxLayout = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(alertBoxLayout);
        scene.setFill(Color.TRANSPARENT);

        Label alertText = (Label) scene.lookup("#alertText");
        JFXButton okButton =(JFXButton) scene.lookup("#okButton");

        alertText.setText(text);

        okButton.setOnAction(e -> {
            window.close();
        });

        window.setScene(scene);
        window.showAndWait();
    }

    public static String stringToAscii(String text)
    {
        String returnString ="";

        int length = text.length();
        for(int i = 0; i < length; i++)
        {
            char c = text.charAt(i);
            int j = (int) c;
            returnString = returnString.concat(String.valueOf(j)).concat(" ");
        }

        return returnString;
    }

    public static String asciiToString(String text)
    {
        String returnString = "";
        if(text == null|| text.equals("")) return returnString;
        String[] parts = text.split(" ");
        int length = parts.length;
        for(int i = 0; i < length; i++)
        {
            int so = Integer.valueOf(parts[i]);
            String c = Character.toString((char) so);
            returnString = returnString.concat(c);
        }

        return returnString;
    }

    public static void logout()
    {
        int selectionQuit = displayConfirmBox("Are you sure to log out ?");
        if(selectionQuit == 1)
        {

            dataManager.writeData();

            primaryStage.close();
            loadLoginWindow();
            user = null;
        }
    }
}
