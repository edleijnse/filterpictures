package filterpictures;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class ExtractPictureContentData {
    private String startsWithDirectory;
    private String csvFile;


    private static String subscriptionKey = "MY-KEY-HERE";


    public static final String uriBase =
            "https://westeurope.api.cognitive.microsoft.com/vision/v1.0/analyze";


    public ExtractPictureContentData(String startsWithDirectory, String csvFile) {
        this.startsWithDirectory = startsWithDirectory;
        this.csvFile = csvFile;
    }

    public void setSubstringKey(String myKey) {
        subscriptionKey = myKey;
    }


    public PictureMetaData getPictureContent(File file) throws IOException {


        PictureMetaData myPictureMetadata = new PictureMetaData();
        myPictureMetadata.setPictureName(Optional.of(file.getName()));
        myPictureMetadata.setAbsolutePath(Optional.of(file.getAbsolutePath()));
        myPictureMetadata.setCanonicalPath(Optional.of(file.getCanonicalPath()));

        HttpClient httpclient = new DefaultHttpClient();
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            // use httpClient (no need to close it explicitly)
            URIBuilder builder = new URIBuilder(uriBase);

            builder.setParameter("visualFeatures", "Categories,Description,Color,Adult");
            builder.setParameter("language", "en");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);

            // https://stackoverflow.com/questions/49463736/how-to-send-a-local-image-instead-of-url-to-microsoft-computer-vision-api-using
            //  request.setHeader("Content-Type", "application/json");
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            // Request body.
            // BufferedImage image = null;
            // File f = null;
            // f = new File("C:\\Coffee.jpg"); //image file path
            // image = ImageIO.read(file);

            // File file = new File("C:\\Coffee.jpg");

            FileEntity reqEntityF =
                    new FileEntity(file, ContentType.APPLICATION_OCTET_STREAM);

            request.setEntity(reqEntityF);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                JSONObject json = new JSONObject(jsonString);
                System.out.println("REST Response:\n");
                // System.out.println(json.toString(2));
                JSONArray descriptionArr = json.getJSONObject("description").getJSONArray("captions");
                descriptionArr.forEach(element -> {
                    JSONObject myElement = (JSONObject)element;
                    String textContent = myElement.getString("text");
                    System.out.println("content: " + textContent);
                });
                JSONArray tagsArr = json.getJSONObject("description").getJSONArray("tags");
                final String[] myTagsTmp = {""};
                tagsArr.forEach(element -> {
                    String tagName = element.toString();
                    System.out.println(tagName);
                    myTagsTmp[0] +=element.toString()+"%";

                });
                String myTags = myTagsTmp[0];
                System.out.println(myTags);

            }

        } catch (IOException e) {

            // handle
            e.printStackTrace();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        return myPictureMetadata;
    }
}
