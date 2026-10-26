package puppy.code.presentation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import puppy.code.persistence.GestorPerfil;
import puppy.code.domain.Tecnica;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import puppy.code.domain.Elemento;

/** Dibujo e interacción en coordenadas virtuales 960x720, también al redimensionar. */
public final class Lienzo implements Disposable {
  public static final Color TINTA = Color.valueOf("102D31"),
      PANEL = Color.valueOf("102D31"),
      BLANCO = Color.valueOf("EFF5DC"),
      SUAVE = Color.valueOf("A9C6B7"),
      MENTA = Color.valueOf("8DE6B4"),
      ORO = Color.valueOf("F7CB74");
  private static final Color[] TIPOS = {
    Color.valueOf("FF916F"),
    Color.valueOf("75D4F3"),
    Color.valueOf("B8E279"),
    Color.valueOf("FFE08A")
  };
  private final SpriteBatch batch = new SpriteBatch();
  private final FitViewport viewport = new FitViewport(960, 720);
  private final Texture pixel, circulo;
  private final BitmapFont regular, negrita;
  private final Map<String, Texture> sprites = new HashMap<>();
  private final Map<String, TextureRegion[]> cuadros = new HashMap<>();
  private final Map<String, float[]> tiempos = new HashMap<>();
  private final GlyphLayout medida = new GlyphLayout();
  private final Vector2 puntero = new Vector2();
  private boolean clic;

