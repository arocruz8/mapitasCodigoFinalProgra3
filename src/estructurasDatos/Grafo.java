package estructurasDatos;

import java.util.*;

public class Grafo{
    public LinkedList <NodoVertice> vertices;  //Lista de vértices del grafo


    //Constructor del grafo
    public Grafo(){
        this.vertices = new LinkedList<NodoVertice>();
    }

    //Método para agregar un vértice al grafo
    public void agregarVertice(int v){
        vertices.add(new NodoVertice(v));  //Agrega el vértice al final de la lista de vértices
    }

    //Método para agregar una arista al grafo
    public void agregarArista(int origen, int destino, int peso){
        buscarVertice(origen).aristas.add(new Nodo(destino, peso));  //Buscar el vértice origen y agrega un nodo al final de la lista de aristas de ese vértice
    }

    //Método para buscar un vértice en la lista de vertices del grafo según el dato indicado
    public NodoVertice buscarVertice(int v){
        //Recorre la lista de vertices
        for(int indice = 0; indice < this.vertices.size(); indice++){
            if(this.vertices.get(indice).vertice == v)  //Si encuentra el vértice lo retorna
                return this.vertices.get(indice);
        }
        return null;
    }

    //Método para buscar una arista en la lista de aristas
    public Nodo buscarArista(int origen, int destino){
        NodoVertice temp = buscarVertice(origen);  //Busca el vértice de origen
        //Busca en la lista de aristas de ese vértice para identificar si se encuentra la arista buscada
        for(int i = 0; i < temp.aristas.size(); i++){
            if(temp.aristas.get(i).vertice == destino){
                return temp.aristas.get(i);
            }
        }
        return null;
    }


    //Método para buscar el primer vértice del grafo que esté sin visitar
    public NodoVertice buscarVerticeNOVisitado(){

        //Recorre la lista de vértices
        for(int indice = 0; indice < this.vertices.size(); indice++){
            if(!this.vertices.get(indice).visitado ){ //Si encuentra una no visitado lo retorna
                return this.vertices.get(indice);
            }
        }
        return null; //Si todos están visitados retorna null
    }

    //Método para imprimir el grafo recorriendo la lista de vértices y por cada vértice imprimiendo la lista de aristas que tiene
    public void imprimeGrafo(){
        NodoVertice actual;
        for(int i = 0; i < this.vertices.size(); i++){
            actual = vertices.get(i);
            System.out.println("Vertice : " + actual.vertice);
            for(int j = 0; j < actual.aristas.size(); j++){
                System.out.println("Arista de "+actual.vertice+ " a " + actual.aristas.get(j).vertice + " con peso " + actual.aristas.get(j).peso);
            }
        }
    }

    //Método para inicializar todos los nodos como no visitados
    public void inicializarVisitados(){
        for(int i = 0; i < this.vertices.size(); i++)
            this.vertices.get(i).visitado = false;
    }

    //Método para verificar si todos los nodos del grafo están visitados
    public boolean todosVisitados(){
        for(int i = 0; i < this.vertices.size(); i++){
            if(!this.vertices.get(i).visitado)    //Si encuentra uno sin visitar retorna false
                return false;
        }
        return true;    //Si encuentra uno visitado retorna true
    }

    //Método para eliminar un vértice del grafo
    public void eliminarVertice(int v){
        this.vertices.remove(buscarVertice(v));  //Elimina el vértice de la lista de vértices
        //Elimina todas las aristas en las que el vértice eliminado aparezca como destino
        for(int i = 0; i < vertices.size(); i++)
            eliminarArista(vertices.get(i).vertice, v);
    }

    //Método para eliminar una arista de un vértice origen a un vértice destino dado
    public void eliminarArista(int origen, int destino){
        //Verifica si el grafo es no dirigido
        if((buscarArista(origen,destino) != null) && (buscarArista(destino,origen) != null)){ //Busca si existe arista de origen a destino y viceversa
            //Verifica si el peso de ambas aristas es igual con lo que se asume que es un grafo no dirigido
            if(buscarArista(origen,destino).peso == buscarArista(destino,origen).peso){
                buscarVertice(destino).aristas.remove(buscarArista(destino, origen));
            }
        }
        //Elimina el arista de la lista de aristas del vértice origen
        buscarVertice(origen).aristas.remove(buscarArista(origen, destino));

    }

