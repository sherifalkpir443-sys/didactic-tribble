package com.nova.ai

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lightweight procedural 3D avatar preview rendered with OpenGL ES 2.0.
 * It is a stylized 3D placeholder, not a photorealistic imported character model.
 */
class Avatar3DView(context: Context) : GLSurfaceView(context) {
    private val avatarRenderer = AvatarRenderer()

    init {
        setEGLContextClientVersion(2)
        setRenderer(avatarRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun setPersona(persona: String) {
        avatarRenderer.setPersona(persona)
        requestRender()
    }

    override fun onPause() {
        super.onPause()
    }

    private class AvatarRenderer : Renderer {
        @Volatile private var female = false
        private val projection = FloatArray(16)
        private val view = FloatArray(16)
        private val viewProjection = FloatArray(16)
        private val model = FloatArray(16)
        private val mvp = FloatArray(16)
        private var program = 0
        private var positionHandle = 0
        private var normalHandle = 0
        private var mvpHandle = 0
        private var modelHandle = 0
        private var colorHandle = 0
        private val sphere = SphereMesh()

        fun setPersona(value: String) { female = value == "أنثى" }

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            val vertexShader = """
                uniform mat4 uMvp;
                uniform mat4 uModel;
                attribute vec3 aPosition;
                attribute vec3 aNormal;
                varying vec3 vNormal;
                void main() {
                    gl_Position = uMvp * vec4(aPosition, 1.0);
                    vNormal = mat3(uModel) * aNormal;
                }
            """.trimIndent()
            val fragmentShader = """
                precision mediump float;
                uniform vec4 uColor;
                varying vec3 vNormal;
                void main() {
                    vec3 n = normalize(vNormal);
                    float light = 0.42 + 0.58 * max(dot(n, normalize(vec3(-0.35, 0.65, 1.0))), 0.0);
                    gl_FragColor = vec4(uColor.rgb * light, uColor.a);
                }
            """.trimIndent()
            program = createProgram(vertexShader, fragmentShader)
            positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
            normalHandle = GLES20.glGetAttribLocation(program, "aNormal")
            mvpHandle = GLES20.glGetUniformLocation(program, "uMvp")
            modelHandle = GLES20.glGetUniformLocation(program, "uModel")
            colorHandle = GLES20.glGetUniformLocation(program, "uColor")
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)
            GLES20.glClearColor(0.055f, 0.09f, 0.15f, 1f)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
            val ratio = width.toFloat() / height.coerceAtLeast(1)
            Matrix.perspectiveM(projection, 0, 38f, ratio, 1f, 10f)
            Matrix.setLookAtM(view, 0, 0f, 0f, 4.4f, 0f, 0f, 0f, 0f, 1f, 0f)
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            GLES20.glUseProgram(program)
            Matrix.multiplyMM(viewProjection, 0, projection, 0, view, 0)

            val skin = if (female) floatArrayOf(0.91f, 0.69f, 0.59f, 1f)
                       else floatArrayOf(0.72f, 0.48f, 0.33f, 1f)
            val hair = if (female) floatArrayOf(0.16f, 0.10f, 0.20f, 1f)
                       else floatArrayOf(0.08f, 0.09f, 0.12f, 1f)
            val shadow = floatArrayOf(0.28f, 0.17f, 0.15f, 1f)
            val eye = floatArrayOf(0.035f, 0.045f, 0.06f, 1f)

            draw(-0.78f, -0.02f, 0f, 0.16f, 0.22f, 0.14f, skin)
            draw(0.78f, -0.02f, 0f, 0.16f, 0.22f, 0.14f, skin)
            draw(0f, -0.98f, -0.02f, 0.25f, 0.38f, 0.23f, skin)
            draw(0f, 0f, 0f, 0.78f, 0.98f, 0.58f, skin)
            draw(0f, 0.58f, 0.015f, 0.80f, 0.43f, 0.59f, hair)
            draw(-0.27f, 0.16f, 0.535f, 0.075f, 0.085f, 0.045f, eye)
            draw(0.27f, 0.16f, 0.535f, 0.075f, 0.085f, 0.045f, eye)
            draw(0f, -0.06f, 0.57f, 0.09f, 0.18f, 0.10f, shadow)
            draw(0f, -0.48f, 0.535f, 0.19f, 0.045f, 0.035f, shadow)
            if (!female) {
                draw(0f, -0.61f, 0.39f, 0.54f, 0.27f, 0.25f, hair)
            }
        }

        private fun draw(
            x: Float, y: Float, z: Float,
            sx: Float, sy: Float, sz: Float,
            color: FloatArray
        ) {
            Matrix.setIdentityM(model, 0)
            Matrix.translateM(model, 0, x, y, z)
            Matrix.scaleM(model, 0, sx, sy, sz)
            Matrix.multiplyMM(mvp, 0, viewProjection, 0, model, 0)
            GLES20.glUniformMatrix4fv(mvpHandle, 1, false, mvp, 0)
            GLES20.glUniformMatrix4fv(modelHandle, 1, false, model, 0)
            GLES20.glUniform4fv(colorHandle, 1, color, 0)
            sphere.draw(positionHandle, normalHandle)
        }

        private fun createProgram(vertex: String, fragment: String): Int {
            val vs = compileShader(GLES20.GL_VERTEX_SHADER, vertex)
            val fs = compileShader(GLES20.GL_FRAGMENT_SHADER, fragment)
            return GLES20.glCreateProgram().also {
                GLES20.glAttachShader(it, vs)
                GLES20.glAttachShader(it, fs)
                GLES20.glLinkProgram(it)
                val status = IntArray(1)
                GLES20.glGetProgramiv(it, GLES20.GL_LINK_STATUS, status, 0)
                check(status[0] == GLES20.GL_TRUE) { GLES20.glGetProgramInfoLog(it) }
                GLES20.glDeleteShader(vs)
                GLES20.glDeleteShader(fs)
            }
        }

        private fun compileShader(type: Int, source: String): Int {
            val shader = GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader, source)
            GLES20.glCompileShader(shader)
            val status = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0)
            check(status[0] == GLES20.GL_TRUE) { GLES20.glGetShaderInfoLog(shader) }
            return shader
        }
    }

    private class SphereMesh {
        private val vertices: FloatBuffer
        private val normals: FloatBuffer
        private val vertexCount: Int

        init {
            val data = ArrayList<Float>()
            val normalData = ArrayList<Float>()
            val latitudes = 20
            val longitudes = 28
            for (lat in 0 until latitudes) {
                val theta1 = PI * lat / latitudes
                val theta2 = PI * (lat + 1) / latitudes
                for (lon in 0 until longitudes) {
                    val phi1 = 2.0 * PI * lon / longitudes
                    val phi2 = 2.0 * PI * (lon + 1) / longitudes
                    val p1 = point(theta1, phi1)
                    val p2 = point(theta2, phi1)
                    val p3 = point(theta2, phi2)
                    val p4 = point(theta1, phi2)
                    addTriangle(data, normalData, p1, p2, p3)
                    addTriangle(data, normalData, p1, p3, p4)
                }
            }
            vertexCount = data.size / 3
            vertices = buffer(data)
            normals = buffer(normalData)
        }

        fun draw(positionHandle: Int, normalHandle: Int) {
            vertices.position(0)
            normals.position(0)
            GLES20.glEnableVertexAttribArray(positionHandle)
            GLES20.glEnableVertexAttribArray(normalHandle)
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertices)
            GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, normals)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
            GLES20.glDisableVertexAttribArray(positionHandle)
            GLES20.glDisableVertexAttribArray(normalHandle)
        }

        private fun point(theta: Double, phi: Double): FloatArray {
            val s = sin(theta)
            return floatArrayOf((s * cos(phi)).toFloat(), cos(theta).toFloat(), (s * sin(phi)).toFloat())
        }

        private fun addTriangle(
            data: MutableList<Float>, normalData: MutableList<Float>,
            a: FloatArray, b: FloatArray, c: FloatArray
        ) {
            for (p in arrayOf(a, b, c)) {
                data.add(p[0]); data.add(p[1]); data.add(p[2])
                normalData.add(p[0]); normalData.add(p[1]); normalData.add(p[2])
            }
        }

        private fun buffer(values: List<Float>): FloatBuffer {
            val bytes = ByteBuffer.allocateDirect(values.size * 4).order(ByteOrder.nativeOrder())
            return bytes.asFloatBuffer().apply {
                values.forEach { put(it) }
                position(0)
            }
        }
    }
}
