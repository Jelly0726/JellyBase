package com.jelly.jellybase.richeditor.tools;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jelly.jellybase.R;

import java.util.Arrays;
import java.util.List;

/**
 * @author Rex on 2019/6/20.
 */
public class EditToolAdapter extends BaseAdapter {

    private Context mContext;

    public EditToolAdapter(Context context) {
        mContext = context;
    }

    private List<ChooseDialogData> dataList = Arrays.asList(
            new ChooseDialogData(ChooseDialog.Type.Image.name(), R.drawable.html_edit_insert_img),
            new ChooseDialogData(ChooseDialog.Type.Audio.name(), R.drawable.html_edit_audio),
            new ChooseDialogData(ChooseDialog.Type.Video.name(), R.drawable.html_edit_video),
            new ChooseDialogData(ChooseDialog.Type.File.name(), R.drawable.html_edit_file),
            new ChooseDialogData(ChooseDialog.Type.NewLine.name(), R.drawable.html_edit_new_line),
            new ChooseDialogData(ChooseDialog.Type.TextColor.name(), R.drawable.html_edit_color),
            new ChooseDialogData(ChooseDialog.Type.Heading.name(), R.drawable.html_edit_hhh),
            new ChooseDialogData(ChooseDialog.Type.Blod.name(), R.drawable.html_edit_bold),
            new ChooseDialogData(ChooseDialog.Type.Italic.name(), R.drawable.html_edit_italic),
            new ChooseDialogData(ChooseDialog.Type.Subscript.name(), R.drawable.html_edit_subscript),
            new ChooseDialogData(ChooseDialog.Type.Superscript.name(), R.drawable.html_edit_subscript),
            new ChooseDialogData(ChooseDialog.Type.Strikethrough.name(), R.drawable.html_edit_strikethrough),
            new ChooseDialogData(ChooseDialog.Type.Underline.name(), R.drawable.html_edit_underline),
            new ChooseDialogData(ChooseDialog.Type.JustifyLeft.name(), R.drawable.html_edit_justify_left),
            new ChooseDialogData(ChooseDialog.Type.JustifyCenter.name(), R.drawable.html_edit_justify_center),
            new ChooseDialogData(ChooseDialog.Type.JustifyRight.name(), R.drawable.html_edit_justify_right),
            new ChooseDialogData(ChooseDialog.Type.Blockquote.name(), R.drawable.html_edit_blockquote),
            new ChooseDialogData(ChooseDialog.Type.Undo.name(), R.drawable.html_edit_undo),
            new ChooseDialogData(ChooseDialog.Type.Redo.name(), R.drawable.html_edit_redo),
            new ChooseDialogData(ChooseDialog.Type.Indent.name(), R.drawable.html_edit_indent),
            new ChooseDialogData(ChooseDialog.Type.Outdent.name(), R.drawable.html_edit_outdent),
            new ChooseDialogData(ChooseDialog.Type.InsertLink.name(), R.drawable.html_edit_insert_link),
            new ChooseDialogData(ChooseDialog.Type.Checkbox.name(), R.drawable.html_edit_check_box),
            new ChooseDialogData(ChooseDialog.Type.TextBackgroundColor.name(), R.drawable.html_edit_background_color),
            new ChooseDialogData(ChooseDialog.Type.FontSize.name(), R.drawable.html_edit_font_size),
            new ChooseDialogData(ChooseDialog.Type.UnorderedList.name(), R.drawable.html_edit_unordered_list),
            new ChooseDialogData(ChooseDialog.Type.OrderedList.name(), R.drawable.html_edit_ordered_list)
    );

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public ChooseDialogData getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.html_edit_item_gv_edit, null);
        }
        ImageView imageView = convertView.findViewById(R.id.ivIcon);
        TextView tvDes = convertView.findViewById(R.id.tvDes);
        imageView.setImageResource(dataList.get(position).iconId);
        tvDes.setText(dataList.get(position).des);
        return convertView;
    }
}
