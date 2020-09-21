package com.apps.brando.stormbringer;

public interface EditModeListener {
    void onEditModeChanged(boolean edit_mode);
    void undoChanges();
}
