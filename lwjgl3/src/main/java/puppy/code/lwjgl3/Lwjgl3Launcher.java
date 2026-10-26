package puppy.code.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import puppy.code.GameLluviaMenu;

/** Punto de entrada del juego de escritorio. */
public final class Lwjgl3Launcher {
  public static void main(String[] args) {
    Lwjgl3ApplicationConfiguration c=new Lwjgl3ApplicationConfiguration();
    c.setTitle("PokéRun - La senda Varitipo");
    c.setWindowedMode(960,720);
    c.setWindowSizeLimits(800,600,-1,-1);
    c.useVsync(true);c.setForegroundFPS(120);c.setIdleFPS(30);
    c.setWindowIcon("libgdx128.png","libgdx64.png","libgdx32.png","libgdx16.png");
    new Lwjgl3Application(new GameLluviaMenu(),c);
  }
}
