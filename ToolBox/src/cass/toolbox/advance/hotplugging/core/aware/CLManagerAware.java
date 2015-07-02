package cass.toolbox.advance.hotplugging.core.aware;

import cass.toolbox.advance.hotplugging.core.ClassLoaderManager;

public interface CLManagerAware {
	void setManager(ClassLoaderManager clm);
}
