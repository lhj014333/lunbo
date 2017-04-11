package com.yulu.bannerlication;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TextView  title;//显示图片标题
    private List<ImageView> imageViews;//图片
    private List<View> dots;//圆点
    private  ViewPargerAdapter adapter;//适配器
    private int oldPosition;
    private int currentItem;
    //存放图片的ID
    private int [] imageId=new int[]{
        R.drawable.gwx,
            R.drawable.lc,
            R.drawable.ljx,
            R.drawable.tz,
            R.drawable.xll
    };
    //存放标题的数组
    private  String [] titles=new String[]{
            "我是1",
            "我是2",
            "我是3",
            "我是4",
            "我是5"
    };
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager= (ViewPager) findViewById(R.id.vp);
        imageViews=new ArrayList<>();
        for (int i=0;i<imageId.length;i++){
            ImageView imageView=new ImageView(this);
            imageView.setBackgroundResource(imageId[i]);
            imageViews.add(imageView);
        }
        dots=new ArrayList<>();
        dots.add(findViewById(R.id.dot_0));
        dots.add(findViewById(R.id.dot_1));
        dots.add(findViewById(R.id.dot_2));
        dots.add(findViewById(R.id.dot_3));
        dots.add(findViewById(R.id.dot_4));

        title= (TextView) findViewById(R.id.my_title);
        title.setText(titles[0]);

        adapter=new ViewPargerAdapter();
        viewPager.setAdapter(adapter);

        //完成标题和圆点的改变
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                title.setText(titles[position]);
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
                oldPosition=position;
                currentItem=position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //开一个后台线程
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //给线程池添加一个“定时调度任务”
        //延迟initialdDelay时间后开始执行command
        //并且按照period时间周期性重复调用（周期时间包括command运行时间，
        // 如果周期时间比cammand运行时间段，则command运行完毕后，立刻重复运行）
        scheduledExecutorService.scheduleWithFixedDelay(
                new ViewPargerTask(),
                2,
                2,
                TimeUnit.SECONDS
        );

    }
    private class ViewPargerTask implements Runnable{
        @Override
        public void run() {
            //取余来实现轮播
            currentItem=(currentItem+1)%imageId.length;
            handler.sendEmptyMessage(0);

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (scheduledExecutorService!=null){
            scheduledExecutorService.shutdown();
            scheduledExecutorService=null;
        }
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           viewPager.setCurrentItem(currentItem,false);
        }
    };

    private  class ViewPargerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return imageId.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViews.get(position));
            return imageViews.get(position);
        }
        //从ViewGroup中移除view
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
           container.removeView(imageViews.get(position));
        }
    }
}
