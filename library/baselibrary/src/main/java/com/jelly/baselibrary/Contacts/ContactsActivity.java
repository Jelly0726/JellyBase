package com.jelly.baselibrary.Contacts;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jelly.baselibrary.Contacts.adapter.ContactsSortAdapter;
import com.jelly.baselibrary.Contacts.model.SortModel;
import com.jelly.baselibrary.Contacts.model.SortToken;
import com.jelly.baselibrary.Contacts.utils.CharacterParser;
import com.jelly.baselibrary.Contacts.utils.PinyinComparator;
import com.jelly.baselibrary.Contacts.view.SideBar;
import com.jelly.baselibrary.R;
import com.jelly.baselibrary.R2;
import com.jelly.baselibrary.applicationUtil.AppUtils;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.toast.ToastUtils;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ContactsActivity extends AppCompatActivity implements OnItemClickListener{
	private Unbinder unbinder;
	@BindView(R2.id.left_back)
	LinearLayout left_back;
	@BindView(R2.id.right_text)
	LinearLayout right_text;
	@BindView(R2.id.lv_contacts)
	RecyclerView mRecyclerView;
	@BindView(R2.id.et_search)
	EditText etSearch;
	@BindView(R2.id.ivClearText)
	ImageView ivClearText;
	@BindView(R2.id.sidrbar)
	SideBar sideBar;
	@BindView(R2.id.dialog)
	TextView dialog;

	private List<SortModel> mAllContactsList;
	private ContactsSortAdapter adapter;
	/** 汉字转换成拼音的类 */
	private CharacterParser characterParser;

	/** 根据拼音来排列ListView里面的数据类 */
	private PinyinComparator pinyinComparator;
	protected RecyclerView.LayoutManager mLayoutManager;
	protected RecyclerView.ItemDecoration mItemDecoration;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_activity);
		unbinder=ButterKnife.bind(this);
		init();
	}

	@Override
	protected void onDestroy() {
		if (unbinder!=null)
			unbinder.unbind();
		unbinder=null;
		super.onDestroy();

	}

	private void init() {
		initView();
		initListener();
		loadContacts();
	}
	private void initView() {
		sideBar.setTextView(dialog);
		/** 给ListView设置adapter **/
		characterParser = CharacterParser.getInstance();
		mAllContactsList = new ArrayList<SortModel>();
		pinyinComparator = new PinyinComparator();
		Collections.sort(mAllContactsList, pinyinComparator);// 根据a-z进行排序源数据
		adapter = new ContactsSortAdapter(this, mAllContactsList);
		adapter.setOnItemClickListener(this);
		mLayoutManager = createLayoutManager();
		mItemDecoration = createItemDecoration();
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.addItemDecoration(mItemDecoration);
		mRecyclerView.setAdapter(adapter);
	}
	protected RecyclerView.LayoutManager createLayoutManager() {
		return new LinearLayoutManager(this);
	}

	protected RecyclerView.ItemDecoration createItemDecoration() {
		return new DefaultItemDecoration(ContextCompat.getColor(this, R.color.transparent),
				AppUtils.dipTopx(this,1), AppUtils.dipTopx(this,1));
	}
	private void initListener() {

		/**清除输入字符**/
		ivClearText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				etSearch.setText("");
			}
		});
		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable e) {

				String content = etSearch.getText().toString();
				if ("".equals(content)) {
					ivClearText.setVisibility(View.INVISIBLE);
				} else {
					ivClearText.setVisibility(View.VISIBLE);
				}
				if (content.length() > 0) {
					ArrayList<SortModel> fileterList = (ArrayList<SortModel>) search(content);
					adapter.updateListView(fileterList);
					//mAdapter.updateData(mContacts);
				} else {
					adapter.updateListView(mAllContactsList);
				}
				mRecyclerView.scrollToPosition(0);

			}

		});

		//设置右侧[A-Z]快速导航栏触摸监听
		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				//该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					mRecyclerView.scrollToPosition(position);
				}
			}
		});

	}

	@OnClick({R2.id.left_back,R2.id.right_text})
	public void onClick(View v) {
		if (AntiShake.check(v.getId())) {    //判断是否多次点击
			return;
		}
		switch (v.getId()){
			case R2.id.left_back:
				finish();
				break;
			case R2.id.right_text:
				List list=adapter.getSelectedList();
				ToastUtils.showToast(this,"已选择："+list.size()+"个联系人");
				break;
		}
	}
	private void loadContacts() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ContentResolver resolver = getApplicationContext().getContentResolver();
					Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, new String[] { Phone.DISPLAY_NAME, Phone.NUMBER, "sort_key" }, null, null, "sort_key COLLATE LOCALIZED ASC");
					if (phoneCursor == null || phoneCursor.getCount() == 0) {
						Toast.makeText(getApplicationContext(), "未获得读取联系人权限 或 未获得联系人数据", Toast.LENGTH_SHORT).show();
						return;
					}
					int PHONES_NUMBER_INDEX = phoneCursor.getColumnIndex(Phone.NUMBER);
					int PHONES_DISPLAY_NAME_INDEX = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME);
					int SORT_KEY_INDEX = phoneCursor.getColumnIndex("sort_key");
					if (phoneCursor.getCount() > 0) {
						mAllContactsList = new ArrayList<SortModel>();
						while (phoneCursor.moveToNext()) {
							String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
							if (TextUtils.isEmpty(phoneNumber))
								continue;
							String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
							String sortKey = phoneCursor.getString(SORT_KEY_INDEX);
							//System.out.println(sortKey);
							SortModel sortModel = new SortModel(contactName, phoneNumber, sortKey);
							//优先使用系统sortkey取,取不到再使用工具取
							String sortLetters = getSortLetterBySortKey(sortKey);
							if (sortLetters == null) {
								sortLetters = getSortLetter(contactName);
							}
							sortModel.sortLetters = sortLetters;

							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
								sortModel.sortToken = parseSortKey(sortKey);
							else
								sortModel.sortToken = parseSortKeyLollipop(sortKey);

							mAllContactsList.add(sortModel);
						}
					}
					phoneCursor.close();
					runOnUiThread(new Runnable() {
						public void run() {
							Collections.sort(mAllContactsList, pinyinComparator);
							adapter.updateListView(mAllContactsList);
						}
					});
				} catch (Exception e) {
					Log.e("xbc", e.getLocalizedMessage());
				}
			}
		}).start();
	}

	/**
	 * 名字转拼音,取首字母
	 * @param name
	 * @return
	 */
	private String getSortLetter(String name) {
		String letter = "#";
		if (name == null) {
			return letter;
		}
		//汉字转换成拼音
		String pinyin = characterParser.getSelling(name);
		String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);

		// 正则表达式，判断首字母是否是英文字母
		if (sortString.matches("[A-Z]")) {
			letter = sortString.toUpperCase(Locale.CHINESE);
		}
		return letter;
	}

	/**
	 * 取sort_key的首字母
	 * @param sortKey
	 * @return
	 */
	private String getSortLetterBySortKey(String sortKey) {
		if (sortKey == null || "".equals(sortKey.trim())) {
			return null;
		}
		String letter = "#";
		//汉字转换成拼音
		String sortString = sortKey.trim().substring(0, 1).toUpperCase(Locale.CHINESE);
		// 正则表达式，判断首字母是否是英文字母
		if (sortString.matches("[A-Z]")) {
			letter = sortString.toUpperCase(Locale.CHINESE);
		} else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {// 5.0以上需要判断汉字
			if (sortString.matches("^[\u4E00-\u9FFF]+$"))// 正则表达式，判断是否为汉字
				letter = getSortLetter(sortString.toUpperCase(Locale.CHINESE));
		}
		return letter;
	}

	/**
	 * 模糊查询
	 * @param str
	 * @return
	 */
	private List<SortModel> search(String str) {
		List<SortModel> filterList = new ArrayList<SortModel>();// 过滤后的list
		//if (str.matches("^([0-9]|[/+])*$")) {// 正则表达式 匹配号码
		if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
			String simpleStr = str.replaceAll("\\-|\\s", "");
			for (SortModel contact : mAllContactsList) {
				if (contact.number != null && contact.name != null) {
					if (contact.simpleNumber.contains(simpleStr) || contact.name.contains(str)) {
						if (!filterList.contains(contact)) {
							filterList.add(contact);
						}
					}
				}
			}
		}else {
			for (SortModel contact : mAllContactsList) {
				if (contact.number != null && contact.name != null) {
					//姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
					boolean isNameContains = contact.name.toLowerCase(Locale.CHINESE)
							.contains(str.toLowerCase(Locale.CHINESE));

					boolean isSortKeyContains = contact.sortKey.toLowerCase(Locale.CHINESE).replace(" ", "")
							.contains(str.toLowerCase(Locale.CHINESE));

					boolean isSimpleSpellContains = contact.sortToken.simpleSpell.toLowerCase(Locale.CHINESE)
							.contains(str.toLowerCase(Locale.CHINESE));

					boolean isWholeSpellContains = contact.sortToken.wholeSpell.toLowerCase(Locale.CHINESE)
							.contains(str.toLowerCase(Locale.CHINESE));

					if (isNameContains || isSortKeyContains || isSimpleSpellContains || isWholeSpellContains) {
						if (!filterList.contains(contact)) {
							filterList.add(contact);
						}
					}
				}
			}
		}
		return filterList;
	}

	/** 中文字符串匹配 */
	String chReg = "[\\u4E00-\\u9FA5]+";

	//String chReg="[^\\u4E00-\\u9FA5]";//除中文外的字符匹配
	/**
	 * 解析sort_key,封装简拼,全拼
	 * @param sortKey
	 * @return
	 */
	public SortToken parseSortKey(String sortKey) {
		SortToken token = new SortToken();
		if (sortKey != null && sortKey.length() > 0) {
			//其中包含的中文字符
			String[] enStrs = sortKey.replace(" ", "").split(chReg);
			for (int i = 0, length = enStrs.length; i < length; i++) {
				if (enStrs[i].length() > 0) {
					//拼接简拼
					token.simpleSpell += enStrs[i].charAt(0);
					token.wholeSpell += enStrs[i];
				}
			}
		}
		return token;
	}

	/**
	 * 解析sort_key,封装简拼,全拼。
	 * Android 5.0 以上使用
	 * @param sortKey
	 * @return
	 */
	public SortToken parseSortKeyLollipop(String sortKey) {
		SortToken token = new SortToken();
		if (sortKey != null && sortKey.length() > 0) {
			boolean isChinese = sortKey.matches(chReg);
			// 分割条件：中文不分割，英文以大写和空格分割
			String regularExpression = isChinese ? "" : "(?=[A-Z])|\\s";

			String[] enStrs = sortKey.split(regularExpression);

			for (int i = 0, length = enStrs.length; i < length; i++)
				if (enStrs[i].length() > 0) {
					//拼接简拼
					token.simpleSpell += getSortLetter(String.valueOf(enStrs[i].charAt(0)));
					token.wholeSpell += characterParser.getSelling(enStrs[i]);
				}
		}
		return token;
	}
	@Override
	public void onItemClick(RecyclerView.ViewHolder view, int position) {
		ContactsSortAdapter.ViewHolder viewHolder = (ContactsSortAdapter.ViewHolder) view;
		viewHolder.cbChecked.performClick();
		adapter.toggleChecked(position);
	}
}