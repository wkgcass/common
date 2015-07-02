package cass.pure.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cass.toolbox.util.LocalClassRetriever;

public class LocalCLManager implements ClassLoaderManager {

	private LocalClassRetriever retriever;
	private Map<String, CL> map = new HashMap<String, CL>();

	public LocalCLManager(LocalClassRetriever retriever) {
		this.retriever = retriever;
	}

	@Override
	public Class<?> load(String className) throws ClassNotFoundException {
		CL loader = map.get(className);
		if (null == loader) {
			synchronized (map) {
				loader = map.get(className);
				if (null == loader) {
					String path = retriever.getPath(className);
					if (null == path)
						throw new ClassNotFoundException(className);
					try {
						loader = new CL(this, className, path);
					} catch (Exception e) {
						throw new ClassNotFoundException(className, e);
					}
					map.put(className, loader);
				}
			}
		}
		return loader.loadClass(className);
	}

	@Override
	public Class<?> update(String className) throws ClassNotFoundException {
		String path = retriever.getPath(className);
		if (null == path)
			throw new ClassNotFoundException(className);
		CL loader;
		try {
			loader = new CL(this, className, path);
		} catch (IOException e) {
			throw new ClassNotFoundException(className, e);
		}
		synchronized (map) {
			map.put(className, loader);
		}
		return loader.loadClass(className);
	}

	class CL extends ClassLoader {
		private final String name;
		private final Class<?> cls;
		private final ClassLoaderManager manager;

		public CL(ClassLoaderManager manager, String name, String path)
				throws IOException {
			super(null);
			this.manager = manager;
			this.name = name;
			this.cls = load(name, path);
			this.resolveClass(cls);
		}

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			return loadClass(name, false);
		}

		@Override
		protected Class<?> loadClass(String name, boolean resolve)
				throws ClassNotFoundException {
			Class<?> c = null;
			if (name.equals(this.name)) {
				c = this.cls;
			} else {
				c = manager.load(name);
			}
			if (resolve) {
				this.resolveClass(c);
			}
			return c;
		}

		private Class<?> load(String name, String path) throws IOException {
			byte[] raw = this.getArrayFromFile(name, path);

			return defineClass(name, raw, 0, raw.length);

		}

		private byte[] readStreamIntoArray(InputStream fin, long len)
				throws IOException {
			byte[] raw = new byte[(int) len];
			fin.read(raw);
			fin.close();
			return raw;
		}

		private byte[] getArrayFromFile(String name, String path)
				throws IOException {
			byte[] raw;

			// load
			if (path.contains(".jar!")) {
				// jar file

				JarFile jarfile = new JarFile(path.substring(0,
						path.indexOf(".jar!") + 4));
				String inJar = path.substring(path.indexOf(".jar!") + 5);
				if (inJar.startsWith("/"))
					inJar = inJar.substring(1);
				Enumeration<JarEntry> entries = jarfile.entries();
				InputStream fin = null;
				long length = 0;
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					if (entry.getName().equals(inJar)) {
						try {
							fin = jarfile.getInputStream(entry);
						} catch (IOException e) {
							jarfile.close();
							throw e;
						}
						length = entry.getSize();
						break;
					}
				}
				if (null == fin) {
					jarfile.close();
					throw new FileNotFoundException(name);
				}
				try {
					raw = this.readStreamIntoArray(fin, length);
				} catch (IOException e) {
					fin.close();
					jarfile.close();
					throw e;
				}
				jarfile.close();
			} else {
				// ��ͨ�ļ�
				File f = new File(path);
				InputStream fin = new FileInputStream(f);
				try {
					raw = this.readStreamIntoArray(fin, f.length());
				} catch (IOException e) {
					fin.close();
					throw e;
				}
				fin.close();
			}
			return raw;
		}
	}

}
