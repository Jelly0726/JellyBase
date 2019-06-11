package com.base.Contacts.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.base.Contacts.OnItemClickListener;
import com.base.Contacts.model.SortModel;
import com.jelly.jellybase.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactsSortAdapter extends RecyclerView.Adapter<ContactsSortAdapter.ViewHolder> implements SectionIndexer {
	/**
	 * 联系人列表
	 */
	private List<SortModel> mList;
	/**
	 * 选择的联系人列表
	 */
	private List<SortModel> mSelectedList;
	private Context mContext;
	private OnItemClickListener onItemClickListener;
	public ContactsSortAdapter(Context mContext, List<SortModel> list) {
		this.mContext = mContext;
		mSelectedList = new ArrayList<SortModel>();
		if (list == null) {
			this.mList = new ArrayList<SortModel>();
		} else {
			this.mList = list;
		}
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<SortModel> list) {
		if (list == null) {
			this.mList = new ArrayList<SortModel>();
		} else {
			this.mList = list;
		}
		notifyDataSetChanged();
	}
	public Object getItem(int position) {
		return mList.get(position);
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.contacts_item, parent,false));
	}

	@Override
	public void onBindViewHolder(@NonNull final ViewHolder holder,final int position) {
		final SortModel mContent = mList.get(position);
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			holder.tvLetter.setVisibility(View.VISIBLE);
			holder.tvLetter.setText(mContent.sortLetters);
		} else {
			holder.tvLetter.setVisibility(View.GONE);
		}

		holder.tvTitle.setText(this.mList.get(position).name);
		holder.tvNumber.setText(this.mList.get(position).number);
		holder.cbChecked.setChecked(isSelected(mContent));
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onItemClickListener!=null){
					onItemClickListener.onItemClick(v,position);
				}
			}
		});
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return mList.size();
	}

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView tvLetter;
		public TextView tvTitle;
		public TextView tvNumber;
		public CheckBox cbChecked;

		public ViewHolder(View itemView) {
			super(itemView);
			tvTitle = (TextView) itemView.findViewById(R.id.title);
			tvNumber = (TextView) itemView.findViewById(R.id.number);
			tvLetter = (TextView) itemView.findViewById(R.id.catalog);
			cbChecked = (CheckBox) itemView.findViewById(R.id.cbChecked);
		}
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return mList.get(position).sortLetters.charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getItemCount(); i++) {
			String sortStr = mList.get(i).sortLetters;
			char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
	/**
	 * 是否选中状态
	 */
	private boolean isSelected(SortModel model) {
		return mSelectedList.contains(model);
		//return true;
	}
	/**
	 * 设置选中状态
	 */
	public void toggleChecked(int position) {
		if (isSelected(mList.get(position))) {
			removeSelected(position);
		} else {
			setSelected(position);
		}

	}
	/**
	 * 添加联系人到选择的联系人列表
	 */
	private void setSelected(int position) {
		if (!mSelectedList.contains(mList.get(position))) {
			mSelectedList.add(mList.get(position));
		}
	}
	/**
	 * 从选择的联系人列表移除
	 */
	private void removeSelected(int position) {
		if (mSelectedList.contains(mList.get(position))) {
			mSelectedList.remove(mList.get(position));
		}
	}
	/**
	 * 获取选择的联系人列表
	 */
	public List<SortModel> getSelectedList() {
		return mSelectedList;
	}
}