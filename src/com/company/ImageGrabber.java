package com.company;

import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nalydmerc on 12/17/2015.
 *
 * WIP
 */
public class ImageGrabber {

    public static enum redditCriteria {
        DAY,
        WEEK,
        MONTH,
        YEAR,
        ALL
    }

    private static String[] subreddits = {"Earthporn"};

    public static BufferedImage grabRandomRedditPhoto(redditCriteria criteria) {
        Random random = new Random();
        String subreddit = subreddits[random.nextInt(subreddits.length)];
        return grabRandomRedditPhoto(criteria, subreddit);
    }

    public static BufferedImage grabRandomRedditPhoto(redditCriteria criteria, String subreddit) {
        URL url;
        InputStream is = null;
        BufferedReader br;


        try {

            //Get reddit post list

            url = new URL("https://www.reddit.com/r/" + subreddit + "/top.json?count=100&t=all");
            System.out.println(url);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));
            String json = br.readLine();
            System.out.println(json);

            PrintWriter writer = new PrintWriter("C:\\Users\\intern\\Desktop\\json.txt");
            writer.write(json);
            writer.close();


            File file = new File("C:\\Users\\intern\\Desktop\\json.txt");
            BufferedReader br1 = new BufferedReader(new FileReader(file));
            json = br1.readLine();
            System.out.print(json);

            //Get imgur links
            JSONObject job = new JSONObject(json);
            JSONArray posts = job.getJSONObject("data").getJSONArray("children");
            boolean stop = false;
            ArrayList<String> postLinks = new ArrayList<>();
            for (int i = 0; i < posts.length(); i++) {
                JSONObject data = posts.getJSONObject(i).getJSONObject("data");
                if (data.getBoolean("over_18") == false) { //no nsfw
                    String link = data.getString("url");
                    if (link.contains("imgur")) {
                        if (!link.contains("i.imgur")) {
                            link = link.replace("imgur", "i.imgur");
                            link = link.replace("https://", "http://");
                            link += ".jpg";
                        }
                        postLinks.add(link);
                    }
                }
            }

            for (String link: postLinks) {
                System.out.println(link);
            }

            Random random = new Random();
            String randomLink = postLinks.get(random.nextInt(postLinks.size()));
            URL picUrl = new URL(randomLink);

            //Stupid workaround
            if (false) {
                throw new MalformedURLException();
            } else if (false) {
                throw new IOException();
            }

            return ImageIO.read(picUrl);

        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }
        return null;
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
