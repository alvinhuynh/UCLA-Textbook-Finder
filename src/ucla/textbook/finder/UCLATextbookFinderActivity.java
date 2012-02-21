package ucla.textbook.finder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class UCLATextbookFinderActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);

        String URL = "http://svcs.ebay.com/services/search/FindingService/v1?" +
               "OPERATION-NAME=findItemsByProduct&" +
               "SERVICE-VERSION=1.11.0&" + 
               "SECURITY-APPNAME=AlvinHuy-252e-4bc0-8f43-de55884e7b63&" +
               "RESPONSE-DATA-FORMAT=JSON&" + 
               "REST-PAYLOAD&" +
               "paginationInput.entriesPerPage=2&" +
               "productId.@type=ReferenceID&" +
               "productId=53039031";
        
        tv.setText(getURL(URL));
        setContentView(tv);
    }
    
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
