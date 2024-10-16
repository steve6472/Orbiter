package steve6472.orbiter.debug;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;
import steve6472.volkaniums.render.debug.DebugRender;
import steve6472.volkaniums.struct.Struct;

import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by steve6472
 * Date: 10/16/2024
 * Project: Orbiter <br>
 */
public class PrimitiveLineWindow
{
    private long window;
    private float cameraX = 0.0f, cameraY = 0.0f, cameraZ = 1.0f; // Camera position
    private float pitch = 0.0f, yaw = 0.0f; // Camera rotation angles

    public static void main(String[] args)
    {
        new PrimitiveLineWindow().run();
    }

    public void run()
    {
        init();
        loopInit();
    }

    public void cleanup()
    {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
    }

    private void init()
    {
        // Initialize GLFW
        if (!glfwInit())
        {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Keep window hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(800, 600, "3D Line Renderer", NULL, NULL);
        if (window == NULL)
        {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Set up a key callback for camera movement
        glfwSetKeyCallback(window, this::handleInput);

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Show the window
        glfwShowWindow(window);

        // Initialize OpenGL bindings
        GL.createCapabilities();

        // Enable depth testing for 3D rendering
        glEnable(GL_DEPTH_TEST);
    }

    private void handleInput(long window, int key, int scancode, int action, int mods)
    {
        float cameraSpeed = 0.25f;
        if (action == GLFW_PRESS || action == GLFW_REPEAT)
        {
            switch (key)
            {
                case GLFW_KEY_W:
                    cameraZ -= cameraSpeed;
                    break;
                case GLFW_KEY_S:
                    cameraZ += cameraSpeed;
                    break;
                case GLFW_KEY_A:
                    cameraX -= cameraSpeed;
                    break;
                case GLFW_KEY_D:
                    cameraX += cameraSpeed;
                    break;
                case GLFW_KEY_Q:
                    cameraY -= cameraSpeed;
                    break;
                case GLFW_KEY_E:
                    cameraY += cameraSpeed;
                    break;
                case GLFW_KEY_UP:
                    pitch -= 10.0f;
                    break;
                case GLFW_KEY_DOWN:
                    pitch += 10.0f;
                    break;
                case GLFW_KEY_LEFT:
                    yaw -= 10.0f;
                    break;
                case GLFW_KEY_RIGHT:
                    yaw += 10.0f;
                    break;
            }
        }
    }

    public void setPerspective(float fovY, float aspect, float zNear, float zFar)
    {
        float ymax = (float) (zNear * Math.tan(Math.toRadians(fovY / 2)));
        float xmax = ymax * aspect;
        glFrustum(-xmax, xmax, -ymax, ymax, zNear, zFar);
    }

    private void loopInit()
    {
        // Set the clear color to black and clear depth buffer
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Projection matrix (3D perspective)
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        setPerspective(90f, 800f / 600f, 0.1f, 1024f);
    }

    public void runFrame()
    {
        if (!shouldClose())
        {
            // Clear the framebuffer and depth buffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Set up the view matrix (camera transformation)
            updateCamera();

            // Call the method to render lines
            renderLines();

            // Swap the color buffers
            glfwSwapBuffers(window);

            // Poll for window events
            glfwPollEvents();
        }
    }

    public boolean shouldClose()
    {
        return glfwWindowShouldClose(window);
    }

    private void updateCamera()
    {
        // Set the modelview matrix (camera transformation)
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        // Rotate the camera based on pitch and yaw
        glRotatef(pitch, 1.0f, 0.0f, 0.0f); // Pitch (up/down)
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);   // Yaw (left/right)

        // Move the camera to its position
        glTranslatef(-cameraX, -cameraY, -cameraZ);
    }

    private void renderLines()
    {
        List<Struct> verticies = DebugRender.getInstance().createVerticies();
        if (verticies.isEmpty())
            return;

        DebugRender.getInstance().clearOldVerticies();

        // Begin rendering lines
        glBegin(GL_LINES);

        for (Struct vertex : verticies)
        {
            Vector3f pos = vertex.getMember(0, Vector3f.class);
            Vector4f color = vertex.getMember(1, Vector4f.class);

            glColor4f(color.x, color.y, color.z, color.w);
            glVertex3f(pos.x, pos.y, pos.z);
        }

        // End rendering
        glEnd();
    }
}
