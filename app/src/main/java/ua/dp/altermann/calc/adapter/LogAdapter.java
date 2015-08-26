package ua.dp.altermann.calc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ua.dp.altermann.calc.R;
import ua.dp.altermann.calc.model.LogModel;

public class LogAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    List<LogModel> list;

    public LogAdapter(Context ctx, List<LogModel> list) {
        this.ctx = ctx;
        this.list = list;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public LogModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_log_item, parent, false);
        }
        LogModel logModel = getItem(position);
        ((TextView) view.findViewById(R.id.tvExpr)).setText(logModel.expression);
        ((TextView) view.findViewById(R.id.tvResult)).setText(logModel.result);
        return view;
    }

}
