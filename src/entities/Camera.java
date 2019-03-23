package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

    private static final float SPEED_OF_CAMERA_MOVEMENT = 0.2f;
    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch;
    private float yaw;
    private float roll;

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public Camera() {
    }

    public void move() {
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            position.z -= SPEED_OF_CAMERA_MOVEMENT;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            position.x += SPEED_OF_CAMERA_MOVEMENT;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            position.z += SPEED_OF_CAMERA_MOVEMENT;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            position.x -= SPEED_OF_CAMERA_MOVEMENT;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            position.y -= SPEED_OF_CAMERA_MOVEMENT;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            position.y += SPEED_OF_CAMERA_MOVEMENT;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            roll += SPEED_OF_CAMERA_MOVEMENT;
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}
