package puppy.code.domain;

public enum Inicial {
  CHARMANDER(new TipoElemental(Elemento.FUEGO), "charmander", "charmeleon", "charizard"),
  SQUIRTLE(new TipoElemental(Elemento.AGUA), "squirtle", "wartortle", "blastoise"),
  BULBASAUR(new TipoElemental(Elemento.PLANTA), "bulbasaur", "ivysaur", "venusaur"),
  PIKACHU(new TipoElectrico(), "pikachu", "pikachu", "pikachu");
  private final ComportamientoElemento afinidad;
  private final String[] formas;

  Inicial(ComportamientoElemento afinidad, String... formas) {
    this.afinidad = afinidad;
    this.formas = formas;
  }

  public ComportamientoElemento getAfinidad() {
    return afinidad;
  }

  public String getForma(int nivel) {
    if (nivel < 1 || nivel > 3) throw new IllegalArgumentException("Nivel fuera de 1..3");
    return formas[nivel - 1];
  }

  public String getNombre(int nivel) {
    String forma = getForma(nivel);
    return Character.toUpperCase(forma.charAt(0)) + forma.substring(1);
  }
}
