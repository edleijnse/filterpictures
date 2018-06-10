package filterpictures;


import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ExtractPictureMetaData {
    String startsWithDirectory;
    String csvFile;

    public ExtractPictureMetaData(String startsWithDirectory, String csvFile){
        this.startsWithDirectory = startsWithDirectory;
        this.csvFile = csvFile;
    }

    public PictureMetaData getPictureMetaData(File file) throws IOException {
        PictureMetaData myPictureMetadata = new PictureMetaData();
        myPictureMetadata.setPictureName(Optional.of(file.getName()));
        myPictureMetadata.setAbsolutePath(Optional.of(file.getAbsolutePath()));
        myPictureMetadata.setCanonicalPath(Optional.of(file.getCanonicalPath()));
        try {

            Metadata metadata = ImageMetadataReader.readMetadata(file);
            if (metadata != null) {
                metadata.getDirectories().forEach(directory -> {
                    directory.getTags().forEach(tag -> {
                        if (tag.getTagName() == "Make") {
                            myPictureMetadata.setMake(Optional.of(tag.getDescription()));
                        } else if (tag.getTagName() == "Model") {
                            myPictureMetadata.setModel(Optional.of(tag.getDescription()));
                        } else if (tag.getTagName() == "Lens Model") {
                            myPictureMetadata.setLenseModel(Optional.of(tag.getDescription()));
                        } else if (tag.getTagName() == "Lens Specification") {
                            myPictureMetadata.setLenseDescription(Optional.of(tag.getDescription()));
                        } else if (tag.getTagName() == "Date/Time") {
                            myPictureMetadata.setDateTime(Optional.of(tag.getDescription()));
                        } else if (tag.getTagName() == "Image Height") {
                            myPictureMetadata.setHeight(Optional.of(tag.getDescription()));
                        } else if (tag.getTagName() == "Image Width") {
                            myPictureMetadata.setWidth(Optional.of(tag.getDescription()));
                        } else if (tag.getTagName() == "ISO Speed Ratings") {
                            myPictureMetadata.setIso(Optional.of(tag.getDescription()));
                        } else if (tag.getTagName() == "Shutter Speed Value") {
                            myPictureMetadata.setExposure(Optional.of(tag.getDescription()));
                        } else if (tag.getTagName() == "Aperture Value") {
                            myPictureMetadata.setAperture(Optional.of(tag.getDescription()));
                        } else if (tag.getTagName() == "Exposure Bias Value") {
                            myPictureMetadata.setExposureBias((Optional.of(tag.getDescription())));
                        } else if (tag.getTagName() == "Focal Length") {
                            myPictureMetadata.setFocalLength(Optional.of(tag.getDescription()));
                        }
                    });
                });
            }
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        }
        return myPictureMetadata;
    }
    void appendkomma(FileWriter fileWriter) throws IOException {
        fileWriter.append(",");
    }
    void appendnewline(FileWriter fileWriter) throws IOException {
        fileWriter.append("\n");
    }
    void handleDirectory(FileWriter fileWriter, String startsWithDirectory)  {
        try {
            Files.list(Paths.get(startsWithDirectory)).forEach(item -> {
                File file = item.toFile();
                if (file.isFile()){
                    if ((file.getAbsolutePath().toLowerCase().endsWith(".cr2")) || (file.getAbsolutePath().toLowerCase().endsWith(".cr3")) || (file.getAbsolutePath().toLowerCase().endsWith("jpg"))){
                        try {
                            PictureMetaData myMetadata = getPictureMetaData(file);
                            if (myMetadata.getPictureName().isPresent()){
                                fileWriter.append(myMetadata.getPictureName().get());
                            }
                            appendkomma(fileWriter);
                            if (myMetadata.getDateTime().isPresent()) {
                                fileWriter.append(myMetadata.getDateTime().get());
                            }
                            appendkomma(fileWriter);
                            if (myMetadata.getAperture().isPresent()) {
                                fileWriter.append(myMetadata.getAperture().get());
                            }
                            appendkomma(fileWriter);
                            if (myMetadata.getExposure().isPresent()) {
                                fileWriter.append(myMetadata.getExposure().get());
                            }
                            appendkomma(fileWriter);
                            if (myMetadata.getMake().isPresent()) {
                                fileWriter.append(myMetadata.getMake().get());
                            }
                            appendkomma(fileWriter);
                            if (myMetadata.getModel().isPresent()) {
                                fileWriter.append(myMetadata.getModel().get());
                            }
                            appendkomma(fileWriter);
                            if (myMetadata.getLenseModel().isPresent()) {
                                fileWriter.append(myMetadata.getLenseModel().get());
                            }
                            appendkomma(fileWriter);
                            if (myMetadata.getLenseDescription().isPresent()) {
                                fileWriter.append(myMetadata.getLenseDescription().get());
                            }
                            appendnewline(fileWriter);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                } else {
                    handleDirectory(fileWriter, file.getAbsolutePath());
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void handleDirectoryCompletableFuture(FileWriter fileWriter, CompletableFuture<String> startsWithDirectory)  {
        try {
            Files.list(Paths.get(startsWithDirectory.get())).forEach(item -> {
                File file = item.toFile();
                if (file.isFile()){
                    if ((file.getAbsolutePath().toLowerCase().endsWith(".cr2")) || (file.getAbsolutePath().toLowerCase().endsWith(".cr3")) || (file.getAbsolutePath().toLowerCase().endsWith("jpg"))){
                        try {
                            PictureMetaData myMetadata = getPictureMetaData(file);
                            if (myMetadata.getPictureName().isPresent()){
                                fileWriter.append(myMetadata.getPictureName().get());
                            }
                            appendkomma(fileWriter);
                            if (myMetadata.getDateTime().isPresent()) {
                                fileWriter.append(myMetadata.getDateTime().get());
                            }
                            appendkomma(fileWriter);
                            if (myMetadata.getAperture().isPresent()) {
                                fileWriter.append(myMetadata.getAperture().get());
                            }
                            appendkomma(fileWriter);
                            if (myMetadata.getExposure().isPresent()) {
                                fileWriter.append(myMetadata.getExposure().get());
                            }
                            appendkomma(fileWriter);
                            if (myMetadata.getMake().isPresent()) {
                                fileWriter.append(myMetadata.getMake().get());
                            }
                            appendkomma(fileWriter);
                            if (myMetadata.getModel().isPresent()) {
                                fileWriter.append(myMetadata.getModel().get());
                            }
                            appendkomma(fileWriter);
                            if (myMetadata.getLenseModel().isPresent()) {
                                fileWriter.append(myMetadata.getLenseModel().get());
                            }
                            appendkomma(fileWriter);
                            if (myMetadata.getLenseDescription().isPresent()) {
                                fileWriter.append(myMetadata.getLenseDescription().get());
                            }
                            appendnewline(fileWriter);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                } else {
                    handleDirectory(fileWriter, file.getAbsolutePath());
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void createCSVFile(String startsWithDirectory, String csvFile) throws IOException {
        final long timeStart = System.currentTimeMillis();
        FileWriter fileWriter = new FileWriter(csvFile);
        String CSV_HEADER = "pictureName,dateTime,aperture,exposure,make,model,lenseModel,lenseDescription";
        fileWriter.append(CSV_HEADER);
        appendnewline(fileWriter);


        //for (int ii=1;ii>4;ii++){
            handleDirectory(fileWriter, startsWithDirectory);
        //}

        fileWriter.flush();
        fileWriter.close();
        final long timeEnd = System.currentTimeMillis();
        System.out.println("Duration: " + (timeEnd - timeStart) + " millisec.");
    }
    public void createCSVFileCompletableFuture(String startsWithDirectory, String csvFile) throws IOException {
        // See https://www.callicoder.com/java-8-completablefuture-tutorial/
        final long timeStart = System.currentTimeMillis();
        FileWriter fileWriter = new FileWriter(csvFile);
        String CSV_HEADER = "pictureName,dateTime,aperture,exposure,make,model,lenseModel,lenseDescription";
        fileWriter.append(CSV_HEADER);
        appendnewline(fileWriter);

        CompletableFuture<String> startsWithDirectoryCompletable = CompletableFuture.supplyAsync(()->{
            return startsWithDirectory;
        });
        // for (int ii=1;ii>4;ii++){
            handleDirectoryCompletableFuture(fileWriter, startsWithDirectoryCompletable);
        //}

        fileWriter.flush();
        fileWriter.close();
        final long timeEnd = System.currentTimeMillis();
        System.out.println("Duration: " + (timeEnd - timeStart) + " millisec.");
    }

}
