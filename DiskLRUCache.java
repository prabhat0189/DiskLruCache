package com.inmobi.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class DiskLRUCache<K, T> {

	private static final String CACHE_DIR = "cache/";
	private final Map<K, T> cache;

	public DiskLRUCache(final int maxEntries) {
		cache = new LinkedHashMap<K, T>(maxEntries, 0.75f, true) {

			private static final long serialVersionUID = -1236481390177598762L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<K, T> eldest) {
				return size() > maxEntries;
			}
		};
		
		File cacheDir = new File(CACHE_DIR);
		if(cacheDir.exists()) {
			cacheDir.delete();
		}
		cacheDir.mkdirs();
	}

	public T get(K key) {
		synchronized (cache) {
			T value = cache.get(key);
			if (value == null) {
				value = getFromDisk(key);
				cache.put(key, value);
			}
			return value;
		}
	}

	public synchronized void put(K key, T value) {
		synchronized (cache) {
			cache.put(key, value);
			// update disk
			updateDisk(key, value);
		}
	}

	/*
	 * load object from disk file name is : key.hashcode() value is object byte
	 * code stored
	 */
	@SuppressWarnings("unchecked")
	private synchronized T getFromDisk(K key) {
		File file = new File(CACHE_DIR + key.hashCode());
		if (!file.exists()) {
			return null;
		}
		T value = null;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(file));

			value = (T) ois.readObject();

		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		} finally {
			if (ois != null)
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return value;
	}

	/*
	 * delete the file if it's exist
	 */
	private synchronized void deleteFile(K key) {
		File file = new File(CACHE_DIR + key.hashCode());
		if (file.exists() && !file.delete()) {
			System.out.println("Exception : failed to delete " + file);
		}
	}

	/*
	 * create new file in cache folder with key name and value's object byte
	 * code as data.
	 */
	private synchronized void updateDisk(K key, T value) {
		File file = new File(CACHE_DIR + key.hashCode());

		if (file.exists()) {
			deleteFile(key);
		}

		ObjectOutputStream oos = null;
		try {
			if (file.createNewFile()) {
				oos = new ObjectOutputStream(new FileOutputStream(file));
				oos.writeObject(value);
			}
		} catch (IOException e) {
			System.out.println("Exception : failed to create file " + file);
			e.printStackTrace();
		} finally {
			if (oos != null)
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
