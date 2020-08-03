package io.github.zenmoore.moneypage;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

public class DataActivity extends AppCompatActivity {

    private static double multiple = 0.0;

    private void addItem(){
        /*@setView 装入一个EditView
         */
        LayoutInflater factory = LayoutInflater.from(this);
        final View EntryView = factory.inflate(R.layout.add_item, null);
        final EditText editCommodity = (EditText) EntryView.findViewById(R.id.editCommodity);
        final EditText editPrice = (EditText)EntryView.findViewById(R.id.editPrice);
        final EditText editBook = (EditText)EntryView.findViewById(R.id.editBook);
//        final EditText editPage = (EditText)EntryView.findViewById(R.id.editPage);
        final EditText editTime = (EditText)EntryView.findViewById(R.id.editTime);
        final Button ok = (Button)EntryView.findViewById(R.id.ok);
        final AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.setTitle("增加条目:");
//        builder.setIcon(android.R.drawable.ic_dialog_info);
        dialog.setView(EntryView);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double price = 0.0;

                // 安全机制：数据类型判断
                String commodity = editCommodity.getText().toString();
                try{
                    price = Double.parseDouble(editPrice.getText().toString());
                    String book = editBook.getText().toString();
                    double page = price * multiple;
                    String time = editTime.getText().toString();

                    // add item to database
                    Data.insert(commodity, book, time, price, page);

                    update((LinearLayout)findViewById(R.id.layout));
                    dialog.dismiss();
                }catch (NumberFormatException e){
//                        Toast.makeText(getApplicationContext(), "常洁，你的金额不是数字", Toast.LENGTH_LONG).show();
                    editPrice.setText("常洁，你的金额不是数字");

                }
            }
        });
//        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface builder, int i) {
//
//
//            }
//        });
//        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface builder, int i) {
//
//            }
//        });
        dialog.show();// 显示对话框

    }

    private void editItem(TextView view){
        LayoutInflater factory = LayoutInflater.from(this);
        final View EntryView = factory.inflate(R.layout.edit_item, null);
        final EditText editCommodity = (EditText) EntryView.findViewById(R.id.editCommodity0);
        final EditText editPrice = (EditText)EntryView.findViewById(R.id.editPrice0);
        final EditText editBook = (EditText)EntryView.findViewById(R.id.editBook0);
//        final EditText editPage = (EditText)EntryView.findViewById(R.id.editPage0);
        final EditText editTime = (EditText)EntryView.findViewById(R.id.editTime0);
        final AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity.this);
//        final AlertDialog dialog = builder.create();
        builder.setTitle("修改条目:");
//        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setView(EntryView);

        String[] items = view.getText().toString().split(", ");
        final int id = Integer.parseInt(items[0].split(". ")[0])+1;
        editCommodity.setText(items[0].split("=")[1]);
        editPrice.setText(items[1].split("=")[1]);
        editTime.setText(items[2].split("=")[1]);
        editBook.setText(items[3].split("=")[1]);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface builder, int i) {
                String commodity = editCommodity.getText().toString();
                String book = editBook.getText().toString();
                String time = editTime.getText().toString();
                double amount = Double.parseDouble(editPrice.getText().toString());

                Data.update(id, commodity, book, time, amount, amount * multiple);
                update((LinearLayout)findViewById(R.id.layout));
            }
        });

        builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface builder, int i) {
//                if(Data.num_items() == 1){
//                    Toast.makeText(getApplicationContext(), "最后仅剩的一个数据不能删除", Toast.LENGTH_LONG);
//                }
                Data.remove(id);
                update((LinearLayout)findViewById(R.id.layout));
                Toast.makeText(getApplicationContext(), "已经删除", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();// 显示对话框
    }

    public void update(LinearLayout layout){
        // 从数据库加载表格
        Data.load();
        layout.removeAllViews();

        for(int i = 0; i<Data.getNum(); i++){
            if(i==0){
                continue;
            }
            StringBuffer string = new StringBuffer();
            string.append((i)+". ");
            string.append("品=" + Data.getCommodities()[i]);
            string.append(", 金=" + Data.getAmounts()[i]);
            string.append(", 时=" + Data.getTimes()[i]);
            string.append(", 书=" + Data.getBooks()[i]);
            string.append(", 页=" + Data.getPages()[i]);

            final TextView item = new TextView(DataActivity.this);
            Space space = new Space(DataActivity.this);
            space.setMinimumHeight(18);

            item.setText(new String(string));
            item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            item.setTextColor(Color.BLACK);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editItem(item);
                }
            });

            layout.addView(space);
            layout.addView(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        LinearLayout layout = (LinearLayout)findViewById(R.id.layout);

        try{
            multiple = Double.parseDouble(Setting.read("default_multiple"));
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();//todo dialog
        }

        update(layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_in_data);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        Button diagram = (Button)findViewById(R.id.diagram);
        Button filter = (Button)findViewById(R.id.filter);

        diagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity.this);
                final AlertDialog dialog = builder.create();
                dialog.setTitle("Diagram");
                dialog.setMessage("No diagrams.");
                dialog.show();
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity.this);
                final AlertDialog dialog = builder.create();
                dialog.setTitle("Filter");
                dialog.setMessage("No filters.");
                dialog.show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        update((LinearLayout)findViewById(R.id.layout));
        try{
            multiple = Double.parseDouble(Setting.read("default_multiple"));
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();//todo dialog
        }
    }
}
