package filterpictures;


import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.thebuzzmedia.exiftool.ExifTool;
import com.thebuzzmedia.exiftool.ExifToolBuilder;
import com.thebuzzmedia.exiftool.Tag;
import com.thebuzzmedia.exiftool.core.StandardTag;
import com.thebuzzmedia.exiftool.exceptions.UnsupportedFeatureException;
import com.thebuzzmedia.exiftool.logs.Logger;
import com.thebuzzmedia.exiftool.logs.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.Arrays.asList;

public class ExtractPictureMetaData {
    // 20180715 ExifTool Wrapper build in, supports CR3 Format
    // https://github.com/mjeanroy/exiftool/blob/master/README.md

    String startsWithDirectory;
    String csvFile;
    private static final Logger log = LoggerFactory.getLogger(ExtractPictureMetaData.class);

    private static ExifTool exifTool;

    static {
        try {
            exifTool = new ExifToolBuilder().withPoolSize(10).enableStayOpen().build();
        } catch (UnsupportedFeatureException ex) {
            // Fallback to simple exiftool instance.
            exifTool = new ExifToolBuilder().build();
        }
    }

    public ExtractPictureMetaData(String startsWithDirectory, String csvFile) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myPictureMetadata;
    }

    public static Map<Tag, String> parseFromExif(File image) throws Exception {
        // ExifTool path must be defined as a system property (`exiftool.path`),
        // but path can be set using `withPath` method.
        return exifTool.getImageMeta(image, asList(
                StandardTag.MAKE,
                StandardTag.MODEL,
                StandardTag.LENS_MODEL,
                StandardTag.LENS_ID,
                StandardTag.CREATE_DATE,
                StandardTag.IMAGE_HEIGHT,
                StandardTag.IMAGE_WIDTH,
                StandardTag.ISO,
                StandardTag.SHUTTER_SPEED,
                StandardTag.APERTURE,
                StandardTag.EXPOSURE_COMPENSATION,
                StandardTag.FOCAL_LENGTH
        ));

    }


    public PictureMetaData getPictureMetaDataExif(File file) throws IOException {
        // https://github.com/mjeanroy/exiftool


        PictureMetaData myPictureMetadata = new PictureMetaData();
        myPictureMetadata.setPictureName(Optional.of(file.getName()));
        myPictureMetadata.setAbsolutePath(Optional.of(file.getAbsolutePath()));
        myPictureMetadata.setCanonicalPath(Optional.of(file.getCanonicalPath()));
        // ExifTool path must be defined as a system property (`exiftool.path`),
        // but path can be set using `withPath` method.
        try {
            Map<Tag, String> myTags = parseFromExif(file);
            if (myTags != null) {
                myTags.forEach((tag, s) -> {
                    // System.out.println((tag.getName() + ": " + s));
                    if (tag.getName() == StandardTag.MAKE.getName()) {
                        myPictureMetadata.setMake(Optional.of(s));
                    } else if (tag.getName() == StandardTag.MODEL.getName()) {
                        myPictureMetadata.setModel(Optional.of(s));
                    } else if (tag.getName() == StandardTag.LENS_MODEL.getName()) {
                        myPictureMetadata.setLenseModel(Optional.of(s));
                    } else if (tag.getName() == StandardTag.LENS_ID.getName()) {
                        myPictureMetadata.setLenseDescription(Optional.of(s));
                    } else if (tag.getName() == StandardTag.CREATE_DATE.getName()) {
                        myPictureMetadata.setDateTime(Optional.of(s));
                    } else if (tag.getName() == StandardTag.IMAGE_HEIGHT.getName()) {
                        myPictureMetadata.setHeight(Optional.of(s));
                    } else if (tag.getName() == StandardTag.IMAGE_WIDTH.getName()) {
                        myPictureMetadata.setWidth(Optional.of(s));
                    } else if (tag.getName() == StandardTag.ISO.getName()) {
                        myPictureMetadata.setIso(Optional.of(s));
                    } else if (tag.getName() == StandardTag.SHUTTER_SPEED.getName()) {
                        myPictureMetadata.setExposure(Optional.of(s));
                    } else if (tag.getName() == StandardTag.APERTURE.getName()) {
                        myPictureMetadata.setAperture(Optional.of(s));
                    } else if (tag.getName() == StandardTag.EXPOSURE_COMPENSATION.getName()) {
                        myPictureMetadata.setExposureBias((Optional.of(s)));
                    } else if (tag.getName() == StandardTag.FOCAL_LENGTH.getName()) {
                        myPictureMetadata.setFocalLength(Optional.of(s));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return myPictureMetadata;
    }

    void appendkomma(FileWriter fileWriter) throws IOException {
        fileWriter.append(",");
    }

    void appendkomma(StringBuilder fileWriter) throws IOException {
        fileWriter.append(",");
    }

    void appendnewline(FileWriter fileWriter) throws IOException {
        fileWriter.append("\n");
    }

    void appendnewline(StringBuilder fileWriter) throws IOException {
        fileWriter.append("\n");
    }

    void handleDirectory(FileWriter fileWriter, String startsWithDirectory) {
        try {
            Files.list(Paths.get(startsWithDirectory)).forEach(item -> {
                File file = item.toFile();
                if (file.isFile()) {
                    if ((file.getAbsolutePath().toLowerCase().endsWith(".cr2")) || (file.getAbsolutePath().toLowerCase().endsWith(".cr3")) || (file.getAbsolutePath().toLowerCase().endsWith("jpg"))) {
                        try {
                            PictureMetaData myMetadata = getPictureMetaData(file);
                            if (myMetadata.getPictureName().isPresent()) {
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

    void handleDirectoryWalker(FileWriter fileWriter, String startsWithDirectory) {
        try {

            Files.walk(Paths.get(startsWithDirectory))
                    .filter(p -> {
                        return ((p.toString().toLowerCase().endsWith(".cr2")) || (p.toString().toLowerCase().endsWith(".cr3")) || (p.toString().toLowerCase().endsWith(".jpg")));
                    })
                    .forEach(item -> {
                        File file = item.toFile();
                        if (file.isFile()) {
                            try {
                                PictureMetaData myMetadata = getPictureMetaDataExif(file);
                                if (myMetadata.getPictureName().isPresent()) {
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
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public StringBuilder handleDirectoryCompletableFutureStringBuilder(String startsWithDirectory) {
        // http://www.angelikalanger.com/Articles/EffectiveJava/79.Java8.CompletableFuture/79.Java8.CompletableFuture.html
        StringBuilder fileWriter = new StringBuilder();
        List<String> myDirectories = new ArrayList<String>();
        try {


            Files.list(Paths.get(startsWithDirectory)).forEach(item -> {
                File file = item.toFile();
                if (file.isFile()) {
                    if ((file.getAbsolutePath().toLowerCase().endsWith(".cr2")) || (file.getAbsolutePath().toLowerCase().endsWith(".cr3")) || (file.getAbsolutePath().toLowerCase().endsWith("jpg"))) {
                        try {
                            PictureMetaData myMetadata = getPictureMetaData(file);
                            if (myMetadata.getPictureName().isPresent()) {
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
                    // TODO https://stackoverflow.com/questions/19348248/waiting-on-a-list-of-future
                    myDirectories.add(file.getAbsolutePath());

                }

            });
            List<CompletableFuture<StringBuilder>> stringBuilders = new ArrayList<>();
            myDirectories.forEach(myDirectory -> {
                try {
                    // push und pull kombiniert http://www.angelikalanger.com/Articles/EffectiveJava/79.Java8.CompletableFuture/79.Java8.CompletableFuture.html
                    stringBuilders.add(CompletableFuture.supplyAsync(() -> handleDirectoryCompletableFutureStringBuilder(myDirectory)));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            stringBuilders.forEach(cf -> {

                try {
                    cf.thenAccept(f -> {
                        fileWriter.append(f);
                    }).get();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return fileWriter;
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

    public void createCSVFileWalker(String startsWithDirectory, String csvFile) throws IOException {
        final long timeStart = System.currentTimeMillis();
        FileWriter fileWriter = new FileWriter(csvFile);
        String CSV_HEADER = "pictureName,dateTime,aperture,exposure,make,model,lenseModel,lenseDescription";
        fileWriter.append(CSV_HEADER);
        appendnewline(fileWriter);


        //for (int ii=1;ii>4;ii++){
        handleDirectoryWalker(fileWriter, startsWithDirectory);
        //}

        fileWriter.flush();
        fileWriter.close();
        final long timeEnd = System.currentTimeMillis();
        System.out.println("Duration: " + (timeEnd - timeStart) + " millisec.");
    }


    public void createCSVFileCompletableFutureStringBuilder(String startsWithDirectory, String csvFile) throws IOException, ExecutionException, InterruptedException {
        // See https://www.callicoder.com/java-8-completablefuture-tutorial/
        final long timeStart = System.currentTimeMillis();
        FileWriter fileWriter = new FileWriter(csvFile);
        String CSV_HEADER = "pictureName,dateTime,aperture,exposure,make,model,lenseModel,lenseDescription";
        fileWriter.append(CSV_HEADER);
        appendnewline(fileWriter);


        // for (int ii=1;ii>4;ii++){
        StringBuilder fileStringBuilder = new StringBuilder();
        fileStringBuilder = handleDirectoryCompletableFutureStringBuilder(startsWithDirectory);
        fileWriter.append(fileStringBuilder);
        //}

        fileWriter.flush();
        fileWriter.close();
        final long timeEnd = System.currentTimeMillis();
        System.out.println("Duration: " + (timeEnd - timeStart) + " millisec.");
    }


}
