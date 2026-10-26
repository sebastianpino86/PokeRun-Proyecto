package puppy.code.domain;

/** Configuración inmutable. Distancia de campaña medida en metros de juego. */
public final class Nivel {
  private static final Nivel[] CAMPANIA = {
    new Nivel(1, "Pradera del Alba", 600, 105, 155, 1.10f, .78f, 10),
    new Nivel(2, "Bosque Prisma", 850, 165, 215, .78f, .56f, 12),
    new Nivel(3, "Santuario Varitipo", 1800, 220, 300, .56f, .38f, 14)
  };
  private final int numero, longitud, danio;
  private final String nombre;
  private final float velocidadInicial, velocidadFinal, intervaloInicial, intervaloFinal;

  private Nivel(
      int numero, String nombre, int longitud, float vi, float vf, float ii, float iff, int danio) {
    this.numero = numero;
    this.nombre = nombre;
    this.longitud = longitud;
    this.danio = danio;
    velocidadInicial = vi;
    velocidadFinal = vf;
    intervaloInicial = ii;
    intervaloFinal = iff;
  }

  public static Nivel numero(int numero) {
    if (numero < 1 || numero > 3) throw new IllegalArgumentException("Nivel fuera de 1..3");
    return CAMPANIA[numero - 1];
  }

  private float curva(float progreso) {
    float p = Math.max(0, Math.min(1, progreso));
    return p * p * (3 - 2 * p);
  }

  public float velocidad(float progreso, int ps, boolean ayuda) {
    return (velocidadInicial + (velocidadFinal - velocidadInicial) * curva(progreso))
        * (ps < 35 ? .88f : 1)
        * (ayuda ? .80f : 1);
  }

  public float intervalo(float progreso, int ps, boolean ayuda) {
    return (intervaloInicial + (intervaloFinal - intervaloInicial) * curva(progreso))
        * (ps < 35 ? 1.2f : 1)
        * (ayuda ? 1.18f : 1);
  }

  public int getDanio(boolean ayuda) {
    return ayuda ? Math.round(danio * .7f) : danio;
  }

  public int getNumero() {
    return numero;
  }

  public String getNombre() {
    return nombre;
  }

  public int getLongitud() {
    return longitud;
  }
}
