package me.clomclem.sculkcontagione.accessor;

public interface ILivingEntityAccessor {
    default boolean isSculk() {
        return false;
    }

    default void setSculk(boolean isSculk) {

    }
}
