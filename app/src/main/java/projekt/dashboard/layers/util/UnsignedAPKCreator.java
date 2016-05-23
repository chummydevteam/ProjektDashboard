package projekt.dashboard.layers.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Nicholas Chum (nicholaschum)
 */

public class UnsignedAPKCreator {

    public static void main(String whereTheZIPShouldBe, String whichDirectoryToCompress)
            throws Exception {
        zipDir(whereTheZIPShouldBe, whichDirectoryToCompress);
    }

    private static void zipDir(String whereTheZIPShouldBe, String whichDirectoryToCompress)
            throws Exception {
        File directory = new File(whichDirectoryToCompress);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(whereTheZIPShouldBe));
        Log.d("UnsignedAPKCreator", "File created: " + whereTheZIPShouldBe);
        addDir(directory, out);
        out.close();
    }

    static void addDir(File directory, ZipOutputStream zipOutputStream) throws IOException {
        File[] files = directory.listFiles();
        byte[] buffer = new byte[1024];

        for (int i = 0; i < files.length; i++) {

            if (files[i].isDirectory()) {
                addDir(files[i], zipOutputStream);
                continue;
            }

            FileInputStream in = new FileInputStream(files[i].getAbsolutePath());

            // Using .substring(51) to slice away the file paths
            Log.d("UnsignedAPKCreator", "File added: " + files[i].getAbsolutePath().substring(51));
            zipOutputStream.putNextEntry(new ZipEntry(files[i].getAbsolutePath().substring(51)));

            int len;
            while ((len = in.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, len);
            }
            zipOutputStream.closeEntry();
            in.close();
        }
    }
}