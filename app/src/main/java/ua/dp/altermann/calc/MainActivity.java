package ua.dp.altermann.calc;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.PatternSyntaxException;

import ua.dp.altermann.calc.expression.BaseExpr;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "Calc_main";

    EditText etExpression;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etExpression = (EditText) findViewById(R.id.etExpression);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etExpression.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        tvResult = (TextView) findViewById(R.id.tvResult);
//        Button btnCalc = (Button) findViewById(R.id.btnCalc);
    }

    public void onInput(View v) {
        etExpression.setText(etExpression.getText().toString() + ((Button) v).getText());
    }

    public void onClear(View v) {
        switch (v.getId()) {
            case R.id.btnClear:
                etExpression.setText("");
                break;
            case R.id.btnDel:
                String text = etExpression.getText().toString();
                if (text.length() > 0) {
                    etExpression.setText(text.substring(0, text.length() -1));
                }
                break;
        }
    }

    public void onCalc(View v) {
        try {
            String expr = etExpression.getText().toString();
            String result = BaseExpr.calc(expr);
            tvResult.setText(tvResult.getText() + "\n" + expr + " = " + result);
            Log.d(LOG_TAG, "RESULT: " + expr + " = " + result);
            etExpression.setText("");
        } catch (PatternSyntaxException e) {
            Log.d(LOG_TAG, e.getMessage()); // getDescription() + getIndex() + getPattern()
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            Log.d(LOG_TAG, e.getMessage());
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////// MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
