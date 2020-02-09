package ir.parsmobiledesign.quantum.RecyclerViews;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import ir.parsmobiledesign.quantum.Interfaces.SwipeListener;

public class SimpleTouchCallback extends ItemTouchHelper.Callback {
    SwipeListener swipeListener;

    public SimpleTouchCallback(SwipeListener iswipeListener) {
        this.swipeListener = iswipeListener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.START);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        this.swipeListener.onSwipe(viewHolder.getAdapterPosition());
    }
}
