package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        GridLayout gridLayout = findViewById(R.id.gridLayout);

        String[] buttonLabels = getResources().getStringArray(R.array.calculator_buttons);

        for (String label : buttonLabels) {
            Button button = new Button(this);
            button.setText(label);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleButtonClick(label);
                }
            });
            gridLayout.addView(button);
        }
    }

    private void handleButtonClick(String label) {
        String currentText = editText.getText().toString();

        switch (label) {
            case "=":
                try {
                    double result = evaluateExpression(currentText);
                    editText.setText(formatResult(result));
                } catch (Exception e) {
                    editText.setText(getString(R.string.error_message));
                }
                break;
            case getString(R.string.clear):
                editText.setText("");
                break;
            case getString(R.string.backspace):
                if (!currentText.isEmpty()) {
                    editText.setText(currentText.substring(0, currentText.length() - 1));
                }
                break;
            case getString(R.string.add):
            case getString(R.string.subtract):
            case getString(R.string.multiply):
            case getString(R.string.divide):
                editText.append(" " + label + " ");
                break;
            case getString(R.string.sqrt):
                try {
                    double number = Double.parseDouble(currentText);
                    double sqrtResult = Math.sqrt(number);
                    editText.setText(formatResult(sqrtResult));
                } catch (NumberFormatException e) {
                    editText.setText(getString(R.string.error_message));
                }
                break;
            case getString(R.string.change_sign):
                try {
                    double value = Double.parseDouble(currentText);
                    editText.setText(formatResult(-value));
                } catch (NumberFormatException e) {
                    editText.setText(getString(R.string.error_message));
                }
                break;
            default:
                editText.append(label);
                break;
        }
    }

    private double evaluateExpression(String expression) {

        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("js");
            Object result = engine.eval(expression);
            return Double.parseDouble(result.toString());
        } catch (ScriptException e) {
            throw new RuntimeException("Error in evaluating expression");
        }
    }

    private String formatResult(double result) {

        BigDecimal bd = new BigDecimal(result).setScale(10, RoundingMode.HALF_UP);
        return bd.stripTrailingZeros().toPlainString();
    }
}