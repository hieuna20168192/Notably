package com.example.notably.extensions;

import android.os.Environment;
import android.util.Log;

import com.example.notably.repos.entities.Note;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static String requestExportFile(Note note, String format) {
        String FILE_NAME = note.getTitle().replaceAll(" ", "_").toLowerCase() + "." + format;
        // format note
        String note_text = "Note title: " + note.getTitle() + "\n\nNote subtitle: " + note.getSubtitle() + "\n\nNote description: " + note.getDescription();
        // create directory
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + "/com.hieuna.note" + "/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // save file
        File file = new File(dir,  FILE_NAME);
        // write to file
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(note_text.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("requestExportFile", e.getMessage());
            return e.getMessage();
        }
        return FILE_NAME;
    }
}
