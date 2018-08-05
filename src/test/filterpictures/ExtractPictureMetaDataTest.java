package filterpictures;
import com.thoughtworks.xstream.XStream;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class ExtractPictureMetaDataTest {
    @Test
    public void getPictureMetaDataTest(){
        ExtractPictureMetaData testee = new ExtractPictureMetaData("/Volumes/MyDrive01/Lightroom/2018","/Volumes/MyDrive01/MyLightroom.csv" );
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

    @Test
    public void getPictureContentTest(){
        ExtractPictureMetaData testee = new ExtractPictureMetaData("/Volumes/MyDrive01/Lightroom/2018","/Volumes/MyDrive01/MyLightroom.csv" );
        File myFile = new File("src/main/resources/ExportTest/famromano-100.jpg");
        try {
            PictureMetaData output = testee.getPictureContent(myFile);
            XStream xStream = new XStream();
            System.out.println(xStream.toXML(output));
            assertEquals("Canon EOS 6D",output.getModel().get());
            assertEquals("Canon",output.getMake().get());
            assertEquals("2017:11:12 10:15:55",output.getDateTime().get());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void createCSVFileTest(){
        ExtractPictureMetaData testee = new ExtractPictureMetaData("/Volumes/MyDrive01/Lightroom/2018","/Volumes/MyDrive01/MyLightroom.csv" );
        try {
            // Y:\2018
            // testee.createCSVFile("/Volumes/MyDrive01/Lightroom/2018","/Volumes/MyDrive01/MyLightroom.csv" );
            // testee.createCSVFile("/Volumes/MyDrive01/Lightroom","/Volumes/MyDrive01/MyLightroom.csv" );
            // testee.createCSVFile("/Volumes/MyDrive01/Lightroom/2018/2018-07-08","/Volumes/MyDrive01/MyLightroom.csv" );
            // testee.createCSVFile("/Volumes/MyDrive01/Lightroom/2001","/Volumes/MyDrive01/MyLightroom.csv" );
            testee.createCSVFile("e:\\lightroom\\2018\\2018-07-08\\","e:\\lightroom\\MyLightroom.csv" );
            // testee.createCSVFile("e:\\lightroom\\2018\\","e:\\lightroom\\MyLightroom.csv" );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
