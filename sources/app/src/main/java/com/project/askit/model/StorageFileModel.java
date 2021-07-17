package com.project.askit.model;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class StorageFileModel {

    private Context context;
    private String fileName;
    private String path = "logs";

    public StorageFileModel(Context context) {
        this.context = context;
    }

    public StorageFileModel(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public String readFromInternalStorage() {
        String content = null;
        try {

            FileInputStream fileInputStream = this.context.openFileInput(fileName);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(dataInputStream));
            String line;
            content = "";
            while ((line = br.readLine()) != null) {
                content += line;
            }
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    public void writeToInternalStorage(String content) {
        try {
            FileOutputStream fileOutputStream = this.context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileOutputStream);
            outputWriter.write(content);
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void appendToInternalStorage(String content) {
        String currentContent = readFromInternalStorage();
        if (currentContent != null) {
            content = currentContent +  content;
        }

        writeToInternalStorage(content);
    }

    public String readFromExternalStorage() {
        String content = null;
        try {
            File file;
            // Check if external storage is writable
            if (isExternalStorageWritable()) {
                file = new File(this.context.getExternalFilesDir(path), fileName);
            } else {
                throw new Exception("External storage not writable");
            }

            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
            String line;
            content = "";
            while ((line = bufferedReader.readLine()) != null) {
                content += line + "\n";
            }
            dataInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }

    public void writeToExternalStorage(String content) {
        try {
            File file;
            // Check if external storage is writable
            if (isExternalStorageWritable()) {
                file = new File(this.context.getExternalFilesDir(path), fileName);
            } else {
                throw new Exception("External storage not writable");
            }

            FileOutputStream outputStream = new FileOutputStream(file);
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
            outputWriter.write(content);
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void appendToExternalStorage(String content) {
        String currentContent = readFromExternalStorage();
        if (currentContent != null) {
            content = currentContent + content;
        }

        writeToExternalStorage(content);
    }

}
