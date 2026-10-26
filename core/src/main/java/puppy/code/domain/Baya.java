package puppy.code.domain;

/** Recolectable: baya normal o un orbe con pasiva, sin clase extra. */
public final class Baya extends EntidadCayendo {
    private final Pasiva pasiva;

    public Baya(float x, float y, float velocidad) {
        super(x, y, 0, -velocidad, 10);
        pasiva = null;
    }

    public Baya(float x, float y, Pasiva pasiva) {
        super(x, y, 0, -110, 13);
        if (pasiva == null) throw new IllegalArgumentException("Pasiva requerida");
        this.pasiva = pasiva;
    }

    @Override protected void aplicarEfecto(Jugador jugador) {
        if (pasiva == null) jugador.curar(10);
        else jugador.activarPasiva(pasiva);
    }
    @Override public Elemento getElemento() { return Elemento.PLANTA; }
    @Override public boolean esRecolectable() { return true; }
    @Override public Pasiva getPasiva() { return pasiva; }
    @Override public int puntosAlRecoger(Jugador jugador) { return pasiva == null ? 40 : 60; }
}
