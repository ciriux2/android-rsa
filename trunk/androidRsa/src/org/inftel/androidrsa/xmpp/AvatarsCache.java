
package org.inftel.androidrsa.xmpp;

import java.util.HashMap;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class AvatarsCache {
    private static final String TAG = "AvatarsCache";
    private static HashMap<String, Bitmap> avatarMap = null;

    public static HashMap<String, Bitmap> getInstance() {
        if (avatarMap == null) {
            return new HashMap<String, Bitmap>();
        }
        else {
            return avatarMap;
        }
    }

    private static Bitmap getMyAvatar() {
        VCard vCard = new VCard();
        try {
            ProviderManager.getInstance().addIQProvider("vCard",
                    "vcard-temp",
                    new VCardProvider());
            vCard.load(Conexion.getInstance());
            if (vCard.getAvatar() != null) {
                Log.d(TAG, "No es NULL");
                byte[] avatarRaw = vCard.getAvatar();
                Bitmap bm = BitmapFactory.decodeByteArray(avatarRaw, 0, avatarRaw.length);
                return bm;
            }
            else {
                Log.d(TAG, "Si es NULL");
                return null;
            }
        } catch (XMPPException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap getAvatar(String jid) {
        VCard vCard = new VCard();
        try {
            ProviderManager.getInstance().addIQProvider("vCard",
                    "vcard-temp",
                    new VCardProvider());
            vCard.load(Conexion.getInstance(), jid);
            if (vCard.getAvatar() != null) {
                Log.d(TAG, "No es NULL");
                byte[] avatarRaw = vCard.getAvatar();
                Bitmap bm = BitmapFactory.decodeByteArray(avatarRaw, 0, avatarRaw.length);
                return bm;
            }
            else {
                Log.d(TAG, "Si es NULL");
                return null;
            }
        } catch (XMPPException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void populateFromRoster(Roster roster) {
        for (RosterEntry entry : roster.getEntries()) {
            avatarMap
                    .put(roster.getPresence(entry.getUser()).getFrom(), getAvatar(entry.getUser()));
        }
    }

    public static void clear() {
        if (avatarMap != null) {
            avatarMap.clear();
        }
        avatarMap = new HashMap<String, Bitmap>();
    }

}
