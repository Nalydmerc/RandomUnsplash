package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.UINT_PTR;
import com.sun.jna.win32.*;

// Grabs a random image from Unsplash, saves the photo, and sets it as a wallpaper.
// Mostly pasted together from google resuts.
// Links:
// https://source.unsplash.com/
// http://stackoverflow.com/questions/10292792/getting-image-from-url-java
// http://stackoverflow.com/questions/4750372/can-i-change-my-windows-desktop-wallpaper-programmatically-in-java-groovy
// https://github.com/java-native-access/jna

public class Main {

    public static void main(String[] args) {

        String filename = "";

        try {
            URL url = new URL("https://source.unsplash.com/category/buildings/1920x1080");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.getResponseCode();
            String urlString = con.getURL().toString();

            urlString = urlString.replace("&w=1920", "");
            urlString = urlString.replace("&h=1080", "");
            urlString = urlString.replace("&crop=entropy", "");
            urlString = urlString.replace("&fit=crop", "");
            //urlString = urlString.replace("&fm=jpg", "&fm=png");

            url = new URL(urlString);

            filename = "C:\\Users\\intern\\Pictures\\" + LocalDate.now().toString() + ".jpg";

            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedImage image = ImageIO.read(url);
            ImageIO.write(ImageIO.read(url), "jpg", file);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SPI.INSTANCE.SystemParametersInfo(
                new UINT_PTR(SPI.SPI_SETDESKWALLPAPER),
                new UINT_PTR(0),
                filename,
                new UINT_PTR(SPI.SPIF_UPDATEINIFILE | SPI.SPIF_SENDWININICHANGE));
    }

        public interface SPI extends StdCallLibrary {

        long SPI_SETDESKWALLPAPER = 20;
        long SPIF_UPDATEINIFILE = 0x01;
        long SPIF_SENDWININICHANGE = 0x02;

        SPI INSTANCE = (SPI) Native.loadLibrary("user32", SPI.class, new HashMap<Object, Object>() {
            {
                put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
                put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
            }
        });

        boolean SystemParametersInfo(
                UINT_PTR uiAction,
                UINT_PTR uiParam,
                String pvParam,
                UINT_PTR fWinIni
        );
    }
}
