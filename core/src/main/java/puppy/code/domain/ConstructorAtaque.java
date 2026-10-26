package puppy.code.domain;

public final class ConstructorAtaque implements AtaqueBuilder {
  private float x, y, horizontal, caida, radio;
  private int danio;
  private Elemento tipo;
  private Tecnica tecnica;
  private boolean tienePosicion, tieneMovimiento, tieneCombate;

  @Override
  public void reset() {
    x = y = horizontal = caida = radio = 0;
    danio = 0;
    tipo = null;
    tecnica = null;
    tienePosicion = tieneMovimiento = tieneCombate = false;
  }

  @Override
  public void posicion(float x, float y) {
    this.x = x;
    this.y = y;
    tienePosicion = true;
  }

  @Override
  public void movimiento(float horizontal, float caida) {
    this.horizontal = horizontal;
    this.caida = caida;
    tieneMovimiento = true;
  }

  @Override
  public void combate(Elemento tipo, int danio, float radio) {
    this.tipo = tipo;
    this.danio = danio;
    this.radio = radio;
    tieneCombate = true;
  }

  @Override
  public void tecnica(Tecnica tecnica) { this.tecnica = tecnica; }

  @Override
  public Ataque getResult() {
    if (!tienePosicion || !tieneMovimiento || !tieneCombate)
      throw new IllegalStateException("Construcción incompleta");
    if (tipo == null
        || danio <= 0
        || danio > 30
        || !Float.isFinite(caida)
        || caida < 20
        || caida > 500
        || !Float.isFinite(radio)
        || radio < 4
        || radio > 20
        || !Float.isFinite(horizontal)
        || Math.abs(horizontal) > 100)
      throw new IllegalArgumentException("Estadísticas de ataque inválidas");
    Tecnica elegida = tecnica == null ? Tecnica.para(tipo, 1) : tecnica;
    if (elegida.getElemento() != tipo) throw new IllegalArgumentException("Técnica de otro elemento");
    Ataque resultado = new Ataque(x, y, horizontal, caida, radio, tipo, danio, elegida);
    reset();
    return resultado;
  }
}
