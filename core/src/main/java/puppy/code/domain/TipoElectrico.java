package puppy.code.domain;

public final class TipoElectrico implements ComportamientoElemento {
  @Override
  public int calcularImpacto(Elemento ataque, int danioBase) {
    return ataque == Elemento.ELECTRICO ? 5 : -danioBase;
  }

  @Override
  public Elemento getElemento() {
    return Elemento.ELECTRICO;
  }
}
