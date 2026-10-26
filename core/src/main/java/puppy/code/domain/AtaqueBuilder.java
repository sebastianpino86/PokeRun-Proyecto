package puppy.code.domain;

/** Contrato del Builder enseñado en el curso: pasos explícitos y producto final. */
public interface AtaqueBuilder {
  void reset();

  void posicion(float x, float y);

  void movimiento(float horizontal, float caida);

  void combate(Elemento tipo, int danio, float radio);

  void tecnica(Tecnica tecnica);

  Ataque getResult();
}
