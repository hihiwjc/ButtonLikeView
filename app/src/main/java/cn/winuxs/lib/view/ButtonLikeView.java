package cn.winuxs.lib.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import cn.winuxs.lib.R;

/**
 * <br/>Author:hihiwjc
 * <br/>Email:hihiwjc@live.com
 * <br/>Date:2017/10/20
 * <br/>Func:
 */

@SuppressWarnings("unused")
public class ButtonLikeView extends View {
    public static final int DEFAULT_TEXT_SIZE_PIXELS = 16;
    public static final int MAX_ALPHA = 255;
    public static final String TAG = "TAG";
    /**
     * 默认动画时长
     */
    public static final int DEFAULT_ANIMATION_DURATION = 400;
    private float mTextSize;
    /**
     * 文字颜色
     */
    private int mTextColor;
    private int mRippleColor;
    private BitmapDrawable mDrawableLiked;
    private BitmapDrawable mDrawableUnlike;
    private BitmapDrawable mDrawableShining;
    private Paint mPaint;
    private Paint mPaintTextCur;
    private Paint mPaintTextNew;
    private Paint mPaintTextPined;

    /**
     * 数值
     */
    private int mValue = 0;
    private boolean isLiked = false;
    private Rect mTextBounds = new Rect();
    private ObjectAnimator mAnimator;
    private int mTextTranslateY;
    /**
     * 文字动画移动的长度 单位px
     */
    private int mTextTranslateBound;
    private long mAnimationDuration;
    private int mFlag;


    public ButtonLikeView(Context context) {
        this(context, null);
    }

