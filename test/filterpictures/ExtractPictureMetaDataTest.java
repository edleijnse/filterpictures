package filterpictures;
import com.thoughtworks.xstream.XStream;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertEquals;


public class ExtractPictureMetaDataTest {
    @Test
    public void getPictureMetaDataTest(){
        ExtractPictureMetaData testee = new ExtractPictureMetaData("/Volumes/MyDrive01/Lightroom/2018","/Volumes/MyDrive01/MyLightroom.csv", true );
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
    public void createCSVFileTest() throws IOException {
        ExtractPictureMetaData testee = new ExtractPictureMetaData("/Volumes/MyDrive02/Lightroom/visionTest","/Volumes/MyDrive02/Lightroom/VisionTest/MyLightroomVision.csv", true );
        // String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("C:\\Users\\edlei\\OneDrive\\Finanzen\\Lizensen\\Microsoft\\keys\\subscriptionKey1")));
        String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("/Users/edleijnse/OneDrive/Finanzen/Lizensen/Microsoft/keys/subscriptionKey1")));


        testee.setSubscriptionKey(mySubscriptionKey);

        try {
            // Y:\2018
            // testee.createCSVFile("/Volumes/MyDrive01/Lightroom/2018","/Volumes/MyDrive01/MyLightroom.csv" );
            // testee.createCSVFile("/Volumes/MyDrive01/Lightroom","/Volumes/MyDrive01/MyLightroom.csv" );
            // testee.createCSVFile("/Volumes/MyDrive02/Lightroom/visionTest",
            //        "/Volumes/MyDrive02/Lightroom/visionTest/MyLightroomVision.csv", true );
            // testee.createCSVFile("/Volumes/MyDrive01/MyDrive01Original/bilderexport/MajaUndJacques",
            //       "/Volumes/MyDrive01/MyDrive01Original/bilderexport/MajaUndJacques/MyLightroomVision.csv", true );
            // testee.createCSVFile("y:\\MyDrive01Original\\bilderexport\\MajaUndJacques",
            //         "y:\\MyDrive01Original\\bilderexport\\MajaUndJacques\\MyLightroomVision.csv", true );
            testee.createCSVFile("/Volumes/MyDrive01/mydrive01original/bilderexport/Creativa 2017",
                    "/Volumes/MyDrive01/MyLightroomWithVision.csv", true );
            // testee.createCSVFile("/Volumes/MyDrive01/Lightroom/2001","/Volumes/MyDrive01/MyLightroom.csv" );
            // testee.createCSVFile("e:\\lightroom\\2018\\2018-07-08\\","e:\\lightroom\\MyLightroom.csv",true );
            // testee.createCSVFile("e:\\lightroom\\2018\\","e:\\lightroom\\MyLightroom.csv" );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
