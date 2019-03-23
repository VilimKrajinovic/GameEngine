package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

public class DisplayManager {

    private static final int WIDTH=1280;
    private static final int HEIGHT=720;
    private static final int FPS_CAP=200;

    public static void createDisplay(){

        //version of openGL
        ContextAttribs attributes = new ContextAttribs(3,2)
                .withForwardCompatible(true)
                .withProfileCore(true);


        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat(), attributes);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        //Tell openGL to use the whole viewport
        GL11.glViewport(0,0,WIDTH, HEIGHT);

    }

    //gets called every frame
    public static void updateDisplay(){
        Display.sync(FPS_CAP);
        Display.update();
    }

    public static void closeDisplay(){
        Display.destroy();
    }
}
