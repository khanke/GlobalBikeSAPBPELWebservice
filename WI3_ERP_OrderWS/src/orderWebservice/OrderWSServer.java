package orderWebservice;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Endpoint;

import com.thoughtworks.xstream.XStream;

import order.Order;
import order.PurchaseOrder;

@WebService(name = "OrderWS", targetNamespace = "http://orderWebservice/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class OrderWSServer implements OrderWS {
  
  /**
   * Hauptprogramm zum Starten.
   */
  public static void main(String[] args) {

      // Eine neue Instanz des OrderWS erzeugen, diese wird dann mittels
      // deployment-path publiziert.
      OrderWSServer mw = new OrderWSServer();
      Endpoint endpointClock = Endpoint.publish(DEPLOYMENT_PATH, mw);
      System.out.println("Order Webservice running");
  }

  /**
   * Die Methode placeOrder nimmt Bestellanfragen (als XML-String) entgegen
   * und reicht diese an die Instanz von PurchaseOrder weiter. Zuvor prüft
   * sie, ob es sich bei dem XML-String um ein gültiges Order-Objekt handelt.
   * 
   * @param pStrUsername
   *            Benutzername (GBI-XXX)
   * @param pStrPassword
   *            Passwort
   * @param pStrXml
   *            Order-Objekt als XML-String
   * @return Bestellnummer oder Fehlermeldung
   */
  @WebMethod(action = "placeOrder")
  @WebResult(name = "success")
  public String placeOrder(String username, String password, String xml) {

    // XML-String mittels XStream deserialisieren
    XStream xstream = new XStream();
    Object obj = xstream.fromXML(xml);
    Order order = null;
    if (obj instanceof Order)
        order = (Order) obj;
    else
        return "Failed (no Order object)";

    // Die Bestellung durch die Klasse PurchaseOrder abwickeln lassen
    PurchaseOrder po = new PurchaseOrder();
    String ret = po.orderMaterials(username, password, order);

    return ret;
  }

}
