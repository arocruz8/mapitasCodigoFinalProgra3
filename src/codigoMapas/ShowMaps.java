package codigoMapas;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.xml.xpath.XPath;
import org.w3c.dom.Document;


public class ShowMaps extends maps.java.MapsJava{
    private String URLRoot="http://maps.google.es/maps?q=";

    @Override
    protected void onError(URL urlRequest, String status, Exception ex) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    protected String getStatus(XPath xpath, Document document) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    protected void storeInfoRequest(URL urlRequest, String info, String status, Exception exception) {
                super.storageRequest(urlRequest.toString(), "Map request", "OK", null);
    }
    
    /*
     Crea la URL con la dirección del mapa web asociado a dicha dirección
     */
    public String getURLMap(String address) throws MalformedURLException, UnsupportedEncodingException{
         URL urlReturn=new URL(URLRoot + URLEncoder.encode(address, "utf-8") + "&output=embed");
         this.storeInfoRequest(urlReturn,null,null,null);
         return urlReturn.toString();
    }
    
    /*
     Crea la URL con la coordenada geográfica del mapa web asociado 
     */
    public String getURLMap(Double latitude, Double longitude) throws MalformedURLException{
        URL urlReturn=new URL(URLRoot + String.valueOf(latitude)+ "%2C" + String.valueOf(longitude) + "&output=embed");
         this.storeInfoRequest(urlReturn,null,null,null);
         return urlReturn.toString();
    }
}
