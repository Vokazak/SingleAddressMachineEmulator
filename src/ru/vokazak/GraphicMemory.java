package ru.vokazak;

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


import java.util.ArrayList;

public class GraphicMemory extends HBox {

    private ArrayList<GraphicDigit> graphicDigits;
    private Text memoryText;

    GraphicMemory() {
        graphicDigits = new ArrayList<>();
        memoryText = new Text("Data\nmemory:  ");

        setSpacing(2.0);

        for (int i = 0; i < SAM.MEMORY_SIZE; i++) {
            graphicDigits.add(new GraphicDigit(i));
            refresh();
        }
    }

    public void setMemory(ArrayList<Integer> memoryData) {
        for (int i = 0; i < SAM.MEMORY_SIZE; i++) {
            graphicDigits.get(i).setDigit(memoryData.get(i));
        }

        refresh();
    }

    public void resetMemory() {
        for (int i = 0; i < SAM.MEMORY_SIZE; i++) {
            graphicDigits.get(i).resetDigit();
        }

        refresh();
    }

    private void refresh() {
        getChildren().clear();
        getChildren().add(memoryText);
        for (GraphicDigit digit: graphicDigits) {
            getChildren().add(digit);
        }
    }
}
