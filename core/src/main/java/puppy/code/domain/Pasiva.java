package puppy.code.domain;

public enum Pasiva {
    ESCUDO("Escudo","S",3), REGENERACION("Regeneración","+",6), IMAN("Imán","I",8), POTENCIA("Potencia","!",8);
    private final String nombre,simbolo;
    private final float duracion;
    Pasiva(String nombre,String simbolo,float duracion){this.nombre=nombre;this.simbolo=simbolo;this.duracion=duracion;}
    public String getNombre(){return nombre;}
    public String getSimbolo(){return simbolo;}
    public float getDuracion(){return duracion;}
}
