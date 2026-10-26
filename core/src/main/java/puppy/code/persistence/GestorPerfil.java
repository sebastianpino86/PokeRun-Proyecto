package puppy.code.persistence;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/** Singleton clásico: dueño de los datos y del guardado; no exporta estado mutable. */
public final class GestorPerfil {
  private static GestorPerfil instancia;
  private final Path archivo;
  private boolean pikachu, ayuda, reducido, contraste;
  private int record, recordInfinito, victorias;
  private float musica=.35f, efectos=.65f;
  private String aviso="";

  private GestorPerfil() {
    String base=System.getenv("LOCALAPPDATA");
    if(base==null||base.isEmpty())base=System.getProperty("user.home");
    archivo=Paths.get(System.getProperty("pokerun.saveDir",Paths.get(base,"PokeRun").toString()),"perfil.properties");
    cargar();
  }

  public static synchronized GestorPerfil getInstancia() {
    if(instancia==null)instancia=new GestorPerfil();
    return instancia;
  }

  /** Lee también partidas guardadas por la versión anterior. */
  private void cargar() {
    if(!Files.exists(archivo))return;
    try {leer(archivo);}
    catch(Exception error) {
      try {leer(archivo.resolveSibling("perfil.bak"));aviso="Perfil recuperado desde copia de seguridad";}
      catch(Exception ignorado) {aviso="No se pudo leer el perfil anterior; archivos conservados";}
    }
  }

  private void leer(Path ruta) throws IOException {
    Properties p=new Properties();
    try(Reader r=Files.newBufferedReader(ruta,StandardCharsets.UTF_8)){p.load(r);}
    String version=p.getProperty("version");
    if(!"1".equals(version)&&!"2".equals(version))throw new IOException("Versión desconocida");
    boolean nuevoPikachu=Boolean.parseBoolean(p.getProperty("pikachu","false"));
    int nuevoRecord=Integer.parseInt(p.getProperty("record","0"));
    int nuevoInfinito=Integer.parseInt(p.getProperty("recordInfinito","0"));
    int nuevasVictorias=Integer.parseInt(p.getProperty("victorias","0"));
    float nuevaMusica=Float.parseFloat(p.getProperty("musica","0.35"));
    float nuevosEfectos=Float.parseFloat(p.getProperty("efectos","0.65"));
    if(nuevoRecord<0||nuevoInfinito<0||nuevasVictorias<0||nuevaMusica<0||nuevaMusica>1||nuevosEfectos<0||nuevosEfectos>1)
      throw new IOException("Perfil fuera de rango");
    pikachu=nuevoPikachu;record=nuevoRecord;recordInfinito=nuevoInfinito;victorias=nuevasVictorias;
    musica=nuevaMusica;efectos=nuevosEfectos;
    ayuda=Boolean.parseBoolean(p.getProperty("ayuda","false"));
    reducido=Boolean.parseBoolean(p.getProperty("reducido","false"));
    contraste=Boolean.parseBoolean(p.getProperty("contraste","false"));
  }

  public boolean guardar() {
    try {
      Files.createDirectories(archivo.getParent());
      Properties p=new Properties();
      p.setProperty("version","2");p.setProperty("pikachu",""+pikachu);
      p.setProperty("record",""+record);p.setProperty("recordInfinito",""+recordInfinito);
      p.setProperty("victorias",""+victorias);p.setProperty("musica",""+musica);
      p.setProperty("efectos",""+efectos);p.setProperty("ayuda",""+ayuda);
      p.setProperty("reducido",""+reducido);p.setProperty("contraste",""+contraste);
      Path temporal=archivo.resolveSibling("perfil.tmp");
      try(Writer w=Files.newBufferedWriter(temporal,StandardCharsets.UTF_8)){p.store(w,"PokeRun");}
      if(Files.exists(archivo))Files.copy(archivo,archivo.resolveSibling("perfil.bak"),StandardCopyOption.REPLACE_EXISTING);
      Files.move(temporal,archivo,StandardCopyOption.REPLACE_EXISTING);
      aviso="";return true;
    }catch(IOException error){aviso="No fue posible guardar el perfil";return false;}
  }

  public void registrarResultado(boolean infinito, boolean victoria, int puntos) {
    if(puntos<0)throw new IllegalArgumentException("Puntaje negativo");
    if(infinito)recordInfinito=Math.max(recordInfinito,puntos);
    else {record=Math.max(record,puntos);if(victoria){pikachu=true;victorias++;}}
    guardar();
  }

  public void ajustar(int opcion) {
    switch(opcion) {
      case 1:musica=musica>=.99f?0:Math.min(1,musica+.25f);break;
      case 2:efectos=efectos>=.99f?0:Math.min(1,efectos+.25f);break;
      case 3:reducido=!reducido;break;
      case 4:contraste=!contraste;break;
      case 5:ayuda=!ayuda;break;
      default:throw new IllegalArgumentException("Ajuste inválido");
    }
    guardar();
  }

  public boolean tienePikachu(){return pikachu;}
  public boolean tieneInfinito(){return pikachu;}
  public boolean tieneAyuda(){return ayuda;}
  public boolean movimientoReducido(){return reducido;}
  public boolean altoContraste(){return contraste;}
  public float getMusica(){return musica;}
  public float getEfectos(){return efectos;}
  public int getRecord(){return record;}
  public int getRecordInfinito(){return recordInfinito;}
  public int getVictorias(){return victorias;}
  public String getAviso(){return aviso;}
  public String getRuta(){return archivo.toString();}
}
