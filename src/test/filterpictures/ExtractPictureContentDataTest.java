package filterpictures;
import com.thoughtworks.xstream.XStream;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertEquals;


public class ExtractPictureContentDataTest {

    @Test
    public void getPictureContentTest() throws IOException {
        ExtractPictureContentData testee = new ExtractPictureContentData("/Volumes/MyDrive01/Lightroom/2018","/Volumes/MyDrive01/MyLightroom.csv" );
        getPictureContentTestPicture("C:\\Users\\edlei\\OneDrive\\Testfotos\\robot.jpg");
        getPictureContentTestPicture("C:\\Users\\edlei\\OneDrive\\Testfotos\\ed.jpg");
        getPictureContentTestPicture("C:\\Users\\edlei\\OneDrive\\Testfotos\\mylena.jpg");
        getPictureContentTestPicture("C:\\Users\\edlei\\OneDrive\\Testfotos\\gemaelde.jpg");
        getPictureContentTestPicture("C:\\Users\\edlei\\OneDrive\\Testfotos\\landschaft.jpg");
        getPictureContentTestPicture("C:\\Users\\edlei\\OneDrive\\Testfotos\\landschaft2.jpg");
    }
    private void getPictureContentTestPicture(String fileName) throws IOException {
        ExtractPictureContentData testee = new ExtractPictureContentData("/Volumes/MyDrive01/Lightroom/2018","/Volumes/MyDrive01/MyLightroom.csv" );


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
        ExtractPictureContentData testee = new ExtractPictureContentData("/Volumes/MyDrive01/Lightroom/2018","/Volumes/MyDrive01/MyLightroom.csv" );
        File myNewFile = testee.compressJpg(inFile);
    }


}
