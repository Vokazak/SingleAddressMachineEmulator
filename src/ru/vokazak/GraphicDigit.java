package ru.vokazak;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class GraphicDigit extends StackPane {

    private static final double WIDTH = 70;
    private static final double HEIGHT = 50;
    private static final Color emptyColor = Color.LIGHTGRAY;
    private static final Color okColor = Color.GREEN;
    private static final Color defaultColor = Color.BLACK;

    private Text decimalData;
    private Text binaryData;
    private Rectangle form;


    GraphicDigit(int i) {
        decimalData = new Text();
        decimalData.setFill(Color.BLACK);
        decimalData.setFont(Font.font(14));
        setMargin(decimalData, new Insets(1, 5, 20, 5));

        binaryData = new Text();
        binaryData.setFill(Color.BLACK);
        binaryData.setFont(Font.font(14));
        setMargin(binaryData, new Insets(20, 5, 1, 5));

        Text index = new Text(String.valueOf(i));

        Text decimalInfo = new Text("10");
        decimalInfo.setFont(Font.font(10));
        setMargin(decimalInfo, new Insets(5, 0, 10, 25));

        Text binaryInfo = new Text("2");
        binaryInfo.setFont(Font.font(10));
        setMargin(binaryInfo, new Insets(30, 0, 0, 55));

        form = new Rectangle();
        form.setWidth(WIDTH);
        form.setHeight(HEIGHT);
        form.setStroke(defaultColor);

        resetDigit();
        getChildren().addAll(form, decimalData, binaryData, index, decimalInfo, binaryInfo);

        setAlignment(index, Pos.TOP_LEFT);
    }

    public void setDigit(int value) {
        decimalData.setText(String.valueOf(value));
        decimalData.setFill(okColor);

        String binaryString = Integer.toBinaryString(value);
        while (binaryString.length() < SAM.FIELD_LENGTH)
            binaryString = "0".concat(binaryString);
        binaryData.setText(binaryString);
        binaryData.setFill(okColor);
    }

    public void resetDigit() {
        decimalData.setText("0");
        decimalData.setFill(defaultColor);
        binaryData.setText("0");
        binaryData.setFill(defaultColor);
        form.setFill(emptyColor);
    }
}
