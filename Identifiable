import java.util.Objects;
import java.util.UUID;

public interface Identifiable {
    UUID getId();

    void setId(UUID id);

    default boolean equalsById(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identifiable)) return false;
        Identifiable that = (Identifiable) o;
        return Objects.equals(getId(), that.getId());
    }

    default int hashCodeById() {
        return Objects.hash(getId());
    }
}
