package org.ginryan.oemtab.tablayout;

/**
 * 状态接口，可更新状态
 */
public interface State {

    int STATE_CODE_CHECKED = 1001;
    int STATE_CODE_UNCHECK = 1000;

    public void updateState(boolean byCheck);

    public void checkState(int stateCode);

}
