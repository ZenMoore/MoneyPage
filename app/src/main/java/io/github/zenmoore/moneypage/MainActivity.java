package io.github.zenmoore.moneypage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    static final double page_min = 10;
    static final double page_max = 20;

    private double finance = 0.0;
    private double pages = 0.0;
    private double multiple = 0.0;

    private TextView amount = null;
    private TextView pagenum = null;
    private TextView multiple_view = null;
    private SeekBar seekbar = null;


    public void load(){
        Data.load();

        double[] a = Data.getAmounts();
        double[] p = Data.getPages();

        double aa = 0.0;
        double pp = 0.0;
        for(int i=0; i<a.length; i++){
            aa += a[i];
            pp += p[i];
        }

        this.finance = aa;
        this.pages = pp;

        this.amount.setText("" + this.finance);
        this.pagenum.setText("" + this.pages);
    }

    public void update(double finance, double pages){
        // 修改 Home 的值
        this.finance += finance;
        this.pages += pages;

        this.amount.setText("" + finance);
        this.pagenum.setText("" + pages);
    }

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
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.setTitle("增加条目:");
//        builder.setIcon(android.R.drawable.ic_dialog_info);
        dialog.setView(EntryView);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double price = 0.0;
                double page = 0.0;

                // 安全机制：数据类型判断
                String commodity = editCommodity.getText().toString();
                try{
                    price = Double.parseDouble(editPrice.getText().toString());
                    String book = editBook.getText().toString();
                    page = price * multiple;

                    String time = editTime.getText().toString();

                    // add item to database
                    Data.insert(commodity, book, time, price, page);

                    update(price, page);
                    dialog.dismiss();
                }catch (NumberFormatException e){
                    Toast.makeText(getApplicationContext(), "常洁，你的金额不是数字", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
            get permissions
         */
        new MainRequestPermission().writeExternalStorage(MainActivity.this);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle("");

//        MainActivity.this.getBaseContext().getFilesDir().getPath()
//        Context context = getApplicationContext();
//        Setting.setFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MoneyPage");
        Setting.setDatabase(getApplicationContext());
        Data.setDatabase(getApplicationContext());

        if(Data.num_items() == 0){
            Data.insert("placeholder", "placeholder", "placeholder", 0.0, 0.0);
        }

        // load component of amount & pagenum
//        LayoutInflater factory = LayoutInflater.from(this);
//        final View EntryView = factory.inflate(R.layout.content_main, null);
        amount = (TextView)findViewById(R.id.amount);
        multiple_view = (TextView)findViewById(R.id.multiple);
        pagenum = (TextView)findViewById(R.id.pagenum);
        seekbar = (SeekBar) findViewById(R.id.seekBar);

        // 从数据库加载 finance的值
        load();


        // set default seekbar progress
        try{
            double progress = Double.parseDouble(Setting.read("default_multiple"));
            seekbar.setProgress((int)progress);
        }catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            AlertDialog dialog = builder.create();
            dialog.setTitle("Exception");
            dialog.setMessage(e.getMessage());
            dialog.show();
        }

//        System.out.println(finance);
        amount.setText("" + finance);
        multiple = page_min + (seekbar.getProgress() - seekbar.getMin()) * (page_max - page_min)/(seekbar.getMax() - seekbar.getMin());
        multiple_view.setText("x"+multiple);
        pages = finance * multiple;
        pagenum.setText("" + pages);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                multiple = page_min + (seekBar.getProgress() - seekBar.getMin()) * (page_max - page_min)/(seekBar.getMax() - seekBar.getMin());
                multiple_view.setText("x" + multiple);
                pages = finance * multiple;
                pagenum.setText("" + pages);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 更新 database 的值
                Data.change_multiple(multiple);

                try{
                    Setting.set("default_multiple", ""+seekBar.getProgress());
                }catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    AlertDialog dialog = builder.create();
                    dialog.setTitle("Exception");
                    dialog.setMessage(e.getMessage());
                    dialog.show();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                addItem();
            }
        });

        Button setting = (Button)findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final AlertDialog dialog = builder.create();
                dialog.setTitle("Setting");
                dialog.setMessage("No settings.");
                dialog.show();
            }
        });

        Button data = (Button) findViewById(R.id.data);
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itent=new Intent();
                itent.setClass(MainActivity.this, DataActivity.class);
                startActivity(itent);
//                MainActivity.this.finish();
            }
        });

        this.pagenum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "竟然还有这么多书没有看！！", Toast.LENGTH_LONG).show();
            }
        });

        this.amount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "看看你花了多少钱！！", Toast.LENGTH_LONG).show();
                    }
                }
        );

        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "别看杨洋了！快去看书！", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
       load();
    }
}
