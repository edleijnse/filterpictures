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
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

import static java.util.Arrays.asList;

public class ExtractPictureMetaData {
    // 20180715 ExifTool Wrapper build in, supports CR3 Format
    // https://github.com/mjeanroy/exiftool/blob/master/README.md

    private String startsWithDirectory;
    private String csvFile;
    private Boolean withVision;
    private static final Logger log = LoggerFactory.getLogger(ExtractPictureMetaData.class);
    private String subscriptionKey = "";
    private Map<String, String> titleMap = new TreeMap<>();


    private static ExifTool exifTool;

    static {
        try {
            exifTool = new ExifToolBuilder().withPoolSize(10).enableStayOpen().build();
        } catch (UnsupportedFeatureException ex) {
            // Fallback to simple exiftool instance.
            exifTool = new ExifToolBuilder().build();
        }
    }

    public String getSubscriptionKey() {
        return subscriptionKey;
    }

    public void setSubscriptionKey(String subscriptionKey) {
        this.subscriptionKey = subscriptionKey;
    }

    public ExtractPictureMetaData(String startsWithDirectory, String csvFile, Boolean withVision) {
        this.startsWithDirectory = startsWithDirectory;
        this.csvFile = csvFile;
        this.withVision = withVision;
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
        // but path can be set using `withPath` method
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


    public static PictureMetaData getPictureMetaDataExif(File file) throws IOException {
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
                    s = s.replaceAll(",", ".");
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

    void appendNotfound(FileWriter fileWriter) throws IOException {
        fileWriter.append("-");
    }

    void appendItem(FileWriter fileWriter, String item) throws IOException {
        if (StringUtils.isNotEmpty(item)) {
            fileWriter.append(item);
        } else {
            appendNotfound(fileWriter);
        }
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


    void processMetaData(PictureMetaData myMetadata, FileWriter fileWriter) throws IOException {
        if (myMetadata.getPictureName().isPresent()) {
            appendItem(fileWriter, myMetadata.getPictureName().get());
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getDateTime().isPresent()) {
            appendItem(fileWriter, (myMetadata.getDateTime().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getAperture().isPresent()) {
            appendItem(fileWriter, (myMetadata.getAperture().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getExposure().isPresent()) {
            appendItem(fileWriter, (myMetadata.getExposure().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getMake().isPresent()) {
            appendItem(fileWriter, (myMetadata.getMake().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getModel().isPresent()) {
            appendItem(fileWriter, (myMetadata.getModel().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getLenseModel().isPresent()) {
            appendItem(fileWriter, (myMetadata.getLenseModel().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getLenseDescription().isPresent()) {
            appendItem(fileWriter, (myMetadata.getLenseDescription().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getISO().isPresent()) {
            appendItem(fileWriter, (myMetadata.getISO().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getAPERTURE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getAPERTURE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getWHITE_BALANCE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getWHITE_BALANCE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getBRIGHTNESS().isPresent()) {
            appendItem(fileWriter, (myMetadata.getBRIGHTNESS().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getCONTRAST().isPresent()) {
            appendItem(fileWriter, (myMetadata.getCONTRAST().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getSATURATION().isPresent()) {
            appendItem(fileWriter, (myMetadata.getSATURATION().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getSHARPNESS().isPresent()) {
            appendItem(fileWriter, (myMetadata.getSHARPNESS().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getSHUTTER_SPEED().isPresent()) {
            appendItem(fileWriter, (myMetadata.getSHUTTER_SPEED().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getDIGITAL_ZOOM_RATIO().isPresent()) {
            appendItem(fileWriter, (myMetadata.getDIGITAL_ZOOM_RATIO().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getIMAGE_WIDTH().isPresent()) {
            appendItem(fileWriter, (myMetadata.getIMAGE_WIDTH().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getIMAGE_HEIGHT().isPresent()) {
            appendItem(fileWriter, (myMetadata.getIMAGE_HEIGHT().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getX_RESOLUTION().isPresent()) {
            appendItem(fileWriter, (myMetadata.getX_RESOLUTION().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getY_RESOLUTION().isPresent()) {
            appendItem(fileWriter, (myMetadata.getY_RESOLUTION().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getFLASH().isPresent()) {
            appendItem(fileWriter, (myMetadata.getFLASH().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getMETERING_MODE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getMETERING_MODE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getFOCAL_LENGTH().isPresent()) {
            appendItem(fileWriter, (myMetadata.getFOCAL_LENGTH().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getFOCAL_LENGTH_35MM().isPresent()) {
            appendItem(fileWriter, (myMetadata.getFOCAL_LENGTH_35MM().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getEXPOSURE_TIME().isPresent()) {
            appendItem(fileWriter, (myMetadata.getEXPOSURE_TIME().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getEXPOSURE_COMPENSATION().isPresent()) {
            appendItem(fileWriter, (myMetadata.getEXPOSURE_COMPENSATION().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getEXPOSURE_PROGRAM().isPresent()) {
            appendItem(fileWriter, (myMetadata.getEXPOSURE_PROGRAM().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getORIENTATION().isPresent()) {
            appendItem(fileWriter, (myMetadata.getORIENTATION().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getCOLOR_SPACE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getCOLOR_SPACE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getSENSING_METHOD().isPresent()) {
            appendItem(fileWriter, (myMetadata.getSENSING_METHOD().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getSOFTWARE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getSOFTWARE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getMAKE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getMAKE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getMODEL().isPresent()) {
            appendItem(fileWriter, (myMetadata.getMODEL().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getLENS_MAKE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getLENS_MAKE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getLENS_MODEL().isPresent()) {
            appendItem(fileWriter, (myMetadata.getLENS_MODEL().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getOWNER_NAME().isPresent()) {
            appendItem(fileWriter, (myMetadata.getOWNER_NAME().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getTITLE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getTITLE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getAUTHOR().isPresent()) {
            appendItem(fileWriter, (myMetadata.getAUTHOR().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getSUBJECT().isPresent()) {
            appendItem(fileWriter, (myMetadata.getSUBJECT().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getKEYWORDS().isPresent()) {
            appendItem(fileWriter, (myMetadata.getKEYWORDS().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getCOMMENT().isPresent()) {
            appendItem(fileWriter, (myMetadata.getCOMMENT().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getRATING().isPresent()) {
            appendItem(fileWriter, (myMetadata.getRATING().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getRATING_PERCENT().isPresent()) {
            appendItem(fileWriter, (myMetadata.getRATING_PERCENT().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getDATE_TIME_ORIGINAL().isPresent()) {
            appendItem(fileWriter, (myMetadata.getDATE_TIME_ORIGINAL().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getGPS_LATITUDE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getGPS_LATITUDE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getGPS_LATITUDE_REF().isPresent()) {
            appendItem(fileWriter, (myMetadata.getGPS_LATITUDE_REF().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getGPS_LONGITUDE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getGPS_LONGITUDE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getGPS_LONGITUDE_REF().isPresent()) {
            appendItem(fileWriter, (myMetadata.getGPS_LONGITUDE_REF().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getGPS_ALTITUDE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getGPS_ALTITUDE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getGPS_ALTITUDE_REF().isPresent()) {
            appendItem(fileWriter, (myMetadata.getGPS_ALTITUDE_REF().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getGPS_SPEED().isPresent()) {
            appendItem(fileWriter, (myMetadata.getGPS_SPEED().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getGPS_SPEED_REF().isPresent()) {
            appendItem(fileWriter, (myMetadata.getGPS_SPEED_REF().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getGPS_PROCESS_METHOD().isPresent()) {
            appendItem(fileWriter, (myMetadata.getGPS_PROCESS_METHOD().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getGPS_BEARING().isPresent()) {
            appendItem(fileWriter, (myMetadata.getGPS_BEARING().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getGPS_BEARING_REF().isPresent()) {
            appendItem(fileWriter, (myMetadata.getGPS_BEARING_REF().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getGPS_TIMESTAMP().isPresent()) {
            appendItem(fileWriter, (myMetadata.getGPS_TIMESTAMP().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getROTATION().isPresent()) {
            appendItem(fileWriter, (myMetadata.getROTATION().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getEXIF_VERSION().isPresent()) {
            appendItem(fileWriter, (myMetadata.getEXIF_VERSION().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getLENS_ID().isPresent()) {
            appendItem(fileWriter, (myMetadata.getLENS_ID().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getCOPYRIGHT().isPresent()) {
            appendItem(fileWriter, (myMetadata.getCOPYRIGHT().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getARTIST().isPresent()) {
            appendItem(fileWriter, (myMetadata.getARTIST().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getSUB_SEC_TIME_ORIGINAL().isPresent()) {
            appendItem(fileWriter, (myMetadata.getSUB_SEC_TIME_ORIGINAL().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getOBJECT_NAME().isPresent()) {
            appendItem(fileWriter, (myMetadata.getOBJECT_NAME().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getCAPTION_ABSTRACT().isPresent()) {
            appendItem(fileWriter, (myMetadata.getCAPTION_ABSTRACT().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getCREATOR().isPresent()) {
            appendItem(fileWriter, (myMetadata.getCREATOR().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getIPTC_KEYWORDS().isPresent()) {
            appendItem(fileWriter, (myMetadata.getIPTC_KEYWORDS().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getCOPYRIGHT_NOTICE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getCOPYRIGHT_NOTICE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getFILE_TYPE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getFILE_TYPE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getFILE_SIZE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getFILE_SIZE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getAVG_BITRATE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getAVG_BITRATE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getMIME_TYPE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getMIME_TYPE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getCREATE_DATE().isPresent()) {
            appendItem(fileWriter, (myMetadata.getCREATE_DATE().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getVISION_CONTENT().isPresent()) {
            appendItem(fileWriter, (myMetadata.getVISION_CONTENT().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendkomma(fileWriter);
        if (myMetadata.getVISION_TAGS().isPresent()) {
            appendItem(fileWriter, (myMetadata.getVISION_TAGS().get()));
        } else {
            appendNotfound(fileWriter);
        }
        appendnewline(fileWriter);
    }

    public void handleDirectoryBuildTitleMap (String startsWithDirectory){
        try {
            // https://www.codejava.net/java-core/concurrency/java-concurrency-understanding-thread-pool-and-executors
            // Better: completable Future https://www.deadcoderising.com/java8-writing-asynchronous-code-with-completablefuture/


            System.out.println("handleDirectoryBuildTitleMap start");
            Files.walk(Paths.get(startsWithDirectory))
                    .filter(p -> {
                        return (p.toString().toLowerCase().endsWith(".txt"));
                    })
                    .forEach(item -> {

                        File file = item.toFile();
                        if (file.isFile()) {
                            String sourceFile = "";
                            sourceFile = file.getAbsolutePath();

                            try {
                                buildTitleMap(sourceFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            System.out.println("handleDirectoryBuildTitleMap end");

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("handleDirectoryBuildTitleMap completed");
    }

    public void handleDirectoryCopyFile( String startsWithDirectory, String copyDirectory) {
        try {
            // https://www.codejava.net/java-core/concurrency/java-concurrency-understanding-thread-pool-and-executors
            // Better: completable Future https://www.deadcoderising.com/java8-writing-asynchronous-code-with-completablefuture/


            System.out.println("handleDirectoryCopyFile start");
            Files.walk(Paths.get(startsWithDirectory))
                    .filter(p -> {
                        return ((p.toString().toLowerCase().endsWith(".cr2")) || (p.toString().toLowerCase().endsWith(".cr3")) || (p.toString().toLowerCase().endsWith(".jpg")));
                    })
                    .forEach(item -> {

                        File file = item.toFile();
                        if (file.isFile()) {
                           String sourceFile = "";
                           sourceFile = file.getAbsolutePath();
                           String destFile = copyDirectory.trim()+"\\"+ newFileNameGalleryFromTitleMap(file);
                            try {
                                copyFile(sourceFile,destFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            System.out.println("handleDirectoryCopyFile end");

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("handleDirectoryCopyFile completed");
    }

    /**
     * Java 7 way to copy a file from one location to another
     * @param from
     * @param to
     * @throws IOException
     */
    public static void copyFile(String from, String to) throws IOException{
        Path src = Paths.get(from);
        Path dest = Paths.get(to);
        try {
            Files.copy(src, dest);
        } catch (FileAlreadyExistsException ex){
            // do nothing
            System.out.println("Datei bereits vorhanden: " +dest.toString());
        }
    }
    public void buildTitleMap(String iFile) throws IOException{
        System.out.println("buildTitleMap: " + iFile);
        InputStream inputStream       = new FileInputStream(iFile);
        Reader      inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader in = new BufferedReader(inputStreamReader);

        String line = "";
        try {
            while ((line = in.readLine()) != null) {
                String titleKey = line.substring(0,4);
                String title = line.substring(5);
                // title = title.replaceAll("[^\\p{Alpha}\\p{Digit}]+","");
                title = title.replaceAll("/", " ");
                this.titleMap.put(titleKey,title);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String newFileNameGalleryFromTitleMap(File file){
        String fileNameIn = file.getName();
        String fileNameOut = fileNameIn;
        int searchPos = fileNameOut.indexOf("-");
        int startPos = -1;
        if (searchPos>0){
            startPos = searchPos + 1;
        }
        String myTitleKey = fileNameIn.substring(startPos,startPos+4);
        String myTitle = titleMap.get(myTitleKey);
        if (myTitle!=null){
            int startPosPoint = fileNameIn.indexOf(".");
            String fileNamePart1 = fileNameIn.substring(0,startPosPoint);
            fileNameOut = fileNamePart1 + " " +  myTitle + ".jpg";
        }

        return  fileNameOut;
    }

    void handleDirectoryWalker(FileWriter fileWriter, String startsWithDirectory, Boolean withVision) {
        try {
            // https://www.codejava.net/java-core/concurrency/java-concurrency-understanding-thread-pool-and-executors
            // Better: completable Future https://www.deadcoderising.com/java8-writing-asynchronous-code-with-completablefuture/
            ExecutorService executorService = Executors.newFixedThreadPool(200);
            List<Future<PictureMetaData>> listFuturePictureMetaData = new ArrayList<>();

            System.out.println("handleDirectoryWalker start");
            Files.walk(Paths.get(startsWithDirectory))
                    .filter(p -> {
                        return ((p.toString().toLowerCase().endsWith(".cr2")) || (p.toString().toLowerCase().endsWith(".cr3")) || (p.toString().toLowerCase().endsWith(".jpg")));
                    })
                    .forEach(item -> {

                        File file = item.toFile();
                        if (file.isFile()) {

                            Callable<PictureMetaData> callable = () -> {

                                if (withVision) {
                                    ExtractPictureContentData extract = new ExtractPictureContentData(this.startsWithDirectory, this.csvFile);
                                    extract.setSubstringKey(this.getSubscriptionKey());
                                    File fileCompressed = extract.compressJpg(file);
                                    PictureMetaData myMetadata = extract.getPictureContent(file, fileCompressed);
                                    return myMetadata;
                                } else {
                                    PictureMetaData myMetadata = getPictureMetaDataExif(file);
                                    return myMetadata;
                                }


                            };
                            Future<PictureMetaData> future = executorService.submit(callable);
                            listFuturePictureMetaData.add(future);

                         /*   if (withVision) {
                                handleFileWithVision(fileWriter, file);
                            } else {
                                handleFile(fileWriter, file);
                            }*/
                        }
                    });
            System.out.println("handleDirectoryWalker prepare end, get begin");
            // http://www.angelikalanger.com/Articles/EffectiveJava/81.Java8.Parallel-Streams/81.Java8.Parallel-Streams.html
            listFuturePictureMetaData.parallelStream().forEachOrdered(pictureMetaDataFuture -> {
                try {
                    PictureMetaData myMetadata = pictureMetaDataFuture.get();
                    processMetaData(myMetadata, fileWriter);
                    System.out.println("startsWithDirectory: " + startsWithDirectory);
                    System.out.println(("absolutePath: " + pictureMetaDataFuture.get().absolutePath));
                    Command myCommand = new Command(startsWithDirectory);
                    // https://exiftool.org/exiftool_pod.html
                    // exiftool -keywords=EXIF -keywords=editor dst.jpg
                    String doExecute = "exiftool ";
                    if (pictureMetaDataFuture.get().getVISION_TAGS().isPresent()) {

                        String[] arrOfStr = pictureMetaDataFuture.get().getVISION_TAGS().get().split("%");

                        for (String keyword : arrOfStr) {
                            System.out.println(keyword);
                            doExecute += " -keywords=" + keyword;
                        }

                        doExecute += " " + pictureMetaDataFuture.get().absolutePath.get();
                        System.out.println("doExecute: " + doExecute);
                        myCommand.exec(doExecute);
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("handleDirectoryWalker completed");
    }

    public void createCSVFile() throws IOException {
        this.createCSVFile(startsWithDirectory, csvFile, withVision);
    }

    public void createCSVFile(String startsWithDirectory, String csvFile, Boolean withVision) throws IOException {
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
        CSV_HEADER += ",COPYRIGHT_NOTICE, FILE_TYPE, FILE_SIZE, AVG_BITRATE, MIME_TYPE, CREATE_DATE, VISION_CONTENT, VISION_TAGS";
        appendItem(fileWriter, CSV_HEADER);
        appendnewline(fileWriter);

        handleDirectoryWalker(fileWriter, startsWithDirectory, withVision);

        fileWriter.flush();
        fileWriter.close();
        final long timeEnd = System.currentTimeMillis();
        System.out.println("Duration: " + (timeEnd - timeStart) + " millisec.");
    }


}
