package codigoMapas;

import java.awt.Image;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Places extends MapsJava {
    /*
    MalformedURLException sirve para poder obtener la url de forma correcta evitando los problemas
    que se puedan generar
    */
    
    
    /*
    se crean variables con los urls que permiten hacer los reques gracias a la libreria
    soup son los url base después se complementan con los datos que ingresa el usuario
    */
    private final String URLRoot="https://maps.googleapis.com/maps/api/place/search/xml";
    private final String URLDetails="https://maps.googleapis.com/maps/api/place/details/xml";
    private final String URLPhoto="https://maps.googleapis.com/maps/api/place/photo";
    
    private final String pathStatus="PlaceSearchResponse/status";
    private final String pathDetailsStatus="PlaceDetailsResponse/status";
    
    //permite guardar las fotos del lugar al que se le hizo request
    private ArrayList<String> photosReference;

    /*
    devuelve las fotos del ultimo request que se realizo
    */
    public ArrayList<String> getPhotosReference() {
        return photosReference;
    }
    
    /*
    permite guardar el dato de la puntuación que tiene el lugar
    */
    public enum Rankby{prominence,distance}
    
    @Override
    protected void onError(URL urlRequest, String status, Exception ex) {
        super.storageRequest(urlRequest.toString(), "Places request", status, ex);
    }

    @Override
    protected String getStatus(XPath xpath, Document document) {
        NodeList nodes;
        try {
            nodes = (NodeList) xpath.evaluate(this.pathStatus, 
                document, XPathConstants.NODESET);
            return nodes.item(0).getTextContent();
        } catch (Exception ex) {
            return null;
        }
    }
    
    /*
    guarda la información del request
    */
    @Override
    protected void storeInfoRequest(URL urlRequest, String info, String status, Exception exception) {
        super.storageRequest(urlRequest.toString(), info, status, exception);
    }
    
    /*
    guarda los detalles del lugar en una matriz [][] y e ciclo sirve para buscar
    el nodo que se guarda para posteriormente obtener su contenido
    */
    private String[][] getNodesPlaces(ArrayList<NodeList> nodes){
        String[][] result=new String[1000][6];
        for(int i = 0; i < nodes.size();i++){
             for (int j = 0, n = nodes.get(i).getLength(); j < n; j++) {
                String nodeString = nodes.get(i).item(j).getTextContent();
                result[j][i]=nodeString;
             }
        }
        result=(String[][])super.resizeArray(result, nodes.get(0).getLength());
        return result;
    }
    
    /*
    crea el url si se digitan las cordenadas geograficas con el metodo URLEncoder que com dije 
    anteriormte codifica la las variables con el link base
    */
    private URL createURL(double latitude, double longitude,int radius,String keyword,String namePlace,
            Rankby rankby,ArrayList<String> types) throws UnsupportedEncodingException, MalformedURLException{
        
        String _location= URLRoot + "?location=" + latitude + "," + longitude;
        String _radius= "";
        if(!rankby.equals(Rankby.distance)){
            _radius= "&radius=" + radius;
        }
      
        String _keyword="";
        if(keyword!=null && !keyword.isEmpty()){
            _keyword="&keyword=" +  URLEncoder.encode(keyword, "utf-8");
        }
        String _namePlace="";
        if(namePlace!=null && !namePlace.isEmpty()){
            _namePlace="&name=" + URLEncoder.encode(namePlace, "utf-8");
        }
        String _rankby="&rankby=" + rankby.toString();
        String _types="";
        if(types!=null && types.size()>0){
            _types="&types=";
            for(String item:types){
                _types+=item;
            }
        }
        URL urlReturn=new URL(_location + _radius + _keyword + _namePlace + _rankby +
                _types + super.getSelectPropertiesRequest() + "&key=" + MapsJava.getKey());
        return urlReturn;
    }
    
    /**
    devuelve la información del request del lugar que se pidio este dato[][] es de este tipo en 
    el primer [] lo que hace es regresar el lugar actual de busqueda que se solicita y el [] contiene
    la información asociada a este lugar, el uso del xpath lo que hace es  permite buscar información 
    dentro de un XML, navegar entre etiquetas y atributos, toda esta info se guarda en la NodeList para
    su posterior uso
    */
    public String[][] getPlaces(double latitude, double longitude,int radius,String keyword,String namePlace,
            Rankby rankby,ArrayList<String> types) throws UnsupportedEncodingException, MalformedURLException{
        URL url=createURL(latitude,longitude,radius,keyword,namePlace,rankby,types);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
            DocumentBuilder builder = factory.newDocumentBuilder(); 
            Document document = builder.parse(url.openStream()); 

            XPathFactory xpathFactory = XPathFactory.newInstance(); 
            XPath xpath = xpathFactory.newXPath();
            
            NodeList nodeName = (NodeList) xpath.evaluate("PlaceSearchResponse/result/name", 
                         document, XPathConstants.NODESET);
            NodeList nodeVicinity = (NodeList) xpath.evaluate("PlaceSearchResponse/result/vicinity", 
                         document, XPathConstants.NODESET);
            NodeList nodeLatitude = (NodeList) xpath.evaluate("PlaceSearchResponse/result/geometry/location/lat", 
                         document, XPathConstants.NODESET);
            NodeList nodeLongitude = (NodeList) xpath.evaluate("PlaceSearchResponse/result/geometry/location/lng", 
                         document, XPathConstants.NODESET);
            NodeList nodeIcon = (NodeList) xpath.evaluate("PlaceSearchResponse/result/icon", 
                         document, XPathConstants.NODESET);
            NodeList nodeReference = (NodeList) xpath.evaluate("PlaceSearchResponse/result/reference", 
                         document, XPathConstants.NODESET);
                
            ArrayList<NodeList> allNodes=new ArrayList<>();
            allNodes.add(nodeName);allNodes.add(nodeVicinity);allNodes.add(nodeLatitude);
            allNodes.add(nodeLongitude);allNodes.add(nodeIcon);allNodes.add(nodeReference);
            String[][] result=this.getNodesPlaces(allNodes);
            
            this.storeInfoRequest(url, "Places request", this.getStatus(xpath, document), null);
            return result;
        } catch (Exception e) {
            onError(url,"NO STATUS",e);
            return null;
        }
    }
    
    /*
    obtiene los detalles de la información del nodo que se buscó que correspone al 
    segundo [] del arreglo explicado anteriormente
    */
    private String[] getNodesDetails(ArrayList<NodeList> nodes){
        String[] result=new String[8];
        for(int i = 0; i < nodes.size();i++){
                try {
                    result[i]= nodes.get(i).item(0).getTextContent();
                } catch (Exception ex) {
                    result[i]= "NO DATA";
                }
        }
        return result;
    }
    
    
    protected String getStatusDetails(XPath xpath, Document document) {
        NodeList nodes;
        try {
            nodes = (NodeList) xpath.evaluate(this.pathDetailsStatus, 
                document, XPathConstants.NODESET);
            return nodes.item(0).getTextContent();
        } catch (Exception ex) {
            return null;
        }
    }
    
    /*
    crea el url si se busca con la referencia de un lugar en especifco
    */
    private URL createURL(String reference) throws UnsupportedEncodingException, MalformedURLException{
        URL urlReturn=new URL(URLDetails + "?reference=" + URLEncoder.encode(reference, "utf-8") + 
                super.getSelectPropertiesRequest() + "&key=" + MapsJava.getKey());
        return urlReturn;
    }
    
    /*
    Obtiene detalles de un local a partir de su referencia. La referencia se obtiene a través de una 
    búsqueda de places (getPlaces), ya que en los resultados devueltos el último lugar corresponde a 
    la referencia de un lugar [i][5].
    */
    public String[] getPlacesDetails(String referencePlace) throws UnsupportedEncodingException, MalformedURLException{
        URL url=createURL(referencePlace);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
            DocumentBuilder builder = factory.newDocumentBuilder(); 
            Document document = builder.parse(url.openStream()); 

            XPathFactory xpathFactory = XPathFactory.newInstance(); 
            XPath xpath = xpathFactory.newXPath();

            NodeList nodeName = (NodeList) xpath.evaluate("PlaceDetailsResponse/result/name", 
                         document, XPathConstants.NODESET);
            NodeList nodeVicinity = (NodeList) xpath.evaluate("PlaceDetailsResponse/result/vicinity", 
                         document, XPathConstants.NODESET);
            NodeList nodePhone = (NodeList) xpath.evaluate("PlaceDetailsResponse/result/formatted_phone_number", 
                         document, XPathConstants.NODESET);
            NodeList nodeAddress = (NodeList) xpath.evaluate("PlaceDetailsResponse/result/formatted_address", 
                         document, XPathConstants.NODESET);
            NodeList nodeUrlGoogle = (NodeList) xpath.evaluate("PlaceDetailsResponse/result/url", 
                         document, XPathConstants.NODESET);
            NodeList nodeRating = (NodeList) xpath.evaluate("PlaceDetailsResponse/result/rating", 
                         document, XPathConstants.NODESET);
            NodeList nodeIcon = (NodeList) xpath.evaluate("PlaceDetailsResponse/result/icon", 
                         document, XPathConstants.NODESET);
            NodeList nodeWebsite = (NodeList) xpath.evaluate("PlaceDetailsResponse/result/website", 
                         document, XPathConstants.NODESET);
            
            ArrayList<NodeList> allNodes=new ArrayList<>();
            allNodes.add(nodeName);allNodes.add(nodeVicinity);allNodes.add(nodePhone);
            allNodes.add(nodeAddress);allNodes.add(nodeUrlGoogle);allNodes.add(nodeRating);
            allNodes.add(nodeIcon);allNodes.add(nodeWebsite);
            
            String[] result=getNodesDetails(allNodes);
            
            this.storeInfoRequest(url, "Places details request", this.getStatusDetails(xpath, document), null);
            return result;
            
        } catch (Exception e) {
            onError(url,"NO STATUS",e);
            return null;
        }
    }
    
    /*
    obtiene el nodo con los detalles de las reviews del lugar buscado
    */
    private String[][] getNodesReview(ArrayList<NodeList> nodes){
        String[][] result=new String[1000][4];
        for(int i = 0; i < nodes.size();i++){
             for (int j = 0, n = nodes.get(i).getLength(); j < n; j++) {
                 String nodeString = nodes.get(i).item(j).getTextContent();
                 result[j][i]=nodeString;
             }
        }
        result=(String[][])super.resizeArray(result, nodes.get(0).getLength());
        return result;
    }
    
    /*
    obtiene las fotos del lugar que buscó las recorré y regresa
    el resulatdo si es el mismo del conenido
    */
    private ArrayList<String> getNodesPhoto(NodeList node){
       ArrayList<String> result=new ArrayList<>();
             for (int j = 0, n = node.getLength(); j < n; j++) {
                String nodeString = node.item(j).getTextContent();
                result.add(nodeString);
             }
        return result;
    }
    
    /*
    Obtiene las reviews detalladas de un local a partir de su referencia. La referencia se obtiene a 
    través de una búsqueda de places (getPlaces), ya que en los resultados devueltos el último lugar
    corresponde a la referencia de un lugar [i][5].
    */
    public String[][] getPlaceReview(String referencePlace) throws UnsupportedEncodingException, MalformedURLException{
        URL url=createURL(referencePlace);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
            DocumentBuilder builder = factory.newDocumentBuilder(); 
            Document document = builder.parse(url.openStream()); 

            XPathFactory xpathFactory = XPathFactory.newInstance(); 
            XPath xpath = xpathFactory.newXPath();

            NodeList nodeTime = (NodeList) xpath.evaluate("PlaceDetailsResponse/result/review/time", 
                         document, XPathConstants.NODESET);
            NodeList nodeAuthor= (NodeList) xpath.evaluate("PlaceDetailsResponse/result/review/author_name", 
                         document, XPathConstants.NODESET);
            NodeList nodeText = (NodeList) xpath.evaluate("PlaceDetailsResponse/result/review/text", 
                         document, XPathConstants.NODESET);
            NodeList nodeURL = (NodeList) xpath.evaluate("PlaceDetailsResponse/result/review/author_url", 
                         document, XPathConstants.NODESET);
            
            ArrayList<NodeList> allNodes=new ArrayList<>();
            allNodes.add(nodeTime);allNodes.add(nodeAuthor);allNodes.add(nodeText);
            allNodes.add(nodeURL);
            
            String[][] result=getNodesReview(allNodes);
            
            this.storeInfoRequest(url, "Places review request", this.getStatusDetails(xpath, document), null);
            
            NodeList nodePhoto = (NodeList) xpath.evaluate("PlaceDetailsResponse/result/photo/photo_reference", 
                         document, XPathConstants.NODESET);
            this.photosReference=this.getNodesPhoto(nodePhoto);
            
            return result;
            
        } catch (Exception e) {
            onError(url,"NO STATUS",e);
            return null;
        }
    }
    
    private URL createURL(String photoreference,int maxWidth) throws MalformedURLException{
        URL urlReturn=new URL(URLPhoto + "?maxwidth=" + maxWidth + "&photoreference=" + 
                photoreference + super.getSelectPropertiesRequest() + "&key=" + MapsJava.getKey());
        return urlReturn;
    }
    
    /*
    obtiene las fotos del lugar usando photoreference
    */
    public Image getPhoto(String photoreference,int maxWidth) throws MalformedURLException{
        URL url=createURL(photoreference,maxWidth);
        try {
            Image imageReturn;
            imageReturn=ImageIO.read(url);
            storeInfoRequest(url,"Photo request","OK",null);
            return imageReturn;
        } catch (Exception e) {
            onError(url, "NO STATUS", e);
            return null;
        }
    }
}

