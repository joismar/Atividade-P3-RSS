package br.ufpe.cin.if710.rss

import android.app.Activity
import android.content.ContentValues
import android.os.AsyncTask
import android.os.Bundle
import android.widget.TextView

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import android.os.StrictMode
import android.text.TextUtils
import android.util.Log
import org.xmlpull.v1.XmlPullParserException
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View


class MainActivity : Activity() {

    //ao fazer envio da resolucao, use este link no seu codigo!
    private val RSS_FEED = "http://leopoldomt.com/if1001/g1brasil.xml"

    //OUTROS LINKS PARA TESTAR...
    //http://rss.cnn.com/rss/edition.rss
    //http://pox.globo.com/rss/g1/brasil/
    //http://pox.globo.com/rss/g1/ciencia-e-saude/
    //http://pox.globo.com/rss/g1/tecnologia/

    //use ListView ao invés de TextView - deixe o atributo com o mesmo nome
    //private var conteudoRSS: ListView? = null
    // Defini as views titulo e data e a RecyclerView
    private var feedTitle: TextView? = null
    private var feedData: TextView? = null
    private var conteudoRSS: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa as views: titulo, dados e a RecyclerView
        conteudoRSS = findViewById<View>(R.id.conteudoRSS) as RecyclerView
        feedTitle = findViewById<View>(R.id.item_titulo) as? TextView
        feedData = findViewById<View>(R.id.item_data) as? TextView

        // Seta o layout manager
        conteudoRSS!!.setLayoutManager(LinearLayoutManager(this))

        // Isso corrigiu um NetworkException que tava dando
        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            //Executa a Async Task
            FeedTask().execute(null as Void?)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    //Opcional - pesquise outros meios de obter arquivos da internet - bibliotecas, etc.
    //O erro de memoria tava dando no "out.write" dessa função.
    @Throws(IOException::class)
    private fun getRssFeed(feed: String): String {
        var inp: InputStream? = null
        var rssFeed: String
        try {
            val url = URL(feed)
            val conn = url.openConnection() as HttpURLConnection
            inp = conn.inputStream
            val out = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var count: Int
            count = inp!!.read(buffer)
            while (count != -1) {
                out.write(buffer, 0, count)
            }
            val response = out.toByteArray()
            rssFeed = String(response, charset("UTF-8"))
        } finally {
            inp?.close()
        }
        return rssFeed
    }

    // Achei essa AsyncTask em java em um artigo, modifiquei ela pra se adaptar a esse projeto
    private inner class FeedTask : AsyncTask<Void, Int, Boolean>() {

        // Essa lista vai ser jogada no Adapter
        var items: List<ItemRSS> = arrayListOf()

        override fun onPreExecute() {
            feedTitle = null
            feedData = null
            feedTitle?.text = "Feed Title: $feedTitle"
            feedData?.text = "Feed Data: $feedData"
        }

        // Download do Feed
        override fun doInBackground(vararg void: Void): Boolean? {

            var urlLink: String = RSS_FEED

            try {
                if (!urlLink.startsWith("http://") && !urlLink.startsWith("https://"))
                    urlLink = "http://$urlLink"

                val url = URL(urlLink)
                // Essa parte substituiu o getRssFeed
                val inputStream = url.openConnection().getInputStream()
                // Transforma o inputStream em String
                val inputAsString = inputStream.bufferedReader().use { it.readText() }
                items = ParserRSS.parse(inputAsString)
                return true
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Error", e)
            } catch (e: XmlPullParserException) {
                Log.e(ContentValues.TAG, "Error", e)
            }
            return false
        }

        // Alimenta a RecyclerView
        override fun onPostExecute(success: Boolean?) {
            if (success!!) {
                feedTitle?.text = "Feed Title: $feedTitle"
                feedData?.text = "Feed Data: $feedData"
                conteudoRSS?.adapter = RssFeedAdapter(items)
            }
        }
    }
}

