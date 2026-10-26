package puppy.code;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import puppy.code.persistence.GestorPerfil;
import puppy.code.presentation.Lienzo;
import puppy.code.presentation.PantallaMenu;

/** Configura libGDX; único propietario del lienzo, del audio y de las pantallas. */
public final class GameLluviaMenu extends Game {
  private Lienzo lienzo;
  private GestorPerfil perfil;
  private final Map<String,Sound> sonidos=new HashMap<>();
  private Music musica;
  private boolean audioDisponible=true,silenciado;

  @Override public void create() {
    perfil=GestorPerfil.getInstancia();
    lienzo=new Lienzo();
    silenciado=Boolean.getBoolean("pokerun.mute");
    try {
      for(String nombre:new String[]{"damage","heal","cast","dash","shield","break","evolve","victory","defeat","ui"})
        sonidos.put(nombre,Gdx.audio.newSound(Gdx.files.internal("audio/"+nombre+".wav")));
      musica=Gdx.audio.newMusic(Gdx.files.internal("audio/meadow_loop.wav"));
      musica.setLooping(true);musica.setVolume(silenciado?0:perfil.getMusica());musica.play();
    }catch(RuntimeException error){audioDisponible=false;Gdx.app.error("Audio","Juego sin audio",error);}
    setScreen(new PantallaMenu(this));
  }

  @Override public void render() {
    if(Gdx.input.isKeyJustPressed(Input.Keys.M))silenciado=!silenciado;
    super.render();
    if(Gdx.input.isKeyJustPressed(Input.Keys.F12))capturar();
  }

  private void capturar() {
    String nombre="captura-"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    java.nio.file.Path directorio=Paths.get(perfil.getRuta()).getParent().resolve("capturas");
    Pixmap imagen=Pixmap.createFromFrameBuffer(0,0,Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());
    try{
      java.nio.file.Files.createDirectories(directorio);
      PixmapIO.writePNG(Gdx.files.absolute(directorio.resolve(nombre+".png").toString()),imagen,-1,true);
    }catch(Exception error){Gdx.app.error("Captura","No se pudo guardar",error);}
    finally{imagen.dispose();}
  }

  public void cambiarPantalla(Screen nueva) {
    Screen anterior=getScreen();setScreen(nueva);
    if(anterior!=null)anterior.dispose();
  }
  public void reproducir(String nombre) {
    Sound sonido=sonidos.get(nombre);
    if(audioDisponible&&!silenciado&&sonido!=null)sonido.play(perfil.getEfectos());
  }
  public void actualizarAudio(boolean pausa) {
    if(musica!=null)musica.setVolume(silenciado?0:perfil.getMusica()*(pausa?.35f:1f));
  }
  public boolean audioDisponible(){return audioDisponible;}
  public Lienzo getLienzo(){return lienzo;}
  public GestorPerfil getPerfil(){return perfil;}

  @Override public void dispose(){
    if(getScreen()!=null)getScreen().dispose();
    if(perfil!=null)perfil.guardar();
    for(Sound sonido:sonidos.values())sonido.dispose();
    if(musica!=null)musica.dispose();
    if(lienzo!=null)lienzo.dispose();
  }
}
