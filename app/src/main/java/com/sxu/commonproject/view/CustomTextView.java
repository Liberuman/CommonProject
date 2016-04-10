package com.sxu.commonproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/********************************************************************************
 *
 * FileName: CustomTextView.java
 *
 * CopyRright: 2015 all right reserved by zhinanmao
 *
 * Author: Ju Honggang
 *
 * Date: 2015-08-05
 *
 * Description: 自定义TextView（实现文本两端对齐）
 *
 * Version: 2.4
 ********************************************************************************/
public class CustomTextView extends TextView {
	/**
	 * 单行文字高度
	 */
	private float textHeight;
	/**
	 * 控件宽度
	 */
	private int width;
	/**
	 * 分割后的行
	 */
	private List<String> lines = new ArrayList<String>();
	/**
	 * 每一个段落的最后一行
	 */
	private List<Integer> tailLines = new ArrayList<Integer>();
	/**
	 * 每一段落最后一行的对齐方式
	 */
	private Align align = Align.ALIGN_LEFT;
	/**
	 * 最后一行的对齐方式
	 */
	public enum Align {
		ALIGN_LEFT, 	// 左对齐
		ALIGN_CENTER, 	// 居中对齐
		ALIGN_RIGHT 	// 右对齐
	}

	private int lineCount = 0;

	public CustomTextView(Context context) {
		super(context);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		TextPaint paint = getPaint();
		paint.setColor(getCurrentTextColor());
		paint.drawableState = getDrawableState();
		if (getLayout() != null) {
			// 获取字体的大小
			float firstHeight = getTextSize();
			int paddingLeft = getPaddingLeft();
			// 是否垂直居中
			if ((getGravity() & Gravity.CENTER_VERTICAL) == 0) {
				firstHeight = firstHeight + (textHeight - firstHeight) / 2;
			}

			for (int i = 0; i < lineCount; i++) {
				// 每一行内容底部的纵坐标
				float drawY = i * textHeight + firstHeight;
				String line = lines.get(i);
				// 绘画起始x坐标
				float drawSpacingX = paddingLeft;
				// 计算字符间距（gap表示控件宽度和文本内容所占宽度的差，line.length()个字符有line.length()-1个空隙）
				float gap = (width - paint.measureText(line));
				float interval = gap / (line.length() - 1);

				// 绘制最后一行
				if (tailLines.contains(i)) {
					interval = 0;
					if (align == Align.ALIGN_CENTER) {
						drawSpacingX += gap / 2;
					} else if (align == Align.ALIGN_RIGHT) {
						drawSpacingX += gap;
					} else {
						/**
						 * Nothing
						 */
					}

				}
				// 绘制每一行的内容
				for (int j = 0; j < line.length(); j++) {
					float drawX = paint.measureText(line.substring(0, j))
							+ interval * j;
					canvas.drawText(line.substring(j, j + 1), drawX + drawSpacingX,
							drawY, paint);
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 获取显示text的布局
		if (getLayout() != null) {
			// 获取显示的总行数
			String[] items = getText().toString().split("\\n");
			lines.clear();
			tailLines.clear();
			for (String item : items) {
				getLineContent(getPaint(), item);
			}
			lineCount = lines.size();
			// 获取文字的高度
			Paint.FontMetrics fm = getPaint().getFontMetrics();
			textHeight = fm.descent - fm.ascent;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				textHeight = textHeight*getLineSpacingMultiplier()+getLineSpacingExtra();
				if (getLineCount() != 0 && lineCount > getMaxLines()) {
					lineCount = getMaxLines();
				}
			}

			setMeasuredDimension(getMeasuredWidth(), (int)Math.ceil(textHeight*lineCount));
		}
	}

	/********************************************************************************
	 * FunctionName: setAlign
	 *
	 * Description: 设置最后一行的对齐方式
	 *
	 * Parameter: align(要设置的对齐方式)
	 *
	 * Return：void
	 ********************************************************************************/
	public void setAlign(Align align) {
		this.align = align;
		invalidate();
	}

	/********************************************************************************
	 * FunctionName: getLineContent
	 *
	 * Description: 计算每行可显示的文本数（存在的Bug：对于英文，存在单词拆分的问题）
	 *
	 * Parameter: paint(绘制文本的画笔) text(每一段的文本内容)
	 *
	 * Return：void
	 ********************************************************************************/
	private void getLineContent(Paint paint, String text) {
		if (text.length() > 0) {
			int startPosition = 0;
			StringBuffer sb = new StringBuffer("");
			width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
			for (int i = 0; i < text.length(); i++) {
				if (paint.measureText(text.substring(startPosition, i + 1)) > width) {
					startPosition = i;
					lines.add(sb.toString());
					sb = new StringBuffer();
				}
				sb.append(text.charAt(i));
			}
			// 保存最后一行的内容
			if (sb.length() > 0) {
				lines.add(sb.toString());
			}
			// 记录最后一行的行数
			tailLines.add(lines.size() - 1);
		}
	}
}