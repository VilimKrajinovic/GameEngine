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

        Terrain terrainOne = new Terrain(0, 0, loader, texturePack, blendMap, "heightmap");
        Terrain terrainTwo = new Terrain(1, 0, loader, texturePack, blendMap, "heightmap");
        Terrain terrainThree = new Terrain(1, 1, loader, texturePack, blendMap, "heightmap");
        Terrain terrainFour = new Terrain(0, 1, loader, texturePack, blendMap, "heightmap");

        Terrain[][] terrains = new Terrain[2][2];
        terrains[0][0] = terrainOne;
        terrains[1][0] = terrainTwo;
        terrains[1][1] = terrainThree;
        terrains[0][1] = terrainFour;

        populateWorldWithGrassAndFerns(grass, fern, entities, random, terrains);


        Model dragonModel = ObjLoader.loadObjModel("dragon", loader);
        TexturedModel dragonTexturedModel = new TexturedModel(dragonModel, new ModelTexture(loader.loadTexture("white")));
        Player player = new Player(dragonTexturedModel, new Vector3f(0, 0, 0), 0, 0, 0, 1);

        Camera camera = new Camera(player);
        MasterRenderer renderer = new MasterRenderer();
        while (!Display.isCloseRequested()) {
            //game logic
            dragon.increaseRotation(0, 0.1f, 0f);
            camera.move();

//            System.out.println("x: " + player.getPosition().x + "  z: " + player.getPosition().z);
            player.move(getTerrainAtWorldCoordinates(player.getPosition().x, player.getPosition().z, terrains));

            if (checkCollision(player, dragon)){
                System.out.println("Collision between player and dragon!!!");
                player.pickUp(dragon);

            }

            //render
            renderer.processEntity(player);

            renderer.processTerrain(terrainOne);
            renderer.processTerrain(terrainTwo);
            renderer.processTerrain(terrainThree);
            renderer.processTerrain(terrainFour);

            for (Entity grassEntity : entities) {
                renderer.processEntity(grassEntity);
            }

            renderer.processEntity(numberOne);
            renderer.processEntity(numberTwo);
            renderer.processEntity(dragon);
            renderer.render(light, camera);

            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }

    private static void populateWorldWithGrassAndFerns(TexturedModel grass, TexturedModel fern, List<Entity> entities, Random random, Terrain[][] terrains) {
        for (int i = 0; i < 10000; i++) {
            if (i % 2 == 0) {
                float x = random.nextFloat() * 1600;
                float z = random.nextFloat() * 1600;
                Terrain currentTerrain = getTerrainAtWorldCoordinates(x, z, terrains);
                float y = currentTerrain.getHeightOfTerrain(x, z);
                entities.add(new Entity(grass, new Vector3f(x, y, z), 180, 0, 0, 1));
            }
            if (i % 5 == 0) {
                float x = random.nextFloat() * 1600;
                float z = random.nextFloat() * 1600;
                Terrain currentTerrain = getTerrainAtWorldCoordinates(x, z, terrains);
                float y = currentTerrain.getHeightOfTerrain(x, z);
                entities.add(new Entity(fern, new Vector3f(x, y, z), 0, 0, 0, 0.5f));
            }
        }
    }

    private static boolean checkCollision(Player player, Entity entity) {
        if (checkXAxis(player, entity))
            return checkZAxis(player, entity);
        return false;
    }

    private static boolean checkZAxis(Player player, Entity entity) {
        return player.getPosition().z >= entity.getPosition().z - 5 && player.getPosition().z <= entity.getPosition().z + 5;
    }

    private static boolean checkXAxis(Player player, Entity entity) {
        return player.getPosition().x >= entity.getPosition().x - 5 && player.getPosition().x <= entity.getPosition().x + 5;
    }

    private static Terrain getTerrainAtWorldCoordinates(float worldX, float worldZ, Terrain[][] terrains) {
        if (worldX > 1600 || worldZ > 1600) {
            return null;
        }
        int x = (int) (worldX / Terrain.SIZE);
        int z = (int) (worldZ / Terrain.SIZE);
        return terrains[x][z];
    }
}
