package utils;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import twitterFeeds.ExtractedLink;

public class LinkExtractor {
	  public static ExtractedLink extractContent(String url) {

		  String content = "";
		  
		  String title = "";
		  
		  String description = "";
		  
		  String screenshotURL = "";
		  
		  try {
				org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
				
				Elements titles = doc.select("title");
				
				if (titles.get(0) != null) {
					title = titles.get(0).text().replace("'", "");	
				}
				
				System.out.println("Title: " + title);
				
				Elements metas = doc.select("meta");
				
				for(Element meta: metas) {
					if (meta.attr("property").contains("description")) {
						description = meta.attr("content").replace("'", "");;
						
						System.out.println("Description: " + description);
						
						break;
					}
				}
				
				Elements spanTags = doc.getElementsByTag("span");
			    
				for (Element spanTag : spanTags) {
					if (content.length() < 2500) {
						content += (spanTag.ownText().replace("'", "") + ". ");	
					}
					else {
						break;
					}
			    }
				
				System.out.println("Conetnt: " + content);
				
				screenshotURL = ScreenshotGenerator.takeScreenshot(url);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  
		  return new ExtractedLink(url, content, title, description, screenshotURL);
	  }
}
