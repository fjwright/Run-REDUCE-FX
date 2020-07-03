package fjwright.runreduce.templates;

import fjwright.runreduce.RunREDUCE;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.regex.Pattern;

/**
 * This is the base class for the specific template classes.
 */
public abstract class Template {

// Set up the pop-up keyboard on the middle mouse button for all template dialogues ===============

    @FXML
    private VBox templateRoot;

    @FXML
    protected void initialize() {
        // Add the button bar to the bottom of the template:
        var hBox = new HBox();
        templateRoot.getChildren().add(hBox);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setSpacing(20);
        hBox.setPadding(new Insets(10));
        var button = new Button("Edit");
        hBox.getChildren().add(button);
        button.setOnAction(this::editButtonAction);
        button.setTooltip(new Tooltip("Insert into input editor"));
        button = new Button("Evaluate");
        hBox.getChildren().add(button);
        button.setOnAction(this::evaluateButtonAction);
        button.setTooltip(new Tooltip("Send to REDUCE"));
        button = new Button("Close");
        hBox.getChildren().add(button);
        button.setOnAction(this::closeButtonAction);

        // Register the pop-up handler on the template:
        templateRoot.addEventFilter(MouseEvent.MOUSE_CLICKED, PopupKeyboard::showPopupKeyboard);
    }

// Check field entries dynamically ================================================================

    protected static final Pattern VAR_PATTERN =
            Pattern.compile("(?:!|\\p{Alpha}).*");  // non-capturing group

    @FXML
    private void varCheckKeyTyped(KeyEvent keyEvent) {
        final TextField textField = (TextField) keyEvent.getTarget();
        final String text = textField.getText();
        if (!(text.isEmpty() || VAR_PATTERN.matcher(text).matches())) {
            RunREDUCE.errorMessageDialog("Template Error",
                    "This field must be an identifier.");
            textField.setText("");
        }
    }

    private static final Pattern INT_OR_VAR_PATTERN =
            Pattern.compile("(?:[+-]?\\d*)|(?:!|\\p{Alpha}).*");  // non-capturing groups

    @FXML
    private void intOrVarCheckKeyTyped(KeyEvent keyEvent) {
        final TextField textField = (TextField) keyEvent.getTarget();
        final String text = textField.getText();
        if (!(text.isEmpty() || INT_OR_VAR_PATTERN.matcher(text).matches())) {
            RunREDUCE.errorMessageDialog("Template Error",
                    "This field must be an integer or an identifier.");
            textField.setText("");
        }
    }

// Process the result =============================================================================

    protected static class EmptyFieldException extends Exception {
    }

    protected static String getTextCheckNonEmpty(final TextField textField) throws EmptyFieldException {
        final String text = textField.getText().trim();
        if (text.isEmpty()) {
            RunREDUCE.errorMessageDialog("Template Error",
                    "A required field is empty.");
            throw new EmptyFieldException();
        } else
            return text;
    }

    abstract String result() throws EmptyFieldException;

    // Could apply PopupKeyboard.decode() below more efficiently only to appropriate user input.
    // But it seems fast enough as it is.

    @FXML
    private void editButtonAction(ActionEvent actionEvent) {
        // Insert in input editor if valid:
        try {
            final TextArea textArea = RunREDUCE.reducePanel.inputTextArea;
            textArea.insertText(textArea.getCaretPosition(), PopupKeyboard.decode(result()));
            // Close dialogue:
//            cancelButtonAction(actionEvent);
        } catch (EmptyFieldException ignored) {
        }
    }

    @FXML
    private void evaluateButtonAction(ActionEvent actionEvent) {
        // Send to REDUCE if valid:
        try {
            RunREDUCE.reducePanel.sendStringToREDUCEAndEcho(PopupKeyboard.decode(result()) + ";\n");
            // Close dialogue:
//            cancelButtonAction(actionEvent);
        } catch (EmptyFieldException ignored) {
        }
    }

    @FXML
    private void closeButtonAction(ActionEvent actionEvent) {
        // Close dialogue:
        final Node source = (Node) actionEvent.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
