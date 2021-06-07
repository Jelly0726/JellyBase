package win.smartown.tableLayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Smartown on 2017/7/19.
 */
public class TableLayout extends LinearLayout implements TableColumn.Callback {

    private int tableMode;
    private int tableRowHeight;
    //填充方向为 VERTICAL 时保存最大单元格的宽度
    private float maxTableColumnWidth = 0;
    //单元格分割线
    private float tableDividerSize;
    private int tableDividerColor;
    //外边框
    private float tableBorderSize;
    private int tableBorderColor;
    private float tableLeftBorderSize;
    private int tableLeftBorderColor;
    private float tableTopBorderSize;
    private int tableTopBorderColor;
    private float tableRightBorderSize;
    private int tableRightBorderColor;
    private float tableBottomBorderSize;
    private int tableBottomBorderColor;
    //单元格左右内边距
    private int tableColumnPadding;
    private int tableTextGravity;
    private int tableTextSize;
    private int tableTextColor;
    private int tableTextColorSelected;
    private int backgroundColorSelected;
    private int firstBackgroundColor;
    private TableAdapter adapter;

    private Paint paint;

    public TableLayout(Context context) {
        super(context);
        init(null);
    }

    public TableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.TableLayout);
            tableMode = typedArray.getInt(R.styleable.TableLayout_tableMode, 0);
            tableRowHeight = typedArray.getDimensionPixelSize(R.styleable.TableLayout_tableRowHeight, (int) Util.dip2px(getResources(), 36));
            tableDividerSize = typedArray.getDimensionPixelSize(R.styleable.TableLayout_tableDividerSize, 1);
            tableDividerColor = typedArray.getColor(R.styleable.TableLayout_tableDividerColor, Color.GRAY);
            tableBorderSize = typedArray.getDimensionPixelSize(R.styleable.TableLayout_tableBorderSize, 1);
            tableBorderColor = typedArray.getColor(R.styleable.TableLayout_tableBorderColor, Color.GRAY);
            tableLeftBorderSize = typedArray.getDimensionPixelSize(R.styleable.TableLayout_tableLeftBorderSize, 0);
            tableLeftBorderColor = typedArray.getColor(R.styleable.TableLayout_tableLeftBorderColor, Color.GRAY);
            tableTopBorderSize = typedArray.getDimensionPixelSize(R.styleable.TableLayout_tableTopBorderSize, 0);
            tableTopBorderColor = typedArray.getColor(R.styleable.TableLayout_tableTopBorderColor, Color.GRAY);
            tableRightBorderSize = typedArray.getDimensionPixelSize(R.styleable.TableLayout_tableRightBorderSize, 0);
            tableRightBorderColor = typedArray.getColor(R.styleable.TableLayout_tableRightBorderColor, Color.GRAY);
            tableBottomBorderSize = typedArray.getDimensionPixelSize(R.styleable.TableLayout_tableBottomBorderSize, 0);
            tableBottomBorderColor = typedArray.getColor(R.styleable.TableLayout_tableBottomBorderColor, Color.GRAY);
            tableColumnPadding = typedArray.getDimensionPixelSize(R.styleable.TableLayout_tableColumnPadding, 0);
            tableTextGravity = typedArray.getInt(R.styleable.TableLayout_tableTextGravity, 0);
            tableTextSize = typedArray.getDimensionPixelSize(R.styleable.TableLayout_tableTextSize, (int) Util.dip2px(getResources(), 12));
            tableTextColor = typedArray.getColor(R.styleable.TableLayout_tableTextColor, Color.GRAY);
            tableTextColorSelected = typedArray.getColor(R.styleable.TableLayout_tableTextColorSelected, Color.BLACK);
            backgroundColorSelected = typedArray.getColor(R.styleable.TableLayout_backgroundColorSelected, Color.TRANSPARENT);
            firstBackgroundColor = typedArray.getColor(R.styleable.TableLayout_firstBackgroundColor, Color.TRANSPARENT);
            typedArray.recycle();
        } else {
            tableMode = 0;
            tableRowHeight = (int) Util.dip2px(getResources(), 36);
            tableDividerSize = 1;
            tableDividerColor = Color.GRAY;
            tableBorderSize = 1;
            tableBorderColor = Color.GRAY;
            tableColumnPadding = 0;
            tableTextGravity = 0;
            tableTextSize = (int) Util.dip2px(getResources(), 12);
            tableTextColor = Color.GRAY;
            tableTextColorSelected = Color.BLACK;
            backgroundColorSelected = Color.TRANSPARENT;
            firstBackgroundColor = Color.TRANSPARENT;
        }
