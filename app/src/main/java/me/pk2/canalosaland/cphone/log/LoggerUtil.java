package me.pk2.canalosaland.cphone.log;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import me.pk2.canalosaland.cphone.MainActivity;
import me.pk2.canalosaland.cphone.R;

public class LoggerUtil {
    public static File _LOG_FILE_CHECK() {
        String path = MainActivity.INSTANCE.getCacheDir().getAbsolutePath()+ "/amog/log.txt";

        File file = new File(path);
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if(!file.exists())
            try {
                file.createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        return file;
    }

    public static void _LOG_FILE_CLEAR() {
        File file = _LOG_FILE_CHECK();
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        } catch (Exception exception) { exception.printStackTrace(); }
    }

    public static void _LOG_FILE(String log) {
        File file = _LOG_FILE_CHECK();
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.write(log+"\n");
            writer.close();
        } catch (Exception exception) { exception.printStackTrace(); }
    }

    public static void _ALERT(Context context, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(text);
        builder.setPositiveButton(R.string.button_click_accept, (dialog, which) -> {});
        builder.create().show();
    }

    public static void _ALERT(Context context, int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(messageId);
        builder.setPositiveButton(R.string.button_click_accept, (dialog, which) -> {});
        builder.create().show();
    }
}