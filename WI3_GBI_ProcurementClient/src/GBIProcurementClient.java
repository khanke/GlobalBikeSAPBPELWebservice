import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import marketWebservice.MarketWS;
import marketWebservice.MarketWSServerService;
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
	  MarketWSServerService mservice = new MarketWSServerService();
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
      
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      
      try{
          System.out.print("Wie hoch ist dein Budget?: ");
          String budget = br.readLine();

          System.out.println("---------------------------------- ");
          for(int i = 0; i < material.size(); i++) {
            System.out.println(material.get(i).getDescription() + " [" + i + "]");
          }

          System.out.println("---------------------------------- ");
          System.out.print("Welches Material soll bestellt werden?: ");
          int i = Integer.parseInt(br.readLine());
          String partNumber = material.get(i).getNumber();
     
          // 3. und 4. Bestes Angebot heraussuchen
          String offer = marketWS.getBestOffer(partNumber, budget);
          System.out.println("---------------------------------- ");
          System.out.println("Folgendes Angebot wurde ausgewählt: ");
          System.out.println(offer);  
          System.out.println("---------------------------------- ");
          
          // 5. Bestellung aufgeben
          if(offer != "ERROR")
            System.out.println(orderWS.placeOrder("GBI-206", "GBI-206", offer));
          else
            System.out.println("Fehler");
          
      }catch(NumberFormatException nfe){
          System.err.println("Invalid Format!");
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

	}
}