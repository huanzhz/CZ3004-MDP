package com.example.mdpgroup6yr1920sem2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import java.nio.charset.Charset;

public class MapView extends View {

    private static final String TAG = "MazeView";
    private static Cell[][] cells;
    private static final int COLS = 15, ROWS = 20;
    private static final float WALL_THICKNESS = 2;
    private static float cellSize, hMargin, vMargin;
    private static int robotRow = 18, robotCol = 1;
    private static String robotDirection = "Top";
    private Paint wallPaint, robotPaint, directionPaint, goalPaint, gridNumberPaint, unexploredPaint;

    private class Cell {
        float startX, startY, endX, endY;
        Paint paint;

        private Cell(float startX, float startY, float endX, float endY, Paint paint) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.paint = paint;
        }
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        wallPaint = new Paint();
        wallPaint.setColor(Color.parseColor("#64B5F6"));
        wallPaint.setStrokeWidth(WALL_THICKNESS);

        robotPaint = new Paint();
        robotPaint.setColor(Color.parseColor("#1DE9B6"));

        //COLOR FOR ROBOT DIRECTION
        directionPaint = new Paint();
        directionPaint.setColor(Color.parseColor("#FFD600"));

        //COLOR FOR UNEXPLORED PATH
        unexploredPaint = new Paint();
        unexploredPaint.setColor(Color.parseColor("#F5F5F5"));

        gridNumberPaint = new Paint();
        gridNumberPaint.setColor(Color.WHITE);
        gridNumberPaint.setTextSize(18);
        gridNumberPaint.setTypeface(Typeface.DEFAULT);

