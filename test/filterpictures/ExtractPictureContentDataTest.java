package filterpictures;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;


public class ExtractPictureContentDataTest {
    Map<String, Integer> myTags = new TreeMap<>();

    @Test
    public void getPictureContentTest() throws IOException {
        ExtractPictureContentData testee = new ExtractPictureContentData("/Volumes/MyDrive01/Lightroom/2018", "/Volumes/MyDrive01/MyLightroom.csv");
        getPictureContentTestPicture("C:\\Users\\edlei\\OneDrive\\Testfotos\\robot.jpg");
        getPictureContentTestPicture("C:\\Users\\edlei\\OneDrive\\Testfotos\\ed.jpg");
        getPictureContentTestPicture("C:\\Users\\edlei\\OneDrive\\Testfotos\\mylena.jpg");
        getPictureContentTestPicture("C:\\Users\\edlei\\OneDrive\\Testfotos\\gemaelde.jpg");
        getPictureContentTestPicture("C:\\Users\\edlei\\OneDrive\\Testfotos\\landschaft.jpg");
        getPictureContentTestPicture("C:\\Users\\edlei\\OneDrive\\Testfotos\\landschaft2.jpg");
    }

    private void getPictureContentTestPicture(String fileName) throws IOException {
        ExtractPictureContentData testee = new ExtractPictureContentData("/Volumes/MyDrive01/Lightroom/2018", "/Volumes/MyDrive01/MyLightroom.csv");


        // File myFile = new File("src/main/resources/ExportTest/bigmountain01.jpg");
        File myFile = new File(fileName);
        File myFileCompressed = testee.compressJpg(myFile);
        try {
            // String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("/Users/edleijnse/keys/subscriptionKey1")));
            String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("C:\\Users\\edlei\\OneDrive\\Finanzen\\Lizensen\\Microsoft\\keys\\subscriptionKey1")));
            testee.setSubstringKey(mySubscriptionKey);
            PictureMetaData output = testee.getPictureContent(myFileCompressed);
            System.out.println(fileName);
            System.out.println(output.getVISION_CONTENT());
            System.out.println(output.getVISION_TAGS());
            System.out.println("---------------------------------------------------------------------------------");
            // XStream xStream = new XStream();
            // System.out.println(xStream.toXML(output));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void compressJpgTest() throws IOException {
        File inFile = new File("src/main/resources/ExportTest/bigmountain01.jpg");
        ExtractPictureContentData testee = new ExtractPictureContentData("/Volumes/MyDrive01/Lightroom/2018", "/Volumes/MyDrive01/MyLightroom.csv");
        File myNewFile = testee.compressJpg(inFile);
    }

    public String extractTagsTest(String iTags) {
        String oTags = iTags;
        String[] arrayOfStr = iTags.split("%");
        for (String myTagString : arrayOfStr) {
            Integer myValue = myTags.get(myTagString);
            if (myValue != null) {
                myValue++;
            } else {
                myValue = 1;
            }

            myTags.put(myTagString, myValue);
        }

        return oTags;
    }

    @Test
    public void topTagsTest() throws IOException {
        ExtractPictureContentData testee = new ExtractPictureContentData("src/main/resources/ExportTest", "src/main/resources//MyLightroom.csv");
        Map<String, Integer> visionTags = new TreeMap<>();
        String fileName = "src/main/resources/ExportTest/BilderExportAmeno2019.csv";

        Map<String, Integer> topTags = new TreeMap<>();
        visionTags = testee.extractVisionTags(fileName);
        topTags = testee.topTags(visionTags, 50);
        topTags.entrySet().
                forEach(entry -> {
                    System.out.println(entry.getKey() + " " + entry.getValue());
                });
    }

    @Test
    public void extractVisionTagsTest() throws IOException {
        try {
            Map<String, Integer> visionTags = new TreeMap<>();
            String fileName = "src/main/resources/ExportTest/BilderExportAmeno2019.csv";
            ExtractPictureContentData testee = new ExtractPictureContentData("src/main/resources/ExportTest", "src/main/resources//MyLightroom.csv");

            visionTags = testee.extractVisionTags(fileName);

            /*XStream xstream = new XStream();
            System.out.println(xstream.toXML(visionTags));
            System.out.println("myTags size: " + visionTags.size());*/

            visionTags.entrySet().stream().filter(entry -> {
                return (entry.getValue() > 99);
            }).forEach(entry -> {
                System.out.println(entry.getKey() + " " + entry.getValue());
            });
        } catch (IOException io) {
            io.printStackTrace();
        }

    }

}
