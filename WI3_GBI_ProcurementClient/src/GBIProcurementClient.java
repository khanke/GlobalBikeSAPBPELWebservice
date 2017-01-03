import java.util.List;

import com.thoughtworks.xstream.XStream;

import marketWebservice.MarketWS;
import marketWebservice.MarketWSService;
import materialstamm.Material;
import orderWebservice.OrderWS;
import orderWebservice.OrderWSService;
import rawMatWebservice.RawMaterialsWS;
import rawMatWebservice.RawMaterialsWSService;

public class GBIProcurementClient {
	public static OrderWS orderWS;
	public static MarketWS marketWS;
	public static RawMaterialsWS rawMaterialsWS;

	public static void main(String[] args) {
		MarketWSService mservice = new MarketWSService();
		OrderWSService oservice = new OrderWSService();
		RawMaterialsWSService rservice = new RawMaterialsWSService();
		marketWS = mservice.getMarketWSPort();
		orderWS = oservice.getOrderWSPort();
		rawMaterialsWS = rservice.getRawMaterialsWSPort();
		
		new GBIProcurementClient();
	}

	public GBIProcurementClient() {
		
	  // 1. Materialliste holen
      String materiallist = rawMaterialsWS.getRawMaterials("GBI-206", "GBI-206");
	  
      // 2. Material auswählen und Budget festlegen
      XStream xstream = new XStream();
      Object obj = xstream.fromXML(materiallist);
      List<Material> material = null;
      if (obj instanceof List<?>) material = (List<Material>) obj;
      
      String budget = "150";
      String partNumber = material.get(0).getNumber();
      
	  // 3. und 4. Bestes Angebot heraussuchen
      String offer = marketWS.getBestOffer(partNumber, budget);
      System.out.println(offer);
		
      // 5. Bestellung aufgeben
	  if(offer != "ERROR")
	    System.out.println(orderWS.placeOrder("GBI-206", "GBI-206", offer));
	  else
	    System.out.println("Fehler");
	}
}