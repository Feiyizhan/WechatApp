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
			if(!fromUserID.equals(toUserID)) return false;
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
						sendTextMessage("【系统消息】：\n 总共有【"+list.size()+"】个用户删除了我。清单如下：\n",fromUserID);
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
							if(count >=40){
								count =0;
								getHandle().getSession().webwxsendmsg(names.toString()+"\n",fromUserID);
								names = new ArrayList<String>();
							}
							
						}
						if(count >0){
							getHandle().getSession().webwxsendmsg(names.toString()+"\n",fromUserID);
						}
						sendTextMessage("【系统消息】清单结束 \n",fromUserID);
	
					}
					
					
				}.start();
			}else if("删除".equals(cmd)){
				this.markDeleted(this.getHandle().getSession().getUserByID(toUserID, ""));
				return true;
			}else{
				sendTextMessage("【系统消息】处理中。。。 \n",fromUserID);
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
		}else if("删除".equals(cmd)){
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
	public List<String> getDeletedMeUserListWithCreatRoom(String fromUserID){
		List<String> resultList = new ArrayList<String> ();
		List<String> list = new ArrayList<String> ();
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
			String id =UserUtil.getUserID(obj);
			String name = UserUtil.getUserRemarkName(obj);
			if(id.startsWith("@@")){  // 置顶的群不处理
				continue;
			}
			if(name.startsWith("A-A-DEL")){//已标注的略过，直接标志为删除
				resultList.add(id);
				continue;
			}  
			
			if(name.startsWith("B-B-")){//已处理的标注的略过
				continue;
			}  
			
			list.add(id);
			
			JSONObject member = new JSONObject();
			member.put("UserName", id);
			memberList.add(member);
			LOGGER.debug("[*]"+id +"|"+name);
			count ++ ;
			if (count>=30){
				processedCount +=count;
				if(this.processDeleted(processedCount, maxCount, memberList, list, fromUserID)!=null){
					resultList.addAll(list);
					
				}else{
					this.getHandle().getControl().setBatchFlag(false);
				}
				
				count =0;
				memberList = new JSONArray();
				list = new ArrayList<String> ();
				if(!getHandle().getControl().isBatchFlag()){
					break;
				}
				try {
					Thread.sleep(1000*60*15);
				} catch (InterruptedException e) {
				}
				
				
			}
			
		}
		
		if(!getHandle().getControl().isBatchFlag()){
			return resultList;
		}
		
		if (count>=0){
			if(this.processDeleted(processedCount, maxCount, memberList, list, fromUserID)!=null){
				resultList.addAll(list);
				
			}else{
				this.getHandle().getControl().setBatchFlag(false);
			}
		}
		
		resultList.addAll(list);
		return resultList;
	}

	
	/**
	 * 获取被删除的处理方法
	 * @param processedCount
	 * @param maxCount
	 * @param memberList
	 * @param list
	 * @param fromUserID
	 * @return
	 */
	private List<String> processDeleted(int processedCount,int maxCount,JSONArray memberList,List<String> list ,String fromUserID){
		this.sendTextMessage("【系统消息】\n当前已处理【"+processedCount+"】个联系人，还有【"+(maxCount-processedCount)+"】个联系人待处理。", fromUserID);
		JSONObject room = this.getHandle().getSession().webwxCreateChatRoom(memberList);  //调用创建群方法
		
		
		String roomID = room.getString("ChatRoomName");
		String errMsg = room.getString("ErrMsg");
		if(StringKit.isNotBlank(roomID)){
			JSONArray roomMemberList = room.getJSONArray("MemberList");
			for(JSONValue roomMember:roomMemberList){
				JSONObject memberObj = roomMember.asObject();
				String memberID =UserUtil.getUserID(memberObj);
				int memberStatus = memberObj.getInt("MemberStatus",-1);
				if(memberStatus!=4){  //被删除的用户无法入群
					list.remove(memberID);  //入群成功的，为未删除的用户，从删除清单中除去
					markProcessed(memberObj);
				}else{
					markDeleted(memberObj);  // 已删除的用户
				}
				try {
					Thread.sleep(1000*5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			return list;
		}else{
			this.sendTextMessage("【系统消息】\n处理失败，失败原因【"+errMsg+"】。还有【"+(maxCount-processedCount)+"】个联系人未处理。", fromUserID);
			return null;

		}
		
	}
	
	
	

	
	/**
	 * 发送文本消息
	 * @param msg
	 * @param id
	 */
	private void sendTextMessage(String msg,String id){
		getHandle().getSession().webwxsendmsg(msg,id);
	}
	
	/**
	 * 修改用户备注，标注为删除用户。
	 * @param user
	 */
	public void markDeleted(JSONObject user){
		String name = UserUtil.getUserRemarkName(user);
		String remarkName = "A-A-DEL"+name;
		if(!getHandle().getSession().changeUserRemarkName(user,remarkName)){
			LOGGER.info("[*]备注失败【"+name+"】");
		}
	}
	
	/**
	 * 修改用户备注，标注为已处理过的用户。
	 * @param user
	 */
	public void markProcessed(JSONObject user){
		String name = UserUtil.getUserRemarkName(user);
		String remarkName = "B-B-"+name;
		if(!getHandle().getSession().changeUserRemarkName(user,remarkName)){
			LOGGER.info("[*]备注失败【"+name+"】");
		}
	}
}
