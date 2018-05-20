package filterpictures;
import com.thoughtworks.xstream.XStream;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class ExtractPictureMetaDataTest {
    @Test
    public void getPictureMetaDataTest(){
        ExtractPictureMetaData testee = new ExtractPictureMetaData();
        File myFile = new File("src/main/resources/ExportTest/famromano-100.jpg");
        try {
            PictureMetaData output = testee.getPictureMetaData(myFile);
            XStream xStream = new XStream();
            System.out.println(xStream.toXML(output));
            assertEquals("Canon EOS 6D",output.getModel().get());
            assertEquals("Canon",output.getMake().get());
            assertEquals("2017:11:12 10:15:55",output.getDateTime().get());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
