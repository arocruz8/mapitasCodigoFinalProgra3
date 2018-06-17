
package codigoMapas;

import java.awt.Dimension;
import java.awt.Image;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.imageio.ImageIO;
import javax.xml.xpath.XPath;
import org.w3c.dom.Document;

public class StaticMaps extends maps.java.MapsJava {
    private final String URLRoot="http://maps.googleapis.com/maps/api/staticmap";
    
    /*
    se define que tipo de formato va utilizar png,png32,gif,jpg,jpg
    */
    public enum Format{png,png32,gif,jpg,jpg_baseline}
    
    /*
    define el mapa que se va a mostrar ya sea terrestre, satelital, hibrido, etc
     */
    public enum Maptype{roadmap, satellite, hybrid,terrain}
    
    @Override
    protected void onError(URL urlRequest, String status, Exception ex) {
        super.storageRequest(urlRequest.toString(), "Static maps request", "NO STATUS", ex);
    }

    @Override
    protected String getStatus(XPath xpath, Document document) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void storeInfoRequest(URL urlRequest, String info, String status, Exception exception) {
        super.storageRequest(urlRequest.toString(), "Static maps request", "OK", exception);
    }
    
    /*
    crea el mapa estatico con la dirección ingresada por el usurio
    */
    public Image getStaticMap(String centerAddress,int zoom,Dimension size,int scale,Format format, Maptype maptype) throws MalformedURLException, UnsupportedEncodingException{
        URL url=new URL(URLRoot + "?center=" + URLEncoder.encode(centerAddress, "utf-8") + "&zoom=" + zoom +
                "&size=" + size.width + "x" + size.height + "&scale=" + scale +
                "&format=" + format.toString() + "&maptype=" + maptype.toString() + 
                "&markers=" + URLEncoder.encode(centerAddress, "utf-8") + super.getSelectPropertiesRequest());
        try {
            Image imageReturn;
            imageReturn=ImageIO.read(url);
            storeInfoRequest(url,null,null,null);
            return imageReturn;
        } catch (Exception e) {
            onError(url, "NO STATUS", e);
            return null;
        }
    }
}
