package org.coody.framework.util;

import java.util.UUID;

public class JUUIDUtil {

	public synchronized static String createUuid() {
			String str = UUID.randomUUID().toString().replace("-", "");
			return str;
	}

	public static void main(String[] args) {
	}
}
