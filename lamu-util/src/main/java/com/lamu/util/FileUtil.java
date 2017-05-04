package com.lamu.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by songliang on 2015/12/15.
 *
 * @author songliang
 */
public class FileUtil {

    public static String judgeFileType(InputStream inputStream) {
        String fileHeader = getFileHeader(inputStream);
        if (fileHeader == null || fileHeader.length() == 0) {
            return null;
        }
        String s = fileHeader.toUpperCase();
        String type = null;
        for (FileType fileType : FileType.values()) {
            if (s.startsWith(fileType.getName())) {
                type = fileType.toString().toLowerCase();
            } else {
                continue;
            }

        }
        return type;
    }

    public static String getFileHeader(InputStream inputStream) {
        byte[] bytes = new byte[28];
        try {
            inputStream.read(bytes, 0, 28);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesToHexString(bytes);
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
        }
        return sb.toString();
    }
}
