package puppy.code.domain;

/** Una técnica evoluciona en alcance, potencia y penetración, además de su nombre y aspecto. */
public enum Tecnica {
    ASCUAS(Elemento.FUEGO,1,"Ascuas"), LANZALLAMAS(Elemento.FUEGO,2,"Lanzallamas"), LLAMARADA(Elemento.FUEGO,3,"Llamarada"),
    BURBUJA(Elemento.AGUA,1,"Burbuja"), HIDROPULSO(Elemento.AGUA,2,"Hidropulso"), HIDROBOMBA(Elemento.AGUA,3,"Hidrobomba"),
    HOJA_AFILADA(Elemento.PLANTA,1,"Hoja afilada"), BOMBA_GERMEN(Elemento.PLANTA,2,"Bomba germen"), RAYO_SOLAR(Elemento.PLANTA,3,"Rayo solar"),
    IMPACTRUENO(Elemento.ELECTRICO,1,"Impactrueno"), RAYO(Elemento.ELECTRICO,2,"Rayo"), TRUENO(Elemento.ELECTRICO,3,"Trueno");
    private final Elemento elemento;
    private final int rango;
    private final String nombre;
    Tecnica(Elemento elemento,int rango,String nombre){this.elemento=elemento;this.rango=rango;this.nombre=nombre;}
    public static Tecnica para(Elemento elemento,int nivel){
        if(nivel<1||nivel>3)throw new IllegalArgumentException("Rango inválido");
        return values()[elemento.ordinal()*3+nivel-1];
    }
    public Elemento getElemento(){return elemento;}
    public int getRango(){return rango;}
    public String getNombre(){return nombre;}
    public int getDanio(){return rango==1?9:rango==2?16:26;}
    public float getRecarga(){return rango==1?.95f:rango==2?.82f:1.05f;}
    public float getRadio(){return 5+rango*3;}
    public int getPenetracion(){return rango;}
    public float getVelocidad(){return 440+rango*45;}
}
