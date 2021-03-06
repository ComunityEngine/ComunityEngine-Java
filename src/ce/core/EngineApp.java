package ce.core;

import java.util.ArrayList;

import ce.core.graphics.GameObject;
import ce.core.graphics.Mesh;
import ce.core.input.Key;
import ce.core.maths.Transform;
import ce.core.maths.Vector3f;
import ce.core.shader.Default3DShader;
import ce.core.texture.Texture;
import ce.core.texture.TextureLoader;
import ce.core.ui.InternalWindow;
import ce.core.ui.NanoGui;
import ce.core.ui.Node;
import ce.core.ui.Rectangle;
import ce.core.ui.UIScene;
import ce.core.ui.VBox;

public class EngineApp {

	private Window window;
	private Scene scene;

	private Camera camera;
	private Default3DShader shader;
	private Texture defaultTexture;
	private Mesh mesh;
	private GameObject object;

	private ArrayList<Node> nodes = new ArrayList<Node>();
	
	private VBox vbox;
	private UIScene uiScene;
	
	public EngineApp(int width, int height) {
		System.out.println(Version.getEngineVersion());
		scene = new Scene(width, height);
		window = Window.createWindow(scene, "[CE] CommunityEngine");
		scene.setClearColor(1, 0.5f, 0, 1);
		init();
		loop();
		dispose();
	}

	private void init() {
		window.enableDepthBuffer();
		window.enableStencilBuffer();

		camera = new Camera(window, 70f, 0.1f, 1000f);
		camera.setPosition(new Vector3f(0, 0, 20));
		shader = new Default3DShader();
		shader.bind();
		shader.loadMatrix(shader.getProjectionMatrix(), camera.getProjectionMatrix());
		shader.unbind();

		defaultTexture = TextureLoader.loadTexture("res/textures/default.png");

		float[] vertices = {
				//
				-0.5f, 0.5f, 0f, //
				-0.5f, -0.5f, 0f, //
				0.5f, -0.5f, 0f, //
				0.5f, 0.5f, 0f //
		};

		float[] texCoords = { 0, 0, //
				0, 1, //
				1, 1, //
				1, 0, //
		};

		int[] indices = {
				//
				0, 1, 2, //
				2, 3, 0//
		};

		mesh = new Mesh();
		mesh.add(vertices, texCoords, indices);

		object = new GameObject(new Transform(new Vector3f(0), new Vector3f(0), new Vector3f(1))) {
			public void update() {

			}
		};
		object.setTextureID(defaultTexture.getID());

		NanoGui.init();
		nodes.add(new InternalWindow(50, 50, 300, 400));
		
		vbox = new VBox();
		uiScene = new UIScene(vbox);
		vbox.add(new Rectangle(10, 10, 100, 25));
		vbox.add(new Rectangle(10, 10, 100, 25));
		vbox.add(new Rectangle(10, 10, 100, 25));
		vbox.add(new Rectangle(10, 10, 100, 25));
		vbox.add(new Rectangle(10, 10, 100, 25));
		vbox.add(new Rectangle(10, 10, 100, 25));
		vbox.add(new Rectangle(10, 10, 100, 25));
		vbox.add(new Rectangle(10, 10, 100, 25));
		vbox.add(new Rectangle(10, 10, 100, 25));
		vbox.add(new Rectangle(10, 10, 100, 25));
		vbox.add(new Rectangle(10, 10, 100, 25));
		vbox.add(new Rectangle(10, 10, 100, 25));
		vbox.add(new Rectangle(10, 10, 100, 25));
		vbox.add(new Rectangle(10, 10, 100, 25));
		vbox.add(new Rectangle(10, 10, 100, 25));
	}

	private void loop() {
		while (!window.isCloseRequested()) {
			if (window.input.isKeyReleased(Key.KEY_ESCAPE)) {
				break;
			}

			float SPEED = 0.01f;
			if (window.input.isKeyDown(Key.KEY_W)) {
				camera.getPosition().x += Math.sin(camera.getYaw() * Math.PI / 180) * SPEED; // * Time.getDelta();
				camera.getPosition().z += -Math.cos(camera.getYaw() * Math.PI / 180) * SPEED; // * Time.getDelta();
			}

			if (window.input.isKeyDown(Key.KEY_S)) {
				camera.getPosition().x -= Math.sin(camera.getYaw() * Math.PI / 180) * SPEED; // * Time.getDelta();
				camera.getPosition().z -= -Math.cos(camera.getYaw() * Math.PI / 180) * SPEED; // * Time.getDelta();
			}

			if (window.input.isKeyDown(Key.KEY_A)) {
				camera.getPosition().x += Math.sin((camera.getYaw() - 90) * Math.PI / 180) * SPEED; // * Time.getDelta();
				camera.getPosition().z += -Math.cos((camera.getYaw() - 90) * Math.PI / 180) * SPEED; // * Time.getDelta();
			}

			if (window.input.isKeyDown(Key.KEY_D)) {
				camera.getPosition().x += Math.sin((camera.getYaw() + 90) * Math.PI / 180) * SPEED; // * Time.getDelta();
				camera.getPosition().z += -Math.cos((camera.getYaw() + 90) * Math.PI / 180) * SPEED; // * Time.getDelta();
			}

			// if (window.input.isKeyDown(Key.KEY_G)) {
			// System.out.println("Holding G");
			// }

			if (window.input.isKeyPressed(Key.KEY_G)) {
				System.out.println("Realse G");
			}

			if (window.input.isKeyReleased(Key.KEY_H)) {
				System.out.println("Release H");
			}

			if (window.input.isKeyPressed(Key.KEY_L)) {
				System.out.println("Pressed L");
			}

			camera.update();
			shader.bind();
			shader.loadViewMatrix(camera);
			{
				mesh.enable();
				{
					mesh.render(shader, shader.getModelMatrix(), object, camera);
				}
				mesh.disable();
			}
			shader.unbind();

			NanoGui.enable(window.getWidth(), window.getHeight());
//			for (Node n : nodes) {
//				n.update();
//			}
			uiScene.update();
			NanoGui.disable();

			window.update();
		}
	}

	private void dispose() {
		NanoGui.disable();
		NanoGui.dispose();
		defaultTexture.dispose();
		mesh.disable();
		mesh.dispose();
		shader.unbind();
		shader.dispose();
		window.close();
		window.disposeGLFW();
	}

}
