package filterpictures;

import java.util.Optional;

public class PictureMetaData {
    Optional<String> absolutePath = Optional.empty();
    Optional<String> aperture = Optional.empty();
    Optional<String> canonicalPath = Optional.empty();
    Optional<String> dateTime = Optional.empty();
    Optional<String> exposure = Optional.empty();
    Optional<String> exposureBias = Optional.empty();
    Optional<String> focalLength = Optional.empty();
    Optional<String> height = Optional.empty();
    Optional<String> iso = Optional.empty();
    Optional<String> lenseModel = Optional.empty();
    Optional<String> lenseDescription = Optional.empty();
    Optional<String> make = Optional.empty();
    Optional<String> model = Optional.empty();
    Optional<String> pictureName = Optional.empty();
    Optional<String> width = Optional.empty();

    public Optional<String> getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(Optional<String> absolutePath) {
        this.absolutePath = absolutePath;
    }

    public Optional<String> getAperture() {
        return aperture;
    }

    public void setAperture(Optional<String> aperture) {
        this.aperture = aperture;
    }

    public Optional<String> getCanonicalPath() {
        return canonicalPath;
    }

    public void setCanonicalPath(Optional<String> canonicalPath) {
        this.canonicalPath = canonicalPath;
    }

    public Optional<String> getDateTime() {
        return dateTime;
    }

    public void setDateTime(Optional<String> dateTime) {
        this.dateTime = dateTime;
    }

    public Optional<String> getExposure() {
        return exposure;
    }

    public void setExposure(Optional<String> exposure) {
        this.exposure = exposure;
    }

    public Optional<String> getExposureBias() {
        return exposureBias;
    }

    public void setExposureBias(Optional<String> exposureBias) {
        this.exposureBias = exposureBias;
    }

    public Optional<String> getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(Optional<String> focalLength) {
        this.focalLength = focalLength;
    }

    public Optional<String> getHeight() {
        return height;
    }

    public void setHeight(Optional<String> height) {
        this.height = height;
    }

    public Optional<String> getIso() {
        return iso;
    }

    public void setIso(Optional<String> iso) {
        this.iso = iso;
    }

    public Optional<String> getLenseModel() {
        return lenseModel;
    }

    public void setLenseModel(Optional<String> lenseModel) {
        this.lenseModel = lenseModel;
    }

    public Optional<String> getLenseDescription() {
        return lenseDescription;
    }

    public void setLenseDescription(Optional<String> lenseDescription) {
        this.lenseDescription = lenseDescription;
    }

    public Optional<String> getMake() {
        return make;
    }

    public void setMake(Optional<String> make) {
        this.make = make;
    }

    public Optional<String> getModel() {
        return model;
    }

    public void setModel(Optional<String> model) {
        this.model = model;
    }

    public Optional<String> getPictureName() {
        return pictureName;
    }

    public void setPictureName(Optional<String> pictureName) {
        this.pictureName = pictureName;
    }

    public Optional<String> getWidth() {
        return width;
    }

    public void setWidth(Optional<String> width) {
        this.width = width;
    }
}
