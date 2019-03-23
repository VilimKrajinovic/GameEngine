package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.Model;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.ObjLoader;
import terrain.Terrain;
import textures.ModelTexture;

public class MainGameLoop {
    public static void main(String[] args) {

        DisplayManager.createDisplay();

        Loader loader = new Loader();

        Model model = ObjLoader.loadObjModel("dragon", loader);
        TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("white")));
        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(1);

        Entity entity = new Entity(staticModel, new Vector3f(0, 0, -20), 0, 0, 0, 0.5f);
        Light light = new Light(new Vector3f(0, 0, -15.0f), new Vector3f(1, 1, 1));

        Terrain terrainOne = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("grass")));
        Terrain terrainTwo = new Terrain(1, 0, loader, new ModelTexture(loader.loadTexture("wood")));

        Camera camera = new Camera();

        MasterRenderer renderer = new MasterRenderer();
        while (!Display.isCloseRequested()) {
            //game logic
            entity.increaseRotation(0, 0.1f, 0f);
            camera.move();

            //render
            renderer.processTerrain(terrainOne);
            renderer.processTerrain(terrainTwo);

            renderer.processEntity(entity);
            renderer.render(light, camera);

            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }
}
