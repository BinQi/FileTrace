package wbq.ndkdemo

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ctg.filetrace.FileTraceApi
import com.ctg.filetrace.util.DesEncryptor
import com.ctg.filetrace.util.Logger
import kotlinx.android.synthetic.main.activity_main.*
import wbq.ndkdemo.a.B

class MainActivity : AppCompatActivity() {


    fun dd(a: (x: Int, y: Int) -> Int) {
        a(1, 2)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sample_text.setOnClickListener {
        }
        // Example of a call to a native method
        FileTraceApi.setDebug(true)
        val c = DesEncryptor()

//        val encryptedData = "djErTncyZ01RN2NIdCszNUx0K0lveGsvQUJEWG52VnE0QkRTK2VyZ2wxNVd4cHFyRU5Namw5eDVuSkYwc0ovTGIxVw0KWld1NVoyWGFKSzgyVis1cW8zWnd6OWVFblRvTFhiR1pQWjRXa1pQeUdpZ01NM1ZCc2MxL0RzZXViR1I2WHVseA0KMEdxR1FZaFRMZTRoL3RpVnI1YWsvenNzaXdqUmkzSThydDRudVRsSTJ5ZnBmRm9KNnY3N2srMUtnbnVES013RA0KSTFJOVpDRUFBc0ltN2tSZ0EwaUFhSFMxeUwvczM4cWhZWHY4Rnp1MDJObGVFbHg5ZzVyWGk3dVl6ekdrY3VBbQ0KUkw0bFJHMk5MdGxjL3BabmFYQmZBMyt2R3hTa3ZZcU4xT0N2YjV0aVhMcHBRZ05PUHhseEZPUndQclE4Mkx2aA0KVTdPWTdUZFVTc1gzVWhKMEJ4MnNmVTBqR2tzZEtCbkN1TzhicXNpTDFYaXRLbmtDQnpOM1VoZzl6ajViZUVKWg=="//c.encrypt("{test:test}").orEmpty()
//        val decryptedData = c.decrypt(encryptedData)
//        Logger.e("wbq: encryptedData=$encryptedData decryptedData=$decryptedData")

        val pdfPath = "/sdcard/演示文稿1.pptx"//"/sdcard/a.pdf"
        val success = FileTraceApi.setTag(pdfPath, "[{\"companyId\":\"244\",\"companyName\":\"一目团队\",\"operation\":0,\"scene\":1,\"source\":0,\"time\":1677232604394,\"userAvator\":\"https://openfire.bgzs.site:1443/plugins/fileserver/file/download?path=upl_53b2869f91a646c4b5c6bc4595f2e1ea.png \",\"userId\":\"1867\",\"userName\":\"罗伟.#@_*;(/\"},{\"companyId\":\"244\",\"companyName\":\"一目团队\",\"operation\":1,\"scene\":1,\"source\":0,\"time\":1677232738090,\"userAvator\":\"https://openfire.bgzs.site:1443/plugins/fileserver/file/download?path=upl_53b2869f91a646c4b5c6bc4595f2e1ea.png \",\"userId\":\"1867\",\"userName\":\"罗伟.#@_*;(/\"},{\"companyId\":\"244\",\"companyName\":\"一目团队\",\"operation\":1,\"scene\":1,\"source\":0,\"time\":1677398157892,\"userAvator\":\"https://openfire.bgzs.site:1443/plugins/fileserver/file/download?path=upl_53b2869f91a646c4b5c6bc4595f2e1ea.png \",\"userId\":\"1867\",\"userName\":\"罗伟.#@_*;(/\"}]")
        sample_text.text = "FileTraceApi.setTagf = $success"
        Logger.e("wbq: setTag=$success")

        Logger.e("wbq: getTag=${FileTraceApi.getTag(pdfPath)}")

        ff2()
        dd(fun(x: Int, y: Int): Int {
            return x + y
        })
        val sum: Int.(Int) -> Int = { other -> plus(other) }
        sum(1, 2)
        val a : B.(x: Int) -> Int = { other -> other + 1 }
        a(B("1"), 1)
//        wbq.ndkdemo.a.B.bb("ff")

        sample_img.setImageResource(R.drawable.animatorvectordrawable)
    }

    override fun onStart() {
        super.onStart()
        val ad = sample_img.drawable as AnimatedVectorDrawable
        ad.start()
    }

    fun ff1(bb: Bundle) {
//        bb.putChar("b", 'a')
    }

    fun ff2(aa: String = "", bb: Int = 1) {
        var bb = Bundle()

        ff1(bb)
    }
}
