package estructurasDatos;

public class Nodo{

    public int vertice;        //Almacena en dato del vértice
    public int peso;        //Almacena el peso de la arista

    //Constructor de nodo vacío
    public Nodo(){
        this.vertice = 0;
        this.peso = 0;
    }//fin del constructor Nodo


    //Constructor de nodo con el dato del vértice destino y el peso de la arista
    public Nodo(int v, int p){
        this.vertice = v;
        this.peso = p;
    }

    public String aCadena(){
        return "|" + this.vertice + "|" + this.peso + "|->";
    }//fin método aCadena
}//fin class Nodo
