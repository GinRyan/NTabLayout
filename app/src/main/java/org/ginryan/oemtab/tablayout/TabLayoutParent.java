package org.ginryan.oemtab.tablayout;

public interface TabLayoutParent {
    public int getItemIndexByView(TabChild tabChild);

    public void notifyUpdateParent(int item);
}
