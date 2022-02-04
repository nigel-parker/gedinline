package gedinline.lexical;

import gedinline.value.Pointer;

public interface Line {

    Level getLevel();

    Pointer getLabel();

    Tag getTag();

    Pointer getPointer();
}
