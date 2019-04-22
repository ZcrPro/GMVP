package com.zhihuianxin.xyaxf.commonsdk.app.utils;

import android.os.Environment;

import com.zhihuianxin.xyaxf.commonsdk.app.AppLifecyclesImpl;
import com.zhihuianxin.xyaxf.commonsdk.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * Created by John on 2015/3/24.
 */
public class ESUUID {
	public static final String TAG = "ESUUID";
	private String uuid;

	public String getName() {
		return "uuid";
	}

	public void load() {
		// try to load uuid from sp
		AppLifecyclesImpl.mAxLoginSp.setUUID(uuid);
		// try to load uuid from uuid file
		if(!Utils.isEnabled(uuid)){
			try {
				uuid = Utils.readFile(getUuidFile().getAbsolutePath(), "utf-8");
				AppLifecyclesImpl.mAxLoginSp.setUUID(uuid);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// generate new uuid
		if(!Utils.isEnabled(uuid)){
			resetUUID();
			AppLifecyclesImpl.mAxLoginSp.setUUID(uuid);
		}
	}

	private File getUuidFile(){
		String uuidPath = String.format("xiaozhang/%s.dat", URLEncoder.encode(getName()));
		File uuidFile = new File(Environment.getExternalStorageDirectory(), uuidPath);

		return uuidFile;
	}

	private void resetUUID(){
		this.uuid = UUID.randomUUID().toString();

		try{
			File uuidFile = getUuidFile();

			if(uuidFile.exists()){
				uuidFile.delete();
			}

			uuidFile.createNewFile();

			FileOutputStream fos = new FileOutputStream(uuidFile);
			fos.write(Utils.getUtf8Bytes(uuid));

			fos.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getUUID(){
		if(!Utils.isEnabled(uuid)){
			resetUUID();
			AppLifecyclesImpl.mAxLoginSp.setUUID(uuid);
		}

		return uuid.replace("-", " ");
	}
}
