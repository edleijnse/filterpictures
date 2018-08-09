package filterpictures;
import com.thoughtworks.xstream.XStream;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;


public class ExtractPictureContentDataTest {

    @Test
    public void getPictureContentTest(){
        ExtractPictureContentData testee = new ExtractPictureContentData("/Volumes/MyDrive01/Lightroom/2018","/Volumes/MyDrive01/MyLightroom.csv" );
        File myFile = new File("src/main/resources/ExportTest/famromano-100.jpg");
        try {
            PictureMetaData output = testee.getPictureContent(myFile);
            XStream xStream = new XStream();
            System.out.println(xStream.toXML(output));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
