package codigoMapas;

import java.awt.geom.Point2D;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class Geocoding extends MapsJava{
    /*
    se crea la variable con el url para poder hacer el request de la golocalización 
    */
    private final String URLRoot="http://maps.google.com/maps/api/geocode/xml";
    private final String pathStatus="GeocodeResponse/status";
    private final String pathPostalcode="GeocodeResponse/result/address_component";
    
    private String addressFound;
    private String postalcode;
    
    /*
    devuele la dirrreción que sen encontro
     */
    public String getAddressFound() {
        return addressFound;
    }
    
    /*
    devuelve la dirección del codigo postal del lugar
     */
    public String getPostalcode() {
        return postalcode;
    }

    @Override
    protected void onError(URL urlRequest, String status, Exception ex) {
        super.storageRequest(urlRequest.toString(), "Geocoding request", status, ex);
    }

    @Override
    protected String getStatus(XPath xpath, Document document) {
        NodeList nodes;
        try {
            nodes = (NodeList) xpath.evaluate(this.pathStatus, 
                document, XPathConstants.NODESET);
            return nodes.item(0).getTextContent();
        } catch (XPathExpressionException ex) {
            return null;
        }
    }
    
    //guarda la información 
    @Override
    protected void storeInfoRequest(URL urlRequest, String info, String status, Exception exception) {
        super.storageRequest(urlRequest.toString(), "Geocoding request", status, exception);
    }
    
    //guarda la info del lugar en el nodo de tipo NodoList 
    private String getNodesPostalcode(NodeList node){
         String result="No data";
         int i=0;
         while (i<node.getLength()) {
            String nodeString = node.item(i).getTextContent();
            if(nodeString.contains("postal_code")){
                result=nodeString.replace(" ", "").substring(1,6);
                break;
            }
            i+=1;
        }
        return result;
    }
    
    /*
    crea los url dependiendo de que digito el usuario si las coorddenadas o texto para su posterior busqueda
    */
    private URL createURL(String address) throws UnsupportedEncodingException, MalformedURLException{
        URL urlReturn=new URL(URLRoot + "?address=" + URLEncoder.encode(address, "utf-8") + super.getSelectPropertiesRequest());
        return urlReturn;
    }
    
    private URL createURL(Double latitude, Double longitude) throws UnsupportedEncodingException, MalformedURLException{
        URL urlReturn=new URL(URLRoot + "?latlng=" + latitude + "," + longitude + super.getSelectPropertiesRequest());
        return urlReturn;
    }
    
    /*
    transforma la dirección de tipo string en las coordenadas de latitud y longitud
    */
    public Point2D.Double getCoordinates(String address) throws UnsupportedEncodingException, MalformedURLException{
        this.addressFound="";
        URL url=createURL(address);
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
                DocumentBuilder builder = factory.newDocumentBuilder(); 
                Document document = builder.parse(url.openStream()); 

                XPathFactory xpathFactory = XPathFactory.newInstance(); 
                XPath xpath = xpathFactory.newXPath(); 

                NodeList nodeLatLng = (NodeList) xpath.evaluate("GeocodeResponse/result/geometry/location[1]/*", 
                         document, XPathConstants.NODESET);
                NodeList nodeAddress = (NodeList) xpath.evaluate("GeocodeResponse/result/formatted_address", 
                         document, XPathConstants.NODESET);
                NodeList nodePostal = (NodeList) xpath.evaluate(this.pathPostalcode, 
                         document, XPathConstants.NODESET);
                
                Double lat=0.0;
                Double lng=0.0;
                try {
                    this.postalcode=this.getNodesPostalcode(nodePostal);
                    this.addressFound="No data";
                    this.addressFound=nodeAddress.item(0).getTextContent();
                    lat = Double.valueOf(nodeLatLng.item(0).getTextContent());
                    lng = Double.valueOf(nodeLatLng.item(1).getTextContent());
                } catch (Exception e) {
                     onError(url,"NO STATUS",e);
                }
                
                Point2D.Double result = new Point2D.Double(lat, lng);
                this.storeInfoRequest(url, null, this.getStatus(xpath, document), null);
                return result;
            } catch (Exception e) {
                onError(url,"NO STATUS",e);
                return null;
            }
     }
    
    /*
    convierte la dirección dada en cordenadas a texto que pasa a ser usado en una variable tipo string
    */
    public ArrayList<String> getAddress(Double latitude, Double longitude) throws UnsupportedEncodingException, MalformedURLException{
        URL url=createURL(latitude,longitude);
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
                DocumentBuilder builder = factory.newDocumentBuilder(); 
                Document document = builder.parse(url.openStream()); 

                XPathFactory xpathFactory = XPathFactory.newInstance(); 
                XPath xpath = xpathFactory.newXPath(); 

                NodeList nodeAddress = (NodeList) xpath.evaluate("GeocodeResponse/result/formatted_address", 
                         document, XPathConstants.NODESET);
                NodeList nodePostal = (NodeList) xpath.evaluate(this.pathPostalcode, 
                         document, XPathConstants.NODESET);
                
                ArrayList<String> result=super.getNodesString(nodeAddress);
                this.postalcode=this.getNodesPostalcode(nodePostal);
                
                this.storeInfoRequest(url, null, this.getStatus(xpath, document), null);
                return result;
            } catch (Exception e) {
                onError(url,"NO STATUS",e);
                return null;
            }
        }
   }
