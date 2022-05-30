package ca.unbsj.cbakerlab.mutation_impact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

class CacheAlignment {
	private static final Logger log = Logger.getLogger(CacheAlignment.class);

	static Map<String, Set<String>> cache;
		
	static void loadCache(String filename){
		// if cache already initialized and loaded - return
		if(cache != null){
			return;
		}
		
		// if cache is not initialized - initialize it and continue with loading
		if(cache == null){
			cache = new HashMap<String, Set<String>>();
			
			// if file cache file does not exist - return (chache is initialized!!!)
			File cacheFile = new File(filename);
			if(!cacheFile.exists()){
				return;
			}
			//if(new File(filename).exists()){
			//	return;
			//}
			FileInputStream fis = null;
			ObjectInputStream in = null;
			try {
				fis = new FileInputStream(filename);
				in = new ObjectInputStream(fis);
				cache = (Map<String, Set<String>>) in.readObject();
				in.close();
				fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
			log.info("CacheAlignment - Cache Size: " + cache.size());
			log.info("");
			
			
		}
				
	}
	
	static void makeCachePersistent(String filename){
		if(cache == null){
			log.warn("CacheAlignment - WARN: Cache is not initialized");
			return;
		}else if(cache.size()==0){
			log.warn("CacheAlignment - WARN: Cache size is 0");
			return;
		}
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(cache);
			out.close();
			fos.close();
			log.info("CacheAlignment - Cache Persisted");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
}
