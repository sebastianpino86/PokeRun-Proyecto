package puppy.code.domain;

/** Instantánea inmutable de una hitbox; el exterior nunca recibe el estado mutable. */
public final class Caja {
  private final float centroX, centroY, ancho, alto;

  public Caja(float centroX, float centroY, float ancho, float alto) {
    if (!Float.isFinite(centroX)
        || !Float.isFinite(centroY)
        || !Float.isFinite(ancho)
        || !Float.isFinite(alto)
        || ancho <= 0
        || alto <= 0) throw new IllegalArgumentException("Caja inválida");
    this.centroX = centroX;
    this.centroY = centroY;
    this.ancho = ancho;
    this.alto = alto;
  }

  public float getCentroX() {
    return centroX;
  }

  public float getCentroY() {
    return centroY;
  }

  public float getAncho() {
    return ancho;
  }

  public float getAlto() {
    return alto;
  }

  public float getX() {
    return centroX - ancho / 2;
  }

  public float getY() {
    return centroY - alto / 2;
  }

  /** Colisiones exactas sin revelar ni modificar datos de las hitboxes. */
  public static boolean circuloCaja(float x, float y, float radio, Caja caja) {
    float dx = Math.max(Math.abs(x - caja.getCentroX()) - caja.getAncho() / 2, 0);
    float dy = Math.max(Math.abs(y - caja.getCentroY()) - caja.getAlto() / 2, 0);
    return dx * dx + dy * dy <= radio * radio;
  }

  public static boolean barrido(
      float x0, float y0, float x1, float y1, float radio, Caja antes, Caja ahora) {
    float ax = x0 - antes.getCentroX(), ay = y0 - antes.getCentroY();
    float bx = x1 - ahora.getCentroX(), by = y1 - ahora.getCentroY();
    float hw = ahora.getAncho() / 2, hh = ahora.getAlto() / 2;
    if (segmentoCaja(ax, ay, bx, by, -hw - radio, -hh, hw + radio, hh)
        || segmentoCaja(ax, ay, bx, by, -hw, -hh - radio, hw, hh + radio)) return true;
    for (int sx = -1; sx <= 1; sx += 2)
      for (int sy = -1; sy <= 1; sy += 2)
        if (distanciaSegmento2(ax, ay, bx, by, sx * hw, sy * hh) <= radio * radio) return true;
    return false;
  }

  private static float distanciaSegmento2(
      float ax, float ay, float bx, float by, float x, float y) {
    float dx = bx - ax, dy = by - ay, longitud2 = dx * dx + dy * dy;
    float t =
        longitud2 == 0 ? 0 : Math.max(0, Math.min(1, ((x - ax) * dx + (y - ay) * dy) / longitud2));
    float ex = x - ax - t * dx, ey = y - ay - t * dy;
    return ex * ex + ey * ey;
  }

  private static boolean segmentoCaja(
      float ax, float ay, float bx, float by, float minX, float minY, float maxX, float maxY) {
    float entrada = 0, salida = 1;
    float[] inicio = {ax, ay}, delta = {bx - ax, by - ay}, min = {minX, minY}, max = {maxX, maxY};
    for (int i = 0; i < 2; i++) {
      if (Math.abs(delta[i]) < 0.000001f) {
        if (inicio[i] < min[i] || inicio[i] > max[i]) return false;
      } else {
        float t1 = (min[i] - inicio[i]) / delta[i], t2 = (max[i] - inicio[i]) / delta[i];
        entrada = Math.max(entrada, Math.min(t1, t2));
        salida = Math.min(salida, Math.max(t1, t2));
        if (entrada > salida) return false;
      }
    }
    return true;
  }

}
