package com.ycbjie.ycshopcat;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ycbjie.ycshopcat.bean.GoodsInfo;
import com.ycbjie.ycshopcat.bean.StoreInfo;

import java.util.List;
import java.util.Map;


/**
 * 购物车适配器
 */
public class ShopCat2Adapter extends BaseExpandableListAdapter {

    private List<StoreInfo> groups;
    //这个String对应着StoreInfo的Id，也就是店铺的Id
    private Map<String, List<GoodsInfo>> children;
    private Context mContext;
    private CheckInterface checkInterface;
    private ModifyCountInterface modifyCountInterface;
    private GroupEditorListener groupEditorListener;
    /**
     * 组的编辑按钮是否可见，true可见，false不可见
     */
    private boolean flag = true;


    ShopCat2Adapter(List<StoreInfo> groups, Map<String, List<GoodsInfo>> children, Context mContext) {
        this.groups = groups;
        this.children = children;
        this.mContext = mContext;
    }

    /**
     * 获取组的数量
     * @return                          组的数量
     */
    @Override
    public int getGroupCount() {
        return groups.size();
    }

    /**
     * 获取指定组中的子组数
     * @param groupPosition             应为其计数的组的位置
     * @return                          指定组中的子组数
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        String groupId = groups.get(groupPosition).getId();
        return children.get(groupId).size();
    }

    /**
     * 获取与给定组关联的数据
     * @param groupPosition             应为其计数的组的位置
     * @return
     */
    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    /**
     * 获取与给定组中的给定子组关联的数据
     * @param groupPosition             应为其计数的组的位置
     * @param childPosition             子相对于子中其他的位置
     * @return
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<GoodsInfo> child = children.get(groups.get(groupPosition).getId());
        return child.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * 指示在对基础数据的更改中子ID和组ID是否稳定
     * @return
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_shop_cat_group, parent,false);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        if(groupPosition!=groups.size()-1){
            final StoreInfo  group = (StoreInfo) getGroup(groupPosition);
            groupViewHolder.storeName.setText(group.getName());

            groupViewHolder.storeCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    group.setChoosed(((CheckBox) v).isChecked());
                    checkInterface.checkGroup(groupPosition, ((CheckBox) v).isChecked());
                }
            });
            groupViewHolder.storeCheckBox.setChecked(group.isChoosed());


            /**【文字指的是组的按钮的文字】
             * 当我们按下ActionBar的 "编辑"按钮， 应该把所有组的文字显示"编辑",并且设置按钮为不可见
             * 当我们完成编辑后，再把组的编辑按钮设置为可见
             * 不懂，请自己操作淘宝，观察一遍
             */
            if(group.isActionBarEditor()){
                group.setEditor(false);
                groupViewHolder.storeEdit.setVisibility(View.GONE);
                flag = false;
            }else{
                flag = true;
                groupViewHolder.storeEdit.setVisibility(View.VISIBLE);
            }

