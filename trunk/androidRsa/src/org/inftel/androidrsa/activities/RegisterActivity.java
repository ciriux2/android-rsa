
package org.inftel.androidrsa.activities;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.security.cert.CertificateException;

import org.inftel.androidrsa.R;
import org.inftel.androidrsa.rsa.KeyStore;
import org.inftel.androidrsa.rsa.RSA;
import org.inftel.androidrsa.utils.AndroidRsaConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class RegisterActivity extends Activity {

    private static final String TAG = "RegisterActivity";
    private static final int ACTIVITY_SELECT_IMAGE = 100;

    private String[] mFileList;
    private File mChosenFile;
    private String mChosenFileString;
    private Bitmap mChosenImage;
    private String mChosenImagePath;

    private static final int DIALOG_LOAD_FILE = 1000;

    private static final int DIALOG_NOT_CHOSEN = 1002;
    private static final int DIALOG_KEY_NOT_FOUND = 1003;
    private static final int DIALOG_INVALID_CERTIFICATE = 1004;
    private static final int DIALOG_INVALID_KEY = 1005;
    private static final int DIALOG_INVALID_SIGN_CERTIFICATE = 1006;
    private static final int DIALOG_IMAGE_TOO_LARGE = 1007;
    private static final int DIALOG_IMAGE_SIZE_FAIL = 1008;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);
    }

    public void onClickPickImage(View view) throws IOException {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);

    }

    // Find *.crt in the sd
    private void loadFileList() {
        File mPath = AndroidRsaConstants.EXTERNAL_SD_PATH;
        if (mPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    return filename.contains(AndroidRsaConstants.FTYPE);
                }
            };
            mFileList = mPath.list(filter);
        }
        else {
            mFileList = new String[0];
        }
    }

    // Dialogs
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new Builder(this);

        switch (id) {
            case DIALOG_LOAD_FILE:
                if (mFileList == null) {
                    builder.setMessage(R.string.not_found)
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                } else {
                    builder.setTitle(R.string.choose_file);
                    builder.setSingleChoiceItems(mFileList, -1,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mChosenFileString = mFileList[which];
                                }
                            }).setPositiveButton(getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mChosenFile = new File(AndroidRsaConstants.EXTERNAL_SD_PATH
                                            + File.separator + mChosenFileString);

                                    dialog.dismiss();
                                }
                            });
                }
                break;
            case DIALOG_NOT_CHOSEN:
                builder.setMessage(R.string.not_chosen)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                break;
            case DIALOG_KEY_NOT_FOUND:
                builder.setMessage(R.string.key_not_found)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                break;

            case DIALOG_INVALID_CERTIFICATE:
                builder.setMessage(R.string.invalid_certificate)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                break;
            case DIALOG_INVALID_KEY:
                builder.setMessage(R.string.invalid_key)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                break;
            case DIALOG_INVALID_SIGN_CERTIFICATE:
                builder.setMessage(R.string.invalid_sign_certificate)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                break;
            case DIALOG_IMAGE_TOO_LARGE:
                builder.setMessage(R.string.image_too_large)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                break;
            case DIALOG_IMAGE_SIZE_FAIL:
                builder.setMessage(R.string.image_size_fail)
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                break;
        }
        dialog = builder.show();
        return dialog;
    }

    public void onClickButtonPickCertificate(View view) throws IOException {
        loadFileList();
        showDialog(DIALOG_LOAD_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case ACTIVITY_SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {
                            MediaStore.Images.Media.DATA
                    };

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null,
                            null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    // file path of selected image
                    mChosenImagePath = cursor.getString(columnIndex);
                    cursor.close();
                    // Convert file path into bitmap image using below line.
                    mChosenImage = BitmapFactory.decodeFile(mChosenImagePath);

                    // put bitmapimage in imageview
                    ImageView img = (ImageView) findViewById(R.id.image);
                    img.setImageBitmap(mChosenImage);

                    File file = new File(mChosenImagePath);
                    Log.d(TAG, String.valueOf(file.length()));
                    if (file.length() > 500000) {
                        showDialog(DIALOG_IMAGE_TOO_LARGE);
                    } else {
                        if (mChosenImage.getHeight() != mChosenImage.getHeight()) {
                            showDialog(DIALOG_IMAGE_SIZE_FAIL);
                        }
                    }
                }
        }
    }

    public void onClickButtonDone(View view) throws IOException {
        // TODO comprobar tama�o de la foto

        if (mChosenFile != null && mChosenImage != null) {
            String mChosenFileStringWithoutExt = mChosenFileString.substring(0,
                    mChosenFileString.indexOf("."));
            String mKeyPath = AndroidRsaConstants.EXTERNAL_SD_PATH
                    + File.separator + AndroidRsaConstants.KEY_NAME + mChosenFileStringWithoutExt
                    + ".pem";
            File mKey = new File(mKeyPath);
            Log.d(TAG, mKeyPath);

            if (mKey.exists()) {

                String mChosenFilePath = AndroidRsaConstants.EXTERNAL_SD_PATH
                        + File.separator + mChosenFileString;

                // Stores my own certificate, my own private key and the public
                // key of the CA

                try {

                    KeyStore.getInstance().setCertificate(AndroidRsaConstants.OWN_ALIAS,
                            RSA.getCertificate(mChosenFilePath));
                    // Getting the passphrase to encrypt the private Key
                    SharedPreferences prefs = getSharedPreferences(
                            AndroidRsaConstants.SHARED_PREFERENCE_FILE,
                            Context.MODE_PRIVATE);
                    String passphrase = prefs.getString(AndroidRsaConstants.USERID,
                            "thisisapassphrasedefault");

                    // KeyStore.getInstance().setPk(RSA.getPrivateKey(mKey));

                    // storing the private key
                    KeyStore.getInstance().setPk(
                            RSA.getPrivateKeyEncrytedBytes(mKey, passphrase));

                    KeyStore.getInstance().setPb(RSA.getCAPublicKey(getApplicationContext()));

                    KeyStore.getInstance().getCertificate(AndroidRsaConstants.OWN_ALIAS)
                            .verify(KeyStore.getInstance().getPb());

                    // Applying steganography
                    Intent i = new Intent(getApplicationContext(), EncodeActivity.class);
                    i.putExtra(AndroidRsaConstants.FILE_PATH, mChosenFilePath);
                    i.putExtra(AndroidRsaConstants.IMAGE_PATH, mChosenImagePath);
                    startActivity(i);

                } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (CertificateException e) {
                    showDialog(DIALOG_INVALID_CERTIFICATE);
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    showDialog(DIALOG_INVALID_KEY);
                    e.printStackTrace();
                } catch (NoSuchProviderException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SignatureException e) {
                    showDialog(DIALOG_INVALID_SIGN_CERTIFICATE);
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ShortBufferException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else {
                showDialog(DIALOG_KEY_NOT_FOUND);
            }

        } else {
            showDialog(DIALOG_NOT_CHOSEN);
        }

    }
}