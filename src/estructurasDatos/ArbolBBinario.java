package estructurasDatos;

public class ArbolBBinario <T extends Comparable<T>>{
    public NodoBinario<T> raiz;

    public ArbolBBinario(){
         raiz = null;
    }
    
    public void insertarNodo(T dato){
        NodoBinario nodoNuevo = new NodoBinario(dato);
        if (raiz == null){
            raiz = nodoNuevo;
        }else{
            NodoBinario nodoBd = raiz;
            NodoBinario padre;
        while(1==1){   
            padre = nodoBd;
            if(dato.compareTo((T) nodoBd.getDato())<0){
                nodoBd = nodoBd.getHI();
                if(nodoBd == null){
                    padre.setHI(nodoNuevo);
                 return;
                }
                
            }else{
                nodoBd = nodoBd.getHD();
                if(nodoBd == null){
                     padre.setHD(nodoNuevo);
                return; }
                }
            }
        }   
    }
    
    public NodoBinario buscar(T dato){
        NodoBinario nodoBD = raiz;
        while(dato.compareTo((T) nodoBD.getDato())!= 0){
           if(dato.compareTo((T) nodoBD.getDato())<0){
                nodoBD = nodoBD.getHI();
            }
            else{
                nodoBD = nodoBD.getHD();
            }
            if(nodoBD == null){
                System.out.println("NO ENCONTRADO");
                return null;
            }
        }
        System.out.println("encontrado");
        return nodoBD; 
        }
    
    public boolean eliminar(T dato){
        NodoBinario aux = raiz;
        NodoBinario padre = raiz;
        boolean esHI = true;
        while(dato.compareTo((T) aux.getDato())!=0 ){
            padre = aux;
            if(dato.compareTo((T) aux.getDato())<0){
                esHI = true;
                aux = aux.getHI();
            }else{
                esHI= false;
                aux = aux.getHD();
            }
            if (aux == null){
               return false; 
            }
        }
        if(aux.getHI()==null && aux.getHD()==null){
            if(aux == raiz){
                raiz = null;
            }else if(esHI){
                padre.setHI(null);
            }else{
                padre.setHD(null);
            }
            
        }else if(aux.getHD()==null){
            if(aux == raiz){
                raiz = aux.getHI();
            }else if(esHI){
                padre.setHI(aux.getHI());
            }else{
                padre.setHD(aux.getHD());
            }
            
        }else if(aux.getHI() == null){
           if(aux == raiz){
                raiz = aux.getHD();
            }else if(esHI){
                padre.setHI(aux.getHD());
            }else{
                padre.setHD(aux.getHI());
            }  
        }else{
            NodoBinario reemplazo = rotacion(aux);
            if(aux == raiz){
                raiz = reemplazo;
            }else if(esHI){
                padre.setHI(reemplazo);
            }else{
                padre.setHD(reemplazo);
            } 
            reemplazo.setHI(aux.getHI());
            
        }
    return true;    
    
    }
    
    public NodoBinario rotacion(NodoBinario nodoReem){
        NodoBinario reemplazarPadre = nodoReem;
        NodoBinario reemplazo = nodoReem;
        NodoBinario aux = nodoReem.getHD();
        while(aux!=null){
            reemplazarPadre = reemplazo;
            reemplazo = aux;
            aux.setHI(aux);
        }
        if(reemplazo != nodoReem.getHD()){
            reemplazarPadre.setHI(reemplazo.getHD());
            reemplazo.setHD(nodoReem.getHD());
        }
        
        return reemplazo;
    }
    
    public void inOrden(NodoBinario n){
      if(n != null){
          inOrden(n.getHI());
          System.err.println(n.getDato());
          inOrden(n.getHD());
      }  
    }
}
