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
    public void getPictureContentTest(){
        ExtractPictureContentData testee = new ExtractPictureContentData("/Volumes/MyDrive01/Lightroom/2018","/Volumes/MyDrive01/MyLightroom.csv" );


        File myFile = new File("src/main/resources/ExportTest/zuerich-16.jpg");
        try {
            String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("/Users/edleijnse/keys/subscriptionKey1")));
            testee.setSubstringKey(mySubscriptionKey);
            PictureMetaData output = testee.getPictureContent(myFile);
            XStream xStream = new XStream();
            System.out.println(xStream.toXML(output));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void compressJpgTest(){
        File inFile = new File("src/main/resources/ExportTest/famromano-109.jpg");
        ExtractPictureContentData testee = new ExtractPictureContentData("/Volumes/MyDrive01/Lightroom/2018","/Volumes/MyDrive01/MyLightroom.csv" );
        File myNewFile = testee.compressJpg(inFile);
    }


}
