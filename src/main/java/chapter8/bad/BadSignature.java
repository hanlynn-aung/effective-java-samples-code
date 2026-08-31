package chapter8.bad;

public final class BadSignature {

    public boolean qualify(String name, String region,
                           boolean requireActive, boolean requireVerified,
                           boolean requireRecent, boolean failClosed) {
        boolean active = requireActive || !failClosed;
        boolean verified = requireVerified || !failClosed;
        boolean recent = requireRecent || !failClosed;
        return active && verified && recent && region != null && name != null;
    }
}