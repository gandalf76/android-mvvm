package alex.com.myplaces.data.network.volley;

public class ApiError {

    private int code;

    private String description;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiError apiError = (ApiError) o;

        if (code != apiError.code) return false;
        return description != null ? description.equals(apiError.description) : apiError.description == null;
    }

    @Override
    public int hashCode() {
        int result = code;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "code=" + code +
                ", description='" + description + '\'' +
                '}';
    }
}
