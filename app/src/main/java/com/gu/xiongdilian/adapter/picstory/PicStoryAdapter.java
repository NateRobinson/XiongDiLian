package com.gu.xiongdilian.adapter.picstory;

import android.content.Context;
import android.media.Image;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.gu.baselibrary.baseadapter.BaseViewHolder;
import com.gu.baselibrary.baseadapter.MyBaseAdapter;
import com.gu.baselibrary.utils.DrawableUtils;
import com.gu.baselibrary.view.MTextView;
import com.gu.xiongdilian.R;
import com.gu.xiongdilian.pojo.Account;
import com.gu.xiongdilian.pojo.PicStory;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Nate on 2015/12/9.
 */
public class PicStoryAdapter extends MyBaseAdapter<PicStory> {
    private Context mContext;

    public PicStoryAdapter(Context context, int resource, List<PicStory> list) {
        super(context, resource, list);
        this.mContext = context;
    }

    /**
     * @param viewHolder
     * @param picStory
     * @return void 返回类型
     * @Title: setConvert
     * @Description: 抽象方法，由子类去实现每个itme如何设置
     */
    @Override
    public void setConvert(BaseViewHolder viewHolder, PicStory picStory) {
        ImageView headImg = viewHolder.getView(R.id.pic_author_iv);
        if (TextUtils.isEmpty(picStory.getAuthor().getAvatar())) {
            DrawableUtils.disPlayLocRoundCornerImg(headImg, R.mipmap.default_xiongdilian_headimg);
        } else {
            DrawableUtils.displayRoundCornerImgOnNet(headImg, picStory.getAuthor().getAvatar());
        }
        viewHolder.setTextView(R.id.pic_story_creater_tv, picStory.getAuthor().getUsername());
        viewHolder.setTextView(R.id.pic_story_create_time_tv, picStory.getCreatedAt());
        ImageView picImg = viewHolder.getView(R.id.pic_story_main_activity_item_iv);
        DrawableUtils.displayAutoImgOnNet(picImg, picStory.getPicUrl());
        viewHolder.setTextView(R.id.pic_story_view_num_tv, picStory.getViewNum() + "");
        viewHolder.setTextView(R.id.pic_story_praise_num_tv, picStory.getPraiseNum() + "");
        MTextView mTextView = viewHolder.getView(R.id.pic_story_desc_tv);
        SpannableString ss = new SpannableString(picStory.getPicDesc());
        mTextView.setMText(ss);
        mTextView.invalidate();

//        ImageView iv = viewHolder.getView(R.id.pic_story_praise_iv);
//        if (isPraised) {
//            iv.setBackgroundResource(R.mipmap.pic_story_praised);
//        } else {
//            iv.setBackgroundResource(R.mipmap.pic_story_praise_no);
//        }
//        //请求此图片故事是否赞了
//        queryIfPraised(picStory);
//        viewHolder.getView(R.id.pic_story_praise_ll).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

//    // 添加赞
//    private void addParise(final PicStory picStory, final int pos) {
//        // 将当前用户添加到Post表中的likes字段值中，表明当前用户喜欢该帖子
//        BmobRelation relation = new BmobRelation();
//        // 将当前用户添加到多对多关联中
//        relation.add(account);
//        // 多对多关联指向`post`的`parises`字段
//        picStory.setPraises(relation);
//        picStory.update(mContext, new UpdateListener() {
//            @Override
//            public void onSuccess() {
//                isPraised = true;
//                notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(int arg0, String arg1) {
//
//            }
//        });
//        // 赞数加1
//        picStory.increment("praiseNum"); // 帖子数递增1
//        picStory.update(mContext, new UpdateListener() {
//            @Override
//            public void onSuccess() {
//                if (pos != -1) {
//                    list.get(pos).setPraiseNum(picStory.getPraiseNum() + 1);
//                    notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onFailure(int arg0, String arg1) {
//            }
//        });
//    }

//    // 删除赞
//    private void removeParise(final PicStory picStory, final int pos) {
//        BmobRelation relation = new BmobRelation();
//        relation.remove(account);
//        picStory.setPraises(relation);
//        picStory.update(mContext, new UpdateListener() {
//            @Override
//            public void onSuccess() {
//                isPraised = false;
//                notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(int arg0, String arg1) {
//            }
//        });
//        // 赞数减1
//        picStory.increment("praiseNum", -1); // 帖子数递增1
//        picStory.update(mContext, new UpdateListener() {
//            @Override
//            public void onSuccess() {
//                if (pos != -1) {
//                    list.get(pos).setPraiseNum(picStory.getPraiseNum() - 1);
//                    notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onFailure(int arg0, String arg1) {
//            }
//        });
//    }

//    // 查询该帖子目前用户是否赞了
//    private void queryIfPraised(PicStory picStory) {
//        BmobQuery<PicStory> query = new BmobQuery<>();
//        query.addWhereEqualTo("objectId", picStory.getObjectId());
//        // 查询多对多关联要用到BmobPointer
//        query.addWhereEqualTo("praises", account);
//        query.count(mContext, PicStory.class, new CountListener() {
//            @Override
//            public void onSuccess(int count) {
//                if (count > 0) {
//                    isPraised = true;
//                    notifyDataSetChanged();
//                } else {
//                    isPraised = false;
//                    notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onFailure(int code, String msg) {
//
//            }
//        });
//    }
}
