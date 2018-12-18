package com.mikealbert.vision.view.link;

import com.mikealbert.data.entity.ExternalAccount;

public interface EditViewClientLink {

	/**
	 * Override this method in the target bean in which we want to link to edit view driver screen through a composite component like vehicleDisplay.
	 * We can user this method in any number of target beans and any number of components to link to edit view driver screen.
	 * This method should link to edit view driver screen.
	 */
	public void editViewClient(ExternalAccount account);
}
