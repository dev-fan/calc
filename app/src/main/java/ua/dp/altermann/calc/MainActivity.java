package ua.dp.altermann.calc;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import ua.dp.altermann.calc.expression.BaseExpr;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "Calc_main";
    public static final String LIST_ATTR_EXPRESSION = "expression";
    public static final String LIST_ATTR_RESULT = "result";

    private ListView lvLog;
    private EditText etExpression;
    private int logSize = 20;
    private SimpleAdapter logAdapter;
    private List<Map<String, Object>> logList = new ArrayList<>(20);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etExpression = (EditText) findViewById(R.id.etExpression);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etExpression.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        // Логирование выражений
        lvLog = (ListView) findViewById(R.id.lvLog);
        String[] fromSA = {LIST_ATTR_EXPRESSION, LIST_ATTR_RESULT};
        int[] toSA = {R.id.tvExpr, R.id.tvResult};
        logAdapter = new SimpleAdapter(this, logList, R.layout.list_log_item, fromSA, toSA);
        lvLog.setAdapter(logAdapter);
        lvLog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> item = (HashMap<String, Object>) parent.getAdapter().getItem(position);
                etExpression.setText((String) item.get(LIST_ATTR_EXPRESSION));
            }
        });
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
            etExpression.setText("");
            addLog(expr, result);
        } catch (PatternSyntaxException e) {
            Log.d(LOG_TAG, e.getMessage()); // getDescription() + getIndex() + getPattern()
        } catch (NumberFormatException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addLog(String expression, String result) {
        Map<String, Object> mSA = new HashMap<>();
        mSA.put(LIST_ATTR_EXPRESSION, expression);
        mSA.put(LIST_ATTR_RESULT, result);
        logList.add(0, mSA);
        if (logList.size() > logSize) {
            logList.remove(logSize);
        }
        logAdapter.notifyDataSetChanged();
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
