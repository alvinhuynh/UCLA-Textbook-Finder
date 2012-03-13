package amazon.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class runapi{


	  public ArrayList<String> getAmazonItems(String isbn)
	  {
		  Map<String, String> input = new HashMap<String,String>();
		  input.put("Service","AWSECommerceService");
		  input.put("Operation","ItemLookup");
		  input.put("ResponseGroup","ItemAttributes");
		  input.put("Condition","All");
		  input.put("IdType","ISBN");
		  input.put("SearchIndex","Books");
		  input.put("ItemId",isbn);
		  input.put("AssociateTag", "bookfinder");
		  amazonapi a = new amazonapi();
		  String url = a.sign(input);
		  System.out.println(url);
		  
	      ArrayList<String> items = new ArrayList<String>();
		  try {
			  URL url1 = new URL(url);
			  URLConnection conn1 = url1.openConnection();
		      BufferedReader in = new BufferedReader(new InputStreamReader(conn1.getInputStream()));

		      String xmlString = in.readLine();
		      System.out.println(xmlString);
			  
		      org.dom4j.Document doc = new SAXReader().read(new StringReader(xmlString));
		      Element ItemLookupResponse = doc.getRootElement();
		      
		      Element Items = getChildWithName(ItemLookupResponse, "Items");
		      Element Item = null;
		      
		      
		    for(Iterator i = Items.elementIterator(); i.hasNext();)	 {  
		    	  
		    	  Item = (Element) i.next();
		    	  if(Item.getName().equals("Item")) {
		    		  Element DetailPageURL = getChildWithName(Item, "DetailPageURL");
				      Element ItemAttributes = getChildWithName(Item, "ItemAttributes");
				      Element ListPrice = getChildWithName(ItemAttributes, "ListPrice");
				      Element FormattedPrice = (ListPrice != null) ? getChildWithName(ListPrice, "FormattedPrice") : null;
				      Element Title = getChildWithName(ItemAttributes, "Title");
				      
				      String URL = (DetailPageURL != null) ? DetailPageURL.getText() : "NULL";
				      String price = (FormattedPrice != null) ? FormattedPrice.getText() : "NULL";
				      String bookTitle = (Title != null) ? Title.getText() : "NULL";
				      String info = "URL: " + URL + ", Price: " + price + ", Title: " + bookTitle;
				      items.add(info);
		    	  }
		    }
		    
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		return items;

		  
	  }
	  private static Element getChildWithName(Element root, String name) {
		  
		  Element child = null;
	      
		  Iterator i;
		  boolean found = false;
	      for(i = root.elementIterator(); i.hasNext();) {
	    	  child = (Element) i.next();
	    	  if(child.getName().equals(name)) {
	    		  found = true;
	    		  break;
	    	  }
	    		 
	      }
	      if(found)
	    	  return child;
	      else
	    	  return null;  
	  }
	  
}