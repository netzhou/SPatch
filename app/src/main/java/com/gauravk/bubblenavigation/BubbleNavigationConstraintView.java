package com.gauravk.bubblenavigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

public class BubbleNavigationConstraintView extends ConstraintLayout implements View.OnClickListener, IBubbleNavigation {
  private static final int MAX_ITEMS = 5;
  
  private static final int MIN_ITEMS = 2;
  
  private static final String TAG = "BNLView";
  
  private ArrayList<BubbleToggleView> bubbleNavItems;
  
  private int currentActiveItemPosition = 0;
  
  private Typeface currentTypeface;
  
  private DisplayMode displayMode = DisplayMode.SPREAD;
  
  private boolean loadPreviousState;
  
  private BubbleNavigationChangeListener navigationChangeListener;
  
  private SparseArray<String> pendingBadgeUpdate;
  
  public BubbleNavigationConstraintView(Context paramContext) {
    super(paramContext);
    init(paramContext, null);
  }
  
  public BubbleNavigationConstraintView(Context paramContext, AttributeSet paramAttributeSet) {
    super(paramContext, paramAttributeSet);
    init(paramContext, paramAttributeSet);
  }
  
  public BubbleNavigationConstraintView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext, paramAttributeSet);
  }
  
  private void createChains() {
    ConstraintSet constraintSet = new ConstraintSet();
    constraintSet.clone(this);
    int[] arrayOfInt = new int[this.bubbleNavItems.size()];
    float[] arrayOfFloat = new float[this.bubbleNavItems.size()];
    for (int i = 0; i < this.bubbleNavItems.size(); i++) {
      int j = ((BubbleToggleView)this.bubbleNavItems.get(i)).getId();
      arrayOfInt[i] = j;
      arrayOfFloat[i] = 0.0F;
      constraintSet.connect(j, 3, 0, 3, 0);
      constraintSet.connect(j, 4, 0, 4, 0);
    } 
    constraintSet.createHorizontalChain(getId(), 1, getId(), 2, arrayOfInt, arrayOfFloat, getChainTypeFromMode(this.displayMode));
    constraintSet.applyTo(this);
  }
  
  private int getChainTypeFromMode(DisplayMode paramDisplayMode) {
    int i = null.$SwitchMap$com$gauravk$bubblenavigation$BubbleNavigationConstraintView$DisplayMode[paramDisplayMode.ordinal()];
    return (i != 2) ? ((i != 3) ? 0 : 2) : 1;
  }
  
  private int getItemPositionById(int paramInt) {
    for (int i = 0; i < this.bubbleNavItems.size(); i++) {
      if (paramInt == ((BubbleToggleView)this.bubbleNavItems.get(i)).getId())
        return i; 
    } 
    return -1;
  }
  
  private void init(Context paramContext, AttributeSet paramAttributeSet) {
    int i = 0;
    if (paramAttributeSet != null) {
      TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.BubbleNavigationConstraintView, 0, 0);
      try {
        i = typedArray.getInteger(R.styleable.BubbleNavigationConstraintView_bnc_mode, 0);
      } finally {
        typedArray.recycle();
      } 
    } 
    if (i >= 0 && i < (DisplayMode.values()).length)
      this.displayMode = DisplayMode.values()[i]; 
    post(new Runnable() {
          public void run() { BubbleNavigationConstraintView.this.updateChildNavItems(); }
        });
  }
  
  private void setClickListenerForItems() {
    Iterator<BubbleToggleView> iterator = this.bubbleNavItems.iterator();
    while (iterator.hasNext())
      ((BubbleToggleView)iterator.next()).setOnClickListener(this); 
  }
  
  private void setInitialActiveState() {
    boolean bool1;
    if (this.bubbleNavItems == null)
      return; 
    boolean bool = this.loadPreviousState;
    boolean bool2 = false;
    if (!bool) {
      bool1 = false;
      int i = bool1;
      while (bool1 < this.bubbleNavItems.size()) {
        if (((BubbleToggleView)this.bubbleNavItems.get(bool1)).isActive() && i == 0) {
          this.currentActiveItemPosition = bool1;
          i = 1;
        } else {
          ((BubbleToggleView)this.bubbleNavItems.get(bool1)).setInitialState(false);
        } 
        bool1++;
      } 
      bool1 = i;
    } else {
      int i = 0;
      while (true) {
        bool1 = bool2;
        if (i < this.bubbleNavItems.size()) {
          ((BubbleToggleView)this.bubbleNavItems.get(i)).setInitialState(false);
          i++;
          continue;
        } 
        break;
      } 
    } 
    if (!bool1)
      ((BubbleToggleView)this.bubbleNavItems.get(this.currentActiveItemPosition)).setInitialState(true); 
  }
  
  private void updateChildNavItems() {
    this.bubbleNavItems = new ArrayList<BubbleToggleView>();
    boolean bool = false;
    int i = 0;
    while (i < getChildCount()) {
      View view = getChildAt(i);
      if (view instanceof BubbleToggleView) {
        this.bubbleNavItems.add((BubbleToggleView)view);
        i++;
        continue;
      } 
      Log.w("BNLView", "Cannot have child bubbleNavItems other than BubbleToggleView");
      return;
    } 
    if (this.bubbleNavItems.size() < 2) {
      Log.w("BNLView", "The bubbleNavItems list should have at least 2 bubbleNavItems of BubbleToggleView");
    } else if (this.bubbleNavItems.size() > 5) {
      Log.w("BNLView", "The bubbleNavItems list should not have more than 5 bubbleNavItems of BubbleToggleView");
    } 
    setClickListenerForItems();
    setInitialActiveState();
    updateMeasurementForItems();
    createChains();
    Typeface typeface = this.currentTypeface;
    if (typeface != null)
      setTypeface(typeface); 
    if (this.pendingBadgeUpdate != null && this.bubbleNavItems != null) {
      for (i = bool; i < this.pendingBadgeUpdate.size(); i++)
        setBadgeValue(this.pendingBadgeUpdate.keyAt(i), (String)this.pendingBadgeUpdate.valueAt(i)); 
      this.pendingBadgeUpdate.clear();
    } 
  }
  
  private void updateMeasurementForItems() {
    int i = this.bubbleNavItems.size();
    if (i > 0) {
      i = (getMeasuredWidth() - getPaddingRight() + getPaddingLeft()) / i;
      Iterator<BubbleToggleView> iterator = this.bubbleNavItems.iterator();
      while (iterator.hasNext())
        ((BubbleToggleView)iterator.next()).updateMeasurements(i); 
    } 
  }
  
  public int getCurrentActiveItemPosition() { return this.currentActiveItemPosition; }
  
  public void onClick(View paramView) {
    int i = getItemPositionById(paramView.getId());
    if (i >= 0) {
      int j = this.currentActiveItemPosition;
      if (i == j)
        return; 
      BubbleToggleView bubbleToggleView1 = this.bubbleNavItems.get(j);
      BubbleToggleView bubbleToggleView2 = this.bubbleNavItems.get(i);
      if (bubbleToggleView1 != null)
        bubbleToggleView1.toggle(); 
      if (bubbleToggleView2 != null)
        bubbleToggleView2.toggle(); 
      this.currentActiveItemPosition = i;
      BubbleNavigationChangeListener bubbleNavigationChangeListener = this.navigationChangeListener;
      if (bubbleNavigationChangeListener != null) {
        bubbleNavigationChangeListener.onNavigationChanged(paramView, i);
        return;
      } 
    } else {
      Log.w("BNLView", "Selected id not found! Cannot toggle");
    } 
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable) {
    Parcelable parcelable = paramParcelable;
    if (paramParcelable instanceof Bundle) {
      Bundle bundle = (Bundle)paramParcelable;
      this.currentActiveItemPosition = bundle.getInt("current_item");
      this.loadPreviousState = bundle.getBoolean("load_prev_state");
      parcelable = bundle.getParcelable("superState");
    } 
    super.onRestoreInstanceState(parcelable);
  }
  
  protected Parcelable onSaveInstanceState() {
    Bundle bundle = new Bundle();
    bundle.putParcelable("superState", super.onSaveInstanceState());
    bundle.putInt("current_item", this.currentActiveItemPosition);
    bundle.putBoolean("load_prev_state", true);
    return (Parcelable)bundle;
  }
  
  public void setBadgeValue(int paramInt, String paramString) {
    ArrayList<BubbleToggleView> arrayList = this.bubbleNavItems;
    if (arrayList != null) {
      BubbleToggleView bubbleToggleView = arrayList.get(paramInt);
      if (bubbleToggleView != null) {
        bubbleToggleView.setBadgeText(paramString);
        return;
      } 
    } else {
      if (this.pendingBadgeUpdate == null)
        this.pendingBadgeUpdate = new SparseArray(); 
      this.pendingBadgeUpdate.put(paramInt, paramString);
    } 
  }
  
  public void setCurrentActiveItem(int paramInt) {
    ArrayList<BubbleToggleView> arrayList = this.bubbleNavItems;
    if (arrayList == null) {
      this.currentActiveItemPosition = paramInt;
      return;
    } 
    if (this.currentActiveItemPosition == paramInt)
      return; 
    if (paramInt >= 0) {
      if (paramInt >= arrayList.size())
        return; 
      ((BubbleToggleView)this.bubbleNavItems.get(paramInt)).performClick();
    } 
  }
  
  public void setNavigationChangeListener(BubbleNavigationChangeListener paramBubbleNavigationChangeListener) { this.navigationChangeListener = paramBubbleNavigationChangeListener; }
  
  public void setTypeface(Typeface paramTypeface) {
    ArrayList<BubbleToggleView> arrayList = this.bubbleNavItems;
    if (arrayList != null) {
      Iterator<BubbleToggleView> iterator = arrayList.iterator();
      while (iterator.hasNext())
        ((BubbleToggleView)iterator.next()).setTitleTypeface(paramTypeface); 
    } else {
      this.currentTypeface = paramTypeface;
    } 
  }
  
  enum DisplayMode {
    INSIDE, PACKED, SPREAD;
    
    static  {
      INSIDE = new DisplayMode("INSIDE", 1);
      DisplayMode displayMode = new DisplayMode("PACKED", 2);
      PACKED = displayMode;
      $VALUES = new DisplayMode[] { SPREAD, INSIDE, displayMode };
    }
  }
}


/* Location:              C:\User\\user\Desktop\classes-dex2jar.jar!\com\gauravk\bubblenavigation\BubbleNavigationConstraintView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.1
 */