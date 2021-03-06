package ce.core.shader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import ce.core.Camera;
import ce.core.Util;
import ce.core.maths.Matrix4f;
import ce.core.maths.Vector3f;

public abstract class Shader {

	private int program;
	private int vertex;
	private int fragment;

	private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4 * 4);

	private ArrayList<String> uniformsDetection = new ArrayList<String>();

	public Shader(String vertexShader, String fragmentShader) {
		vertex = loadShader(vertexShader, GL20.GL_VERTEX_SHADER);
		fragment = loadShader(fragmentShader, GL20.GL_FRAGMENT_SHADER);

		createProgram();
	}
	
	private void createProgram() {
		program = GL20.glCreateProgram();
		GL20.glAttachShader(program, vertex);
		GL20.glAttachShader(program, fragment);
	}

	public void bindAttribLocation(int index, String name) {
		GL20.glBindAttribLocation(program, index, name);
	}

	public void linkAndValidate() {
		GL20.glLinkProgram(program);
		GL20.glValidateProgram(program);
	}

	/**
	 * Used to activate the current shader
	 */
	public void bind() {
		GL20.glUseProgram(program);
	}

	/**
	 * Used to deactivate any active shader
	 */
	public void unbind() {
		GL20.glUseProgram(0);
	}
	
	public void loadMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4fv(location, false, matrixBuffer);
	}

	public void loadVector3f(int location, Vector3f vector) {
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}

	public void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}

	public int getUniformLocation(String uniform) {
		for (String s : uniformsDetection) {
			if (s.equals(uniform)) {
				return GL20.glGetUniformLocation(program, uniform);
			}
		}

		System.err.println("Uniform not found: " + uniform);
		return -1;
	}
	
	public Matrix4f createViewMatrix(Camera camera)
	{
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float)Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float)Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float)Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}

	/**
	 * loads the current shader type into memory
	 */
	private int loadShader(String fileName, int type) {
		StringBuilder sb = new StringBuilder();
		Exception exception = null;
		try {
			InputStream in = Util.loadInternal(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			br.close();
		} catch (Exception e) {
			exception = e;
			sb.append(fileName);
		}
		
		String previousString = "";
		String lastString = "";
		for (String s : sb.toString().split("\\s+")) {
			for (String s2 : s.split(";")) {
				if (lastString.equals("uniform")) {
					String uniform = s2.replace(" ", "").replace(";", "");
					uniformsDetection.add(uniform);
				}
				lastString = previousString;
				previousString = s2;
			}
		}
		
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, sb);
		GL20.glCompileShader(shaderID);
		
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			exception.printStackTrace();
			System.err.println(GL20.glGetShaderInfoLog(shaderID));
		}
		
		return shaderID;
	}
	
	public void dispose()
	{
		unbind();
		GL20.glDetachShader(program, vertex);
		GL20.glDetachShader(program, fragment);
		GL20.glDeleteShader(vertex);
		GL20.glDeleteShader(fragment);
		GL20.glDeleteProgram(program);
	}
}
