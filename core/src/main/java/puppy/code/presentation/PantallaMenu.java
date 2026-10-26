package puppy.code.presentation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import puppy.code.GameLluviaMenu;
import puppy.code.domain.Inicial;

import puppy.code.domain.Elemento;
import puppy.code.persistence.GestorPerfil;
import com.badlogic.gdx.ScreenAdapter;

public final class PantallaMenu extends ScreenAdapter {
  private final GameLluviaMenu juego;
  private final Lienzo l;
  private float tiempo;
  private int seleccion;
  private boolean ajustes;
  private boolean infinito;

  public PantallaMenu(GameLluviaMenu juego) {
    this.juego=juego;this.l=juego.getLienzo();
  }

  @Override
  public void render(float delta) {
    tiempo += Math.min(delta, .1f);
    GestorPerfil perfil = juego.getPerfil();
    juego.actualizarAudio(false);
    boolean iniciar = false;
    l.comenzar();
    l.paisaje(tiempo, 1, perfil);
    l.rect(0, 0, 960, 720, Color.valueOf("082B2E"), .76f);
    l.texto("POKÉRUN", 55, 656, 66, Lienzo.BLANCO, true);
    l.texto("LA SENDA VARITIPO", 60, 576, 16, Lienzo.MENTA, true);
    l.texto("Prismeon protege el santuario. Cruza sus rutas,", 60, 535, 17, Lienzo.BLANCO, false);
    l.texto("domina tu elemento y descubre tu última evolución.", 60, 507, 17, Lienzo.SUAVE, false);
    l.panel(687, 482, 218, 180, Color.valueOf("214641"));
    l.circulo(798, 545, 45, Lienzo.MENTA, .08f);
    l.prismeon( 798, 557, tiempo, Elemento.FUEGO, false);
    l.centrado("Un rival. Cuatro tipos.", 796, 504, 12, Lienzo.SUAVE, false);
    l.texto("ELIGE A TU COMPAÑERO", 58, 445, 13, Lienzo.MENTA, true);
    l.texto("Récord  " + (!infinito ? perfil.getRecord() : perfil.getRecordInfinito()), 733, 444, 13, Lienzo.ORO, true);
    int cantidad = perfil.tienePikachu() ? 4 : 3;
    if (!ajustes) {
      for (int i = 0; i < cantidad; i++)
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
          seleccion = i;
          juego.reproducir("ui");
        }
      if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) seleccion = (seleccion + 1) % cantidad;
      if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
        seleccion = (seleccion + cantidad - 1) % cantidad;
    }
    for (int i = 0; i < 4; i++) {
      Inicial inicial = Inicial.values()[i];
      float x = 54 + i * 216;
      boolean disponible = i < 3 || perfil.tienePikachu();
      boolean elegida = i == seleccion;
      l.panel(x, 205, 204, 212, elegida ? Color.valueOf("345C4C") : Color.valueOf("153B36"));
      if (elegida) {
        l.rect(x + 16, 407, 172, 3, Lienzo.MENTA);
        l.texto("ELEGIDO", x + 122, 393, 10, Lienzo.MENTA, true);
      }
      l.texto("0" + (i + 1), x + 16, 394, 12, Lienzo.SUAVE, true);
      l.circulo(
          x + 102, 312, 42, l.tipo(inicial.getAfinidad().getElemento()), disponible ? .12f : .04f);
      l.pokemon(
          inicial.getForma(1),
          x + 102,
          283,
          96,
          tiempo,
          disponible ? Color.WHITE : Color.valueOf("6D8277"));
      l.centrado(
          inicial.getNombre(1), x + 102, 277, 20, disponible ? Lienzo.BLANCO : Lienzo.SUAVE, true);
      l.centrado(
          disponible ? inicial.getAfinidad().getElemento().getNombre() : "COMPLETA LAS 3 RUTAS",
          x + 102,
          247,
          disponible ? 13 : 10,
          disponible ? l.tipo(inicial.getAfinidad().getElemento()) : Lienzo.ORO,
          false);
      l.centrado(
          disponible
              ? (i == 3 ? "Compañero desbloqueado" : "3 formas · ataques evolutivos")
              : "Pikachu te espera al final",
          x + 102,
          226,
          10,
          Lienzo.SUAVE,
          false);
      if (!ajustes && disponible && l.pulsado(x, 205, 204, 212)) {
        seleccion = i;
        juego.reproducir("ui");
      }
    }
    if (perfil.tieneInfinito()) {
      if (l.boton(!infinito ? "[ CAMPAÑA ]" : "CAMPAÑA", 258, 181, 215, 24, !ajustes)) infinito=false;
      if (l.boton(infinito ? "[ INFINITO ]" : "INFINITO [I]", 487, 181, 215, 24, !ajustes)) infinito=true;
      if (!ajustes && Gdx.input.isKeyJustPressed(Input.Keys.I)) infinito=!infinito;
    } else l.centrado("Completa la campaña: desbloquea Pikachu + modo infinito", 480, 193, 12, Lienzo.ORO, false);
    if (!ajustes) {
      iniciar =
          l.boton((infinito ? "Resistir hasta el final  [Enter]" : "Comenzar aventura  [Enter]"), 258, 115, 444, 58, true)
              || Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
      if (l.boton("Ajustes [O]", 716, 46, 188, 43, true)
          || Gdx.input.isKeyJustPressed(Input.Keys.O)) ajustes = true;
    } else l.boton((infinito ? "Resistir hasta el final  [Enter]" : "Comenzar aventura  [Enter]"), 258, 115, 444, 58, false);
    l.texto("1 - 4  Elegir     Enter  Jugar     F12  Captura", 55, 78, 12, Lienzo.SUAVE, false);
    l.texto(
        "Fangame académico · sprites Pokémon / PokéAPI · audio original",
        55,
        49,
        10,
        Lienzo.SUAVE,
        false);
    if (!juego.getPerfil().getAviso().isEmpty())
      l.texto(juego.getPerfil().getAviso(), 55, 23, 11, Lienzo.ORO, false);
    if (!juego.audioDisponible())
      l.texto("Audio no disponible en este equipo", 55, 21, 11, Lienzo.ORO, false);
    if (ajustes && dibujarAjustes(l,juego.getPerfil(),juego)) ajustes = false;
    l.terminar();
    if (iniciar) {
      juego.reproducir("ui");
      juego.cambiarPantalla(
          new PantallaPartida(juego, Inicial.values()[seleccion], System.nanoTime(), infinito));
    }
  }

  @Override public void resize(int ancho,int alto){l.resize(ancho,alto);}
  public static boolean dibujarAjustes(Lienzo l, GestorPerfil gestor, GameLluviaMenu juego) {
    GestorPerfil p = gestor;
    l.rect(0, 0, 960, 720, Color.BLACK, .72f);
    l.panel(210, 110, 540, 505, Lienzo.PANEL);
    l.texto("Ajustes", 246, 576, 32, Lienzo.BLANCO, true);
    l.texto("Se conservan al cerrar el juego.", 246, 532, 14, Lienzo.SUAVE, false);
    boolean cambio = false;
    if (l.boton("[1] Música  " + Math.round(p.getMusica() * 100) + " %", 246, 447, 468, 48, true)
        || Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
      p.ajustar(1);
      cambio = true;
    }
    if (l.boton("[2] Efectos  " + Math.round(p.getEfectos() * 100) + " %", 246, 385, 468, 48, true)
        || Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
      p.ajustar(2);
      cambio = true;
    }
    if (l.boton(
            "[3] Movimiento reducido: " + (p.movimientoReducido() ? "sí" : "no"),
            246,
            323,
            468,
            48,
            true)
        || Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
      p.ajustar(3);
      cambio = true;
    }
    if (l.boton("[4] Contraste alto: " + (p.altoContraste() ? "sí" : "no"), 246, 261, 468, 48, true)
        || Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
      p.ajustar(4);
      cambio = true;
    }
    if (l.boton(
            "[5] Ayuda en próxima partida: " + (p.tieneAyuda() ? "sí" : "no"),
            246,
            199,
            468,
            48,
            true)
        || Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
      p.ajustar(5);
      cambio = true;
    }
    if (cambio) {
      gestor.guardar();
      juego.reproducir("ui");
    }
    l.texto("Ayuda: ataques más lentos y menos daño.", 246, 181, 12, Lienzo.SUAVE, false);
    return l.boton("Volver [Esc]", 380, 123, 200, 42, true)
        || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
  }

}
