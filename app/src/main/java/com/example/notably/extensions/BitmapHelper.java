package com.example.notably.extensions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;

public class BitmapHelper {
    /**
     * request for the file path
     * @param uri for uri
     * @return file path (images, videos allowed)
     */
    public static String requestFilePath(Context context, Uri uri) {
        @SuppressLint("Recycle") Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);

        int nameIndex = Objects.requireNonNull(returnCursor).getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));

        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);

            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = Objects.requireNonNull(inputStream).available();

            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            Log.e("Size", "Size: " + size);
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return file.getPath();
    }
}
