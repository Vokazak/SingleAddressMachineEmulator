package ru.vokazak;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    private static GraphicMemory graphicMemory;

    private static TextArea lexerTextArea;
    private static TextArea parserTextArea;
    private static TextArea machineTextArea;

    public static void main(String[] args) {
        defaultBackground = new Background(new BackgroundFill(Color.rgb(220, 220, 250, 0.7), new CornerRadii(5.0), new Insets(-5.0)));
        errorBackground = new Background(new BackgroundFill(Color.rgb(250, 180, 180, 0.7), new CornerRadii(5.0), new Insets(-5.0)));
        correctBackground = new Background(new BackgroundFill(Color.rgb(180, 250, 180, 0.7), new CornerRadii(5.0), new Insets(-5.0)));
        Application.launch(args);

    }

    @Override
    public void start(Stage stage) {

        StringBuilder startText = new StringBuilder();

        try(FileReader reader = new FileReader(FILE_PATH))
        {
            int c;
            while((c=reader.read()) != -1){
                startText.append((char) c);
            }
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }

        Label enterLabel = new Label("Enter your code here: ");
        Label lexerLabel = new Label("Lexer log:");
        Label parserLabel = new Label("Parser log");
        Label machineLabel = new Label("Machine log:");

        TextArea enterTextArea = new TextArea(startText.toString());
        enterTextArea.setPrefColumnCount(20);
        enterTextArea.setPrefRowCount(32);

        lexerTextArea = new TextArea();
        lexerTextArea.setBackground(defaultBackground);
        lexerTextArea.setPrefColumnCount(15);
        lexerTextArea.setPrefRowCount(32);
        lexerTextArea.setEditable(false);

        parserTextArea = new TextArea();
        parserTextArea.setBackground(defaultBackground);
        parserTextArea.setPrefColumnCount(25);
        parserTextArea.setPrefRowCount(32);
        parserTextArea.setEditable(false);

        machineTextArea = new TextArea();
        machineTextArea.setBackground(defaultBackground);
        machineTextArea.setPrefColumnCount(40);
        machineTextArea.setPrefRowCount(32);
        machineTextArea.setEditable(false);

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

               graphicMemory.resetMemory();
               machineTextArea.setBackground(defaultBackground);
               compile();

            }
        });

        FlowPane logPane = new FlowPane(Orientation.VERTICAL, 30, 10, enterTextArea, lexerTextArea, parserTextArea, machineTextArea);
        graphicMemory = new GraphicMemory();

        AnchorPane.setLeftAnchor(enterLabel, 80.0);
        AnchorPane.setTopAnchor(enterLabel, 10.0);
        AnchorPane.setLeftAnchor(lexerLabel, 370.0);
        AnchorPane.setTopAnchor(lexerLabel, 10.0);
        AnchorPane.setLeftAnchor(parserLabel, 640.0);
        AnchorPane.setTopAnchor(parserLabel, 10.0);
        AnchorPane.setLeftAnchor(machineLabel, 1030.0);
        AnchorPane.setTopAnchor(machineLabel, 10.0);

        AnchorPane.setLeftAnchor(logPane, 10.0);
        AnchorPane.setTopAnchor(logPane, 40.0);

        AnchorPane.setBottomAnchor(compileButton, 100.0);
        AnchorPane.setLeftAnchor(compileButton, 100.0);


        AnchorPane.setBottomAnchor(graphicMemory, 10.0);
        AnchorPane.setLeftAnchor(graphicMemory, 10.0);

        AnchorPane generalPane = new AnchorPane(logPane, compileButton, enterLabel, lexerLabel, parserLabel,  machineLabel, graphicMemory);

        Scene scene = new Scene(generalPane, 1550, 730);

        stage.setScene(scene);
        stage.setTitle("Single address machine");
        stage.show();
    }

    private static void compile() {

        lexerTextArea.clear();
        parserTextArea.clear();
        machineTextArea.clear();
        lexerTextArea.setBackground(defaultBackground);
        parserTextArea.setBackground(defaultBackground);
        machineTextArea.setBackground(defaultBackground);
        Lexer lexer = new Lexer(FILE_PATH);

        if (!lexer.isLexerErrorMessage()) {
            String log = lexer.getLexerLog();
            lexerTextArea.setText(log);
            lexerTextArea.setBackground(correctBackground);

            Parser parser = new Parser(lexer.getLexemeList());

            if (!parser.isParserErrorMessage()) {
                log = parser.getParserLog();
                parserTextArea.setText(log);
                parserTextArea.setBackground(correctBackground);

                SAM machine = new SAM(parser.getCommandList());

                if (!machine.isMachineErrorMessage()) {
                    log = machine.getMachineLog();
                    graphicMemory.setMemory(machine.getDataMemory());
                    machineTextArea.setBackground(correctBackground);
                    machineTextArea.setText(log);
                    showDone();
                } else {
                    machineTextArea.setBackground(errorBackground);
                    machineTextArea.setText("ERROR");
                    showError(machine.getMachineErrorMessage());
                }

            } else {
                parserTextArea.setBackground(errorBackground);
                parserTextArea.setText("ERROR");
                showError(parser.getParserErrorMessage());
            }

        } else {
            lexerTextArea.setBackground(errorBackground);
            lexerTextArea.setText("ERROR");
            showError(lexer.getLexerErrorMessage());
        }
    }

    private static void showError(String content) {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(content);
        alert.showAndWait();
    }

    private static void showDone() {
        alert.setAlertType(Alert.AlertType.INFORMATION);
        alert.setTitle("Done");
        alert.setHeaderText("Program finished successfully");
        alert.showAndWait();
    }

}
