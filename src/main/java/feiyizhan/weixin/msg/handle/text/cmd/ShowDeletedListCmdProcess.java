package feiyizhan.weixin.msg.handle.text.cmd;

import java.util.ArrayList;
import java.util.List;

import blade.kit.StringKit;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import feiyizhan.weixin.msg.handle.text.CmdTextMessageHandle;
import feiyizhan.weixin.util.UserUtil;

/**
 * 显示删除掉我的用户清单命令处理模块
 * @author Pluto Xu
 *
 */
public class ShowDeletedListCmdProcess extends BaseCmdProcess {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShowDeletedListCmdProcess.class);

	public ShowDeletedListCmdProcess(CmdTextMessageHandle handle) {
		super(handle);
		// TODO 自动生成的构造函数存根
	}
	
	

	@Override
	public String help() {
		// TODO 自动生成的方法存根
		StringBuilder sb = new StringBuilder();
		sb.append("显示删除我的联系人清单:\n");
		sb.append("--个人聊天模式\n");
		sb.append("【获取删除我的用户清单】:获取删除我的用户清单  -- 显示删除我的用户清单\n");
		return sb.toString();
	}

	@Override
	public boolean process(final String fromUserID, String toUserID, String content, String currUserID) {
		// TODO 自动生成的方法存根
		String[] cmds = content.split("[ ]");
		String cmd=null;
		if(cmds.length==2){
			return false;
		}else if(cmds.length==1){
			cmd = cmds[0];
		}else{
			return false;
		}
		if(!isProcess(cmd)){
			return false;
		}
		
		if("获取删除我的用户清单".equals(cmd)){
			if(!this.getHandle().getControl().isBatchFlag()){
				this.getHandle().getControl().setBatchFlag(true);
				new Thread(){
					
					/* （非 Javadoc）
					 * @see java.lang.Thread#run()
					 */
					public void run(){
						List<String> list = getDeletedMeUserListWithCreatRoom(fromUserID);
						getHandle().getSession().webwxsendmsg("【系统消息】：\n 总共有【"+list.size()+"】个用户删除了我。清单如下：\n",fromUserID);
						int count =0;
						ArrayList<String> names = new ArrayList<String>();
						for(String id:list){
							if(!getHandle().getControl().isBatchFlag()){
								break;
							}
							count ++ ;
							String userName = getHandle().getSession().getUserName(id, null);
							names.add(userName);
							LOGGER.debug("[*] "+id + "|"+userName);
							if(count >=70){
								count =0;
								getHandle().getSession().webwxsendmsg(names.toString()+"\n",fromUserID);
								names = new ArrayList<String>();
							}
							
						}
						if(count >0){
							getHandle().getSession().webwxsendmsg(names.toString()+"\n",fromUserID);
						}
						getHandle().getSession().webwxsendmsg("【系统消息】清单结束 \n",fromUserID);
	
					}
					
					
				}.start();
				
			}
			
			return true;
		}else{
			return false;
		}
		
	}

	@Override
	public boolean isProcess(String cmd) {
		// TODO 自动生成的方法存根
		if("获取删除我的用户清单".equals(cmd)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 获取删除我的用户清单。
	 * @param roomID
	 * @return
	 */
	public ArrayList<String> getDeletedMeUserListWithCreatRoom(String fromUserID){
		ArrayList<String> list = new ArrayList<String> ();
		int count =0;
		JSONArray memberList = new JSONArray();
		JSONArray ContactList = this.getHandle().getSession().ContactList;
		int maxCount = ContactList.size();
		int processedCount =0 ;
		this.sendTextMessage("【系统消息】\n您有【"+maxCount+"】个联系人需要甄别", fromUserID);
		for(JSONValue val:ContactList){
			if(!getHandle().getControl().isBatchFlag()){
				break;
			}
			JSONObject obj = val.asObject();
			String id =obj.getString("UserName");
			list.add(id);
			if(id.startsWith("@@")){  // 置顶的群不处理
				list.remove(id);
				continue;
			}
			
			JSONObject member = new JSONObject();
			member.put("UserName", id);
			memberList.add(member);
			LOGGER.debug("[*]"+id +"|"+UserUtil.getUserRemarkName(obj));
			count ++ ;
			if (count>=35){
				processedCount +=count;
				this.sendTextMessage("【系统消息】\n当前已处理【"+processedCount+"】个联系人，还有【"+(maxCount-processedCount)+"】个联系人待处理。", fromUserID);
				JSONObject room = this.getHandle().getSession().webwxCreateChatRoom(memberList);  //调用创建群方法
				memberList = new JSONArray();
				count =0;
				String roomID = room.getString("ChatRoomName");
				if(StringKit.isNotBlank(roomID)){
					JSONArray roomMemberList = room.getJSONArray("MemberList");
					for(JSONValue roomMember:roomMemberList){
						JSONObject memberObj = roomMember.asObject();
						String memberID =memberObj.getString("UserName");
						int memberStatus = memberObj.getInt("MemberStatus",-1);
						if(memberStatus!=4){  //被删除的用户无法入群
							list.remove(memberID);
						}
						
					}
				}else{
					this.sendTextMessage("【系统消息】\n处理失败，还有【"+(maxCount-processedCount)+"】个联系人未处理。", fromUserID);
					return list;
				}
				try {
					Thread.sleep(1000*60*5);
				} catch (InterruptedException e) {
				}
			}
			
		}
		
		if(!getHandle().getControl().isBatchFlag()){
			return list;
		}
		
		if (count>=0){
			JSONObject room = this.getHandle().getSession().webwxCreateChatRoom(memberList);  //调用创建群方法
			memberList = new JSONArray();
			count =0;
			String roomID = room.getString("ChatRoomName");
			if(StringKit.isNotBlank(roomID)){
				JSONArray roomMemberList = room.getJSONArray("MemberList");
				for(JSONValue roomMember:roomMemberList){
					JSONObject memberObj = roomMember.asObject();
					String memberID =memberObj.getString("UserName");
					int memberStatus = memberObj.getInt("MemberStatus",-1);
					if(memberStatus!=4){  //被删除的用户无法入群
						list.remove(memberID);
					}
					
				}
			}
		}
		return list;
	}

	
	
	
	/**
	 * 获取删除我的用户清单。
	 * @param roomID
	 * @return
	 */
	public ArrayList<String> getDeletedMeUserListWithAddRoom(){
		ArrayList<String> list = new ArrayList<String> ();
		int count =0;
		JSONArray memberList = new JSONArray();
		for(JSONValue val:this.getHandle().getSession().ContactList){
			JSONObject obj = val.asObject();
			String id =obj.getString("UserName");
			list.add(id);
			if(id.startsWith("@@")){  // 置顶的群不处理
				list.remove(id);
				continue;
			}
			
			JSONObject member = new JSONObject();
			member.put("UserName", id);
			memberList.add(member);
			LOGGER.debug("[*]"+id +"|"+UserUtil.getUserRemarkName(obj));
			count ++ ;
			if (count>=50){
				JSONObject room = this.getHandle().getSession().webwxCreateChatRoom(memberList);  //调用创建群方法
				memberList = new JSONArray();
				count =0;
				String roomID = room.getString("ChatRoomName");
				if(StringKit.isNotBlank(roomID)){
					JSONArray roomMemberList = room.getJSONArray("MemberList");
					for(JSONValue roomMember:roomMemberList){
						JSONObject memberObj = roomMember.asObject();
						String memberID =memberObj.getString("UserName");
						int memberStatus = memberObj.getInt("MemberStatus",-1);
						if(memberStatus!=4){  //被删除的用户无法入群
							list.remove(memberID);
						}
						
					}
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
				}
			}
//			if(this.userSession.webwxupdatechatroomAdd(roomID, obj.getString("UserName"))){  //增加入群成功，说明没被删除
//				this.userSession.webwxupdatechatroomDel(roomID, obj.getString("UserName")); // 移除出群
//				LOGGER.info("[*]"+this.userSession.getUserRemarkName(obj));
//			}else{ //被删除
//				list.add(this.userSession.getUserRemarkName(obj));
//			}

			
			
		}
		return list;
	}

	
	/**
	 * 发送文本消息
	 * @param msg
	 * @param id
	 */
	private void sendTextMessage(String msg,String id){
		getHandle().getSession().webwxsendmsg(msg,id);
	}
}
