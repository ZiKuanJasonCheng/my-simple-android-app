package com.jc.simpleapp.floatingwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jc.simpleapp.constant.MessageConstant;
import com.jc.simpleapp.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;


public class FloatingIcon {
    private static final String TAG="FloatIconService";
    private Context mContext;
    private WindowManager windowManager;
    private LayoutInflater inflater;
    WindowManager.LayoutParams param;
    private RelativeLayout floatingIconView, removeIconView;
    private ImageView imgFloatingIcon, imgRemoveIcon;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();
    private Handler mHandler;
    private boolean isStart;


    public FloatingIcon(Context c, Handler handler){
        isStart = false;
        mHandler=handler;
        mContext=c;
        windowManager = (WindowManager) c.getSystemService(WINDOW_SERVICE);
        inflater = (LayoutInflater)c.getSystemService(LAYOUT_INFLATER_SERVICE);

        //Setup of floatingIconView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            windowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }

        floatingIconView = (RelativeLayout) inflater.inflate(R.layout.floating_icon, null);
        imgFloatingIcon = (ImageView) floatingIconView.findViewById(R.id.imgFloatingIcon);

        //Setup of removeIconView
        removeIconView = (RelativeLayout)inflater.inflate(R.layout.floating_icon_remove, null);
        removeIconView.setVisibility(View.GONE);
        imgRemoveIcon = (ImageView) removeIconView.findViewById(R.id.imgRemoveIcon);

        imgFloatingIcon.setOnTouchListener(new View.OnTouchListener() {
            long time_start = 0, time_end = 0;
            boolean isLongclick = false, inBounded = false;
            int remove_img_width = 0, remove_img_height = 0;

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "Into runnable_longClick");
                    isLongclick = true;
                    removeIconView.setVisibility(View.VISIBLE);
                    chathead_longclick();
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG,"onTouch chatHeadView.getWidth="+ floatingIconView.getWidth());
                Log.d(TAG,"onTouch chatHeadView.getHeight="+ floatingIconView.getHeight());

                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) floatingIconView.getLayoutParams();

                Log.d(TAG,"onTouch chatHeadView.x="+param.x);
                Log.d(TAG,"onTouch chatHeadView.y="+param.y);

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();
                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();
                        handler_longClick.postDelayed(runnable_longClick, 500);

                        remove_img_width = imgRemoveIcon.getLayoutParams().width;
                        remove_img_height = imgRemoveIcon.getLayoutParams().height;

                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        if(isLongclick){
                            int x_bound_left = szWindow.x / 2 - (int)(remove_img_width * 1.5);
                            int x_bound_right = szWindow.x / 2 +  (int)(remove_img_width * 1.5);
                            int y_bound_top = szWindow.y - (int)(remove_img_height * 1.5);

                            if((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top){
                                inBounded = true;

                                int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
                                int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight() ));

                                if(imgRemoveIcon.getLayoutParams().height == remove_img_height){
                                    imgRemoveIcon.getLayoutParams().height = (int) (remove_img_height * 1.5);
                                    imgRemoveIcon.getLayoutParams().width = (int) (remove_img_width * 1.5);

                                    WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeIconView.getLayoutParams();
                                    param_remove.x = x_cord_remove;
                                    param_remove.y = y_cord_remove;

                                    windowManager.updateViewLayout(removeIconView, param_remove);
                                }

                                layoutParams.x = x_cord_remove + (Math.abs(removeIconView.getWidth() - imgFloatingIcon.getWidth())) / 2;
                                layoutParams.y = y_cord_remove + (Math.abs(removeIconView.getHeight() - imgFloatingIcon.getHeight())) / 2 ;

                                windowManager.updateViewLayout(floatingIconView, layoutParams);
                                break;
                            }else{
                                inBounded = false;
                                imgRemoveIcon.getLayoutParams().height = remove_img_height;
                                imgRemoveIcon.getLayoutParams().width = remove_img_width;

                                WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeIconView.getLayoutParams();
                                int x_cord_remove = (szWindow.x - removeIconView.getWidth()) / 2;
                                int y_cord_remove = szWindow.y - (removeIconView.getHeight() + getStatusBarHeight() );

                                param_remove.x = x_cord_remove;
                                param_remove.y = y_cord_remove;

                                windowManager.updateViewLayout(removeIconView, param_remove);
                            }

                        }

                        layoutParams.x = x_cord_Destination;
                        layoutParams.y = y_cord_Destination;

                        windowManager.updateViewLayout(floatingIconView, layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG,"action up chatHeadView.getWidth="+ floatingIconView.getWidth());
                        Log.d(TAG,"action up chatHeadView.getHeight="+ floatingIconView.getHeight());
                        isLongclick = false;
                        removeIconView.setVisibility(View.GONE);
                        imgRemoveIcon.getLayoutParams().height = remove_img_height;
                        imgRemoveIcon.getLayoutParams().width = remove_img_width;
                        handler_longClick.removeCallbacks(runnable_longClick);

                        if(inBounded){

                            Log.d(TAG, "Ready to stop FloatIconService!!!");
                            //Stop FloatIconService to make the icon disappear
                            stop();
                            inBounded = false;
                            break;
                        }


                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        if(Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5){
                            time_end = System.currentTimeMillis();
                            if((time_end - time_start) < 300){
                                onIconClicked();
                            }
                        }

                        y_cord_Destination = y_init_margin + y_diff;

                        int BarHeight =  getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (imgFloatingIcon.getHeight() + BarHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (imgFloatingIcon.getHeight() + BarHeight );
                        }
                        layoutParams.y = y_cord_Destination;

                        inBounded = false;
                        break;
                    default:
                        Log.d(TAG, "floatingIconView.setOnTouchListener  -> event.getAction() : default");
                        break;
                }

                return true;
            }
        });
    }

    public void start(){
        if(!isStart){
            //LayoutParam for public use
            param = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_TOAST,    //Never use TYPE_PHONE or TYPE_SYSTEM_ALERT for apiLevel >= 19
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
            param.gravity = Gravity.TOP | Gravity.LEFT;

            if (Build.VERSION.SDK_INT > 24)  // https://www.jianshu.com/p/c05bcb003cd6
                param.type = WindowManager.LayoutParams.TYPE_PHONE;

            windowManager.addView(removeIconView, param);

            param.x = 0;
            param.y = 100;
            windowManager.addView(floatingIconView, param);
        }
        isStart=true;

    }



    public void stop(){
        if(isStart){
            windowManager.removeView(floatingIconView);
            windowManager.removeView(removeIconView);
        }
        isStart=false;
    }

    private void onIconClicked(){
        Message message = mHandler.obtainMessage();
        message.what = MessageConstant.MESSAGE_TOUCH_FLOATING_ICON;
        mHandler.sendMessage(message);
    }

    private void chathead_longclick(){
        Log.d(TAG, "Into FloatIconService.chathead_longclick() ");

        WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeIconView.getLayoutParams();
        int x_cord_remove = (szWindow.x - removeIconView.getWidth()) / 2;
        int y_cord_remove = szWindow.y - (removeIconView.getHeight() + getStatusBarHeight() );

        param_remove.x = x_cord_remove;
        param_remove.y = y_cord_remove;

        windowManager.updateViewLayout(removeIconView, param_remove);
    }

    private int getStatusBarHeight() {
        int statusBarHeight = (int) Math.ceil(25 * mContext.getApplicationContext().getResources().getDisplayMetrics().density);
        return statusBarHeight;
    }

}
