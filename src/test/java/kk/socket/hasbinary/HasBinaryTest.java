package kk.socket.hasbinary;

import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;

import kk.json.JSONArray;
import kk.json.JSONObject;
import kk.json.JSONValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class HasBinaryTest {

    @Test
    public void byteArray() {
        assertTrue(HasBinary.hasBinary(new byte[0]));
    }

    @Test
    public void anArrayThatDoesNotContainByteArray() {
        JSONArray arr = (JSONArray) JSONValue.parse("[1, \"cool\", 2]");
        assertTrue(!HasBinary.hasBinary(arr));
    }

    @Test
    public void anArrayContainsByteArray() {
        JSONArray arr = (JSONArray) JSONValue.parse("[1, null, 2]");
        arr.add(1, "asdfasdf".getBytes(Charset.forName("UTF-8")));
        assertTrue(HasBinary.hasBinary(arr));
    }

    @Test
    public void anObjectThatDoesNotContainByteArray() {
        JSONObject ob = (JSONObject) JSONValue.parse("{\"a\": \"a\", \"b\": [], \"c\": 1234}");
        assertTrue(!HasBinary.hasBinary(ob));
    }

    @Test
    public void anObjectThatContainsByteArray() {
        JSONObject ob = (JSONObject) JSONValue.parse("{\"a\": \"a\", \"b\": null, \"c\": 1234}");
        ob.put("b", "abc".getBytes(Charset.forName("UTF-8")));
        assertTrue(HasBinary.hasBinary(ob));
    }

    @Test
    public void testNull() {
        assertTrue(!HasBinary.hasBinary(null));
    }

    @Test
    public void aComplexObjectThatContainsNoBinary() {
        JSONObject ob = new JSONObject();
        ob.put("x", JSONValue.parse("[\"a\", \"b\", 123]"));
        ob.put("y", null);
        ob.put("z", JSONValue.parse("{\"a\": \"x\", \"b\": \"y\", \"c\": 3, \"d\": null}"));
        ob.put("w", new JSONArray());
        assertTrue(!HasBinary.hasBinary(ob));
    }

    @Test
    public void aComplexObjectThatContainsBinary() {
        JSONObject ob = new JSONObject();
        ob.put("x", JSONValue.parse("[\"a\", \"b\", 123]"));
        ob.put("y", null);
        ob.put("z", JSONValue.parse("{\"a\": \"x\", \"b\": \"y\", \"c\": 3, \"d\": null}"));
        ob.put("w", new JSONArray());
        ob.put("bin", "xxx".getBytes(Charset.forName("UTF-8")));
        assertTrue(HasBinary.hasBinary(ob));
    }
}
