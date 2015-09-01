package ua.dp.altermann.calc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import ua.dp.altermann.calc.adapter.LogAdapter;
import ua.dp.altermann.calc.expression.BaseExpr;
import ua.dp.altermann.calc.model.LogModel;

public class MainActivity extends Activity {

    public static final String LOG_TAG = "Calc_main";
    protected static final String SAVE_EXPR = "save_expr";
    protected static final String SAVE_RESULT = "save_result";

    private ListView lvLog;
    private EditText etExpression;
    private int logSize = 20;
    private LogAdapter logAdapter;
    private List<LogModel> logList = new ArrayList<>(21);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etExpression = (EditText) findViewById(R.id.etExpression);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etExpression.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        // Логирование выражений
        lvLog = (ListView) findViewById(R.id.lvLog);
        logAdapter = new LogAdapter(this, logList);
        lvLog.setAdapter(logAdapter);
        lvLog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogModel item = (LogModel) parent.getAdapter().getItem(position);
                etExpression.setText(item.expression);
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
        LogModel logModel = new LogModel(expression, result);
        logList.add(0, logModel);
        if (logList.size() > logSize) {
            logList.remove(logSize);
        }
        logAdapter.notifyDataSetChanged();
        lvLog.smoothScrollToPosition(0);
    }

    ///////////////////////////////////////////////////////////////////////////////// SAVE & RESTORE
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<String> expressions = savedInstanceState.getStringArrayList(SAVE_EXPR);
        ArrayList<String> results = savedInstanceState.getStringArrayList(SAVE_RESULT);
        if (expressions != null && results != null) {
            for (int i = 0; i < expressions.size() && i < results.size(); i++) {
                logList.add(new LogModel(expressions.get(i), results.get(i)));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> expressions = new ArrayList<>();
        ArrayList<String> results = new ArrayList<>();
        for (LogModel cm : logList) {
            expressions.add(cm.expression);
            results.add(cm.result);
        }
        outState.putStringArrayList(SAVE_EXPR, expressions);
        outState.putStringArrayList(SAVE_RESULT, results);
    }

}
