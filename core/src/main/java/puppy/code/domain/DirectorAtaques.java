package puppy.code.domain;

import java.util.Objects;

public final class DirectorAtaques {
  private final AtaqueBuilder builder;

  public DirectorAtaques(AtaqueBuilder builder) {
    this.builder = Objects.requireNonNull(builder);
  }

  public Ataque crear(Elemento tipo, float x, float y, float velocidad, int danio, float deriva) {
    return crear(tipo, x, y, velocidad, danio, deriva, 1);
  }

  public Ataque crear(Elemento tipo, float x, float y, float velocidad, int danio, float deriva, int rango) {
    builder.reset();
    builder.posicion(x, y);
    builder.movimiento(deriva, velocidad);
    builder.combate(tipo, danio, (tipo == Elemento.AGUA ? 8 : 10) + rango - 1);
    builder.tecnica(Tecnica.para(tipo, rango));
    return builder.getResult();
  }
}
