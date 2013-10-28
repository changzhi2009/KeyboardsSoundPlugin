package com.gamestar.pianoperfect.plugin.aidl;
import com.gamestar.pianoperfect.plugin.aidl.IClientCallback;
interface TonePlugin {
	
	/**
	*	Mandatory Implemented Method.
	*	Get the tone type. This is a keyboard or Drum tone.
	*	Must return "keyboard" or "drumkit" in String.
	**/
	String getInstrumentType();
	
	/**
	*	Mandatory Implemented Method.
	*	Return apk package name.
	**/
	String obtainPackageName();
	
	/**
	*	Optional Implemented Method.
	*	Return the program id in General MIDI Spec
	*	For example: 10 -> Music Box
	**/
	int getTone();
	
	/**
	*	Mandatory Implemented Method.
	*	Return Tone's display name
	**/
	String getToneName();
	
	/**
	*	Mandatory Implemented Method.
	*	Return the icon showed in Tone selection menu.
	**/
	Bitmap getMenuItemIcon();
	
	/**
	*	Mandatory Implemented Method.
	*	Return the icon showed in Toolbar.
	**/
	Bitmap getToolbarIcon();
	
	/**
	*	Mandatory Implemented Method.
	*   For Keyboard:
	*		Return keyboard sound index array if "keyboard" type. More detail information please visit the developer website.
	*
	*	For Drum:
	*		This used to define how to play back a drum sound when touch a drum icon on Drum Pad。 The index must be the same as SOUND_NAME_ARRAY.
	*	 	Notice: This array doesn't work on Perfect Piano v5.8.3. It will work on a newer release.
	*
	*	www.revontuletsoft.com/developer.html
	**/
	int[] getKeyboardSoundsIndexArray();
	
	/**
	*	Mandatory Implemented Method. Useless and can skip for Drumkit Sound.
	*	Return the keyboard sound quick choose array. More detail information please visit the developer website.
	*	www.revontuletsoft.com/developer.html
	**/
	int[] getKeyboardQuickArrayForChoosedIndexArray();
	
	/**
	*	Mandatory Implemented Method.
	*	Return the sound src file name array. 
	*   For example "c1.ogg, d1.ogg ..."
	**/
	String[] getToneFileNameArray();
	
	/**
	*	Mandatory Implemented Method.
	*	Inform the plugin to save sound files to sdcard. The directory must be /sdcard/PerfectPiano/plugin/packagename/. 
	*	Notice: The callback function "void pluginInitFinished();" define in IClientCallback.aidl should be called in the end of this function 
	*	after the initialization finished.
	**/
	void initPlugin(String pluginPath);
	
	/**
	*	Mandatory Implemented Method.
	*	Helper method to register the above callback function when initPlugin finished. 
	*	Just copy the code from example: www.revontuletsoft.com/developer.html
	**/
	void registerCallBack(IClientCallback cb);
	
}