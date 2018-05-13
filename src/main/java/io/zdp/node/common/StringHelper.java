package io.zdp.node.common;

import org.apache.commons.lang3.StringUtils;

public class StringHelper {

	public static String cleanUpMemo(final String memo) {
		final StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(memo)) {
			for (Character c : memo.toCharArray()) {
				if (Character.isLetterOrDigit(c)) {
					sb.append(c);
				} else if (c == ' ') {
					sb.append(c);
				} else if (c == '-') {
					sb.append(c);
				} else if (c == '_') {
					sb.append(c);
				}
			}
		}

		String result = sb.toString().trim();

		if (result.length() > 64) {
			result = result.substring(0, 63);
		}

		return result;

	}

}
