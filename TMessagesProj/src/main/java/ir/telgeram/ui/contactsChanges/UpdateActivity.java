package ir.telgeram.ui.contactsChanges;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import ir.telgeram.messenger.AndroidUtilities;
import ir.telgeram.messenger.LocaleController;
import ir.telgeram.messenger.MessageObject;
import ir.telgeram.messenger.MessagesController;
import ir.telgeram.messenger.NotificationCenter;
import ir.telgeram.messenger.NotificationCenter.NotificationCenterDelegate;
import ir.telgeram.messenger.R;
import ir.telgeram.messenger.VideoEditedInfo;
import ir.telgeram.tgnet.TLRPC.FileLocation;
import ir.telgeram.tgnet.TLRPC.User;
import ir.telgeram.ui.ActionBar.ActionBar;
import ir.telgeram.ui.ActionBar.ActionBarMenu;
import ir.telgeram.ui.ActionBar.ActionBarMenuItem;
import ir.telgeram.ui.ActionBar.BaseFragment;
import ir.telgeram.ui.ChatActivity;
import ir.telgeram.ui.Components.BackupImageView;
import ir.telgeram.ui.PhotoViewer.PhotoViewerProvider;
import ir.telgeram.ui.PhotoViewer.PlaceProviderObject;

 public class UpdateActivity extends BaseFragment implements NotificationCenterDelegate, PhotoViewerProvider {
    private static final int delete = 2;
    private static final int filter = 3;
    private int currentFilterType;
    private UpdateCursorAdapter cursorAdapter;
    private a dataBaseAccess;
    private TextView emptyTextView;
    private ActionBarMenuItem filterItem;
    private ListView listView;
    private boolean paused;
    private User selectedUser;
    protected BackupImageView selectedUserAvatar;


    public UpdateActivity(Bundle bundle) {
        super(bundle);
        this.currentFilterType = 0;
    }

    private void forceReload() {
        this.cursorAdapter = new UpdateCursorAdapter(getParentActivity(), new a().a(this.currentFilterType, 500));
        this.listView.setAdapter(this.cursorAdapter);
    }

    private void openChatActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", this.selectedUser.id);
        presentFragment(new ChatActivity(bundle), false);
    }

    private void showDeleteHistoryConfirmation() {
        Builder builder = new Builder(getParentActivity());
        builder.setMessage(LocaleController.getString("AreYouSureDeleteChanges", R.string.AreYouSureDeleteChanges));
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UpdateActivity.this.dataBaseAccess.b();
                UpdateActivity.this.forceReload();
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    public boolean cancelButtonPressed() {
        return false;
    }

     @Override
     public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo) {

     }

     public View createView(Context context) {
        this.fragmentView = new FrameLayout(context);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ContactsChanges", R.string.ContactsChanges));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    UpdateActivity.this.finishFragment();
                } else if (i == UpdateActivity.delete) {
                    UpdateActivity.this.showDeleteHistoryConfirmation();
                } else if (i == UpdateActivity.filter) {
                    UpdateActivity.this.showFilterDialog();
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.addItem((int) delete, (int) R.drawable.ic_delete);
        this.filterItem = createMenu.addItem((int) filter, (int) R.drawable.ic_ab_filter);
        this.dataBaseAccess = new a();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setVisibility(View.INVISIBLE);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        ((FrameLayout) this.fragmentView).addView(linearLayout);
        LayoutParams layoutParams = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        layoutParams.gravity = 48;
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        this.emptyTextView = new TextView(context);
        emptyTextView.setTextColor(0xff808080);
        this.emptyTextView.setTextSize(1, 20.0f);
        this.emptyTextView.setGravity(17);
        this.emptyTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTextView.setText(LocaleController.getString("NoContactChanges", R.string.NoContactChanges));
        linearLayout.addView(this.emptyTextView);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.emptyTextView.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        this.emptyTextView.setLayoutParams(layoutParams2);
        View frameLayout = new FrameLayout(context);
        linearLayout.addView(frameLayout);
        layoutParams2 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -1;
        layoutParams2.weight = 0.5f;
        frameLayout.setLayoutParams(layoutParams2);
        this.cursorAdapter = new UpdateCursorAdapter(context, new a().a(this.currentFilterType, 500));
        this.listView = new ListView(context);
        this.listView.setEmptyView(linearLayout);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setFastScrollEnabled(true);
        listView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        this.listView.setCacheColorHint(0);
        this.listView.setScrollingCacheEnabled(false);
        this.listView.setAdapter(this.cursorAdapter);
        ((FrameLayout) this.fragmentView).addView(this.listView);
        layoutParams = (LayoutParams) this.listView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.listView.setLayoutParams(layoutParams);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long j) {
                UpdateActivity.this.selectedUser = MessagesController.getInstance().getUser(Integer.valueOf(UpdateActivity.this.dataBaseAccess.a((Cursor) UpdateActivity.this.cursorAdapter.getItem(i)).getUserId()));
                if (UpdateActivity.this.selectedUser != null) {
                    UpdateActivity.this.selectedUserAvatar = ((UpdateCell) view).getAvatarImageView();
                    UpdateActivity.this.showUserActionsDialog();
                }
            }
        });

        return this.fragmentView;
    }

    public void didReceivedNotification(int i, Object... objArr) {
        if (!this.paused) {
            UpdateNotificationUtil.dismissNotification();
            this.dataBaseAccess.a();
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        }
        forceReload();
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        if (fileLocation == null) {
            return null;
        }
        FileLocation fileLocation2;
        if (!(this.selectedUser == null || this.selectedUser.id == 0)) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(this.selectedUser.id));
            if (!(user == null || user.photo == null || user.photo.photo_big == null)) {
                fileLocation2 = user.photo.photo_big;
                if (fileLocation2 != null || fileLocation2.local_id != fileLocation.local_id || fileLocation2.volume_id != fileLocation.volume_id || fileLocation2.dc_id != fileLocation.dc_id) {
                    return null;
                }
                int[] iArr = new int[delete];
                this.selectedUserAvatar.getLocationInWindow(iArr);
                PlaceProviderObject placeProviderObject = new PlaceProviderObject();
                placeProviderObject.viewX = iArr[0];
                placeProviderObject.viewY = iArr[1] - AndroidUtilities.statusBarHeight;
                placeProviderObject.parentView = this.selectedUserAvatar;
                placeProviderObject.imageReceiver = this.selectedUserAvatar.getImageReceiver();
              //  placeProviderObject.user_id = this.selectedUser.id;
                placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmap();
                placeProviderObject.size = -1;
                placeProviderObject.radius = this.selectedUserAvatar.getImageReceiver().getRoundRadius();
                return placeProviderObject;
            }
        }
        fileLocation2 = null;
        return fileLocation2 != null ? null : null;
    }

    public int getSelectedCount() {
        return 0;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
        return null;
    }

    public boolean isPhotoChecked(int i) {
        return false;
    }

     public boolean onFragmentCreate() {
        super.onFragmentCreate();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public void onPause() {
        super.onPause();
        this.paused = true;
    }

    public void onResume() {
        super.onResume();
        this.paused = false;
        UpdateNotificationUtil.dismissNotification();
        this.dataBaseAccess.a();
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);

    }

    public void sendButtonPressed(int i) {
    }

    public void setPhotoChecked(int i) {
    }

    protected void showFilterDialog() {
        int i = 0;
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(R.string.filter_title);
        CharSequence[] charSequenceArr = new CharSequence[]{getParentActivity().getString(R.string.AllChanges),
                getParentActivity().getString(R.string.change_name),
                getParentActivity().getString(R.string.change_photo),
                getParentActivity().getString(R.string.change_phone)};
        if (this.currentFilterType != 0) {
            i = this.currentFilterType - 1;
        }
        builder.setSingleChoiceItems(charSequenceArr, i, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    UpdateActivity.this.filterItem.setIcon(R.drawable.ic_ab_filter);
                } else {
                    UpdateActivity.this.filterItem.setIcon(R.drawable.ic_ab_filter_selected);
                }
                if (i == 0) {
                    UpdateActivity.this.currentFilterType = 0;
                } else if (i == 1) {
                    UpdateActivity.this.currentFilterType = UpdateActivity.delete;
                } else if (i == UpdateActivity.delete) {
                    UpdateActivity.this.currentFilterType = UpdateActivity.filter;
                } else if (i == UpdateActivity.filter) {
                    UpdateActivity.this.currentFilterType = 4;
                }
                UpdateActivity.this.forceReload();
                dialogInterface.dismiss();
            }
        });
        showDialog(builder.create());

    }

    protected void showUserActionsDialog() {
        UpdateActivity.this.openChatActivity();

       /* if (this.selectedUser.photo == null || this.selectedUser.photo.photo_big == null) {
            openChatActivity();
            return;
        }

        Builder builder = new Builder(getParentActivity());
        builder.setTitle(ContactsController.formatName(this.selectedUser.first_name, this.selectedUser.last_name));
        CharSequence[] charSequenceArr = new CharSequence[delete];
        charSequenceArr[0] = getParentActivity().getString(R.string.send_message_in_telegram);
        charSequenceArr[1] = getParentActivity().getString(R.string.show_user_photos);
        builder.setItems(charSequenceArr, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    UpdateActivity.this.openChatActivity();
                } else if (i == 1) {
                    PhotoViewer.getInstance().setParentActivity(getParentActivity());
                    PhotoViewer.getInstance().openPhoto(selectedUser.photo.photo_big, UpdateActivity.this);
                }
                dialogInterface.dismiss();
            }
        });
        showDialog(builder.create());*/
    }

    public void updatePhotoAtIndex(int i) {
    }

    @Override
    public boolean allowCaption() {
        return false;
    }

    @Override
    public boolean scaleToFill() {
        return false;
    }

    public void willHidePhotoViewer() {
        if (this.selectedUserAvatar != null) {
            this.selectedUserAvatar.getImageReceiver().setVisible(true, true);
        }
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
    }
}
