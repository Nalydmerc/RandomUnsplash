package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.UINT_PTR;
import com.sun.jna.win32.*;

import static java.util.concurrent.TimeUnit.*;


/**
 * @author Nalydmerc
 *
 * Grabs a random image from Unsplash, saves the photo, and sets it as a wallpaper.
 * Mostly pasted together from google resuts.
 * Links:
 *  https://source.unsplash.com/
 *  http://stackoverflow.com/questions/10292792/getting-image-from-url-java
 *  http://stackoverflow.com/questions/4750372/can-i-change-my-windows-desktop-wallpaper-programmatically-in-java-groovy
 *  https://github.com/java-native-access/jna
 */


public class Main {

    private static final ScheduledExecutorService sch = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        final Runnable GrabMeFreshPaper = new Runnable() {
            @Override
            public void run() {
                String filename = "C:\\Users\\intern\\Pictures\\" + LocalDate.now().toString() + ".jpg";
                BufferedImage unsplash = grabUnsplashPhoto();
                savePhoto(unsplash, filename);
                setWallpaper(filename);
            }
        };

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long delay = (c.getTimeInMillis()-System.currentTimeMillis());

        int delayMinutes = (int) delay/60000;

        sch.scheduleAtFixedRate(GrabMeFreshPaper, 0, 1440, MINUTES);
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

    public static void setWallpaper(String path) {
        SPI.INSTANCE.SystemParametersInfo(
                new UINT_PTR(SPI.SPI_SETDESKWALLPAPER),
                new UINT_PTR(0),
                path,
                new UINT_PTR(SPI.SPIF_UPDATEINIFILE | SPI.SPIF_SENDWININICHANGE));
    }

    public static void savePhoto(BufferedImage image, String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            ImageIO.write(image, "jpg", file);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage grabUnsplashPhoto() {
        try {

            String [] urlChoices = {
                    "https://source.unsplash.com/category/buildings/1920x1080",
                    "https://source.unsplash.com/category/nature/1920x1080"
            };

            String filename = "";

            Random rand = new Random();
            int i = rand.nextInt(urlChoices.length);
            String urlString = urlChoices[i];
            System.out.print(i);

            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.getResponseCode();
            urlString = con.getURL().toString();

            urlString = urlString.replace("&w=1920", "");
            urlString = urlString.replace("&h=1080", "");
            urlString = urlString.replace("&crop=entropy", "");
            urlString = urlString.replace("&fit=crop", "");

            System.out.println(urlString);

            url = new URL(urlString);

            return ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}