package com.negrets.bot

import android.app.Notification
import android.app.PendingIntent
import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NegretsListener : NotificationListenerService() {

    // ===== CONFIGURAÇÃO =====
    private val nomeBot = "Negrets Bot"

    // Palavra que a pessoa manda -> resposta do bot
    private val regras = mapOf(
        "oi" to "Oi {nome}! Fala comigo 😎",
        "opa" to "Opa {nome}! Cheguei, o que manda?",
        "bom dia" to "Bom dia {nome}! Como cê tá hoje?",
        "boa tarde" to "Boa tarde {nome}! Bora conversar?",
        "boa noite" to "Boa noite {nome}! Descansa bem!",
        "tudo bem" to "Tudo certo {nome}, e com você?",
        "quem é você" to "Eu sou o {bot}! Bot criado pelo Negrets 🤖",
        "negrets" to "Opa, chamou? Sou o {bot}!",
        "ajuda" to "Fala {nome}, manda 'oi', 'bom dia' ou 'tudo bem' que eu respondo!",
        "obrigado" to "Tamo junto {nome}! ❤️",
        "valeu" to "Valeu {nome}! Precisando é só chamar."
    )

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        // Só WhatsApp normal e Business
        if (sbn.packageName != "com.whatsapp" && sbn.packageName != "com.whatsapp.w4b") return

        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return

        val texto = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: return
        val quemMandou = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: "amigo"

        // Não responder mensagem de grupo nem a si mesmo (evita loop infinito)
        if (quemMandou.contains(":") || texto.contains(nomeBot)) return
        if (texto.isBlank()) return

        // Acha a resposta
        var respostaEncontrada: String? = null
        for ((palavra, resp) in regras) {
            if (texto.lowercase().contains(palavra)) {
                respostaEncontrada = resp
                break
            }
        }
        if (respostaEncontrada == null) return

        // Troca as variáveis {nome} e {bot}
        var final = respostaEncontrada
            .replace("{nome}", quemMandou)
            .replace("{bot}", nomeBot)

        Log.d("NegretsBot", "Respondendo $quemMandou: $final")
        responder(sbn, final)
    }

    private fun responder(sbn: StatusBarNotification, texto: String) {
        val actions = sbn.notification.actions ?: return

        for (action in actions) {
            val remoteInputs = action.remoteInputs ?: continue
            if (remoteInputs.isEmpty()) continue

            // Pega a ação de "Responder"
            if (action.title.toString().lowercase().contains("responder") || remoteInputs.isNotEmpty()) {
                try {
                    val intent = Intent()
                    val bundle = Bundle()
                    bundle.putCharSequence(remoteInputs[0].resultKey, texto)
                    RemoteInput.addResultsToIntent(remoteInputs, intent, bundle)
                    action.actionIntent.send(this, 0, intent)
                    break
                } catch (e: PendingIntent.CanceledException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
