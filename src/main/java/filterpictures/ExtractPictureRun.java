package filterpictures;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExtractPictureRun {
    public static void main(String[] args){
        try {
            ExtractPictureRun extractPictureRun = new ExtractPictureRun();
            // extractPictureRun.createCSVFileRun("/Volumes/MyDrive01/BilderExport/201902ExtractSmall","/Volumes/MyDrive01/BilderExport/MyLightroomVision201902ExtractSmall.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public ExtractPictureRun() throws IOException {
        // this.createCSVFileRun("/Volumes/MyDrive01/BilderExport/201902ExtractSmall","/Volumes/MyDrive01/BilderExport/MyLightroomVision201902ExtractSmall.csv");
        this.createCSVFileRun("/home/edleijnse/BilderExport/Annalis/Transformationen", "/home/edleijnse/filterpicturesBilderExportAnnalisTransformationen20191206.csv");
    }
    public void createCSVFileRun(String startDir, String writeCSV) throws IOException {
        ExtractPictureMetaData testee = new ExtractPictureMetaData(startDir,writeCSV, true );
        // String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("C:\\Users\\edlei\\OneDrive\\Finanzen\\Lizensen\\Microsoft\\keys\\subscriptionKey1")));
        // String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("/Users/edleijnse/OneDrive/Finanzen/Lizensen/Microsoft/keys/subscriptionKey1")));
        String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("/home/edleijnse/keys/Microsoft/subscriptionKey1")));



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
            // testee.createCSVFile("/Volumes/MyDrive01/BilderExport/Ameno 2019",
            //         "/Volumes/MyDrive01/BilderExportAmeno2019.csv", true );
            testee.createCSVFile(startDir,
                    writeCSV, true );
            // testee.createCSVFile("/Volumes/MyDrive01/Lightroom/2001","/Volumes/MyDrive01/MyLightroom.csv" );
            // testee.createCSVFile("e:\\lightroom\\2018\\2018-07-08\\","e:\\lightroom\\MyLightroom.csv",true );
            // testee.createCSVFile("e:\\lightroom\\2018\\","e:\\lightroom\\MyLightroom.csv" );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
