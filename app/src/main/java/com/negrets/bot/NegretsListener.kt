package com.negrets.bot

import android.app.Notification
import android.app.RemoteInput
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NegretsListener : NotificationListenerService() {

    // SUAS VARIÁVEIS - VOCÊ MUDA AQUI
    val variaveis = mapOf(
        "{nome}" to "cliente",
        "{codigo}" to "NEG-1234",
        "{modelo}" to "iPhone 11",
        "{status}" to "Em conserto - previsão hoje 17h",
        "{preco_tela_iphone11}" to "350",
        "{preco_bateria_a54}" to "180",
        "{preco_capinha}" to "40",
        "{pix}" to "11 99999-9999",
        "{horario}" to "Seg a Sex 9h às 19h, Sáb 9h às 14h"
    )

    // SUAS REGRAS - IGUAL AUTORESPONDER
    val regras = mapOf(
        "tela iphone 11" to "Opa {nome}! Tela do iPhone 11 fica R\$350 + capinha R\$40 = R\$390. Fechando hoje faço R\$370 à vista. Quer agendar?",
        "tela" to "Opa {nome}! Qual modelo? Manda 'tela iPhone 11' que eu te passo o preço na hora!",
        "bateria a54" to "Bateria do A54 fica R\$180 com 3 meses de garantia! Quer agendar?",
        "preco" to "Tabela NEGRET'S:\nTela iPhone 11 - R\$350\nBateria A54 - R\$180\nCapinha - R\$40\n\nTela + Capinha = R\$390!",
        "rastreio" to "Me manda seu código tipo NEG-1234 que eu vejo seu celular!",
        "neg-" to "Achei! Seu {modelo} código {codigo} está: {status}",
        "horario" to "Funcionamos {horario}. Barueri - SP",
        "pix" to "Nosso Pix é {pix} - NEGRET'S. Manda o comprovante!",
        "oi" to "Opa {nome}! Bem-vindo a NEGRET'S! Digite PRECO pra ver preços, RASTREIO + código pra ver seu celular.",
        "ola" to "Opa {nome}! Bem-vindo a NEGRET'S! Digite PRECO pra ver preços.",
        "atendente" to "Te chamo já! Um atendente da NEGRET'S vai te atender em 2 min."
    )

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        if (sbn.packageName != "com.whatsapp" && sbn.packageName != "com.whatsapp.w4b") return

        val extras = sbn.notification.extras
        val texto = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: return
        val quemMandou = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: return

        if (quemMandou == "Você" || quemMandou.contains("NEGRET'S")) return
        if (texto.contains("NEGRET'S BOT")) return

        var resposta: String? = null
        for ((palavra, resp) in regras) {
            if (texto.lowercase().contains(palavra)) {
                resposta = resp
                break
            }
        }
        if (resposta == null) return

        var final = resposta
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
                bundle.putCharSequence(action.remoteInputs[0].resultKey, texto)
                RemoteInput.addResultsToIntent(action.remoteInputs, intent, bundle)
                try { action.actionIntent.send(this, 0, intent) } catch (e: Exception) {}
                break
            }
        }
    }
}
