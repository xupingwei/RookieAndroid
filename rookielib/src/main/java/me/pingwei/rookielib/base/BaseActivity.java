package me.pingwei.rookielib.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import me.pingwei.rookielib.R;
import me.pingwei.rookielib.app.ActivityTask;
import me.pingwei.rookielib.interfaces.IToaster;
import me.pingwei.rookielib.interfaces.IUserState;
import me.pingwei.rookielib.utils.Toaster;


/**
 * Created by xupingwei on 2016/4/14.
 */
public abstract class BaseActivity extends AppCompatActivity implements IToaster, IUserState {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageButton toolbarMenu;

    /**
     * 设置要显示的Layout ID
     *
     * @return
     */
    protected int setLayoutId() {
        return 0;
    }


    /**
     * 设置顶部 Toolbar 的 title
     *
     * @param id
     */
    public void setToolbarTitle(int id) {
        if (null != toolbarTitle) {
            toolbarTitle.setText(getResources().getText(id));
        }
    }

    /**
     * 设置顶部 Toolbar 的 title
     *
     * @param title
     */
    public void setToolbarTitle(String title) {
        if (null != toolbarTitle) {
            toolbarTitle.setText(title);
        }
    }

    /**
     * 设置顶部 Toolbar 的 title
     *
     * @param title
     */
    public void setToolbarTitleSpannable(SpannableString title) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    /**
     * 设置顶部 Toolbar 的 title
     *
     * @param id
     */
    public void setToolbarTitleBackground(int id) {
        if (toolbarTitle != null) {
            toolbarTitle.setBackgroundDrawable(getResources().getDrawable(id));
        }
    }

    /**
     * 设置toolbar背景(导航的Back,左边按钮)
     *
     * @param resId
     */
    public void setNavigationbarBackground(int resId) {
        if (null != toolbar) {
            toolbar.setNavigationIcon(resId);
        }
    }

    /**
     * 获取toolbar(导航的Back,左边按钮)
     *
     * @return
     */
    public Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * 隐藏右边菜单
     */
    public void hideRightMenu() {
        if (toolbarMenu != null) {
            toolbarMenu.setVisibility(View.GONE);
        }
    }

    /**
     * 显示右边菜单
     */
    public void showRightMenu() {
        if (toolbarMenu != null) {
            toolbarMenu.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 设置标题文字颜色
     *
     * @param colorId
     */
    public void setToolbarTitleTextColor(int colorId) {
        toolbarTitle.setTextColor(colorId);
    }

    /**
     * 获取工具菜单对象(导航的右边按钮)
     *
     * @return
     */
    public ImageButton getToolbarRightMenu() {
        return toolbarMenu;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
        ActivityTask.addActivity(this);
        if (setLayoutId() != 0) {
            setContentView(setLayoutId());
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (null != toolbar) {
                toolbar.setTitle("");
                toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
                toolbarMenu = (ImageButton) toolbar.findViewById(R.id.toolbar_menu_right);
                setSupportActionBar(toolbar);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityTask.finishCurrentActivity();
                    }
                });
            }
        }

        initViews();

        loadData();
    }

    /**
     * finish当前Activity
     */
    public void finishCurrentActivity() {
        ActivityTask.finishCurrentActivity();
    }


    /**
     * finish所有Activity¬
     */
    public void finishAllActivity() {
        ActivityTask.finishAllActivity();
    }


    @Override
    public void showToast(String msg) {
        Toaster toaster = new Toaster(this.getApplicationContext(), msg);
        toaster.showLoading();
    }

    /**
     * toast为资源文件
     *
     * @param resId
     */
    public void showToast(int resId) {
        Toaster toaster = new Toaster(this, this.getString(resId));
        toaster.showLoading();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }


    public abstract void initVariables();

    public abstract void initViews();

    public abstract void loadData();

}
