package codigoMapas;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/*
clase base que permite acceder al uso de los mapas
*/
public abstract class MapsJava {

    //request properties 
    private static int connectTimeout=300;
    private static String region="es";
    private static String language="es";
    private static Boolean sensor= Boolean.FALSE;
    private static String APIKey="";
    
    //Stock request
    private static String[][] stockRequest=new String[0][6];

    /*metodos abstractos que permiten acceder a la información sin que ocurra errores, 
    además de almacenarla y oltener su estatus*/
    protected abstract void onError(URL urlRequest,String status,Exception ex);
    protected abstract String getStatus(XPath xpath, Document document);
    protected abstract void storeInfoRequest(URL urlRequest,String info,String status,Exception exception);
    

    /*
    metodos protegidos de la clase
    */
    private static int numRequest=0;
    
    /*
    esta función se encarga de almacenar los request 
    */
    protected void storageRequest(String urlRequest,String info,String status,
            Exception exception){
        Date date = new Date();
        numRequest+=1;
        MapsJava.stockRequest=(String[][])(this.resizeArray(MapsJava.stockRequest,numRequest));
        if(MapsJava.stockRequest[numRequest-1]==null){
                MapsJava.stockRequest[numRequest-1]=new String[6];
            }
        MapsJava.stockRequest[numRequest-1][0]=String.valueOf(numRequest);
        MapsJava.stockRequest[numRequest-1][1]=date.toString();
        MapsJava.stockRequest[numRequest-1][2]=status;
        MapsJava.stockRequest[numRequest-1][3]=urlRequest;
        MapsJava.stockRequest[numRequest-1][4]=info;
        if(exception==null){
            MapsJava.stockRequest[numRequest-1][5]="No exception";
        }else{
            MapsJava.stockRequest[numRequest-1][5]=exception.toString();
        } 
    }
    
    /*
    slecciona las propiedades de los requests 
    */
    protected String getSelectPropertiesRequest(){
        return "&region=" + MapsJava.region + "&language=" + MapsJava.language + 
                "&sensor=" + MapsJava.sensor;
    }
     protected ArrayList<String> getNodesString(NodeList node){
         ArrayList<String> result=new ArrayList<>();
             for (int j = 0, n = node.getLength(); j < n; j++) {
                String nodeString = node.item(j).getTextContent();
                result.add(nodeString);
             }
        return result;
    }
     
    protected ArrayList<Double> getNodesDouble(NodeList node){
         ArrayList<Double> result=new ArrayList<>();
             for (int j = 0, n = node.getLength(); j < n; j++) {
                String nodeString = node.item(j).getTextContent();
                result.add(Double.valueOf(nodeString));
             }
        return result;
    }
    
    protected ArrayList<Integer> getNodesInteger(NodeList node){
         ArrayList<Integer> result=new ArrayList<>();
             for (int j = 0, n = node.getLength(); j < n; j++) {
                String nodeString = node.item(j).getTextContent();
                result.add(Integer.valueOf(nodeString));
             }
        return result;
    }
    
    /*
    recalcula el tamaño del array 
    */
    protected Object resizeArray (Object oldArray, int newSize) {
       int oldSize = java.lang.reflect.Array.getLength(oldArray);
       Class elementType = oldArray.getClass().getComponentType();
       Object newArray = java.lang.reflect.Array.newInstance(
             elementType, newSize);
       int preserveLength = Math.min(oldSize, newSize);
       if (preserveLength > 0)
          System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
       return newArray; 
    }
    
 
    /*
    verfica que la llave del api exista si no es así mandara un error de uso
    */
    public static String APIkeyCheck(String key){
        try{
            URL url=new URL("https://maps.googleapis.com/maps/api/place/search/xml?location=0,0&radius=1000" + 
                    "&sensor=false&key=" + key);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
            DocumentBuilder builder = factory.newDocumentBuilder(); 
            Document document = builder.parse(url.openStream()); 
            XPathFactory xpathFactory = XPathFactory.newInstance(); 
            XPath xpath = xpathFactory.newXPath(); 

            NodeList nodeLatLng = (NodeList) xpath.evaluate("PlaceSearchResponse/status", 
                            document, XPathConstants.NODESET);
            String status = nodeLatLng.item(0).getTextContent();
            return status;
        }catch (Exception e){
            return "NO STATUS";
        }
    }
    
    //set y gets 
    //metodos set y get de los métodos de tiempo de espera
    public static int getConnectTimeout() {
        return connectTimeout;
    }
   
    public static void setConnectTimeout(int aConnectTimeout) {
        connectTimeout = aConnectTimeout;
    }
    
    /*
    regresa la region de busqueda que ingreso l usuario normalmente se usa .cr para nuestro país
     */
    public static String getRegion() {
        return region;
    }
    
    /*
    establece la región de busqueda
    */
    public static void setRegion(String aRegion) {
        region = aRegion;
    }

    /*
    regresa el idioma que establecio el usuario
     */
    public static String getLanguage() {
        return language;
    }
    /*
    define el idioma
    */
    public static void setLanguage(String aLanguage) {
        language = aLanguage;
    }

    /*
    variable verifica si el dispositvo que usa tiene un sensor(gps)
     */
    public static Boolean getSensor() {
        return sensor;
    }
    
    public static void setSensor(Boolean aSensor) {
        sensor = aSensor;
    }
    
    /*
    obtiene la api key
    */
    public static String getKey() {
        return APIKey;
    }

    public static void setKey(String aKey) {
        APIKey = aKey;
    }
    
    
    /*
    Obtiene registro de todas las peticiones HTTP realizadas. Conforma un String[n][6] con la siguiente estructura: 
       [0][0]="Número de petición";<br/
       [0][1]="Fecha/hora petición";<br/>
       [0][2]="status de la petición";<br/>
       [0][3]="URL de la petición";<br/>
       [0][4]="Información sobre petición realizada";<br/>
       [0][5]="Excepciones generadas";<br/>
    @return devuelve un array de dos dimensiones con las diferentes peticiones realizadas
     */
    public static String[][] getStockRequest() {
        return stockRequest;
    }

    /*
     * Obtiene registro de la última petición HTTP realizada. Conforma un String[6] con la siguiente estructura:</br>
     * [0]="Número de petición";<br/>[1]="Fecha/hora petición";<br/>[2]="status de la petición";<br/>
     * [3]="URL de la petición";<br/>[4]="Información sobre petición realizada";<br/>[5]="Excepciones generadas";<br/>
     * @return array de una dimensión con la última petición realizada
     */
    public static  String[] getLastRequestRequest() {
        String[] stockRequestTemp=new String[6];
        System.arraycopy(stockRequest[stockRequest.length-1], 0, stockRequestTemp, 0, 6);
        return stockRequestTemp;
    }
    
    /*
    devuelve el status de la última petición
    */
    public static String getLastRequestStatus() {
         return stockRequest[stockRequest.length-1][2];
    }
    
    /*
    obtiene el url del request que se hizo
    */
    public static String getLastRequestURL() {
        return stockRequest[stockRequest.length-1][3];
    }
    
    /*
    regresa la información del request
     */
    public static String getLastRequestInfo() {
         return stockRequest[stockRequest.length-1][4];
    }
    
    /*
    obtiene la información del último request hecho que pueda tener algún error
     */
    public static String getLastRequestException() {
         return stockRequest[stockRequest.length-1][5];
    }
  
}
