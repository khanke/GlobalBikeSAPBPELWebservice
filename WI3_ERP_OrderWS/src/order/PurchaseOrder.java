package order;

import java.util.Date;
import java.util.regex.Pattern;

import loginData.LoginData;
import materialstamm.Material;
import bapi.BAPI;

import com.sap.mw.jco.JCO;

import connections.Connection;

/**
 * PurchaseOrder-Klasse: Führt die Bestellung im SAP per JCO durch.
 */
public class PurchaseOrder extends BAPI {
	
	JCO.Client SAPConnection;	
	
	
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
	
	/**
	 * Bestellung aufgeben
	 * @param pStrUsername Benutzername (GBI-XXX)
	 * @param pStrPassword Passwort
	 * @param pOrder Bestellung als Order-Objekt
	 * @return Bestellnummer bzw. Fehlermeldung
	 */
	public String orderMaterials(String pStrUsername, String pStrPassword, Order pOrder) {
	
		// check for correct formatted username (gbi-###)
		if(! Pattern.matches("[gG][bB][iI]-\\d{3}+", pStrUsername))
			return "CHECK_USERNAME_ERROR";
		
		if (! connectToSAP(pStrUsername, pStrPassword)) {
			return "CONNECTION_FAILED";
		}
		
		
		// the resulting purchase order number
		String PO_NUM = null;
		
		try 
		{
			myFunction = this.createFunction("BAPI_PO_CREATE", SAPConnection);
			
			JCO.Structure PO_HDR = myFunction.getImportParameterList().getStructure("PO_HEADER");
			JCO.Table PO_LITEMS = myFunction.getTableParameterList().getTable("PO_ITEMS");
			JCO.Table PO_ITM_SCH = myFunction.getTableParameterList().getTable("PO_ITEM_SCHEDULES");
			
			
			PO_HDR.setValue(pOrder.getMaterial().getPurchaseOrg(),"PURCH_ORG");
			PO_HDR.setValue(pOrder.getMaterial().getPurchaseGrp(),"PUR_GROUP");
			PO_HDR.setValue(pOrder.getMaterial().getVendor(), "VENDOR");
			PO_HDR.setValue("NB","DOC_TYPE");
			
			PO_LITEMS.appendRow();
			PO_LITEMS.setValue("10","PO_ITEM");
			PO_LITEMS.setValue(pOrder.getMaterial().getNumber(),"PUR_MAT");
			PO_LITEMS.setValue(pOrder.getMaterial().getPlant(),"PLANT");
			PO_LITEMS.setValue("EA", "UNIT");
			PO_LITEMS.setValue(String.valueOf(pOrder.getMaterial().getPrice()), "NET_PRICE");
			
			PO_ITM_SCH.appendRow();
			PO_ITM_SCH.setValue("10", "PO_ITEM");
			PO_ITM_SCH.setValue(new Date(), "DELIV_DATE");
			PO_ITM_SCH.setValue(pOrder.getQty(), "QUANTITY");
		
			this.SAPConnection.execute(myFunction);
			
			JCO.Table returnTable = myFunction.getTableParameterList().getTable("RETURN");
			
			for (int i=0; i<returnTable.getNumRows(); i++) {
				returnTable.setRow(i);
				System.out.println(returnTable.getString("TYPE") + 
						" " + returnTable.getString("MESSAGE"));
			}
			
			PO_NUM = myFunction.getExportParameterList().getString("PURCHASEORDER");
			
		}
		catch (Exception e) {
			//e.printStackTrace();
		}
		finally {
			disconnectFromSAP();
		}
		
		if (PO_NUM == null || PO_NUM.equals(""))
			return "CREATE_PURCHASE_FAILED";
		else
			return "SUCCESS. Bestellnummer: " + PO_NUM;
	}
	
}