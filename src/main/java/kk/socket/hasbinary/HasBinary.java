package kk.socket.hasbinary;

import java.util.Iterator;
import java.util.logging.Logger;

import kk.json.JSONArray;
import kk.json.JSONObject;

public class HasBinary {
	
    private static final Logger logger = Logger.getLogger(HasBinary.class.getName());
	
    private HasBinary() {}

    public static boolean hasBinary(Object data) {
        return _hasBinary(data);
    }

    private static boolean _hasBinary(Object obj) {
        if (obj == null) return false;

        if (obj instanceof byte[]) {
            return true;
        }

        if (obj instanceof JSONArray) {
            JSONArray _obj = (JSONArray)obj;
            for (Object v : _obj) {
                if (_hasBinary(v)) {
                    return true;
                }
            }
        } else if (obj instanceof JSONObject) {
            JSONObject _obj = (JSONObject)obj;
            Iterator keys = _obj.keySet().iterator();
            while (keys.hasNext()) {
                String key = (String)keys.next();
                Object v = _obj.get(key);
                if (_hasBinary(v)) {
                    return true;
                }
            }
        }

        return false;
    }
}