    public ButtonLikeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonLikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        /*初始化参数*/
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ButtonLikeView);
        mTextSize = array.getDimensionPixelSize(R.styleable.ButtonLikeView_textSize, DEFAULT_TEXT_SIZE_PIXELS);
        mTextTranslateBound = array.getDimensionPixelSize(R.styleable.ButtonLikeView_textTranslateBound, (int) (mTextSize * 1.5));
        mTextColor = array.getColor(R.styleable.ButtonLikeView_textColor, Color.GRAY);
        mRippleColor = array.getColor(R.styleable.ButtonLikeView_rippleColor, Color.GRAY);
        mDrawableLiked = (BitmapDrawable) array.getDrawable(R.styleable.ButtonLikeView_drawableLiked);
        mDrawableUnlike = (BitmapDrawable) array.getDrawable(R.styleable.ButtonLikeView_drawableUnlike);
        mDrawableShining = (BitmapDrawable) array.getDrawable(R.styleable.ButtonLikeView_drawableShining);
        mAnimationDuration = array.getInt(R.styleable.ButtonLikeView_animationDuration, DEFAULT_ANIMATION_DURATION);
        array.recycle();
        /*初始化Paint*/
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTextCur = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTextNew = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTextPined = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTextCur.setTextSize(mTextSize);
        mPaintTextNew.setTextSize(mTextSize);
        mPaintTextPined.setTextSize(mTextSize);
        mPaintTextCur.setColor(mTextColor);
        mPaintTextNew.setColor(mTextColor);
        mPaintTextPined.setColor(mTextColor);
        /*animator*/
        mAnimator = ObjectAnimator.ofInt(this, "textTranslateY", 0, mTextTranslateBound);
        mAnimator.setDuration(mAnimationDuration);
        mAnimator.addListener(new ObjectAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isLiked = mFlag > 0;
                mFlag = isLiked ? 1 : -1;
                mValue += mFlag;
                mTextTranslateY = 0;
                //Log.e(TAG, "onAnimationEnd: mValue=" + mValue);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAnimator.isRunning()) {
                    return;
                }
                mFlag = isLiked ? -1 : 1;
                //Log.e(TAG, "mTextTranslateBound=" + mTextTranslateBound);
                mAnimator.start();
            }
        });
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mAnimator.isRunning()) {
                    return true;
                }
                ButtonLikeView.this.setValue((int) (Math.random() * 100000 + 1));
                return true;
            }
        });
    }

    /**
     * 设置文字颜色
     *
     * @param color 文字颜色
     */
    public void setTextColor(@ColorInt int color) {
        mTextColor = color;
        mPaint.setColor(mTextColor);
        mPaintTextCur.setColor(mTextColor);
        mPaintTextNew.setColor(mTextColor);
        mPaintTextPined.setColor(mTextColor);
        invalidate();
    }

    /**
     * 设置动画时长
     *
     * @param duration 动画时长
     */
    public void setAnimationDuration(long duration) {
        mAnimationDuration = duration;
        mAnimator.setDuration(duration);
    }

    /**
     * 设置当前值
     *
     * @param value Value
     */
    public void setValue(int value) {
        mValue = value;
        invalidate();
    }

    public void setTextTranslateY(int y) {
        mTextTranslateY = y;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        drawBitmap(canvas, centerX, centerY);
        drawText(canvas, centerX, centerY);
        mPaint.setColor(Color.RED);
        //canvas.drawLine(0, centerY, getWidth(), centerY, mPaint);
    }

    private void drawText(Canvas canvas, int centerX, int centerY) {
        int flag = isLiked ? 1 : -1;
        /*改变文字透明度*/
        int alpha = (int) (((float) mTextTranslateY / mTextTranslateBound) * MAX_ALPHA);
        mPaintTextCur.setAlpha(MAX_ALPHA - alpha);
        mPaintTextNew.setAlpha(alpha);
        int newValue = mValue - flag;
        String textCur = mValue + "";
        String textNew = newValue + "";
        /*长度变化直接为整个文本做动画*/
        if (textCur.length() != textNew.length() || textCur.length() == 1) {
            mPaintTextCur.getTextBounds(textCur, 0, textCur.length(), mTextBounds);
            int leftX = (int) (centerX + (mTextSize / 2));
            mTextTranslateY = mTextTranslateY * flag;
            int baseLineY = (centerY - (mTextBounds.top + mTextBounds.bottom) / 2) + mTextTranslateY;
            drawMovedText(canvas, leftX, baseLineY, textCur, textNew);
            //Log.e("TAG", "drawText: isLiked=" + isLiked + ",mTextTranslateY=" + mTextTranslateY + ",textCur=" + textCur + ",textNew=" + textNew + ",mValue=" + mValue);
            return;
        }
        /*长度不变则只需为变化的文本做动画*/
        int dIndex = findFirstDifferentIndex(textCur, textNew);
        String textPined = textCur.substring(0, dIndex);
        int leftX = (int) (centerX + (mTextSize / 2));
        mPaintTextPined.getTextBounds(textPined, 0, textPined.length(), mTextBounds);
        int baseLineY = (centerY - (mTextBounds.top + mTextBounds.bottom) / 2);
        /*绘制固定的部分文字*/
        drawPinedText(canvas, leftX, baseLineY, textPined);
        mTextTranslateY = mTextTranslateY * flag;
        baseLineY = (centerY - (mTextBounds.top + mTextBounds.bottom) / 2) + mTextTranslateY;
        leftX = (int) (leftX + mPaintTextPined.measureText(textPined));
        /*绘制移动的部分文字*/
        drawMovedText(canvas, leftX, baseLineY, textCur.substring(dIndex, textCur.length()), textNew.substring(dIndex, textCur.length()));
        //Log.e("TAG", "drawText: isLiked=" + isLiked + ",mTextTranslateY=" + mTextTranslateY + ",textPined=" + textPined + ",textCur=" + textCur + ",textNew=" + textNew + ",mValue=" + mValue);
    }

    private void drawPinedText(Canvas canvas, int leftX, int baseLineY, String textPined) {
        canvas.drawText(textPined, leftX, baseLineY, mPaintTextPined);
    }

    private int findFirstDifferentIndex(String text1, String text2) {
        if (text1.length() <= 0 || text1.length() != text2.length()) {
            throw new IllegalArgumentException("two string's length must be same!");
        }
        int index = 0;
        for (int i = 0; i < text1.length(); i++) {
            if (text1.charAt(i) != text2.charAt(i)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void drawMovedText(Canvas canvas, int leftX, int baseLineY, String textCur, String textNew) {
        int flag = isLiked ? 1 : -1;
        mPaintTextCur.getTextBounds(textCur, 0, textCur.length(), mTextBounds);
        mTextTranslateY = mTextTranslateY * flag;
        int baseLineNewY = baseLineY + (-flag * mTextTranslateBound);
        canvas.drawText(textCur, leftX, baseLineY, mPaintTextCur);
        canvas.drawText(textNew, leftX, baseLineNewY, mPaintTextNew);
    }

    private void drawBitmap(Canvas canvas, int centerX, int centerY) {
        Bitmap bitmap = (isLiked ? mDrawableLiked : mDrawableUnlike).getBitmap();
        Bitmap bitmapNew = (isLiked ? mDrawableUnlike : mDrawableLiked).getBitmap();
        Bitmap shiningBitmap = mDrawableShining.getBitmap();
        int bWidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();
        int bNWidth = bitmapNew.getWidth();
        int bNHeight = bitmapNew.getHeight();
        float progress = (float) mTextTranslateY / mTextTranslateBound;
        int alpha = (int) (progress * MAX_ALPHA);
        mPaint.setAlpha(MAX_ALPHA - alpha);
        canvas.drawBitmap(bitmap, centerX - bWidth, centerY - (bHeight / 2), mPaint);
        mPaint.setAlpha(alpha);
        canvas.drawBitmap(bitmapNew, centerX - bNWidth, centerY - (bNHeight / 2), mPaint);
        if (!isLiked) {
            float radius = (bNWidth / 2f) * (progress + 0.5f);
            mPaint.setStrokeWidth(radius / 10f);
            mPaint.setColor(mRippleColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAlpha(alpha);
            canvas.drawBitmap(shiningBitmap, centerX - bNWidth, centerY - bNHeight, mPaint);
            canvas.drawCircle(centerX - (bNWidth / 2), centerY, radius, mPaint);
            //Log.e(TAG, "drawText: isLiked=" + isLiked + ",radius=" + radius);
        }else{
            mPaint.setAlpha(MAX_ALPHA - alpha);
            canvas.drawBitmap(shiningBitmap, centerX - bNWidth, centerY - bNHeight, mPaint);
        }
    }

}
