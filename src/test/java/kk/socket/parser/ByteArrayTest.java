package kk.socket.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.charset.Charset;

import kk.json.JSONArray;
import kk.json.JSONObject;
import kk.json.JSONValue;
import kk.socket.emitter.Emitter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ByteArrayTest {

    private static Parser.Encoder encoder = new Parser.Encoder();

    @Test
    public void encodeByteArray() {
        Packet<byte[]> packet = new Packet<byte[]>(Parser.BINARY_EVENT);
        packet.data = "abc".getBytes(Charset.forName("UTF-8"));
        packet.id = 23;
        packet.nsp = "/cool";
        Helpers.testBin(packet);
    }

    @Test
    public void encodeByteArray2() {
        Packet<byte[]> packet = new Packet<byte[]>(Parser.BINARY_EVENT);
        packet.data = new byte[2];
        packet.id = 0;
        packet.nsp = "/";
        Helpers.testBin(packet);
    }

    @Test
    public void encodeByteArrayDeepInJson() {
        JSONObject data = (JSONObject) JSONValue.parse("{\"a\": \"hi\", \"b\": {},\"c\": {\"a\": \"bye\", \"b\": {}}}");
		((JSONObject)data.get("b")).put("why", new byte[3]);
		((JSONObject)((JSONObject)data.get("c")).get("b")).put("a", new byte[6]);

        Packet<JSONObject> packet = new Packet<JSONObject>(Parser.BINARY_EVENT);
        packet.data = data;
        packet.id = 999;
        packet.nsp = "/deep";
        Helpers.testBin(packet);
    }

    @Test
    public void encodeDeepBinaryJSONWithNullValue() {
        JSONObject data = (JSONObject) JSONValue.parse("{\"a\": \"b\", \"c\": 4, \"e\": {\"g\": null}, \"h\": null}");
        data.put("h", new byte[9]);

        Packet<JSONObject> packet = new Packet<JSONObject>(Parser.BINARY_EVENT);
        packet.data = data;
        packet.nsp = "/";
        packet.id = 600;
        Helpers.testBin(packet);
    }

    @Test
    public void encodeBinaryAckWithByteArray() {
        JSONArray data = (JSONArray) JSONValue.parse("[a, null, {}]");
        data.add(1, "xxx".getBytes(Charset.forName("UTF-8")));

        Packet<JSONArray> packet = new Packet<JSONArray>(Parser.BINARY_ACK);
        packet.data = data;
        packet.id = 127;
        packet.nsp = "/back";
        Helpers.testBin(packet);
    }

    @Test
    public void cleanItselfUpOnClose() {
        JSONArray data = new JSONArray();
        data.add(new byte[2]);
        data.add(new byte[3]);

        Packet<JSONArray> packet = new Packet<JSONArray>(Parser.BINARY_EVENT);
        packet.data = data;
        packet.id = 0;
        packet.nsp = "/";

        encoder.encode(packet, new Parser.Encoder.Callback() {
            @Override
            public void call(final Object[] encodedPackets) {
                final Parser.Decoder decoder = new Parser.Decoder();
                decoder.on(Parser.Decoder.EVENT_DECODED, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        throw new RuntimeException("received a packet when not all binary data was sent.");
                    }
                });

                decoder.add((String)encodedPackets[0]);
                decoder.add((byte[]) encodedPackets[1]);
                decoder.destroy();
                assertThat(decoder.reconstructor.buffers.size(), is(0));
            }
        });
    }
}