            /**
             * 思路:当我们按下组的"编辑"按钮后，组处于编辑状态，文字显示"完成"
             * 当我们点击“完成”按钮后，文字显示"编辑“,组处于未编辑状态
             */
            if (group.isEditor()) {
                groupViewHolder.storeEdit.setText("完成");
            } else {
                groupViewHolder.storeEdit.setText("编辑");
            }
            groupViewHolder.storeEdit.setVisibility(View.VISIBLE);
            groupViewHolder.storeEdit.setOnClickListener(new GroupViewClick(group, groupPosition, groupViewHolder.storeEdit));
        }else {
            groupViewHolder.storeName.setText("失效商品");
            groupViewHolder.storeEdit.setVisibility(View.GONE);
        }


        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_shop_cat_product, parent,false);
            childViewHolder = new ChildViewHolder(convertView);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        if(groupPosition!=groups.size()-1){
             /*
         * 根据组的编辑按钮的可见与不可见，去判断是组对下辖的子元素编辑  还是ActionBar对组的下瞎元素的编辑
         * 如果组的编辑按钮可见，那么肯定是组对自己下辖元素的编辑
         * 如果组的编辑按钮不可见，那么肯定是ActionBar对组下辖元素的编辑
         */
            if(flag){
                if (groups.get(groupPosition).isEditor()) {
                    childViewHolder.editGoodsData.setVisibility(View.VISIBLE);
                    childViewHolder.goodsData.setVisibility(View.GONE);
                } else {
                    childViewHolder.goodsData.setVisibility(View.VISIBLE);
                    childViewHolder.editGoodsData.setVisibility(View.GONE);
                }
            }else{
                if(groups.get(groupPosition).isActionBarEditor()){
                    childViewHolder.editGoodsData.setVisibility(View.VISIBLE);
                    childViewHolder.goodsData.setVisibility(View.GONE);
                }else{
                    childViewHolder.goodsData.setVisibility(View.VISIBLE);
                    childViewHolder.editGoodsData.setVisibility(View.GONE);
                }
            }

            final GoodsInfo child = (GoodsInfo) getChild(groupPosition, childPosition);
            if (child != null) {
                childViewHolder.goodsName.setText(child.getDesc());
                childViewHolder.goodsPrice.setText("￥" + child.getPrice() + "");
                childViewHolder.goodsNum.setText(String.valueOf(child.getCount()));
                childViewHolder.goodsImage.setImageResource(R.drawable.bg_lake_min);
                childViewHolder.goods_size.setText("门票:" + child.getColor() + ",类型:" + child.getSize());
                //设置打折前的原价
                SpannableString spannableString = new SpannableString("￥" + child.getPrime_price() + "");
                StrikethroughSpan span = new StrikethroughSpan();
                spannableString.setSpan(span,0,spannableString.length()-1+1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                //避免无限次的append
                if (childViewHolder.goodsPrimePrice.length() > 0) {
                    childViewHolder.goodsPrimePrice.setText("");
                }
                childViewHolder.goodsPrimePrice.setText(spannableString);
                childViewHolder.goodsBuyNum.setText("x" + child.getCount() + "");
                childViewHolder.singleCheckBox.setChecked(child.isChoosed());
                childViewHolder.singleCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        child.setChoosed(((CheckBox) v).isChecked());
                        childViewHolder.singleCheckBox.setChecked(((CheckBox) v).isChecked());
                        checkInterface.checkChild(groupPosition, childPosition, ((CheckBox) v).isChecked());
                    }
                });
                childViewHolder.increaseGoodsNum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modifyCountInterface.doIncrease(groupPosition, childPosition, childViewHolder.goodsNum, childViewHolder.singleCheckBox.isChecked());
                    }
                });
                childViewHolder.reduceGoodsNum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modifyCountInterface.doDecrease(groupPosition, childPosition, childViewHolder.goodsNum, childViewHolder.singleCheckBox.isChecked());
                    }
                });
                childViewHolder.goodsNum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
            childViewHolder.singleCheckBox.setVisibility(View.VISIBLE);
        }else {
            childViewHolder.singleCheckBox.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }



    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    public GroupEditorListener getGroupEditorListener() {
        return groupEditorListener;
    }

    public void setGroupEditorListener(GroupEditorListener groupEditorListener) {
        this.groupEditorListener = groupEditorListener;
    }

    public CheckInterface getCheckInterface() {
        return checkInterface;
    }

    public void setCheckInterface(CheckInterface checkInterface) {
        this.checkInterface = checkInterface;
    }

    public ModifyCountInterface getModifyCountInterface() {
        return modifyCountInterface;
    }

    public void setModifyCountInterface(ModifyCountInterface modifyCountInterface) {
        this.modifyCountInterface = modifyCountInterface;
    }


    static class GroupViewHolder {
        CheckBox storeCheckBox;
        TextView storeName;
        TextView storeEdit;

        GroupViewHolder(View view) {
            storeCheckBox = view.findViewById(R.id.cb_store_checkBox);
            storeName = view.findViewById(R.id.tv_store_name);
            storeEdit = view.findViewById(R.id.btn_store_edit);
        }
    }

    /**
     * 店铺的复选框
     */
    public interface CheckInterface {
        /**
         * 组选框状态改变触发的事件
         *
         * @param groupPosition 组元素的位置
         * @param isChecked     组元素的选中与否
         */
        void checkGroup(int groupPosition, boolean isChecked);

        /**
         * 子选框状态改变触发的事件
         *
         * @param groupPosition 组元素的位置
         * @param childPosition 子元素的位置
         * @param isChecked     子元素的选中与否
         */
        void checkChild(int groupPosition, int childPosition, boolean isChecked);
    }


    /**
     * 改变数量的接口
     */
    public interface ModifyCountInterface {
        /**
         * 增加操作
         *
         * @param groupPosition 组元素的位置
         * @param childPosition 子元素的位置
         * @param showCountView 用于展示变化后数量的View
         * @param isChecked     子元素选中与否
         */
        void doIncrease(int groupPosition, int childPosition, View showCountView, boolean isChecked);

        void doDecrease(int groupPosition, int childPosition, View showCountView, boolean isChecked);

        void doUpdate(int groupPosition, int childPosition, View showCountView, boolean isChecked);

        /**
         * 删除子Item
         *
         * @param groupPosition
         * @param childPosition
         */
        void childDelete(int groupPosition, int childPosition);
    }

    /**
     * 监听编辑状态
     */
    public interface GroupEditorListener {
        void groupEditor(int groupPosition);
    }

    /**
     * 使某个小组处于编辑状态
     */
    private class GroupViewClick implements View.OnClickListener {

        private StoreInfo group;
        private int groupPosition;
        private TextView editor;

        GroupViewClick(StoreInfo group, int groupPosition, TextView editor) {
            this.group = group;
            this.groupPosition = groupPosition;
            this.editor = editor;
        }

        @Override
        public void onClick(View v) {
            if (editor.getId() == v.getId()) {
                groupEditorListener.groupEditor(groupPosition);
                if (group.isEditor()) {
                    group.setEditor(false);
                } else {
                    group.setEditor(true);
                }
                notifyDataSetChanged();
            }
        }
    }


    static class ChildViewHolder {
        CheckBox singleCheckBox;
        ImageView goodsImage;
        TextView goodsName;
        TextView goods_size;
        TextView goodsPrice;
        TextView goodsPrimePrice;
        TextView goodsBuyNum;
        RelativeLayout goodsData;
        TextView reduceGoodsNum;
        TextView goodsNum;
        TextView increaseGoodsNum;
        TextView goodsSize;
        LinearLayout editGoodsData;

        ChildViewHolder(View view) {
            singleCheckBox = view.findViewById(R.id.cb_single_checkBox);
            goodsImage = view.findViewById(R.id.iv_goods_image);
            goodsData = view.findViewById(R.id.rl_goods_data);
            goodsName = view.findViewById(R.id.tv_goods_name);
            goods_size = view.findViewById(R.id.tv_goods_size);
            goodsPrice = view.findViewById(R.id.tv_goods_price);
            goodsPrimePrice = view.findViewById(R.id.tv_goods_prime_price);
            goodsNum = view.findViewById(R.id.tv_goods_Num);
            goodsBuyNum = view.findViewById(R.id.tv_goods_buyNum);
            editGoodsData = view.findViewById(R.id.ll_edit_goods_data);
            reduceGoodsNum = view.findViewById(R.id.tv_reduce_goodsNum);
            increaseGoodsNum = view.findViewById(R.id.tv_increase_goods_Num);
            goodsSize = view.findViewById(R.id.tv_goodsSize);
        }
    }
}
