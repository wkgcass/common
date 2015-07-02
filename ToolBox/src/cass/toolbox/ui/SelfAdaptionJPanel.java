package cass.toolbox.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class SelfAdaptionJPanel extends JPanel {
	private static final long serialVersionUID = -7024463669873409505L;

	protected final int minWidth;
	protected final int minHeight;
	protected final int maxWidth;
	protected final int maxHeight;
	protected final int mode;

	private ComponentBoundsListenerHandler handler = new ComponentBoundsListenerHandler(
			this);

	public final static int MODE_NONE = -1;
	public final static int MODE_EXPAND_WIDTH = 0;
	public final static int MODE_EXPAND_HEIGHT = 1;
	public final static int MODE_EXPAND_BOTH = 2;

	public SelfAdaptionJPanel(int initWidth, int initHeight, int mode) {
		this(initWidth, initHeight, -1, -1, mode);
	}

	public SelfAdaptionJPanel(int minWidth, int minHeight, int maxWidth,
			int maxHeight, int mode) {
		super();
		super.setLayout(null);
		super.setSize(minWidth, minHeight);
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		this.mode = mode;
	}

	@Override
	public void setLayout(LayoutManager mgr) {
		// do nothing
	}

	@Override
	public Component add(Component comp) {
		comp = this.handler.addComponent(comp);
		super.add(comp);
		changeSize();
		return comp;
	}

	@Override
	public void remove(Component comp) {
		super.remove(comp);
		handler.removeComponent(comp);
		changeSize();
	}

	@Override
	public void remove(int index) {
		Component c = this.getComponent(index);
		this.remove(c);
	}

	@Override
	public void setSize(Dimension d) {
		// do nothing
	}

	@Override
	public void setSize(int widht, int height) {
		// do nothing
	}

	@Override
	public void setBounds(Rectangle r) {
		super.setLocation(r.x, r.y);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		Dimension d = getInferredSize();
		super.setBounds(x, y, d.width, d.height);
	}

	protected void changeSize() {
		super.setSize(getInferredSize());
	}

	protected Dimension getInferredSize() {
		Component[] components = super.getComponents();
		int w = 0;
		int h = 0;
		for (Component c : components) {
			int tmpW = c.getX() + c.getWidth();
			int tmpH = c.getY() + c.getHeight();
			if (tmpW > w)
				w = tmpW;
			if (tmpH > h)
				h = tmpH;
			System.out.println(c);
		}
		if (minWidth > 0 && w < minWidth)
			w = minWidth;
		if (minHeight > 0 && h < minHeight)
			h = minHeight;
		if (maxWidth > 0 && w > maxWidth)
			w = maxWidth;
		if (maxHeight > 0 && h > maxHeight)
			h = maxHeight;
		return new Dimension(w, h);
	}
}

class ComponentBoundsListenerHandler implements MethodInterceptor {

	private SelfAdaptionJPanel saj;

	private static Map<Component, Component> objToProxy = new HashMap<Component, Component>();

	public Component addComponent(Component c) {
		Enhancer e = new Enhancer();
		e.setSuperclass(c.getClass());
		e.setCallback(this);
		Component ret = (Component) e.create();
		objToProxy.put(c, ret);
		ret.setBounds(c.getBounds());
		return ret;
	}

	public void removeComponent(Component c) {
		objToProxy.remove(c);
	}

	public Component getNonProxy(Component proxy) {
		for (Component c : objToProxy.keySet()) {
			if (objToProxy.get(c).equals(proxy))
				return c;
		}
		return null;
	}

	public ComponentBoundsListenerHandler(SelfAdaptionJPanel saj) {
		this.saj = saj;
	}

	@Override
	public Object intercept(Object arg0, Method arg1, Object[] arg2,
			MethodProxy arg3) throws Throwable {
		Object ret = arg3.invokeSuper(arg0, arg2);
		String name = arg1.getName();
		if (name.equals("setSize") || name.equals("setLocation")
				|| name.equals("setBounds")) {
			saj.changeSize();
		}
		return ret;
	}

}
