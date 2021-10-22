package org.ginryan.github.slidingtab;

/**
 * Updatable state
 */
public interface State {

    int STATE_CODE_CHECKED = 1001;
    int STATE_CODE_UNCHECK = 1000;

    public void updateState(boolean byCheck);

    public void checkState(int stateCode);

}
