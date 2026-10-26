package puppy.code.domain;

/** Tipo primario de combate. Las formas de esta campaña conservan su afinidad. */
public enum Elemento {
  FUEGO("Fuego", "F"),
  AGUA("Agua", "A"),
  PLANTA("Planta", "P"),
  ELECTRICO("Eléctrico", "E");
  private final String nombre;
  private final String simbolo;

  Elemento(String nombre, String simbolo) {
    this.nombre = nombre;
    this.simbolo = simbolo;
  }

  public String getNombre() {
    return nombre;
  }

  public String getSimbolo() {
    return simbolo;
  }
}
