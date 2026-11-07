package com.nova.ai

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Small local-only conversation history. It is stored in this app's private
 * SharedPreferences and is never uploaded by itself; recent turns are included
 * only when the user sends a new message to the configured AI gateway.
 */
object ChatHistoryStore {
    private const val PREFS = "nova_chat_history"
    private const val KEY_MESSAGES = "messages"
    private const val MAX_MESSAGES = 20
    private const val MAX_CONTEXT_CHARS = 64 * 1024

    @Synchronized
    fun add(context: Context, role: String, text: String) {
        if (role !in setOf("user", "assistant") || text.isBlank()) return
        val items = read(context)
        items.put(JSONObject().put("role", role).put("text", text.take(MAX_CONTEXT_CHARS)))
        while (items.length() > MAX_MESSAGES) items.remove(0)
        save(context, items)
    }

    @Synchronized
    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().remove(KEY_MESSAGES).apply()
    }

    @Synchronized
    fun transcript(context: Context): String {
        val items = read(context)
        val output = StringBuilder()
        for (i in 0 until items.length()) {
            val item = items.optJSONObject(i) ?: continue
            val role = item.optString("role")
            val text = item.optString("text")
            if (text.isBlank()) continue
            output.append(if (role == "assistant") "المساعد: " else "المستخدم: ")
            output.append(text).append('\n')
        }
        return output.toString().takeLast(MAX_CONTEXT_CHARS)
    }

    @Synchronized
    fun preview(context: Context): String {
        val items = read(context)
        if (items.length() == 0) return "مفيش محادثات محفوظة لسه."
        val output = StringBuilder()
        for (i in 0 until items.length()) {
            val item = items.optJSONObject(i) ?: continue
            val role = if (item.optString("role") == "assistant") "NOVA" else "إنت"
            output.append(role).append(": ")
                .append(item.optString("text").take(240))
                .append("\n\n")
        }
        return output.toString()
    }

    private fun read(context: Context): JSONArray {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_MESSAGES, "[]")
        return try { JSONArray(raw) } catch (_: Exception) { JSONArray() }
    }

    private fun save(context: Context, items: JSONArray) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_MESSAGES, items.toString()).apply()
    }
}
