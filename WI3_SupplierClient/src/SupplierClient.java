import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

import com.thoughtworks.xstream.XStream;

import marketWebservice.MarketWS;
import marketWebservice.MarketWSService;
import materialstamm.Material;
import rawMatWebservice.RawMaterialsWS;
import rawMatWebservice.RawMaterialsWSService;

public class SupplierClient {
	public static MarketWS marketWS;
    public static RawMaterialsWS rawMaterialsWS;

	public static void main(String[] args) {
		MarketWSService service = new MarketWSService();
        RawMaterialsWSService rmWS = new RawMaterialsWSService();
		marketWS = service.getMarketWSPort();
		rawMaterialsWS = rmWS.getRawMaterialsWSPort();
		new SupplierClient();
	}

	public SupplierClient() {
	  String materiallist = rawMaterialsWS.getRawMaterials("GBI-206", "GBI-206");
//	  System.out.println(materiallist);
      System.out.println("#######################################################");
	  
      XStream xstream = new XStream();
      Object obj = xstream.fromXML(materiallist);
      List<Material> material = null;
      if (obj instanceof List<?>) material = (List<Material>) obj;
      
      runSupplier("0000121206", material); // Shell Gear
      runSupplier("0000122206", material); // Cologne Bike Supplies
      runSupplier("0000123206", material); // Sachsen Stahl AG
           
	}
	
	private void runSupplier(String supplier, List<Material> material) {
	  Random r = new Random();
	  
      for(int i = 0; i < material.size(); i++) {
        BigDecimal bd = new BigDecimal(10 + (50 - 10) * r.nextDouble());
        bd = bd.setScale(2, RoundingMode.HALF_UP);
      
        material.get(i).setPrice(bd.doubleValue());
      }
      
      XStream xstream = new XStream();
      String materiallist = xstream.toXML(material);

      System.out.println(supplier + " [" + marketWS.updateRawMaterials(supplier, materiallist) + "]");
	}

}

