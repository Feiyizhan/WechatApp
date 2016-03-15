package feiyizhan.weixin.util;

import java.util.ArrayList;
import java.util.List;

import blade.kit.StringKit;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;

/**
 * JSON 工具类
 * @author Pluto Xu
 *
 */
public class JSONUtil {
	
	/**
	 * 返回JSONArray 中根据names匹配的name的对应的值列表。<br/>
	 * 按照names中的顺序，获取到第一个StringKit.isNotBlank为false的值的集合字符串，
	 * @param array
	 * @param names
	 * @return
	 */
	public static String getStringValues(JSONArray array,List<String> names){
		if(null!=array && null!=names && array.size()>0 && names.size()>0){
			ArrayList<String> values = new ArrayList<String>();
			for(JSONValue val:array){
				JSONObject obj = val.asObject();
				for(String name:names){
					String nameVal = obj.getString(name);
					if(StringKit.isNotBlank(nameVal.toString())){
						values.add(nameVal);
						break;
						
					}
				}
			}
			
			return values.toString();
		}else{
			return null ;
		}
		
	}
	
	/**
	 * 根据name和name对应的value在array查找，找到返回下标，否则返回-1
	 * @param array
	 * @param name
	 * @param value
	 * @return
	 */
	public static int findIndexByKey(JSONArray array,String name,String value){
		if(null!=array){
			for(int i=0;i<array.size();i++){
				JSONObject obj = array.getJSONObject(i);
				String val =obj.getString(name);
				if(StringKit.isNotBlank(val)&&value.equals(val)){
					return i;
				}
			}
			return -1;
		}else{
			return -1;
		}
	}
	
	/**
	 * 根据name和name对应的value在array查找，找到返回JSONObject，否则返回null
	 * @param array
	 * @param name
	 * @param value
	 * @return
	 */
	public static JSONObject findObjectByKey(JSONArray array,String name,String value){
		int index = findIndexByKey(array,name,value);
		
		return index!=-1?array.getJSONObject(index):null;
	}
	
	
	/**
	 * 按照names中的顺序，获取到第一个匹配value的值的对象对应的下标。未找到返回-1
	 * @param array
	 * @param name
	 * @param value
	 * @return
	 */
	public static int findIndexByKey(JSONArray array,List<String> names,String value){
		if(null!=array){
			for(int i=0;i<array.size();i++){
				JSONObject obj = array.getJSONObject(i);
				for(String name:names){
					String val =obj.getString(name);
					if(StringKit.isNotBlank(val)&&value.equals(val)){
						return i;
					}
				}

			}
			return -1;
		}else{
			return -1;
		}
	}
	
	/**
	 * 按照names中的顺序，获取到第一个匹配value的值的对象对应的对象。未找到返回null
	 * @param array
	 * @param name
	 * @param value
	 * @return
	 */
	public static JSONObject findObjectByKey(JSONArray array,ArrayList<String> names,String value){
		int index = findIndexByKey(array,names,value);
		return index!=-1?array.getJSONObject(index):null;
	}

}
