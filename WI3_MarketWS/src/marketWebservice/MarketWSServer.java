package marketWebservice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Endpoint;

import com.thoughtworks.xstream.XStream;

import materialstamm.Material;
import order.Order;

@WebService(name = "MarketWS", targetNamespace = "http://marketWebservice/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class MarketWSServer {

  public final static String DEPLOYMENT_PATH = "http://localhost:8082/MarketWS";
  String result = "ERROR";
  HashMap<String, List<Material>> hmap = new HashMap<String, List<Material>>();

  /**
   * Hauptprogramm zum Starten.
   */
  public static void main(String[] args) {
    // Eine neue Instanz des OrderWS erzeugen, diese wird dann mittels
    // deployment-path publiziert.
    MarketWSServer mw = new MarketWSServer();
    Endpoint endpointClock = Endpoint.publish(DEPLOYMENT_PATH, mw);
    System.out.println("Market Webservice running");
  }

  @WebMethod
  public String updateRawMaterials(String supplierNumber, String materiallist) {
    // XML-String mittels XStream deserialisieren
    XStream xstream = new XStream();
    Object obj = xstream.fromXML(materiallist);
    List<Material> material = null;

    if (obj instanceof List<?>) {
      material = (List<Material>) obj;
      if (hmap.replace(supplierNumber, material) == null)
        hmap.put(supplierNumber, material);
      result = "Success";
    }

    return result;
  }

  @WebMethod
  public String getBestOffer(String partNumber, String budget) {
    String result;
    
    try {

      Set set = hmap.entrySet();
      Iterator iterator = set.iterator();
      double price = Double.MAX_VALUE;
      String supplier = "";

      while (iterator.hasNext()) {
        Map.Entry mentry = (Map.Entry) iterator.next();
        List<Material> list = (List<Material>) mentry.getValue();

        for (int i = 0; i < list.size(); i++) {
          if (list.get(i).getNumber().equals(partNumber)) {
            if (list.get(i).getPrice() < price) {
              price = list.get(i).getPrice();
              supplier = (String) mentry.getKey();
              break;
            }
          }
        }
      }

      int amount = (int) (Integer.valueOf(budget) / price);
      List<Material> list = hmap.get(supplier);

      Material material = null;
      for (int i = 0; i < list.size(); i++) {
        if (list.get(i).getNumber().equals(partNumber)) {
          material = list.get(i);
          break;
        }
      }

      Order order = new Order();
      order.setMaterial(material);
      order.setQty(amount);
      order.setSupplierNumber(supplier);

      XStream xstream = new XStream();
      result = xstream.toXML(order);

    } catch (Exception e) {
      result = "ERROR";
    }

    return result;
  }

}
