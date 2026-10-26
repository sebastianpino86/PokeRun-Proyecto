package puppy.code.domain;

/** Strategy: un impacto positivo cura; uno negativo daña. No modifica al jugador. */
public interface ComportamientoElemento {
  int calcularImpacto(Elemento ataque, int danioBase);

  Elemento getElemento();
}
