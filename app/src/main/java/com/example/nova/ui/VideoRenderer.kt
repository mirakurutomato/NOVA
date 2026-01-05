package com.example.nova.ui

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class VideoRenderer(private val context: Context, private val onSurfaceTextureReady: (SurfaceTexture) -> Unit) :
    GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private var programId: Int = 0
    private var vertexBuffer: FloatBuffer? = null
    private var textureId: Int = 0
    private var surfaceTexture: SurfaceTexture? = null
    
    // Uniform locations
    private var uSigmaHandle: Int = 0
    private var uNHandle: Int = 0
    private var uBrightThreshHandle: Int = 0
    private var uBrightKHandle: Int = 0
    private var uGammaHandle: Int = 0

    // Enhancement parameters (default)
    var sigma: Float = 0.5f
    var n: Float = 15.0f
    var brightThresh: Float = 0.8f
    var brightK: Float = 0.08f
    var gamma: Float = 0.8f

    private val vertexShaderCode = """
        #version 300 es
        in vec4 aPosition;
        in vec2 aTexCoord;
        out vec2 vTexCoord;
        void main() {
            gl_Position = aPosition;
            vTexCoord = aTexCoord;
        }
    """.trimIndent()

    private val vertices = floatArrayOf(
        // X, Y, Z, U, V
        -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
         1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
        -1.0f,  1.0f, 0.0f, 0.0f, 1.0f,
         1.0f,  1.0f, 0.0f, 1.0f, 1.0f
    )

    init {
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        vertexBuffer?.position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val fragShaderCode = loadShaderFromAssets("shaders/retinex_shader.glsl")
        programId = createProgram(vertexShaderCode, fragShaderCode)

        val textures = IntArray(1)
        GLES30.glGenTextures(1, textures, 0)
        textureId = textures[0]
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        
        // Linear filtering
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR)
        GLES30.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR)
        
        surfaceTexture = SurfaceTexture(textureId)
        surfaceTexture?.setOnFrameAvailableListener(this)
        
        // Notify main thread/camera setup
        onSurfaceTextureReady(surfaceTexture!!)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        surfaceTexture?.updateTexImage()

        GLES30.glUseProgram(programId)

        // Bind attributes
        val aPosition = GLES30.glGetAttribLocation(programId, "aPosition")
        val aTexCoord = GLES30.glGetAttribLocation(programId, "aTexCoord")

        vertexBuffer?.position(0)
        GLES30.glVertexAttribPointer(aPosition, 3, GLES30.GL_FLOAT, false, 5 * 4, vertexBuffer)
        GLES30.glEnableVertexAttribArray(aPosition)

        vertexBuffer?.position(3)
        GLES30.glVertexAttribPointer(aTexCoord, 2, GLES30.GL_FLOAT, false, 5 * 4, vertexBuffer)
        GLES30.glEnableVertexAttribArray(aTexCoord)

        // Bind texture
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        
        // Update Uniforms
        uSigmaHandle = GLES30.glGetUniformLocation(programId, "uSigma")
        uNHandle = GLES30.glGetUniformLocation(programId, "uN")
        uBrightThreshHandle = GLES30.glGetUniformLocation(programId, "uBrightThresh")
        uBrightKHandle = GLES30.glGetUniformLocation(programId, "uBrightK")
        uGammaHandle = GLES30.glGetUniformLocation(programId, "uGamma")

        GLES30.glUniform1f(uSigmaHandle, sigma)
        GLES30.glUniform1f(uNHandle, n)
        GLES30.glUniform1f(uBrightThreshHandle, brightThresh)
        GLES30.glUniform1f(uBrightKHandle, brightK)
        GLES30.glUniform1f(uGammaHandle, gamma)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        // Request render is usually handled by GLSurfaceView.RENDERMODE_CONTINUOUSLY,
        // but if RENDERMODE_WHEN_DIRTY is used, call requestRender() here.
    }

    private fun loadShaderFromAssets(filename: String): String {
        return context.assets.open(filename).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).readText()
        }
    }

    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource)
        val fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource)
        val program = GLES30.glCreateProgram()
        GLES30.glAttachShader(program, vertexShader)
        GLES30.glAttachShader(program, fragmentShader)
        GLES30.glLinkProgram(program)
        return program
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES30.glCreateShader(type)
        GLES30.glShaderSource(shader, shaderCode)
        GLES30.glCompileShader(shader)
        
        // Check compile status (optional for prod, good for debug)
        val compiled = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            Log.e("VideoRenderer", "Could not compile shader $type:")
            Log.e("VideoRenderer", GLES30.glGetShaderInfoLog(shader))
            GLES30.glDeleteShader(shader)
            return 0
        }
        return shader
    }
}
