package example;

import kotlin.NotImplementedError;
import net.minecraft.tags.ITag;

public class Block {
    public static class NotImplementedException extends RuntimeException {
        public NotImplementedException(String message) {
            super(message);
        }
    }

    private Block delegate;

    protected void createState(ITag.Builder builder) {
        // todo
    }

    void a() {
        if (delegate != null) {
            delegate.a();
        }
    }

    boolean b() {
        if (delegate != null) {
            return delegate.b();
        } else {
            throw new NotImplementedError("no delegate found");
        }
    }
}
