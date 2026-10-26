package puppy.code.domain;
/** Prismeon, evolución propia de Eevee. Dañar su barrera produce recompensas. */
public final class Eevee {
 public static final int DANIO_POR_ORBE=60;
 private float tiempo,x=480,xAnterior=480,aturdimiento;
 private int nivel=1,ps=240,acumulado,recompensas;
 public void actualizar(float dt){if(!Float.isFinite(dt)||dt<0)throw new IllegalArgumentException("Tiempo inválido");tiempo+=dt;xAnterior=x;x=480+(float)Math.sin(tiempo*.55f)*160;
  if(aturdimiento>0){aturdimiento=Math.max(0,aturdimiento-dt);if(aturdimiento==0)ps=getPsMaximos();}}
 public int recibirDanio(int danio){
  if(danio<=0)throw new IllegalArgumentException("Daño inválido");if(aturdimiento>0)return 0;
  int aplicado=Math.min(ps,danio);ps-=aplicado;acumulado+=aplicado;
  while(acumulado>=DANIO_POR_ORBE){acumulado-=DANIO_POR_ORBE;recompensas++;}
  if(ps==0)aturdimiento=2.4f;return aplicado;
 }
 public int retirarRecompensas(){int n=recompensas;recompensas=0;return n;}
 public void evolucionar(int nivel){if(nivel<1||nivel>3)throw new IllegalArgumentException("Nivel inválido");this.nivel=nivel;ps=getPsMaximos();aturdimiento=0;}
 public Elemento getElemento(){return Elemento.values()[((int)(tiempo/8)+nivel-1)%4];}
 public float getX(){return x;}
 public int getPs(){return ps;}
 public int getPsMaximos(){return 180+nivel*60;}
 public int getDanioHastaOrbe(){return DANIO_POR_ORBE-acumulado;}
 public boolean estaAturdido(){return aturdimiento>0;}
 public Caja getHitbox(){return new Caja(x,573,50,52);}
 public Caja getHitboxAnterior(){return new Caja(xAnterior,573,50,52);}
}
