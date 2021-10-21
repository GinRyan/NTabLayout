package org.ginryan.github.slidingtab;

public interface TabLayoutParent {
    public int getItemIndexByView(TabChild tabChild);

    public void notifyUpdateParent(int item);
}
