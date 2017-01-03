import marketWebservice.MarketWS;
import marketWebservice.MarketWSService;
import orderWebservice.OrderWS;
import orderWebservice.OrderWSService;

public class GBIProcurementClient {
	public static OrderWS orderWS;
	public static MarketWS marketWS;

	public static void main(String[] args) {
		MarketWSService mservice = new MarketWSService();
		OrderWSService oservice = new OrderWSService();
		marketWS = mservice.getMarketWSPort();
		orderWS = oservice.getOrderWSPort();
		
		new GBIProcurementClient();
	}

	public GBIProcurementClient() {
		
	  //1.
	  
	  //3.
		String offer = marketWS.getBestOffer("BOLT1040", "150");
		System.out.println(offer);
		
		if(offer != "ERROR")
			System.out.println(orderWS.placeOrder("GBI-206", "GBI-206", offer));
		else
			System.out.println("Fehler");
	}
}