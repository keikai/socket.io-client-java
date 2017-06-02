package kk.socket.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import kk.json.JSONArray;
import kk.json.JSONObject;

public class Binary {

    private static final String KEY_PLACEHOLDER = "_placeholder";

    private static final String KEY_NUM = "num";
    
    private static final Logger logger = Logger.getLogger(Binary.class.getName());

    @SuppressWarnings("unchecked")
    public static DeconstructedPacket deconstructPacket(Packet packet) {
        List<byte[]> buffers = new ArrayList<byte[]>();

        packet.data = _deconstructPacket(packet.data, buffers);
        packet.attachments = buffers.size();

        DeconstructedPacket result = new DeconstructedPacket();
        result.packet = packet;
        result.buffers = buffers.toArray(new byte[buffers.size()][]);
        return result;
    }

    private static Object _deconstructPacket(Object data, List<byte[]> buffers) {
        if (data == null) return null;

        if (data instanceof byte[]) {
            JSONObject placeholder = new JSONObject(2);
			placeholder.put(KEY_PLACEHOLDER, true);
			placeholder.put(KEY_NUM, buffers.size());
            buffers.add((byte[])data);
            return placeholder;
        } else if (data instanceof JSONArray) {
            JSONArray newData = new JSONArray();
            JSONArray _data = (JSONArray)data;
            int len = _data.size();
            for (int i = 0; i < len; i ++) {
				newData.add(i, _deconstructPacket(_data.get(i), buffers));
            }
            return newData;
        } else if (data instanceof JSONObject) {
            JSONObject newData = new JSONObject();
            JSONObject _data = (JSONObject)data;
            Iterator<?> iterator = _data.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String)iterator.next();
				newData.put(key, _deconstructPacket(_data.get(key), buffers));
            }
            return newData;
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public static Packet reconstructPacket(Packet packet, byte[][] buffers) {
        packet.data = _reconstructPacket(packet.data, buffers);
        packet.attachments = -1;
       return packet;
    }

    private static Object _reconstructPacket(Object data, byte[][] buffers) {
        if (data instanceof JSONArray) {
            JSONArray _data = (JSONArray)data;
            int i = 0;
            for (Object v : _data) {
				_data.add(i, _reconstructPacket(v, buffers));
				i++;
            }
            return _data;
        } else if (data instanceof JSONObject) {
            JSONObject _data = (JSONObject)data;
            if (Boolean.valueOf((String)_data.get(KEY_PLACEHOLDER))) {
                int num = Optional.<Integer>of((Integer)_data.get(KEY_NUM)).orElse(-1);
                return num >= 0 && num < buffers.length ? buffers[num] : null;
            }
            Iterator<?> iterator = _data.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String)iterator.next();
				_data.put(key, _reconstructPacket(_data.get(key), buffers));
            }
            return _data;
        }
        return data;
    }

    public static class DeconstructedPacket {

        public Packet packet;
        public byte[][] buffers;
    }
}


