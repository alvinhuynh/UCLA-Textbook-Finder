package ucla.textbook.finder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class UCLATextbookFinderActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        
        
        /*
         // ****NOTE: URL is wrong. Not sure what goes under Signature and Timestamp.****
         String amazonURL = "http://webservices.amazon.com/onca/json?" + 
				"Service=AWSECommerceService&" + 
				"AWSAccessKeyId=[AKIAIQ3OO6JG2GLGO62A]&" +
				"Operation=ItemLookup&" +
				"ItemId=0316067938&" +
				"IdType=ISBN&" +
				"ResponseGroup=OfferFull&" + 
				"Condition=All" + 
				"Timestamp=[YYYY-MM-DDThh:mm:ssZ]&" +
				"Signature=[Request Signature]";
				
				
		String JSONResponse = getURL(amazonURL);
		String items = getAmazonItems(JSONResponse); */
        
        /*
        String ebayURL = "http://svcs.ebay.com/services/search/FindingService/v1?" +
               "OPERATION-NAME=findItemsByProduct&" +
               "SERVICE-VERSION=1.11.0&" + 
               "SECURITY-APPNAME=AlvinHuy-252e-4bc0-8f43-de55884e7b63&" +
               "RESPONSE-DATA-FORMAT=JSON&" + 
               "REST-PAYLOAD&" +
               "paginationInput.entriesPerPage=10&" +
               "productId.@type=ISBN&" +
               "productId=1451648537";
        
        String JSONResponse = getURL(ebayURL);
        String items = getEbayItems(JSONResponse); */
        
        String halfURL = "https://svcs.ebay.com/services/half/HalfFindingService/v1?" + 
               "OPERATION-NAME=findHalfItems" + 
        	   "&X-EBAY-SOA-SERVICE-NAME=HalfFindingService&SERVICE-VERSION=1.0.0" + 
               "&GLOBAL-ID=EBAY-US" + 
        	   "&X-EBAY-SOA-SECURITY-APPNAME=AlvinHuy-252e-4bc0-8f43-de55884e7b63" + 
               "&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD" + 
        	   "&productID=1451648537&productID.@type=ISBN";
        
        String JSONResponse = getURL(halfURL);
		String items = getHalfItems(JSONResponse);
		tv.setText(items);
        setContentView(tv);
    }
    
    // Does nothing so far.
    private String getAmazonItems(String JSONResponse) {
    	return "";
    }
    
    // This function takes in a JSON String returned by the Half API and returns a list of items
    // where each item includes the title, purchase URL, condition, and price of the book.
    private String getHalfItems(String JSONResponse) {

        try {
        	
        	JSONObject JSONResponseObject = (JSONObject) new JSONTokener(JSONResponse).nextValue();
			JSONObject findHalfItemsResponseObject = JSONResponseObject.getJSONObject("findHalfItemsResponse");
			JSONObject productObject = findHalfItemsResponseObject.getJSONObject("product");
			String title = productObject.getString("title");
			JSONArray itemArray = productObject.getJSONArray("item");
			String items = "";
		
			for(int i = 0; i < itemArray.length(); i++) {
				
				
				JSONObject itemObject = itemArray.getJSONObject(i);
				String itemURL = itemObject.getString("itemURL");
				String condition = itemObject.getString("condition");
				JSONObject priceObject = itemObject.getJSONObject("price");
				String price = priceObject.getString("@currencyId") + priceObject.getString("__value__");	
				
				int itemNumber = i + 1;
				String item = "Item " + itemNumber + "\n";
				item += "Title: " + title + "\n" + 
					    "URL: " + itemURL + "\n" + "Condition: " + 
					    condition + "\n" + "Price: " + price + "\n\n";
			
				items += item;
			
			}
			
			return items;
			
        }catch (JSONException e) {
			// TODO Auto-generated catch block
			return e.toString();
        }
    }
    
    // This function takes in a JSON String returned by the Ebay API and returns a list of items
    // where each item includes the title, purchase URL, condition, and price of the book.
    // Note that Ebay items either are auctioned off or sold at a buy now price.  In the case of
    // an auctioned item, it returns the current highest bid for the item.  Otherwise, it returns
    // the buy now price.
    private String getEbayItems(String JSONResponse) {

        try {
        	
			JSONObject JSONResponseObject = (JSONObject) new JSONTokener(JSONResponse).nextValue();
			JSONArray findItemsByProductResponseArray = JSONResponseObject.getJSONArray("findItemsByProductResponse");
			JSONObject findItemsByProductResponseObject = findItemsByProductResponseArray.getJSONObject(0);
			JSONArray searchResultArray = findItemsByProductResponseObject.getJSONArray("searchResult");	
			JSONObject searchResultObject = searchResultArray.getJSONObject(0);
			JSONArray itemArray = searchResultObject.getJSONArray("item");
			String items = "";
			
			for(int i = 0; i < itemArray.length(); i++) {
				
				JSONObject itemObject = itemArray.getJSONObject(i);
				String title = itemObject.getString("title");
				String viewItemURL = itemObject.getString("viewItemURL");
				
				JSONArray listingInfoArray = itemObject.getJSONArray("listingInfo");
				JSONObject listingInfoObject = listingInfoArray.getJSONObject(0);
				String buyItNowAvailable = listingInfoObject.getString("buyItNowAvailable");
				
				JSONArray conditionArray = itemObject.getJSONArray("condition");
				JSONObject conditionObject = conditionArray.getJSONObject(0);
				String conditionDisplayName = conditionObject.getString("conditionDisplayName");
				
				int itemNumber = i + 1;
				String item = "Item " + itemNumber + "\n";
				String buyPrice = "";
				String highestBid = "";
				
				if(buyItNowAvailable.equals("true")) {
					
					JSONArray buyItNowPriceArray = listingInfoObject.getJSONArray("buyItNowPrice");
					JSONObject buyItNowPriceObject = buyItNowPriceArray.getJSONObject(0);
					buyPrice += buyItNowPriceObject.getString("__value__");
					buyPrice += " " + buyItNowPriceObject.getString("@currencyId");	
					
					item += "Title: " + title + "\n" + 
						    "URL: " + viewItemURL + "\n" + "Condition: " + 
				            conditionDisplayName + "\n" + "Price: " + buyPrice + "\n\n";
				}
				else {
					
					JSONArray sellingStatusArray = itemObject.getJSONArray("sellingStatus");
					JSONObject sellingStatusObject = sellingStatusArray.getJSONObject(0);
					JSONArray currentPriceArray = sellingStatusObject.getJSONArray("currentPrice");
					JSONObject currentPriceObject = currentPriceArray.getJSONObject(0);
					highestBid += currentPriceObject.getString("__value__");
					highestBid += " " + currentPriceObject.getString("@currencyId");
					item += "Title: " + title + "\n" + 
						    "URL: " + viewItemURL + "\n" + "Condition: " + 
					        conditionDisplayName + "\n" + "Highest Bid: " + highestBid + "\n\n";
				}
				
				items += item;
			
			}
			
			return items;
			
        }catch (JSONException e) {
			// TODO Auto-generated catch block
			return e.toString();
        }
    }
    
    // This function takes in a HTTP Get Request URL and returns the response sent back
    // by the server.
    private String getURL(String url) {
    	
    	try {
    		
        	HttpClient client = new DefaultHttpClient();
        	HttpGet request = new HttpGet(url);
        	HttpResponse responseGet = client.execute(request);
        	HttpEntity resEntityGet = responseGet.getEntity();
        	if(resEntityGet != null) {
                return EntityUtils.toString(resEntityGet);
        	} else {
        		//TODO: Throw exception instead (URL not found)
        		return "NOT FOUND:" + url;
        	}
        } catch (Exception e) {
    		//TODO: Throw exception instead (URL not found)
        	return e.toString();
        }
    }
}
