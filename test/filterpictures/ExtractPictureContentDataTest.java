package filterpictures;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;


public class ExtractPictureContentDataTest {

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

    @Test
    public void extractVisionTagsTest() throws IOException {
        try {
            String fileName = "src/main/resources/ExportTest/BilderExportAmeno2019.csv";
            Stream<String> lines = Files
                    .lines(Paths.get(fileName))
                    .filter(line -> line.contains("%"))
                    .map(line -> {
                        int myIndexContentBegin = StringUtils.ordinalIndexOf(line, ",", 73);
                        int myIndexContentEnd = StringUtils.ordinalIndexOf(line, ",", 74);
                        int myIndexTagsBegin = StringUtils.ordinalIndexOf(line, ",", 74);
                        int myIndexImageEnd = StringUtils.ordinalIndexOf(line, ",", 1);
                        String imageName = line.substring(0, myIndexImageEnd);
                        String imageDescription = line.substring(myIndexContentBegin + 1, myIndexContentEnd);
                        String imageTags = line.substring(myIndexTagsBegin + 1);
                        String myOutput = imageName + ";" +
                                imageDescription + ";" +
                                imageTags;
                        return myOutput;
                    });

            System.out.println("<!-----Filtering the file data using Java8 filtering-----!>");
            lines.forEach(System.out::println);
            lines.close();
        } catch (IOException io) {
            io.printStackTrace();
        }

    }

}
