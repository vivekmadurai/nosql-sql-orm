package me.util;

import java.util.Map;
import java.util.UUID;

public class Util {

   public static String generateUUID(String modelName) {
	   String modelFirstChar = modelName.substring(0,1);
       return modelFirstChar.concat(UUID.randomUUID().toString());
   }

    public static<KeyType,ValueType> KeyType findKey(Map<KeyType,ValueType> map,ValueType value){
        for (Map.Entry<KeyType, ValueType> entry : map.entrySet()) {
            if(value == null && entry.getValue() == null){
                return entry.getKey();
            } else if (entry.getValue() != null && value.equals(entry.getValue())){
                return entry.getKey();
            }
        }
        return null;
    }
}