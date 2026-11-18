package com.negrets.bot

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 60, 50, 50)
            setBackgroundColor(android.graphics.Color.BLACK)
        }

        val titulo = TextView(this).apply {
            text = "⚡ NEGRET'S BOT"
            textSize = 28f
            setTextColor(android.graphics.Color.parseColor("#FFD700"))
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }

        val subtitulo = TextView(this).apply {
            text = "AutoResponder - Barueri / SP"
            textSize = 14f
            setTextColor(android.graphics.Color.WHITE)
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }

        val status = TextView(this).apply {
            text = "\nStatus: Pronto pra ligar\n\n1. Clique no botão abaixo\n2. Ative a chavinha do NEGRET'S BOT\n3. Volte pra cá"
            textSize = 16f
            setTextColor(android.graphics.Color.WHITE)
        }

        val btnPermissao = Button(this).apply {
            text = "1. PERMITIR LER NOTIFICAÇÕES"
            setBackgroundColor(android.graphics.Color.parseColor("#FFD700"))
            setTextColor(android.graphics.Color.BLACK)
        }

        val btnTeste = Button(this).apply {
            text = "2. COMO TESTAR"
            setBackgroundColor(android.graphics.Color.DKGRAY)
            setTextColor(android.graphics.Color.WHITE)
        }

        val aviso = TextView(this).apply {
            text = "\n\n⚠️ DICAS IGUAL AUTORESPONDER:\n- Deixe o app aberto em 2º plano\n- Não feche no limpador de memória\n- Mantenha o WhatsApp logado"
            textSize = 13f
            setTextColor(android.graphics.Color.LTGRAY)
        }

        btnPermissao.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        btnTeste.setOnClickListener {
            Toast.makeText(this, "Manda 'oi' no seu WhatsApp de outro celular pra testar!", Toast.LENGTH_LONG).show()
        }

        layout.addView(titulo)
        layout.addView(subtitulo)
        layout.addView(status)
        layout.addView(btnPermissao)
        layout.addView(btnTeste)
        layout.addView(aviso)
        setContentView(layout)
    }
}
