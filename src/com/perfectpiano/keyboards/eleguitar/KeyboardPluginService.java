package com.perfectpiano.keyboards.eleguitar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.TypedValue;

import com.gamestar.pianoperfect.plugin.aidl.IClientCallback;
import com.gamestar.pianoperfect.plugin.aidl.TonePlugin;

/**
 * This is an example for Keyboard sound using.
 *
 */
public class KeyboardPluginService extends Service {
	/**
	 * Select one sound type below and return in getInstrumentType() method.
	 */
	private static final String PLUGIN_TYPE_FOR_KEYBOARD = "keyboard";
	@SuppressWarnings("unused")
	private static final String PLUGIN_TYPE_FOR_DRUM = "drumkit";
	
	/**
	 * Choose PLUGIN_TYPE_FOR_KEYBOARD.
	 */
	private static final String PluginType = PLUGIN_TYPE_FOR_KEYBOARD;
	
	/**
	 * General MIDI Program Change Event for Clean Electric Guitar
	 * 
	 * Please refer General MIDI Spec: http://en.wikipedia.org/wiki/General_MIDI
	 */
	private static final int MIDI_CLEAN_ELECTRIC_GUITAR = 27;
	
	/**
	 * Drawable res id of icon design for Perfect Piano Instrument Switch Menu
	 */
	private static final int MenuIconResID = R.drawable.ic_launcher;
	
	/**
	 * Drawable res id of icon design for Perfect Piano tool bar
	 */
	private static final int ToolBarIconResID = R.drawable.ic_launcher;
	
	/**
	 * String res id of this instrument
	 */
	private static final int InstrumentNameID = R.string.ins_name;
	
	/**
	 * Raw sound file name under asserts. It could be 88 size and one by one mapping to the keys on keyboards.
	 * 
	 * And we also accept part of the keys sound and modulate the sounds in code. Then two array: RawSoundsIndexArray and QuickChooseIndexArray
	 * should return right value. PerfectPiano will handle the modulate.
	 */	
	private final static String[] SOUND_NAME_ARRAY = {
		"1d.ogg","1f.ogg","1a.ogg",
		"2d.ogg","2f.ogg","2a.ogg",
		"3d.ogg","3f.ogg","3a.ogg",
		"4c.ogg","4d.ogg","4f.ogg","4g.ogg","4a.ogg","4b.ogg",
		"5d.ogg","5f.ogg","5a.ogg",
		"6c.ogg","6f.ogg","6am.ogg",
		"7c.ogg","7fm.ogg"
	};
	
	/**
	 * The sound file mapping keys index on Keyboard. From 0 to 87 (Total 88 Keys on a Keyboard)
	 * 
	 * For Example: Sound for C0 which is the first key of the keyboard. The index value is 0.
	 * 				Sound for C8 which is the last key of the keyboard. The index value is 87.
	 * 
	 * Note: This is only use for PLUGIN_TYPE_FOR_KEYBOARD. This is useless when using PLUGIN_TYPE_FOR_DRUM.
	 */
	private static final int RawSoundsIndexArray[] = {
		5,  8,  12,								//"1d.ogg","1f.ogg","1a.ogg",
		17, 20, 24,								//"2d.ogg","2f.ogg","2a.ogg",
		29, 32, 36,								//"3d.ogg","3f.ogg","3a.ogg",
		39, 41, 44, 46, 48, 50,					//"4c.ogg","4d.ogg","4f.ogg","4g.ogg","4a.ogg","4b.ogg",
		53, 56, 60,								//"5d.ogg","5f.ogg","5a.ogg",
		63, 68, 73,								//"6c.ogg","6f.ogg","6am.ogg",
		75, 81									//"7c.ogg","7fm.ogg"
	};
	

	/**
	 * Size is 88. It is a key mapping that each key of the keyboard using which index of audio sounds of SOUND_NAME_ARRAY.
	 * 
	 * For Example: In this code. From A0 ~ D1# (0 ~ 6) all using "1d.ogg". Give the right index value.
	 * 
	 * Note: This is only use for PLUGIN_TYPE_FOR_KEYBOARD. This is useless when using PLUGIN_TYPE_FOR_DRUM.
	 */
	private static final int QuickChooseIndexArray[] = {
		0, 0, 0, 0, 0, 0, 0,					//0  ~ 6		//C0  ~ D1#		//1d.ogg
		1, 1, 1, 1,								//7  ~ 10		//D1# ~ G1 		//1f.ogg
		2, 2, 2, 2, 2,							//11 ~ 15		//G1# ~ C2		//1a.ogg
		3, 3, 3,								//16 ~ 18 		//C2# ~ D2#		//2d.ogg
		4, 4, 4, 4,								//19 ~ 22		//E2  ~ G2 		//2f.ogg
		5, 5, 5, 5,								//23 ~ 26		//G2# ~ B2		//2a.ogg
		6, 6, 6, 6,								//27 ~ 30		//C3  ~ D3#		//3d.ogg
		7, 7, 7, 7, 							//31 ~ 34		//E3  ~ G3		//3f.ogg
		8, 8, 8,								//35 ~ 37		//G3# ~ A3#		//3a.ogg
		9, 9,									//38 ~ 39		//B3  ~ C4		//4c.ogg
		10, 10, 10,								//40 ~ 42		//C4# ~ D4#		//4d.ogg
		11, 11, 11, 							//43 ~ 45		//E4  ~ F4#		//4f.ogg
		12,	12,									//46 ~ 47		//G4  ~ G4#		//4g.ogg
		13, 13,									//48 ~ 49		//A4  ~ A4#		//4a.ogg
		14, 14, 								//50 ~ 51		//C4  ~ C5		//4b.ogg
		15, 15, 15,								//52 ~ 54		//C5# ~ D5#		//5d.ogg
		16, 16, 16, 16,							//55 ~ 58		//E5  ~ G5		//5f.ogg
		17, 17, 17,								//59 ~ 61		//G5# ~ A5#		//5a.ogg
		18, 18, 18, 18, 18,						//62 ~ 66		//B5  ~ D6#		//6c.ogg
		19, 19, 19, 19,							//67 ~ 70		//E6  ~ G6		//6f.ogg
		20, 20, 20, 20,					 		//71 ~ 74		//G6# ~ B6		//6am.ogg
		21, 21, 21, 21,	21,						//75 ~ 79		//C7  ~ E7		//7c.ogg
		22, 22, 22, 22, 22, 22, 22, 22			//80 ~ 87		//F7  ~ C8 		//7fm.ogg
	};
	 
