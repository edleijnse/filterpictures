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
        // this.createCSVFileRun("/home/edleijnse/BilderExport/Annalis/Transformationen", "/home/edleijnse/filterpicturesBilderExportAnnalisTransformationen20191206.csv");
        this.copyFilesRun("E:\\BilderImport\\Annalis\\BilderAnnalis","E:\\test\\AesthetikDesZerfalls", "E:\\test\\AesthetikDesZerfallsCopy");
        // this.buildTitleMap("E:\\BilderImport\\Annalis\\BilderAnnalis");
    }
    public void createCSVFileRun(String startDir, String writeCSV) throws IOException {
        ExtractPictureMetaData testee = new ExtractPictureMetaData(startDir,writeCSV, true );
        // String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("C:\\Users\\edlei\\OneDrive\\Finanzen\\Lizensen\\Microsoft\\keys\\subscriptionKey1")));
        // String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("/Users/edleijnse/OneDrive/Finanzen/Lizensen/Microsoft/keys/subscriptionKey1")));
        String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("/home/edleijnse/keys/Microsoft/subscriptionKey1")));



        testee.setSubscriptionKey(mySubscriptionKey);

        try {
            testee.createCSVFile(startDir,
                    writeCSV, true );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void copyFilesRun(String dirTitleMap, String startDir, String copyDir) throws IOException {
        ExtractPictureMetaData testee = new ExtractPictureMetaData(startDir,copyDir, true );
        String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("C:\\Users\\edlei\\OneDrive\\Finanzen\\Lizensen\\Microsoft\\keys\\subscriptionKey1")));
        // String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("/Users/edleijnse/OneDrive/Finanzen/Lizensen/Microsoft/keys/subscriptionKey1")));
        // String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("/home/edleijnse/keys/Microsoft/subscriptionKey1")));

        testee.setSubscriptionKey(mySubscriptionKey);
        testee.handleDirectoryBuildTitleMap(dirTitleMap);
        testee.handleDirectoryCopyFile(startDir,copyDir);

    }
    public void buildTitleMap(String startDir ) throws IOException {
        ExtractPictureMetaData testee = new ExtractPictureMetaData(startDir,"", true );
        String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("C:\\Users\\edlei\\OneDrive\\Finanzen\\Lizensen\\Microsoft\\keys\\subscriptionKey1")));
        // String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("/Users/edleijnse/OneDrive/Finanzen/Lizensen/Microsoft/keys/subscriptionKey1")));
        // String mySubscriptionKey = new String(Files.readAllBytes(Paths.get("/home/edleijnse/keys/Microsoft/subscriptionKey1")));

        testee.setSubscriptionKey(mySubscriptionKey);

        testee.handleDirectoryBuildTitleMap(startDir);

    }

}
