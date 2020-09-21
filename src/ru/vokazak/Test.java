package ru.vokazak;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Test extends Application {

    private static final String FILE_PATH = "Input.txt";

    private static final Alert alert = new Alert(Alert.AlertType.WARNING);

    private static Background defaultBackground;
    private static Background errorBackground;
    private static Background correctBackground;

    private static TextArea machineTextArea;
    private static String log = "";

    public static void main(String[] args) {
        defaultBackground = new Background(new BackgroundFill(Color.rgb(220, 220, 250, 0.7), new CornerRadii(5.0), new Insets(-5.0)));
        errorBackground = new Background(new BackgroundFill(Color.rgb(250, 180, 180, 0.7), new CornerRadii(5.0), new Insets(-5.0)));
        correctBackground = new Background(new BackgroundFill(Color.rgb(180, 250, 180, 0.7), new CornerRadii(5.0), new Insets(-5.0)));
        Application.launch(args);

    }

    @Override
    public void start(Stage stage) {

        String startText = "";

        try(FileReader reader = new FileReader(FILE_PATH))
        {
            int c;
            while((c=reader.read()) != -1){
                startText += (char) c;
            }
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }

        Label enterLabel = new Label("Enter your code here: ");
        Label logLabel = new Label("Machine log:");

        TextArea enterTextArea = new TextArea(startText);
        enterTextArea.setPrefColumnCount(20);
        enterTextArea.setPrefRowCount(32);

        machineTextArea = new TextArea();
        machineTextArea.setBackground(defaultBackground);
        machineTextArea.setPrefColumnCount(40);
        machineTextArea.setPrefRowCount(32);
        machineTextArea.cancelEdit();

        Button compileButton = new Button("Compile");
        compileButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                try(FileWriter writer = new FileWriter(FILE_PATH, false)) {
                    writer.write(enterTextArea.getText());
                    writer.append('\n');
                    writer.flush();
                }

                catch(IOException ex){

                    System.out.println(ex.getMessage());
                }

               log = "";
               machineTextArea.setBackground(defaultBackground);
               compile();

            }
        });

        FlowPane logPane = new FlowPane(Orientation.VERTICAL, 30, 10, enterTextArea, machineTextArea);

        AnchorPane.setLeftAnchor(enterLabel, 80.0);
        AnchorPane.setTopAnchor(enterLabel, 10.0);

        AnchorPane.setLeftAnchor(logPane, 10.0);
        AnchorPane.setTopAnchor(logPane, 40.0);

        AnchorPane.setBottomAnchor(compileButton, 30.0);
        AnchorPane.setLeftAnchor(compileButton, 100.0);

        AnchorPane.setTopAnchor(logLabel, 10.0);
        AnchorPane.setLeftAnchor(logLabel, 380.0);

        AnchorPane generalPane = new AnchorPane(logPane, compileButton, enterLabel, logLabel);

        Scene scene = new Scene(generalPane, 800, 660);

        stage.setScene(scene);
        stage.setTitle("Single address machine");
        stage.show();
    }

    private static void compile() {

        Lexer lexer = new Lexer(FILE_PATH);

        if (!lexer.isLexerErrorMessage()) {
            //lexer.printLexemeList();
            log = log.concat(lexer.getLexerLog());

            Parser parser = new Parser(lexer.getLexemeList());

            if (!parser.isParserErrorMessage()) {
                //parser.printCommandList();
                log = log.concat(parser.getParserLog());

                SingleAddressMachine machine = new SingleAddressMachine(parser.getCommandList());

                if (!machine.isMachineErrorMessage()) {
                    log = log.concat(machine.getMachineLog());
                    showDone();
                } else showError(machine.getMachineErrorMessage());

            } else showError(parser.getParserErrorMessage());

        } else showError(lexer.getLexerErrorMessage());
    }

    private static void showError(String content) {
        machineTextArea.setBackground(errorBackground);
        machineTextArea.setText("ERROR");
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(content);
        alert.showAndWait();
    }

    private static void showDone() {
        machineTextArea.setBackground(correctBackground);
        machineTextArea.setText(log);
        alert.setAlertType(Alert.AlertType.INFORMATION);
        alert.setTitle("Done");
        alert.setHeaderText("Program finished successfully");
        alert.showAndWait();
    }

}
