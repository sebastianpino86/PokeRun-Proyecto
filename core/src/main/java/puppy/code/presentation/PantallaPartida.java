package puppy.code.presentation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.ScreenAdapter;
import puppy.code.persistence.GestorPerfil;
import puppy.code.GameLluviaMenu;
import puppy.code.domain.*;

public final class PantallaPartida extends ScreenAdapter {
  private final Partida partida;
  private final GameLluviaMenu juego;
  private final Lienzo l;
  private float tiempo;
  private double acumulado;
  private boolean resultadoGuardado;
  private boolean tutorial = true, hitboxes, ajustes;

  public PantallaPartida(GameLluviaMenu juego, Inicial inicial, long semilla) {
    this(juego, inicial, semilla, false);
  }

  public PantallaPartida(GameLluviaMenu juego, Inicial inicial, long semilla, boolean infinito) {
    this.juego=juego;this.l=juego.getLienzo();
    if (infinito && !juego.getPerfil().tieneInfinito())
      throw new IllegalStateException("Completa la campaña para desbloquear Infinito");
    partida = new Partida(inicial, semilla, juego.getPerfil().tieneAyuda(), infinito);
    partida.pausar();
  }

  @Override
  public void render(float delta) {
    float dt = Math.min(.1f, Math.max(0, delta));
    tiempo += dt;
    boolean salir = false;
    if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) hitboxes = !hitboxes;
    if (!tutorial && !ajustes && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      if (partida.getEstado() == Partida.JUGANDO) partida.pausar();
      else if (partida.getEstado() == Partida.PAUSA) partida.continuar();
    }
    if (!tutorial && !ajustes) {
      float eje = 0;
      if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) eje--;
      if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) eje++;
      if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) partida.atacar();
      if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)
          || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT)
          || Gdx.input.isKeyJustPressed(Input.Keys.X)) partida.dash();
      if (Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.C))
        partida.escudo();
      if(partida.getEstado()==Partida.JUGANDO) {
        acumulado+=dt;
        while(acumulado+1e-9>=1f/120) {
          partida.actualizar(1f/120,eje);
          acumulado-=1f/120;
        }
      }else acumulado=0;
    }
    int evento=partida.retirarEvento();
    if(evento!=Partida.SIN_EVENTO)sonido(evento);
    juego.actualizarAudio(partida.getEstado()!=Partida.JUGANDO);
    l.comenzar();
    dibujarPartida(l, partida, juego.getPerfil(), tiempo, hitboxes);
    if (tutorial) dibujarTutorial();
    else if (partida.getEstado() == Partida.PAUSA) {
      l.rect(0, 0, 960, 720, Color.BLACK, .6f);
      l.panel(258, 205, 444, 330, Lienzo.PANEL);
      l.centrado("Un respiro", 480, 491, 34, Lienzo.BLANCO, true);
      l.centrado("La ruta y tus habilidades están en pausa.", 480, 445, 13, Lienzo.SUAVE, false);
      if (!ajustes) {
        if (l.boton("Continuar [Enter]", 300, 356, 360, 52, true)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) partida.continuar();
        if (l.boton("Ajustes [O]", 300, 292, 360, 48, true)
            || Gdx.input.isKeyJustPressed(Input.Keys.O)) ajustes = true;
        if (l.boton("Volver al menú [Q]", 300, 229, 360, 48, true)
            || Gdx.input.isKeyJustPressed(Input.Keys.Q)) salir = true;
      }
    } else if (partida.getEstado() == Partida.EVOLUCION) dibujarEvolucion();
    if (ajustes && PantallaMenu.dibujarAjustes(l,juego.getPerfil(),juego)) ajustes = false;
    if(partida.getEstado()==Partida.VICTORIA||partida.getEstado()==Partida.DERROTA) {
      if(!resultadoGuardado) {
        resultadoGuardado=true;
        juego.getPerfil().registrarResultado(partida.esInfinito(),partida.getEstado()==Partida.VICTORIA,partida.getPuntos());
      }
      dibujarResultado();
    }
    l.terminar();
    if(salir)juego.cambiarPantalla(new PantallaMenu(juego));

  }

  private void dibujarTutorial() {
    l.rect(0, 0, 960, 720, Color.BLACK, .7f);
    l.panel(155, 119, 650, 486, Lienzo.PANEL);
    l.texto(partida.esInfinito() ? "Resiste y supera tu récord" : "Tu primera ruta", 193, 564, 30, Lienzo.BLANCO, true);
    l.texto("Esquiva. Contraataca. Absorbe tu elemento.", 193, 521, 17, Lienzo.MENTA, false);
    l.texto("A / D o flechas", 197, 477, 17, Lienzo.BLANCO, true);
    l.texto("Muévete por el claro.", 445, 475, 15, Lienzo.SUAVE, false);
    l.texto("Mantén ESPACIO", 197, 435, 17, Lienzo.BLANCO, true);
    l.texto("Dispara y daña a Prismeon.", 445, 433, 15, Lienzo.SUAVE, false);
    l.texto("SHIFT / X  ·  E / C", 197, 393, 17, Lienzo.BLANCO, true);
    l.texto("Dash en ruta 2; escudo en ruta 3.", 445, 391, 14, Lienzo.SUAVE, false);
    l.texto("Cada 60 de daño: un orbe pasivo", 197, 350, 18, Lienzo.ORO, true);
    l.texto("Recógelo: escudo 3 s, regeneración, imán o potencia.", 197, 321, 14, Lienzo.SUAVE, false);
    l.texto("Tu tipo: +5 PS y 25 puntos. Baya: +10 PS y 40 puntos.", 197, 287, 14, Lienzo.MENTA, false);
    l.texto(partida.esInfinito() ? "Infinito: sobrevive hasta morir. El terreno cambia cada minuto." : "Tres rutas, técnicas más poderosas y una evolución en cada paso.", 197, 256, 13, Lienzo.SUAVE, false);
    l.texto("Esc: pausa · F3: hitboxes · M: silencio · F12: captura", 197, 218, 12, Lienzo.SUAVE, false);
    if (l.boton("¡Vamos! [Enter]", 312, 143, 336, 48, true)
        || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) iniciar();
  }

  private void dibujarEvolucion() {
    int siguiente = partida.getNivel().getNumero() + 1;
    Inicial inicial = partida.getJugador().getInicial();
    l.rect(0, 0, 960, 720, Color.BLACK, .66f);
    l.panel(195, 143, 570, 435, Lienzo.PANEL);
    l.centrado(
        inicial == Inicial.PIKACHU ? "¡Pikachu se fortalece!" : "¡Es hora de evolucionar!",
        480,
        538,
        28,
        Lienzo.BLANCO,
        true);
    l.pokemon(inicial.getForma(siguiente - 1), 342, 366, 110, tiempo, Color.WHITE);
    l.texto(">", 472, 411, 35, Lienzo.MENTA, true);
    l.circulo(620, 415, 62, Lienzo.MENTA, .12f);
    l.pokemon(inicial.getForma(siguiente), 620, 366, 135, tiempo, Color.WHITE);
    l.centrado(inicial.getNombre(siguiente), 480, 339, 24, Lienzo.MENTA, true);
    l.centrado(
        siguiente == 2
            ? "Nuevo poder: dash [SHIFT / X]"
            : "Nuevo poder: escudo de 2 segundos [E / C]",
        480,
        302,
        17,
        Lienzo.BLANCO,
        true);
    l.centrado(
        "Recuperas 18 PS. Conservas tus poderes anteriores.", 480, 271, 14, Lienzo.SUAVE, false);
    l.centrado(
        "Tu técnica evoluciona: " + Tecnica.para(partida.getJugador().getElemento(), siguiente).getNombre(),
        480,
        244,
        14,
        Lienzo.ORO,
        false);
    if (l.boton("Continuar a la ruta " + siguiente + " [Enter]", 268, 173, 424, 52, true)
        || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
      partida.avanzarNivel();
      acumulado=0;
      juego.reproducir("evolve");
    }
  }

  private void sonido(int tipo) {
    switch(tipo) {
      case Partida.DANIO:juego.reproducir("damage");break;
      case Partida.CURACION:juego.reproducir("heal");break;
      case Partida.DISPARO:juego.reproducir("cast");break;
      case Partida.DASH:juego.reproducir("dash");break;
      case Partida.ESCUDO:case Partida.PASIVA_EVENTO:juego.reproducir("shield");break;
      case Partida.DESTRUIDO:case Partida.DANIO_RIVAL:juego.reproducir("break");break;
      case Partida.EVOLUCION_EVENTO:juego.reproducir("evolve");break;
      case Partida.VICTORIA_EVENTO:juego.reproducir("victory");break;
      case Partida.DERROTA_EVENTO:juego.reproducir("defeat");break;
      default:break;
    }
  }

  public void iniciar() {
    tutorial = false;
    partida.continuar();
    acumulado=0;
  }

  public void mostrarHitboxes(boolean mostrar) {
    hitboxes = mostrar;
  }

  public Partida getPartida() {
    return partida;
  }

  @Override
  public void pause() {
    partida.pausar();
    acumulado=0;
  }
  private void dibujarResultado() {
    boolean victoria=partida.getEstado()==Partida.VICTORIA;
    boolean infinito=partida.esInfinito();
    l.rect(0,0,960,720,Color.BLACK,.72f);
    l.panel(175,97,610,533,Lienzo.PANEL);
    l.centrado(victoria?"¡Santuario alcanzado!":infinito?"Fin de la resistencia":"La ruta continúa",480,589,31,Lienzo.BLANCO,true);
    l.centrado(victoria?"Completaste las tres rutas de Prismeon.":"Busca ataques de tu tipo y vuelve a intentarlo.",480,541,15,Lienzo.SUAVE,false);
    l.circulo(480,455,60,Lienzo.ORO,.12f);
    l.pokemon(victoria?"pikachu":partida.getJugador().getForma(),480,411,124,tiempo,Color.WHITE);
    l.centrado(victoria?"PIKACHU + INFINITO":infinito?"RESISTISTE "+(int)partida.getTiempo()/60+"m "+(int)partida.getTiempo()%60+"s":"RUTA "+partida.getNivel().getNumero(),480,390,22,Lienzo.ORO,true);
    l.centrado(victoria?"Progreso guardado. ¡Ahora puedes jugar infinito!":"Mejora tu récord y domina las técnicas.",480,356,13,Lienzo.SUAVE,false);
    l.centrado(partida.getPuntos()+" puntos  ·  "+(int)partida.getDistanciaTotal()+" m recorridos",480,314,19,Lienzo.BLANCO,true);
    l.centrado("Impactos: "+partida.getImpactos()+"   Curaciones: "+partida.getCuraciones()+"   Ataques eliminados: "+partida.getDestruidos(),480,281,13,Lienzo.SUAVE,false);
    l.centrado("Daño a Prismeon: "+partida.getDanioRival()+"   Orbes: "+partida.getPasivasRecogidas()+"   Récord: "+(infinito?juego.getPerfil().getRecordInfinito():juego.getPerfil().getRecord()),480,252,12,Lienzo.MENTA,false);
    if(l.boton("Menú [Enter]",211,184,250,54,true)||Gdx.input.isKeyJustPressed(Input.Keys.ENTER))juego.cambiarPantalla(new PantallaMenu(juego));
    else if(l.boton("Otra aventura [R]",491,184,257,54,true)||Gdx.input.isKeyJustPressed(Input.Keys.R))
      juego.cambiarPantalla(new PantallaPartida(juego,partida.getJugador().getInicial(),System.nanoTime(),infinito));
  }

  @Override public void resize(int ancho,int alto){l.resize(ancho,alto);}
  private void dibujarPartida(Lienzo l, Partida p, GestorPerfil perfil, float tiempo, boolean hitboxes) {
    l.paisaje( p.getTiempo(), p.getTerreno(), perfil);
    Eevee rival = p.getRival();
    Color afinidad = l.tipo(rival.getElemento());
    float rx = rival.getX();

    l.texto("PRISMEON · VARITIPO", 128, 603, 15, Lienzo.BLANCO, true);
    l.texto(
        rival.estaAturdido()
            ? "BARRERA ROTA · RESPIRO"
            : "Barrera " + rival.getPs() + " / " + rival.getPsMaximos(),
        128, 580, 11, Lienzo.SUAVE, false);

    l.rect(128, 558, 160, 6, Lienzo.PANEL);
    l.rect(128, 558, 160f * rival.getPs() / rival.getPsMaximos(), 6, afinidad);

    // CORRECCIÓN 1: Nombre correcto del método en Eevee.java
    l.texto(
        "Orbe en " + rival.getDanioHastaOrbe() + " de daño",
        128, 544, 10, Lienzo.ORO, false);

    l.simbolo(rival.getElemento(), 808, 586, 12, 1);
    l.texto(
        Tecnica.para(rival.getElemento(), p.getNivel().getNumero()).getNombre(),
        695, 611, 14, afinidad, true);
    l.texto(p.getPresion(), 695, 566, 10, Lienzo.SUAVE, true);
    l.texto(p.getPatron(), 695, 548, 10, Lienzo.SUAVE, false);

    l.prismeon(
        rx, 573,
        perfil.movimientoReducido() ? 0 : tiempo,
        rival.getElemento(),
        rival.estaAturdido());

    for (Ataque aviso : p.getAvisos()) {
      Color c = l.tipo(aviso.getElemento());
      float fuerza = Math.max(0, Math.min(1, 1 - aviso.getRestante() / aviso.getAnticipacion()));
      float impactoX = aviso.getImpactoEn(Jugador.Y);
      // Aviso local sin línea larga de trayectoria.
      l.circulo(impactoX, Jugador.Y, 12 + fuerza * 5, c, .035f + fuerza * .08f);
      l.circulo(aviso.getX(), 532, 15 + fuerza * 7, c, .10f + fuerza * .18f);
      l.simbolo(aviso.getElemento(), aviso.getX(), 532, 8 + fuerza * 4, .5f + fuerza * .5f);
      l.rect(aviso.getX() - 18, 510, 36 * fuerza, 3, c);
    }

    for (EntidadCayendo e : p.getEntidades()) {
      if (e.getPasiva() != null) {
        l.rombo(e.getX(), e.getY(), 22, Lienzo.ORO, .3f);
        l.rombo(e.getX(), e.getY(), 15, Lienzo.ORO, 1);
        l.rombo(e.getX(), e.getY(), 12, Lienzo.PANEL, 1);
        l.centrado(
            e.getPasiva().getSimbolo(),
            e.getX(), e.getY() + 8,
            18, Lienzo.BLANCO, true);

      } else if (e.esRecolectable()) {
        l.circulo(e.getX(), e.getY(), 16, Color.valueOf("376AA0"), 1);
        l.circulo(
            e.getX() - 4, e.getY() + 4,
            4, Color.valueOf("93D5EE"), .8f);
        l.linea(
            e.getX(), e.getY() + 14,
            e.getX() + 7, e.getY() + 20,
            4, Lienzo.MENTA, 1);
        l.centrado("+", e.getX(), e.getY() + 8, 17, Lienzo.BLANCO, true);

      } else {
        Color c = l.tipo(e.getElemento());
        l.circulo(e.getX(), e.getY() + 17, 7, c, .17f);
        l.circulo(e.getX(), e.getY() + 28, 4, c, .10f);

        l.tecnica(
                        ((Ataque) e).getTecnica(),
            e.getX(),
            e.getY(),
            e.getRadio(),
            perfil.movimientoReducido() ? 0 : tiempo,
            false);
      }
    }

    for (ProyectilJugador proyectil : p.getProyectiles()) {
      l.tecnica(
                    proyectil.getTecnica(),
          proyectil.getX(),
          proyectil.getY(),
          proyectil.getTecnica().getRadio(),
          perfil.movimientoReducido() ? 0 : tiempo,
          true);
    }

    Jugador j = p.getJugador();
    l.circulo(j.getX(), 112, 28, Color.BLACK, .25f);

    if (j.estaEnDash() && !perfil.movimientoReducido()) {
      for (int i = 1; i < 4; i++) {
        l.circulo(
            j.getX() - (j.getX() - j.getXAnterior()) * i * 2,
            139, 18, Lienzo.MENTA, .12f);
      }
    }

    if (j.esInvulnerable()) {
      l.circulo(j.getX(), Jugador.Y, 42, Lienzo.MENTA, .13f);

      for (int i = 0; i < 14; i++) {
        float a = i * .4488f + tiempo;

        l.rombo(
            j.getX() + (float) Math.cos(a) * 39,
            Jugador.Y + (float) Math.sin(a) * 39,
            3,
            Lienzo.MENTA,
            .85f);
      }
    }

    Color tint =
        j.esInvulnerable()
            && !perfil.movimientoReducido()
            && ((int) (tiempo * 12)) % 2 == 0
                ? Color.valueOf("BBF3D3")
                : Color.WHITE;

    l.pokemon(j.getForma(), j.getX(), 110, 90, tiempo, tint);

    if (hitboxes) {
      Caja b = rival.getHitbox();
      l.borde(
          b.getX(), b.getY(),
          b.getAncho(), b.getAlto(),
          Lienzo.ORO, 2);

      Caja h = j.getHitbox();
      l.borde(
          h.getX(), h.getY(),
          h.getAncho(), h.getAlto(),
          Lienzo.MENTA, 2);

      for (EntidadCayendo e : p.getEntidades()) {
        for (int i = 0; i < 24; i++) {
          // CORRECCIÓN 2: Evita duplicar la variable b
          float a = i * .2618f;
          float anguloFinal = (i + 1) * .2618f;

          l.linea(
              e.getX() + (float) Math.cos(a) * e.getRadio(),
              e.getY() + (float) Math.sin(a) * e.getRadio(),
              e.getX() + (float) Math.cos(anguloFinal) * e.getRadio(),
              e.getY() + (float) Math.sin(anguloFinal) * e.getRadio(),
              1.5f,
              Lienzo.BLANCO,
              1);
        }
      }

      for (ProyectilJugador proyectil : p.getProyectiles()) {
        float radio = proyectil.getTecnica().getRadio();
        l.borde(proyectil.getX() - radio, proyectil.getY() - 12,
            radio * 2, 24, Lienzo.MENTA, 1);
      }
      l.panel(120, 194, 315, 35, Lienzo.PANEL);
      l.texto(
          "F3  HITBOX: cuerpo 28 × 30 / núcleo circular",
          132, 218, 12, Lienzo.MENTA, true);
    }

    hud(l, p);
  }

  private void hud(Lienzo l, Partida p) {
    Jugador j = p.getJugador();

    l.panel(25, 623, 910, 79, Lienzo.PANEL);
    l.texto(j.getNombre(), 46, 689, 20, Lienzo.BLANCO, true);
    l.texto("PS " + j.getPs() + " / 100", 46, 660, 13, Lienzo.SUAVE, false);

    l.rect(150, 644, 155, 10, Color.valueOf("294B46"));
    l.rect(
        150,
        644,
        155 * j.getPs() / 100,
        10,
        j.getPs() < 35 ? l.tipo(Elemento.FUEGO) : Lienzo.MENTA);

    l.texto(
        p.esInfinito()
            ? "INFINITO · OLEADA " + p.getOleada()
            : "RUTA 0" + p.getNivel().getNumero()
                + "  /  " + p.getNivel().getNombre(),
        338,
        688,
        16,
        Lienzo.BLANCO,
        true);

    l.rect(338, 645, 337, 10, Color.valueOf("294B46"));
    l.rect(338, 645, 337 * p.getProgreso(), 10, Lienzo.ORO);

    l.texto(
        p.esInfinito()
            ? (int) p.getTiempo() / 60 + "m "
                + (int) p.getTiempo() % 60 + "s · "
                + Nivel.numero(p.getTerreno()).getNombre()
            : (int) p.getDistancia() + " / "
                + p.getNivel().getLongitud() + " m",
        338,
        668,
        12,
        Lienzo.SUAVE,
        false);

    l.texto(
        String.format(java.util.Locale.ROOT, "%05d", p.getPuntos()),
        746,
        689,
        23,
        Lienzo.ORO,
        true);

    l.texto(
        p.tieneAyuda() ? "MODO AYUDA" : "PUNTOS",
        746, 657, 11, Lienzo.SUAVE, false);

    habilidad(
        l,
        "ESPACIO",
        j.getTecnica().getNombre(),
        154,
        20,
        j.getRecargaAtaque(),
        j.getTecnica().getRecarga(),
        true);

    habilidad(
        l, "SHIFT / X", "Dash",
        374, 20,
        j.getRecargaDash(), 4,
        j.getNivel() >= 2);

    habilidad(
        l, "E / C", "Escudo",
        594, 20,
        j.getRecargaEscudo(), 12,
        j.getNivel() >= 3);

    int indice = 0;

    for (Pasiva pasiva : Pasiva.values()) {
      float restante = j.getPasiva(pasiva);

      if (restante > 0) {
        float px = 153 + indice++ * 170;

        l.panel(px, 83, 162, 22, Lienzo.PANEL);

        l.texto(
            pasiva.getNombre() + " "
                + String.format(java.util.Locale.ROOT, "%.1fs", restante),
            px + 8, 99, 11, Lienzo.ORO, true);

        l.rect(
            px, 83,
            162 * restante / pasiva.getDuracion(),
            2, Lienzo.ORO);
      }
    }

    l.texto("A / D o flechas", 30, 59, 11, Lienzo.BLANCO, false);
    l.texto("Mover", 30, 40, 12, Lienzo.SUAVE, false);
    l.texto("ESC  Pausa", 830, 57, 11, Lienzo.BLANCO, false);
    l.texto("F3  Hitboxes", 830, 39, 11, Lienzo.SUAVE, false);
    l.texto("M  Silencio", 830, 21, 11, Lienzo.SUAVE, false);
  }

  private void habilidad(
      Lienzo l,
      String tecla,
      String nombre,
      float x,
      float y,
      float restante,
      float total,
      boolean activa) {

    l.panel(x, y, 202, 57, Lienzo.PANEL);
    l.texto(tecla, x + 12, y + 45, 10, Lienzo.SUAVE, true);

    l.texto(
        activa
            ? nombre
            : "Disponible en ruta " + (total == 4 ? 2 : 3),
        x + 12,
        y + 29,
        12,
        activa ? Lienzo.BLANCO : Lienzo.SUAVE,
        true);

    l.rect(
        x + 12, y + 8,
        178, 3,
        Color.valueOf("294B46"));

    if (activa) {
      l.rect(
          x + 12,
          y + 8,
          178 * (1 - restante / total),
          3,
          restante <= 0 ? Lienzo.MENTA : Lienzo.ORO);
    }

    if (activa && restante > 0) {
      l.texto(
          String.format(java.util.Locale.ROOT, "%.1fs", restante),
          x + 148,
          y + 45,
          10,
          Lienzo.ORO,
          false);
    }
  }
}
