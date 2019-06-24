/*****************************************************************************
 *
 * FILENAME:        com.grandstream.gxp2200.demo.GlobalConfig.java
 *
 * LAST REVISION:   $Revision: 1.0
 * LAST MODIFIED:   $Date: 2012-12-4
 *
 *
 * vi: set ts=4:
 *
 * Copyright (c) 2009-2013 by Grandstream Networks, Inc.
 * All rights reserved.
 *
 * This material is proprietary to Grandstream Networks, Inc. and,
 * in addition to the above mentioned Copyright, may be
 * subject to protection under other intellectual property
 * regimes, including patents, trade secrets, designs and/or
 * trademarks.
 *
 * Any use of this material for any purpose, except with an
 * express license from Grandstream Networks, Inc. is strictly
 * prohibited.
 *
 ***************************************************************************/
package com.grandstream.gxp2200.demo;

public class GlobalConfig {
	public static final String NUMBER = "number";
	public static final String ACCOUNT = "account";
	public static final String CONTENT = "content";//Receive sms's content
	// EDITENABLE priority higher than DRAFT 
	public static final String EDITENABLE = "editable";
	public static final String DRAFT = "draft";

	public static boolean isAccountAvailable(int account) {

		// accountID [0 ~ 5]
		if (account < 0 || account > 5) {
			return false;
		}
		return true;
	}
}
 