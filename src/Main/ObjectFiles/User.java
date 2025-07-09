package Main.ObjectFiles;

public class User {

    private final String first_name;
    private final String birth_date;
    private final String phone_number;
    private final String email;
    private final String address;
    private final String state;
    private final String city;
    private final String password_hash;

    public User(String first_name, String email, String password_hash , String phone_number, String address, String state, String city, String birth_date) {
        this.first_name = first_name;
        this.birth_date = birth_date;
        this.phone_number = phone_number;
        this.email = email;
        this.address = address;
        this.state = state;
        this.city = city;
        this.password_hash = password_hash;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }
}
