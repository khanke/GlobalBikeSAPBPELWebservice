package rawMatWebservice;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Endpoint;

import com.sap.mw.jco.JCO;
import com.thoughtworks.xstream.XStream;

import connections.Connection;
import loginData.LoginData;
import materialstamm.Material;

@WebService(name = "RawMaterialsWS", targetNamespace = "http://rawMatWebservice/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class RawMaterialsWSServer implements RawMaterialsWS {
  
  JCO.Client SAPConnection;   
  
  public final static String DEPLOYMENT_PATH = "http://localhost:8081/RawMaterialsWS";
  
  /**
   * Hauptprogramm zum Starten.
   */
  public static void main(String[] args) {

      // Eine neue Instanz des OrderWS erzeugen, diese wird dann mittels
      // deployment-path publiziert.
      RawMaterialsWSServer mw = new RawMaterialsWSServer();
      Endpoint endpointClock = Endpoint.publish(DEPLOYMENT_PATH, mw);
      System.out.println("RawMaterials Webservice running");
  }
  
  @WebMethod(action = "getRawMaterials")
  @WebResult(name = "success")
  public String getRawMaterials(String username, String password) {
        
    // check for correct formatted username (gbi-###)
    if(! Pattern.matches("[gG][bB][iI]-\\d{3}+", username))
        return "CHECK_USERNAME_ERROR";
    
    if (! connectToSAP(username, password)) {
        return "CONNECTION_FAILED";
    }
    
    List<Material> materialliste = new ArrayList<>();
    
    Material mat = new Material("BOLT1040", "desc");
    mat.setPrice(42.0);
    mat.setCurrency("EUR");
    mat.setType("ROH");
    mat.setPlant("HD00");
    mat.setPurchaseOrg("DE00");
    mat.setPurchaseGrp("E00");
    mat.setVendor("0000122022");
    
    XStream xstream = new XStream();
    return xstream.toXML(materialliste);
  }
  
  /**
   * Eine Verbindung mit dem SAP-System per JCO herstellen.
   * @param pStrUsername Benutzername (GBI-XXX)
   * @param pStrPassword Passwort
   * @return Gibt Success(True), Fehler(False) zurück.
   */
  private boolean connectToSAP(String pStrUsername, String pStrPassword) {
      Connection conn = new Connection();
      
      try {
          this.SAPConnection = conn.openConnectionToSAP(
                  LoginData.Mandant,
                  pStrUsername,
                  pStrPassword,
                  LoginData.Lang,
                  LoginData.ConnString,
                  LoginData.Instanznummer);
      } catch (Exception e) {
          System.out.println(e.getMessage() + "\n" + e.getCause());
      }

      if (this.SAPConnection != null && this.SAPConnection.isAlive())
          return true;
      else
          return false;
  }
  
  /**
   * Die Verbindung mit dem SAP-System trennen.
   */
  private void disconnectFromSAP() {
      JCO.releaseClient(this.SAPConnection);
  }

}
