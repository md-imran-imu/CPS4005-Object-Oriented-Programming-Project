package library;

public class Member {
    private int memberId;
    private String name;
    private String email;
    private String membershipType;

    public Member(int id, String name, String email, String type) {
        this.memberId = id;
        this.name = name;
        this.email = email;
        this.membershipType = type;
    }

    public int getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMembershipType() { return membershipType; }
}