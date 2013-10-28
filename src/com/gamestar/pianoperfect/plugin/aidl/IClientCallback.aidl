package com.gamestar.pianoperfect.plugin.aidl;

interface IClientCallback {  
	/**
	*	Mandatory Implemented Method.
	*	CallBack function when Tone Plug initialize finished.
	*	Tell the Perfect Piano App to go on loading sound.
	*/
    void pluginInitFinished();  
}