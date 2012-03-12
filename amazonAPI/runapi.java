package amazon.api;

import java.util.HashMap;
import java.util.Map;

public class runapi{

	  public static void main(String[] args)
	  {
		  Map<String, String> input = new HashMap<String,String>();
		  input.put("Service","AWSECommerceService");
		  input.put("Operation","ItemLookup");
		  input.put("ResponseGroup","ItemAttributes");
		  input.put("Condition","All");
		  input.put("IdType","ISBN");
		  input.put("SearchIndex","Books");
		  input.put("ItemId","012383872X");
		  input.put("AssociateTag", "bookfinder");
		  amazonapi a = new amazonapi();
		  String test = a.sign(input);
		  System.out.println(test);
	  }
}