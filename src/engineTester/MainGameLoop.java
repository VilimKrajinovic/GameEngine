package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.Model;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.ObjFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.ObjLoader;
import terrain.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {
    public static void main(String[] args) {

        DisplayManager.createDisplay();

        Loader loader = new Loader();

        // TERRAIN TEXTURE

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        //

        ModelData dragonData = ObjFileLoader.loadOBJ("dragon");
        Model model = loader.loadToVAO(dragonData.getVertices(), dragonData.getTextureCoords(), dragonData.getNormals(), dragonData.getIndices());
        TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("white")));
        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(1);

        TexturedModel grass = new TexturedModel(ObjLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
        TexturedModel fern = new TexturedModel(ObjLoader.loadObjModel("fern", loader), new ModelTexture(loader.loadTexture("fern")));
        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUseFakeLighting(true);
        fern.getTexture().setHasTransparency(true);

        Entity dragon = new Entity(staticModel, new Vector3f(0, 0, -20), 0, 0, 0, 0.5f);
        Light light = new Light(new Vector3f(2000, 2000, -15.0f), new Vector3f(1, 1, 1));

        List<Entity> entities = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i<500; i++){
            entities.add(new Entity(grass, new Vector3f(random.nextFloat()*800 -400, 1.2f, -random.nextFloat() * 600), 180,0,0,1));
            entities.add(new Entity(fern, new Vector3f(random.nextFloat()*800 -400, 0, -random.nextFloat() * 600), 0,0,0,0.5f));
        }

        Terrain terrainOne = new Terrain(0, 0, loader, texturePack, blendMap,"heightmap");
        Terrain terrainTwo = new Terrain(-1, 0, loader, texturePack, blendMap,"heightmap");
        Terrain terrainThree = new Terrain(-1, -1, loader, texturePack, blendMap,"heightmap");
        Terrain terrainFour = new Terrain(0, -1, loader, texturePack, blendMap,"heightmap");


        Model dragonModel = ObjLoader.loadObjModel("dragon", loader);
        TexturedModel dragonTexturedModel = new TexturedModel(dragonModel, new ModelTexture(loader.loadTexture("white")));
        Player player = new Player(dragonTexturedModel, new Vector3f(0,0,-50), 0,0,0,1);

        Camera camera = new Camera(player);
        MasterRenderer renderer = new MasterRenderer();
        while (!Display.isCloseRequested()) {
            //game logic
            dragon.increaseRotation(0, 0.1f, 0f);
            camera.move();

            player.move(terrainOne);
            renderer.processEntity(player);

            //render
            renderer.processTerrain(terrainOne);
            renderer.processTerrain(terrainTwo);
            renderer.processTerrain(terrainThree);
            renderer.processTerrain(terrainFour);

            for (Entity grassEntity : entities) {
                renderer.processEntity(grassEntity);
            }

            renderer.processEntity(dragon);
            renderer.render(light, camera);

            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }
}
