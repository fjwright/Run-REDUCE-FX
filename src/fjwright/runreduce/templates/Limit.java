package fjwright.runreduce.templates;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Limit extends Template {
    @FXML
    private Label dirLabel;
    @FXML
    private TextField varTextField, limPointTextField, exprnTextField;
    @FXML
    private CheckBox loadSPECFN;

    private int dirInd = 0;
    private final static String[] DIRSTRINGS = new String[]{"\u2000", "+", "-"}; // En Quad = U+2000

    @FXML
    private void dirMouseClicked() {
        dirLabel.setText(DIRSTRINGS[dirInd = (++dirInd) % 3]);
    }

    @Override
    protected String result() throws EmptyFieldException {
        if (loadSPECFN.isSelected()) preamble("load_package specfn;\n");
        final var text = new StringBuilder("limit");
        switch (dirInd) {
            case 0:
                text.append("(");
                break;
            case 1:
                text.append("!+(");
                break;
            case 2:
                text.append("!-(");
        }
        text.append(getTextCheckNonEmpty(exprnTextField))
                .append(", ").append(getTextCheckNonEmpty(varTextField))
                .append(", ").append(getTextCheckNonEmpty(limPointTextField))
                .append(")");
        return text.toString();
    }
}
