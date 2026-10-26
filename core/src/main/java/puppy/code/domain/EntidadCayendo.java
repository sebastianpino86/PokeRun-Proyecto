package puppy.code.domain;

/**
 * Template Method: comprobar vigencia, detectar impacto, aplicar efecto y consumir exactamente una
 * vez.
 */
public abstract class EntidadCayendo {
  private float x, y, xAnterior, yAnterior;
  private final float velocidadX, velocidadY, radio;
  private boolean consumida;

  protected EntidadCayendo(float x, float y, float vx, float vy, float radio) {
    if (!Float.isFinite(x)
        || !Float.isFinite(y)
        || !Float.isFinite(vx)
        || !Float.isFinite(vy)
        || !Float.isFinite(radio)
        || radio <= 0) throw new IllegalArgumentException("Entidad inválida");
    this.x = this.xAnterior = x;
    this.y = this.yAnterior = y;
    velocidadX = vx;
    velocidadY = vy;
    this.radio = radio;
  }

  public final void actualizar(float dt) {
    if (!Float.isFinite(dt) || dt < 0) throw new IllegalArgumentException("Tiempo inválido");
    xAnterior = x;
    yAnterior = y;
    x += velocidadX * dt;
    y += velocidadY * dt;
  }

  public final boolean procesarColision(Jugador jugador) {
    if (consumida || jugador.getPs() == 0
        || !Caja.barrido(
            xAnterior, yAnterior, x, y, radio, jugador.getHitboxAnterior(), jugador.getHitbox()))
      return false;
    aplicarEfecto(jugador);
    consumida = true;
    return true;
  }

  protected abstract void aplicarEfecto(Jugador jugador);

  public abstract Elemento getElemento();

  public abstract boolean esRecolectable();

  public Pasiva getPasiva(){return null;}
  public int puntosAlRecoger(Jugador jugador){return esRecolectable()?40:getElemento()==jugador.getElemento()?25:0;}
  public boolean beneficia(Jugador jugador){return esRecolectable()||getElemento()==jugador.getElemento();}
  /** Se llama después de actualizar para que el barrido conserve la posición previa real. */
  public final void atraerHacia(Jugador jugador,float dt){
    if(!beneficia(jugador))return;
    float dx=jugador.getX()-x,dy=Jugador.Y-y;
    if(dx*dx+dy*dy<150*150){float factor=Math.min(1,dt*3.5f);x+=dx*factor;y+=dy*factor;}
  }

  public final void consumir() {
    consumida = true;
  }

  public final boolean estaConsumida() {
    return consumida;
  }

  public final boolean estaFuera() {
    return y < 60 - radio || x < 70 || x > 890;
  }

  public final float getX() {
    return x;
  }

  public final float getY() {
    return y;
  }

  public final float getXAnterior() {
    return xAnterior;
  }

  public final float getYAnterior() {
    return yAnterior;
  }

  public final float getRadio() {
    return radio;
  }

  public final float getVelocidadX() { return velocidadX; }

  public final float getVelocidadY() {
    return velocidadY;
  }
}
