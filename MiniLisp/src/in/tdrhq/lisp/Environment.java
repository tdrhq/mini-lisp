package in.tdrhq.lisp;

public interface Environment {
	public Object getSymbolValue(Symbol s);
	public void setSymbolValue(Symbol s, Object value);
	public Environment getParent();
}
