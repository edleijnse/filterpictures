# filterpictures
with this library you can walk through a directory with subdirectories and extract all picture-bound metadata.

Supported filetypes: jpg, CR2

this utility uses the following repositories to extract the metadata:

       <dependency>
            <groupId>com.adobe.xmp</groupId>
            <artifactId>xmpcore</artifactId>
            <version>5.1.2</version>
        </dependency>
	
        <dependency>
            <groupId>com.drewnoakes</groupId>
            <artifactId>metadata-extractor</artifactId>
            <version>2.11.0</version>
            <scope>compile</scope>
        </dependency>
	
To use this library, you can take the added jar file and copy it into your lib library or 
you can try to add the repository with jitpack (no garantee, not yet thoroughly tested:

<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
  
  <dependency>
	    <groupId>com.github.edleijnse</groupId>
	    <artifactId>filterpictures</artifactId>
	    <version>R1.0.3.20180523</version>
	</dependency>
  
  Programming example (copied from gui client in repository edleijnse/cloudninegui) :

        ExtractPictureMetaData extractPictureMetaData;
        extractPictureMetaData = new ExtractPictureMetaData(myChoosenDirectory, csvFile);
        try {
            extractPictureMetaData.createCSVFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
