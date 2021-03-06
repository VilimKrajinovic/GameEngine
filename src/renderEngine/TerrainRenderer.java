package renderEngine;

import entities.Entity;
import helpers.Maths;
import models.Model;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import shaders.TerrainShader;
import terrain.Terrain;
import textures.ModelTexture;
import textures.TerrainTexturePack;

import java.util.List;

public class TerrainRenderer {

    private TerrainShader shader;
    public TerrainRenderer(TerrainShader terrainShader, Matrix4f projectionMatrix){
        this.shader = terrainShader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();
        shader.stop();
    }

    public void render(List<Terrain> terrains){
        for (Terrain terrain : terrains) {
            prepareTerrain(terrain);
            loadModelMatrix(terrain);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT,0);
            unbindTexturedModel();
        }
    }


    private void prepareTerrain(Terrain terrain) {
        Model model = terrain.getModel();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);//positions
        GL20.glEnableVertexAttribArray(1);//textures
        GL20.glEnableVertexAttribArray(2);//normals
        bindTextures(terrain);
        shader.loadShineVariables(1, 0);
    }

    private void bindTextures(Terrain terrain){
        TerrainTexturePack texturePack = terrain.getTexturePack();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureId());

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getTextureId());

        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getTextureId());

        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getTextureId());

        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureId());
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);//unbind
    }

    private void loadModelMatrix(Terrain terrain) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(),0,terrain.getZ()), // create a transformation matrix
                0, 0, 0, 1);
        shader.loadTransformationMatrix(transformationMatrix);
    }

}
