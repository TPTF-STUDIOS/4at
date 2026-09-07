package xyz.paintingthefish.chat.internals

import org.ini4j.Wini
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.util.Base64
import kotlin.math.round

@Suppress("unused", "UnusedVariable")
class ServerClass(conf: Wini) :
    WebSocketServer(InetSocketAddress("::", Shared.DEFAULT_PORT)) {
    var recentMessagesBuffer: RecentMessagesBuffer? = RecentMessagesBuffer(conf.get("info", "amount").toInt())
    var cfg: Wini = conf
    val base64Decoder: Base64.Decoder = Base64.getDecoder()
    val base64Encoder: Base64.Encoder = Base64.getEncoder()
    val base64SizeOverhead: Float = (4.0f / 3.0f)

    val ok: Byte = 0x00
    val tooShort: Byte = 0x0A
    val tooLong: Byte = 0x0B
    val parsingError: Byte = 0x0C

    override fun onStart() {
        System.err.println("ЧAT server starting on port " + getPort().toString())
        TODO("well... idk but prolly something")
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String?, remote: Boolean) {
        System.err.printf(
            "[INFO] client %s disconnected. (code %d)\n",
            Shared.getWiniFromStr(conn.getAttachment()).get("info", "conn_id"),
            code
        )
    }

    override fun onError(conn: WebSocket, err: Exception?) {
        conn.close()
        TODO("implement full handling of glitching WebSockets")
    }

    override fun onMessage(conn: WebSocket, message: String) {
        if (round(message.length / base64SizeOverhead) <= 1) {
            conn.send(base64Encoder.encode(byteArrayOf(tooShort)))
            return
        } else if (round(message.length / base64SizeOverhead) >= 1048576) {
            conn.send(base64Encoder.encode(byteArrayOf(tooLong)))
            return
        }
        try {
            val decodedMessage: ByteArray = base64Decoder.decode(message)
            val userData = Wini(conn.getAttachment<String>().reader())
        } catch (e: Exception) {
            e.printStackTrace()
            conn.send(base64Encoder.encode(byteArrayOf(parsingError)))
            return
        }
        TODO("basically everything else")
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake?) {
        conn.setAttachment("")
        TODO("implement the rest of the connection flow")
    }
}
