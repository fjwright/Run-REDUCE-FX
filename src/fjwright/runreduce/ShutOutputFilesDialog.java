package fjwright.runreduce;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class ShutOutputFilesDialog {
    @FXML
    private ListView<File> listView;

    @FXML
    private void initialize() {
        ObservableList<File> files = FXCollections.observableArrayList(
                RunREDUCE.reducePanel.outputFileList);
        listView.setItems(files);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String fileName = ((Text) e.getTarget()).getText();
                RunREDUCE.reducePanel.sendStringToREDUCEAndEcho("shut \"" + fileName + "\";\n");
                RunREDUCE.reducePanel.outputFileList.remove(new File(fileName));
                ((Stage) ((Node) e.getSource()).getScene().getWindow()).close();
            }
        });
    }

    @FXML
    private void shutButtonAction(ActionEvent actionEvent) {
        List<Integer> fileIndices = listView.getSelectionModel().getSelectedIndices();
        int i = fileIndices.size();
        if (i > 0) {
            // Process backwards to avoid remove() changing subsequent indices:
            StringBuilder text = new StringBuilder(
                    RunREDUCE.reducePanel.outputFileList.remove((int) fileIndices.get(--i)).toString());
            text.append("\";\n");
            while (i > 0) {
                text.insert(0, "\", \"");
                text.insert(0,
                        RunREDUCE.reducePanel.outputFileList.remove((int) fileIndices.get(--i)).toString());
            }
            text.insert(0, "shut \"");
            RunREDUCE.reducePanel.sendStringToREDUCEAndEcho(text.toString());
        }
        // Close dialogue:
        cancelButtonAction(actionEvent);
    }

    @FXML
    private void cancelButtonAction(ActionEvent actionEvent) {
        // Close dialogue:
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
