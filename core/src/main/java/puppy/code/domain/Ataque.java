package puppy.code.domain;

import java.util.Objects;

public final class Ataque extends EntidadCayendo {
  private final Elemento elemento;
  private final int danio;
  private final Tecnica tecnica;
  private float avisoRestante, avisoInicial;


  Ataque(float x, float y, float vx, float velocidad, float radio, Elemento elemento, int danio, Tecnica tecnica) {
    super(x, y, vx, -velocidad, radio);
    this.elemento = Objects.requireNonNull(elemento);
    this.danio = danio;
    this.tecnica = Objects.requireNonNull(tecnica);
  }

  @Override
  protected void aplicarEfecto(Jugador jugador) {
    jugador.recibir(elemento, danio);
  }

  @Override
  public Elemento getElemento() {
    return elemento;
  }

  @Override
  public boolean esRecolectable() {
    return false;
  }

  public Tecnica getTecnica() { return tecnica; }

  /** El ataque permanece suspendido mientras se advierte el punto de salida. */
  public void advertir(float segundos) {
    if (segundos < .8f) throw new IllegalArgumentException("Aviso muy breve");
    avisoInicial = avisoRestante = segundos;
  }
  public void actualizarAviso(float dt) { avisoRestante -= dt; }
  public boolean avisoTerminado() { return avisoRestante <= 0; }
  public float getRestante() { return avisoRestante; }
  public float getAnticipacion() { return avisoInicial; }
  public float getImpactoEn(float altura) {
    return getX() + getVelocidadX() * (getY() - altura) / -getVelocidadY();
  }
}
