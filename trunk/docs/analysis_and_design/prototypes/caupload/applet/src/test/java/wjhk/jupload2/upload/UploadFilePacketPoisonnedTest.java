//
// $Id$
//
// jupload - A file upload applet.
//
// Copyright 2010 The JUpload Team
//
// Created: 27 janv. 2010
// Creator: etienne_sf
// Last modified: $Date$
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

package wjhk.jupload2.upload;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author etienne_sf
 * 
 */

public class UploadFilePacketPoisonnedTest extends AbstractJUploadTestHelper {
	/** */
	@Test
	public void testIsPoisonned() {
		UploadFilePacket uploadFilePacket = new UploadFilePacketPoisonned(
				this.uploadPolicy);
		Assert.assertTrue("This class is poisonned", uploadFilePacket
				.isPoisonned());
	}

}
