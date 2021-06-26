package juniorjar35.sunflower3d.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryUtil;

import juniorjar35.sunflower3d.Render.RenderableObject;

public final class ModelLoader {
	
	private ModelLoader() {};
	
	private static RenderableObject load(AIScene ref) {
		if (ref == null) {
			throw new RuntimeException("Failed to load model!");
		}
		AIMesh mesh = AIMesh.create(ref.mMeshes().get(0));
		int vc = mesh.mNumVertices();
		AIVector3D.Buffer vertxs = mesh.mVertices();
		AIVector3D.Buffer norms = mesh.mNormals();
		
		float[] verts = new float[vc * 3];
		float[] normals = norms == null ? new float[0] : new float[norms.capacity() * 3];
		float[] texs = new float[vc * 2];
		for (int i = 0; i < vc; i++) {
			AIVector3D vertex = vertxs.get(i);
			verts[i * 3] = vertex.x();
			verts[i * 3 + 1] = vertex.y();
			verts[i * 3 + 2] = vertex.z();
			if (normals != null) {
				AIVector3D norm = norms.get(i);
				normals[i * 3] = norm.x();
				normals[i * 3 + 1] = norm.y();
				normals[i * 3 + 2] = norm.z();
			}
		}
		
		int faceCount = mesh.mNumFaces();
		AIFace.Buffer ind = mesh.mFaces();
		int[] indices = new int[faceCount * 3];
		for (int i = 0; i < faceCount; i++) {
			AIFace face = ind.get(i);
			indices[i * 3] = face.mIndices().get(0);
			indices[i * 3 + 1] = face.mIndices().get(1);
			indices[i * 3 + 2] = face.mIndices().get(2);
		}
		Assimp.aiFreeScene(ref);
		RenderableObject object = new RenderableObject(verts, indices, texs, normals);
		return object;
	}
	
	public static RenderableObject loadResource(String resource, String extension) throws IOException {
		ByteBuffer buffer = ResourceUtils.loadBufferDirect(resource);
		AIScene ref = Assimp.aiImportFileFromMemory(buffer, Assimp.aiProcess_Triangulate | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_FixInfacingNormals, extension);
		MemoryUtil.memFree(buffer);
		return load(ref);
	}
	
	public static RenderableObject loadResource(String resource) throws IOException {
		return loadResource(resource,ResourceUtils.getFileExtension(resource));
	}
	
	public static RenderableObject loadFile(String path) {
		AIScene ref = Assimp.aiImportFile(path, Assimp.aiProcess_Triangulate | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_FixInfacingNormals);
		return load(ref);
	}
	
	
}
