package com.nova.ai

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var status: TextView
    private lateinit var messageInput: EditText
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null

    companion object { private const val REQUEST_RECORD_AUDIO = 410 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textToSpeech = TextToSpeech(this, this)
        buildInterface()
    }

    private fun buildInterface() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 48, 28, 28)
            setBackgroundColor(0xFF101827.toInt())
        }
        val heading = TextView(this).apply {
            text = "NOVA AI"
            textSize = 30f
            setTextColor(0xFF70D6FF.toInt())
            gravity = Gravity.CENTER
        }
        root.addView(heading, matchWidth())

        status = TextView(this).apply {
            text = "مساعدك جاهز. اكتب رسالة أو اضغط على الميكروفون."
            textSize = 17f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 32, 0, 24)
        }
        root.addView(status, matchWidth())

        messageInput = EditText(this).apply {
            hint = "اكتب رسالتك هنا"
            textSize = 17f
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xFFB7C4D6.toInt())
            minLines = 2
        }
        root.addView(messageInput, matchWidth())

        root.addView(Button(this).apply {
            text = "إرسال"
            setOnClickListener { respondTo(messageInput.text.toString()) }
        }, matchWidth())

        root.addView(Button(this).apply {
            text = "التحدث بالصوت"
            setOnClickListener { beginVoiceInput() }
        }, matchWidth())

        root.addView(Button(this).apply {
            text = "قراءة آخر رد"
            setOnClickListener {
                textToSpeech?.speak(status.text.toString(), TextToSpeech.QUEUE_FLUSH, null, "nova-reply")
            }
        }, matchWidth())

        root.addView(TextView(this).apply {
            text = "نسخة تجريبية: الردود محلية، وخدمة الذكاء الاصطناعي غير متصلة بعد."
            textSize = 12f
            setTextColor(0xFFB7C4D6.toInt())
            setPadding(0, 28, 0, 0)
        }, matchWidth())
        setContentView(root)
    }

    private fun matchWidth() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
    )

    private fun respondTo(message: String) {
        if (message.isBlank()) {
            status.text = "اكتب رسالة الأول."
            return
        }
        status.text = "وصلت رسالتك: $message\n\nالرد الذكي السحابي غير متوصل في النسخة دي."
    }

    private fun beginVoiceInput() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO)
        } else startListening()
    }

    private fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            status.text = "خدمة تحويل الكلام لنص مش متاحة على الجهاز."
            return
        }
        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).also { recognizer ->
            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { status.text = "سامعك... اتكلم." }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() { status.text = "ثواني بفهم كلامك..." }
                override fun onError(error: Int) { status.text = "حصلت مشكلة في التعرف على الصوت. جرّب تاني." }
                override fun onResults(results: Bundle?) {
                    val spoken = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                    if (!spoken.isNullOrBlank()) {
                        messageInput.setText(spoken)
                        respondTo(spoken)
                    } else status.text = "ملقتش كلام واضح في التسجيل."
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ar-EG")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "اتكلم")
            }
            recognizer.startListening(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) startListening()
            else status.text = "رفضت إذن الميكروفون؛ تقدر تستخدم الكتابة عادي."
        }
    }

    override fun onInit(statusCode: Int) {
        if (statusCode == TextToSpeech.SUCCESS) textToSpeech?.language = Locale("ar", "EG")
    }

    override fun onDestroy() {
        speechRecognizer?.destroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}
