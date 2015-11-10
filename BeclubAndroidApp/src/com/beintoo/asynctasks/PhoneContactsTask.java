package com.beintoo.asynctasks;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.beintoo.wrappers.ShareContactWrapper;

import java.util.ArrayList;
import java.util.List;

public class PhoneContactsTask extends AsyncTask<Void, Void, List<ShareContactWrapper>> {

    public interface PhoneContactsCallback {
        public void onContacts(List<ShareContactWrapper> contacts);
    }

    private Context mContext;
    private List<ShareContactWrapper> mContacts;
    private PhoneContactsCallback mCallback;

    public PhoneContactsTask(Context context, PhoneContactsCallback callback) {
        mContext = context;
        mContacts = new ArrayList<ShareContactWrapper>();
        mCallback = callback;
    }

    @Override
    protected List<ShareContactWrapper> doInBackground(Void... voids) {
            ContentResolver cr = mContext.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {

                    ShareContactWrapper contact = new ShareContactWrapper();

                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    contact.setName(name);
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        //System.out.println("name : " + name + ", ID : " + id);

                        // get the phone number
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phone = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            //System.out.println("phone" + phone);
                            contact.setContact(phone);
                            contact.setId(id);
                            break;
                        }
                        pCur.close();

                        mContacts.add(contact);

                        // get email and type
                        /*Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (emailCur.moveToNext()) {
                            // This would allow you get several email addresses
                            // if the email addresses were stored in an array
                            String email = emailCur.getString(
                                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                            int type = emailCur.getInt(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                            String customLabel = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL));
                            CharSequence CustomemailType = ContactsContract.CommonDataKinds.Email.getTypeLabel(mContext.getResources(), type, customLabel);

                            //System.out.println("Email " + email + " Email Type : " + emailType);
                        }
                        emailCur.close();

                        // Get note.......
                        String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " +
                                ContactsContract.Data.MIMETYPE + " = ?";
                        String[] noteWhereParams = new String[]{id,
                                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};

                        Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null,
                                noteWhere, noteWhereParams, null);
                        if (noteCur.moveToFirst()) {
                            String note = noteCur.getString(
                                    noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                            System.out.println("Note " + note);
                        }
                        noteCur.close();

                        //Get Postal Address....

                        String addrWhere = ContactsContract.Data.CONTACT_ID
                                + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                        String[] addrWhereParams = new String[]{id,
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                        Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                                null, null, null, null);
                        while(addrCur.moveToNext()) {
                            String poBox = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                            String street = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                            String city = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                            String state = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                            String postalCode = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                            String country = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                            String type = addrCur.getString(
                                    addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

                            // Do something with these....

                        }
                        addrCur.close();

                        // Get Instant Messenger.........
                        String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND "
                                + ContactsContract.Data.MIMETYPE + " = ?";
                        String[] imWhereParams = new String[]{id,
                                ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                        Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
                                null, imWhere, imWhereParams, null);
                        if (imCur.moveToFirst()) {
                            String imName = imCur.getString(
                                    imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                            String imType;
                            imType = imCur.getString(
                                    imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
                        }
                        imCur.close();

                        // Get Organizations.........

                        String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                        String[] orgWhereParams = new String[]{id,
                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                        Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                                null, orgWhere, orgWhereParams, null);
                        if (orgCur.moveToFirst()) {
                            String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                            String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                        }
                        orgCur.close();*/
                    }
                }
            }


        return mContacts;
    }

    @Override
    protected void onPostExecute(List<ShareContactWrapper> contacts) {
        super.onPostExecute(contacts);
        if(mCallback != null)
            mCallback.onContacts(contacts);
    }
}