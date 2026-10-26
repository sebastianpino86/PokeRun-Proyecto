package puppy.code.domain;

/** Strategy para los tres elementos iniciales: fuego, agua y planta. */
public final class TipoElemental implements ComportamientoElemento {
    private final Elemento elemento;

    public TipoElemental(Elemento elemento) {
        if (elemento == null || elemento == Elemento.ELECTRICO)
            throw new IllegalArgumentException("Elemento básico inválido");
        this.elemento = elemento;
    }

    @Override public Elemento getElemento() { return elemento; }

    @Override public int calcularImpacto(Elemento ataque, int danioBase) {
        if (ataque == elemento) return 5;
        boolean debilidad = (elemento == Elemento.FUEGO && ataque == Elemento.AGUA)
                || (elemento == Elemento.AGUA && ataque == Elemento.PLANTA)
                || (elemento == Elemento.PLANTA && ataque == Elemento.FUEGO);
        return -Math.round(danioBase * (debilidad ? 1.4f : 1f));
    }
}
