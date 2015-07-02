package cass.toolbox.advance.hotplugging.core;

import java.io.InputStream;

public interface ClassFileDecoder {
	byte[] decode(InputStream fin,long len);
}
