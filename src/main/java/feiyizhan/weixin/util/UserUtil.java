package feiyizhan.weixin.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import blade.kit.StringKit;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;

/**
 * 用户工具类
 * @author Pluto Xu
 *
 */
public class UserUtil {

	/**
	 * 获取用户名的关键字
	 */
	public static final List<String> NAMES_KEYS = Arrays.asList("RemarkName","NickName");
	/**
	 * 获取备注用户名，如果用户有备注名，返回备注名，没有返回用户名。
	 * @param user
	 * @return
	 */
	public static String getUserRemarkName(JSONObject user){
		String name = "这个人物名字未知";
		if(null!=user){
			if(StringKit.isNotBlank(user.getString("RemarkName"))){
				name = user.getString("RemarkName");
			}else{
				name = user.getString("NickName");
			}
		}
		return name;
	}
	
	/**
	 * 获取用户ID
	 * @param user
	 * @return
	 */
	public static String getUserID(JSONObject user){
		return user.getString("UserName");
	}
	
	/**
	 * 返回该用户ID在该用户列表的下标,未找到返回-1
	 * @param userList
	 * @param id
	 * @return
	 */
	public static int findUserIndexByID(JSONArray userList,String id){
		return JSONUtil.findIndexByKey(userList, "UserName", id);
	}
	
	/**
	 * 返回该用户ID在该用户列表下的用户对象,未找到返回null
	 * @param userList
	 * @param id
	 * @return
	 */
	public static JSONObject findUserObjectByID(JSONArray userList,String id){
		int index =findUserIndexByID(userList,id);
		return index ==-1?null:userList.getJSONObject(index);
	}

	/**
	 *  返回该用户名在该用户列表下的下标，未找到返回-1
	 * @param userList
	 * @param id
	 * @return
	 */
	public static int findUserIndexByName(JSONArray userList,String name){
		return JSONUtil.findIndexByKey(userList,NAMES_KEYS, name);
	}
	

	/**
	 *  返回该用户名在该用户列表下的用户对象，未找到返回null
	 * @param userList
	 * @param id
	 * @return
	 */
	public static JSONObject findUserObjectByName(JSONArray userList,String name){
		int index =findUserIndexByName(userList, name);
		return index ==-1?null:userList.getJSONObject(index);
	}
	
	
	/**
	 * 返回该用户在该用户列表的下标,未找到返回-1
	 * @param userList
	 * @param id
	 * @return
	 */
	public static int findUserIndexByUser(JSONArray userList,JSONObject user){
		return JSONUtil.findIndexByKey(userList, "UserName", getUserID(user));
	}
	
	
	/**
	 * 根据ID判断用户是否存在
	 * @param userList
	 * @param id
	 * @return
	 */
	public static boolean isFoundByID(JSONArray userList,String id){
		return findUserIndexByID(userList,id) !=-1;
	}
	
	/**
	 * 根据用户名判断用户是否存在
	 * @param userList
	 * @param id
	 * @return
	 */
	public static boolean isFoundByName(JSONArray userList,String name){
		return findUserIndexByName(userList,name) !=-1;
	}
	
	/**
	 * 判断用户是否存在
	 * @param userList
	 * @param user
	 * @return
	 */
	public static boolean isFound(JSONArray userList,JSONObject user){
		return isFoundByID(userList, getUserID(user));
	}
	
	/**
	 * 删除用户
	 * @param userList
	 * @param user
	 * @return
	 */
	public static boolean remove(JSONArray userList,JSONObject user){
		int index = findUserIndexByUser(userList,user);
		if (index == -1){
			return false;
		}else{
			userList.remove(index);
			return true;
		}
	}
	
	/**
	 * 增加用户
	 * @param userList
	 * @param user
	 * @return
	 */
	public static boolean add(JSONArray userList,JSONObject user){
		if (isFound(userList,user)){
			return false;
		}else{
			userList.add(user);
			return true;
		}
	}
	
	/**
	 * 获取指定群的成员列表,无成员列表返回null。
	 * @param group
	 * @return
	 */
	public static JSONArray getGroupMemberList(JSONObject group){
		if(group!=null){
			return group.getJSONArray("MemberList");
		}else{
			return null ;
		}
	}
	
	
	/**
	 * 移除群成员清单
	 * @param group
	 */
	public static void removeGroupMemberList(JSONObject group){
		group.remove("MemberList");
	}
	
	
	/**
	 * 增加群成员清单
	 * @param group
	 * @param memberList
	 */
	public static void addGroupMemberList(JSONObject group,JSONArray memberList){
		group.put("MemberList", memberList);
	}
	
	/**
	 * 替换群成员列表
	 * @param group
	 * @param memberList
	 */
	public static void replaceGroupMemberList(JSONObject group,JSONArray memberList){
		removeGroupMemberList(group);
		addGroupMemberList(group,memberList);
	}
	/**
	 * 转换为获取联系人清单的对象
	 * @param user
	 * @return
	 */
	public static JSONObject transferToGetContactFromat(String id,String roomID){
		JSONObject obj = new JSONObject();
		obj.put("UserName", id);
		obj.put("EncryChatRoomId", roomID);
		return obj;
		
	}
	
	/**
	 * 转换为获取联系人清单的对象列表
	 * @param idList
	 * @param roomID
	 * @return
	 */
	public static JSONArray transferToGetContactFromatArray(List<String> idList,String roomID){
		JSONArray list = new JSONArray();
		for(String str:idList){
			list.add(transferToGetContactFromat(str,roomID));
		}
		return list;
	}
	
	/**
	 * 合并用户清单
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static JSONArray combinUserList(JSONArray list1,JSONArray list2){
		if(list1!=null && list2!=null){
			for(JSONValue val :list2){
				JSONObject obj = val.asObject();
				UserUtil.add(list1, obj);
				
			}
			return list1;
			
		}else{
			return list1;
		}
	}
	
	
}
