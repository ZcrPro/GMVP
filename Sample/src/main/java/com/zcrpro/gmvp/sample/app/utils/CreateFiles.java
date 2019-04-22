package com.zcrpro.gmvp.sample.app.utils;

import android.icu.text.IDNA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * ================================================
 * Created by zcrpro on 2019-04-22
 *
 * <a href="mailto:zcrpro@gmail.com">Contact me</a>
 * <a href="https://github.com/ZcrPro/GMVP">Follow me</a>
 * ================================================
 */
public class CreateFiles {


    String filenameTemp;

    public CreateFiles(String info, String CrashInfoPath) {
        this.filenameTemp = CrashInfoPath + "/gmvp";
        try {
            CreateText(info, CrashInfoPath, filenameTemp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //创建文件夹及文件
    public void CreateText(String info, String CrashInfoPath, String filenameTemp) throws IOException {
        File file = new File(CrashInfoPath);
        if (!file.exists()) {
            try {
                //按照指定的路径创建文件夹
                file.mkdirs();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        File dir = new File(filenameTemp);
        if (!dir.exists()) {
            try {
                //在指定的文件夹中创建文件
                dir.createNewFile();
            } catch (Exception e) {
            }
        }
    }

    //向已创建的文件中写入数据
    public void print(String info) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        String datetime = "";
        try {
            SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd" + " "
                    + "hh:mm:ss");
            datetime = tempDate.format(new java.util.Date()).toString();
            fw = new FileWriter(filenameTemp, true);//
            // 创建FileWriter对象，用来写入字符流
            bw = new BufferedWriter(fw); // 将缓冲对文件的输出
            String myreadline = datetime + "[]" + info;

            bw.write(myreadline + "\n"); // 写入文件
            bw.newLine();
            bw.flush(); // 刷新该流的缓冲
            bw.close();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                bw.close();
                fw.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
            }
        }
    }

}
