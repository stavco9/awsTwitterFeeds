package utils;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class ScreenshotGenerator {
	
	//private static final String bucketName = System.getProperty("config.s3.bucket");
	//private static final String geckoDriver = System.getProperty("config.gecko.driver");
	
	private static final String bucketName = "colmantwitterscreenshots";
	private static final String geckoDriver = "C:\\Cloud\\geckodriver-v0.24.0-win64\\geckodriver.exe";
	
	private static final String s3AmazonUrl = ("https://" + bucketName + ".s3.amazonaws.com/");
	
	private static AmazonS3 client = AmazonS3ClientBuilder.defaultClient();
	
	private static String sendToAmazonObjectStorage(File screenshotFile) {
    	  
		try {
			// Upload a file as a new object with ContentType and title specified.
	        PutObjectRequest request = new PutObjectRequest(bucketName, screenshotFile.getName(), screenshotFile);
	        request.setCannedAcl(CannedAccessControlList.PublicRead);
	        ObjectMetadata metadata = new ObjectMetadata();
	        metadata.setContentType("plain/text");
	        metadata.addUserMetadata("x-amz-meta-title", "someTitle");
	      
	        request.setMetadata(metadata);
	        
	        client.putObject(request);	
		}catch (AmazonServiceException e) {
	        // The call was transmitted successfully, but Amazon S3 couldn't process 
	        // it, so it returned an error response.
	        e.printStackTrace();
	    } catch (SdkClientException e) {
	        // Amazon S3 couldn't be contacted for a response, or the client
	        // couldn't parse the response from Amazon S3.
	        e.printStackTrace();
	    }
        
        return (s3AmazonUrl + screenshotFile.getName());
   }
	
	  @SuppressWarnings("finally")
	public static String takeScreenshot(String url) {
		    
		String screenshotFilePath = "";
	    
		//if you didn't update the Path system variable to add the full directory path to the executable as above mentioned then doing this directly through code
		System.setProperty("webdriver.gecko.driver", geckoDriver);
		
        final WebDriver driver = new FirefoxDriver();
		
	    try {
	      URL urlObj = new URL(url);
			
		  final File screenShot = new File(urlObj.getHost() + ".png").getAbsoluteFile();
	    	
	      driver.get(url);
	      
	      TimeUnit.SECONDS.sleep(5);

	      final File outputFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
	      FileUtils.copyFile(outputFile, screenShot);
	      
	      screenshotFilePath = sendToAmazonObjectStorage(outputFile);	      
	    } 
	    catch(Exception e){
	    	e.printStackTrace();
	    }finally {
	      driver.close();
	    
	      return screenshotFilePath;
	    }
	  }
}