//        setOrientation(HORIZONTAL);
        setWillNotDraw(false);
        paint = new Paint();
        paint.setAntiAlias(true);
        if (isInEditMode()) {
            String[] content = {"a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", "aaaaaaa", "aaaaaaaa"};
            addView(new TableColumn(getContext(), content, this));
            addView(new TableColumn(getContext(), content, this));
            addView(new TableColumn(getContext(), content, this));
            addView(new TableColumn(getContext(), content, this));
            addView(new TableColumn(getContext(), content, this));
            addView(new TableColumn(getContext(), content, this));
            addView(new TableColumn(getContext(), content, this));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 0;
        int height = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //填充方向为 HORIZONTAL 时宽高计算
            if (getOrientation() == HORIZONTAL) {
                width += child.getMeasuredWidth();
                height = Math.max(height, child.getMeasuredHeight());
            } else {
                //填充方向为 VERTICAL 时宽高计算
                width = Math.max(width, child.getMeasuredWidth());
                height += child.getMeasuredHeight();
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        //放在super前是后景，相反是前景，前景会覆盖子布局
        super.dispatchDraw(canvas);
        iniDraw(canvas);
    }

    private void iniDraw(Canvas canvas) {
        paint.setColor(tableDividerColor);
        float drawnWidth = 0;
        int maxRowCount = 0;
        int childCount = getChildCount();
        if (getOrientation() == VERTICAL) {
            for (int i = 0; i < childCount; i++) {
                TableColumn column = (TableColumn) getChildAt(i);
                maxRowCount = Math.max(maxRowCount, column.getChildCount());
                //每行的下边框边框
                float y = i * tableRowHeight;
                canvas.drawRect(0, y - tableDividerSize, getWidth(), y, paint);
            }
            //每列的右边框边框
            for (int i = 1; i < maxRowCount; i++) {
                drawnWidth += (tableColumnPadding * 2 + maxTableColumnWidth);
                canvas.drawRect(drawnWidth - tableDividerSize, 0, drawnWidth, getHeight(), paint);
                //当分割线宽度大于时每列的分割线宽度设为一半和外边框区分开
//                if (tableDividerSize > 1) {
//                    canvas.drawRect(drawnWidth - tableDividerSize / 2, 0, drawnWidth + tableDividerSize / 2, getHeight(), paint);
//                } else {
//                    canvas.drawRect(drawnWidth - tableDividerSize, 0, drawnWidth, getHeight(), paint);
//                }
            }
        } else {
            for (int i = 0; i < childCount; i++) {
                TableColumn column = (TableColumn) getChildAt(i);
                maxRowCount = Math.max(maxRowCount, column.getChildCount());
                //每列的右边框
                if (i > 0) {
                    canvas.drawRect(drawnWidth - tableDividerSize, 0, drawnWidth, getHeight(), paint);
                    //当分割线宽度大于时每列的分割线宽度设为一半和外边框区分开
//                if (tableDividerSize > 1) {
//                    canvas.drawRect(drawnWidth - tableDividerSize / 2, 0, drawnWidth + tableDividerSize / 2, getHeight(), paint);
//                } else {
//                    canvas.drawRect(drawnWidth - tableDividerSize, 0, drawnWidth, getHeight(), paint);
//                }
                }
                drawnWidth += column.getWidth();
            }
            //每行的下边框
            for (int i = 1; i < maxRowCount; i++) {
                float y = i * tableRowHeight;
                canvas.drawRect(0, y - tableDividerSize, getWidth(), y, paint);
                //当分割线宽度大于时每列的分割线宽度设为一半和外边框区分开
//            if (tableDividerSize > 1) {
//                canvas.drawRect(0, y - tableDividerSize / 2, getWidth(), y + tableDividerSize / 2, paint);
//            } else {
//                canvas.drawRect(0, y - tableDividerSize, getWidth(), y, paint);
//            }
            }
        }
        //如果设置了共用边框的大小
        if (tableBorderSize > 0) {
            //开始画外边框
            paint.setColor(tableBorderColor);
            //最左边边框
            canvas.drawRect(0, 0, tableBorderSize, getHeight(), paint);
            //最右边边框
            canvas.drawRect(getWidth() - tableBorderSize, 0, getWidth(), getHeight(), paint);
            //最上边边框
            canvas.drawRect(0, 0, getWidth(), tableBorderSize, paint);
            //最下边边框
            canvas.drawRect(0, getHeight() - tableBorderSize, getWidth(), getHeight(), paint);
        } else {
            //开始画外边框
            //最左边边框
            paint.setColor(tableLeftBorderColor);
            canvas.drawRect(0, 0, tableLeftBorderSize, getHeight(), paint);
            //最右边边框
            paint.setColor(tableRightBorderColor);
            canvas.drawRect(getWidth() - tableRightBorderSize, 0, getWidth(), getHeight(), paint);
            //最上边边框
            paint.setColor(tableTopBorderColor);
            canvas.drawRect(0, 0, getWidth(), tableTopBorderSize, paint);
            //最下边边框
            paint.setColor(tableBottomBorderColor);
            canvas.drawRect(0, getHeight() - tableBottomBorderSize, getWidth(), getHeight(), paint);
        }
    }

    @Override
    public TableLayout getTableLayout() {
        return this;
    }

    public int getTableMode() {
        return tableMode;
    }

    public int getTableRowHeight() {
        return tableRowHeight;
    }

    public float getMaxTableColumnWidth() {
        return maxTableColumnWidth;
    }

    public void setMaxTableColumnWidth(float maxTableColumnWidth) {
        this.maxTableColumnWidth = maxTableColumnWidth;
    }

    public float getTableDividerSize() {
        return tableDividerSize;
    }

    public int getTableDividerColor() {
        return tableDividerColor;
    }

    public int getTableColumnPadding() {
        return tableColumnPadding;
    }

    public int getTableTextGravity() {
        return tableTextGravity;
    }

    public int getTableTextSize() {
        return tableTextSize;
    }

    public int getTableTextColor() {
        return tableTextColor;
    }

    public int getTableTextColorSelected() {
        return tableTextColorSelected;
    }

    public int getBackgroundColorSelected() {
        return backgroundColorSelected;
    }

    public int getFirstBackgroundColor() {
        return firstBackgroundColor;
    }

    public void setAdapter(TableAdapter adapter) {
        this.adapter = adapter;
        useAdapter();
    }

    private void useAdapter() {
        removeAllViews();
        int count = adapter.getColumnCount();
        //当填充方向为 VERTICAL 时需要先计算出最大单元格的宽度
        if (getOrientation() == VERTICAL) {
            for (int i = 0; i < count; i++) {
                ViewGroup view = new TableColumn(getContext(), adapter.getColumnContent(i), this);
                addView(view);
            }
        }
        for (int i = 0; i < count; i++) {
            ViewGroup view = new TableColumn(getContext(), adapter.getColumnContent(i), this);
            if (i == 0) {
                //设置第一列背景色
                view.setBackgroundColor(firstBackgroundColor);
            }
            //设置每行的第一个单元格背景色
            View child = view.getChildAt(0);
            child.setBackgroundColor(firstBackgroundColor);
            addView(view);
        }
    }

    public void onClick(float x, float y) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            TableColumn tableColumn = (TableColumn) getChildAt(i);
            if (tableColumn.getRight() >= x) {
                if (i == 0) {
                    return;
                }
                tableColumn.onClick(y);
                return;
            }
        }
    }
}