    //Método para realizar el recorrido en profundidad en el grafo
    public void recorridoProfundidad(int inicial){
        inicializarVisitados();  //Inicializa todos los nodos como no visitados
        System.out.println("Recorrido en profundidad");
        DFS(inicial); //Hace la llamada al método que realiza el algoritmo
        System.out.println();
    }



    private void DFS(int inicial){

        NodoVertice actual = buscarVertice(inicial); //Busca el vértice inicial para el recorrido en la lista de vértices

        while(actual != null)  //Ciclo para verificar que todos los vértices estén visitados
        {
            //Si el nodo inicial no está visitado
            if(!actual.visitado){
                System.out.print(actual.vertice + " "); //Imprime el vértice
                actual.visitado = true;    //Lo actualiza como visitado
            }

            //Revisa todos los nodos adyacentes del nodo recién visitado
            for(int j = 0; j < actual.aristas.size(); j++){
                if(! buscarVertice(actual.aristas.get(j).vertice).visitado)  //Si el nodo adyacente no está visitado
                    DFS(actual.aristas.get(j).vertice); //Hace la llamada recursiva para realizar el recorrido en el nodo adyacente
            }
            //Verifica si queda algún nodo sin visitar
            actual = buscarVerticeNOVisitado();
        }
    }

    //Método para recorrer el grafo en anchura
    public void recorridoAnchura(int inicial){

        NodoVertice actual;
        LinkedList <NodoVertice> Cola = new LinkedList<NodoVertice>();    //Estructura auxiliar para hacer el recorrido

        inicializarVisitados(); //Inicializa todos los nodos como no visitados

        System.out.println("Recorrido en anchura");
        actual = buscarVertice(inicial); //Busca el vértice para iniciar el recorrido en la lista de vértices del grafo

        //Ciclo para verificar que todos los nodos estén visitados
        while(actual != null)
        {
            //Si el nodo actual no ha sido visitado
            if(!actual.visitado){
                System.out.print(actual.vertice + " "); //Imprime el vértice
                actual.visitado = true; //Indica que el vértice fue visitado
                Cola.add(actual);  //Agrega el vértice a la cola(estructura auxiliar)
            }
            //Busca todos los vértices adyacentes al vértice recién visitado
            for(int j = 0; j < actual.aristas.size(); j++){
                if(! buscarVertice(actual.aristas.get(j).vertice).visitado){ //Si el vértice no se ha visitado aun
                    System.out.print(actual.aristas.get(j).vertice + " ");  //Se imprime el vértice
                    Cola.add(buscarVertice(actual.aristas.get(j).vertice));    //Se agrega el vértice a la cola
                    buscarVertice(actual.aristas.get(j).vertice).visitado = true;  //Se indica que el vértice ha sido visitado
                }
            }

            //Mientras la cola no esté vacía
            while(Cola.size() > 0)
            {
                NodoVertice temp1;

                for(int x = 0; x < Cola.getFirst().aristas.size(); x++){
                    //Obtiene el primer vértice en la cola
                    temp1 = buscarVertice(Cola.getFirst().aristas.get(x).vertice);
                    if(!temp1.visitado){  //Verifica si el primero de la cola no ha sido visitado
                        System.out.print(temp1.vertice + " "); //Imprime el vértice
                        temp1.visitado = true;    //Indica que el vértice se ha visitado
                        Cola.add(temp1);    //Agrega el vértice al final de la cola
                    }
                }
                //Elimina el primer vértice de la cola
                Cola.removeFirst();
            }
            actual = buscarVerticeNOVisitado();//Verifica si aun quedan vértices sin visitar
        }
        System.out.println();
    }
}//Fin de la clase
