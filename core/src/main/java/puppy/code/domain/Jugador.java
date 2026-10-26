package puppy.code.domain;

import java.util.Objects;

/** Experto en salud, movimiento y enfriamientos. No conoce pantalla, sonidos ni archivos. */
public final class Jugador {
  public static final int PS_MAXIMOS = 100;
  public static final float MIN_X = 114, MAX_X = 846, Y = 144;
  private final Inicial inicial;
  private final ComportamientoElemento afinidad;
  private float x = 480,
      xAnterior = 480,
      direccion = 1,
      invulnerabilidad,
      dash,
      recargaDash,
      recargaAtaque,
      recargaEscudo;
  private int ps = PS_MAXIMOS, nivel = 1;
  private final float[] pasivas = new float[Pasiva.values().length];
  private float pulsoRegeneracion;

  public Jugador(Inicial inicial) {
    this.inicial = Objects.requireNonNull(inicial);
    afinidad = inicial.getAfinidad();
  }

  public void actualizar(float dt, float eje) {
    if (!Float.isFinite(dt) || dt < 0 || !Float.isFinite(eje))
      throw new IllegalArgumentException("Tiempo/eje inválido");
    xAnterior = x;
    eje = Math.max(-1, Math.min(1, eje));
    if (eje != 0 && dash <= 0) direccion = Math.signum(eje);
    float pasoDash = Math.min(dt, dash);
    float pasoNormal = dt - pasoDash;
    x = Math.max(MIN_X, Math.min(MAX_X, x + direccion * 800 * pasoDash + eje * 325 * pasoNormal));
    dash = Math.max(0, dash - dt);
    invulnerabilidad = Math.max(0, invulnerabilidad - dt);
    recargaDash = Math.max(0, recargaDash - dt);
    recargaAtaque = Math.max(0, recargaAtaque - dt);
    recargaEscudo = Math.max(0, recargaEscudo - dt);
    float regenerando = Math.min(dt,pasivas[Pasiva.REGENERACION.ordinal()]);
    pulsoRegeneracion += regenerando;
    while(pulsoRegeneracion >= .5f){curar(1);pulsoRegeneracion -= .5f;}
    for(int i=0;i<pasivas.length;i++)pasivas[i]=Math.max(0,pasivas[i]-dt);
  }

  public int recibir(Elemento elemento, int danio) {
    if (danio < 0 || elemento == null) throw new IllegalArgumentException("Impacto inválido");
    if (ps == 0) return 0;
    int impacto = afinidad.calcularImpacto(elemento, danio);
    if (impacto >= 0) return curar(impacto);
    if (esInvulnerable()) return 0;
    int antes = ps;
    ps = Math.max(0, ps + impacto);
    invulnerabilidad = 0.85f;
    return ps - antes;
  }

  public int curar(int cantidad) {
    if (cantidad < 0) throw new IllegalArgumentException("Curación negativa");
    if (ps == 0) return 0;
    int antes = ps;
    ps = Math.min(PS_MAXIMOS, ps + cantidad);
    return ps - antes;
  }

  public boolean lanzarAtaque() {
    if (ps == 0 || recargaAtaque > 0) return false;
    recargaAtaque = getTecnica().getRecarga()*(getPasiva(Pasiva.POTENCIA)>0?.8f:1);
    return true;
  }

  public boolean activarDash() {
    if (ps == 0 || nivel < 2 || recargaDash > 0) return false;
    dash = 0.22f;
    invulnerabilidad = Math.max(invulnerabilidad, 0.28f);
    recargaDash = 4f;
    return true;
  }

  public boolean activarEscudo() {
    if (ps == 0 || nivel < 3 || recargaEscudo > 0) return false;
    invulnerabilidad = 2f;
    recargaEscudo = 12f;
    return true;
  }

  public void evolucionar() {
    if (nivel >= 3 || ps == 0) return;
    nivel++;
    curar(18);
    xAnterior = x;
    recargaDash = recargaAtaque = recargaEscudo = dash = 0;
    invulnerabilidad = 1.4f;
  }

  public Caja getHitbox() {
    return new Caja(x, Y, 28, 30);
  }

  public Caja getHitboxAnterior() {
    return new Caja(xAnterior, Y, 28, 30);
  }

  public Inicial getInicial() {
    return inicial;
  }

  public Elemento getElemento() {
    return afinidad.getElemento();
  }

  public String getForma() {
    return inicial.getForma(nivel);
  }

  public String getNombre() {
    return inicial.getNombre(nivel);
  }

  public float getX() {
    return x;
  }

  public float getXAnterior() {
    return xAnterior;
  }

  public int getPs() {
    return ps;
  }

  public int getNivel() {
    return nivel;
  }

  public boolean esInvulnerable() {
    return invulnerabilidad > 0 || getPasiva(Pasiva.ESCUDO)>0;
  }

  public void activarPasiva(Pasiva pasiva){
    if(ps==0)return;
    pasivas[pasiva.ordinal()]=pasiva.getDuracion();
    if(pasiva==Pasiva.REGENERACION)pulsoRegeneracion=0;
  }
  public float getPasiva(Pasiva pasiva){return pasivas[pasiva.ordinal()];}
  public Tecnica getTecnica(){return Tecnica.para(getElemento(),nivel);}
  public int getDanioAtaque(){return Math.round(getTecnica().getDanio()*(getPasiva(Pasiva.POTENCIA)>0?1.5f:1));}

  public boolean estaEnDash() {
    return dash > 0;
  }

  public float getRecargaAtaque() {
    return recargaAtaque;
  }

  public float getRecargaDash() {
    return recargaDash;
  }

  public float getRecargaEscudo() {
    return recargaEscudo;
  }
}