        goalPaint = new Paint();
        goalPaint.setColor(Color.parseColor("#EF5350"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        alignMap(canvas);
        drawBorder(canvas);
        initCells();
        drawCell(canvas);
        drawGridNumber(canvas);
        initRobot(canvas);
        drawRobotDirection(canvas);
        //initGoal(canvas);

    }

    private void alignMap(Canvas canvas) {
        //Canvas Color
        //canvas.drawColor(Color.WHITE);

        //Canvas Width & Height
        int width = getWidth();
        int height = getHeight();

        //Margin is at least half of the cell size
        //Two scenarios, add + 1 (Margin on both sides) either to COLS or ROWS
        if (width / height < COLS / ROWS) {
            cellSize = width / (COLS + 1);
        } else {
            cellSize = height / (ROWS + 1);
        }

        //Get margin on each side for horizontal and vertical
        hMargin = (width - COLS * cellSize) / 2;
        vMargin = (height - ROWS * cellSize) / 2;

        //set margin in place
        canvas.translate(hMargin, vMargin);
    }

    //Draw out the 15x20 grid
    private void drawBorder(Canvas canvas) {
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                //Draw line for top
                canvas.drawLine(
                        x * cellSize,
                        y * cellSize,
                        (x + 1) * cellSize,
                        y * cellSize, wallPaint
                );
                //Draw line for left
                canvas.drawLine(
                        x * cellSize,
                        y * cellSize,
                        x * cellSize,
                        (y + 1) * cellSize, wallPaint
                );
                //Draw line for bottom
                canvas.drawLine(
                        x * cellSize,
                        (y + 1) * cellSize,
                        (x + 1) * cellSize,
                        (y + 1) * cellSize, wallPaint
                );
                //Draw line for right
                canvas.drawLine(
                        (x + 1) * cellSize,
                        y * cellSize,
                        (x + 1) * cellSize,
                        (y + 1) * cellSize, wallPaint
                );
            }
        }
    }

    //Create a new cell with margin in each grid box
    private void initCells() {
        cells = new Cell[COLS][ROWS];

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                cells[x][y] = new Cell(x * cellSize + (cellSize / 30),
                        y * cellSize + (cellSize / 30),
                        (x + 1) * cellSize - (cellSize / 40),
                        (y + 1) * cellSize - (cellSize / 60), unexploredPaint);
            }
        }

    }

    //Draw each cell in the border box
    private void drawCell(Canvas canvas) {

        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                canvas.drawRect(cells[x][y].startX, cells[x][y].startY, cells[x][y].endX, cells[x][y].endY, cells[x][y].paint);
                //Log.d(TAG, "Start X: " + cells[x][y].startX + " Start Y: " + cells[x][y].startY + " End X: " + cells[x][y].endX + " End Y: " + cells[x][y].endY);
            }
        }
    }

    //Draw grid number
    private void drawGridNumber(Canvas canvas) {

        for (int x = 0; x < 15; x++) {

            if(x >9 && x <15){

                canvas.drawText(Integer.toString(x), cells[x][19].startX + (cellSize / 5), cells[x][19].endY + (cellSize / 1.5f), gridNumberPaint);
            }
            else {
                //GRID NUMBER FOR ROW
                canvas.drawText(Integer.toString(x), cells[x][19].startX + (cellSize / 3), cells[x][19].endY + (cellSize / 1.5f), gridNumberPaint);

            }
        }

        for (int x = 0; x <20; x++) {

            if(x >9 && x <20){

                canvas.drawText(Integer.toString(19 - x), cells[0][x].startX - (cellSize / 1.5f), cells[0][x].endY - (cellSize / 3.5f), gridNumberPaint);
            }
            else {

                canvas.drawText(Integer.toString(19 - x), cells[0][x].startX - (cellSize / 1.2f), cells[0][x].endY - (cellSize / 3.5f), gridNumberPaint);

            }
        }
    }

    private void initRobot(Canvas canvas) {
        canvas.drawRect(cells[robotCol][robotRow].startX, cells[robotCol][robotRow].startY, cells[robotCol][robotRow].endX, cells[robotCol][robotRow].endY, robotPaint);
        //top
        canvas.drawRect(cells[robotCol][robotRow - 1].startX, cells[robotCol][robotRow - 1].startY, cells[robotCol][robotRow - 1].endX, cells[robotCol][robotRow - 1].endY, robotPaint);
        //bottom
        canvas.drawRect(cells[robotCol][robotRow + 1].startX, cells[robotCol][robotRow + 1].startY, cells[robotCol][robotRow + 1].endX, cells[robotCol][robotRow + 1].endY, robotPaint);
        //left
        canvas.drawRect(cells[robotCol - 1][robotRow].startX, cells[robotCol - 1][robotRow].startY, cells[robotCol - 1][robotRow].endX, cells[robotCol - 1][robotRow].endY, robotPaint);
        //right
        canvas.drawRect(cells[robotCol + 1][robotRow].startX, cells[robotCol + 1][robotRow].startY, cells[robotCol + 1][robotRow].endX, cells[robotCol + 1][robotRow].endY, robotPaint);
        //diagonal top left
        canvas.drawRect(cells[robotCol - 1][robotRow - 1].startX, cells[robotCol - 1][robotRow - 1].startY, cells[robotCol - 1][robotRow - 1].endX, cells[robotCol - 1][robotRow - 1].endY, robotPaint);
        //diagonal top right
        canvas.drawRect(cells[robotCol + 1][robotRow - 1].startX, cells[robotCol + 1][robotRow - 1].startY, cells[robotCol + 1][robotRow - 1].endX, cells[robotCol + 1][robotRow - 1].endY, robotPaint);
        //diagonal bottom left
        canvas.drawRect(cells[robotCol - 1][robotRow + 1].startX, cells[robotCol - 1][robotRow + 1].startY, cells[robotCol - 1][robotRow + 1].endX, cells[robotCol - 1][robotRow + 1].endY, robotPaint);
        //diagonal bottom right
        canvas.drawRect(cells[robotCol + 1][robotRow + 1].startX, cells[robotCol + 1][robotRow + 1].startY, cells[robotCol + 1][robotRow + 1].endX, cells[robotCol + 1][robotRow + 1].endY, robotPaint);
    }

    private void drawRobotDirection(Canvas canvas) {
        switch (robotDirection) {
            case "Down":
                canvas.drawRect(cells[robotCol][robotRow + 1].startX,
                        cells[robotCol][robotRow + 1].startY,
                        cells[robotCol][robotRow + 1].endX,
                        cells[robotCol][robotRow + 1].endY, directionPaint);

                break;
            case "Left":
                canvas.drawRect(cells[robotCol - 1][robotRow].startX,
                                cells[robotCol - 1][robotRow].startY,
                                cells[robotCol - 1][robotRow].endX,
                                cells[robotCol - 1][robotRow].endY,
                                directionPaint);
                break;
            case "Right":
                canvas.drawRect(cells[robotCol + 1][robotRow].startX,
                                cells[robotCol + 1][robotRow].startY,
                                cells[robotCol + 1][robotRow].endX,
                                cells[robotCol + 1][robotRow].endY,
                                directionPaint);
                break;
            default:
                canvas.drawRect(cells[robotCol][robotRow - 1].startX,
                        cells[robotCol][robotRow - 1].startY,
                        cells[robotCol][robotRow - 1].endX,
                        cells[robotCol][robotRow - 1].endY,
                        directionPaint);
               }
    }


    //ON TOUCH METHOD
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int coordinates[];
        float x = event.getX();
        float y = event.getY();

        //Log.d(TAG, x + " " + y);
        coordinates = getSelectedRowCol(x, y);

        int selectedCol = coordinates[0];
        int selectedRow = coordinates[1];

        //Check to see within range
        if (selectedRow >= 0 && selectedCol >= 0) {
            //Check to see if the selected box is not at the first or last row/cols
            if ((selectedCol != 0 && selectedCol != 14) && (selectedRow != 0 && selectedRow != 19)) {
                robotCol = selectedCol;
                robotRow = selectedRow;
                //Recycle view
                invalidate();
            }
        }

        Log.d(TAG, "Selected Column and Row" + coordinates[0] + " " + coordinates[1]);
        return super.onTouchEvent(event);
    }

    public int[] getSelectedRowCol(float x, float y) {
        int row = -1, cols = -1;

        //Get selected column index
        for (int i = 0; i < COLS; i++) {
            if (cells[i][0].endX >= (x - hMargin) && cells[i][0].startX <= (x - hMargin)) {
                cols = i;
                break;
            }
        }

        int counter = 0;
        //Get selected row index
        for (int j = ROWS-1; j >= 0; j--) {
            if (cells[0][j].endY >= (y - vMargin) && cells[0][j].startY <= (y - vMargin)) {
                row = ROWS - counter -1;
                break;
            }
            counter++;
        }

        return new int[]{cols, row};
    }
}
