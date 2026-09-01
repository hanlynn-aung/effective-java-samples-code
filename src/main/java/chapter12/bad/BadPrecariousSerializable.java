package chapter12.bad;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * (bad) Implements {@link Serializable} with little thought: it is a public,
 * extendable, mutable class whose representation is directly serialized, and it
 * has no {@code serialVersionUID}. This silently couples the serialized form to
 * the internal representation, costs correctness (equals/hashCode are then tied
 * to the representation) and makes the class's API effectively permanent.
 */
public class BadPrecariousSerializable implements Serializable {

    public String title;

    private ObjectInputStream readHandle;

    public BadPrecariousSerializable(String title) {
        this.title = title;
    }

    // No serialVersionUID declared => computed from this exact class; any change
    // to the class layout breaks the stream without warning.

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.readHandle = in;
    }

    @Override
    public String toString() {
        return "BadPrecariousSerializable{title='" + title + "'}";
    }
}