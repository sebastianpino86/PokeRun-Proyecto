package puppy.code.domain;
import java.util.*;
/** Controlador de campaña e infinito. No conoce gráficos, audio ni archivos. */
public final class Partida {
 public static final int JUGANDO=0, PAUSA=1, EVOLUCION=2, VICTORIA=3, DERROTA=4;
 public static final int SIN_EVENTO=0, DANIO=1, CURACION=2, BLOQUEO=3, DISPARO=4, DASH=5,
 ESCUDO=6, DESTRUIDO=7, EVOLUCION_EVENTO=8, VICTORIA_EVENTO=9, DERROTA_EVENTO=10,
 DANIO_RIVAL=11, PASIVA_EVENTO=12;
 private final Jugador jugador;private final Eevee rival=new Eevee();
 private final DirectorAtaques director=new DirectorAtaques(new ConstructorAtaque());
 private float tiempoDificultad, desdeDanio, dominio;
 private int ultimoEvento;

 private final List<EntidadCayendo> entidades=new ArrayList<>();private final List<Ataque> avisos=new ArrayList<>();
 private final List<ProyectilJugador> proyectiles=new ArrayList<>();
 private final List<EntidadCayendo> vistaEntidades=Collections.unmodifiableList(entidades);
 private final List<Ataque> vistaAvisos=Collections.unmodifiableList(avisos);
 private final List<ProyectilJugador> vistaProyectiles=Collections.unmodifiableList(proyectiles);
 private final Random azar;private final boolean ayuda,infinito;
 private Nivel nivel=Nivel.numero(1);private int estado=JUGANDO;
 private float distancia,tiempo,proximaCaida=1.8f,proximaBaya=7,proximaOleada=12;
 private int puntos,impactos,curaciones,destruidos,danioRival,pasivasRecogidas,numeroOleada;
 private String patron="LLUVIA ELEMENTAL";
 public Partida(Inicial i,long s,boolean a){this(i,s,a,false);}
 public Partida(Inicial i,long s,boolean a,boolean infinito){jugador=new Jugador(i);azar=new Random(s);ayuda=a;this.infinito=infinito;}
 public void actualizar(float dt,float eje){
  if(!Float.isFinite(dt)||dt<0||dt>.05f)throw new IllegalArgumentException("Usar pasos de hasta 50 ms");
  if(estado!=JUGANDO)return;
  tiempo+=dt;distancia+=10*dt;if(!infinito||nivel.getNumero()<3)distancia=Math.min(nivel.getLongitud(),distancia);
  jugador.actualizar(dt,eje);rival.actualizar(dt);actualizarDificultad(dt);generarAmenazas(dt);
  for(Iterator<Ataque> it=avisos.iterator();it.hasNext();){Ataque a=it.next();a.actualizarAviso(dt);if(a.avisoTerminado()){entidades.add(a);it.remove();}}
  for(EntidadCayendo e:entidades){e.actualizar(dt);if(jugador.getPasiva(Pasiva.IMAN)>0)e.atraerHacia(jugador,dt);}
  actualizarDisparos(dt);resolverRecogidas();
  if(jugador.getPs()==0){estado=DERROTA;evento(DERROTA_EVENTO,jugador.getX(),Jugador.Y,0);}
  else if(distancia>=nivel.getLongitud()&&(!infinito||nivel.getNumero()<3)){
   entidades.clear();avisos.clear();proyectiles.clear();puntos+=500;
   if(nivel.getNumero()==3){estado=VICTORIA;evento(VICTORIA_EVENTO,480,350,0);}
   else{estado=EVOLUCION;evento(EVOLUCION_EVENTO,480,350,0);if(infinito)avanzarNivel();}
  }
 }
 private void generarAmenazas(float dt){
  proximaCaida-=dt;proximaBaya-=dt;proximaOleada-=dt;
  if(proximaBaya<=0){entidades.add(new Baya(150+azar.nextFloat()*660,530,125));proximaBaya=jugador.getPs()<35?6:10;}
  if(rival.estaAturdido()){proximaCaida=Math.max(.5f,proximaCaida);proximaOleada=Math.max(1,proximaOleada);return;}
  if(proximaCaida<=0){Elemento tipo=azar.nextFloat()<.28f?jugador.getElemento():rival.getElemento();avisar(150+azar.nextInt(8)*(660f/7),tipo,0);proximaCaida+=intervalo();}
  if(proximaOleada<=0&&!esRespiro()){generarPatron();proximaOleada=intervaloOleada();}
 }
 private void generarPatron(){
  int fase=numeroOleada++%6,n=nivel.getNumero();Elemento tipo=rival.getElemento();
  if(fase==0){
    patron="ABANICO";
    float centro=Math.max(230,Math.min(730,jugador.getX()));
    for(int k=-1;k<=1;k++)avisar(centro+k*55,tipo,k*(n==1?15:32));
  }else if(fase==1){
    patron="BARRIDO ESCALONADO";
    int inicio=azar.nextInt(4);
    for(int k=0;k<(n==1?2:4);k++)avisar(150+(inicio+k)*94,tipo,n==1?0:15,k*.16f);
  }else if(fase==2){
    patron="PINZA";
    avisar(Math.max(150,jugador.getX()-150),tipo,24);
    avisar(Math.min(810,jugador.getX()+150),tipo,-24);
  }else if(fase==3){
    patron="SALVA ALTERNADA";
    int par=azar.nextInt(2);
    for(int k=0;k<(n==1?2:3);k++)avisar(150+(k*2+par)*94,tipo,0,k*.12f);
  }else if(fase==4){
    patron="ZIGZAG";
    int inicio=azar.nextInt(3);
    for(int k=0;k<(n==1?2:4);k++)
      avisar(190+(inicio+k)*82,tipo,k%2==0?-(n==1?12:34):(n==1?12:34),k*.19f);
  }else{
    patron="DOS FLANCOS";
    float x=Math.max(270,Math.min(690,jugador.getX()));
    avisar(x-120,tipo,-(n==1?8:25));
    avisar(x+120,tipo,n==1?8:25,.22f);
    if(n>=3)avisar(azar.nextBoolean()?170:790,tipo,0,.4f);
  }
 }
 private void avisar(float x,Elemento tipo,float deriva){avisar(x,tipo,deriva,0);}
 private void avisar(float x,Elemento tipo,float deriva,float demora){
  if(avisos.size()>=14||entidades.size()>=45)return;
  float velocidad=velocidad();
  Ataque aviso=director.crear(tipo,x,530,velocidad,nivel.getDanio(ayuda),deriva,nivel.getNumero());
  aviso.advertir(.8f+demora);
  avisos.add(aviso);
 }
 private void actualizarDisparos(float dt){
  List<Baya> nuevos=new ArrayList<>();
  for(ProyectilJugador p:proyectiles){p.actualizar(dt);
   for(EntidadCayendo e:entidades)if(!e.estaConsumida()&&p.alcanza(e)){e.consumir();p.perforar();puntos+=10;destruidos++;evento(DESTRUIDO,e.getX(),e.getY(),10);}
   if(p.alcanza(rival)){p.consumir();int dano=rival.recibirDanio(p.getDanio());danioRival+=dano;puntos+=dano*2;
    if(dano>0)evento(DANIO_RIVAL,rival.getX(),573,-dano);
    int n=rival.retirarRecompensas();for(int i=0;i<n;i++)nuevos.add(new Baya(rival.getX(),526,Pasiva.values()[azar.nextInt(Pasiva.values().length)]));
   }
  }
  entidades.addAll(nuevos);for(Iterator<ProyectilJugador> it=proyectiles.iterator();it.hasNext();)if(it.next().terminado())it.remove();
 }
 private void resolverRecogidas(){
  for(Iterator<EntidadCayendo> it=entidades.iterator();it.hasNext();){EntidadCayendo e=it.next();int antes=jugador.getPs();
   if(!e.estaConsumida()&&e.procesarColision(jugador)){
    int cambio=jugador.getPs()-antes;puntos+=e.puntosAlRecoger(jugador);
    if(cambio<0){impactos++;desdeDanio=0;evento(DANIO,jugador.getX(),Jugador.Y,cambio);}
    else if(e.getPasiva()!=null){pasivasRecogidas++;evento(PASIVA_EVENTO,jugador.getX(),Jugador.Y,e.getPasiva().ordinal());}
    else if(e.puntosAlRecoger(jugador)>0){curaciones++;evento(CURACION,jugador.getX(),Jugador.Y,cambio);}
    else evento(BLOQUEO,jugador.getX(),Jugador.Y,0);
   }
   if(e.estaConsumida()||e.estaFuera())it.remove();
  }
 }
 private void evento(int tipo,float x,float y,int valor){ultimoEvento=tipo;}
 public void atacar(){if(estado==JUGANDO&&jugador.lanzarAtaque()){proyectiles.add(new ProyectilJugador(jugador.getX(),jugador.getTecnica(),jugador.getDanioAtaque()));evento(DISPARO,jugador.getX(),Jugador.Y,0);}}
 public void dash(){if(estado==JUGANDO&&jugador.activarDash())evento(DASH,jugador.getX(),Jugador.Y,0);}
 public void escudo(){if(estado==JUGANDO&&jugador.activarEscudo())evento(ESCUDO,jugador.getX(),Jugador.Y,0);}
 public void pausar(){if(estado==JUGANDO)estado=PAUSA;}public void continuar(){if(estado==PAUSA)estado=JUGANDO;}
 public void avanzarNivel(){if(estado!=EVOLUCION)return;jugador.evolucionar();nivel=Nivel.numero(nivel.getNumero()+1);rival.evolucionar(nivel.getNumero());distancia=0;proximaCaida=2;proximaBaya=6;proximaOleada=10;estado=JUGANDO;}
 public int retirarEvento(){int resultado=ultimoEvento;ultimoEvento=SIN_EVENTO;return resultado;}
 public Jugador getJugador(){return jugador;}public Eevee getRival(){return rival;}public Nivel getNivel(){return nivel;}public int getEstado(){return estado;}public boolean esInfinito(){return infinito;}
 public float getProgreso(){
  if(infinito&&nivel.getNumero()==3)
   return Math.max(0,Math.min(1,(tiempo-145)/210f));
  return distancia/nivel.getLongitud();
 }
 public int getTerreno(){
  if(!infinito||nivel.getNumero()<3)return nivel.getNumero();
  return 1+(2+(int)Math.max(0,tiempo-145)/60)%3;
 }
 public int getOleada(){return 1+(int)tiempo/30;}
 public String getPresion(){return esRespiro()?"RESPIRO":dominio>.45f?"PRESIÓN ALTA":"EN COMBATE";}public String getPatron(){return patron;}
 public float getDistancia(){return distancia;}public float getTiempo(){return tiempo;}public int getPuntos(){return puntos+(int)getDistanciaTotal();}
 public float getDistanciaTotal(){return distancia+(nivel.getNumero()>1?600:0)+(nivel.getNumero()>2?850:0);}
 public int getImpactos(){return impactos;}public int getCuraciones(){return curaciones;}public int getDestruidos(){return destruidos;}
 public int getDanioRival(){return danioRival;}public int getPasivasRecogidas(){return pasivasRecogidas;}public boolean tieneAyuda(){return ayuda;}
 public List<EntidadCayendo> getEntidades(){return vistaEntidades;}public List<Ataque> getAvisos(){return vistaAvisos;}public List<ProyectilJugador> getProyectiles(){return vistaProyectiles;}

