package ee.ivkhk.NPTV23Store.interfaces;

import java.util.List;

public interface AppHelper<T> {
    T createEntity(Object... args);
    boolean isValid(T entity);
    List<String> formatEntities(List<T> entities);
    void addEntity();
    void showEntities();
    void editEntity();
    default void showIncome() {
        throw new UnsupportedOperationException("showIncome() не поддержан для данного типа!");
    }
}
