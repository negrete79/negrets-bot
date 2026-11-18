package com.negrets.bot

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.RemoteInput

class NegretsListener : NotificationListenerService() {

    private val regras = mapOf(
        "oi" to "Oi {nome}! Fala comigo 😎",
        "bom dia" to "Bom dia {nome}! Como cê tá?",
        "boa noite" to "Boa noite {nome}! Descansa bem!",
        "tudo bem" to "Tudo certo {nome}, e com você?",
        "negrets" to "Opa, chamou? Sou o Negrets Bot!"
    )

    private val variaveis = mapOf(
        "{bot}" to "Negrets Bot"
    )

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        if (sbn.packageName != "com.whatsapp" && sbn.packageName != "com.whatsapp.w4b") return

        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return

        val texto = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: return
        val quemMandou = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: "amigo"

        // Não responder a si mesmo / evitar loop
        if (texto.contains("NEGRETS BOT")) return

        var resposta: String? = null
        for ((palavra, resp) in regras) {
            if (texto.lowercase().contains(palavra)) {
                resposta = resp
                break
            }
        }
        if (resposta == null) return

        var final = resposta!!
        for ((k, v) in variaveis) {
            final = final.replace(k, v)
        }
        final = final.replace("{nome}", quemMandou)

        responder(sbn, final)
    }

    fun responder(sbn: StatusBarNotification, texto: String) {
        val actions = sbn.notification.actions ?: return
        for (action in actions) {
            if (action.remoteInputs != null && action.remoteInputs.isNotEmpty()) {
                val intent = Intent()
                val bundle = Bundle()
                val remoteInput = action.remoteInputs[0]
                bundle.putCharSequence(remoteInput.resultKey, texto)
                RemoteInput.addResultsToIntent(action.remoteInputs, intent, bundle)
                try {
                    action.actionIntent.send(this, 0, intent)
                    break
                } catch (e: PendingIntent.CanceledException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
