package com.amaze.filemanager.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amaze.filemanager.R;
import com.amaze.filemanager.activities.superclasses.ThemedActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FilesSaveboxActivity extends ThemedActivity {
    private static String passwordFile = "test.txt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_savebox);

        setToolbar();
        showInputPasswordDialog();

    }

    private void setToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.savebox_extras);//设置右上角的填充菜单
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modify_password:
                        final EditText editText1 = new EditText(FilesSaveboxActivity.this);
                        AlertDialog.Builder inputDialog1 = new AlertDialog.Builder(FilesSaveboxActivity.this);
                        inputDialog1.setTitle("请输入新密码").setView(editText1);
                        inputDialog1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String str1=editText1.getText().toString();
                                inputDialog1.create().dismiss();
                                final EditText editText2 = new EditText(FilesSaveboxActivity.this);
                                AlertDialog.Builder inputDialog2 = new AlertDialog.Builder(FilesSaveboxActivity.this);
                                inputDialog2.setTitle("请再次输入新密码以确认").setView(editText2);
                                inputDialog2.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(str1.equals(editText2.getText().toString()))
                                        {
                                            setPassword(str1);
                                            //Toast.makeText(getApplicationContext(), "密码修改成功", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), "两次输入密码不一致，修改失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                inputDialog2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(), "取消", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                AlertDialog alertDialog2 = inputDialog2.create();
                                alertDialog2.show();
                            }
                        });
                        inputDialog1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "取消", Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog alertDialog1 = inputDialog1.create();
                        alertDialog1.show();
                        break;

                    case R.id.share:
                        String psw="";
                        try {
                            psw = read(passwordFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "我的密码是"+psw);
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, "Share to..."));
                        break;
                }
                return true;
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//返回
            }
        });

    }

    private void showInputPasswordDialog()
    {
        final EditText editText = new EditText(FilesSaveboxActivity.this);
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance()); //设置为密码输入框
        editText.setHint("请输入密码");

        AlertDialog.Builder inputDialog = new AlertDialog.Builder(FilesSaveboxActivity.this);
        inputDialog.setTitle("请输入密码").setView(editText);
        inputDialog.setMessage("请输入密码以开启您的文件保险箱。若未设置密码，请直接点击确认。");
        inputDialog.setPositiveButton("确定",null);
        inputDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "取消", Toast.LENGTH_SHORT).show();
                gobackToMain();
            }
        });
        AlertDialog alertDialog = inputDialog.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editText.getText().toString();
                String psw="";
                try {
                    psw = read(passwordFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (input.equals(psw)) {
                    //Toast.makeText(getApplicationContext(), "密码正确", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void gobackToMain()
    {
        Intent in = new Intent(FilesSaveboxActivity.this, MainActivity.class);
        in.setAction(Intent.ACTION_MAIN);
        in.setAction(Intent.CATEGORY_LAUNCHER);
        startActivity(in);
        finish();
    }

    public void setPassword(String newPassword)
    {
        try {
            save(passwordFile, newPassword);
            Toast.makeText(getApplicationContext(), "密码修改成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "数据写入失败，密码修改失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void setPasswordForTest(File file, String newPassword){
        try {
            saveForTest(file, newPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 文件保存
     * */
    public void save(String filename, String filecontent) throws Exception {
        //使用私有模式,创建出来的文件只能被本应用访问,还会覆盖原文件
        FileOutputStream output = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
        output.write(filecontent.getBytes());  //将String字符串以字节流的形式写入到输出流中
        output.close();         //关闭输出流
    }

    public void saveForTest(File file, String filecontent) throws IOException {
        FileOutputStream output = new FileOutputStream(file);
        output.write(filecontent.getBytes());  //将String字符串以字节流的形式写入到输出流中
        output.close();         //关闭输出流
    }

    /*
     * 文件读取
     * */
    public String read(String filename) throws IOException {
        //打开文件输入流
        FileInputStream input = getApplicationContext().openFileInput(filename);
        byte[] temp = new byte[1024];
        StringBuilder sb = new StringBuilder("");
        int len = 0;
        //读取文件内容:
        while ((len = input.read(temp)) > 0) {
            sb.append(new String(temp, 0, len));
        }
        //关闭输入流
        input.close();
        return sb.toString();
    }

    public String readForTest(File file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        byte[] temp = new byte[1024];
        StringBuilder sb = new StringBuilder("");
        int len = 0;
        //读取文件内容:
        while ((len = input.read(temp)) > 0) {
            sb.append(new String(temp, 0, len));
        }
        //关闭输入流
        input.close();
        return sb.toString();
    }

}
