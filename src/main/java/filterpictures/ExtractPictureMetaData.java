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
                StandardTag.ISO,
                StandardTag.APERTURE,
                StandardTag.WHITE_BALANCE,
                StandardTag.BRIGHTNESS,
                StandardTag.CONTRAST,
                StandardTag.SATURATION,
                StandardTag.SHARPNESS,
                StandardTag.SHUTTER_SPEED,
                StandardTag.DIGITAL_ZOOM_RATIO,
                StandardTag.IMAGE_WIDTH,
                StandardTag.IMAGE_HEIGHT,
                StandardTag.X_RESOLUTION,
                StandardTag.Y_RESOLUTION,
                StandardTag.FLASH,
                StandardTag.METERING_MODE,
                StandardTag.FOCAL_LENGTH,
                StandardTag.FOCAL_LENGTH_35MM,
                StandardTag.EXPOSURE_TIME,
                StandardTag.EXPOSURE_COMPENSATION,
                StandardTag.EXPOSURE_PROGRAM,
                StandardTag.ORIENTATION,
                StandardTag.COLOR_SPACE,
                StandardTag.SENSING_METHOD,
                StandardTag.SOFTWARE,
                StandardTag.MAKE,
                StandardTag.MODEL,
                StandardTag.LENS_MAKE,
                StandardTag.LENS_MODEL,
                StandardTag.OWNER_NAME,
                StandardTag.TITLE,
                StandardTag.AUTHOR,
                StandardTag.SUBJECT,
                StandardTag.KEYWORDS,
                StandardTag.COMMENT,
                StandardTag.RATING,
                StandardTag.RATING_PERCENT,
                StandardTag.DATE_TIME_ORIGINAL,
                StandardTag.GPS_LATITUDE,
                StandardTag.GPS_LATITUDE_REF,
                StandardTag.GPS_LONGITUDE,
                StandardTag.GPS_LONGITUDE_REF,
                StandardTag.GPS_ALTITUDE,
                StandardTag.GPS_ALTITUDE_REF,
                StandardTag.GPS_SPEED,
                StandardTag.GPS_SPEED_REF,
                StandardTag.GPS_PROCESS_METHOD,
                StandardTag.GPS_BEARING,
                StandardTag.GPS_BEARING_REF,
                StandardTag.GPS_TIMESTAMP,
                StandardTag.ROTATION,
                StandardTag.EXIF_VERSION,
                StandardTag.LENS_ID,
                StandardTag.COPYRIGHT,
                StandardTag.ARTIST,
                StandardTag.SUB_SEC_TIME_ORIGINAL,
                StandardTag.OBJECT_NAME,
                StandardTag.CAPTION_ABSTRACT,
                StandardTag.CREATOR,
                StandardTag.IPTC_KEYWORDS,
                StandardTag.COPYRIGHT_NOTICE,
                StandardTag.FILE_TYPE,
                StandardTag.FILE_SIZE,
                StandardTag.AVG_BITRATE,
                StandardTag.AVG_BITRATE,
                StandardTag.CREATE_DATE

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
                        myPictureMetadata.setMAKE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.MODEL.getName()) {
                        myPictureMetadata.setModel(Optional.of(s));
                        myPictureMetadata.setMODEL(Optional.of(s));
                    } else if (tag.getName() == StandardTag.LENS_MODEL.getName()) {
                        myPictureMetadata.setLenseDescription(Optional.of(s));
                        myPictureMetadata.setLENS_MODEL(Optional.of(s));
                    } else if (tag.getName() == StandardTag.LENS_ID.getName()) {
                        myPictureMetadata.setLENS_ID(Optional.of(s));
                    } else if (tag.getName() == StandardTag.CREATE_DATE.getName()) {
                        myPictureMetadata.setDateTime(Optional.of(s));
                        myPictureMetadata.setCREATE_DATE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.IMAGE_HEIGHT.getName()) {
                        myPictureMetadata.setHeight(Optional.of(s));
                        myPictureMetadata.setIMAGE_HEIGHT(Optional.of(s));
                    } else if (tag.getName() == StandardTag.IMAGE_WIDTH.getName()) {
                        myPictureMetadata.setWidth(Optional.of(s));
                        myPictureMetadata.setIMAGE_WIDTH(Optional.of(s));
                    } else if (tag.getName() == StandardTag.ISO.getName()) {
                        myPictureMetadata.setIso(Optional.of(s));
                        myPictureMetadata.setISO(Optional.of(s));
                    } else if (tag.getName() == StandardTag.SHUTTER_SPEED.getName()) {
                        myPictureMetadata.setExposure(Optional.of(s));
                        myPictureMetadata.setSHUTTER_SPEED(Optional.of(s));
                    } else if (tag.getName() == StandardTag.APERTURE.getName()) {
                        myPictureMetadata.setAperture(Optional.of(s));
                        myPictureMetadata.setAPERTURE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.EXPOSURE_COMPENSATION.getName()) {
                        myPictureMetadata.setExposureBias((Optional.of(s)));
                        myPictureMetadata.setEXPOSURE_COMPENSATION((Optional.of(s)));
                    } else if (tag.getName() == StandardTag.FOCAL_LENGTH.getName()) {
                        myPictureMetadata.setFocalLength(Optional.of(s));
                        myPictureMetadata.setFOCAL_LENGTH(Optional.of(s));
                    } else if (tag.getName() == StandardTag.ISO.getName()) {
                        myPictureMetadata.setISO(Optional.of(s));
                    } else if (tag.getName() == StandardTag.APERTURE.getName()) {
                        myPictureMetadata.setAPERTURE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.WHITE_BALANCE.getName()) {
                        myPictureMetadata.setWHITE_BALANCE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.BRIGHTNESS.getName()) {
                        myPictureMetadata.setBRIGHTNESS(Optional.of(s));
                    } else if (tag.getName() == StandardTag.CONTRAST.getName()) {
                        myPictureMetadata.setCONTRAST(Optional.of(s));
                    } else if (tag.getName() == StandardTag.SATURATION.getName()) {
                        myPictureMetadata.setSATURATION(Optional.of(s));
                    } else if (tag.getName() == StandardTag.SHARPNESS.getName()) {
                        myPictureMetadata.setSHARPNESS(Optional.of(s));
                    } else if (tag.getName() == StandardTag.SHUTTER_SPEED.getName()) {
                        myPictureMetadata.setSHUTTER_SPEED(Optional.of(s));
                    } else if (tag.getName() == StandardTag.DIGITAL_ZOOM_RATIO.getName()) {
                        myPictureMetadata.setDIGITAL_ZOOM_RATIO(Optional.of(s));
                    } else if (tag.getName() == StandardTag.IMAGE_WIDTH.getName()) {
                        myPictureMetadata.setIMAGE_WIDTH(Optional.of(s));
                    } else if (tag.getName() == StandardTag.IMAGE_HEIGHT.getName()) {
                        myPictureMetadata.setIMAGE_HEIGHT(Optional.of(s));
                    } else if (tag.getName() == StandardTag.X_RESOLUTION.getName()) {
                        myPictureMetadata.setX_RESOLUTION(Optional.of(s));
                    } else if (tag.getName() == StandardTag.Y_RESOLUTION.getName()) {
                        myPictureMetadata.setY_RESOLUTION(Optional.of(s));
                    } else if (tag.getName() == StandardTag.FLASH.getName()) {
                        myPictureMetadata.setFLASH(Optional.of(s));
                    } else if (tag.getName() == StandardTag.METERING_MODE.getName()) {
                        myPictureMetadata.setMETERING_MODE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.FOCAL_LENGTH.getName()) {
                        myPictureMetadata.setFOCAL_LENGTH(Optional.of(s));
                    } else if (tag.getName() == StandardTag.FOCAL_LENGTH_35MM.getName()) {
                        myPictureMetadata.setFOCAL_LENGTH_35MM(Optional.of(s));
                    } else if (tag.getName() == StandardTag.EXPOSURE_TIME.getName()) {
                        myPictureMetadata.setEXPOSURE_TIME(Optional.of(s));
                    } else if (tag.getName() == StandardTag.EXPOSURE_COMPENSATION.getName()) {
                        myPictureMetadata.setEXPOSURE_COMPENSATION(Optional.of(s));
                    } else if (tag.getName() == StandardTag.EXPOSURE_PROGRAM.getName()) {
                        myPictureMetadata.setEXPOSURE_PROGRAM(Optional.of(s));
                    } else if (tag.getName() == StandardTag.ORIENTATION.getName()) {
                        myPictureMetadata.setORIENTATION(Optional.of(s));
                    } else if (tag.getName() == StandardTag.COLOR_SPACE.getName()) {
                        myPictureMetadata.setCOLOR_SPACE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.SENSING_METHOD.getName()) {
                        myPictureMetadata.setSENSING_METHOD(Optional.of(s));
                    } else if (tag.getName() == StandardTag.SOFTWARE.getName()) {
                        myPictureMetadata.setSOFTWARE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.MAKE.getName()) {
                        myPictureMetadata.setMAKE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.MODEL.getName()) {
                        myPictureMetadata.setMODEL(Optional.of(s));
                    } else if (tag.getName() == StandardTag.LENS_MAKE.getName()) {
                        myPictureMetadata.setLENS_MAKE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.LENS_MODEL.getName()) {
                        myPictureMetadata.setLENS_MODEL(Optional.of(s));
                    } else if (tag.getName() == StandardTag.OWNER_NAME.getName()) {
                        myPictureMetadata.setOWNER_NAME(Optional.of(s));
                    } else if (tag.getName() == StandardTag.TITLE.getName()) {
                        myPictureMetadata.setTITLE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.AUTHOR.getName()) {
                        myPictureMetadata.setAUTHOR(Optional.of(s));
                    } else if (tag.getName() == StandardTag.SUBJECT.getName()) {
                        myPictureMetadata.setSUBJECT(Optional.of(s));
                    } else if (tag.getName() == StandardTag.KEYWORDS.getName()) {
                        myPictureMetadata.setKEYWORDS(Optional.of(s));
                    } else if (tag.getName() == StandardTag.COMMENT.getName()) {
                        myPictureMetadata.setCOMMENT(Optional.of(s));
                    } else if (tag.getName() == StandardTag.RATING.getName()) {
                        myPictureMetadata.setRATING(Optional.of(s));
                    } else if (tag.getName() == StandardTag.RATING_PERCENT.getName()) {
                        myPictureMetadata.setRATING_PERCENT(Optional.of(s));
                    } else if (tag.getName() == StandardTag.DATE_TIME_ORIGINAL.getName()) {
                        myPictureMetadata.setDATE_TIME_ORIGINAL(Optional.of(s));
                    } else if (tag.getName() == StandardTag.GPS_LATITUDE.getName()) {
                        myPictureMetadata.setGPS_LATITUDE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.GPS_LATITUDE_REF.getName()) {
                        myPictureMetadata.setGPS_LATITUDE_REF(Optional.of(s));
                    } else if (tag.getName() == StandardTag.GPS_LONGITUDE.getName()) {
                        myPictureMetadata.setGPS_LONGITUDE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.GPS_LONGITUDE_REF.getName()) {
                        myPictureMetadata.setGPS_LONGITUDE_REF(Optional.of(s));
                    } else if (tag.getName() == StandardTag.GPS_ALTITUDE.getName()) {
                        myPictureMetadata.setGPS_ALTITUDE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.GPS_ALTITUDE_REF.getName()) {
                        myPictureMetadata.setGPS_ALTITUDE_REF(Optional.of(s));
                    } else if (tag.getName() == StandardTag.GPS_SPEED.getName()) {
                        myPictureMetadata.setGPS_SPEED(Optional.of(s));
                    } else if (tag.getName() == StandardTag.GPS_SPEED_REF.getName()) {
                        myPictureMetadata.setGPS_SPEED_REF(Optional.of(s));
                    } else if (tag.getName() == StandardTag.GPS_PROCESS_METHOD.getName()) {
                        myPictureMetadata.setGPS_PROCESS_METHOD(Optional.of(s));
                    } else if (tag.getName() == StandardTag.GPS_BEARING.getName()) {
                        myPictureMetadata.setGPS_BEARING(Optional.of(s));
                    } else if (tag.getName() == StandardTag.GPS_BEARING_REF.getName()) {
                        myPictureMetadata.setGPS_BEARING_REF(Optional.of(s));
                    } else if (tag.getName() == StandardTag.GPS_TIMESTAMP.getName()) {
                        myPictureMetadata.setGPS_TIMESTAMP(Optional.of(s));
                    } else if (tag.getName() == StandardTag.ROTATION.getName()) {
                        myPictureMetadata.setROTATION(Optional.of(s));
                    } else if (tag.getName() == StandardTag.EXIF_VERSION.getName()) {
                        myPictureMetadata.setEXIF_VERSION(Optional.of(s));
                    } else if (tag.getName() == StandardTag.LENS_ID.getName()) {
                        myPictureMetadata.setLENS_ID(Optional.of(s));
                    } else if (tag.getName() == StandardTag.COPYRIGHT.getName()) {
                        myPictureMetadata.setCOPYRIGHT(Optional.of(s));
                    } else if (tag.getName() == StandardTag.ARTIST.getName()) {
                        myPictureMetadata.setARTIST(Optional.of(s));
                    } else if (tag.getName() == StandardTag.SUB_SEC_TIME_ORIGINAL.getName()) {
                        myPictureMetadata.setSUB_SEC_TIME_ORIGINAL(Optional.of(s));
                    } else if (tag.getName() == StandardTag.OBJECT_NAME.getName()) {
                        myPictureMetadata.setOBJECT_NAME(Optional.of(s));
                    } else if (tag.getName() == StandardTag.CAPTION_ABSTRACT.getName()) {
                        myPictureMetadata.setCAPTION_ABSTRACT(Optional.of(s));
                    } else if (tag.getName() == StandardTag.CREATOR.getName()) {
                        myPictureMetadata.setCREATOR(Optional.of(s));
                    } else if (tag.getName() == StandardTag.IPTC_KEYWORDS.getName()) {
                        myPictureMetadata.setIPTC_KEYWORDS(Optional.of(s));
                    } else if (tag.getName() == StandardTag.COPYRIGHT_NOTICE.getName()) {
                        myPictureMetadata.setCOPYRIGHT_NOTICE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.FILE_TYPE.getName()) {
                        myPictureMetadata.setFILE_TYPE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.FILE_SIZE.getName()) {
                        myPictureMetadata.setFILE_SIZE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.AVG_BITRATE.getName()) {
                        myPictureMetadata.setAVG_BITRATE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.MIME_TYPE.getName()) {
                        myPictureMetadata.setMIME_TYPE(Optional.of(s));
                    } else if (tag.getName() == StandardTag.CREATE_DATE.getName()) {
                        myPictureMetadata.setCREATE_DATE(Optional.of(s));
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
                                appendkomma(fileWriter);
                                if (myMetadata.getISO().isPresent()) {
                                    fileWriter.append(myMetadata.getISO().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getAPERTURE().isPresent()) {
                                    fileWriter.append(myMetadata.getAPERTURE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getWHITE_BALANCE().isPresent()) {
                                    fileWriter.append(myMetadata.getWHITE_BALANCE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getBRIGHTNESS().isPresent()) {
                                    fileWriter.append(myMetadata.getBRIGHTNESS().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getCONTRAST().isPresent()) {
                                    fileWriter.append(myMetadata.getCONTRAST().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getSATURATION().isPresent()) {
                                    fileWriter.append(myMetadata.getSATURATION().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getSHARPNESS().isPresent()) {
                                    fileWriter.append(myMetadata.getSHARPNESS().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getSHUTTER_SPEED().isPresent()) {
                                    fileWriter.append(myMetadata.getSHUTTER_SPEED().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getDIGITAL_ZOOM_RATIO().isPresent()) {
                                    fileWriter.append(myMetadata.getDIGITAL_ZOOM_RATIO().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getIMAGE_WIDTH().isPresent()) {
                                    fileWriter.append(myMetadata.getIMAGE_WIDTH().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getIMAGE_HEIGHT().isPresent()) {
                                    fileWriter.append(myMetadata.getIMAGE_HEIGHT().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getX_RESOLUTION().isPresent()) {
                                    fileWriter.append(myMetadata.getX_RESOLUTION().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getY_RESOLUTION().isPresent()) {
                                    fileWriter.append(myMetadata.getY_RESOLUTION().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getFLASH().isPresent()) {
                                    fileWriter.append(myMetadata.getFLASH().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getMETERING_MODE().isPresent()) {
                                    fileWriter.append(myMetadata.getMETERING_MODE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getFOCAL_LENGTH().isPresent()) {
                                    fileWriter.append(myMetadata.getFOCAL_LENGTH().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getFOCAL_LENGTH_35MM().isPresent()) {
                                    fileWriter.append(myMetadata.getFOCAL_LENGTH_35MM().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getEXPOSURE_TIME().isPresent()) {
                                    fileWriter.append(myMetadata.getEXPOSURE_TIME().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getEXPOSURE_COMPENSATION().isPresent()) {
                                    fileWriter.append(myMetadata.getEXPOSURE_COMPENSATION().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getEXPOSURE_PROGRAM().isPresent()) {
                                    fileWriter.append(myMetadata.getEXPOSURE_PROGRAM().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getORIENTATION().isPresent()) {
                                    fileWriter.append(myMetadata.getORIENTATION().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getCOLOR_SPACE().isPresent()) {
                                    fileWriter.append(myMetadata.getCOLOR_SPACE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getSENSING_METHOD().isPresent()) {
                                    fileWriter.append(myMetadata.getSENSING_METHOD().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getSOFTWARE().isPresent()) {
                                    fileWriter.append(myMetadata.getSOFTWARE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getMAKE().isPresent()) {
                                    fileWriter.append(myMetadata.getMAKE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getMODEL().isPresent()) {
                                    fileWriter.append(myMetadata.getMODEL().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getLENS_MAKE().isPresent()) {
                                    fileWriter.append(myMetadata.getLENS_MAKE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getLENS_MODEL().isPresent()) {
                                    fileWriter.append(myMetadata.getLENS_MODEL().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getOWNER_NAME().isPresent()) {
                                    fileWriter.append(myMetadata.getOWNER_NAME().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getTITLE().isPresent()) {
                                    fileWriter.append(myMetadata.getTITLE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getAUTHOR().isPresent()) {
                                    fileWriter.append(myMetadata.getAUTHOR().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getSUBJECT().isPresent()) {
                                    fileWriter.append(myMetadata.getSUBJECT().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getKEYWORDS().isPresent()) {
                                    fileWriter.append(myMetadata.getKEYWORDS().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getCOMMENT().isPresent()) {
                                    fileWriter.append(myMetadata.getCOMMENT().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getRATING().isPresent()) {
                                    fileWriter.append(myMetadata.getRATING().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getRATING_PERCENT().isPresent()) {
                                    fileWriter.append(myMetadata.getRATING_PERCENT().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getDATE_TIME_ORIGINAL().isPresent()) {
                                    fileWriter.append(myMetadata.getDATE_TIME_ORIGINAL().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getGPS_LATITUDE().isPresent()) {
                                    fileWriter.append(myMetadata.getGPS_LATITUDE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getGPS_LATITUDE_REF().isPresent()) {
                                    fileWriter.append(myMetadata.getGPS_LATITUDE_REF().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getGPS_LONGITUDE().isPresent()) {
                                    fileWriter.append(myMetadata.getGPS_LONGITUDE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getGPS_LONGITUDE_REF().isPresent()) {
                                    fileWriter.append(myMetadata.getGPS_LONGITUDE_REF().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getGPS_ALTITUDE().isPresent()) {
                                    fileWriter.append(myMetadata.getGPS_ALTITUDE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getGPS_ALTITUDE_REF().isPresent()) {
                                    fileWriter.append(myMetadata.getGPS_ALTITUDE_REF().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getGPS_SPEED().isPresent()) {
                                    fileWriter.append(myMetadata.getGPS_SPEED().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getGPS_SPEED_REF().isPresent()) {
                                    fileWriter.append(myMetadata.getGPS_SPEED_REF().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getGPS_PROCESS_METHOD().isPresent()) {
                                    fileWriter.append(myMetadata.getGPS_PROCESS_METHOD().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getGPS_BEARING().isPresent()) {
                                    fileWriter.append(myMetadata.getGPS_BEARING().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getGPS_BEARING_REF().isPresent()) {
                                    fileWriter.append(myMetadata.getGPS_BEARING_REF().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getGPS_TIMESTAMP().isPresent()) {
                                    fileWriter.append(myMetadata.getGPS_TIMESTAMP().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getROTATION().isPresent()) {
                                    fileWriter.append(myMetadata.getROTATION().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getEXIF_VERSION().isPresent()) {
                                    fileWriter.append(myMetadata.getEXIF_VERSION().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getLENS_ID().isPresent()) {
                                    fileWriter.append(myMetadata.getLENS_ID().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getCOPYRIGHT().isPresent()) {
                                    fileWriter.append(myMetadata.getCOPYRIGHT().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getARTIST().isPresent()) {
                                    fileWriter.append(myMetadata.getARTIST().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getSUB_SEC_TIME_ORIGINAL().isPresent()) {
                                    fileWriter.append(myMetadata.getSUB_SEC_TIME_ORIGINAL().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getOBJECT_NAME().isPresent()) {
                                    fileWriter.append(myMetadata.getOBJECT_NAME().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getCAPTION_ABSTRACT().isPresent()) {
                                    fileWriter.append(myMetadata.getCAPTION_ABSTRACT().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getCREATOR().isPresent()) {
                                    fileWriter.append(myMetadata.getCREATOR().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getIPTC_KEYWORDS().isPresent()) {
                                    fileWriter.append(myMetadata.getIPTC_KEYWORDS().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getCOPYRIGHT_NOTICE().isPresent()) {
                                    fileWriter.append(myMetadata.getCOPYRIGHT_NOTICE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getFILE_TYPE().isPresent()) {
                                    fileWriter.append(myMetadata.getFILE_TYPE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getFILE_SIZE().isPresent()) {
                                    fileWriter.append(myMetadata.getFILE_SIZE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getAVG_BITRATE().isPresent()) {
                                    fileWriter.append(myMetadata.getAVG_BITRATE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getMIME_TYPE().isPresent()) {
                                    fileWriter.append(myMetadata.getMIME_TYPE().get());
                                }
                                appendkomma(fileWriter);
                                if (myMetadata.getCREATE_DATE().isPresent()) {
                                    fileWriter.append(myMetadata.getCREATE_DATE().get());
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
        CSV_HEADER += ",ISO,APERTURE,WHITE_BALANCE,BRIGHTNESS,CONTRAST,SATURATION,SHARPNESS,SHUTTER_SPEED";
        CSV_HEADER += ",DIGITAL_ZOOM_RATIO,IMAGE_WIDTH,IMAGE_HEIGHT,X_RESOLUTION,Y_RESOLUTION,FLASH,METERING_MODE,FOCAL_LENGTH";
        CSV_HEADER += ",FOCAL_LENGTH_35MM, EXPOSURE_TIME, EXPOSURE_COMPENSATION, EXPOSURE_PROGRAM, ORIENTATION, COLOR_SPACE";
        CSV_HEADER += ",SENSING_METHOD,SOFTWARE,MAKE,MODEL,LENS_MAKE,LENS_MODEL,OWNER_NAME,TITLE,AUTHOR,SUBJECT,KEYWORDS";
        CSV_HEADER += ",COMMENT,RATING,RATING_PERCENT,DATE_TIME_ORIGINAL,GPS_LATITUDE,GPS_LATITUDE_REF";
        CSV_HEADER += ",GPS_LONGITUDE, GPS_LONGITUDE_REF, GPS_ALTITUDE, GPS_ALTITUDE_REF, GPS_SPEED, GPS_SPEED_REF, GPS_PROCESS_METHOD";
        CSV_HEADER += ",GPS_BEARING, GPS_BEARING_REF, GPS_TIMESTAMP, ROTATION, EXIF_VERSION, LENS_ID";
        CSV_HEADER += ",COPYRIGHT,ARTIST,SUB_SEC_TIME_ORIGINAL, OBJECT_NAME, CAPTION_ABSTRACT,CREATOR,IPTC_KEYWORDS";
        CSV_HEADER += ",COPYRIGHT_NOTICE, FILE_TYPE, FILE_SIZE, AVG_BITRATE, MIME_TYPE, CREATE_DATE";
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
