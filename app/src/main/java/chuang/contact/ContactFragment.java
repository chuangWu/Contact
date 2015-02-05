package chuang.contact;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AlphabetIndexer;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuchuang1992 on 2015/2/4.
 */
public class ContactFragment extends Fragment implements AbsListView.OnScrollListener ,LetterBar.OnLetterTouchListener {
    public static final Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    private Context mContext;
    //UI
    private ListView mListView;
    private LinearLayout mTitleLayout;
    private TextView mTitle;
    private TextView mLetterView;
    private AlphabetIndexer mAlphabetIndexer;
    private  LetterBar mLetterBar;

    //Data
    private List<Contact> mContacts = new ArrayList<Contact>();
    private String mAlphabetStr = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private int lastFirstVisibleItem = -1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_contact, container, false);
        initView(contentView);
        setAdapter();
        return contentView;
    }


    private void initView(View contentView) {
        mListView = (ListView) contentView.findViewById(R.id.lv_contact);
        mTitleLayout = (LinearLayout) contentView.findViewById(R.id.ll_title);
        mTitle = (TextView) contentView.findViewById(R.id.tv_title);
        mLetterBar = (LetterBar) contentView.findViewById(R.id.letter_list);
        mLetterView = (TextView) contentView.findViewById(R.id.letter_text);

        mListView.setOnScrollListener(this);
        mLetterBar.setShowString(getResources().getStringArray(R.array.letters));
        mLetterBar.setOnLetterTouchListener(this);
        mLetterView.setVisibility(View.INVISIBLE);

    }


    public void findData() {
        Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{"display_name", "sort_key"}, null, null,
                "sort_key");

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String sortKey = getSortKey(cursor.getString(1));
            Contact contact = new Contact();
            contact.setName(name);
            contact.setSortKey(sortKey);
            mContacts.add(contact);
        }
        getActivity().startManagingCursor(cursor);
        mAlphabetIndexer = new AlphabetIndexer(cursor, 1, mAlphabetStr);
    }

    private String getSortKey(String sortKey) {
        String key = sortKey.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        }
        return "#";
    }

    private void setAdapter() {
        ContactAdapter contactAdapter = new ContactAdapter(mContext, mContacts);
        mListView.setAdapter(contactAdapter);
        contactAdapter.setIndexer(mAlphabetIndexer);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int section = mAlphabetIndexer.getSectionForPosition(firstVisibleItem);
        int nextSecPosition = mAlphabetIndexer.getPositionForSection(section + 1);
        if (firstVisibleItem != lastFirstVisibleItem) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mTitleLayout.getLayoutParams();
            params.topMargin = 0;
            mTitleLayout.setLayoutParams(params);
            mTitle.setText(String.valueOf(mAlphabetStr.charAt(section)));
        }
        if (nextSecPosition == firstVisibleItem + 1) {
            View childView = view.getChildAt(0);
            if (childView != null) {
                int titleHeight = mTitleLayout.getHeight();
                int bottom = childView.getBottom();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mTitleLayout.getLayoutParams();
                if (bottom < titleHeight) {
                    float pushedDistance = bottom - titleHeight;
                    params.topMargin = (int) pushedDistance;
                    mTitleLayout.setLayoutParams(params);
                } else {
                    if (params.topMargin != 0) {
                        params.topMargin = 0;
                        mTitleLayout.setLayoutParams(params);
                    }
                }
            }
        }
        lastFirstVisibleItem = firstVisibleItem;
    }

    @Override
    public void onLetterTouch(String letter, int position) {
        mLetterView.setText(letter);
        mLetterView.bringToFront();
        mLetterView.setVisibility(View.VISIBLE);
        int listViewPosition = mAlphabetIndexer.getPositionForSection(position-1);
        mListView.setSelection(listViewPosition);
    }

    @Override
    public void onActionUp() {
        mLetterView.setVisibility(View.GONE);
    }
}
