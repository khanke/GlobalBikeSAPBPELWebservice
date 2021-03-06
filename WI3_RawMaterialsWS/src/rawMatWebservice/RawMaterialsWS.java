
package rawMatWebservice;

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
@WebService(name = "RawMaterialsWS", targetNamespace = "http://rawMatWebservice/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface RawMaterialsWS {


    /**
     * 
     * @param password
     * @param username
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "getRawMaterials")
    @WebResult(name = "materials", partName = "materials")
    @Action(input = "getRawMaterials", output = "http://rawMatWebservice/RawMaterialsWS/getRawMaterialsResponse")
    public String getRawMaterials(
        @WebParam(name = "username", partName = "username")
        String username,
        @WebParam(name = "password", partName = "password")
        String password);

}
