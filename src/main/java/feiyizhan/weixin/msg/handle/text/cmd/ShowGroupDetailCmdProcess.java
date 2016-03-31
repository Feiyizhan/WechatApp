package feiyizhan.weixin.msg.handle.text.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import blade.kit.StringKit;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import feiyizhan.weixin.UserSession;
import feiyizhan.weixin.msg.handle.text.CmdTextMessageHandle;
import feiyizhan.weixin.util.UserUtil;

/**
 * 显示群明细
 * @author Pluto Xu
 *
 */
public class ShowGroupDetailCmdProcess extends BaseCmdProcess {
	private static final Logger LOGGER = LoggerFactory.getLogger(ShowGroupDetailCmdProcess.class);

	public ShowGroupDetailCmdProcess(CmdTextMessageHandle handle) {
		super(handle);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public String help() {
		// TODO 自动生成的方法存根
		StringBuilder sb = new StringBuilder();
		sb.append("============================\n");
		sb.append("该命令用于当前群的一些信息。\n");
		sb.append("显示当前群明细命令:\n");
		sb.append("--在群聊天界面输入：\n");
		sb.append("【显示明细】:显示明细  -- 显示当前群成员明细\n");
		sb.append("【显示地域报告】:显示地域报告  -- 显示当前群成员地域报告\n");
		sb.append("【显示未改名的群成员】:显示未改名的群成员  -- 显示当前群成员中未改名的清单\n");
		sb.append("【显示不符合群名规则的群成员】:显示不符合群名规则的群成员 前缀  -- 显示当前群成员中未按照改名规则的改名的清单（前缀匹配）\n");
		return sb.toString();
	}

	@Override
	public boolean process(String fromUserID, String toUserID, String content, String currUserID) {
		// TODO 自动生成的方法存根
		String[] cmds = content.split("[ ]");
		String cmd=null;
		String prefix=null ;  //前缀
		if(fromUserID.equals(toUserID)) return false;  //自己的聊天窗口不执行
		if(cmds.length==2){
			cmd =  cmds[0];
			prefix = cmds[1];
		}else if(cmds.length==1){
			if(!toUserID.startsWith("@@")) return false;
			cmd = cmds[0];
		}else{
			return false;
		}
		
		if(!isProcess(cmd)){
			return false;
		}
		
		if("显示明细".equals(cmd)){
			this.getHandle().getSession().sendTextMessage(getGroupDetail(this.getHandle().getSession().getGroup(toUserID)),toUserID);
			return true;
		}else if("显示地域报告".equals(cmd))	{
			this.getHandle().getSession().sendTextMessage(getGroupRegionDetail(this.getHandle().getSession().getGroup(toUserID)),toUserID);
			return true;
		}else if("显示未改名的群成员".equals(cmd))	{
			this.getHandle().getSession().sendTextMessage(getGroupDoNotChangeNameDetail(this.getHandle().getSession().getGroup(toUserID)),toUserID);
			return true;
		}else if("显示不符合群名规则的群成员".equals(cmd))	{
			this.getHandle().getSession().sendTextMessage(getGroupNonConfirmNameDetail(this.getHandle().getSession().getGroup(toUserID),prefix),toUserID);
			return true;
		}else{
			return false;
		}	
	}



	@Override
	public boolean isProcess(String cmd) {
		// TODO 自动生成的方法存根
		if("显示明细".equals(cmd)){
			return true;
		}else if("显示地域报告".equals(cmd)){
			return true;
		}else if("显示未改名的群成员".equals(cmd)){
			return true;
		}else if("显示不符合群名规则的群成员".equals(cmd)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 获取群明细
	 * @return
	 */
	private String getGroupDetail(JSONObject group){
		StringBuilder sb = new StringBuilder();
		sb.append("当前群【");
		sb.append(UserUtil.getUserName(group));
		sb.append("】:\n");
		JSONArray memberList = UserUtil.getGroupMemberList(group);
		sb.append("总共有【");
		sb.append(memberList.size());
		sb.append("】个成员\n");
		List<String> unisexList = new ArrayList<String>();  // 未设置性别
		List<String> maleList = new ArrayList<String>();  //男性
		List<String> femaleList = new ArrayList<String>();  //女性
		List<String> contactList = new ArrayList<String>();  //通讯录统计
		for(JSONValue val:memberList){
			JSONObject member = val.asObject();
			if(UserUtil.isFound(this.getHandle().getSession().MemberList, member)){  //该用户在个人通讯录里
				contactList.add(UserUtil.getUserName(member));
			}
			int sex = member.getInt("Sex", -1);
			switch (sex){
			case 0:{ //不男不女
				unisexList.add(UserUtil.getUserName(member));
				break;
			}
			case 1:{ //男性
				maleList.add(UserUtil.getUserName(member));
				break;
			}
			case 2:{ //女性
				femaleList.add(UserUtil.getUserName(member));
				break;
			}
			default:{  //未识别的性别信息
				LOGGER.info("[*]未识别的性别信息"+member);
			}
			}
			

			
			
		}
		
		sb.append(" 其中有【"+ maleList.size()+"】位男性。\n");
		sb.append(" 有【"+ femaleList.size()+"】位女性。\n");
		sb.append(" 有【"+ unisexList.size()+"】位未设置性别。\n");
		sb.append("分别是："+unisexList+"\n");
		sb.append(" 有【"+ contactList.size()+"】位已加为好友。\n");
		return sb.toString();
		
		
	}
	
	/**
	 * 显示群成员地域明细
	 * @param group
	 * @return
	 */
	private String getGroupRegionDetail(JSONObject group){
		StringBuilder sb = new StringBuilder();
		sb.append("当前群【");
		sb.append(UserUtil.getUserName(group));
		sb.append("】:\n");
		JSONArray memberList = UserUtil.getGroupMemberList(group);
		String maxName = "";
		int max =0 ;
		
		
		Map<String,Integer> regionCountMap =  new TreeMap<String,Integer>();  //区域统计
		for(JSONValue val:memberList){
			JSONObject member = val.asObject();
			
			
			//统计地域信息
			String province = member.getString("Province");  //省
			if(StringKit.isBlank(province)){
				province ="未设置省或直辖市";
			}
			String city =  member.getString("City");  //市
			if(StringKit.isBlank(city)){
				city ="未设置市";
			}
			String region = province+"-"+city;
			Integer regionCount = regionCountMap.get(region);
			if(regionCount!=null){
				regionCount++;
			}else{
				regionCount=1;
			}
			if(max < regionCount){
				max = regionCount;
				maxName = region;
			}
			regionCountMap.put(region, regionCount);
			
			
		}
		sb.append(" 群友的地域在【"+ regionCountMap.size()+"】个区域。。\n");
		sb.append(" 其中最多的是来自【"+ maxName+"】的，有【"+max+"】个\n");
		sb.append(" 分别为【"+ regionCountMap+"】。\n");
		return sb.toString();
		
		
	}

	
	
	/**
	 * 显示未改名的群成员
	 * @param group
	 * @return
	 */
	private String getGroupDoNotChangeNameDetail(JSONObject group){
		StringBuilder sb = new StringBuilder();
		sb.append("当前群【");
		sb.append(UserUtil.getUserName(group));
		sb.append("】:\n");
		JSONArray memberList = UserUtil.getGroupMemberList(group);
		
		List<String> displayNames =  new ArrayList<String>();  //区域统计
		for(JSONValue val:memberList){
			JSONObject member = val.asObject();
			
			
			//统计名称
			String displayName = UserUtil.getDisplayName(member)  ;//群里显示的名称
			if(StringKit.isBlank(displayName)){
				displayNames.add(UserUtil.getUserName(member));
			}
			
		}
		sb.append(" 有【"+ displayNames.size()+"】个人未修改名称。\n");
		sb.append(" 分别是【"+ displayNames+"】。\n");
		return sb.toString();
		
		
	}
	
	/**
	 * 显示有修改名称，但没有按规则修改名称的群成员
	 * @param group
	 * @return
	 */
	private String getGroupNonConfirmNameDetail(JSONObject group,String prefix){
		StringBuilder sb = new StringBuilder();
		sb.append("当前群【");
		sb.append(UserUtil.getUserName(group));
		sb.append("】:\n");
		JSONArray memberList = UserUtil.getGroupMemberList(group);
		
		List<String> displayNames =  new ArrayList<String>();  //区域统计
		for(JSONValue val:memberList){
			JSONObject member = val.asObject();
			
			
			//统计名称
			String displayName = UserUtil.getDisplayName(member)  ;//群里显示的名称
			if(StringKit.isNotBlank(displayName)){
				if(!displayName.startsWith("prefix")){
					displayNames.add(UserUtil.getUserName(member));
				}
				
			}
			
		}
		sb.append(" 有【"+ displayNames.size()+"】个人有修改名称，但没有按规则修改名称。\n");
		sb.append(" 分别是【"+ displayNames+"】。\n");
		return sb.toString();
		
		
	}
}
