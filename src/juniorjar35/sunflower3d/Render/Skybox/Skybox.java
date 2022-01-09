package juniorjar35.sunflower3d.Render.Skybox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import juniorjar35.sunflower3d.Image.Decoder.AbstractImageDecoder;
import juniorjar35.sunflower3d.Utils.Deleteable;
import juniorjar35.sunflower3d.Utils.OpenGLUtils;

public class Skybox implements Deleteable{
	
	protected int tex;
	
	/**
	 * 
	 * @param textures Right texture, Left texture, Top texture, Bottom texture, Back texture, Front texture
	 */
	public Skybox(Class<? extends AbstractImageDecoder> dclass, String[] textures) {
		tex = OpenGLUtils.loadCubeMap(textures, dclass);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		
	}

	@Override
	public void delete() {
		if (GL11.glIsTexture(tex)) GL11.glDeleteTextures(tex);
	}
	
	
	
}
