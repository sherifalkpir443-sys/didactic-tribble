package com.nova.ai

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var status: TextView
    private lateinit var messageInput: EditText
    private lateinit var avatar: Avatar3DView
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var selectedPersona = "ذكر"

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
            setPadding(24, 32, 24, 24)
            setBackgroundColor(0xFF101827.toInt())
        }
        root.addView(TextView(this).apply {
            text = "NOVA AI"
            textSize = 30f
            setTextColor(0xFF70D6FF.toInt())
            gravity = Gravity.CENTER
        }, matchWidth())

        avatar = Avatar3DView(this)
        avatar.setPersona(selectedPersona)
        root.addView(avatar, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 250
        ).apply { topMargin = 12; bottomMargin = 8 })
        root.addView(TextView(this).apply {
            text = "وجه ثلاثي الأبعاد تجريبي — النموذج الواقعي المتحرك لسه مرحلة تطوير"
            textSize = 11f
            setTextColor(0xFFB7C4D6.toInt())
            gravity = Gravity.CENTER
        }, matchWidth())

        val personaRow = LinearLayout(this).apply { gravity = Gravity.CENTER }
        personaRow.addView(Button(this).apply {
            text = "شخصية ذكر"
            setOnClickListener { selectedPersona = "ذكر"; avatar.setPersona(selectedPersona) }
        })
        personaRow.addView(Button(this).apply {
            text = "شخصية أنثى"
            setOnClickListener { selectedPersona = "أنثى"; avatar.setPersona(selectedPersona) }
        })
        root.addView(personaRow, matchWidth())

        status = TextView(this).apply {
            text = if (BuildConfig.NOVA_API_BASE_URL.isBlank())
                "المساعد جاهز. خادم الذكاء الاصطناعي غير مضبوط بعد."
            else "المساعد جاهز. اكتب رسالتك."
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 18, 0, 18)
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
            setOnClickListener { sendMessage(messageInput.text.toString()) }
        }, matchWidth())
        root.addView(Button(this).apply {
            text = "التحدث بالصوت"
            setOnClickListener { beginVoiceInput() }
        }, matchWidth())
        root.addView(Button(this).apply {
            text = "معلومات الهاتف للمساعد"
            setOnClickListener { shareDeviceContextWithAssistant() }
        }, matchWidth())
        root.addView(Button(this).apply {
            text = "قراءة آخر رد"
            setOnClickListener {
                textToSpeech?.speak(status.text.toString(), TextToSpeech.QUEUE_FLUSH, null, "nova-reply")
            }
        }, matchWidth())
        root.addView(Button(this).apply {
            text = "فتح إعدادات الجهاز"
            setOnClickListener {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("تأكيد فتح الإعدادات")
                    .setMessage("هيتم فتح إعدادات أندرويد فقط. مش هيتم تغيير أي إعداد تلقائيًا.")
                    .setNegativeButton("إلغاء", null)
                    .setPositiveButton("فتح") { _, _ -> startActivity(Intent(Settings.ACTION_SETTINGS)) }
                    .show()
            }
        }, matchWidth())
        root.addView(TextView(this).apply {
            text = "التحكم في الجهاز محدود بفتح الإعدادات بعد تأكيدك. باقي أوامر الجهاز مش مفعّلة."
            textSize = 11f
            setTextColor(0xFFB7C4D6.toInt())
            setPadding(0, 18, 0, 0)
        }, matchWidth())

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)
    }

    private fun matchWidth() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
    )

    private fun sendMessage(message: String) {
        if (message.isBlank()) { status.text = "اكتب رسالة الأول."; return }
        val baseUrl = BuildConfig.NOVA_API_BASE_URL.trim().trimEnd('/')
        if (baseUrl.isBlank()) {
            status.text = "رسالتك جاهزة، لكن خادم الذكاء الاصطناعي مش مربوط. يلزم ضبط NOVA_API_BASE_URL."
            return
        }
        status.text = "ببعت رسالتك للخادم..."
        AiGateway.send(baseUrl, message) { reply ->
            runOnUiThread {
                status.text = reply ?: "تعذر الاتصال بالخادم. راجع عنوان الخدمة والاتصال."
            }
        }
    }

    private fun shareDeviceContextWithAssistant() {
        val snapshot = try {
            DeviceContextProvider.collect(this)
        } catch (_: Exception) {
            status.text = "مقدرتش أقرأ معلومات الجهاز دلوقتي."
            return
        }
        AlertDialog.Builder(this)
            .setTitle("مراجعة معلومات الهاتف")
            .setMessage("$snapshot\n\nهل توافق على إرسال الملخص ده لخادم NOVA AI عشان يساعدك؟")
            .setNegativeButton("إلغاء", null)
            .setPositiveButton("موافق وإرسال") { _, _ ->
                sendMessage("حلل معلومات جهازي دي، واشرح حالته واقترح خطوات مناسبة من غير ما تغيّر أي إعداد:\n$snapshot")
            }
            .show()
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
                        sendMessage(spoken)
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

    override fun onResume() {
        super.onResume()
        if (::avatar.isInitialized) avatar.onResume()
    }

    override fun onPause() {
        if (::avatar.isInitialized) avatar.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        speechRecognizer?.destroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}