 /** Dificultad dinámica sin un intermediario adicional: datos propios de la partida. */
 private void actualizarDificultad(float dt){
   tiempoDificultad+=dt;desdeDanio+=dt;
   float objetivo=jugador.getPs()>=75&&desdeDanio>14?1:jugador.getPs()<35?-.8f:0;
   dominio+=(objetivo-dominio)*Math.min(1,dt*.18f);
 }
 private boolean esRespiro(){return tiempoDificultad>15&&tiempoDificultad%26>20;}
 private float velocidad(){
   float crecimiento=infinito?Math.min(.55f,tiempoDificultad/900):0;
   return Math.min(390,nivel.velocidad(getProgreso(),jugador.getPs(),ayuda)*(1+dominio*.10f+crecimiento));
 }
 private float intervalo(){
   float crecimiento=infinito?Math.min(.35f,tiempoDificultad/1400):0;
   return Math.max(.25f,nivel.intervalo(getProgreso(),jugador.getPs(),ayuda)*(esRespiro()?1.65f:1)/(1+dominio*.25f+crecimiento));
 }
 private float intervaloOleada(){
   return (nivel.getNumero()==1?13:nivel.getNumero()==2?9:6.5f)*(esRespiro()?1.5f:1)*(infinito?.85f:1);
 }
}
