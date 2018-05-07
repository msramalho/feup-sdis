package src.util;


import java.util.ArrayList;

public class ArrayListC<C> extends ArrayList {
    @Override
    public Object set(int i, Object o) {
        while (i >= size()) {add(null);}
        return super.set(i, o);
    }

    @Override
    public C get(int i) {
        return (C) super.get(i);
    }
}
