package chuang.contact;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wuchuang1992 on 2015/2/4.
 */
public class ContactAdapter extends BaseAdapter {

    private Context mContext;
    private List<Contact> mContact;
    private SectionIndexer mIndexer;

    public ContactAdapter(Context context, List<Contact> contact) {
        mContext = context;
        mContact = contact;
    }

    @Override
    public int getCount() {
        return mContact.size();
    }

    @Override
    public Object getItem(int position) {
        return mContact.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = mContact.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_contact, null);
            viewHolder.sortkeyLayout = (LinearLayout) convertView.findViewById(R.id.sortkey_layout);
            viewHolder.sortkey = (TextView) convertView.findViewById(R.id.tv_sortkey);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(contact.getName());
        int section = mIndexer.getSectionForPosition(position);
        if (position == mIndexer.getPositionForSection(section)) {
            viewHolder.sortkey.setText(contact.getSortKey());
            viewHolder.sortkeyLayout.setVisibility(View.VISIBLE);
        }else {
            viewHolder.sortkeyLayout.setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewHolder {
        private LinearLayout sortkeyLayout;
        private TextView sortkey;
        private TextView name;
    }

    public void setIndexer(SectionIndexer indexer) {
        mIndexer = indexer;
    }
}
