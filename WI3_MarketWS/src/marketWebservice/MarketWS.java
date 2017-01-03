
package marketWebservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Action;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "MarketWS", targetNamespace = "http://marketWebservice/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface MarketWS {


    /**
     * 
     * @param materiallist
     * @param supplierNumber
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "updateRawMaterials")
    @WebResult(name = "success", partName = "success")
    @Action(input = "updateRawMaterials", output = "http://marketWebservice/MarketWS/updateRawMaterialsResponse")
    public String updateRawMaterials(
        @WebParam(name = "supplierNumber", partName = "supplierNumber")
        String supplierNumber,
        @WebParam(name = "materiallist", partName = "materiallist")
        String materiallist);

    /**
     * 
     * @param partNumber
     * @param budget
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "getBestOffer")
    @WebResult(partName = "return")
    @Action(input = "getBestOffer", output = "http://marketWebservice/MarketWS/getBestOfferResponse")
    public String getBestOffer(
        @WebParam(name = "partNumber", partName = "partNumber")
        String partNumber,
        @WebParam(name = "budget", partName = "budget")
        String budget);

}