  public Lienzo() {
    Pixmap p=new Pixmap(2,2,Pixmap.Format.RGBA8888);
    p.setColor(1,1,1,1);p.fill();pixel=new Texture(p);p.dispose();
    Pixmap c=new Pixmap(64,64,Pixmap.Format.RGBA8888);
    c.setColor(1,1,1,1);c.fillCircle(32,32,32);
    circulo=new Texture(c);c.dispose();
    circulo.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
    regular=new BitmapFont(Gdx.files.internal("fonts/regular.fnt"));
    negrita=new BitmapFont(Gdx.files.internal("fonts/bold.fnt"));
    regular.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
    negrita.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
    viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),true);
  }

  private TextureRegion cuadroPokemon(String especie,float tiempo) {
    if(!cuadros.containsKey(especie)) {
      Texture textura=new Texture(Gdx.files.internal("sprites/"+especie+".png"));
      textura.setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);
      sprites.put(especie,textura);
      Properties p=new Properties();
      try {p.load(new StringReader(Gdx.files.internal("sprites/"+especie+".properties").readString("UTF-8")));}
      catch(IOException ex){throw new IllegalStateException("Sprite inválido: "+especie,ex);}
      int n=Integer.parseInt(p.getProperty("frames"));
      String[] duraciones=p.getProperty("durations").split(",");
      TextureRegion[] fotos=new TextureRegion[n];float[] limites=new float[n];float total=0;
      for(int i=0;i<n;i++) {
        fotos[i]=new TextureRegion(textura,(i%8)*96,(i/8)*96,96,96);
        total+=Integer.parseInt(duraciones[i])/1000f;limites[i]=total;
      }
      cuadros.put(especie,fotos);tiempos.put(especie,limites);
    }
    TextureRegion[] fotos=cuadros.get(especie);float[] limites=tiempos.get(especie);
    float t=Math.max(0,tiempo)%limites[limites.length-1];
    for(int i=0;i<limites.length;i++)if(t<limites[i])return fotos[i];
    return fotos[fotos.length-1];
  }

  public void comenzar() {
    ScreenUtils.clear(.025f, .07f, .08f, 1);
    viewport.apply();
    batch.setProjectionMatrix(viewport.getCamera().combined);
    puntero.set(Gdx.input.getX(), Gdx.input.getY());
    viewport.unproject(puntero);
    clic = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    batch.begin();
  }

  public void terminar() {
    batch.setColor(Color.WHITE);
    batch.end();
  }

  public void resize(int w, int h) {
    viewport.update(w, h, true);
  }

  public void rect(float x, float y, float w, float h, Color c) {
    rect(x, y, w, h, c, 1);
  }

  public void rect(float x, float y, float w, float h, Color c, float alpha) {
    batch.setColor(c.r, c.g, c.b, alpha);
    batch.draw(pixel, x, y, w, h);
  }

  public void circulo(float x, float y, float radio, Color color, float alpha) {
    batch.setColor(color.r, color.g, color.b, alpha);
    batch.draw(circulo, x - radio, y - radio, radio * 2, radio * 2);
  }

  public void linea(
      float ax, float ay, float bx, float by, float grosor, Color color, float alpha) {
    float dx = bx - ax, dy = by - ay;
    batch.setColor(color.r, color.g, color.b, alpha);
    batch.draw(
        pixel,
        ax,
        ay,
        0,
        grosor / 2,
        (float) Math.sqrt(dx * dx + dy * dy),
        grosor,
        1,
        1,
        (float) Math.toDegrees(Math.atan2(dy, dx)),
        0,
        0,
        2,
        2,
        false,
        false);
  }

  public void rombo(float x, float y, float tam, Color color, float alpha) {
    batch.setColor(color.r, color.g, color.b, alpha);
    batch.draw(
        pixel,
        x - tam / 2,
        y - tam / 2,
        tam / 2,
        tam / 2,
        tam,
        tam,
        1,
        1,
        45,
        0,
        0,
        2,
        2,
        false,
        false);
  }

  public void panel(float x, float y, float w, float h, Color color) {
    // Esquinas escalonadas de pixel art; sin uniones transparentes entre círculos.
    rect(x + 8, y, w - 16, h, color);
    rect(x, y + 8, w, h - 16, color);
    rect(x + 3, y + 3, w - 6, h - 6, color);
  }

  public void borde(float x, float y, float w, float h, Color c, float grosor) {
    rect(x, y, w, grosor, c);
    rect(x, y + h - grosor, w, grosor, c);
    rect(x, y, grosor, h, c);
    rect(x + w - grosor, y, grosor, h, c);
  }

  public void texto(String texto, float x, float y, float tam, Color c, boolean bold) {
    BitmapFont f = (bold ? negrita : regular);
    f.getData().setScale(tam / 32);
    f.setColor(c);
    f.draw(batch, texto, x, y);
  }

  public void centrado(String texto, float x, float y, float tam, Color c, boolean bold) {
    BitmapFont f = (bold ? negrita : regular);
    f.getData().setScale(tam / 32);
    medida.setText(f, texto);
    texto(texto, x - medida.width / 2, y, tam, c, bold);
  }

  public boolean encima(float x, float y, float w, float h) {
    return puntero.x >= x && puntero.x <= x + w && puntero.y >= y && puntero.y <= y + h;
  }

  public boolean boton(String label, float x, float y, float w, float h, boolean activo) {
    boolean hover = encima(x, y, w, h);
    panel(x, y, w, h, activo ? (hover ? Color.valueOf("B2F2C8") : MENTA) : Color.valueOf("36554E"));
    centrado(label, x + w / 2, y + h / 2 + 11, 19, activo ? TINTA : SUAVE, true);
    return activo && hover && clic;
  }

  public boolean pulsado(float x, float y, float w, float h) {
    return clic && encima(x, y, w, h);
  }

  public void pokemon(String especie, float cx, float pies, float tam, float tiempo, Color tint) {
    batch.setColor(tint);
    batch.draw(
        cuadroPokemon(especie,tiempo), cx - tam / 2, pies - 12 * tam / 96, tam, tam);
  }

  public void simbolo(Elemento tipo, float x, float y, float r, float alpha) {
    Color c = tipo(tipo);
    circulo(x, y, r + 5, c, alpha * .13f);
    circulo(x, y, r, c, alpha);
    // El símbolo escrito permite distinguir los tipos sin depender solo del color.
    centrado(tipo.getSimbolo(), x, y + r * .72f, Math.max(11, r * 1.25f), TINTA, true);
  }

  public Color tipo(Elemento e) {
    return TIPOS[e.ordinal()];
  }

  @Override public void dispose() {
    batch.dispose();regular.dispose();negrita.dispose();pixel.dispose();circulo.dispose();
    for(Texture sprite:sprites.values())sprite.dispose();
  }

  private static final Color[] BASE = {Color.valueOf("436B51"),Color.valueOf("33594F"),Color.valueOf("394B61")};
  private static final Color[] CAMINO = {Color.valueOf("71915B"),Color.valueOf("5B8266"),Color.valueOf("66798A")};
  private static final Color HOJA = Color.valueOf("264C3C"), LUZ = Color.valueOf("6DA46B"), OSCURO = Color.valueOf("173A31");

  public void paisaje(float tiempo, int nivel, GestorPerfil perfil) {
    float t = perfil.movimientoReducido() ? 0 : tiempo;
    if (nivel < 1 || nivel > 3) throw new IllegalArgumentException("Terreno fuera de 1..3");
    Color hoja = nivel == 1 ? HOJA : nivel == 2 ? Color.valueOf("225346") : Color.valueOf("41566C");
    Color luz = nivel == 1 ? LUZ : nivel == 2 ? Color.valueOf("4CA78B") : Color.valueOf("A6ACDE");
    this.rect(0, 0, 960, 720, BASE[nivel - 1]);
    this.rect(99, 88, 762, 534, OSCURO);
    this.rect(110, 88, 740, 534, perfil.altoContraste() ? Color.valueOf("15352F") : CAMINO[nivel - 1]);
    for (int i = 0; i < 12; i++) {
      float y = 92 + (i * 47 + t * 25) % 520;
      this.rect(124, y, 712, 2, Color.WHITE, perfil.altoContraste() ? .015f : .04f);
      for (int j = 0; j < 8; j++) {
        float x = 140 + j * 91 + (i % 2) * 18;
        this.rect(x, y + 18, 5, 3, Color.WHITE, .045f);
      }
    }
    for (int i = 0; i < 88; i++) {
      boolean derecha = i % 2 == 0;
      float x = derecha ? 865 + (i * 31) % 94 : -(i * 31) % 94 + 94;
      float y = (i * 83) % 720;
      float onda = (float) Math.sin(t * 1.8 + i) * 2;
      this.circulo(x, y, 20 + (i % 4) * 5, OSCURO, .45f);
      this.circulo(x - 5, y + 10, 22, hoja, 1);
      this.linea(x, y - 8, x - 13 + onda, y + 20, 5, luz, .70f);
      this.linea(x + 3, y - 9, x + 16 + onda, y + 24, 4, luz, .55f);
      this.linea(x - 2, y - 10, x + onda, y + 32, 5, hoja, 1);
      if (i % 7 == 0) {
        this.circulo(x + 7, y + 12, 3, Lienzo.ORO, .8f);
        this.circulo(x + 9, y + 15, 2, Lienzo.BLANCO, 1);
      }
    }
    // Siluetas exclusivamente en los bordes. Los ataques y el jugador quedan despejados.
    for (int i = 0; i < 16; i++) {
      float x = i % 2 == 0 ? 34 + i % 3 * 11 : 920 - i % 3 * 11;
      float y = (i * 79 + (int)(t * 5)) % 700;
      if (nivel == 1) {
        this.circulo(x, y, 4 + (i % 2) * 2, Lienzo.ORO, .55f);
        this.linea(x, y-7, x, y-15, 2, luz, .6f);
      } else if (nivel == 2) {
        this.linea(x, y-15, x, y+18, 6, Color.valueOf("173C34"), .8f);
        this.circulo(x+3, y+16, 13, hoja, .7f);
        this.circulo(x-6, y+12, 11, luz, .45f);
      } else {
        this.rombo(x, y, 16+(i%3)*6, this.tipo(puppy.code.domain.Elemento.values()[i%4]), .5f);
        this.rombo(x, y+5, 5, Lienzo.BLANCO, .55f);
      }
    }
    for (int i = 0; i < 14; i++) {
      float x = (i * 71 + 31) % 960, y = 110 + (i * 137 + t * 12) % 485;
      this.circulo(x, y, 1.8f, Lienzo.ORO, .2f + (float) Math.abs(Math.sin(t + i)) * .25f);
    }
  }
  public void prismeon(float x, float y, float t, Elemento tipo, boolean aturdido) {
    Color color = this.tipo(tipo);
    this.circulo(x, y - 24, 47, Color.BLACK, .2f);
    this.circulo(x, y, 55, color, .1f);
    for (int i = 0; i < 8; i++) {
      float a = i * .7854f;
      this.rombo(x + (float)Math.cos(a)*35, y + (float)Math.sin(a)*23, 17,
          this.tipo(Elemento.values()[i % 4]), aturdido ? .35f : .8f);
    }
    this.pokemon("eevee", x, y - 30, 92, t, aturdido ? Color.valueOf("AEC7CD") : Color.valueOf("D6E9F1"));
    this.rombo(x - 20, y + 38, 9, Lienzo.MENTA, 1);
    this.rombo(x + 17, y + 38, 9, Lienzo.ORO, 1);
    this.rombo(x, y + 28, 9, color, 1);
    this.rombo(x - 23, y - 15, 11, this.tipo(Elemento.AGUA), .95f);
    this.rombo(x + 25, y - 14, 11, this.tipo(Elemento.FUEGO), .95f);
    for (int i = 0; i < 4; i++) {
      float a = t * .9f + i * 1.5708f;
      this.rombo(x + (float)Math.cos(a)*62, y + (float)Math.sin(a)*16, 7,
          this.tipo(Elemento.values()[i]), .85f);
    }
  }
  public void tecnica(Tecnica tecnica, float x, float y, float radio, float t, boolean jugador) {
    int rango = tecnica.getRango();
    float r = radio + 3, cola = jugador ? -1 : 1;
    Color c = this.tipo(tecnica.getElemento());
    this.circulo(x, y, r + 6, c, .13f);
    switch (tecnica.getElemento()) {
      case FUEGO:
        for(int i=0;i<rango+1;i++) {
          float dx=(i-rango*.5f)*7;
          this.rombo(x+dx,y+cola*(10+i*5),r+6,c,.8f);
        }
        this.circulo(x,y,r,c,1);
        this.rombo(x,y+cola*5,r*.85f,Lienzo.ORO,1);
        this.circulo(x,y-cola*3,r*.4f,Lienzo.BLANCO,.9f);
        break;
      case AGUA:
        if(rango == 1) {
          anillo(x,y,r,c);
          anillo(x+13,y+cola*17,5,c);
          anillo(x-8,y+cola*27,4,c);
        } else {
          this.linea(x,y,x,y+cola*(20+rango*8),r*1.2f,c,.6f);
          anillo(x,y,r+2,c);
          this.circulo(x-3,y+3,r*.45f,Lienzo.BLANCO,.8f);
          for(int i=1;i<=rango;i++) anillo(x+(i%2==0?-6:6),y+cola*i*12,5,c);
        }
        break;
      case PLANTA:
        if(rango == 3) {
          this.linea(x,y,x,y+cola*48,12,Lienzo.ORO,.65f);
          this.linea(x,y,x,y+cola*48,4,Lienzo.BLANCO,.9f);
          this.circulo(x,y,r,Lienzo.ORO,1);
          this.circulo(x,y,r*.5f,Lienzo.BLANCO,1);
        } else if(rango == 2) {
          this.circulo(x,y,r,c,1);
          this.rombo(x-3,y+6,r*.65f,Lienzo.ORO,.8f);
          this.linea(x,y+r,x+7,y+r+7,3,c,1);
        } else {
          this.rombo(x-5,y+4,r+6,c,1);
          this.rombo(x+7,y-4,r+3,c,1);
          this.linea(x-12,y+12,x+12,y-12,2,Lienzo.TINTA,.6f);
        }
        break;
      case ELECTRICO:
        float fase=(float)Math.sin(t*20)*3;
        for(int i=0;i<rango;i++) {
          float dx=(i-(rango-1)*.5f)*9;
          this.linea(x+dx+5,y+22+fase,x+dx-6,y+6,4,c,1);
          this.linea(x+dx-6,y+6,x+dx+6,y-4,4,c,1);
          this.linea(x+dx+6,y-4,x+dx-5,y-22-fase,4,c,1);
        }
        this.circulo(x,y,4,Lienzo.BLANCO,1);
        break;
      default: break;
    }
    if(!jugador) {
      this.circulo(x+19,y-9,7,Lienzo.PANEL,.9f);
      this.centrado(tecnica.getElemento().getSimbolo(),x+19,y-4,9,Lienzo.BLANCO,true);
    }
  }
  private void anillo(float x,float y,float r,Color color) {
    this.circulo(x,y,r,color,.22f);
    for(int i=0;i<20;i++) {
      float a=i*.314159f,b=(i+1)*.314159f;
      this.linea(x+(float)Math.cos(a)*r,y+(float)Math.sin(a)*r,
          x+(float)Math.cos(b)*r,y+(float)Math.sin(b)*r,2,color,1);
    }
    this.circulo(x-r*.35f,y+r*.35f,Math.max(2,r*.18f),Lienzo.BLANCO,.9f);
  }
}
