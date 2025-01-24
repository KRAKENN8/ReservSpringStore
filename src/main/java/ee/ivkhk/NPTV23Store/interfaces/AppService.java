package ee.ivkhk.NPTV23Store.interfaces;

public interface AppService<T> {
    void addEntity();

    void showEntities();

    void editEntity();

    default void showIncome() {
        throw new UnsupportedOperationException("showIncome() не поддержан для данного типа!");
    }
}
