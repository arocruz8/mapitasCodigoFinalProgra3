package estructurasDatos;

public class NodoBinario<T extends Comparable<T>> {
    private T dato;
    private NodoBinario hi;
    private NodoBinario hd;
    NodoBinario(T dato){
    this.dato = dato;
    }
    public Object getDato(){
        return dato;
    }
        public NodoBinario getHD() {
        return hd;
    }

    public NodoBinario getHI() {
        return hi;
    }

    public void setHD(NodoBinario hd) {
        this.hd = hd;
    }

    public void setHI(NodoBinario hi) {
        this.hi = hi;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }
}
