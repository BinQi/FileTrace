package wbq.ndkdemo.a

/**
 * Created by Jerry on 2019-07-31 15:13
 */
fun bb(msg: String) : String {
    return "aaa $msg"
}

class B (var msg: String) {
    fun foo() = print(msg)
}