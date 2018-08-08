package com.ycbjie.ycshopcat;

import android.annotation.SuppressLint;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ycbjie.ycshopcat.bean.GoodsInfo;
import com.ycbjie.ycshopcat.bean.ShopBean;
import com.ycbjie.ycshopcat.bean.StoreInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private TextView mTvTitleRight;
    private SwipeRefreshLayout mRefreshLayout;
    private ExpandableListView mListView;
    private CheckBox mCbAllCheckBox;
    private LinearLayout mLlOrderInfo;
    private TextView mTvTotalPrice;
    private TextView mTvGoPay;
    private LinearLayout mLlShareInfo;
    private TextView mTvShareGoods;
    private TextView mTvCollectGoods;
    private TextView mTvDelGoods;


    /**
     * 组元素的列表
     */
    private List<StoreInfo> groups;
    /**
     * 子元素的列表
     */
    private Map<String, List<GoodsInfo>> childs;
    /**
     * false就是编辑，true就是完成
     */
    private boolean flag = false;
    private ShopCat1Adapter adapter;
    private ShopMoreAdapter moreAdapter;
    private List<ShopBean> shopBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initData();
        initListView();
    }

    private void initView() {
        initFindViewById();
        initActionBar();
    }

    private void initFindViewById() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mTvTitleRight = (TextView) findViewById(R.id.tv_title_right);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        mListView = (ExpandableListView) findViewById(R.id.listView);
        mCbAllCheckBox = (CheckBox) findViewById(R.id.cb_all_checkBox);
        mLlOrderInfo = (LinearLayout) findViewById(R.id.ll_order_info);
        mTvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
        mTvGoPay = (TextView) findViewById(R.id.tv_go_pay);
        mLlShareInfo = (LinearLayout) findViewById(R.id.ll_share_info);
        mTvShareGoods = (TextView) findViewById(R.id.tv_share_goods);
        mTvCollectGoods = (TextView) findViewById(R.id.tv_collect_goods);
        mTvDelGoods = (TextView) findViewById(R.id.tv_del_goods);
    }


    private void initActionBar() {
        mToolbarTitle.setText("购物车");
        mTvTitleRight.setText("编辑");
    }

    private void initListener() {
        mTvTitleRight.setOnClickListener(this);
        mCbAllCheckBox.setOnClickListener(this);
    }


    private void initData() {
        groups = new ArrayList<>();
        childs = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            groups.add(new StoreInfo(i + "", "杨充：" + (i + 1) + "号当铺",true));
            List<GoodsInfo> goods = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                int[] img = {R.drawable.bg_autumn_tree_min,
                        R.drawable.bg_kites_min,
                        R.drawable.bg_lake_min,
                        R.drawable.bg_leaves_min,
                        R.drawable.bg_magnolia_trees_min,
                        R.drawable.bg_autumn_tree_min};
                //i-j 就是商品的id， 对应着第几个店铺的第几个商品，1-1 就是第一个店铺的第一个商品
                goods.add(new GoodsInfo(i + "-" + j, "商品",
                        groups.get(i).getName() + "的第" + (j + 1) + "个商品",
                        255.00 + new Random().nextInt(1500),
                        1555 + new Random().nextInt(3000),
                        "第一排",
                        "出头天者",
                        img[j], new Random().nextInt(100)));
            }
            childs.put(groups.get(i).getId(), goods);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_title_right:
                flag = !flag;
                setActionBarEditor();
                setVisibility();
                break;
            case R.id.cb_all_checkBox:
                doCheckAll();
                break;
            default:
                break;
        }
    }

    /**
     * 全选和反选
     * 错误标记：在这里出现过错误
     */
    private void doCheckAll() {
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            group.setChoosed(mCbAllCheckBox.isChecked());
            List<GoodsInfo> child = childs.get(group.getId());
            for (int j = 0; j < child.size(); j++) {
                child.get(j).setChoosed(mCbAllCheckBox.isChecked());
            }
        }
        adapter.notifyDataSetChanged();
        calulate();
    }

    /**
     * ActionBar标题上点编辑的时候，只显示每一个店铺的商品修改界面
     * ActionBar标题上点完成的时候，只显示每一个店铺的商品信息界面
     */
    private void setActionBarEditor() {
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            if (group.isActionBarEditor()) {
                group.setActionBarEditor(false);
            } else {
                group.setActionBarEditor(true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setVisibility() {
        if (flag) {
            mTvTitleRight.setText("完成");
        } else {
            mTvTitleRight.setText("编辑");
        }
    }


    private void initListView() {
        adapter = new ShopCat1Adapter(groups, childs, this);
        //关键步骤1：设置复选框的接口
        adapter.setCheckInterface(new ShopCat1Adapter.CheckInterface() {
            /**
             * @param groupPosition 组元素的位置
             * @param isChecked     组元素的选中与否
             *                      思路:组元素被选中了，那么下辖全部的子元素也被选中
             */
            @Override
            public void checkGroup(int groupPosition, boolean isChecked) {
                checkGroupData(groupPosition, isChecked);
            }

            /**
             * @param groupPosition 组元素的位置
             * @param childPosition 子元素的位置
             * @param isChecked     子元素的选中与否
             */
            @Override
            public void checkChild(int groupPosition, int childPosition, boolean isChecked) {
                checkChildData(groupPosition, childPosition, isChecked);
            }
        });
        //关键步骤2:设置增删减的接口
        adapter.setModifyCountInterface(new ShopCat1Adapter.ModifyCountInterface() {
            @Override
            public void doIncrease(int groupPosition, int childPosition, View showCountView, boolean isChecked) {
                GoodsInfo good = (GoodsInfo) adapter.getChild(groupPosition, childPosition);
                int count = good.getCount();
                count++;
                good.setCount(count);
                ((TextView) showCountView).setText(String.valueOf(count));
                adapter.notifyDataSetChanged();
                calulate();
            }

            @Override
            public void doDecrease(int groupPosition, int childPosition, View showCountView, boolean isChecked) {
                GoodsInfo good = (GoodsInfo) adapter.getChild(groupPosition, childPosition);
                int count = good.getCount();
                if (count == 1) {
                    return;
                }
                count--;
                good.setCount(count);
                ((TextView) showCountView).setText("" + count);
                adapter.notifyDataSetChanged();
                calulate();
            }

            @Override
            public void doUpdate(int groupPosition, int childPosition, View showCountView, boolean isChecked) {
                GoodsInfo good = (GoodsInfo) adapter.getChild(groupPosition, childPosition);
                int count = good.getCount();
                Log.i("进行更新数据，数量", count + "");
                ((TextView) showCountView).setText(String.valueOf(count));
                adapter.notifyDataSetChanged();
                calulate();
            }

            @Override
            public void childDelete(int groupPosition, int childPosition) {

            }
        });
        //关键步骤3:监听组列表的编辑状态
        adapter.setGroupEditorListener(new ShopCat1Adapter.GroupEditorListener() {
            @Override
            public void groupEditor(int groupPosition) {

            }
        });
        //设置属性 GroupIndicator 去掉向下箭头
        mListView.setGroupIndicator(null);
        mListView.setAdapter(adapter);
        mListView.addFooterView(getFootView());
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            //关键步骤4:初始化，将ExpandableListView以展开的方式显示
            mListView.expandGroup(i);
        }
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private View getFootView() {
        View view = View.inflate(this, R.layout.footer_shop_view, null);
        return view;
    }

    private void setFootView(){
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        moreAdapter = new ShopMoreAdapter(this);
        recyclerView.setAdapter(moreAdapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        List<ShopBean> shopBeans = new ArrayList<>();
        for(int a=0 ; a<20 ; a++){
            ShopBean bean = new ShopBean(R.drawable.bg_kites_min,"这个是猜你喜欢的标题");
            shopBeans.add(bean);
        }
        moreAdapter.addAll(shopBeans);
    }

    /**
     * @param groupPosition 组元素的位置
     * @param isChecked     组元素的选中与否
     *                      思路:组元素被选中了，那么下辖全部的子元素也被选中
     */
    private void checkGroupData(int groupPosition, boolean isChecked) {
        StoreInfo group = groups.get(groupPosition);
        List<GoodsInfo> child = childs.get(group.getId());
        for (int i = 0; i < child.size(); i++) {
            child.get(i).setChoosed(isChecked);
        }
        if (isCheckAll()) {
            mCbAllCheckBox.setChecked(true);//全选
        } else {
            mCbAllCheckBox.setChecked(false);//反选
        }
        adapter.notifyDataSetChanged();
        calulate();
    }

    /**
     * @param groupPosition 组元素的位置
     * @param childPosition 子元素的位置
     * @param isChecked     子元素的选中与否
     */
    private void checkChildData(int groupPosition, int childPosition, boolean isChecked) {
        boolean allChildSameState = true; //判断该组下面的所有子元素是否处于同一状态
        StoreInfo group = groups.get(groupPosition);
        List<GoodsInfo> child = childs.get(group.getId());
        for (int i = 0; i < child.size(); i++) {
            //不选全中
            if (child.get(i).isChoosed() != isChecked) {
                allChildSameState = false;
                break;
            }
        }
        if (allChildSameState) {
            group.setChoosed(isChecked);//如果子元素状态相同，那么对应的组元素也设置成这一种的同一状态
        } else {
            group.setChoosed(false);//否则一律视为未选中
        }
        if (isCheckAll()) {
            mCbAllCheckBox.setChecked(true);//全选
        } else {
            mCbAllCheckBox.setChecked(false);//反选
        }
        adapter.notifyDataSetChanged();
        calulate();
    }



    /**
     * @return 判断组元素是否全选
     */
    private boolean isCheckAll() {
        for (StoreInfo group : groups) {
            if (!group.isChoosed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算商品总价格，操作步骤
     * 1.先清空全局计价,计数
     * 2.遍历所有的子元素，只要是被选中的，就进行相关的计算操作
     * 3.给textView填充数据
     */
    private double mTotalPrice = 0.00;
    private int mTotalCount = 0;
    @SuppressLint("SetTextI18n")
    private void calulate() {
        mTotalPrice = 0.00;
        mTotalCount = 0;
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            List<GoodsInfo> child = childs.get(group.getId());
            for (int j = 0; j < child.size(); j++) {
                GoodsInfo good = child.get(j);
                if (good.isChoosed()) {
                    mTotalCount++;
                    mTotalPrice += good.getPrice() * good.getCount();
                }
            }
        }
        mTvTotalPrice.setText("￥" + mTotalPrice + "");
        mTvGoPay.setText("去支付(" + mTotalCount + ")");
        if (mTotalCount == 0) {
            setCartNum();
        } else {
            mToolbarTitle.setText("购物车(" + mTotalCount + ")");
        }
    }

    /**
     * 设置购物车的数量
     */
    @SuppressLint("SetTextI18n")
    private void setCartNum() {
        int count = 0;
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            group.setChoosed(mCbAllCheckBox.isChecked());
            List<GoodsInfo> Childs = childs.get(group.getId());
            for (GoodsInfo childs : Childs) {
                count++;
            }
        }
        //购物车已经清空
        if (count == 0) {
            clearCart();
        } else {
            mToolbarTitle.setText("购物车(" + count + ")");
        }
    }


    private void clearCart() {
        mToolbarTitle.setText("购物车(0)");
        mTvTitleRight.setVisibility(View.GONE);
    }



}