	private IBinder mBinder = new PluginServiceBinder();

	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	private static final int MAX_FILE_SIZE = 128;
	
	/**
	 * Save raw sound file from Asserts to the given directory in External Storage
	 * 
	 * @param dir: value pass from Perfect Piano Plugin Framework.
	 */
	private void savePluginSounds(String dir) {
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}

		AssetManager mngr = getAssets();
		int length = SOUND_NAME_ARRAY.length;
		for(int i=0; i<length; i++) {
			String name = SOUND_NAME_ARRAY[i];
			try {
				File file = new File(dir + File.separator + name);
				if (!file.exists()) {
					InputStream path = mngr.open(name);
					BufferedInputStream bis = new BufferedInputStream(path,
							1024 * MAX_FILE_SIZE);
					ByteArrayBuffer baf = new ByteArrayBuffer(
							1024 * MAX_FILE_SIZE);
					// get the bytes one by one
					int current = 0;

					while ((current = bis.read()) != -1) {

						baf.append((byte) current);
					}
					byte[] data = baf.toByteArray();
					FileOutputStream fos;
					fos = new FileOutputStream(file);
					fos.write(data);
					fos.flush();
					fos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private Bitmap getPluginMenuItemIcon() {
		Resources res = this.getResources();
		TypedValue value = new TypedValue();
		res.openRawResource(MenuIconResID, value);
	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inTargetDensity = value.density;
	    return BitmapFactory.decodeResource(res, MenuIconResID, opts);
	}
	
	private Bitmap getPluginToolbarIcon() {
		Resources res = this.getResources();
		TypedValue value = new TypedValue();
		res.openRawResource(ToolBarIconResID, value);
	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inTargetDensity = value.density;
	    return BitmapFactory.decodeResource(res, ToolBarIconResID, opts);
	}
	
	/**
	 * Please refer the comments in aidl file.
	 * 
	 * @author Administrator
	 *
	 */
	private final class PluginServiceBinder extends TonePlugin.Stub{

		@Override
		public String getInstrumentType() throws RemoteException {
			// TODO Auto-generated method stub
			return PluginType;
		}

		@Override
		public String getToneName() throws RemoteException {
			// TODO Auto-generated method stub
			return getString(InstrumentNameID);
		}
		
		@Override
		public Bitmap getMenuItemIcon() throws RemoteException {
			// TODO Auto-generated method stub
			return getPluginMenuItemIcon();
		}
		
		@Override
		public Bitmap getToolbarIcon() throws RemoteException {
			// TODO Auto-generated method stub
			return getPluginToolbarIcon();
		}

		@Override
		public String[] getToneFileNameArray() throws RemoteException {
			// TODO Auto-generated method stub
			return SOUND_NAME_ARRAY;
		}

		@Override
		public void initPlugin(String pluginPath) throws RemoteException {
			savePluginSounds(pluginPath);
			initFinished();//ahking
		}


		@Override
		public int[] getKeyboardSoundsIndexArray() throws RemoteException {
			// TODO Auto-generated method stub
			//only for keyboard
			return RawSoundsIndexArray;
		}

		@Override
		public int[] getKeyboardQuickArrayForChoosedIndexArray()
				throws RemoteException {
			// TODO Auto-generated method stub
			//only for keyboard
			return QuickChooseIndexArray;
		}
 
	    
	    public void initFinished() {
	        try {
	            mCallbacks.beginBroadcast();
	            // now for time being we will consider only one activity is bound to the service, so hardcode 0
	            mCallbacks.getBroadcastItem(0).pluginInitFinished();
	            mCallbacks.finishBroadcast();
	        } catch (RemoteException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    final RemoteCallbackList<IClientCallback> mCallbacks = new RemoteCallbackList<IClientCallback>();
	    
		@Override
		public void registerCallBack(IClientCallback cb) throws RemoteException {
			// TODO Auto-generated method stub
	        if(cb!=null){
	            mCallbacks.register(cb);
	        }
		}

		@Override
		public int getTone() throws RemoteException {
			return MIDI_CLEAN_ELECTRIC_GUITAR;
		}

		@Override
		public String obtainPackageName() throws RemoteException {
			return getPackageName();
		} 
	}
}
