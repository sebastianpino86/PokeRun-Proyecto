package puppy.code.domain;
/** Técnica y daño se capturan al disparar. La evolución aumenta tamaño y penetración. */
public final class ProyectilJugador {
 private final float x;private final Tecnica tecnica;private final int danio;
 private float y,anterior;private int penetracion;private boolean consumido;
 public ProyectilJugador(float x,Tecnica tecnica,int danio){this.x=x;this.tecnica=tecnica;this.danio=danio;penetracion=tecnica.getPenetracion();y=anterior=Jugador.Y+20;}
 public void actualizar(float dt){anterior=y;y+=tecnica.getVelocidad()*dt;}
 public boolean alcanza(EntidadCayendo e){return !terminado()&&!e.esRecolectable()&&Caja.barrido(e.getXAnterior(),e.getYAnterior(),e.getX(),e.getY(),e.getRadio(),new Caja(x,anterior,tecnica.getRadio()*2,24),new Caja(x,y,tecnica.getRadio()*2,24));}
 public boolean alcanza(Eevee rival){return !terminado()&&Caja.barrido(x,anterior,x,y,tecnica.getRadio(),rival.getHitboxAnterior(),rival.getHitbox());}
 public void perforar(){if(--penetracion<=0)consumido=true;}
 public void consumir(){consumido=true;}
 public boolean terminado(){return consumido||y>620;}
 public float getX(){return x;}public float getY(){return y;}
 public Tecnica getTecnica(){return tecnica;}public int getDanio(){return danio;}
}
