package feiyizhan.weixin.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 * @author Pluto Xu
 *
 */
public class StringUtil {

	/**
	 * 转换为Unicode值的字符串。 例如 \u541b\u60a6
	 * 
	 * @param s
	 * @return
	 */
	public static String convertToUnicodeValueString(String s) {
		StringBuilder sb = new StringBuilder(s.length() * 3);
		for (char c : s.toCharArray()) {
			if (c < 256) {
				sb.append(c);
			} else {
				sb.append("\\u");
				sb.append(Character.forDigit((c >>> 12) & 0xf, 16));
				sb.append(Character.forDigit((c >>> 8) & 0xf, 16));
				sb.append(Character.forDigit((c >>> 4) & 0xf, 16));
				sb.append(Character.forDigit((c) & 0xf, 16));
			}
		}
		return sb.toString();
	}

	/**
	 * 转换字符串中的中文为Unicode值
	 * 
	 * @param s
	 * @return
	 */
	public static String convertChineseToUnicodeValueString(String s) {
		if(s==null || s.length()==0){
			return s;
		}
		final String pattern = "([\u4e00-\u9fa5])";
		Pattern r = Pattern.compile(pattern);

		// Now create matcher object.
		Matcher m = r.matcher(s);

		while (m.find()) {
			s = s.replace(m.group(0), StringUtil.convertToUnicodeValueString(m.group(0)));
			m = r.matcher(s);
		}

		return s;

	}

}
