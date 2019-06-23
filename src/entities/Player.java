package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrain.Terrain;

import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {

    private static final float RUN_SPEED = 200;
    private static final float TURN_SPEED = 160;
    private static final float GRAVITY = -50;
    private static final float JUMP_POWER = 30;

    private static float TERRAIN_HEIGHT = 0;


    private boolean isInAir = false;
    private float upwardsSpeed = 0;
    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private List<Entity> inventory = new ArrayList<>();

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void move(Terrain terrain){
        checkInputs();
        super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx, 0, dz);
        upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0,upwardsSpeed * DisplayManager.getFrameTimeSeconds(),0);
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if(super.getPosition().y < terrainHeight){
            upwardsSpeed = 0;
            super.getPosition().y = terrainHeight;
            isInAir = false;
        }
    }

    private void checkInputs(){
        if(Keyboard.isKeyDown(Keyboard.KEY_W)){
            this.currentSpeed = RUN_SPEED;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
            this.currentSpeed = - RUN_SPEED;
        }else{
            this.currentSpeed = 0;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_D)){
            this.currentTurnSpeed = -TURN_SPEED;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            this.currentTurnSpeed = TURN_SPEED;
        }else{
            this.currentTurnSpeed = 0;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !isInAir){
            jump();
        }
    }

    public void pickUp(Entity entity){
        System.out.println("Currently there are " + inventory.size() + " items in inventory");
        if(!inventory.contains(entity)){
            System.out.println("Picked up entity");
            inventory.add(entity);
            entity.setShouldRender(false);
        }
    }

    private void jump() {
        upwardsSpeed = JUMP_POWER;
        isInAir = true;
    }
}
